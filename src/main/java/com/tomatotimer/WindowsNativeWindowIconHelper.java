package com.tomatotimer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.CRC32;

/**
 * Pushes a JavaFX {@link Image} directly to the Windows native window as {@code HICON},
 * fixing the "stuck taskbar icon" problem where {@link javafx.stage.Stage#getIcons()}
 * changes are not reliably picked up by the Windows taskbar button.
 *
 * <h3>How it works</h3>
 * <ol>
 *   <li>Locates the native {@code HWND} by enumerating all top-level windows via
 *       {@code User32.EnumWindows}, filtering by {@code IsWindowVisible} and matching
 *       the window's owning process against the current JVM process ID
 *       ({@code GetWindowThreadProcessId} vs {@code ProcessHandle.current().pid()}).
 *       The first visible top-level window belonging to this process is used.
 *       This approach is robust regardless of the stage title at the time of the call.</li>
 *   <li>Converts the JavaFX {@link Image} to BMP-in-ICO bytes (32-bit ARGB, single entry).
 *       A CRC32 of these bytes is computed and compared against the cached value; file I/O
 *       only occurs when the data has changed since the last call, significantly reducing
 *       SSD writes during repeated icon updates (e.g., taskbar timer display).</li>
 *   <li>Writes the ICO bytes to a single process-lifetime temporary file (created once,
 *       reused on every update, deleted on JVM shutdown via {@code deleteOnExit}).</li>
 *   <li>Loads the ICO file as an {@code HICON} via {@code LoadImageW(IMAGE_ICON, LR_LOADFROMFILE)}.</li>
 *   <li>Sends {@code WM_SETICON} for {@code ICON_SMALL} (0), {@code ICON_BIG} (1),
 *       and {@code ICON_SMALL2} (2) via {@code SendMessageW}.</li>
 *   <li>Destroys the previously loaded {@code HICON} to prevent GDI handle leaks.</li>
 * </ol>
 *
 * <p><strong>Key optimization:</strong> Icon byte data is hashed with CRC32; the cached
 * checksum prevents unnecessary file I/O. For a timer that updates every 1 second but
 * changes only once per minute (e.g., countdown display), this reduces SSD writes by ~98%.
 * The temp file is reused across calls (no repeated create/delete churn).</p>
 *
 * <p>All operations fail silently on non-Windows platforms or when JNA is unavailable.
 * Must be called on the JavaFX Application Thread (pixel extraction requires it).</p>
 *
 * <h3>Diagnostic logging</h3>
 * <p>A lightweight debug mode can be enabled at JVM startup to trace every lifecycle
 * step (HWND search, CRC cache, file I/O, {@code LoadImageW}, {@code WM_SETICON},
 * {@code DestroyIcon}) without affecting functional behaviour:</p>
 * <pre>{@code
 *   java -Dtomatotimer.icon.debug=true -jar tomato-timer.jar
 * }</pre>
 * <p>All debug output uses {@code java.util.logging} at {@code FINE} level via the
 * class-named logger {@code com.tomatotimer.WindowsNativeWindowIconHelper}.</p>
 */
public final class WindowsNativeWindowIconHelper {

    private static final Logger LOG =
            Logger.getLogger(WindowsNativeWindowIconHelper.class.getName());

    /**
     * When {@code true}, key lifecycle steps are emitted as {@code FINE} log records.
     * Enabled by passing {@code -Dtomatotimer.icon.debug=true} on the JVM command line.
     * Has no effect on functional behaviour.
     */
    private static final boolean DEBUG_MODE =
            Boolean.getBoolean("tomatotimer.icon.debug");

    /**
     * Guards the one-time debug-logging bootstrap so it executes at most once,
     * even if {@link #apply} is called concurrently from multiple threads.
     */
    private static final AtomicBoolean BOOTSTRAP_DONE = new AtomicBoolean(false);

    // ── Win32 constants ───────────────────────────────────────────────────────
    /** {@code WM_SETICON} – sets the icon associated with a window. */
    private static final int WM_SETICON      = 0x0080;
    /** {@code ICON_SMALL}  – 16-px taskbar / title-bar icon slot. */
    private static final int ICON_SMALL      = 0;
    /** {@code ICON_BIG}    – 32-px alt-tab / thumbnail icon slot. */
    private static final int ICON_BIG        = 1;
    /** {@code ICON_SMALL2} – secondary small icon maintained by the shell (Vista+). */
    private static final int ICON_SMALL2     = 2;
    /** {@code IMAGE_ICON}  – image type constant for {@code LoadImageW}: load as icon. */
    private static final int IMAGE_ICON      = 1;
    /** {@code LR_LOADFROMFILE} – {@code LoadImageW} flag: load the image from a file path. */
    private static final int LR_LOADFROMFILE = 0x0010;

    // ── Win32 extended-style constants ────────────────────────────────────────
    /**
     * {@code GWL_EXSTYLE} – index for {@code GetWindowLong}: retrieves the extended
     * window styles. 32-bit on both 32-bit and 64-bit Windows.
     */
    private static final int GWL_EXSTYLE      = -20;
    /**
     * {@code WS_EX_TOOLWINDOW} – extended style: the window is a floating toolbar.
     * Tool windows do not appear in the taskbar or in the dialog box that appears
     * when the user presses {@code Alt+Tab}.
     */
    private static final int WS_EX_TOOLWINDOW = 0x00000080;
    /**
     * {@code WS_EX_APPWINDOW} – extended style: forces a top-level window onto
     * the taskbar when the window is minimised or visible.  When present, this
     * overrides {@code WS_EX_TOOLWINDOW} for taskbar-participation purposes.
     */
    private static final int WS_EX_APPWINDOW  = 0x00040000;

    // ── State ─────────────────────────────────────────────────────────────────
    /**
     * The {@code HICON} installed during the most recent successful call.
     * Retained so that the <em>next</em> call can destroy it via
     * {@code DestroyIcon} before installing the new handle, preventing GDI leaks.
     * <p>Access is confined to the JavaFX Application Thread, so no explicit
     * synchronisation is needed beyond {@code volatile} for visibility.</p>
     */
    private static volatile WinDef.HICON previousHIcon = null;

    /**
     * Guards against a false cache-hit on the very first call when the CRC
     * of the first icon payload happens to equal the zero-initialised
     * {@link #lastIcoCrc}.  Once the first icon has been applied successfully
     * this is set to {@code true} and never flipped back.
     */
    private static volatile boolean cacheInitialized = false;

    /**
     * CRC32 checksum of the last ICO byte payload written to disk.
     * Compared on every call; file I/O is skipped when the checksum is unchanged.
     * Only meaningful when {@link #cacheInitialized} is {@code true}.
     */
    private static volatile long lastIcoCrc = 0L;

    /**
     * Single process-lifetime temporary {@code .ico} file.  Created once on first
     * class load, reused on every subsequent update (bytes are overwritten with
     * {@link StandardOpenOption#TRUNCATE_EXISTING}), and deleted on JVM shutdown
     * via {@link java.io.File#deleteOnExit()}.  May be {@code null} if the temp file could
     * not be created (e.g., no write permission in the system temp directory).
     */
    private static final Path TEMP_ICO_PATH = createTempIcoPath();

    private static Path createTempIcoPath() {
        try {
            final Path p = Files.createTempFile("tomato-icon-", ".ico");
            p.toFile().deleteOnExit();
            return p;
        } catch (IOException e) {
            Logger.getLogger(WindowsNativeWindowIconHelper.class.getName())
                    .log(Level.WARNING, "Failed to allocate process-lifetime temp ICO file – "
                            + "native icon updates will be unavailable: {0}", e.getMessage());
            return null;
        }
    }

    // ── Minimal JNA interface ─────────────────────────────────────────────────

    /**
     * The subset of {@code User32.dll} required for native icon injection.
     *
     * <p>Declared as a private inner interface so the JNA library load
     * ({@link Native#load}) is deferred until the interface is first accessed,
     * which only happens on Windows after the {@link #isWindows()} guard passes.
     * On non-Windows platforms this inner class is never loaded.</p>
     */
    private interface User32Icon extends StdCallLibrary {

        User32Icon INSTANCE = Native.load(
                "user32", User32Icon.class, W32APIOptions.DEFAULT_OPTIONS);

        /**
         * Callback interface for {@link #EnumWindows}.
         * Return {@code true} to continue enumeration, {@code false} to stop.
         */
        interface WndEnumProc extends StdCallCallback {
            boolean callback(WinDef.HWND hWnd, Pointer data);
        }

        /**
         * Enumerates all top-level windows on the screen by passing each one in
         * turn to the supplied {@code lpEnumFunc} callback.
         *
         * @return {@code false} if the callback returned {@code false};
         *         {@code true} if all windows were enumerated
         */
        boolean EnumWindows(WndEnumProc lpEnumFunc, Pointer lParam);

        /**
         * Retrieves the identifier of the thread that created the specified window
         * and, optionally, the identifier of the process that created the window.
         * The process ID is written to {@code lpdwProcessId}.
         *
         * @return the thread ID that created the window
         */
        int GetWindowThreadProcessId(WinDef.HWND hWnd, IntByReference lpdwProcessId);

        /**
         * Determines whether the specified window is visible (i.e., has the
         * {@code WS_VISIBLE} style set and is not fully clipped).
         */
        boolean IsWindowVisible(WinDef.HWND hWnd);

        /**
         * Loads an icon from a file when {@code fuLoad} contains
         * {@code LR_LOADFROMFILE}.  Pass {@code null} for {@code hinst}.
         *
         * @return a new {@code HICON} handle on success;
         *         {@code null} or a zero-pointer handle on failure
         */
        WinDef.HICON LoadImageW(WinDef.HWND hinst, String name, int type,
                                int cxDesired, int cyDesired, int fuLoad);

        /**
         * Sends {@code msg} to {@code hWnd} and blocks until the window
         * procedure has processed it.
         */
        WinDef.LRESULT SendMessageW(WinDef.HWND hWnd, int msg,
                                    WinDef.WPARAM wParam, WinDef.LPARAM lParam);

        /**
         * Destroys an {@code HICON} that was <em>not</em> loaded with
         * {@code LR_SHARED}.  Must be called to release the GDI object.
         */
        boolean DestroyIcon(WinDef.HICON hIcon);

        /**
         * Retrieves information about the specified window.  {@code nIndex} is one
         * of the {@code GWL_*} / {@code GWL_EX*} constants.  Returns the value as a
         * signed 32-bit integer.  Use to read {@code GWL_EXSTYLE} (extended styles).
         *
         * <p>On 64-bit Windows, pointer-sized values require {@code GetWindowLongPtr};
         * for {@code GWL_EXSTYLE} the value is always 32-bit so this overload suffices.</p>
         */
        int GetWindowLong(WinDef.HWND hWnd, int nIndex);
    }

    private WindowsNativeWindowIconHelper() { /* utility class – no instances */ }

    /** Emits a {@code FINE} log record only when {@link #DEBUG_MODE} is active. */
    private static void debug(final String fmt, final Object... args) {
        if (DEBUG_MODE) {
            LOG.fine(String.format(fmt, args));
        }
    }

    /**
     * One-time bootstrap that guarantees {@link #LOG} can emit {@code FINE} records to
     * the console when {@link #DEBUG_MODE} is active, regardless of the JUL configuration
     * present in the environment.
     *
     * <p>JUL's default root handler only surfaces {@code INFO} and above.  If no
     * {@code logging.properties} is supplied (e.g., when launched via Maven without
     * {@code -Djava.util.logging.config.file}), {@code FINE} messages are silently dropped.
     * This method prevents that by installing a dedicated {@link ConsoleHandler} at
     * {@code FINE} level on the class logger if none already exists in the logger hierarchy,
     * so debug output reliably reaches the console without any external configuration.</p>
     *
     * <p>Thread-safe: the {@link AtomicBoolean} guard ensures the setup runs at most once.</p>
     */
    private static void bootstrapDebugLogging() {
        if (!DEBUG_MODE || !BOOTSTRAP_DONE.compareAndSet(false, true)) {
            return;
        }
        // Ensure this logger passes FINE records down to its own handlers.
        LOG.setLevel(Level.FINE);

        // Walk the full logger hierarchy; if any ancestor already has a ConsoleHandler
        // capable of handling FINE, skip adding a duplicate.
        boolean found = false;
        for (Logger cursor = LOG; cursor != null; cursor = cursor.getParent()) {
            for (final var h : cursor.getHandlers()) {
                if (h instanceof ConsoleHandler
                        && h.getLevel().intValue() <= Level.FINE.intValue()) {
                    found = true;
                    break;
                }
            }
            if (found) break;
        }

        if (!found) {
            final var console = new ConsoleHandler();
            console.setLevel(Level.FINE);
            console.setFormatter(new SimpleFormatter());
            LOG.addHandler(console);
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Applies {@code icon} as the native {@code HICON} on all eligible top-level
     * windows that belong to the current JVM process, bypassing the JavaFX icon
     * layer and talking directly to Win32.
     *
     * <p>Unlike the previous single-window approach, this method broadcasts
     * {@code WM_SETICON} to <em>every</em> visible, non-tool-window top-level window
     * owned by the process.  JavaFX may create several windows (glass panel,
     * accessibility, focus-trap helper) and on a transparent stage the "first"
     * window returned by {@code EnumWindows} is not always the window that owns
     * the taskbar button.  Broadcasting ensures the correct target is reached
     * regardless of enumeration order.</p>
     *
     * <p>Tool windows ({@code WS_EX_TOOLWINDOW} set and {@code WS_EX_APPWINDOW}
     * absent) are explicitly skipped because they never receive a taskbar button.</p>
     *
     * <p>The HWND set is resolved at call-time via {@code EnumWindows} so this
     * method is robust against title changes and races during stage initialisation.</p>
     *
     * <p>This is a no-op on non-Windows platforms, when JNA is unavailable, or
     * when any native call fails.  All failures are logged at {@code WARNING} level
     * and never propagate as exceptions.</p>
     *
     * <p><strong>Must be called on the JavaFX Application Thread.</strong></p>
     *
     * @param icon the already-rendered taskbar icon; should be ≥ 32 × 32 px
     */
    public static void apply(final Image icon) {
        bootstrapDebugLogging();
        if (!isWindows()) {
            debug("apply: skipped – not a Windows platform (os.name=%s)",
                    System.getProperty("os.name", "<unknown>"));
            return;
        }
        if (icon == null) {
            debug("apply: skipped – icon image is null");
            return;
        }
        try {
            applyUnsafe(icon);
        } catch (Throwable t) {
            LOG.log(Level.WARNING,
                    "WindowsNativeWindowIconHelper: native HICON update failed – {0}",
                    t.getMessage());
        }
    }

    /**
     * Invalidates the internal CRC cache so that the next call to {@link #apply}
     * unconditionally writes the ICO file and sends {@code WM_SETICON}, even if
     * the rendered icon bytes happen to be identical to the last applied icon.
     *
     * <p>Call this whenever the taskbar-icon feature is toggled off and then back on,
     * or after the native window has been recreated (e.g. stage re-shown), to
     * guarantee that the live countdown icon is immediately reapplied.</p>
     *
     * <p>Thread-safe: safe to call from any thread.  The flag is {@code volatile}
     * so the next {@link #apply} call on the FX thread will observe the reset.</p>
     */
    public static void resetCache() {
        cacheInitialized = false;
        debug("resetCache: cacheInitialized cleared – next apply() will force WM_SETICON");
    }

    /**
     * Removes the custom countdown icon from all eligible top-level windows of
     * this process by sending {@code WM_SETICON} with an {@code HICON} value of
     * {@code NULL} (zero) for all three icon slots.
     *
     * <p>This restores the default window icon (typically the Java logo from
     * {@code javaw.exe}) on the native level, complementing the JavaFX-level
     * {@code stage.getIcons().clear()} call.  Should be called whenever the
     * taskbar-icon feature is disabled so that the Windows taskbar reverts to
     * showing the standard icon immediately.</p>
     *
     * <p>Also destroys the previously loaded {@code HICON} handle (if any) to
     * prevent GDI handle leaks.</p>
     *
     * <p>No-op on non-Windows or when JNA is unavailable.  Failures are logged at
     * {@code WARNING} level and never propagate as exceptions.</p>
     *
     * <p><strong>Must be called on the JavaFX Application Thread.</strong></p>
     */
    public static void clearNativeIcon() {
        if (!isWindows()) {
            debug("clearNativeIcon: skipped – not a Windows platform");
            return;
        }
        try {
            clearNativeIconUnsafe();
        } catch (Throwable t) {
            LOG.log(Level.WARNING,
                    "WindowsNativeWindowIconHelper: clearNativeIcon failed – {0}",
                    t.getMessage());
        }
    }

    // ── Private implementation ────────────────────────────────────────────────

    private static void applyUnsafe(final Image icon) throws IOException {

        // ── Step 1: locate all eligible native HWNDs for this process ─────────
        // Broadcast to EVERY non-tool visible window rather than stopping at the
        // first match.  On a transparent FXML stage JavaFX may create auxiliary
        // windows (accessibility, glass panel); the "first" one found by
        // EnumWindows is not guaranteed to be the taskbar-button owner.
        final int currentPid = (int) ProcessHandle.current().pid();
        final List<WinDef.HWND> windows = resolveProcessWindows(currentPid);

        if (windows.isEmpty()) {
            // Stage not yet shown (init() called before show()) – will retry on next tick.
            LOG.fine("resolveProcessWindows: no eligible visible windows found for current process");
            debug("applyUnsafe: no eligible HWNDs found for pid=%d – aborting (stage not shown yet?)",
                    currentPid);
            return;
        }
        debug("applyUnsafe: resolved %d eligible window(s) for pid=%d", windows.size(), currentPid);

        // ── Step 2: extract ARGB pixels from the JavaFX Image ─────────────────
        final int w = (int) icon.getWidth();
        final int h = (int) icon.getHeight();
        final int[] argb = new int[w * h];
        icon.getPixelReader().getPixels(
                0, 0, w, h, PixelFormat.getIntArgbInstance(), argb, 0, w);

        // ── Step 3: build ICO bytes (BMP-in-ICO, 32-bit ARGB, single entry) ───
        final byte[] icoBytes = buildBmpIco(argb, w, h);

        // ── Step 4: compute CRC32 and check cache ────────────────────────────
        final long currentCrc = computeCrc32(icoBytes);
        if (cacheInitialized && currentCrc == lastIcoCrc) {
            // Icon data unchanged – skip file I/O and re-registration
            LOG.finest("Icon data unchanged – skipping SSD write and WM_SETICON");
            debug("cache HIT  crc=0x%08X – skipping file I/O and WM_SETICON", currentCrc);
            return;
        }
        debug("cache MISS crc 0x%08X → 0x%08X – proceeding with update", lastIcoCrc, currentCrc);
        lastIcoCrc = currentCrc;
        cacheInitialized = true;

        // ── Step 5: write bytes to the reusable process-lifetime temp file ────
        if (TEMP_ICO_PATH == null) {
            LOG.warning("Temp ICO path unavailable – WM_SETICON skipped");
            debug("applyUnsafe: TEMP_ICO_PATH is null – WM_SETICON skipped");
            return;
        }
        Files.write(TEMP_ICO_PATH, icoBytes,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        debug("wrote %d bytes to temp ICO → %s", icoBytes.length, TEMP_ICO_PATH);

        // ── Step 6: load HICON from the temp ICO file ────────────────────────
        final WinDef.HICON hIcon = User32Icon.INSTANCE.LoadImageW(
                null, TEMP_ICO_PATH.toString(), IMAGE_ICON, 0, 0, LR_LOADFROMFILE);

        if (hIcon == null || isNullHandle(hIcon)) {
            LOG.warning("LoadImageW returned a null/invalid HICON – WM_SETICON skipped");
            debug("LoadImageW FAILED (null/zero HICON) for path=%s", TEMP_ICO_PATH);
            return;
        }
        debug("LoadImageW OK → HICON ptr=0x%X", Pointer.nativeValue(hIcon.getPointer()));

        // ── Step 7: broadcast WM_SETICON to ALL eligible windows ─────────────
        // Sending to every candidate ensures the taskbar-button owner is reached
        // regardless of which window EnumWindows happens to return first.
        final WinDef.LPARAM hIconLParam =
                new WinDef.LPARAM(Pointer.nativeValue(hIcon.getPointer()));
        for (final WinDef.HWND hwnd : windows) {
            for (final int slot : new int[]{ ICON_SMALL, ICON_BIG, ICON_SMALL2 }) {
                User32Icon.INSTANCE.SendMessageW(
                        hwnd, WM_SETICON, new WinDef.WPARAM(slot), hIconLParam);
            }
        }
        debug("WM_SETICON broadcast complete (%d window(s), slots SMALL/BIG/SMALL2)", windows.size());

        // ── Step 8: rotate out stale handle to release the GDI object ─────────
        final WinDef.HICON stale = previousHIcon;
        previousHIcon = hIcon;
        if (stale != null && !isNullHandle(stale)) {
            final boolean destroyed = User32Icon.INSTANCE.DestroyIcon(stale);
            debug("DestroyIcon stale HICON ptr=0x%X → %s",
                    Pointer.nativeValue(stale.getPointer()), destroyed ? "OK" : "FAILED");
        }
    }

    /**
     * Removes the custom countdown icon from all eligible process windows by
     * sending {@code WM_SETICON(NULL)} for each icon slot, then destroys the
     * previously loaded {@code HICON} to prevent GDI handle leaks.
     */
    private static void clearNativeIconUnsafe() {
        final int currentPid = (int) ProcessHandle.current().pid();
        final List<WinDef.HWND> windows = resolveProcessWindows(currentPid);
        if (windows.isEmpty()) {
            debug("clearNativeIconUnsafe: no eligible windows found for pid=%d", currentPid);
            return;
        }

        final WinDef.LPARAM zero = new WinDef.LPARAM(0L);
        for (final WinDef.HWND hwnd : windows) {
            for (final int slot : new int[]{ ICON_SMALL, ICON_BIG, ICON_SMALL2 }) {
                User32Icon.INSTANCE.SendMessageW(
                        hwnd, WM_SETICON, new WinDef.WPARAM(slot), zero);
            }
        }
        debug("clearNativeIconUnsafe: WM_SETICON(NULL) sent to %d window(s)", windows.size());

        final WinDef.HICON stale = previousHIcon;
        previousHIcon = null;
        if (stale != null && !isNullHandle(stale)) {
            final boolean destroyed = User32Icon.INSTANCE.DestroyIcon(stale);
            debug("DestroyIcon stale HICON ptr=0x%X → %s",
                    Pointer.nativeValue(stale.getPointer()), destroyed ? "OK" : "FAILED");
        }
    }

    /**
     * Collects all eligible top-level visible windows that belong to the current
     * JVM process and should receive a taskbar button.
     *
     * <p>A window is <em>eligible</em> if it satisfies all of the following:</p>
     * <ol>
     *   <li>{@code IsWindowVisible} returns {@code true}.</li>
     *   <li>Its owning process ID matches {@code pid}.</li>
     *   <li>It is <em>not</em> a pure tool window – i.e., it does not have
     *       {@code WS_EX_TOOLWINDOW} set without also having {@code WS_EX_APPWINDOW}.
     *       Tool windows are floating toolbars that never appear in the taskbar.</li>
     * </ol>
     *
     * <p>This algorithm mirrors {@code WindowsTaskbarPreviewButtonsHelper.resolveCurrentProcessWindow()}'s
     * candidate-collection phase and avoids the "wrong HWND" bug present in the
     * original single-first-match approach.</p>
     *
     * @param pid the current JVM process ID
     * @return mutable list of eligible HWNDs (may be empty if the stage is not yet shown)
     */
    private static List<WinDef.HWND> resolveProcessWindows(final int pid) {
        final List<WinDef.HWND> candidates = new ArrayList<>();
        final IntByReference pidRef = new IntByReference();

        User32Icon.INSTANCE.EnumWindows((hwnd, data) -> {
            if (!User32Icon.INSTANCE.IsWindowVisible(hwnd)) return true; // skip invisible

            pidRef.setValue(0);
            User32Icon.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef);
            if (pidRef.getValue() != pid) return true; // skip other processes

            // Skip pure tool windows – they do not get a Windows taskbar button.
            // A window with BOTH WS_EX_TOOLWINDOW and WS_EX_APPWINDOW is kept because
            // WS_EX_APPWINDOW forces taskbar participation regardless of the tool-window flag.
            final int exStyle = User32Icon.INSTANCE.GetWindowLong(hwnd, GWL_EXSTYLE);
            if ((exStyle & WS_EX_TOOLWINDOW) != 0 && (exStyle & WS_EX_APPWINDOW) == 0) {
                debug("resolveProcessWindows: HWND 0x%X skipped – WS_EX_TOOLWINDOW (exStyle=0x%X)",
                        Pointer.nativeValue(hwnd.getPointer()), exStyle);
                return true; // skip
            }

            candidates.add(hwnd);
            debug("resolveProcessWindows: HWND 0x%X added (exStyle=0x%X)",
                    Pointer.nativeValue(hwnd.getPointer()), exStyle);
            return true; // continue enumeration – collect ALL eligible windows
        }, null);

        return candidates;
    }

    // ── ICO / BMP format builder ──────────────────────────────────────────────

    /**
     * Builds a minimal single-entry ICO file whose image data is a 32-bit
     * ARGB device-independent bitmap (BITMAPINFOHEADER + XOR mask + AND mask).
     *
     * <h4>ICO layout</h4>
     * <pre>
     *   Offset   Size   Field
     *   ------   ----   -----
     *        0      6   ICONDIR  (reserved=0, type=1, count=1)
     *        6     16   ICONDIRENTRY  (width, height, colorCount, planes, bitCount, …)
     *       22     40   BITMAPINFOHEADER
     *       62   w*h*4  XOR mask (bottom-up, BGRA)
     *        …   align  AND mask (1-bit/px, row-padded to 4 bytes, all zeros)
     * </pre>
     *
     * @param argb   row-major pixels in {@code 0xAARRGGBB} format (top-to-bottom)
     * @param width  image width in pixels
     * @param height image height in pixels
     * @return raw ICO file bytes suitable for writing to disk and loading via
     *         {@code LoadImageW(IMAGE_ICON, LR_LOADFROMFILE)}
     */
    private static byte[] buildBmpIco(final int[] argb, final int width, final int height) {
        // AND mask: 1-bit per pixel, each row padded up to a 4-byte boundary
        final int andMaskRowBytes = ((width + 31) / 32) * 4;
        final int bmpDataSize = 40                   // BITMAPINFOHEADER
                + width * height * 4                 // XOR mask (32 bpp BGRA pixels)
                + andMaskRowBytes * height;          // AND mask (all-zero → alpha-only)
        final int icoOffset = 6 + 16;               // ICONDIR + one ICONDIRENTRY
        final byte sz = (byte) (width >= 256 ? 0 : width); // 0 encodes 256 in ICO spec

        final ByteBuffer buf = ByteBuffer
                .allocate(icoOffset + bmpDataSize)
                .order(ByteOrder.LITTLE_ENDIAN);

        // ── ICONDIR (6 bytes) ─────────────────────────────────────────────────
        buf.putShort((short) 0);     // idReserved – must be 0
        buf.putShort((short) 1);     // idType     – 1 = icon, 2 = cursor
        buf.putShort((short) 1);     // idCount    – number of images

        // ── ICONDIRENTRY (16 bytes) ───────────────────────────────────────────
        buf.put(sz);                 // bWidth
        buf.put(sz);                 // bHeight
        buf.put((byte) 0);          // bColorCount – 0 for true-colour
        buf.put((byte) 0);          // bReserved
        buf.putShort((short) 1);    // wPlanes
        buf.putShort((short) 32);   // wBitCount
        buf.putInt(bmpDataSize);    // dwBytesInRes
        buf.putInt(icoOffset);      // dwImageOffset

        // ── BITMAPINFOHEADER (40 bytes) ───────────────────────────────────────
        buf.putInt(40);             // biSize
        buf.putInt(width);          // biWidth
        buf.putInt(height * 2);     // biHeight – doubled because XOR + AND are stacked
        buf.putShort((short) 1);    // biPlanes
        buf.putShort((short) 32);   // biBitCount
        buf.putInt(0);              // biCompression = BI_RGB
        buf.putInt(0);              // biSizeImage   = 0 (allowed for BI_RGB)
        buf.putInt(0);              // biXPelsPerMeter
        buf.putInt(0);              // biYPelsPerMeter
        buf.putInt(0);              // biClrUsed
        buf.putInt(0);              // biClrImportant

        // ── XOR mask: bottom-up rows, BGRA byte order ─────────────────────────
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                final int px = argb[y * width + x];   // 0xAARRGGBB
                buf.put((byte)  (px        & 0xFF));  // B
                buf.put((byte) ((px >>  8) & 0xFF));  // G
                buf.put((byte) ((px >> 16) & 0xFF));  // R
                buf.put((byte) ((px >> 24) & 0xFF));  // A
            }
        }

        // ── AND mask: all zeros – transparency is carried by the 32-bit alpha ─
        for (int i = 0; i < andMaskRowBytes * height; i++) {
            buf.put((byte) 0);
        }

        return buf.array();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} when {@code handle} is a zero-value (null) Win32 handle.
     * A null {@code HICON} is represented in JNA as a {@link WinDef.HICON} whose
     * backing {@link Pointer} has a native address of {@code 0}.
     */
    private static boolean isNullHandle(final WinDef.HICON handle) {
        return Pointer.nativeValue(handle.getPointer()) == 0L;
    }

    /**
     * Computes a CRC32 checksum of the entire icon byte payload for cache validation.
     * CRC32 is lightweight (hardware-accelerated on x86) and has sufficient collision
     * resistance for this use-case (small, structurally similar payloads with single-pixel
     * differences, e.g., one digit changing in a countdown timer).
     *
     * @return unsigned 32-bit CRC value in the range [0, 2³²−1], returned as {@code long}
     *         to avoid sign-extension ambiguity
     */
    private static long computeCrc32(final byte[] data) {
        final CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}


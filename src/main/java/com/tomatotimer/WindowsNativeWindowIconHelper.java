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
import java.util.logging.Level;
import java.util.logging.Logger;

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
 *   <li>Converts the JavaFX {@link Image} to a BMP-in-ICO file (32-bit ARGB, single entry).
 *       The classic BMP variant is used because {@code LoadImageW} handles it reliably on
 *       all Windows XP+ systems, unlike PNG-in-ICO which requires Vista+ ICO codec.</li>
 *   <li>Loads the ICO file as an {@code HICON} via {@code LoadImageW(LR_LOADFROMFILE)}.</li>
 *   <li>Sends {@code WM_SETICON} for {@code ICON_SMALL} (0), {@code ICON_BIG} (1),
 *       and {@code ICON_SMALL2} (2) via {@code SendMessageW}.</li>
 *   <li>Destroys the previously loaded {@code HICON} to prevent GDI handle leaks.</li>
 *   <li>Deletes the temporary ICO file immediately after {@code LoadImageW} returns
 *       (the call is synchronous).</li>
 * </ol>
 *
 * <p>All operations fail silently on non-Windows platforms or when JNA is unavailable.
 * Must be called on the JavaFX Application Thread (pixel extraction requires it).</p>
 */
public final class WindowsNativeWindowIconHelper {

    private static final Logger LOG =
            Logger.getLogger(WindowsNativeWindowIconHelper.class.getName());

    // ── Win32 constants ───────────────────────────────────────────────────────
    /** {@code WM_SETICON} – sets the icon associated with a window. */
    private static final int WM_SETICON      = 0x0080;
    /** {@code ICON_SMALL}  – 16-px taskbar / title-bar icon slot. */
    private static final int ICON_SMALL      = 0;
    /** {@code ICON_BIG}    – 32-px alt-tab / thumbnail icon slot. */
    private static final int ICON_BIG        = 1;
    /** {@code ICON_SMALL2} – secondary small icon maintained by the shell (Vista+). */
    private static final int ICON_SMALL2     = 2;
    /** {@code IMAGE_ICON}  – {@code LoadImage} type for {@code HICON}. */
    private static final int IMAGE_ICON      = 1;
    /** {@code LR_LOADFROMFILE} – instructs {@code LoadImage} to load from a file path. */
    private static final int LR_LOADFROMFILE = 0x0010;

    // ── State ─────────────────────────────────────────────────────────────────
    /**
     * The {@code HICON} installed during the most recent successful call.
     * Retained so that the <em>next</em> call can destroy it via
     * {@code DestroyIcon} before installing the new handle, preventing GDI leaks.
     * <p>Access is confined to the JavaFX Application Thread, so no explicit
     * synchronisation is needed beyond {@code volatile} for visibility.</p>
     */
    private static volatile WinDef.HICON previousHIcon = null;

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
    }

    private WindowsNativeWindowIconHelper() { /* utility class – no instances */ }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Applies {@code icon} as the native {@code HICON} on the top-level visible window
     * that belongs to the current JVM process, bypassing the JavaFX icon layer and
     * talking directly to Win32.
     *
     * <p>The HWND is resolved at call-time via {@code EnumWindows} +
     * {@code GetWindowThreadProcessId} so this method is robust against title changes,
     * title localisation, and races during stage initialisation.</p>
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
        if (!isWindows()) return;
        if (icon == null) return;
        try {
            applyUnsafe(icon);
        } catch (Throwable t) {
            LOG.log(Level.WARNING,
                    "WindowsNativeWindowIconHelper: native HICON update failed – {0}",
                    t.getMessage());
        }
    }

    // ── Private implementation ────────────────────────────────────────────────

    private static void applyUnsafe(final Image icon) throws IOException {

        // ── Step 1: locate native HWND for this process ───────────────────────
        final int currentPid = (int) ProcessHandle.current().pid();
        final WinDef.HWND[] found = { null };
        final IntByReference pidRef = new IntByReference();

        User32Icon.INSTANCE.EnumWindows((hwnd, data) -> {
            if (!User32Icon.INSTANCE.IsWindowVisible(hwnd)) return true; // skip invisible
            pidRef.setValue(0);
            User32Icon.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef);
            if (pidRef.getValue() == currentPid) {
                found[0] = hwnd;
                return false; // stop – first visible top-level window of this process found
            }
            return true; // continue enumeration
        }, null);

        final WinDef.HWND hwnd = found[0];
        if (hwnd == null) {
            LOG.fine("EnumWindows: no visible top-level window found for current process");
            return;
        }

        // ── Step 2: extract ARGB pixels from the JavaFX Image ─────────────────
        final int w = (int) icon.getWidth();
        final int h = (int) icon.getHeight();
        final int[] argb = new int[w * h];
        icon.getPixelReader().getPixels(
                0, 0, w, h, PixelFormat.getIntArgbInstance(), argb, 0, w);

        // ── Step 3: build ICO bytes (BMP-in-ICO, 32-bit ARGB, single entry) ───
        final byte[] icoBytes = buildBmpIco(argb, w, h);

        // ── Steps 4-7: write temp file → LoadImage → SendMessage → cleanup ────
        final Path tmp = Files.createTempFile("tomato-icon-", ".ico");
        try {
            Files.write(tmp, icoBytes);

            // Step 4: load HICON from the temp ICO file
            final WinDef.HICON hIcon = User32Icon.INSTANCE.LoadImageW(
                    null, tmp.toString(), IMAGE_ICON, 0, 0, LR_LOADFROMFILE);

            if (hIcon == null || isNullHandle(hIcon)) {
                LOG.warning("LoadImageW returned a null/invalid HICON – WM_SETICON skipped");
                return;
            }

            // Step 5: broadcast WM_SETICON for all three icon slots
            final WinDef.LPARAM hIconLParam =
                    new WinDef.LPARAM(Pointer.nativeValue(hIcon.getPointer()));
            for (final int slot : new int[]{ ICON_SMALL, ICON_BIG, ICON_SMALL2 }) {
                User32Icon.INSTANCE.SendMessageW(
                        hwnd, WM_SETICON, new WinDef.WPARAM(slot), hIconLParam);
            }

            // Step 6: rotate out stale handle to release the GDI object
            final WinDef.HICON stale = previousHIcon;
            previousHIcon = hIcon;
            if (stale != null && !isNullHandle(stale)) {
                User32Icon.INSTANCE.DestroyIcon(stale);
            }

        } finally {
            // Step 7: LoadImageW reads synchronously – temp file can be deleted now
            try { Files.deleteIfExists(tmp); }
            catch (IOException ignored) { /* cleanup is best-effort */ }
        }
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
     * @return raw ICO file bytes ready to be written to disk
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

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}


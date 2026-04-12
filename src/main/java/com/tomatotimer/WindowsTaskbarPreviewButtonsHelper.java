package com.tomatotimer;

import com.sun.jna.CallbackReference;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import javafx.application.Platform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adds clickable buttons to the Windows taskbar thumbnail preview for quick timer control.
 *
 * <p>No-op on non-Windows platforms. Fails safely when COM/JNA calls are unavailable.</p>
 */
public final class WindowsTaskbarPreviewButtonsHelper {

    private static final Logger LOG = Logger.getLogger(WindowsTaskbarPreviewButtonsHelper.class.getName());

    private static final Guid.CLSID CLSID_TASKBAR_LIST =
            new Guid.CLSID("{56FDF344-FD6D-11D0-958A-006097C9A090}");
    /** Preferred interface – superset of ITaskbarList3, available on Windows 7+. */
    private static final Guid.IID IID_ITASKBAR_LIST4 =
            new Guid.IID("{C43DC798-95D1-4BEA-9030-BB99E2983A1A}");
    /** Fallback interface when ITaskbarList4 is not available. */
    private static final Guid.IID IID_ITASKBAR_LIST3 =
            new Guid.IID("{EA1AFB91-9E28-4B86-90E9-9E9F8A5EEA1E}");
    /** Base IUnknown interface – used as the creation interface to avoid E_NOINTERFACE. */
    private static final Guid.IID IID_IUNKNOWN =
            new Guid.IID("{00000000-0000-0000-C000-000000000046}");

    private static final int CLSCTX_INPROC_SERVER = 0x1;

    private static final int WM_COMMAND = 0x0111;
    private static final int THBN_CLICKED = 0x1800;
    private static final int GWLP_WNDPROC = -4;

    // Extended-style / owner-query constants used by robust HWND resolution
    private static final int GWL_EXSTYLE      = -20;
    private static final int GW_OWNER         = 4;
    private static final int WS_EX_TOOLWINDOW = 0x00000080;
    private static final int WS_EX_APPWINDOW  = 0x00040000;

    private static final int THB_ICON = 0x00000002;
    private static final int THB_TOOLTIP = 0x00000004;
    private static final int THB_FLAGS = 0x00000008;

    private static final int THBF_ENABLED        = 0x0000;
    private static final int THBF_DISABLED       = 0x0001;
    private static final int THBF_DISMISSONCLICK = 0x0002;
    /** THUMBBUTTONFLAGS: button is not rendered and cannot receive input. */
    private static final int THBF_HIDDEN         = 0x0008;

    private static final int IMAGE_ICON = 1;
    private static final int LR_LOADFROMFILE = 0x0010;

    private static final int BUTTON_RESET       = 2001;
    private static final int BUTTON_PAUSE       = 2002;
    private static final int BUTTON_FINISH_WORK = 2003;
    private static final int BUTTON_TAKE_BREAK  = 2004;
    private static final int BUTTON_GO_TO_WORK  = 2005;

    private static final int BUTTON_COUNT = 5;
    /**
     * Total icon slots: 5 for the regular buttons + 1 extra for the Resume alternate
     * icon that occupies the BUTTON_PAUSE slot when the WORK timer is paused.
     */
    private static final int ICON_COUNT        = 6;
    /** Index inside {@link #buttonIcons} / {@link #buttonIconFiles} for the Resume icon. */
    private static final int ICON_SLOT_RESUME  = 5;

    // ── SVG path data (Material Design 24 × 24 viewbox, mirrors IconFactory) ────
    // These paths are duplicated here to avoid a JavaFX dependency from the
    // pure-AWT rendering pipeline used for thumbnail-toolbar icons.
    private static final String SVG_RESET =
        "M12,5V1L7,6L12,11V7A6,6 0 0,1 18,13A6,6 0 0,1 12,19" +
        "A6,6 0 0,1 6,13H4A8,8 0 0,0 12,21A8,8 0 0,0 20,13A8,8 0 0,0 12,5Z";
    private static final String SVG_PAUSE =
        "M14,19H18V5H14M6,19H10V5H6Z";
    private static final String SVG_CLOCK =
        "M12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20" +
        "M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22C6.47,22 2,17.5 2,12" +
        "A10,10 0 0,1 12,2M12.5,7V12.25L17,14.92L16.25,16.15L11,13V7H12.5Z";
    private static final String SVG_RELAX =
        "M7,6H9V9H12V11H9V14H7V11H4V9H7V6" +
        "M18,9A1,1 0 0,1 19,10A1,1 0 0,1 18,11A1,1 0 0,1 17,10A1,1 0 0,1 18,9" +
        "M15,12A1,1 0 0,1 16,13A1,1 0 0,1 15,14A1,1 0 0,1 14,13A1,1 0 0,1 15,12" +
        "M21,6H3C1.89,6 1,6.89 1,8V16C1,17.11 1.89,18 3,18H21" +
        "C22.11,18 23,17.11 23,16V8C23,6.89 22.11,6 21,6Z";
    private static final String SVG_WORK =
        "M20,6H16V4C16,2.89 15.11,2 14,2H10C8.89,2 8,2.89 8,4V6" +
        "H4C2.89,6 2,6.89 2,8V19C2,20.11 2.89,21 4,21H20" +
        "C21.11,21 22,20.11 22,19V8C22,6.89 21.11,6 20,6M10,4H14V6H10V4Z";
    /** Play triangle – used as the Resume icon in the BUTTON_PAUSE slot when the timer is paused. */
    private static final String SVG_RESUME =
        "M8,5.14V19.14L19,12.14L8,5.14Z";

    /** Matches SVG number tokens (integers, decimals, scientific notation). */
    private static final Pattern NUMBER_RE =
        Pattern.compile("[+-]?\\d*\\.?\\d+(?:[eE][+-]?\\d+)?");

    // ITaskbarList3 vtable indexes
    private static final int VT_HR_INIT = 3;
    private static final int VT_THUMB_BAR_ADD_BUTTONS    = 15;
    private static final int VT_THUMB_BAR_UPDATE_BUTTONS = 16;
    private static final int VT_RELEASE = 2;
    /** IUnknown::QueryInterface is always at vtable index 0. */
    private static final int VT_QUERY_INTERFACE = 0;

    private WinDef.HWND hwnd;
    private Pointer iTaskbarList3;
    private Pointer originalWndProc;
    private WinUser.WindowProc windowProc;
    private THUMBBUTTON[] buttons;
    private WinDef.HICON[] buttonIcons;
    private Path[] buttonIconFiles;

    private Runnable onReset;
    private Runnable onPause;
    private Runnable onResume;
    private Runnable onFinishWork;
    private Runnable onTakeBreak;
    private Runnable onGoToWork;

    private boolean installed;
    private boolean buttonsAdded;
    private boolean comInitializedByThisThread;

    /** ID of the registered "TaskbarButtonCreated" window message (0 if registration failed). */
    private int wmTaskbarButtonCreated;

    /** Cached timer state so WM_TASKBARBUTTONCREATED can restore correct button states. */
    private TimerMode lastMode    = TimerMode.WORK;
    private boolean  lastIsPaused = false;

    public void install(Runnable onReset,
                        Runnable onPause,
                        Runnable onResume,
                        Runnable onFinishWork,
                        Runnable onTakeBreak,
                        Runnable onGoToWork) {
        if (!isWindows() || installed) {
            return;
        }

        this.onReset      = onReset;
        this.onPause      = onPause;
        this.onResume     = onResume;
        this.onFinishWork = onFinishWork;
        this.onTakeBreak  = onTakeBreak;
        this.onGoToWork   = onGoToWork;

        try {
            if (!initializeComAndTaskbar()) {
                return;
            }

            hwnd = resolveCurrentProcessWindow();
            if (hwnd == null) {
                LOG.fine("Taskbar preview buttons: HWND not found, skipping installation.");
                dispose();
                return;
            }

            // Register the shell message BEFORE installing the WndProc hook so we
            // never miss the notification even if it arrives during hook installation.
            wmTaskbarButtonCreated =
                    User32.INSTANCE.RegisterWindowMessage("TaskbarButtonCreated");
            if (wmTaskbarButtonCreated == 0) {
                LOG.warning("RegisterWindowMessage(TaskbarButtonCreated) failed");
            }

            prepareButtons();
            installWindowProcHook();

            // Mark as installed so updateState() stores the current mode/pause flag.
            installed = true;
            LOG.fine(() -> "Taskbar preview buttons installed: HWND=0x%X  wmTaskbarButtonCreated=%d"
                    .formatted(Pointer.nativeValue(hwnd.getPointer()), wmTaskbarButtonCreated));

            // Attempt an immediate ThumbBarAddButtons call.  This succeeds when the
            // taskbar button already exists (typical case for Platform.runLater after
            // Stage.show()).  If it fails the WM_TASKBARBUTTONCREATED handler retries.
            addThumbButtons();

        } catch (Throwable t) {
            LOG.log(Level.WARNING,
                    "Taskbar preview button installation failed: {0}",
                    t.getMessage());
            dispose();
        }
    }

    /**
     * Calls {@code ThumbBarAddButtons} and, on success, applies the current
     * button states.  Safe to call multiple times; a second call after
     * WM_TASKBARBUTTONCREATED is expected when Explorer restarts.
     */
    private void addThumbButtons() {
        if (buttons == null || hwnd == null || iTaskbarList3 == null) {
            return;
        }

        // Write every structure to native memory as one contiguous block
        // before handing the pointer to the COM call.
        for (final THUMBBUTTON b : buttons) {
            b.write();
        }

        final WinNT.HRESULT hr = invokeTaskbarMethod(
                VT_THUMB_BAR_ADD_BUTTONS,
                hwnd,
                new WinDef.UINT(buttons.length),
                buttons[0].getPointer());

        if (!COMUtils.FAILED(hr)) {
            buttonsAdded = true;
            LOG.fine(() -> "ThumbBarAddButtons HRESULT=0x%08X – buttonsAdded=true"
                    .formatted(hr.intValue()));
            applyButtonStates();
        } else {
            LOG.warning(() -> String.format(
                    "ThumbBarAddButtons failed: HRESULT=0x%08X – will retry on WM_TASKBARBUTTONCREATED",
                    hr.intValue()));
        }
    }

    public void updateState(TimerMode mode, boolean isPaused) {
        this.lastMode    = mode;
        this.lastIsPaused = isPaused;
        LOG.fine(() -> "updateState: mode=%s  paused=%b  installed=%b  buttonsAdded=%b"
                .formatted(mode, isPaused, installed, buttonsAdded));
        if (!installed || !buttonsAdded || buttons == null) {
            return;
        }
        applyButtonStates();
    }

    /**
     * Pushes current {@link #lastMode}/{@link #lastIsPaused} state to the
     * native thumbnail toolbar via {@code ThumbBarUpdateButtons}.
     *
     * <p>Visibility rules:</p>
     * <ul>
     *   <li><b>Work phase – running</b>: visible: Reset, <em>Pause</em>, Finish Work, Take a Break;
     *       Go to Work is <em>hidden</em>.</li>
     *   <li><b>Work phase – paused</b>: the Pause slot is replaced by an active <em>Resume</em> button
     *       (same button ID 2002, swapped icon and tooltip) so the user can continue from the
     *       thumbnail preview.</li>
     *   <li><b>Rest phase</b>: visible: Reset, Go to Work;
     *       Pause/Resume, Finish Work, Take a Break are <em>hidden</em>.</li>
     * </ul>
     * Buttons are hidden via {@code THBF_HIDDEN} so they occupy no space in the
     * thumbnail toolbar rather than appearing greyed-out.
     */
    private void applyButtonStates() {
        final boolean inWorkMode = (lastMode == TimerMode.WORK);

        // Reset is always visible and enabled
        setButtonFlags(BUTTON_RESET,
                THBF_ENABLED | THBF_DISMISSONCLICK);

        // BUTTON_PAUSE slot:
        //   • WORK + running → Pause icon, active
        //   • WORK + paused  → Resume icon, active  (swap icon + tooltip)
        //   • REST           → hidden
        if (inWorkMode) {
            if (!lastIsPaused) {
                setButtonFull(BUTTON_PAUSE, buttonIcons[1], "Pause",
                        THBF_ENABLED | THBF_DISMISSONCLICK);
            } else {
                setButtonFull(BUTTON_PAUSE, buttonIcons[ICON_SLOT_RESUME], "Resume",
                        THBF_ENABLED | THBF_DISMISSONCLICK);
            }
        } else {
            setButtonFlags(BUTTON_PAUSE, THBF_HIDDEN);
        }

        // Finish work: visible and enabled in work phase, hidden in rest phase
        setButtonFlags(BUTTON_FINISH_WORK,
                inWorkMode ? (THBF_ENABLED | THBF_DISMISSONCLICK) : THBF_HIDDEN);

        // Take a break: visible and enabled in work phase, hidden in rest phase
        setButtonFlags(BUTTON_TAKE_BREAK,
                inWorkMode ? (THBF_ENABLED | THBF_DISMISSONCLICK) : THBF_HIDDEN);

        // Go to Work: hidden in work phase, visible and enabled in rest phase
        setButtonFlags(BUTTON_GO_TO_WORK,
                !inWorkMode ? (THBF_ENABLED | THBF_DISMISSONCLICK) : THBF_HIDDEN);

        try {
            final WinNT.HRESULT hr = invokeTaskbarMethod(
                    VT_THUMB_BAR_UPDATE_BUTTONS,
                    hwnd,
                    new WinDef.UINT(buttons.length),
                    buttons[0].getPointer());
            if (COMUtils.FAILED(hr)) {
                LOG.fine(() -> String.format(
                        "ThumbBarUpdateButtons failed: HRESULT=0x%08X", hr.intValue()));
            } else {
                LOG.fine(() -> "ThumbBarUpdateButtons HRESULT=0x%08X".formatted(hr.intValue()));
            }
        } catch (Throwable t) {
            LOG.log(Level.FINE,
                    "Taskbar preview state update failed: {0}", t.getMessage());
        }
    }

    public void dispose() {
        installed    = false;
        buttonsAdded = false;
        wmTaskbarButtonCreated = 0;

        if (hwnd != null && originalWndProc != null) {
            try {
                User32.INSTANCE.SetWindowLongPtr(hwnd, GWLP_WNDPROC, originalWndProc);
            } catch (Throwable ignored) {
            }
        }
        windowProc = null;
        originalWndProc = null;
        hwnd = null;

        if (buttonIcons != null) {
            for (WinDef.HICON icon : buttonIcons) {
                if (icon != null) {
                    try {
                        User32.INSTANCE.DestroyIcon(icon);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        buttonIcons = null;

        if (buttonIconFiles != null) {
            for (Path p : buttonIconFiles) {
                if (p != null) {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        buttonIconFiles = null;
        buttons = null;

        if (iTaskbarList3 != null) {
            try {
                invokeTaskbarMethod(VT_RELEASE);
            } catch (Throwable ignored) {
            }
        }
        iTaskbarList3 = null;

        if (comInitializedByThisThread) {
            try {
                Ole32.INSTANCE.CoUninitialize();
            } catch (Throwable ignored) {
            }
        }
        comInitializedByThisThread = false;
    }

    private boolean initializeComAndTaskbar() {
        final WinNT.HRESULT initHr = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_APARTMENTTHREADED);
        final int initCode = initHr.intValue();
        if (initCode == COMUtils.S_OK) {
            comInitializedByThisThread = true;
        } else if (initCode == COMUtils.S_FALSE || initCode == 0x80010106) {
            comInitializedByThisThread = false;
        } else if (COMUtils.FAILED(initHr)) {
            LOG.warning(() -> String.format("CoInitializeEx failed: HRESULT=0x%08X", initCode));
            return false;
        }

        // ── Step 1: create via IUnknown ──────────────────────────────────────
        // Requesting ITaskbarList3 directly from CoCreateInstance can return
        // E_NOINTERFACE (0x80004002) on certain Windows builds.  Creating via
        // IUnknown first and then calling QueryInterface is the robust idiom.
        final PointerByReference ppvUnknown = new PointerByReference();
        final WinNT.HRESULT createHr = Ole32.INSTANCE.CoCreateInstance(
                CLSID_TASKBAR_LIST,
                null,
                CLSCTX_INPROC_SERVER,
                IID_IUNKNOWN,
                ppvUnknown);
        if (COMUtils.FAILED(createHr)) {
            LOG.warning(() -> String.format(
                    "CoCreateInstance(IUnknown) failed: HRESULT=0x%08X", createHr.intValue()));
            return false;
        }
        LOG.fine(() -> String.format("CoCreateInstance(IUnknown) OK: HRESULT=0x%08X", createHr.intValue()));

        // ── Step 2a: QueryInterface → ITaskbarList4 (preferred, Windows 7+) ─
        final Pointer pUnknown = ppvUnknown.getValue();
        final PointerByReference ppvTaskbar = new PointerByReference();
        final WinNT.HRESULT qiHr4 = queryInterface(pUnknown, IID_ITASKBAR_LIST4, ppvTaskbar);

        final String acquiredIface;
        final int failHr3;
        if (!COMUtils.FAILED(qiHr4)) {
            acquiredIface = "ITaskbarList4";
            failHr3 = 0;
            LOG.fine(() -> String.format(
                    "QueryInterface(ITaskbarList4) OK: HRESULT=0x%08X", qiHr4.intValue()));
        } else {
            // ── Step 2b: fall back to ITaskbarList3 ─────────────────────────
            LOG.fine(() -> String.format(
                    "QueryInterface(ITaskbarList4) unavailable (HRESULT=0x%08X) – trying ITaskbarList3",
                    qiHr4.intValue()));
            final WinNT.HRESULT qiHr3 = queryInterface(pUnknown, IID_ITASKBAR_LIST3, ppvTaskbar);
            if (!COMUtils.FAILED(qiHr3)) {
                acquiredIface = "ITaskbarList3";
                failHr3 = 0;
                LOG.fine(() -> String.format(
                        "QueryInterface(ITaskbarList3) OK: HRESULT=0x%08X", qiHr3.intValue()));
            } else {
                acquiredIface = null;
                failHr3 = qiHr3.intValue();
            }
        }

        // ── Step 3: release the temporary IUnknown (whether QI succeeded or not)
        releaseComPointer(pUnknown);

        if (acquiredIface == null) {
            LOG.warning(() -> String.format(
                    "COM ITaskbarList unavailable on this system – QueryInterface failed for both " +
                    "ITaskbarList4 (HRESULT=0x%08X) and ITaskbarList3 (HRESULT=0x%08X). " +
                    "Taskbar thumbnail preview buttons will not be installed.",
                    qiHr4.intValue(), failHr3));
            return false;
        }
        LOG.fine(() -> "Acquired COM interface: " + acquiredIface);

        iTaskbarList3 = ppvTaskbar.getValue();

        // ── Step 4: initialise the acquired interface ────────────────────────
        // ITaskbarList4 vtable is a strict superset of ITaskbarList3, so vtable
        // indices VT_HR_INIT / VT_THUMB_BAR_ADD_BUTTONS / VT_THUMB_BAR_UPDATE_BUTTONS
        // are identical for both interfaces.
        final WinNT.HRESULT hrInit = invokeTaskbarMethod(VT_HR_INIT);
        if (COMUtils.FAILED(hrInit)) {
            LOG.warning(() -> String.format(
                    "%s.HrInit failed: HRESULT=0x%08X", acquiredIface, hrInit.intValue()));
            return false;
        }
        LOG.fine(() -> String.format(
                "%s.HrInit OK: HRESULT=0x%08X", acquiredIface, hrInit.intValue()));
        return true;
    }

    /**
     * Calls {@code IUnknown::QueryInterface} on {@code pUnknown} to obtain the
     * interface identified by {@code iid}.
     *
     * @param pUnknown raw pointer to a COM object that implements {@code IUnknown}
     * @param iid      the interface identifier to request
     * @param ppvOut   receives the requested interface pointer on success
     * @return HRESULT returned by {@code QueryInterface}
     */
    private static WinNT.HRESULT queryInterface(Pointer pUnknown, Guid.IID iid,
                                                PointerByReference ppvOut) {
        iid.write();
        final Pointer vtbl  = pUnknown.getPointer(0L);
        final Pointer fnPtr = vtbl.getPointer((long) VT_QUERY_INTERFACE * Native.POINTER_SIZE);
        final Function fn   = Function.getFunction(fnPtr, Function.ALT_CONVENTION);
        final int hr = (Integer) fn.invoke(int.class,
                new Object[]{pUnknown, iid.getPointer(), ppvOut});
        return new WinNT.HRESULT(hr);
    }

    /**
     * Calls {@code IUnknown::Release} on a raw COM pointer.
     * Safe to call with {@code null} – the call is silently skipped.
     *
     * @param p raw COM pointer; may be {@code null}
     */
    private static void releaseComPointer(Pointer p) {
        if (p == null) {
            return;
        }
        try {
            final Pointer vtbl  = p.getPointer(0L);
            final Pointer fnPtr = vtbl.getPointer((long) VT_RELEASE * Native.POINTER_SIZE);
            final Function fn   = Function.getFunction(fnPtr, Function.ALT_CONVENTION);
            fn.invoke(int.class, new Object[]{p});
        } catch (Throwable t) {
            LOG.log(Level.FINE, "releaseComPointer failed: {0}", t.getMessage());
        }
    }

    private void installWindowProcHook() {
        windowProc = (hWnd, uMsg, wParam, lParam) -> {

            // WM_TASKBARBUTTONCREATED is sent when the taskbar button is first
            // created and also after Explorer restarts.  Re-add buttons each time.
            if (wmTaskbarButtonCreated != 0 && uMsg == wmTaskbarButtonCreated) {
                buttonsAdded = false;   // allow ThumbBarAddButtons to be called again
                addThumbButtons();
                // Fall through to forward the message to the original WndProc.
                return User32.INSTANCE.CallWindowProc(originalWndProc, hWnd, uMsg, wParam, lParam);
            }

            if (uMsg == WM_COMMAND) {
                final long raw      = wParam.longValue();
                final int commandId = lowWord(raw);
                final int notifyCode= highWord(raw);
                if (notifyCode == THBN_CLICKED) {
                    dispatchPreviewAction(commandId);
                    return new WinDef.LRESULT(0);
                }
            }
            return User32.INSTANCE.CallWindowProc(originalWndProc, hWnd, uMsg, wParam, lParam);
        };

        final Pointer procPtr = CallbackReference.getFunctionPointer(windowProc);
        originalWndProc = User32.INSTANCE.SetWindowLongPtr(hwnd, GWLP_WNDPROC, procPtr);
    }

    private void dispatchPreviewAction(int commandId) {
        final Runnable action = switch (commandId) {
            case BUTTON_RESET       -> onReset;
            // The PAUSE slot acts as Resume when the timer is paused; the icon and
            // tooltip have already been swapped by applyButtonStates() so the user
            // sees "Resume" – routing the click to the matching callback is consistent.
            case BUTTON_PAUSE       -> lastIsPaused ? onResume : onPause;
            case BUTTON_FINISH_WORK -> onFinishWork;
            case BUTTON_TAKE_BREAK  -> onTakeBreak;
            case BUTTON_GO_TO_WORK  -> onGoToWork;
            default -> null;
        };
        if (action != null) {
            Platform.runLater(action);
        }
    }

    private void prepareButtons() throws IOException {
        buttons         = (THUMBBUTTON[]) new THUMBBUTTON().toArray(BUTTON_COUNT);
        buttonIcons     = new WinDef.HICON[ICON_COUNT];
        buttonIconFiles = new Path[ICON_COUNT];

        // Slot 0 – Reset (replay arrow)  – teal
        buttonIcons[0] = loadButtonIconFromSvg(SVG_RESET,  new Color(0x2D, 0xD4, 0xBF), 0);
        // Slot 1 – Pause (two bars)       – amber
        buttonIcons[1] = loadButtonIconFromSvg(SVG_PAUSE,  new Color(0xF5, 0x9E, 0x0B), 1);
        // Slot 2 – Finish work (clock)    – violet
        buttonIcons[2] = loadButtonIconFromSvg(SVG_CLOCK,  new Color(0x8B, 0x5C, 0xF6), 2);
        // Slot 3 – Take a break (gamepad) – green
        buttonIcons[3] = loadButtonIconFromSvg(SVG_RELAX,  new Color(0x22, 0xC5, 0x5E), 3);
        // Slot 4 – Go to Work (briefcase) – red
        buttonIcons[4] = loadButtonIconFromSvg(SVG_WORK,   new Color(0xEF, 0x44, 0x44), 4);
        // Slot 5 – Resume (play triangle) – bright green; alternate for BUTTON_PAUSE slot when paused
        buttonIcons[ICON_SLOT_RESUME] = loadButtonIconFromSvg(SVG_RESUME, new Color(0x4A, 0xDE, 0x80), ICON_SLOT_RESUME);

        configureButton(buttons[0], BUTTON_RESET,       buttonIcons[0], "Reset");
        configureButton(buttons[1], BUTTON_PAUSE,       buttonIcons[1], "Pause");
        configureButton(buttons[2], BUTTON_FINISH_WORK, buttonIcons[2], "Finish work and start short break");
        configureButton(buttons[3], BUTTON_TAKE_BREAK,  buttonIcons[3], "Take a break");
        configureButton(buttons[4], BUTTON_GO_TO_WORK,  buttonIcons[4], "Go to Work");
    }

    /**
     * Renders a 32 × 32 thumbnail-toolbar icon from a Material-Design SVG path,
     * writes it as a temporary {@code .ico} file, and loads it with {@code LoadImage}.
     *
     * <p>Visual design:</p>
     * <ul>
     *   <li>Dark (near-black) rounded-rectangle background – matches the app's neon icon style.</li>
     *   <li>Thin accent-coloured border.</li>
     *   <li>Near-white SVG vector icon (24 × 24 viewbox scaled to ~20 × 20 px, centred).</li>
     * </ul>
     */
    private WinDef.HICON loadButtonIconFromSvg(String svgPath, Color accent, int slot)
            throws IOException {
        final int size = 32;
        final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // ── Background ────────────────────────────────────────────────────────
            g.setColor(new Color(22, 25, 32, 230));
            g.fillRoundRect(1, 1, size - 2, size - 2, 10, 10);

            // ── Accent border ─────────────────────────────────────────────────────
            g.setColor(accent);
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(1, 1, size - 3, size - 3, 10, 10);

            // ── SVG icon: scale 24 × 24 → 20 × 20, centred inside the 32 × 32 canvas ──
            final double iconSize = 20.0;
            final double svgScale = iconSize / 24.0;
            final double offsetX  = (size - iconSize) / 2.0;
            final double offsetY  = (size - iconSize) / 2.0;

            final Shape rawShape = parseSvgPath(svgPath);
            final AffineTransform at = AffineTransform.getTranslateInstance(offsetX, offsetY);
            at.scale(svgScale, svgScale);
            final Shape scaledShape = at.createTransformedShape(rawShape);

            // Near-white fill so icons stay visible both enabled and (Windows-dimmed) disabled
            g.setColor(new Color(235, 240, 255));
            g.fill(scaledShape);

        } finally {
            g.dispose();
        }

        final int[] argb  = image.getRGB(0, 0, size, size, null, 0, size);
        final byte[] ico  = buildBmpIco(argb, size, size);

        final Path iconFile = Files.createTempFile("tomato-thumb-" + slot + "-", ".ico");
        iconFile.toFile().deleteOnExit();
        Files.write(iconFile, ico, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        buttonIconFiles[slot] = iconFile;

        final WinNT.HANDLE handle = User32.INSTANCE.LoadImage(
                null, iconFile.toString(), IMAGE_ICON, 16, 16, LR_LOADFROMFILE);

        if (handle == null || Pointer.nativeValue(handle.getPointer()) == 0L) {
            throw new IOException("LoadImage failed for preview button icon: " + iconFile);
        }

        return new WinDef.HICON(handle.getPointer());
    }

    /**
     * Parses a Material-Design SVG path-data string into an AWT {@link Shape}.
     *
     * <p>Supported commands: {@code M m L l H h V v C c A a Z z}.
     * The 24 × 24 viewbox is preserved; call
     * {@link AffineTransform#createTransformedShape} to scale/translate the result.</p>
     */
    private static Shape parseSvgPath(String d) {
        final Path2D.Double path = new Path2D.Double();
        double x = 0, y = 0;   // current drawing point
        double mx = 0, my = 0; // last moveTo anchor (for closePath reset)

        int i = 0;
        final int len = d.length();

        while (i < len) {
            // skip whitespace
            while (i < len && Character.isWhitespace(d.charAt(i))) i++;
            if (i >= len) break;

            final char cmd = d.charAt(i++);
            if (!Character.isLetter(cmd)) break;

            // Collect raw number text until the next command letter
            final int numStart = i;
            while (i < len && !Character.isLetter(d.charAt(i))) i++;
            final double[] n = parseNumbers(d.substring(numStart, i));

            switch (cmd) {
                case 'M' -> {
                    for (int j = 0; j < n.length; j += 2) {
                        x = n[j]; y = n[j + 1];
                        if (j == 0) { path.moveTo(x, y); mx = x; my = y; }
                        else          path.lineTo(x, y);
                    }
                }
                case 'm' -> {
                    for (int j = 0; j < n.length; j += 2) {
                        x += n[j]; y += n[j + 1];
                        if (j == 0) { path.moveTo(x, y); mx = x; my = y; }
                        else          path.lineTo(x, y);
                    }
                }
                case 'L' -> {
                    for (int j = 0; j < n.length; j += 2) {
                        x = n[j]; y = n[j + 1]; path.lineTo(x, y);
                    }
                }
                case 'l' -> {
                    for (int j = 0; j < n.length; j += 2) {
                        x += n[j]; y += n[j + 1]; path.lineTo(x, y);
                    }
                }
                case 'H' -> { for (double nx : n) { x = nx;  path.lineTo(x, y); } }
                case 'h' -> { for (double dx : n) { x += dx; path.lineTo(x, y); } }
                case 'V' -> { for (double ny : n) { y = ny;  path.lineTo(x, y); } }
                case 'v' -> { for (double dy : n) { y += dy; path.lineTo(x, y); } }
                case 'C' -> {
                    for (int j = 0; j < n.length; j += 6) {
                        path.curveTo(n[j], n[j+1], n[j+2], n[j+3], n[j+4], n[j+5]);
                        x = n[j+4]; y = n[j+5];
                    }
                }
                case 'c' -> {
                    for (int j = 0; j < n.length; j += 6) {
                        path.curveTo(x+n[j], y+n[j+1], x+n[j+2], y+n[j+3],
                                     x+n[j+4], y+n[j+5]);
                        x += n[j+4]; y += n[j+5];
                    }
                }
                case 'A' -> {
                    for (int j = 0; j < n.length; j += 7) {
                        final double nx = n[j+5], ny = n[j+6];
                        svgArcTo(path, x, y, n[j], n[j+1], n[j+2],
                                 (int) n[j+3], (int) n[j+4], nx, ny);
                        x = nx; y = ny;
                    }
                }
                case 'a' -> {
                    for (int j = 0; j < n.length; j += 7) {
                        final double nx = x + n[j+5], ny = y + n[j+6];
                        svgArcTo(path, x, y, n[j], n[j+1], n[j+2],
                                 (int) n[j+3], (int) n[j+4], nx, ny);
                        x = nx; y = ny;
                    }
                }
                case 'Z', 'z' -> { path.closePath(); x = mx; y = my; }
                default -> { /* unknown command – skip */ }
            }
        }
        return path;
    }

    /**
     * Appends an SVG elliptical arc segment to {@code path}, implementing the
     * <a href="https://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes">
     * SVG arc → centre-parameterisation</a> conversion.
     *
     * @param path         target path (current point must be (x1, y1))
     * @param x1           current point x (arc start)
     * @param y1           current point y (arc start)
     * @param rx           semi-axis x
     * @param ry           semi-axis y
     * @param xRotDeg      x-axis rotation in degrees
     * @param largeArcFlag 0 or 1
     * @param sweepFlag    0 = counter-clockwise, 1 = clockwise
     * @param x2           arc end point x
     * @param y2           arc end point y
     */
    private static void svgArcTo(Path2D.Double path,
                                  double x1, double y1,
                                  double rx, double ry, double xRotDeg,
                                  int largeArcFlag, int sweepFlag,
                                  double x2, double y2) {
        if (x1 == x2 && y1 == y2) return;
        if (rx == 0 || ry == 0)   { path.lineTo(x2, y2); return; }

        rx = Math.abs(rx);
        ry = Math.abs(ry);

        final double phi    = Math.toRadians(xRotDeg);
        final double cosPhi = Math.cos(phi);
        final double sinPhi = Math.sin(phi);

        // Step 1 – mid-point transform
        final double dx  = (x1 - x2) / 2.0;
        final double dy  = (y1 - y2) / 2.0;
        final double x1p =  cosPhi * dx + sinPhi * dy;
        final double y1p = -sinPhi * dx + cosPhi * dy;

        // Step 2 – fix radii if too small, then find centre (cx', cy')
        double rx2 = rx * rx, ry2 = ry * ry;
        final double x1p2 = x1p * x1p, y1p2 = y1p * y1p;
        final double lambda = x1p2 / rx2 + y1p2 / ry2;
        if (lambda > 1) {
            final double ls = Math.sqrt(lambda);
            rx *= ls; ry *= ls;
            rx2 = rx * rx; ry2 = ry * ry;
        }

        final double num = rx2 * ry2 - rx2 * y1p2 - ry2 * x1p2;
        final double den = rx2 * y1p2 + ry2 * x1p2;
        double sq = (num <= 0) ? 0 : Math.sqrt(num / den);
        if (largeArcFlag == sweepFlag) sq = -sq;

        final double cxp =  sq * rx * y1p / ry;
        final double cyp = -sq * ry * x1p / rx;

        // Step 3 – un-rotate
        final double cx = cosPhi * cxp - sinPhi * cyp + (x1 + x2) / 2.0;
        final double cy = sinPhi * cxp + cosPhi * cyp + (y1 + y2) / 2.0;

        // Step 4 – start angle and sweep angle
        final double ux = (x1p - cxp) / rx,  uy = (y1p - cyp) / ry;
        final double vx = (-x1p - cxp) / rx, vy = (-y1p - cyp) / ry;

        double theta1 = svgVectorAngle(1, 0, ux, uy);
        double dtheta = svgVectorAngle(ux, uy, vx, vy);

        if (sweepFlag == 0 && dtheta > 0) dtheta -= 2 * Math.PI;
        if (sweepFlag == 1 && dtheta < 0) dtheta += 2 * Math.PI;

        // AWT Arc2D measures angles in degrees from 3-o'clock, positive = CCW in
        // screen coords (Y-down), whereas SVG theta is positive = CCW in math coords.
        // The sign flip (−) converts between the two conventions.
        final double startDeg  = -Math.toDegrees(theta1);
        final double extentDeg = -Math.toDegrees(dtheta);

        final Arc2D.Double arc = new Arc2D.Double(
                cx - rx, cy - ry, 2 * rx, 2 * ry,
                startDeg, extentDeg, Arc2D.OPEN);
        path.append(arc, true);   // connect=true joins with the current point
    }

    /** Signed angle between 2-D vectors {@code (ux,uy)} and {@code (vx,vy)}. */
    private static double svgVectorAngle(double ux, double uy, double vx, double vy) {
        final double n = Math.sqrt(ux * ux + uy * uy) * Math.sqrt(vx * vx + vy * vy);
        if (n == 0) return 0;
        final double cos   = Math.max(-1, Math.min(1, (ux * vx + uy * vy) / n));
        final double angle = Math.acos(cos);
        return (ux * vy - uy * vx < 0) ? -angle : angle;
    }

    /**
     * Tokenises an SVG number-sequence string into a {@code double[]} array.
     *
     * <p>Handles integers, decimals ({@code .5}), negative numbers and scientific
     * notation.  Commas, spaces and consecutive decimal points all act as
     * separators (the regex simply finds all number tokens).</p>
     */
    private static double[] parseNumbers(String s) {
        if (s == null || s.isBlank()) return new double[0];
        final Matcher m = NUMBER_RE.matcher(s);
        final List<Double> result = new ArrayList<>();
        while (m.find()) result.add(Double.parseDouble(m.group()));
        return result.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private static void configureButton(THUMBBUTTON button, int id, WinDef.HICON icon, String tooltip) {
        button.dwMask = new WinDef.DWORD(THB_ICON | THB_TOOLTIP | THB_FLAGS);
        button.iId = new WinDef.UINT(id);
        button.iBitmap = new WinDef.UINT(0);
        button.hIcon = icon;
        button.szTip = toTooltip(tooltip);
        button.dwFlags = new WinDef.DWORD(THBF_ENABLED | THBF_DISMISSONCLICK);
        button.write();
    }

    private void setButtonFlags(int buttonId, int flags) {
        for (THUMBBUTTON button : buttons) {
            if (button.iId != null && button.iId.intValue() == buttonId) {
                button.dwFlags = new WinDef.DWORD(flags);
                button.write();
                return;
            }
        }
    }

    /**
     * Updates the icon, tooltip, <em>and</em> flags for the THUMBBUTTON identified by
     * {@code buttonId} in a single call.  Sets {@code dwMask} to
     * {@code THB_ICON | THB_TOOLTIP | THB_FLAGS} so that {@code ThumbBarUpdateButtons}
     * picks up all three changes atomically.
     *
     * <p>Used to swap the Pause slot between the Pause and Resume icons.</p>
     */
    private void setButtonFull(int buttonId, WinDef.HICON icon, String tooltip, int flags) {
        for (THUMBBUTTON button : buttons) {
            if (button.iId != null && button.iId.intValue() == buttonId) {
                button.dwMask  = new WinDef.DWORD(THB_ICON | THB_TOOLTIP | THB_FLAGS);
                button.hIcon   = icon;
                button.szTip   = toTooltip(tooltip);
                button.dwFlags = new WinDef.DWORD(flags);
                button.write();
                return;
            }
        }
    }

    private WinNT.HRESULT invokeTaskbarMethod(int vtableIndex, Object... args) {
        final Pointer vtbl = iTaskbarList3.getPointer(0L);
        final Pointer fnPtr = vtbl.getPointer((long) vtableIndex * Native.POINTER_SIZE);
        final Function function = Function.getFunction(fnPtr, Function.ALT_CONVENTION);

        final Object[] nativeArgs = new Object[args.length + 1];
        nativeArgs[0] = iTaskbarList3;
        System.arraycopy(args, 0, nativeArgs, 1, args.length);

        final int hr = (Integer) function.invoke(int.class, nativeArgs);
        return new WinNT.HRESULT(hr);
    }

    private static WinDef.HWND resolveCurrentProcessWindow() {
        final int currentPid = (int) ProcessHandle.current().pid();
        final IntByReference pidRef = new IntByReference();
        final List<WinDef.HWND> candidates = new ArrayList<>();

        User32.INSTANCE.EnumWindows((hwnd, data) -> {
            if (!User32.INSTANCE.IsWindowVisible(hwnd)) {
                return true;
            }
            pidRef.setValue(0);
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef);
            if (pidRef.getValue() != currentPid) {
                return true;
            }
            // Skip pure tool windows – they never receive a Windows taskbar button.
            // A window with BOTH WS_EX_TOOLWINDOW and WS_EX_APPWINDOW is kept because
            // WS_EX_APPWINDOW forces taskbar participation regardless of the tool-window flag.
            final int exStyle = User32.INSTANCE.GetWindowLong(hwnd, GWL_EXSTYLE);
            if ((exStyle & WS_EX_TOOLWINDOW) != 0 && (exStyle & WS_EX_APPWINDOW) == 0) {
                LOG.fine(() -> "HWND 0x%X skipped – WS_EX_TOOLWINDOW (exStyle=0x%X, pid=%d)"
                        .formatted(Pointer.nativeValue(hwnd.getPointer()), exStyle, currentPid));
                return true;
            }
            candidates.add(hwnd);
            return true;
        }, Pointer.NULL);

        if (candidates.isEmpty()) {
            LOG.fine(() -> "HWND resolution: no eligible visible windows found for pid=%d"
                    .formatted(currentPid));
            return null;
        }

        // Prefer unowned top-level windows (they are guaranteed to get a taskbar button).
        // Among unowned candidates, prefer WS_EX_APPWINDOW (explicitly forced onto taskbar).
        WinDef.HWND best = null;
        for (final var candidate : candidates) {
            final WinDef.HWND owner =
                    User32.INSTANCE.GetWindow(candidate, new WinDef.DWORD(GW_OWNER));
            final boolean unowned = (owner == null
                    || Pointer.nativeValue(owner.getPointer()) == 0L);
            if (unowned) {
                if (best == null) {
                    best = candidate;    // first unowned – use as tentative best
                }
                final int exStyle = User32.INSTANCE.GetWindowLong(candidate, GWL_EXSTYLE);
                if ((exStyle & WS_EX_APPWINDOW) != 0) {
                    best = candidate;
                    break;              // ideal: unowned + explicitly on taskbar
                }
            }
        }
        if (best == null) {
            best = candidates.get(0);   // fallback: first eligible visible process window
        }

        if (LOG.isLoggable(Level.FINE)) {
            final char[] titleBuf = new char[256];
            User32.INSTANCE.GetWindowText(best, titleBuf, titleBuf.length);
            final String title = new String(titleBuf).trim();
            final int exStyle  = User32.INSTANCE.GetWindowLong(best, GWL_EXSTYLE);
            final long hval    = Pointer.nativeValue(best.getPointer());
            final int  ccount  = candidates.size();
            LOG.fine(() -> "HWND resolved: 0x%X  title='%s'  exStyle=0x%X  pid=%d  candidates=%d"
                    .formatted(hval, title, exStyle, currentPid, ccount));
        }
        return best;
    }

    private static char[] toTooltip(String text) {
        final char[] tip = new char[260];
        final char[] src = text.toCharArray();
        final int copyLen = Math.min(src.length, tip.length - 1);
        System.arraycopy(src, 0, tip, 0, copyLen);
        return tip;
    }

    private static int lowWord(long value) {
        return (int) (value & 0xFFFFL);
    }

    private static int highWord(long value) {
        return (int) ((value >>> 16) & 0xFFFFL);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    /**
     * Minimal native THUMBBUTTON structure used by ITaskbarList3.
     */
    public static final class THUMBBUTTON extends Structure {

        public WinDef.DWORD dwMask;
        public WinDef.UINT iId;
        public WinDef.UINT iBitmap;
        public WinDef.HICON hIcon;
        public char[] szTip = new char[260];
        public WinDef.DWORD dwFlags;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("dwMask", "iId", "iBitmap", "hIcon", "szTip", "dwFlags");
        }
    }

    private static byte[] buildBmpIco(final int[] argb, final int width, final int height) {
        final int andMaskRowBytes = ((width + 31) / 32) * 4;
        final int bmpDataSize = 40 + width * height * 4 + andMaskRowBytes * height;
        final int icoOffset = 6 + 16;
        final byte sz = (byte) (width >= 256 ? 0 : width);

        final ByteBuffer buf = ByteBuffer
                .allocate(icoOffset + bmpDataSize)
                .order(ByteOrder.LITTLE_ENDIAN);

        buf.putShort((short) 0);
        buf.putShort((short) 1);
        buf.putShort((short) 1);

        buf.put(sz);
        buf.put(sz);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.putShort((short) 1);
        buf.putShort((short) 32);
        buf.putInt(bmpDataSize);
        buf.putInt(icoOffset);

        buf.putInt(40);
        buf.putInt(width);
        buf.putInt(height * 2);
        buf.putShort((short) 1);
        buf.putShort((short) 32);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                final int px = argb[y * width + x];
                buf.put((byte) (px & 0xFF));
                buf.put((byte) ((px >> 8) & 0xFF));
                buf.put((byte) ((px >> 16) & 0xFF));
                buf.put((byte) ((px >> 24) & 0xFF));
            }
        }

        for (int i = 0; i < andMaskRowBytes * height; i++) {
            buf.put((byte) 0);
        }

        return buf.array();
    }
}



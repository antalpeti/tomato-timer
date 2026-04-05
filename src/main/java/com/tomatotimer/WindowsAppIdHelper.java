package com.tomatotimer;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sets the Windows <em>Application User Model ID</em> (AppUserModelID) for the
 * current process early at startup, before any window is shown.
 *
 * <h3>Why this is needed</h3>
 * <p>On Windows the taskbar groups windows by their AppUserModelID.  JavaFX
 * processes run under {@code javaw.exe}, so without an explicit ID Windows
 * uses the {@code javaw} identity and may show the generic Java-cup icon in
 * the taskbar even after {@code Stage.getIcons()} has been populated.
 * Calling {@code Shell32.SetCurrentProcessExplicitAppUserModelID} before the
 * first window appears pins the process to its own unique ID, which lets
 * Windows use the correct icon and lets users pin the shortcut.</p>
 *
 * <h3>Safety</h3>
 * <ul>
 *   <li>No-op on non-Windows platforms.</li>
 *   <li>All JNA calls are wrapped in a broad {@code catch(Throwable)} so a
 *       missing native library or an unsupported OS version never crashes the
 *       app.</li>
     *   <li>Must be called from the {@code main} thread, before
     *       {@link javafx.application.Application#launch}.</li>
 * </ul>
 */
public final class WindowsAppIdHelper {

    /** Reverse-domain Application User Model ID registered with Windows. */
    public static final String APP_USER_MODEL_ID = "com.tomatotimer.TomatoTimer";

    private static final Logger LOG = Logger.getLogger(WindowsAppIdHelper.class.getName());

    // -------------------------------------------------------------------------
    // Minimal Shell32 JNA interface – only the one method we need.
    // Using a private inner interface avoids tying us to a specific jna-platform
    // Shell32 coverage version and keeps the footprint minimal.
    // -------------------------------------------------------------------------

    private interface Shell32SetAppId extends StdCallLibrary {
        Shell32SetAppId INSTANCE = Native.load(
                "shell32", Shell32SetAppId.class, W32APIOptions.DEFAULT_OPTIONS);

        /**
         * Sets the AppUserModelID for the current process.
         *
         * @param appId wide-character string (JNA maps {@link WString} → {@code PCWSTR})
         * @return {@code HRESULT} – {@code S_OK} (0) on success
         */
        int SetCurrentProcessExplicitAppUserModelID(WString appId);
    }

    private WindowsAppIdHelper() { /* utility class */ }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Registers {@link #APP_USER_MODEL_ID} with Windows.
     *
     * <p>Call once from the application {@code main} method <em>before</em>
     * {@link javafx.application.Application#launch} so that the ID is
     * registered before the JavaFX toolkit initialises native Windows peer
     * windows and before the first taskbar button is created.</p>
     *
     * <p>This method is a no-op on non-Windows platforms or when JNA is not
     * available.</p>
     */
    public static void apply() {
        if (!isWindows()) return;
        try {
            final int hr = Shell32SetAppId.INSTANCE
                    .SetCurrentProcessExplicitAppUserModelID(new WString(APP_USER_MODEL_ID));
            if (hr != 0) {
                LOG.warning(() -> String.format(
                        "SetCurrentProcessExplicitAppUserModelID returned HRESULT 0x%08X", hr));
            } else {
                LOG.fine(() -> "AppUserModelID set to: " + APP_USER_MODEL_ID);
            }
        } catch (Throwable t) {
            // JNA not on classpath, native library missing, or OS too old – skip silently.
            LOG.log(Level.WARNING,
                    "Could not set Windows AppUserModelID (taskbar icon may show Java cup): {0}",
                    t.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}


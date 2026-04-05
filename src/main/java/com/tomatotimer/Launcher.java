package com.tomatotimer;

/**
 * Stand-alone launcher – avoids the "JavaFX runtime components are missing"
 * error when running the fat-jar on some JDKs.
 */
public class Launcher {
    public static void main(String[] args) {
        // Apply the Windows AppUserModelID here as well so that this entry
        // point (used by the fat-jar manifest) also sets the ID before
        // Application.launch() spins up the JavaFX toolkit.
        // No-op on non-Windows and safe when JNA is absent.
        WindowsAppIdHelper.apply();
        App.main(args);
    }
}


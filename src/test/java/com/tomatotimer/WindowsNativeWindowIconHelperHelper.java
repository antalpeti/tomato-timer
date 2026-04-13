package com.tomatotimer;

import java.lang.reflect.Field;

public class WindowsNativeWindowIconHelperHelper {

    public static final String OS_NAME_PROPERTY = "os.name";
    public static final String OS_LINUX         = "Linux";
    public static final String OS_WINDOWS       = "Windows 10";

    protected static boolean invokeIsWindows() throws Exception {
        final var m = WindowsNativeWindowIconHelper.class
                .getDeclaredMethod("isWindows");
        m.setAccessible(true);
        return (boolean) m.invoke(null);
    }

    protected static long invokeComputeCrc32(byte[] data) throws Exception {
        final var m = WindowsNativeWindowIconHelper.class
                .getDeclaredMethod("computeCrc32", byte[].class);
        m.setAccessible(true);
        return (long) m.invoke(null, data);
    }

    protected static byte[] invokeBuildBmpIco(int[] argb, int width, int height)
            throws Exception {
        final var m = WindowsNativeWindowIconHelper.class
                .getDeclaredMethod("buildBmpIco", int[].class, int.class, int.class);
        m.setAccessible(true);
        return (byte[]) m.invoke(null, argb, width, height);
    }

    protected String setOsName(String newName) {
        final var original = System.getProperty(OS_NAME_PROPERTY, "");
        System.setProperty(OS_NAME_PROPERTY, newName);
        return original;
    }

    /**
     * Reads the private static {@code cacheInitialized} field via reflection.
     * Used to assert cache state in unit tests without modifying production code.
     */
    protected static boolean getCacheInitialized() throws Exception {
        final Field f = WindowsNativeWindowIconHelper.class
                .getDeclaredField("cacheInitialized");
        f.setAccessible(true);
        return f.getBoolean(null);
    }

    /**
     * Sets the private static {@code cacheInitialized} field via reflection,
     * allowing tests to pre-seed a specific cache state.
     */
    protected static void setCacheInitialized(boolean value) throws Exception {
        final Field f = WindowsNativeWindowIconHelper.class
                .getDeclaredField("cacheInitialized");
        f.setAccessible(true);
        f.setBoolean(null, value);
    }

    /**
     * Invokes the public {@link WindowsNativeWindowIconHelper#resetCache()} method.
     * Provided as a helper so tests don't need to call the static method directly
     * (keeping the test pattern consistent with the other helper methods).
     */
    protected static void invokeResetCache() {
        WindowsNativeWindowIconHelper.resetCache();
    }
}


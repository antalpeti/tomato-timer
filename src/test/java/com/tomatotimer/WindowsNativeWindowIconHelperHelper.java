package com.tomatotimer;

import java.lang.reflect.Method;

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
}


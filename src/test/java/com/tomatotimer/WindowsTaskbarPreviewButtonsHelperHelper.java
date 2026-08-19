package com.tomatotimer;

import java.awt.Color;

public class WindowsTaskbarPreviewButtonsHelperHelper {

    public static final String OS_NAME_PROPERTY = "os.name";
    public static final String OS_LINUX         = "Linux";
    public static final String OS_WINDOWS       = "Windows 10";

    protected static int invokeLowWord(long value) throws Exception {
        final var m = WindowsTaskbarPreviewButtonsHelper.class
                .getDeclaredMethod("lowWord", long.class);
        m.setAccessible(true);
        return (int) m.invoke(null, value);
    }

    protected static int invokeHighWord(long value) throws Exception {
        final var m = WindowsTaskbarPreviewButtonsHelper.class
                .getDeclaredMethod("highWord", long.class);
        m.setAccessible(true);
        return (int) m.invoke(null, value);
    }

    protected static Color invokeHexToAwtColor(String hex) throws Exception {
        final var m = WindowsTaskbarPreviewButtonsHelper.class
                .getDeclaredMethod("hexToAwtColor", String.class);
        m.setAccessible(true);
        return (Color) m.invoke(null, hex);
    }

    protected static double[] invokeParseNumbers(String s) throws Exception {
        final var m = WindowsTaskbarPreviewButtonsHelper.class
                .getDeclaredMethod("parseNumbers", String.class);
        m.setAccessible(true);
        return (double[]) m.invoke(null, s);
    }

    protected static boolean invokeIsWindows() throws Exception {
        final var m = WindowsTaskbarPreviewButtonsHelper.class
                .getDeclaredMethod("isWindows");
        m.setAccessible(true);
        return (boolean) m.invoke(null);
    }

    protected static char[] invokeToTooltip(String text) throws Exception {
        final var m = WindowsTaskbarPreviewButtonsHelper.class
                .getDeclaredMethod("toTooltip", String.class);
        m.setAccessible(true);
        return (char[]) m.invoke(null, text);
    }

    protected String setOsName(String newName) {
        final var original = System.getProperty(OS_NAME_PROPERTY, "");
        System.setProperty(OS_NAME_PROPERTY, newName);
        return original;
    }
}


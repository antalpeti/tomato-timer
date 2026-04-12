package com.tomatotimer;

public class WindowsAppIdHelperHelper {

    public static final String EXPECTED_APP_ID = "com.tomatotimer.TomatoTimer";

    public static final String OS_NAME_PROPERTY = "os.name";
    public static final String OS_LINUX         = "Linux";
    public static final String OS_WINDOWS       = "Windows 10";

    protected String setOsName(String newName) {
        final var original = System.getProperty(OS_NAME_PROPERTY, "");
        System.setProperty(OS_NAME_PROPERTY, newName);
        return original;
    }
}


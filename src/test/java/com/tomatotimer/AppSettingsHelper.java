package com.tomatotimer;

public class AppSettingsHelper {

    /** Must match {@link AppSettings#CURRENT_SETTINGS_VERSION}. */
    public static final int    CURRENT_SETTINGS_VERSION   = AppSettings.CURRENT_SETTINGS_VERSION;

    public static final int    DEFAULT_WORK_TIME          = 25;
    public static final int    DEFAULT_RELAX_TIME         = 5;
    public static final int    DEFAULT_RELAX_TIME_LONG    = 15;
    public static final boolean DEFAULT_GCAL_ENABLE        = true;
    public static final boolean DEFAULT_GCAL_COPY_CLIP     = false;
    public static final boolean DEFAULT_ALWAYS_ON_TOP      = false;
    public static final NeonPreset DEFAULT_NEON_PRESET     = NeonPreset.AURORA_DRIFT;
    public static final NeonGlowProfile DEFAULT_NEON_GLOW_PROFILE = NeonGlowProfile.BALANCED;
    public static final boolean DEFAULT_TASKBAR_ICON_ENABLE = true;
    /** Default for {@code taskbar_font_size_mmss} (MM:SS display, hours == 0). */
    public static final double DEFAULT_TASKBAR_FONT_SIZE_MMSS   = 23.0;
    /** Default for {@code taskbar_font_size_hhmmss} (HH:MM:SS display, hours > 0). */
    public static final double DEFAULT_TASKBAR_FONT_SIZE_HHMMSS = 17.0;
    public static final TaskbarTimeLayout DEFAULT_TASKBAR_LAYOUT = TaskbarTimeLayout.VERTICAL;
    public static final ThemeSelectionMode DEFAULT_THEME_SELECTION_MODE = ThemeSelectionMode.SHUFFLE;
    public static final double DEFAULT_WINDOW_X           = -1.0;
    public static final double DEFAULT_WINDOW_Y           = -1.0;
    public static final double DEFAULT_WINDOW_WIDTH       = 260.0;
    public static final double DEFAULT_WINDOW_HEIGHT      = 44.0;
    public static final double DELTA                      = 1e-9;

    public static final int    TEST_WORK_TIME       = 45;
    public static final int    TEST_RELAX_TIME      = 10;
    public static final int    TEST_RELAX_TIME_LONG = 20;
    public static final String TEST_GCAL_SRC        = "https://example.com/cal";
    public static final String TEST_GCAL_TEXT       = "Work session";
    public static final double TEST_WIN_X           = 100.0;
    public static final double TEST_WIN_Y           = 200.0;
    public static final double TEST_WIN_W           = 320.0;
    public static final double TEST_WIN_H           = 60.0;
    public static final String TEST_RESTORE_DT      = "2026-04-12T10:00:00";
    public static final int    TEST_RESTORE_MODE    = 2;
    public static final String TEST_SOUND_PATH      = "/tmp/beep.wav";
    public static final double TEST_TASKBAR_FONT_SZ_MMSS   = 14.0;
    public static final double TEST_TASKBAR_FONT_SZ_HHMMSS = 13.0;
}

package com.tomatotimer;

import java.util.prefs.Preferences;

/**
 * Persistent application settings backed by java.util.prefs.Preferences.
 * Equivalent to TimerSettings in the WPF project.
 */
public class AppSettings {

    private static final AppSettings INSTANCE = new AppSettings();
    private final Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);

    // ---- keys ---------------------------------------------------------------
    private static final String KEY_WORK_TIME          = "work_time";
    private static final String KEY_RELAX_TIME         = "relax_time";
    private static final String KEY_RELAX_TIME_LONG    = "relax_time_long";
    private static final String KEY_GCAL_SRC           = "gcal_src";
    private static final String KEY_GCAL_TEXT          = "gcal_text";
    private static final String KEY_GCAL_ENABLE        = "gcal_enable";
    private static final String KEY_GCAL_COPY_CLIP     = "gcal_copy_clipboard";
    private static final String KEY_ALWAYS_ON_TOP      = "always_on_top";
    private static final String KEY_WIN_X              = "window_x";
    private static final String KEY_WIN_Y              = "window_y";
    private static final String KEY_WIN_W              = "window_width";
    private static final String KEY_WIN_H              = "window_height";
    private static final String KEY_TIMER_RESTORE_DT   = "timer_restore_datetime";
    private static final String KEY_TIMER_RESTORE_MODE = "timer_restore_mode";
    private static final String KEY_SOUND_RESUME       = "sound_resume";
    private static final String KEY_SOUND_PAUSE        = "sound_pause";
    private static final String KEY_SOUND_WORK_DONE    = "sound_work_done";
    private static final String KEY_SOUND_REST_TIMEOUT = "sound_rest_timeout";
    private static final String KEY_NEON_PRESET           = "neon_preset";
    private static final String KEY_TASKBAR_ICON_ENABLE   = "taskbar_icon_enable";
    private static final String KEY_TASKBAR_FONT_SIZE     = "taskbar_font_size";
    private static final String KEY_TASKBAR_LAYOUT        = "taskbar_layout";

    private AppSettings() {}

    public static AppSettings getInstance() { return INSTANCE; }

    // ---- timer durations ----------------------------------------------------
    public int  getWorkTime()          { return prefs.getInt(KEY_WORK_TIME, 30); }
    public void setWorkTime(int v)     { prefs.putInt(KEY_WORK_TIME, v); }

    public int  getRelaxTime()         { return prefs.getInt(KEY_RELAX_TIME, 5); }
    public void setRelaxTime(int v)    { prefs.putInt(KEY_RELAX_TIME, v); }

    public int  getRelaxTimeLong()     { return prefs.getInt(KEY_RELAX_TIME_LONG, 15); }
    public void setRelaxTimeLong(int v){ prefs.putInt(KEY_RELAX_TIME_LONG, v); }

    // ---- Google Calendar ----------------------------------------------------
    public String getGCalSrc()             { return prefs.get(KEY_GCAL_SRC, ""); }
    public void   setGCalSrc(String v)     { prefs.put(KEY_GCAL_SRC, v); }

    public String getGCalText()            { return prefs.get(KEY_GCAL_TEXT, ""); }
    public void   setGCalText(String v)    { prefs.put(KEY_GCAL_TEXT, v); }

    public boolean isGCalEnable()          { return prefs.getBoolean(KEY_GCAL_ENABLE, false); }
    public void    setGCalEnable(boolean v){ prefs.putBoolean(KEY_GCAL_ENABLE, v); }

    public boolean isGCalCopyToClipboard()          { return prefs.getBoolean(KEY_GCAL_COPY_CLIP, false); }
    public void    setGCalCopyToClipboard(boolean v){ prefs.putBoolean(KEY_GCAL_COPY_CLIP, v); }

    // ---- window state -------------------------------------------------------
    public boolean isAlwaysOnTop()          { return prefs.getBoolean(KEY_ALWAYS_ON_TOP, true); }
    public void    setAlwaysOnTop(boolean v){ prefs.putBoolean(KEY_ALWAYS_ON_TOP, v); }

    public double getWindowX()         { return prefs.getDouble(KEY_WIN_X, -1); }
    public void   setWindowX(double v) { prefs.putDouble(KEY_WIN_X, v); }

    public double getWindowY()         { return prefs.getDouble(KEY_WIN_Y, -1); }
    public void   setWindowY(double v) { prefs.putDouble(KEY_WIN_Y, v); }

    public double getWindowWidth()     { return prefs.getDouble(KEY_WIN_W, 260); }
    public void   setWindowWidth(double v){ prefs.putDouble(KEY_WIN_W, v); }

    public double getWindowHeight()    { return prefs.getDouble(KEY_WIN_H, 44); }
    public void   setWindowHeight(double v){ prefs.putDouble(KEY_WIN_H, v); }

    // ---- timer restore ------------------------------------------------------
    public String getTimerRestoreDateTime()        { return prefs.get(KEY_TIMER_RESTORE_DT, ""); }
    public void   setTimerRestoreDateTime(String v){ prefs.put(KEY_TIMER_RESTORE_DT, v); }

    public int  getTimerRestoreMode()       { return prefs.getInt(KEY_TIMER_RESTORE_MODE, 0); }
    public void setTimerRestoreMode(int v)  { prefs.putInt(KEY_TIMER_RESTORE_MODE, v); }

    // ---- sound paths --------------------------------------------------------
    public String getSoundPath(SoundType type) {
        return switch (type) {
            case RESUME      -> prefs.get(KEY_SOUND_RESUME, "");
            case PAUSE       -> prefs.get(KEY_SOUND_PAUSE, "");
            case WORK_DONE   -> prefs.get(KEY_SOUND_WORK_DONE, "");
            case REST_TIMEOUT-> prefs.get(KEY_SOUND_REST_TIMEOUT, "");
        };
    }

    public void setSoundPath(SoundType type, String path) {
        switch (type) {
            case RESUME       -> prefs.put(KEY_SOUND_RESUME, path);
            case PAUSE        -> prefs.put(KEY_SOUND_PAUSE, path);
            case WORK_DONE    -> prefs.put(KEY_SOUND_WORK_DONE, path);
            case REST_TIMEOUT -> prefs.put(KEY_SOUND_REST_TIMEOUT, path);
        }
    }

    // ---- neon theme preset --------------------------------------------------
    public NeonPreset getNeonPreset() {
        return NeonPreset.fromName(prefs.get(KEY_NEON_PRESET, NeonPreset.NEON_BALANCED.name()));
    }

    public void setNeonPreset(NeonPreset preset) {
        prefs.put(KEY_NEON_PRESET, preset.name());
    }

    // ---- taskbar icon -------------------------------------------------------
    public boolean isTaskbarIconEnable()           { return prefs.getBoolean(KEY_TASKBAR_ICON_ENABLE, true); }
    public void    setTaskbarIconEnable(boolean v) { prefs.putBoolean(KEY_TASKBAR_ICON_ENABLE, v); }

    /**
     * Base font size used when rendering the taskbar icon (at the 64 px reference canvas).
     * Range [8, 28], default 17.0.  Applies to both vertical and horizontal layouts.
     */
    public double getTaskbarFontSize()         { return prefs.getDouble(KEY_TASKBAR_FONT_SIZE, 17.0); }
    public void   setTaskbarFontSize(double v) { prefs.putDouble(KEY_TASKBAR_FONT_SIZE, v); }

    /** Layout orientation for the taskbar countdown display. Defaults to {@link TaskbarTimeLayout#VERTICAL}. */
    public TaskbarTimeLayout getTaskbarLayout() {
        return TaskbarTimeLayout.fromName(prefs.get(KEY_TASKBAR_LAYOUT, TaskbarTimeLayout.VERTICAL.name()));
    }

    public void setTaskbarLayout(TaskbarTimeLayout layout) {
        prefs.put(KEY_TASKBAR_LAYOUT, layout.name());
    }

    // ---- flush --------------------------------------------------------------
    public void save() {
        try { prefs.flush(); } catch (Exception ignored) {}
    }
}


package com.tomatotimer;

import java.util.prefs.Preferences;

/**
 * Persistent application settings backed by java.util.prefs.Preferences.
 * Equivalent to TimerSettings in the WPF project.
 *
 * <h3>Changing a factory default</h3>
 * <ol>
 *   <li>Update the {@code defaultValue} argument in the getter (e.g.
 *       {@code prefs.getInt(KEY_WORK_TIME, NEW_VALUE)}).</li>
 *   <li>Increment {@link #CURRENT_SETTINGS_VERSION}.</li>
 *   <li>Add a {@code if (stored < N)} block in {@link #migrateIfNeeded()}
 *       that overwrites each changed key with its new factory value.</li>
 *   <li>Mirror the new version constant in
 *       {@code AppSettingsHelper#CURRENT_SETTINGS_VERSION} (test helper).</li>
 * </ol>
 *
 * <h3>Taskbar font sizes (v4+)</h3>
 * <p>The single {@code taskbar_font_size} key was split in schema v4 into two
 * role-specific keys:</p>
 * <ul>
 *   <li>{@code taskbar_font_size_mmss}   – used when the icon shows {@code MM:SS}; default&nbsp;23.</li>
 *   <li>{@code taskbar_font_size_hhmmss} – used when the icon shows {@code HH:MM:SS}; default&nbsp;17.</li>
 * </ul>
 * <p>On upgrade from a v3 install the old {@code taskbar_font_size} value is preserved
 * as the new {@code taskbar_font_size_mmss} value so existing user customisations are kept.</p>
 *
 * <p>Without this migration, existing users whose Windows Registry already
 * contains the old value would never see the updated default, because
 * {@link Preferences#getInt(String, int)} ignores the default argument
 * whenever the key is already present.</p>
 */
public class AppSettings {

    private static final AppSettings INSTANCE = new AppSettings();
    private final Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);

    // ---- schema version -----------------------------------------------------

    private static final String KEY_SETTINGS_VERSION = "settings_version";

    /**
     * Schema version written to the preferences store after every successful
     * migration run.  Increment this constant and add a matching migration
     * block in {@link #migrateIfNeeded()} whenever any factory default changes.
     *
     * <p>Package-private so {@code AppSettingsTest} can read and reset it
     * without reflection.</p>
     */
    static final int CURRENT_SETTINGS_VERSION = 4;

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
    private static final String KEY_NEON_GLOW_PROFILE     = "neon_glow_profile";
    private static final String KEY_TASKBAR_ICON_ENABLE      = "taskbar_icon_enable";
    /** Legacy single-font key – kept only for the v3→v4 migration read. */
    private static final String KEY_TASKBAR_FONT_SIZE        = "taskbar_font_size";
    private static final String KEY_TASKBAR_FONT_SIZE_MMSS   = "taskbar_font_size_mmss";
    private static final String KEY_TASKBAR_FONT_SIZE_HHMMSS = "taskbar_font_size_hhmmss";
    private static final String KEY_TASKBAR_LAYOUT           = "taskbar_layout";
    private static final String KEY_THEME_SELECTION_MODE     = "theme_selection_mode";

    private AppSettings() {
        migrateIfNeeded();
    }

    /**
     * Applies all pending schema migrations so that changed factory defaults
     * take effect on machines that already have a stale value in the
     * preferences store (Windows Registry).
     *
     * <p>Called once from the private constructor of this singleton.
     * Package-private so {@code AppSettingsTest} can invoke it again after
     * manually resetting {@link #KEY_SETTINGS_VERSION} to simulate a legacy
     * preferences store.</p>
     *
     * <p>Only app-behaviour settings are touched by migrations; user-specific
     * data is always preserved:</p>
     * <ul>
     *   <li>window geometry ({@code window_x/y/width/height})</li>
     *   <li>custom sound file paths</li>
     *   <li>Google Calendar source URL and event text</li>
     *   <li>timer-restore datetime and mode</li>
     * </ul>
     */
    void migrateIfNeeded() {
        final int stored = prefs.getInt(KEY_SETTINGS_VERSION, 0);
        if (stored >= CURRENT_SETTINGS_VERSION) return;

        // ── v0 → v1 : introduce versioning; stamp all factory defaults as of v1 ─
        if (stored < 1) {
            prefs.putInt    (KEY_WORK_TIME,             30);
            prefs.putInt    (KEY_RELAX_TIME,             5);
            prefs.putInt    (KEY_RELAX_TIME_LONG,       15);
            prefs.putBoolean(KEY_GCAL_ENABLE,         true);
            prefs.putBoolean(KEY_GCAL_COPY_CLIP,     false);
            prefs.putBoolean(KEY_ALWAYS_ON_TOP,       true);
            prefs.put       (KEY_NEON_PRESET,       NeonPreset.AURORA_DRIFT.name());
            prefs.put       (KEY_NEON_GLOW_PROFILE, NeonGlowProfile.BALANCED.name());
            prefs.putBoolean(KEY_TASKBAR_ICON_ENABLE, true);
            prefs.putDouble (KEY_TASKBAR_FONT_SIZE,  23.0);
            prefs.put       (KEY_TASKBAR_LAYOUT,     TaskbarTimeLayout.VERTICAL.name());
            prefs.put       (KEY_THEME_SELECTION_MODE, ThemeSelectionMode.SHUFFLE.name());
            // NOT overwriting (user-specific / runtime state):
            //   KEY_WIN_X/Y/W/H, KEY_SOUND_*, KEY_GCAL_SRC, KEY_GCAL_TEXT,
            //   KEY_TIMER_RESTORE_DT, KEY_TIMER_RESTORE_MODE
        }

        // ── v1 → v2 : work_time default 30 → 25; always_on_top default true → false ─
        if (stored < 2) {
            prefs.putInt    (KEY_WORK_TIME,        25);
            prefs.putBoolean(KEY_ALWAYS_ON_TOP, false);
        }

        // ── v2 → v3 : auto-heal users whose registry retained stale values
        //              (gcal_enable=false, taskbar disabled, font=17, etc.)
        //              while settings_version was already at 2.
        if (stored < 3) {
            prefs.putInt    (KEY_WORK_TIME,                   25);
            prefs.putBoolean(KEY_GCAL_ENABLE,               true);
            prefs.putBoolean(KEY_TASKBAR_ICON_ENABLE,       true);
            prefs.putDouble (KEY_TASKBAR_FONT_SIZE,         23.0);
            prefs.putBoolean(KEY_ALWAYS_ON_TOP,            false);
            prefs.put       (KEY_THEME_SELECTION_MODE, ThemeSelectionMode.SHUFFLE.name());
            // NOT overwriting (user-specific / runtime state):
            //   KEY_WIN_X/Y/W/H, KEY_SOUND_*, KEY_GCAL_SRC, KEY_GCAL_TEXT,
            //   KEY_TIMER_RESTORE_DT, KEY_TIMER_RESTORE_MODE
        }

        // ── v3 → v4 : split single taskbar_font_size into two role-specific keys.
        //              Preserve the existing taskbar_font_size value (user may have customised it)
        //              as the MM:SS font size so the change is backward-compatible.
        //              The HH:MM:SS key always gets the new factory default (17.0).
        if (stored < 4) {
            final double legacyFont = prefs.getDouble(KEY_TASKBAR_FONT_SIZE, 23.0);
            prefs.putDouble(KEY_TASKBAR_FONT_SIZE_MMSS,   legacyFont);
            prefs.putDouble(KEY_TASKBAR_FONT_SIZE_HHMMSS, 17.0);
        }

        prefs.putInt(KEY_SETTINGS_VERSION, CURRENT_SETTINGS_VERSION);
        save();
    }

    public static AppSettings getInstance() { return INSTANCE; }

    // ---- timer durations ----------------------------------------------------
    public int  getWorkTime()          { return prefs.getInt(KEY_WORK_TIME, 25); }
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

    public boolean isGCalEnable()          { return prefs.getBoolean(KEY_GCAL_ENABLE, true); }
    public void    setGCalEnable(boolean v){ prefs.putBoolean(KEY_GCAL_ENABLE, v); }

    public boolean isGCalCopyToClipboard()          { return prefs.getBoolean(KEY_GCAL_COPY_CLIP, false); }
    public void    setGCalCopyToClipboard(boolean v){ prefs.putBoolean(KEY_GCAL_COPY_CLIP, v); }

    // ---- window state -------------------------------------------------------
    public boolean isAlwaysOnTop()          { return prefs.getBoolean(KEY_ALWAYS_ON_TOP, false); }
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
        return NeonPreset.fromName(prefs.get(KEY_NEON_PRESET, NeonPreset.AURORA_DRIFT.name()));
    }

    public void setNeonPreset(NeonPreset preset) {
        prefs.put(KEY_NEON_PRESET, preset.name());
    }

    // ---- neon glow profile --------------------------------------------------
    /**
     * Returns the active {@link NeonGlowProfile}, defaulting to {@link NeonGlowProfile#BALANCED}
     * when no value has been saved yet.
     */
    public NeonGlowProfile getNeonGlowProfile() {
        return NeonGlowProfile.fromName(prefs.get(KEY_NEON_GLOW_PROFILE, NeonGlowProfile.BALANCED.name()));
    }

    public void setNeonGlowProfile(NeonGlowProfile profile) {
        prefs.put(KEY_NEON_GLOW_PROFILE, profile.name());
    }

    // ---- taskbar icon -------------------------------------------------------
    public boolean isTaskbarIconEnable()           { return prefs.getBoolean(KEY_TASKBAR_ICON_ENABLE, true); }
    public void    setTaskbarIconEnable(boolean v) { prefs.putBoolean(KEY_TASKBAR_ICON_ENABLE, v); }

    /**
     * Base font size used when the taskbar icon shows {@code MM:SS} (hours == 0).
     * Range [8, 28], default 23.0.
     */
    public double getTaskbarFontSizeMmss()         { return prefs.getDouble(KEY_TASKBAR_FONT_SIZE_MMSS, 23.0); }
    public void   setTaskbarFontSizeMmss(double v) { prefs.putDouble(KEY_TASKBAR_FONT_SIZE_MMSS, v); }

    /**
     * Base font size used when the taskbar icon shows {@code HH:MM:SS} (hours &gt; 0).
     * Range [8, 28], default 17.0.
     */
    public double getTaskbarFontSizeHhmmss()         { return prefs.getDouble(KEY_TASKBAR_FONT_SIZE_HHMMSS, 17.0); }
    public void   setTaskbarFontSizeHhmmss(double v) { prefs.putDouble(KEY_TASKBAR_FONT_SIZE_HHMMSS, v); }

    /** Layout orientation for the taskbar countdown display. Defaults to {@link TaskbarTimeLayout#VERTICAL}. */
    public TaskbarTimeLayout getTaskbarLayout() {
        return TaskbarTimeLayout.fromName(prefs.get(KEY_TASKBAR_LAYOUT, TaskbarTimeLayout.VERTICAL.name()));
    }

    public void setTaskbarLayout(TaskbarTimeLayout layout) {
        prefs.put(KEY_TASKBAR_LAYOUT, layout.name());
    }

    // ---- theme selection mode -----------------------------------------------

    /**
     * Returns the active {@link ThemeSelectionMode}, defaulting to {@link ThemeSelectionMode#SHUFFLE}
     * when no value has been saved yet.
     */
    public ThemeSelectionMode getThemeSelectionMode() {
        return ThemeSelectionMode.fromName(
                prefs.get(KEY_THEME_SELECTION_MODE, ThemeSelectionMode.SHUFFLE.name()));
    }

    public void setThemeSelectionMode(ThemeSelectionMode mode) {
        prefs.put(KEY_THEME_SELECTION_MODE, mode.name());
    }

    // ---- flush --------------------------------------------------------------
    public void save() {
        try { prefs.flush(); } catch (Exception ignored) {}
    }
}


package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppSettings")
class AppSettingsTest extends AppSettingsHelper {

    // ── singleton ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getInstance() returns a non-null instance")
    void testGetInstanceReturnsNonNull() {
        assertNotNull(AppSettings.getInstance());
    }

    @Test
    @DisplayName("getInstance() always returns the same object")
    void testGetInstanceReturnsSameInstance() {
        assertSame(AppSettings.getInstance(), AppSettings.getInstance());
    }

    // ── work time ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("setWorkTime / getWorkTime round-trip")
    void testWorkTimeRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getWorkTime();
        try {
            s.setWorkTime(TEST_WORK_TIME);
            assertEquals(TEST_WORK_TIME, s.getWorkTime());
        } finally {
            s.setWorkTime(original);
        }
    }

    // ── relax time ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("setRelaxTime / getRelaxTime round-trip")
    void testRelaxTimeRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getRelaxTime();
        try {
            s.setRelaxTime(TEST_RELAX_TIME);
            assertEquals(TEST_RELAX_TIME, s.getRelaxTime());
        } finally {
            s.setRelaxTime(original);
        }
    }

    @Test
    @DisplayName("setRelaxTimeLong / getRelaxTimeLong round-trip")
    void testRelaxTimeLongRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getRelaxTimeLong();
        try {
            s.setRelaxTimeLong(TEST_RELAX_TIME_LONG);
            assertEquals(TEST_RELAX_TIME_LONG, s.getRelaxTimeLong());
        } finally {
            s.setRelaxTimeLong(original);
        }
    }

    // ── Google Calendar ───────────────────────────────────────────────────────

    @Test
    @DisplayName("setGCalSrc / getGCalSrc round-trip")
    void testGCalSrcRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getGCalSrc();
        try {
            s.setGCalSrc(TEST_GCAL_SRC);
            assertEquals(TEST_GCAL_SRC, s.getGCalSrc());
        } finally {
            s.setGCalSrc(original);
        }
    }

    @Test
    @DisplayName("setGCalText / getGCalText round-trip")
    void testGCalTextRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getGCalText();
        try {
            s.setGCalText(TEST_GCAL_TEXT);
            assertEquals(TEST_GCAL_TEXT, s.getGCalText());
        } finally {
            s.setGCalText(original);
        }
    }

    @Test
    @DisplayName("setGCalEnable / isGCalEnable round-trip for true and false")
    void testGCalEnableRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.isGCalEnable();
        try {
            s.setGCalEnable(true);
            assertEquals(true, s.isGCalEnable());
            s.setGCalEnable(false);
            assertEquals(false, s.isGCalEnable());
        } finally {
            s.setGCalEnable(original);
        }
    }

    @Test
    @DisplayName("setGCalCopyToClipboard / isGCalCopyToClipboard round-trip")
    void testGCalCopyToClipboardRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.isGCalCopyToClipboard();
        try {
            s.setGCalCopyToClipboard(true);
            assertEquals(true, s.isGCalCopyToClipboard());
        } finally {
            s.setGCalCopyToClipboard(original);
        }
    }

    // ── window state ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("setAlwaysOnTop / isAlwaysOnTop round-trip")
    void testAlwaysOnTopRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.isAlwaysOnTop();
        try {
            s.setAlwaysOnTop(false);
            assertEquals(false, s.isAlwaysOnTop());
        } finally {
            s.setAlwaysOnTop(original);
        }
    }

    @Test
    @DisplayName("setWindowX / getWindowX round-trip")
    void testWindowXRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getWindowX();
        try {
            s.setWindowX(TEST_WIN_X);
            assertEquals(TEST_WIN_X, s.getWindowX(), DELTA);
        } finally {
            s.setWindowX(original);
        }
    }

    @Test
    @DisplayName("setWindowY / getWindowY round-trip")
    void testWindowYRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getWindowY();
        try {
            s.setWindowY(TEST_WIN_Y);
            assertEquals(TEST_WIN_Y, s.getWindowY(), DELTA);
        } finally {
            s.setWindowY(original);
        }
    }

    @Test
    @DisplayName("setWindowWidth / getWindowWidth round-trip")
    void testWindowWidthRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getWindowWidth();
        try {
            s.setWindowWidth(TEST_WIN_W);
            assertEquals(TEST_WIN_W, s.getWindowWidth(), DELTA);
        } finally {
            s.setWindowWidth(original);
        }
    }

    @Test
    @DisplayName("setWindowHeight / getWindowHeight round-trip")
    void testWindowHeightRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getWindowHeight();
        try {
            s.setWindowHeight(TEST_WIN_H);
            assertEquals(TEST_WIN_H, s.getWindowHeight(), DELTA);
        } finally {
            s.setWindowHeight(original);
        }
    }

    // ── timer restore ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("setTimerRestoreDateTime / getTimerRestoreDateTime round-trip")
    void testTimerRestoreDateTimeRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getTimerRestoreDateTime();
        try {
            s.setTimerRestoreDateTime(TEST_RESTORE_DT);
            assertEquals(TEST_RESTORE_DT, s.getTimerRestoreDateTime());
        } finally {
            s.setTimerRestoreDateTime(original);
        }
    }

    @Test
    @DisplayName("setTimerRestoreMode / getTimerRestoreMode round-trip")
    void testTimerRestoreModeRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getTimerRestoreMode();
        try {
            s.setTimerRestoreMode(TEST_RESTORE_MODE);
            assertEquals(TEST_RESTORE_MODE, s.getTimerRestoreMode());
        } finally {
            s.setTimerRestoreMode(original);
        }
    }

    // ── sound paths ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("setSoundPath / getSoundPath round-trip for every SoundType")
    void testSoundPathRoundTripForAllTypes() {
        final var s = AppSettings.getInstance();
        for (final var type : SoundType.values()) {
            final var original = s.getSoundPath(type);
            try {
                s.setSoundPath(type, TEST_SOUND_PATH);
                assertEquals(TEST_SOUND_PATH, s.getSoundPath(type));
            } finally {
                s.setSoundPath(type, original);
            }
        }
    }

    // ── neon preset ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("setNeonPreset / getNeonPreset round-trip for every preset")
    void testNeonPresetRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getNeonPreset();
        try {
            for (final var preset : NeonPreset.values()) {
                s.setNeonPreset(preset);
                assertEquals(preset, s.getNeonPreset());
            }
        } finally {
            s.setNeonPreset(original);
        }
    }

    // ── neon glow profile ─────────────────────────────────────────────────────

    @Test
    @DisplayName("setNeonGlowProfile / getNeonGlowProfile round-trip for every profile")
    void testNeonGlowProfileRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getNeonGlowProfile();
        try {
            for (final var profile : NeonGlowProfile.values()) {
                s.setNeonGlowProfile(profile);
                assertEquals(profile, s.getNeonGlowProfile());
            }
        } finally {
            s.setNeonGlowProfile(original);
        }
    }

    // ── taskbar icon ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("setTaskbarIconEnable / isTaskbarIconEnable round-trip")
    void testTaskbarIconEnableRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.isTaskbarIconEnable();
        try {
            s.setTaskbarIconEnable(false);
            assertEquals(false, s.isTaskbarIconEnable());
        } finally {
            s.setTaskbarIconEnable(original);
        }
    }

    @Test
    @DisplayName("setTaskbarFontSize / getTaskbarFontSize round-trip")
    void testTaskbarFontSizeRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getTaskbarFontSize();
        try {
            s.setTaskbarFontSize(TEST_TASKBAR_FONT_SZ);
            assertEquals(TEST_TASKBAR_FONT_SZ, s.getTaskbarFontSize(), DELTA);
        } finally {
            s.setTaskbarFontSize(original);
        }
    }

    @Test
    @DisplayName("setTaskbarLayout / getTaskbarLayout round-trip for every layout")
    void testTaskbarLayoutRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getTaskbarLayout();
        try {
            for (final var layout : TaskbarTimeLayout.values()) {
                s.setTaskbarLayout(layout);
                assertEquals(layout, s.getTaskbarLayout());
            }
        } finally {
            s.setTaskbarLayout(original);
        }
    }

    // ── theme selection mode ──────────────────────────────────────────────────

    @Test
    @DisplayName("setThemeSelectionMode / getThemeSelectionMode round-trip for every mode")
    void testThemeSelectionModeRoundTrip() {
        final var s        = AppSettings.getInstance();
        final var original = s.getThemeSelectionMode();
        try {
            for (final var mode : ThemeSelectionMode.values()) {
                s.setThemeSelectionMode(mode);
                assertEquals(mode, s.getThemeSelectionMode());
            }
        } finally {
            s.setThemeSelectionMode(original);
        }
    }

    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save() does not throw any exception")
    void testSaveDoesNotThrow() {
        assertDoesNotThrow(() -> AppSettings.getInstance().save());
    }

    // ── settings migration ────────────────────────────────────────────────────
    //
    // These tests verify that migrateIfNeeded() correctly stamps factory
    // defaults over stale registry values so that changed defaults in
    // AppSettings.java also take effect for users upgrading from an older build.
    //
    // Pattern: save → corrupt → migrate → assert → restore (in finally).
    // migrateIfNeeded() is package-private so same-package tests can call it
    // directly after manually resetting the stored schema version to 0.

    /** Restores a key to its saved state (removes it again if it was absent). */
    private static void restore(java.util.prefs.Preferences p, String key, String saved) {
        if (saved != null) p.put(key, saved); else p.remove(key);
    }

    @Test
    @DisplayName("migrateIfNeeded() stamps timer-duration defaults when settings_version is 0")
    void testMigrationFromV0StampsTimerDefaults() {
        final var prefs      = java.util.prefs.Preferences.userNodeForPackage(AppSettings.class);
        final var s          = AppSettings.getInstance();
        final var savedVer   = prefs.get("settings_version", null);
        final var savedWork  = prefs.get("work_time",       null);
        final var savedRelax = prefs.get("relax_time",      null);
        final var savedLong  = prefs.get("relax_time_long", null);
        try {
            // Simulate stale registry from an older build
            prefs.remove("settings_version");
            prefs.putInt("work_time",       99);
            prefs.putInt("relax_time",       2);
            prefs.putInt("relax_time_long",  3);

            s.migrateIfNeeded();

            assertEquals(DEFAULT_WORK_TIME,       s.getWorkTime(),       "work_time after migration");
            assertEquals(DEFAULT_RELAX_TIME,      s.getRelaxTime(),      "relax_time after migration");
            assertEquals(DEFAULT_RELAX_TIME_LONG, s.getRelaxTimeLong(),  "relax_time_long after migration");
        } finally {
            restore(prefs, "settings_version",  savedVer);
            restore(prefs, "work_time",         savedWork);
            restore(prefs, "relax_time",        savedRelax);
            restore(prefs, "relax_time_long",   savedLong);
        }
    }

    @Test
    @DisplayName("migrateIfNeeded() stamps feature-flag defaults when settings_version is 0")
    void testMigrationFromV0StampsFeatureFlagDefaults() {
        final var prefs        = java.util.prefs.Preferences.userNodeForPackage(AppSettings.class);
        final var s            = AppSettings.getInstance();
        final var savedVer     = prefs.get("settings_version",      null);
        final var savedGcal    = prefs.get("gcal_enable",           null);
        final var savedClip    = prefs.get("gcal_copy_clipboard",   null);
        final var savedOnTop   = prefs.get("always_on_top",         null);
        final var savedTbIcon  = prefs.get("taskbar_icon_enable",   null);
        try {
            prefs.remove("settings_version");
            prefs.putBoolean("gcal_enable",         !DEFAULT_GCAL_ENABLE);
            prefs.putBoolean("gcal_copy_clipboard", !DEFAULT_GCAL_COPY_CLIP);
            prefs.putBoolean("always_on_top",       !DEFAULT_ALWAYS_ON_TOP);
            prefs.putBoolean("taskbar_icon_enable", !DEFAULT_TASKBAR_ICON_ENABLE);

            s.migrateIfNeeded();

            assertEquals(DEFAULT_GCAL_ENABLE,         s.isGCalEnable(),         "gcal_enable after migration");
            assertEquals(DEFAULT_GCAL_COPY_CLIP,      s.isGCalCopyToClipboard(),"gcal_copy_clipboard after migration");
            assertEquals(DEFAULT_ALWAYS_ON_TOP,       s.isAlwaysOnTop(),        "always_on_top after migration");
            assertEquals(DEFAULT_TASKBAR_ICON_ENABLE, s.isTaskbarIconEnable(),  "taskbar_icon_enable after migration");
        } finally {
            restore(prefs, "settings_version",    savedVer);
            restore(prefs, "gcal_enable",         savedGcal);
            restore(prefs, "gcal_copy_clipboard", savedClip);
            restore(prefs, "always_on_top",       savedOnTop);
            restore(prefs, "taskbar_icon_enable", savedTbIcon);
        }
    }

    @Test
    @DisplayName("migrateIfNeeded() stamps visual and layout defaults when settings_version is 0")
    void testMigrationFromV0StampsVisualDefaults() {
        final var prefs          = java.util.prefs.Preferences.userNodeForPackage(AppSettings.class);
        final var s              = AppSettings.getInstance();
        final var savedVer       = prefs.get("settings_version",      null);
        final var savedPreset    = prefs.get("neon_preset",           null);
        final var savedGlow      = prefs.get("neon_glow_profile",     null);
        final var savedTbLayout  = prefs.get("taskbar_layout",        null);
        final var savedTbFont    = prefs.get("taskbar_font_size",     null);
        final var savedTheme     = prefs.get("theme_selection_mode",  null);
        try {
            prefs.remove("settings_version");
            // Use values that differ from each factory default
            prefs.put   ("neon_preset",          NeonPreset.SCARLET_SURGE.name());
            prefs.put   ("neon_glow_profile",    NeonGlowProfile.SOFT.name());
            prefs.put   ("taskbar_layout",       TaskbarTimeLayout.HORIZONTAL.name());
            prefs.putDouble("taskbar_font_size", 10.0);
            prefs.put   ("theme_selection_mode", ThemeSelectionMode.STATIC.name());

            s.migrateIfNeeded();

            assertEquals(DEFAULT_NEON_PRESET,          s.getNeonPreset(),          "neon_preset after migration");
            assertEquals(DEFAULT_NEON_GLOW_PROFILE,    s.getNeonGlowProfile(),     "neon_glow_profile after migration");
            assertEquals(DEFAULT_TASKBAR_LAYOUT,       s.getTaskbarLayout(),       "taskbar_layout after migration");
            assertEquals(DEFAULT_TASKBAR_FONT_SIZE,    s.getTaskbarFontSize(), DELTA);
            assertEquals(DEFAULT_THEME_SELECTION_MODE, s.getThemeSelectionMode(),  "theme_selection_mode after migration");
        } finally {
            restore(prefs, "settings_version",     savedVer);
            restore(prefs, "neon_preset",          savedPreset);
            restore(prefs, "neon_glow_profile",    savedGlow);
            restore(prefs, "taskbar_layout",       savedTbLayout);
            restore(prefs, "taskbar_font_size",    savedTbFont);
            restore(prefs, "theme_selection_mode", savedTheme);
        }
    }

    @Test
    @DisplayName("migrateIfNeeded() is a no-op when settings_version equals CURRENT_SETTINGS_VERSION")
    void testMigrationIsNoOpWhenVersionIsCurrent() {
        final var prefs    = java.util.prefs.Preferences.userNodeForPackage(AppSettings.class);
        final var s        = AppSettings.getInstance();
        final var savedVer  = prefs.get("settings_version", null);
        final var savedWork = prefs.get("work_time",        null);
        try {
            prefs.putInt("settings_version", AppSettings.CURRENT_SETTINGS_VERSION);
            prefs.putInt("work_time", TEST_WORK_TIME);  // value that differs from factory default

            s.migrateIfNeeded();

            // Migration must NOT overwrite – user's (non-default) value is preserved
            assertEquals(TEST_WORK_TIME, s.getWorkTime(),
                    "work_time must not be overwritten when version is already current");
        } finally {
            restore(prefs, "settings_version", savedVer);
            restore(prefs, "work_time",        savedWork);
        }
    }

    @Test
    @DisplayName("migrateIfNeeded() writes CURRENT_SETTINGS_VERSION to preferences store")
    void testMigrationWritesSettingsVersionToCurrent() {
        final var prefs    = java.util.prefs.Preferences.userNodeForPackage(AppSettings.class);
        final var s        = AppSettings.getInstance();
        final var savedVer = prefs.get("settings_version", null);
        try {
            prefs.remove("settings_version");  // simulate pre-versioning install

            s.migrateIfNeeded();

            assertEquals(AppSettings.CURRENT_SETTINGS_VERSION,
                    prefs.getInt("settings_version", -1),
                    "settings_version must be stamped with CURRENT_SETTINGS_VERSION after migration");
        } finally {
            restore(prefs, "settings_version", savedVer);
        }
    }

    @Test
    @DisplayName("migrateIfNeeded() stamps work_time=25 and always_on_top=false when settings_version is 1")
    void testMigrationFromV1StampsNewDefaults() {
        final var prefs       = java.util.prefs.Preferences.userNodeForPackage(AppSettings.class);
        final var s           = AppSettings.getInstance();
        final var savedVer    = prefs.get("settings_version", null);
        final var savedWork   = prefs.get("work_time",        null);
        final var savedOnTop  = prefs.get("always_on_top",   null);
        try {
            // Simulate a v1 install (old defaults already written, but v2 not yet applied)
            prefs.putInt    ("settings_version", 1);
            prefs.putInt    ("work_time",       30);   // old v1 default
            prefs.putBoolean("always_on_top", true);   // old v1 default

            s.migrateIfNeeded();

            assertEquals(DEFAULT_WORK_TIME,   s.getWorkTime(),    "work_time after v1→v2 migration");
            assertEquals(DEFAULT_ALWAYS_ON_TOP, s.isAlwaysOnTop(), "always_on_top after v1→v2 migration");
        } finally {
            restore(prefs, "settings_version", savedVer);
            restore(prefs, "work_time",        savedWork);
            restore(prefs, "always_on_top",    savedOnTop);
        }
    }

    // ── first-run defaults ────────────────────────────────────────────────────

    @Test
    @DisplayName("isGCalEnable() default is true on first run")
    void testGCalEnableDefaultIsTrue() {
        final var prefs = Preferences.userNodeForPackage(AppSettings.class);
        final var saved = prefs.get("gcal_enable", null);
        try {
            prefs.remove("gcal_enable");
            assertEquals(DEFAULT_GCAL_ENABLE, AppSettings.getInstance().isGCalEnable());
        } finally {
            if (saved != null) prefs.put("gcal_enable", saved);
        }
    }

    @Test
    @DisplayName("isAlwaysOnTop() default is false on first run")
    void testAlwaysOnTopDefaultIsFalse() {
        final var prefs = Preferences.userNodeForPackage(AppSettings.class);
        final var saved = prefs.get("always_on_top", null);
        try {
            prefs.remove("always_on_top");
            assertEquals(DEFAULT_ALWAYS_ON_TOP, AppSettings.getInstance().isAlwaysOnTop());
        } finally {
            if (saved != null) prefs.put("always_on_top", saved);
        }
    }

    @Test
    @DisplayName("getWorkTime() default is 25 on first run")
    void testWorkTimeDefaultIs25() {
        final var prefs = Preferences.userNodeForPackage(AppSettings.class);
        final var saved = prefs.get("work_time", null);
        try {
            prefs.remove("work_time");
            assertEquals(DEFAULT_WORK_TIME, AppSettings.getInstance().getWorkTime());
        } finally {
            if (saved != null) prefs.put("work_time", saved);
        }
    }

    @Test
    @DisplayName("isTaskbarIconEnable() default is true on first run")
    void testTaskbarIconEnableDefaultIsTrue() {
        final var prefs = Preferences.userNodeForPackage(AppSettings.class);
        final var saved = prefs.get("taskbar_icon_enable", null);
        try {
            prefs.remove("taskbar_icon_enable");
            assertEquals(DEFAULT_TASKBAR_ICON_ENABLE, AppSettings.getInstance().isTaskbarIconEnable());
        } finally {
            if (saved != null) prefs.put("taskbar_icon_enable", saved);
        }
    }

    @Test
    @DisplayName("getTaskbarFontSize() default is 23.0 on first run")
    void testTaskbarFontSizeDefaultIs23() {
        final var prefs = Preferences.userNodeForPackage(AppSettings.class);
        final var saved = prefs.get("taskbar_font_size", null);
        try {
            prefs.remove("taskbar_font_size");
            assertEquals(DEFAULT_TASKBAR_FONT_SIZE, AppSettings.getInstance().getTaskbarFontSize(), DELTA);
        } finally {
            if (saved != null) prefs.put("taskbar_font_size", saved);
        }
    }

    @Test
    @DisplayName("getThemeSelectionMode() default is SHUFFLE on first run")
    void testThemeSelectionModeDefaultIsShuffle() {
        final var prefs = Preferences.userNodeForPackage(AppSettings.class);
        final var saved = prefs.get("theme_selection_mode", null);
        try {
            prefs.remove("theme_selection_mode");
            assertEquals(DEFAULT_THEME_SELECTION_MODE, AppSettings.getInstance().getThemeSelectionMode());
        } finally {
            if (saved != null) prefs.put("theme_selection_mode", saved);
        }
    }
}


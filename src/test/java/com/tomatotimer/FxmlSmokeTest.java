package com.tomatotimer;

import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke tests for FXML resources.
 *
 * <h3>Tier 1 – resource URL tests (no JavaFX toolkit required)</h3>
 * <p>Verify that every FXML file is present on the classpath.  These always
 * run, even in headless CI environments.</p>
 *
 * <h3>Tier 2 – FXMLLoader integration tests (JavaFX toolkit required)</h3>
 * <p>Verify that FXMLLoader can successfully parse and instantiate the two
 * most frequently used views ({@code buttons.fxml} and {@code settings.fxml}).
 * These tests are skipped automatically when the toolkit is unavailable.</p>
 */
@DisplayName("FXML resources – smoke tests")
class FxmlSmokeTest {

    // ── resource base (same package as App) ───────────────────────────────────
    private static final Class<?> BASE = App.class;

    // =========================================================================
    //  Tier 1: classpath URL accessibility (toolkit NOT required)
    // =========================================================================

    @Test
    @DisplayName("main.fxml is present on the classpath")
    void testMainFxmlUrl() {
        assertNotNull(BASE.getResource("main.fxml"),
                "main.fxml not found on classpath");
    }

    @Test
    @DisplayName("buttons.fxml is present on the classpath")
    void testButtonsFxmlUrl() {
        assertNotNull(BASE.getResource("buttons.fxml"),
                "buttons.fxml not found on classpath");
    }

    @Test
    @DisplayName("settings.fxml is present on the classpath")
    void testSettingsFxmlUrl() {
        assertNotNull(BASE.getResource("settings.fxml"),
                "settings.fxml not found on classpath");
    }

    @Test
    @DisplayName("sound_settings.fxml is present on the classpath")
    void testSoundSettingsFxmlUrl() {
        assertNotNull(BASE.getResource("sound_settings.fxml"),
                "sound_settings.fxml not found on classpath");
    }

    @Test
    @DisplayName("taskbar_settings.fxml is present on the classpath")
    void testTaskbarSettingsFxmlUrl() {
        assertNotNull(BASE.getResource("taskbar_settings.fxml"),
                "taskbar_settings.fxml not found on classpath");
    }

    @Test
    @DisplayName("calendar_settings.fxml is present on the classpath")
    void testCalendarSettingsFxmlUrl() {
        assertNotNull(BASE.getResource("calendar_settings.fxml"),
                "calendar_settings.fxml not found on classpath");
    }

    @Test
    @DisplayName("theme_settings.fxml is present on the classpath")
    void testThemeSettingsFxmlUrl() {
        assertNotNull(BASE.getResource("theme_settings.fxml"),
                "theme_settings.fxml not found on classpath");
    }

    // =========================================================================
    //  Tier 2: FXMLLoader integration (JavaFX toolkit required)
    // =========================================================================

    /**
     * Loads {@code buttons.fxml} (the main timer face) and verifies that the
     * root node and its controller were created successfully.
     *
     * <p>The controller's {@code @FXML initialize()} method is called
     * automatically by {@link FXMLLoader}, which exercises the SVG icon
     * creation path ({@link IconFactory#create}) and FXML field injection.</p>
     */
    @Test
    @DisplayName("buttons.fxml loads without exception and returns a non-null root node")
    void testButtonsFxmlLoads() throws Exception {
        Assumptions.assumeTrue(JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – buttons.fxml load test skipped");

        final URL url = BASE.getResource("buttons.fxml");
        assertNotNull(url);

        final Object root = JavaFxTestHelper.runOnFxThread(() -> {
            final FXMLLoader loader = new FXMLLoader(url);
            return loader.load();
        });

        assertNotNull(root, "FXMLLoader.load() must return a non-null root node");
    }

    /**
     * Loads {@code settings.fxml} and verifies the root node and its
     * controller were created successfully.
     */
    @Test
    @DisplayName("settings.fxml loads without exception and returns a non-null root node")
    void testSettingsFxmlLoads() throws Exception {
        Assumptions.assumeTrue(JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – settings.fxml load test skipped");

        final URL url = BASE.getResource("settings.fxml");
        assertNotNull(url);

        final Object root = JavaFxTestHelper.runOnFxThread(() -> {
            final FXMLLoader loader = new FXMLLoader(url);
            return loader.load();
        });

        assertNotNull(root, "FXMLLoader.load() must return a non-null root node");
    }

    /**
     * Loads {@code sound_settings.fxml} and verifies the root node and its
     * controller ({@link com.tomatotimer.controller.SoundSettingsController}) were
     * created successfully.
     */
    @Test
    @DisplayName("sound_settings.fxml loads without exception and returns a non-null root node")
    void testSoundSettingsFxmlLoads() throws Exception {
        Assumptions.assumeTrue(JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – sound_settings.fxml load test skipped");

        final URL url = BASE.getResource("sound_settings.fxml");
        assertNotNull(url);

        final Object root = JavaFxTestHelper.runOnFxThread(() -> {
            final FXMLLoader loader = new FXMLLoader(url);
            return loader.load();
        });

        assertNotNull(root, "FXMLLoader.load() must return a non-null root node");
    }

    /**
     * Loads {@code taskbar_settings.fxml} and verifies the root node and its
     * controller ({@link com.tomatotimer.controller.TaskbarSettingsController}) were
     * created successfully.
     */
    @Test
    @DisplayName("taskbar_settings.fxml loads without exception and returns a non-null root node")
    void testTaskbarSettingsFxmlLoads() throws Exception {
        Assumptions.assumeTrue(JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – taskbar_settings.fxml load test skipped");

        final URL url = BASE.getResource("taskbar_settings.fxml");
        assertNotNull(url);

        final Object root = JavaFxTestHelper.runOnFxThread(() -> {
            final FXMLLoader loader = new FXMLLoader(url);
            return loader.load();
        });

        assertNotNull(root, "FXMLLoader.load() must return a non-null root node");
    }

    /**
     * Loads {@code calendar_settings.fxml} and verifies the root node and its
     * controller ({@link com.tomatotimer.controller.CalendarSettingsController}) were
     * created successfully.
     */
    @Test
    @DisplayName("calendar_settings.fxml loads without exception and returns a non-null root node")
    void testCalendarSettingsFxmlLoads() throws Exception {
        Assumptions.assumeTrue(JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – calendar_settings.fxml load test skipped");

        final URL url = BASE.getResource("calendar_settings.fxml");
        assertNotNull(url);

        final Object root = JavaFxTestHelper.runOnFxThread(() -> {
            final FXMLLoader loader = new FXMLLoader(url);
            return loader.load();
        });

        assertNotNull(root, "FXMLLoader.load() must return a non-null root node");
    }

    /**
     * Loads {@code theme_settings.fxml} and verifies the root node and its
     * controller ({@link com.tomatotimer.controller.ThemeSettingsController}) were
     * created successfully.
     */
    @Test
    @DisplayName("theme_settings.fxml loads without exception and returns a non-null root node")
    void testThemeSettingsFxmlLoads() throws Exception {
        Assumptions.assumeTrue(JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – theme_settings.fxml load test skipped");

        final URL url = BASE.getResource("theme_settings.fxml");
        assertNotNull(url);

        final Object root = JavaFxTestHelper.runOnFxThread(() -> {
            final FXMLLoader loader = new FXMLLoader(url);
            return loader.load();
        });

        assertNotNull(root, "FXMLLoader.load() must return a non-null root node");
    }
}


package com.tomatotimer;

import com.tomatotimer.controller.MainController;
import com.tomatotimer.controller.ThemeSettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TestFX-based UI integration tests for {@code theme_settings.fxml} /
 * {@link ThemeSettingsController}.
 *
 * <p>All tests are guarded by {@link JavaFxTestHelper#ensureToolkitStarted()} and
 * skipped automatically in headless / CI environments.</p>
 *
 * <h3>Coverage targets</h3>
 * <ul>
 *   <li>Structural node presence.</li>
 *   <li>ComboBox population: all enum values present for preset, glow profile, and selection mode.</li>
 *   <li>{@link ThemeSettingsController#syncFromSettings()} updates all three combos.</li>
 *   <li>{@link ThemeSettingsController#syncPresetComboBox()} updates only the preset combo.</li>
 *   <li>Changing cbNeonPreset persists to {@link AppSettings} and triggers {@link MainController#updateUI()}.</li>
 *   <li>Changing cbNeonGlowProfile persists to {@link AppSettings}.</li>
 *   <li>Back button calls {@link MainController#showSettings()}.</li>
 * </ul>
 */
@DisplayName("ThemeSettingsView – TestFX UI integration tests")
class ThemeSettingsViewUiTest {

    private Stage                   stage;
    private ThemeSettingsController controller;

    // ── JavaFX toolkit guard ─────────────────────────────────────────────────

    @BeforeAll
    static void requireFxToolkit() {
        Assumptions.assumeTrue(
                JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – ThemeSettingsView UI tests skipped");
    }

    // ── Per-test fixture setup ───────────────────────────────────────────────

    @BeforeEach
    void setUp() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            final var loader = new FXMLLoader(App.class.getResource("theme_settings.fxml"));
            final HBox root = loader.load();
            controller = loader.getController();
            stage = new Stage();
            stage.setScene(new Scene(root, 520, 44));
            stage.show();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void tearDown() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            stage.hide();
            return null;
        });
    }

    // =========================================================================
    //  Test 1 – Root node type
    // =========================================================================

    @Test
    @DisplayName("theme_settings.fxml root node is an HBox")
    void rootIsHBox() {
        assertInstanceOf(HBox.class, stage.getScene().getRoot(),
                "The root node in theme_settings.fxml must be an HBox");
    }

    // =========================================================================
    //  Test 2 – Key nodes present
    // =========================================================================

    @Test
    @DisplayName("Key nodes are present (btnBack, cbNeonPreset, cbNeonGlowProfile, cbThemeSelectionMode)")
    void keyNodesArePresent() {
        final var root = stage.getScene().getRoot();
        assertNotNull(root.lookup("#btnBack"),              "#btnBack must be present");
        assertNotNull(root.lookup("#cbNeonPreset"),         "#cbNeonPreset must be present");
        assertNotNull(root.lookup("#cbNeonGlowProfile"),    "#cbNeonGlowProfile must be present");
        assertNotNull(root.lookup("#cbThemeSelectionMode"), "#cbThemeSelectionMode must be present");
    }

    // =========================================================================
    //  Test 3 – cbNeonPreset populated with all NeonPreset values
    // =========================================================================

    @Test
    @DisplayName("cbNeonPreset contains all NeonPreset enum values")
    @SuppressWarnings("unchecked")
    void neonPresetComboIsFullyPopulated() {
        final var cb = (ComboBox<NeonPreset>) stage.getScene().getRoot().lookup("#cbNeonPreset");
        assertNotNull(cb);
        assertEquals(NeonPreset.values().length, cb.getItems().size(),
                "cbNeonPreset must contain exactly " + NeonPreset.values().length + " items");
    }

    // =========================================================================
    //  Test 4 – cbNeonGlowProfile populated with all NeonGlowProfile values
    // =========================================================================

    @Test
    @DisplayName("cbNeonGlowProfile contains all NeonGlowProfile enum values")
    @SuppressWarnings("unchecked")
    void neonGlowComboIsFullyPopulated() {
        final var cb = (ComboBox<NeonGlowProfile>) stage.getScene().getRoot().lookup("#cbNeonGlowProfile");
        assertNotNull(cb);
        assertEquals(NeonGlowProfile.values().length, cb.getItems().size(),
                "cbNeonGlowProfile must contain exactly " + NeonGlowProfile.values().length + " items");
    }

    // =========================================================================
    //  Test 5 – cbThemeSelectionMode populated with all ThemeSelectionMode values
    // =========================================================================

    @Test
    @DisplayName("cbThemeSelectionMode contains all ThemeSelectionMode enum values")
    @SuppressWarnings("unchecked")
    void themeSelectionModeComboIsFullyPopulated() {
        final var cb = (ComboBox<ThemeSelectionMode>) stage.getScene().getRoot()
                .lookup("#cbThemeSelectionMode");
        assertNotNull(cb);
        assertEquals(ThemeSelectionMode.values().length, cb.getItems().size(),
                "cbThemeSelectionMode must contain exactly "
                        + ThemeSelectionMode.values().length + " items");
    }

    // =========================================================================
    //  Test 6 – syncFromSettings() updates all three combos
    // =========================================================================

    @Test
    @DisplayName("syncFromSettings() updates cbNeonPreset, cbNeonGlowProfile, and cbThemeSelectionMode")
    @SuppressWarnings("unchecked")
    void syncFromSettingsUpdatesAllCombos() throws Exception {
        final var settings  = AppSettings.getInstance();
        final var origPreset = settings.getNeonPreset();
        final var origGlow   = settings.getNeonGlowProfile();
        final var origMode   = settings.getThemeSelectionMode();
        try {
            settings.setNeonPreset(NeonPreset.SCARLET_SURGE);
            settings.setNeonGlowProfile(NeonGlowProfile.VIVID);
            settings.setThemeSelectionMode(ThemeSelectionMode.SEQUENTIAL);

            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var root     = stage.getScene().getRoot();
            final var cbPreset = (ComboBox<NeonPreset>)         root.lookup("#cbNeonPreset");
            final var cbGlow   = (ComboBox<NeonGlowProfile>)    root.lookup("#cbNeonGlowProfile");
            final var cbMode   = (ComboBox<ThemeSelectionMode>) root.lookup("#cbThemeSelectionMode");

            assertEquals(NeonPreset.SCARLET_SURGE,        cbPreset.getValue(),
                    "cbNeonPreset must show SCARLET_SURGE after syncFromSettings()");
            assertEquals(NeonGlowProfile.VIVID,           cbGlow.getValue(),
                    "cbNeonGlowProfile must show VIVID after syncFromSettings()");
            assertEquals(ThemeSelectionMode.SEQUENTIAL,   cbMode.getValue(),
                    "cbThemeSelectionMode must show SEQUENTIAL after syncFromSettings()");
        } finally {
            settings.setNeonPreset(origPreset);
            settings.setNeonGlowProfile(origGlow);
            settings.setThemeSelectionMode(origMode);
        }
    }

    // =========================================================================
    //  Test 7 – syncPresetComboBox() updates only the preset combo
    // =========================================================================

    @Test
    @DisplayName("syncPresetComboBox() updates cbNeonPreset to the current AppSettings preset")
    @SuppressWarnings("unchecked")
    void syncPresetComboBoxUpdatesPresetCombo() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origPreset = settings.getNeonPreset();
        try {
            settings.setNeonPreset(NeonPreset.PRISM_VEIL);

            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncPresetComboBox();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var cb = (ComboBox<NeonPreset>) stage.getScene().getRoot().lookup("#cbNeonPreset");
            assertEquals(NeonPreset.PRISM_VEIL, cb.getValue(),
                    "cbNeonPreset must reflect PRISM_VEIL after syncPresetComboBox()");
        } finally {
            settings.setNeonPreset(origPreset);
        }
    }

    // =========================================================================
    //  Test 8 – Changing cbNeonPreset persists to AppSettings and calls updateUI()
    // =========================================================================

    @Test
    @DisplayName("Changing cbNeonPreset value persists to AppSettings and triggers updateUI()")
    @SuppressWarnings("unchecked")
    void presetComboChangePersistedToSettings() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origPreset = settings.getNeonPreset();
        final var mockMc     = Mockito.mock(MainController.class);
        try {
            // Choose a target preset different from the current one
            final var target = (origPreset != NeonPreset.AURORA_DRIFT)
                    ? NeonPreset.AURORA_DRIFT
                    : NeonPreset.COSMOS_BLAZE;

            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                final var cb = (ComboBox<NeonPreset>) stage.getScene().getRoot()
                        .lookup("#cbNeonPreset");
                cb.setValue(target);
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals(target, settings.getNeonPreset(),
                    "AppSettings.getNeonPreset() must match the combo box selection");
            Mockito.verify(mockMc, Mockito.atLeastOnce()).updateUI();
        } finally {
            settings.setNeonPreset(origPreset);
        }
    }

    // =========================================================================
    //  Test 9 – Changing cbNeonGlowProfile persists to AppSettings
    // =========================================================================

    @Test
    @DisplayName("Changing cbNeonGlowProfile value persists to AppSettings and triggers updateUI()")
    @SuppressWarnings("unchecked")
    void glowComboChangePersistedToSettings() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origGlow   = settings.getNeonGlowProfile();
        final var mockMc     = Mockito.mock(MainController.class);
        try {
            final var target = (origGlow != NeonGlowProfile.SOFT)
                    ? NeonGlowProfile.SOFT
                    : NeonGlowProfile.VIVID;

            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                final var cb = (ComboBox<NeonGlowProfile>) stage.getScene().getRoot()
                        .lookup("#cbNeonGlowProfile");
                cb.setValue(target);
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals(target, settings.getNeonGlowProfile(),
                    "AppSettings.getNeonGlowProfile() must match the combo box selection");
            Mockito.verify(mockMc, Mockito.atLeastOnce()).updateUI();
        } finally {
            settings.setNeonGlowProfile(origGlow);
        }
    }

    // =========================================================================
    //  Test 10 – Changing cbThemeSelectionMode persists to AppSettings
    // =========================================================================

    @Test
    @DisplayName("Changing cbThemeSelectionMode value persists to AppSettings")
    @SuppressWarnings("unchecked")
    void selectionModeComboChangePersistedToSettings() throws Exception {
        final var settings  = AppSettings.getInstance();
        final var origMode  = settings.getThemeSelectionMode();
        try {
            final var target = (origMode != ThemeSelectionMode.SHUFFLE)
                    ? ThemeSelectionMode.SHUFFLE
                    : ThemeSelectionMode.RANDOM;

            JavaFxTestHelper.runOnFxThread(() -> {
                final var cb = (ComboBox<ThemeSelectionMode>) stage.getScene().getRoot()
                        .lookup("#cbThemeSelectionMode");
                cb.setValue(target);
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals(target, settings.getThemeSelectionMode(),
                    "AppSettings.getThemeSelectionMode() must match the combo box selection");
        } finally {
            settings.setThemeSelectionMode(origMode);
        }
    }

    // =========================================================================
    //  Test 11 – btnBack.fire() calls mainController.showSettings()
    // =========================================================================

    @Test
    @DisplayName("btnBack.fire() triggers mainController.showSettings()")
    void btnBackFiresShowSettings() throws Exception {
        final var mockMc = Mockito.mock(MainController.class);
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.setMainController(mockMc);
            ((Button) stage.getScene().getRoot().lookup("#btnBack")).fire();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mockMc, Mockito.times(1)).showSettings();
    }
}


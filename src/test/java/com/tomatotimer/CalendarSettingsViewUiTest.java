package com.tomatotimer;

import com.tomatotimer.controller.CalendarSettingsController;
import com.tomatotimer.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestFX-based UI integration tests for {@code calendar_settings.fxml} /
 * {@link CalendarSettingsController}.
 *
 * <p>All tests are guarded by {@link JavaFxTestHelper#ensureToolkitStarted()} and
 * skipped automatically in headless / CI environments.</p>
 *
 * <h3>Coverage targets</h3>
 * <ul>
 *   <li>Structural node presence and correct types.</li>
 *   <li>{@link CalendarSettingsController#syncFromSettings()} populates text fields and checkboxes.</li>
 *   <li>{@code gpGCalDetails} is disabled when GCal is off, enabled when GCal is on.</li>
 *   <li>{@code cbEnableGCal.fire()} toggles the detail grid's disabled state via
 *       {@code onGCalEnableChanged()}.</li>
 *   <li>Back button calls {@link MainController#showSettings()}.</li>
 * </ul>
 */
@DisplayName("CalendarSettingsView – TestFX UI integration tests")
class CalendarSettingsViewUiTest {

    private Stage                      stage;
    private CalendarSettingsController controller;

    // ── JavaFX toolkit guard ─────────────────────────────────────────────────

    @BeforeAll
    static void requireFxToolkit() {
        Assumptions.assumeTrue(
                JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – CalendarSettingsView UI tests skipped");
    }

    // ── Per-test fixture setup ───────────────────────────────────────────────

    @BeforeEach
    void setUp() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            final var loader = new FXMLLoader(App.class.getResource("calendar_settings.fxml"));
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
    @DisplayName("calendar_settings.fxml root node is an HBox")
    void rootIsHBox() {
        assertInstanceOf(HBox.class, stage.getScene().getRoot(),
                "The root node in calendar_settings.fxml must be an HBox");
    }

    // =========================================================================
    //  Test 2 – Key nodes present
    // =========================================================================

    @Test
    @DisplayName("Key nodes are present (btnBack, btnTestGCal, cbEnableGCal, cbCopyToClipboard, "
            + "tfGCalSrc, tfGCalText, gpGCalDetails)")
    void keyNodesArePresent() {
        final var root = stage.getScene().getRoot();
        assertNotNull(root.lookup("#btnBack"),           "#btnBack must be present");
        assertNotNull(root.lookup("#btnTestGCal"),       "#btnTestGCal must be present");
        assertNotNull(root.lookup("#cbEnableGCal"),      "#cbEnableGCal must be present");
        assertNotNull(root.lookup("#cbCopyToClipboard"), "#cbCopyToClipboard must be present");
        assertNotNull(root.lookup("#tfGCalSrc"),         "#tfGCalSrc must be present");
        assertNotNull(root.lookup("#tfGCalText"),        "#tfGCalText must be present");
        assertNotNull(root.lookup("#gpGCalDetails"),     "#gpGCalDetails must be present");
    }

    // =========================================================================
    //  Test 3 – syncFromSettings() sets text fields and checkboxes
    // =========================================================================

    @Test
    @DisplayName("syncFromSettings() populates text fields and checkboxes from AppSettings")
    void syncFromSettingsSetsValues() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origSrc    = settings.getGCalSrc();
        final var origText   = settings.getGCalText();
        final var origEnable = settings.isGCalEnable();
        final var origCopy   = settings.isGCalCopyToClipboard();
        try {
            settings.setGCalSrc("test@gmail.com");
            settings.setGCalText("Pomodoro session");
            settings.setGCalEnable(true);
            settings.setGCalCopyToClipboard(true);

            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var root = stage.getScene().getRoot();
            assertEquals("test@gmail.com",   ((TextField) root.lookup("#tfGCalSrc")).getText(),
                    "tfGCalSrc must reflect setGCalSrc(\"test@gmail.com\")");
            assertEquals("Pomodoro session", ((TextField) root.lookup("#tfGCalText")).getText(),
                    "tfGCalText must reflect setGCalText(\"Pomodoro session\")");
            assertTrue(((CheckBox) root.lookup("#cbEnableGCal")).isSelected(),
                    "cbEnableGCal must be selected when isGCalEnable() is true");
            assertTrue(((CheckBox) root.lookup("#cbCopyToClipboard")).isSelected(),
                    "cbCopyToClipboard must be selected when isGCalCopyToClipboard() is true");
        } finally {
            settings.setGCalSrc(origSrc);
            settings.setGCalText(origText);
            settings.setGCalEnable(origEnable);
            settings.setGCalCopyToClipboard(origCopy);
        }
    }

    // =========================================================================
    //  Test 4 – gpGCalDetails disabled when GCal is off
    // =========================================================================

    @Test
    @DisplayName("gpGCalDetails is disabled after syncFromSettings() when isGCalEnable() is false")
    void detailGridIsDisabledWhenGCalOff() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origEnable = settings.isGCalEnable();
        try {
            settings.setGCalEnable(false);
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var gp = (GridPane) stage.getScene().getRoot().lookup("#gpGCalDetails");
            assertTrue(gp.isDisable(),
                    "gpGCalDetails must be disabled when GCal integration is off");
        } finally {
            settings.setGCalEnable(origEnable);
        }
    }

    // =========================================================================
    //  Test 5 – gpGCalDetails enabled when GCal is on
    // =========================================================================

    @Test
    @DisplayName("gpGCalDetails is enabled after syncFromSettings() when isGCalEnable() is true")
    void detailGridIsEnabledWhenGCalOn() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origEnable = settings.isGCalEnable();
        try {
            settings.setGCalEnable(true);
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var gp = (GridPane) stage.getScene().getRoot().lookup("#gpGCalDetails");
            assertFalse(gp.isDisable(),
                    "gpGCalDetails must be enabled when GCal integration is on");
        } finally {
            settings.setGCalEnable(origEnable);
        }
    }

    // =========================================================================
    //  Test 6 – cbEnableGCal.fire() (onGCalEnableChanged) toggles the detail grid
    // =========================================================================

    @Test
    @DisplayName("cbEnableGCal.fire() (onGCalEnableChanged) enables gpGCalDetails when toggled to selected")
    void cbEnableGCalToggleEnablesDetailGrid() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origEnable = settings.isGCalEnable();
        try {
            // Start with GCal disabled so the detail grid is disabled
            settings.setGCalEnable(false);
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            // Toggle checkbox to enabled via programmatic fire()
            JavaFxTestHelper.runOnFxThread(() -> {
                ((CheckBox) stage.getScene().getRoot().lookup("#cbEnableGCal")).fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var gp = (GridPane) stage.getScene().getRoot().lookup("#gpGCalDetails");
            assertFalse(gp.isDisable(),
                    "gpGCalDetails must be enabled after cbEnableGCal is toggled to checked");
        } finally {
            settings.setGCalEnable(origEnable);
        }
    }

    // =========================================================================
    //  Test 7 – cbEnableGCal.fire() disables detail grid when toggled to unselected
    // =========================================================================

    @Test
    @DisplayName("cbEnableGCal.fire() (onGCalEnableChanged) disables gpGCalDetails when toggled to unselected")
    void cbEnableGCalToggleDisablesDetailGrid() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origEnable = settings.isGCalEnable();
        try {
            // Start with GCal enabled so the detail grid is enabled
            settings.setGCalEnable(true);
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            // Toggle checkbox to disabled via programmatic fire()
            JavaFxTestHelper.runOnFxThread(() -> {
                ((CheckBox) stage.getScene().getRoot().lookup("#cbEnableGCal")).fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var gp = (GridPane) stage.getScene().getRoot().lookup("#gpGCalDetails");
            assertTrue(gp.isDisable(),
                    "gpGCalDetails must be disabled after cbEnableGCal is toggled to unchecked");
        } finally {
            settings.setGCalEnable(origEnable);
        }
    }

    // =========================================================================
    //  Test 8 – btnBack.fire() calls mainController.showSettings()
    // =========================================================================

    @Test
    @DisplayName("btnBack.fire() triggers mainController.showSettings()")
    void btnBackFiresShowSettings() throws Exception {
        // Save and restore all settings that syncToSettings() might write on Back.
        final var settings   = AppSettings.getInstance();
        final var origEnable = settings.isGCalEnable();
        final var origCopy   = settings.isGCalCopyToClipboard();
        final var origSrc    = settings.getGCalSrc();
        final var origText   = settings.getGCalText();
        try {
            final var mockMc = Mockito.mock(MainController.class);
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                ((Button) stage.getScene().getRoot().lookup("#btnBack")).fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();
            Mockito.verify(mockMc, Mockito.times(1)).showSettings();
        } finally {
            settings.setGCalEnable(origEnable);
            settings.setGCalCopyToClipboard(origCopy);
            settings.setGCalSrc(origSrc);
            settings.setGCalText(origText);
        }
    }

    // =========================================================================
    //  Test 9 – onBack() persists text-field values to AppSettings
    // =========================================================================

    @Test
    @DisplayName("btnBack.fire() persists tfGCalSrc and tfGCalText to AppSettings via syncToSettings()")
    void btnBackPersistsTextFieldsToSettings() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origSrc    = settings.getGCalSrc();
        final var origText   = settings.getGCalText();
        final var origEnable = settings.isGCalEnable();
        final var origCopy   = settings.isGCalCopyToClipboard();
        final var mockMc     = Mockito.mock(MainController.class);
        try {
            // Type into text fields on FX thread, then fire Back
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                ((TextField) stage.getScene().getRoot().lookup("#tfGCalSrc")).setText("work@example.com");
                ((TextField) stage.getScene().getRoot().lookup("#tfGCalText")).setText("Deep Work");
                ((Button)    stage.getScene().getRoot().lookup("#btnBack")).fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals("work@example.com", settings.getGCalSrc(),
                    "getGCalSrc() must reflect the text entered in tfGCalSrc after onBack()");
            assertEquals("Deep Work", settings.getGCalText(),
                    "getGCalText() must reflect the text entered in tfGCalText after onBack()");
        } finally {
            settings.setGCalSrc(origSrc);
            settings.setGCalText(origText);
            settings.setGCalEnable(origEnable);
            settings.setGCalCopyToClipboard(origCopy);
        }
    }

    // =========================================================================
    //  Test 10 – regression: initialize() must pre-populate cbEnableGCal from
    //              AppSettings (not the FXML default "unchecked") so that
    //              btnBack.fire() → syncToSettings() never writes false to the registry.
    // =========================================================================

    @Test
    @DisplayName("initialize() pre-populates cbEnableGCal from AppSettings, not FXML default – " +
            "btnBack.fire() must preserve gcal_enable=true")
    void initializePrePopulatesGCalEnableAndBtnBackPreservesIt() throws Exception {
        // Regression: CalendarSettingsController.initialize() used to do nothing with
        // AppSettings (no syncFromSettings() call).  The cbEnableGCal FXML default was
        // unchecked (false).  Firing btnBack triggered syncToSettings() which wrote false
        // to gcal_enable in java.util.prefs.Preferences (Windows Registry), overriding
        // the migration default of true.
        final var settings   = AppSettings.getInstance();
        final var origEnable = settings.isGCalEnable();
        final var origCopy   = settings.isGCalCopyToClipboard();
        final var origSrc    = settings.getGCalSrc();
        final var origText   = settings.getGCalText();
        final Stage[] freshStage = new Stage[1];
        try {
            settings.setGCalEnable(true);

            // Load a FRESH FXML instance *after* setting the value so initialize() picks it up.
            final CalendarSettingsController[] freshCtrl = new CalendarSettingsController[1];
            JavaFxTestHelper.runOnFxThread(() -> {
                final var loader = new FXMLLoader(App.class.getResource("calendar_settings.fxml"));
                final HBox freshRoot = loader.load();
                freshCtrl[0] = loader.getController();
                freshStage[0] = new Stage();
                freshStage[0].setScene(new Scene(freshRoot, 520, 44));
                freshStage[0].show();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            // cbEnableGCal must already reflect AppSettings (initialize() calls syncFromSettings()).
            final var cb = (CheckBox) freshStage[0].getScene().getRoot().lookup("#cbEnableGCal");
            assertNotNull(cb, "#cbEnableGCal must be present");
            assertTrue(cb.isSelected(),
                    "cbEnableGCal must be true (AppSettings value) immediately after FXML load; " +
                    "initialize() must call syncFromSettings() so the FXML default unchecked is overridden");

            // Fire Back WITHOUT an explicit syncFromSettings() – must not write false to registry.
            final var mockMc = Mockito.mock(MainController.class);
            JavaFxTestHelper.runOnFxThread(() -> {
                freshCtrl[0].setMainController(mockMc);
                ((Button) freshStage[0].getScene().getRoot().lookup("#btnBack")).fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertTrue(settings.isGCalEnable(),
                    "gcal_enable must remain true after btnBack fires; " +
                    "syncToSettings() must not write the FXML stub false to prefs");
        } finally {
            settings.setGCalEnable(origEnable);
            settings.setGCalCopyToClipboard(origCopy);
            settings.setGCalSrc(origSrc);
            settings.setGCalText(origText);
            if (freshStage[0] != null) {
                JavaFxTestHelper.runOnFxThread(() -> {
                    freshStage[0].hide();
                    return null;
                });
            }
        }
    }
}


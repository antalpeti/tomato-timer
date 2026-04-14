package com.tomatotimer;

import com.tomatotimer.controller.MainController;
import com.tomatotimer.controller.TaskbarSettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
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

@SuppressWarnings("unchecked")

/**
 * TestFX-based UI integration tests for {@code taskbar_settings.fxml} /
 * {@link TaskbarSettingsController}.
 *
 * <p>All tests are guarded by {@link JavaFxTestHelper#ensureToolkitStarted()} and
 * skipped automatically in headless / CI environments.</p>
 *
 * <h3>Coverage targets</h3>
 * <ul>
 *   <li>Structural node presence and correct types.</li>
 *   <li>MM:SS font-size spinner range: min&nbsp;8 / max&nbsp;28.</li>
 *   <li>HH:MM:SS font-size spinner range: min&nbsp;8 / max&nbsp;28.</li>
 *   <li>{@link TaskbarSettingsController#syncFromSettings()} reflects persisted enable flag and layout.</li>
 *   <li>Enable checkbox {@code fire()} propagates to {@link MainController#updateUI()}.</li>
 *   <li>Layout radio button {@code fire()} persists the new layout to settings.</li>
 *   <li>Back button calls {@link MainController#showSettings()} and {@link MainController#updateUI()}.</li>
 *   <li>MM:SS spinner change persists to {@link AppSettings#getTaskbarFontSizeMmss()}.</li>
 *   <li>HH:MM:SS spinner change persists to {@link AppSettings#getTaskbarFontSizeHhmmss()}.</li>
 * </ul>
 */
@DisplayName("TaskbarSettingsView – TestFX UI integration tests")
class TaskbarSettingsViewUiTest {

    private Stage                     stage;
    private TaskbarSettingsController controller;

    // ── JavaFX toolkit guard ─────────────────────────────────────────────────

    @BeforeAll
    static void requireFxToolkit() {
        Assumptions.assumeTrue(
                JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – TaskbarSettingsView UI tests skipped");
    }

    // ── Per-test fixture setup ───────────────────────────────────────────────

    @BeforeEach
    void setUp() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            final var loader = new FXMLLoader(App.class.getResource("taskbar_settings.fxml"));
            final HBox root = loader.load();
            controller = loader.getController();
            stage = new Stage();
            stage.setScene(new Scene(root, 500, 44));
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
    @DisplayName("taskbar_settings.fxml root node is an HBox")
    void rootIsHBox() {
        assertInstanceOf(HBox.class, stage.getScene().getRoot(),
                "The root node in taskbar_settings.fxml must be an HBox");
    }

    // =========================================================================
    //  Test 2 – Key nodes present
    // =========================================================================

    @Test
    @DisplayName("Key nodes are present (btnBack, cbTaskbarEnable, spFontSizeMmss, spFontSizeHhmmss, rbVertical, rbHorizontal)")
    void keyNodesArePresent() {
        final var root = stage.getScene().getRoot();
        assertNotNull(root.lookup("#btnBack"),           "#btnBack must be present");
        assertNotNull(root.lookup("#cbTaskbarEnable"),   "#cbTaskbarEnable must be present");
        assertNotNull(root.lookup("#spFontSizeMmss"),    "#spFontSizeMmss must be present");
        assertNotNull(root.lookup("#spFontSizeHhmmss"),  "#spFontSizeHhmmss must be present");
        assertNotNull(root.lookup("#rbVertical"),        "#rbVertical must be present");
        assertNotNull(root.lookup("#rbHorizontal"),      "#rbHorizontal must be present");
    }

    // =========================================================================
    //  Test 3 – MM:SS font-size spinner range
    // =========================================================================

    @Test
    @DisplayName("spFontSizeMmss spinner min is 8 and max is 28 (matches FONT_MIN / FONT_MAX)")
    void fontSizeMmssSpinnerRangeMatchesConstants() {
        final var sp = (javafx.scene.control.Spinner<Integer>)
                stage.getScene().getRoot().lookup("#spFontSizeMmss");
        assertNotNull(sp, "#spFontSizeMmss must be present");
        final var factory = (javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory)
                sp.getValueFactory();
        assertEquals(8,  factory.getMin(), "spFontSizeMmss min must be 8");
        assertEquals(28, factory.getMax(), "spFontSizeMmss max must be 28");
    }

    // =========================================================================
    //  Test 4 – HH:MM:SS font-size spinner range
    // =========================================================================

    @Test
    @DisplayName("spFontSizeHhmmss spinner min is 8 and max is 28 (matches FONT_MIN / FONT_MAX)")
    void fontSizeHhmmssSpinnerRangeMatchesConstants() {
        final var sp = (javafx.scene.control.Spinner<Integer>)
                stage.getScene().getRoot().lookup("#spFontSizeHhmmss");
        assertNotNull(sp, "#spFontSizeHhmmss must be present");
        final var factory = (javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory)
                sp.getValueFactory();
        assertEquals(8,  factory.getMin(), "spFontSizeHhmmss min must be 8");
        assertEquals(28, factory.getMax(), "spFontSizeHhmmss max must be 28");
    }

    // =========================================================================
    //  Test 5 – syncFromSettings() reflects isTaskbarIconEnable() == false
    // =========================================================================

    @Test
    @DisplayName("syncFromSettings() unchecks cbTaskbarEnable when isTaskbarIconEnable() is false")
    void syncFromSettingsWhenDisabled() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origEnable = settings.isTaskbarIconEnable();
        try {
            settings.setTaskbarIconEnable(false);
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var cb = (CheckBox) stage.getScene().getRoot().lookup("#cbTaskbarEnable");
            assertFalse(cb.isSelected(),
                    "cbTaskbarEnable must be unchecked when isTaskbarIconEnable() is false");
        } finally {
            settings.setTaskbarIconEnable(origEnable);
        }
    }

    // =========================================================================
    //  Test 6 – syncFromSettings() selects rbVertical for VERTICAL layout
    // =========================================================================

    @Test
    @DisplayName("syncFromSettings() selects rbVertical for TaskbarTimeLayout.VERTICAL")
    void syncFromSettingsSelectsVertical() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origLayout = settings.getTaskbarLayout();
        try {
            settings.setTaskbarLayout(TaskbarTimeLayout.VERTICAL);
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var rbV = (RadioButton) stage.getScene().getRoot().lookup("#rbVertical");
            final var rbH = (RadioButton) stage.getScene().getRoot().lookup("#rbHorizontal");
            assertTrue(rbV.isSelected(),  "rbVertical must be selected for VERTICAL layout");
            assertFalse(rbH.isSelected(), "rbHorizontal must not be selected for VERTICAL layout");
        } finally {
            settings.setTaskbarLayout(origLayout);
        }
    }

    // =========================================================================
    //  Test 7 – syncFromSettings() selects rbHorizontal for HORIZONTAL layout
    // =========================================================================

    @Test
    @DisplayName("syncFromSettings() selects rbHorizontal for TaskbarTimeLayout.HORIZONTAL")
    void syncFromSettingsSelectsHorizontal() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origLayout = settings.getTaskbarLayout();
        try {
            settings.setTaskbarLayout(TaskbarTimeLayout.HORIZONTAL);
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var rbV = (RadioButton) stage.getScene().getRoot().lookup("#rbVertical");
            final var rbH = (RadioButton) stage.getScene().getRoot().lookup("#rbHorizontal");
            assertFalse(rbV.isSelected(), "rbVertical must not be selected for HORIZONTAL layout");
            assertTrue(rbH.isSelected(),  "rbHorizontal must be selected for HORIZONTAL layout");
        } finally {
            settings.setTaskbarLayout(origLayout);
        }
    }

    // =========================================================================
    //  Test 8 – cbTaskbarEnable.fire() calls mainController.updateUI()
    // =========================================================================

    @Test
    @DisplayName("cbTaskbarEnable.fire() (onEnableChanged) calls mainController.updateUI()")
    void cbTaskbarEnableFireCallsUpdateUI() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origEnable = settings.isTaskbarIconEnable();
        final var mockMc     = Mockito.mock(MainController.class);
        try {
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                final var cb = (CheckBox) stage.getScene().getRoot().lookup("#cbTaskbarEnable");
                cb.fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();
            Mockito.verify(mockMc, Mockito.atLeastOnce()).updateUI();
        } finally {
            settings.setTaskbarIconEnable(origEnable);
        }
    }

    // =========================================================================
    //  Test 9 – rbHorizontal.fire() persists HORIZONTAL layout and calls updateUI()
    // =========================================================================

    @Test
    @DisplayName("rbHorizontal.fire() (onLayoutChanged) persists HORIZONTAL and calls updateUI()")
    void rbHorizontalFirePersistsLayout() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origLayout = settings.getTaskbarLayout();
        final var mockMc     = Mockito.mock(MainController.class);
        try {
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                final var rb = (RadioButton) stage.getScene().getRoot().lookup("#rbHorizontal");
                rb.fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals(TaskbarTimeLayout.HORIZONTAL, settings.getTaskbarLayout(),
                    "getTaskbarLayout() must be HORIZONTAL after rbHorizontal fires");
            Mockito.verify(mockMc, Mockito.atLeastOnce()).updateUI();
        } finally {
            settings.setTaskbarLayout(origLayout);
        }
    }

    // =========================================================================
    //  Test 10 – rbVertical.fire() persists VERTICAL layout
    // =========================================================================

    @Test
    @DisplayName("rbVertical.fire() (onLayoutChanged) persists VERTICAL layout")
    void rbVerticalFirePersistsLayout() throws Exception {
        final var settings   = AppSettings.getInstance();
        final var origLayout = settings.getTaskbarLayout();
        final var mockMc     = Mockito.mock(MainController.class);
        try {
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                ((RadioButton) stage.getScene().getRoot().lookup("#rbHorizontal")).fire();
                ((RadioButton) stage.getScene().getRoot().lookup("#rbVertical")).fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals(TaskbarTimeLayout.VERTICAL, settings.getTaskbarLayout(),
                    "getTaskbarLayout() must be VERTICAL after rbVertical fires");
        } finally {
            settings.setTaskbarLayout(origLayout);
        }
    }

    // =========================================================================
    //  Test 11 – spFontSizeMmss change persists to AppSettings
    // =========================================================================

    @Test
    @DisplayName("spFontSizeMmss value change persists the clamped MM:SS font size to AppSettings")
    void fontSizeMmssSpinnerPersistsToSettings() throws Exception {
        final var settings     = AppSettings.getInstance();
        final var origFontSize = settings.getTaskbarFontSizeMmss();
        final var mockMc       = Mockito.mock(MainController.class);
        try {
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                final var sp = (Spinner<Integer>) stage.getScene().getRoot().lookup("#spFontSizeMmss");
                sp.getValueFactory().setValue(20);
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals(20.0, settings.getTaskbarFontSizeMmss(), 1e-9,
                    "AppSettings.getTaskbarFontSizeMmss() must be 20 after spinner changes to 20");
        } finally {
            settings.setTaskbarFontSizeMmss(origFontSize);
        }
    }

    // =========================================================================
    //  Test 12 – spFontSizeHhmmss change persists to AppSettings
    // =========================================================================

    @Test
    @DisplayName("spFontSizeHhmmss value change persists the clamped HH:MM:SS font size to AppSettings")
    void fontSizeHhmmssSpinnerPersistsToSettings() throws Exception {
        final var settings     = AppSettings.getInstance();
        final var origFontSize = settings.getTaskbarFontSizeHhmmss();
        final var mockMc       = Mockito.mock(MainController.class);
        try {
            JavaFxTestHelper.runOnFxThread(() -> {
                controller.setMainController(mockMc);
                final var sp = (Spinner<Integer>) stage.getScene().getRoot().lookup("#spFontSizeHhmmss");
                sp.getValueFactory().setValue(15);
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals(15.0, settings.getTaskbarFontSizeHhmmss(), 1e-9,
                    "AppSettings.getTaskbarFontSizeHhmmss() must be 15 after spinner changes to 15");
        } finally {
            settings.setTaskbarFontSizeHhmmss(origFontSize);
        }
    }

    // =========================================================================
    //  Test 13 – btnBack.fire() calls showSettings() and updateUI()
    // =========================================================================

    @Test
    @DisplayName("btnBack.fire() calls mainController.showSettings() and mainController.updateUI()")
    void btnBackFireCallsShowSettingsAndUpdateUI() throws Exception {
        final var mockMc = Mockito.mock(MainController.class);
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.setMainController(mockMc);
            ((Button) stage.getScene().getRoot().lookup("#btnBack")).fire();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mockMc, Mockito.times(1)).showSettings();
        Mockito.verify(mockMc, Mockito.atLeastOnce()).updateUI();
    }

    // =========================================================================
    //  Test 14 – regression: initialize() must pre-populate controls from AppSettings
    //             so that btnBack.fire() → syncToSettings() never overwrites the registry
    //             with stale FXML initial values.
    // =========================================================================

    @Test
    @DisplayName("initialize() pre-populates cbTaskbarEnable and both font spinners from AppSettings, not FXML stubs – " +
            "btnBack.fire() must preserve taskbar_icon_enable=true, font_mmss=23, font_hhmmss=17")
    void initializePrePopulatesTaskbarControlsAndBtnBackPreservesThem() throws Exception {
        final var settings      = AppSettings.getInstance();
        final var origEnable    = settings.isTaskbarIconEnable();
        final var origFontMmss  = settings.getTaskbarFontSizeMmss();
        final var origFontHhmm  = settings.getTaskbarFontSizeHhmmss();
        final Stage[] freshStage = new Stage[1];
        try {
            settings.setTaskbarIconEnable(true);
            settings.setTaskbarFontSizeMmss(23.0);
            settings.setTaskbarFontSizeHhmmss(17.0);

            final TaskbarSettingsController[] freshCtrl = new TaskbarSettingsController[1];
            JavaFxTestHelper.runOnFxThread(() -> {
                final var loader = new FXMLLoader(App.class.getResource("taskbar_settings.fxml"));
                final HBox freshRoot = loader.load();
                freshCtrl[0] = loader.getController();
                freshStage[0] = new Stage();
                freshStage[0].setScene(new Scene(freshRoot, 500, 44));
                freshStage[0].show();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var cb    = (CheckBox)         freshStage[0].getScene().getRoot().lookup("#cbTaskbarEnable");
            final var spMm  = (Spinner<Integer>) freshStage[0].getScene().getRoot().lookup("#spFontSizeMmss");
            final var spHh  = (Spinner<Integer>) freshStage[0].getScene().getRoot().lookup("#spFontSizeHhmmss");
            assertNotNull(cb,   "#cbTaskbarEnable must be present");
            assertNotNull(spMm, "#spFontSizeMmss must be present");
            assertNotNull(spHh, "#spFontSizeHhmmss must be present");
            assertTrue(cb.isSelected(),
                    "cbTaskbarEnable must be true (AppSettings value) immediately after FXML load");
            assertEquals(23, (int) spMm.getValue(),
                    "spFontSizeMmss must be 23 (AppSettings value) immediately after FXML load");
            assertEquals(17, (int) spHh.getValue(),
                    "spFontSizeHhmmss must be 17 (AppSettings value) immediately after FXML load");

            final var mockMc = Mockito.mock(MainController.class);
            JavaFxTestHelper.runOnFxThread(() -> {
                freshCtrl[0].setMainController(mockMc);
                ((Button) freshStage[0].getScene().getRoot().lookup("#btnBack")).fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertTrue(settings.isTaskbarIconEnable(),
                    "taskbar_icon_enable must remain true; syncToSettings() must not write the FXML stub false");
            assertEquals(23.0, settings.getTaskbarFontSizeMmss(), 1e-9,
                    "taskbar_font_size_mmss must remain 23.0; syncToSettings() must not overwrite");
            assertEquals(17.0, settings.getTaskbarFontSizeHhmmss(), 1e-9,
                    "taskbar_font_size_hhmmss must remain 17.0; syncToSettings() must not overwrite");
        } finally {
            settings.setTaskbarIconEnable(origEnable);
            settings.setTaskbarFontSizeMmss(origFontMmss);
            settings.setTaskbarFontSizeHhmmss(origFontHhmm);
            if (freshStage[0] != null) {
                JavaFxTestHelper.runOnFxThread(() -> {
                    freshStage[0].hide();
                    return null;
                });
            }
        }
    }
}


package com.tomatotimer;

import com.tomatotimer.controller.MainController;
import com.tomatotimer.controller.SettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestFX-based UI integration tests for {@code settings.fxml} /
 * {@link SettingsController}.
 *
 * <p>All tests are guarded by {@link JavaFxTestHelper#ensureToolkitStarted()} and
 * skipped automatically in headless / CI environments.</p>
 *
 * <h3>Coverage targets</h3>
 * <ul>
 *   <li>Structural node presence and correct types.</li>
 *   <li>{@link SettingsController#syncFromSettings()} propagates AppSettings values to spinners.</li>
 *   <li>Navigation button fire() calls the correct {@link MainController} method.</li>
 * </ul>
 */
@DisplayName("SettingsView – TestFX UI integration tests")
class SettingsViewUiTest {

    private Stage             stage;
    private SettingsController controller;

    // ── JavaFX toolkit guard ─────────────────────────────────────────────────

    @BeforeAll
    static void requireFxToolkit() {
        Assumptions.assumeTrue(
                JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – SettingsView UI tests skipped");
    }

    // ── Per-test fixture setup ───────────────────────────────────────────────

    @BeforeEach
    void setUp() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            final var loader = new FXMLLoader(App.class.getResource("settings.fxml"));
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
    @DisplayName("settings.fxml root node is an HBox")
    void rootIsHBox() {
        assertInstanceOf(HBox.class, stage.getScene().getRoot(),
                "The root node in settings.fxml must be an HBox");
    }

    // =========================================================================
    //  Test 2 – btnBack presence
    // =========================================================================

    @Test
    @DisplayName("btnBack is present and is a Button")
    void btnBackIsPresent() {
        final var node = stage.getScene().getRoot().lookup("#btnBack");
        assertNotNull(node, "#btnBack must be present in settings.fxml");
        assertInstanceOf(Button.class, node);
    }

    // =========================================================================
    //  Test 3 – Spinner nodes present
    // =========================================================================

    @Test
    @DisplayName("Work / Rest / LongRest spinners are present")
    void spinnersArePresent() {
        final var root = stage.getScene().getRoot();
        assertNotNull(root.lookup("#spWorkTime"),      "#spWorkTime must be present");
        assertNotNull(root.lookup("#spRelaxTime"),     "#spRelaxTime must be present");
        assertNotNull(root.lookup("#spLongRelaxTime"), "#spLongRelaxTime must be present");
    }

    // =========================================================================
    //  Test 4 – Navigation buttons present
    // =========================================================================

    @Test
    @DisplayName("Navigation icon buttons are present")
    void navButtonsArePresent() {
        final var root = stage.getScene().getRoot();
        assertNotNull(root.lookup("#btnThemeSettings"),    "#btnThemeSettings must be present");
        assertNotNull(root.lookup("#btnCalendarSettings"), "#btnCalendarSettings must be present");
        assertNotNull(root.lookup("#btnSoundSettings"),    "#btnSoundSettings must be present");
        assertNotNull(root.lookup("#btnTaskbarSettings"),  "#btnTaskbarSettings must be present");
    }

    // =========================================================================
    //  Test 5 – Version label
    // =========================================================================

    @Test
    @DisplayName("labelVersion is present and its text starts with 'v'")
    void labelVersionStartsWithV() {
        final var node = stage.getScene().getRoot().lookup("#labelVersion");
        assertNotNull(node, "#labelVersion must be present in settings.fxml");
        assertInstanceOf(Label.class, node);
        assertTrue(((Label) node).getText().startsWith("v"),
                "Version label text must start with 'v'");
    }

    // =========================================================================
    //  Test 6 – syncFromSettings() propagates AppSettings values to spinners
    // =========================================================================

    @Test
    @DisplayName("syncFromSettings() sets spinner values from AppSettings")
    @SuppressWarnings("unchecked")
    void syncFromSettingsUpdatesSpinners() throws Exception {
        final var settings  = AppSettings.getInstance();
        final int origWork  = settings.getWorkTime();
        final int origRelax = settings.getRelaxTime();
        final int origLong  = settings.getRelaxTimeLong();
        try {
            settings.setWorkTime(20);
            settings.setRelaxTime(7);
            settings.setRelaxTimeLong(12);

            JavaFxTestHelper.runOnFxThread(() -> {
                controller.syncFromSettings();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            final var root    = stage.getScene().getRoot();
            final var spWork  = (Spinner<Integer>) root.lookup("#spWorkTime");
            final var spRelax = (Spinner<Integer>) root.lookup("#spRelaxTime");
            final var spLong  = (Spinner<Integer>) root.lookup("#spLongRelaxTime");

            assertEquals(20, (int) spWork.getValue(),
                    "spWorkTime must reflect setWorkTime(20)");
            assertEquals(7, (int) spRelax.getValue(),
                    "spRelaxTime must reflect setRelaxTime(7)");
            assertEquals(12, (int) spLong.getValue(),
                    "spLongRelaxTime must reflect setRelaxTimeLong(12)");
        } finally {
            settings.setWorkTime(origWork);
            settings.setRelaxTime(origRelax);
            settings.setRelaxTimeLong(origLong);
        }
    }

    // =========================================================================
    //  Test 7 – btnBack.fire() → mainController.showButtons()
    // =========================================================================

    @Test
    @DisplayName("btnBack.fire() triggers mainController.showButtons()")
    void btnBackFiresShowButtons() throws Exception {
        final var mockMc = Mockito.mock(MainController.class);
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.setMainController(mockMc);
            final var btn = (Button) stage.getScene().getRoot().lookup("#btnBack");
            assertNotNull(btn, "#btnBack not found on FX thread");
            btn.fire();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mockMc, Mockito.times(1)).showButtons();
    }

    // =========================================================================
    //  Test 8 – btnThemeSettings.fire() → mainController.showThemeSettings()
    // =========================================================================

    @Test
    @DisplayName("btnThemeSettings.fire() triggers mainController.showThemeSettings()")
    void btnThemeSettingsFiresShowThemeSettings() throws Exception {
        final var mockMc = Mockito.mock(MainController.class);
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.setMainController(mockMc);
            final var btn = (Button) stage.getScene().getRoot().lookup("#btnThemeSettings");
            assertNotNull(btn, "#btnThemeSettings not found on FX thread");
            btn.fire();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mockMc, Mockito.times(1)).showThemeSettings();
    }

    // =========================================================================
    //  Test 9 – btnCalendarSettings.fire() → mainController.showCalendarSettings()
    // =========================================================================

    @Test
    @DisplayName("btnCalendarSettings.fire() triggers mainController.showCalendarSettings()")
    void btnCalendarSettingsFiresShowCalendarSettings() throws Exception {
        final var mockMc = Mockito.mock(MainController.class);
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.setMainController(mockMc);
            final var btn = (Button) stage.getScene().getRoot().lookup("#btnCalendarSettings");
            assertNotNull(btn, "#btnCalendarSettings not found on FX thread");
            btn.fire();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mockMc, Mockito.times(1)).showCalendarSettings();
    }

    // =========================================================================
    //  Test 10 – btnSoundSettings.fire() → mainController.showSoundSettings()
    // =========================================================================

    @Test
    @DisplayName("btnSoundSettings.fire() triggers mainController.showSoundSettings()")
    void btnSoundSettingsFiresShowSoundSettings() throws Exception {
        final var mockMc = Mockito.mock(MainController.class);
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.setMainController(mockMc);
            final var btn = (Button) stage.getScene().getRoot().lookup("#btnSoundSettings");
            assertNotNull(btn, "#btnSoundSettings not found on FX thread");
            btn.fire();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mockMc, Mockito.times(1)).showSoundSettings();
    }

    // =========================================================================
    //  Test 11 – btnTaskbarSettings.fire() → mainController.showTaskbarSettings()
    // =========================================================================

    @Test
    @DisplayName("btnTaskbarSettings.fire() triggers mainController.showTaskbarSettings()")
    void btnTaskbarSettingsFiresShowTaskbarSettings() throws Exception {
        final var mockMc = Mockito.mock(MainController.class);
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.setMainController(mockMc);
            final var btn = (Button) stage.getScene().getRoot().lookup("#btnTaskbarSettings");
            assertNotNull(btn, "#btnTaskbarSettings not found on FX thread");
            btn.fire();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();
        Mockito.verify(mockMc, Mockito.times(1)).showTaskbarSettings();
    }
}


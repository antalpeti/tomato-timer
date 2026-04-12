package com.tomatotimer;

import com.tomatotimer.controller.ButtonsController;
import com.tomatotimer.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestFX-based UI integration tests for {@code buttons.fxml} /
 * {@link ButtonsController}.
 *
 * <p>All tests are guarded by {@link JavaFxTestHelper#ensureToolkitStarted()} and
 * are skipped automatically in headless / CI environments where the JavaFX Glass
 * backend cannot initialise.</p>
 *
 * <h3>Coverage targets</h3>
 * <ul>
 *   <li>Initial FXML state (label text, progress bar value).</li>
 *   <li>Button visibility transitions driven by {@link ButtonsController#updateUI}.</li>
 *   <li>Minimal button interaction: {@code btnPlay.fire()} propagates to
 *       {@link MainController#resume()} via the FXML action binding.</li>
 * </ul>
 */
@DisplayName("ButtonsView – TestFX UI integration tests")
class ButtonsViewUiTest {

    private Stage             stage;
    private ButtonsController controller;

    // ── JavaFX toolkit guard ─────────────────────────────────────────────────

    @BeforeAll
    static void requireFxToolkit() {
        Assumptions.assumeTrue(
                JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – ButtonsView UI tests skipped");
    }

    // ── Per-test fixture setup ───────────────────────────────────────────────

    @BeforeEach
    void setUp() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            final var loader = new FXMLLoader(App.class.getResource("buttons.fxml"));
            final StackPane root = loader.load();
            controller = loader.getController();
            stage = new Stage();
            stage.setScene(new Scene(root, 260, 44));
            stage.show();
            return null;
        });
        // Flush Platform.runLater() callbacks queued during initialize()
        // (e.g. ButtonsController.updateDynamicSizing).
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
    //  Test 1 – Initial FXML label text
    // =========================================================================

    @Test
    @DisplayName("labelTime shows the FXML default text 'Work  30:00' before any update")
    void labelTimeShowsInitialWorkText() {
        final var label = (Label) stage.getScene().getRoot().lookup("#labelTime");
        assertNotNull(label, "#labelTime node must be present in buttons.fxml");
        assertEquals("Work  30:00", label.getText(),
                "Initial labelTime text must match the value defined in buttons.fxml");
    }

    // =========================================================================
    //  Test 2 – Initial progress bar value
    // =========================================================================

    @Test
    @DisplayName("progressBar starts at progress=0.0 (no timer running yet)")
    void progressBarStartsAtZero() {
        final var bar = (ProgressBar) stage.getScene().getRoot().lookup("#progressBar");
        assertNotNull(bar, "#progressBar node must be present in buttons.fxml");
        assertEquals(0.0, bar.getProgress(), 1e-9,
                "progressBar.progress must be 0.0 before any updateUI() call");
    }

    // =========================================================================
    //  Test 3 – WORK mode (timer running): correct button visibility
    // =========================================================================

    @Test
    @DisplayName("updateUI(WORK, running): btnPause visible, btnPlay hidden, btnWork hidden")
    void workModeRunningShowsCorrectButtons() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.updateUI(
                    NeonPreset.AURORA_DRIFT, NeonGlowProfile.BALANCED,
                    TimerMode.WORK,
                    /*isPaused=*/false, /*isOverTime=*/false,
                    30.0, "Work  15:00", "Start @ 8:00");
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();

        final var root     = stage.getScene().getRoot();
        final var btnPause = (Button) root.lookup("#btnPause");
        final var btnPlay  = (Button) root.lookup("#btnPlay");
        final var btnWork  = (Button) root.lookup("#btnWork");

        assertNotNull(btnPause, "#btnPause not found in buttons.fxml");
        assertNotNull(btnPlay,  "#btnPlay not found in buttons.fxml");
        assertNotNull(btnWork,  "#btnWork not found in buttons.fxml");

        assertTrue(btnPause.isVisible(),
                "btnPause must be visible while the WORK timer is running");
        assertFalse(btnPlay.isVisible(),
                "btnPlay must be hidden while the WORK timer is running (not paused)");
        assertFalse(btnWork.isVisible(),
                "btnWork must be hidden during WORK mode");
    }

    // =========================================================================
    //  Test 4 – WORK mode (paused): btnPlay visible + fire() triggers resume()
    // =========================================================================

    @Test
    @DisplayName("updateUI(WORK, paused): btnPlay visible, btnPause hidden; fire() triggers resume()")
    void workModePausedShowsPlayAndFireTriggersResume() throws Exception {
        // A mock MainController captures the resume() call without starting
        // the full application lifecycle (clock, taskbar helpers, etc.).
        final var mockMc = Mockito.mock(MainController.class);

        JavaFxTestHelper.runOnFxThread(() -> {
            // Put the view into WORK-paused state
            controller.updateUI(
                    NeonPreset.AURORA_DRIFT, NeonGlowProfile.BALANCED,
                    TimerMode.WORK,
                    /*isPaused=*/true, /*isOverTime=*/false,
                    30.0, "Work  15:00", "Start @ 8:00");

            // Attach the mock so button FXML action bindings have a target
            controller.setMainController(mockMc);

            // Minimal interaction: fire the play/resume button
            final var btnPlay = (Button) stage.getScene().getRoot().lookup("#btnPlay");
            assertNotNull(btnPlay, "#btnPlay not found on FX thread");
            btnPlay.fire();
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();

        // ── Visibility assertions ─────────────────────────────────────────────
        final var root     = stage.getScene().getRoot();
        final var btnPause = (Button) root.lookup("#btnPause");
        final var btnPlay  = (Button) root.lookup("#btnPlay");

        assertFalse(btnPause.isVisible(),
                "btnPause must be hidden when the WORK timer is paused");
        assertTrue(btnPlay.isVisible(),
                "btnPlay must be visible when the WORK timer is paused");

        // ── Interaction assertion ─────────────────────────────────────────────
        // btnPlay.fire() dispatches onPlay() → mainController.resume()
        Mockito.verify(mockMc, Mockito.times(1)).resume();
    }
}


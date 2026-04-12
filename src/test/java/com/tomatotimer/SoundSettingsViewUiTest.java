package com.tomatotimer;

import com.tomatotimer.controller.MainController;
import com.tomatotimer.controller.SoundSettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestFX-based UI integration tests for {@code sound_settings.fxml} /
 * {@link SoundSettingsController}.
 *
 * <p>All tests are guarded by {@link JavaFxTestHelper#ensureToolkitStarted()} and
 * skipped automatically in headless / CI environments.</p>
 *
 * <h3>Coverage targets</h3>
 * <ul>
 *   <li>Structural node and label presence.</li>
 *   <li>Open-file buttons are always visible regardless of configured file path.</li>
 *   <li>{@link SoundSettingsController#playSound(SoundType)} is a no-op when no file is set.</li>
 *   <li>Mute-button programmatic {@code fire()} sets the sound path to {@code "Mute"} in settings.</li>
 *   <li>Back button calls {@link MainController#showSettings()}.</li>
 * </ul>
 */
@DisplayName("SoundSettingsView – TestFX UI integration tests")
class SoundSettingsViewUiTest {

    private Stage                  stage;
    private SoundSettingsController controller;

    // ── JavaFX toolkit guard ─────────────────────────────────────────────────

    @BeforeAll
    static void requireFxToolkit() {
        Assumptions.assumeTrue(
                JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – SoundSettingsView UI tests skipped");
    }

    // ── Per-test fixture setup ───────────────────────────────────────────────

    @BeforeEach
    void setUp() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            final var loader = new FXMLLoader(App.class.getResource("sound_settings.fxml"));
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
    @DisplayName("sound_settings.fxml root node is an HBox")
    void rootIsHBox() {
        assertInstanceOf(HBox.class, stage.getScene().getRoot(),
                "The root node in sound_settings.fxml must be an HBox");
    }

    // =========================================================================
    //  Test 2 – btnBack presence
    // =========================================================================

    @Test
    @DisplayName("btnBack is present and is a Button")
    void btnBackIsPresent() {
        final var node = stage.getScene().getRoot().lookup("#btnBack");
        assertNotNull(node, "#btnBack must be present in sound_settings.fxml");
        assertInstanceOf(Button.class, node);
    }

    // =========================================================================
    //  Test 3 – Section labels present
    // =========================================================================

    @Test
    @DisplayName("Section labels are present (Resume / Pause / Work Done / Rest End)")
    void sectionLabelsArePresent() {
        final var root = stage.getScene().getRoot();
        assertNotNull(root.lookup("#lblResume"),   "#lblResume must be present");
        assertNotNull(root.lookup("#lblPause"),    "#lblPause must be present");
        assertNotNull(root.lookup("#lblWorkDone"), "#lblWorkDone must be present");
        assertNotNull(root.lookup("#lblRestEnd"),  "#lblRestEnd must be present");
    }

    // =========================================================================
    //  Test 4 – Section label texts
    // =========================================================================

    @Test
    @DisplayName("Section labels carry the expected text values")
    void sectionLabelTexts() {
        final var root = stage.getScene().getRoot();
        assertEquals("Resume",    ((Label) root.lookup("#lblResume")).getText());
        assertEquals("Pause",     ((Label) root.lookup("#lblPause")).getText());
        assertEquals("Work Done", ((Label) root.lookup("#lblWorkDone")).getText());
        assertEquals("Rest End",  ((Label) root.lookup("#lblRestEnd")).getText());
    }

    // =========================================================================
    //  Test 5 – Open-file buttons always visible
    // =========================================================================

    @Test
    @DisplayName("Open-file buttons are always visible when no sound file is configured")
    void openFileButtonsAlwaysVisible() throws Exception {
        // Calling playSound() with no file path initialises the button arrays (ensureButtons)
        // so that visibility assertions are reliable even before layout height is available.
        JavaFxTestHelper.runOnFxThread(() -> {
            controller.playSound(SoundType.RESUME); // no-op – no sound file configured
            return null;
        });
        WaitForAsyncUtils.waitForFxEvents();

        final var root = stage.getScene().getRoot();
        final var btnResumeOpen = (Button) root.lookup("#btnResumeOpenFile");
        final var btnPauseOpen  = (Button) root.lookup("#btnPauseOpenFile");
        final var btnWorkOpen   = (Button) root.lookup("#btnWorkOpenFile");
        final var btnRestOpen   = (Button) root.lookup("#btnRestOpenFile");

        assertNotNull(btnResumeOpen, "#btnResumeOpenFile must be present");
        assertNotNull(btnPauseOpen,  "#btnPauseOpenFile must be present");
        assertNotNull(btnWorkOpen,   "#btnWorkOpenFile must be present");
        assertNotNull(btnRestOpen,   "#btnRestOpenFile must be present");

        assertTrue(btnResumeOpen.isVisible(), "btnResumeOpenFile must always be visible");
        assertTrue(btnPauseOpen.isVisible(),  "btnPauseOpenFile must always be visible");
        assertTrue(btnWorkOpen.isVisible(),   "btnWorkOpenFile must always be visible");
        assertTrue(btnRestOpen.isVisible(),   "btnRestOpenFile must always be visible");
    }

    // =========================================================================
    //  Test 6 – playSound() with no configured file is a no-op
    // =========================================================================

    @Test
    @DisplayName("playSound() with no configured sound file does not throw for any SoundType")
    void playSoundWithNoFileIsNoOp() {
        for (final var type : SoundType.values()) {
            assertDoesNotThrow(() ->
                    JavaFxTestHelper.runOnFxThread(() -> {
                        controller.playSound(type);
                        return null;
                    })
            );
        }
    }

    // =========================================================================
    //  Test 7 – Mute button fire() sets sound path to "Mute"
    // =========================================================================

    @Test
    @DisplayName("btnWorkMute.fire() sets WORK_DONE sound path to 'Mute' in AppSettings")
    void muteButtonSetsPathToMute() throws Exception {
        final var settings = AppSettings.getInstance();
        final var original = settings.getSoundPath(SoundType.WORK_DONE);
        try {
            // Button.fire() bypasses visibility, so this works even before a file is configured.
            JavaFxTestHelper.runOnFxThread(() -> {
                final var btn = (Button) stage.getScene().getRoot().lookup("#btnWorkMute");
                assertNotNull(btn, "#btnWorkMute not found on FX thread");
                btn.fire();
                return null;
            });
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals("Mute", settings.getSoundPath(SoundType.WORK_DONE),
                    "WORK_DONE sound path must be 'Mute' after mute button fires");
        } finally {
            settings.setSoundPath(SoundType.WORK_DONE, original);
        }
    }

    // =========================================================================
    //  Test 8 – Mute for all SoundTypes via FXML mute buttons
    // =========================================================================

    @Test
    @DisplayName("All mute buttons set the corresponding SoundType path to 'Mute'")
    void allMuteButtonsSetPathToMute() throws Exception {
        final var settings = AppSettings.getInstance();
        // Save originals
        final var origPaths = new String[SoundType.values().length];
        for (final var t : SoundType.values()) origPaths[t.getIndex()] = settings.getSoundPath(t);

        try {
            final String[] muteButtonIds = {
                "#btnResumeMute", "#btnPauseMute", "#btnWorkMute", "#btnRestMute"
            };
            final SoundType[] types = {
                SoundType.RESUME, SoundType.PAUSE, SoundType.WORK_DONE, SoundType.REST_TIMEOUT
            };
            for (int i = 0; i < types.length; i++) {
                final var btnId = muteButtonIds[i];
                JavaFxTestHelper.runOnFxThread(() -> {
                    final var btn = (Button) stage.getScene().getRoot().lookup(btnId);
                    assertNotNull(btn, btnId + " not found on FX thread");
                    btn.fire();
                    return null;
                });
                WaitForAsyncUtils.waitForFxEvents();
                assertEquals("Mute", settings.getSoundPath(types[i]),
                        types[i] + " path must be 'Mute' after " + btnId + " fires");
            }
        } finally {
            for (final var t : SoundType.values()) settings.setSoundPath(t, origPaths[t.getIndex()]);
        }
    }

    // =========================================================================
    //  Test 9 – btnBack.fire() → mainController.showSettings()
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


package com.tomatotimer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * TestFX-based structural UI tests for {@code main.fxml}.
 *
 * <p>These tests load {@code main.fxml} and verify that the key FXML nodes are
 * present with the correct types and IDs.  {@code MainController.init()} is
 * intentionally <em>not</em> called so that the 1-second clock, Windows taskbar
 * helpers, and sub-FXML loading are not triggered here – those are exercised by
 * separate integration tests.
 *
 * <p>All tests are skipped automatically when the JavaFX Glass backend cannot
 * start (headless environments).</p>
 *
 * <h3>Coverage targets</h3>
 * <ul>
 *   <li>{@code rootPane} StackPane identity / fx:id correctness.</li>
 *   <li>Window-control button presence and type ({@link ToggleButton},
 *       {@link MenuButton}).</li>
 *   <li>{@code contentPane} presence (page-swap target).</li>
 * </ul>
 */
@DisplayName("MainView – TestFX structural tests")
class MainViewUiTest {

    private Stage stage;

    // ── JavaFX toolkit guard ─────────────────────────────────────────────────

    @BeforeAll
    static void requireFxToolkit() {
        Assumptions.assumeTrue(
                JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – MainView UI tests skipped");
    }

    // ── Per-test fixture setup ───────────────────────────────────────────────

    @BeforeEach
    void setUp() throws Exception {
        JavaFxTestHelper.runOnFxThread(() -> {
            final var loader = new FXMLLoader(App.class.getResource("main.fxml"));
            final StackPane root = loader.load();

            // Load stylesheet so CSS-dependent lookups work exactly as at runtime,
            // but skip controller.init() to avoid starting the 1-second clock.
            final var scene = new Scene(root, 260, 44);
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            App.class.getResource("style.css"),
                            "style.css not found on classpath"
                    ).toExternalForm());

            stage = new Stage();
            stage.setScene(scene);
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
    //  Test 1 – Root StackPane identity
    // =========================================================================

    @Test
    @DisplayName("Scene root carries fx:id='rootPane' (StackPane)")
    void rootPaneHasCorrectId() {
        final var root = stage.getScene().getRoot();
        assertNotNull(root, "Scene must have a non-null root node");
        assertEquals("rootPane", root.getId(),
                "The root node must carry fx:id='rootPane' as defined in main.fxml");
    }

    // =========================================================================
    //  Test 2 – Always-on-top toggle button
    // =========================================================================

    @Test
    @DisplayName("btnAlwaysOnTop is present and is a ToggleButton")
    void alwaysOnTopButtonIsPresentAndCorrectType() {
        final var node = stage.getScene().getRoot().lookup("#btnAlwaysOnTop");
        assertNotNull(node,
                "#btnAlwaysOnTop must be present in main.fxml");
        assertInstanceOf(ToggleButton.class, node,
                "#btnAlwaysOnTop must be a ToggleButton");
    }

    // =========================================================================
    //  Test 3 – Close menu button
    // =========================================================================

    @Test
    @DisplayName("btnClose is present and is a MenuButton")
    void closeButtonIsPresentAndCorrectType() {
        final var node = stage.getScene().getRoot().lookup("#btnClose");
        assertNotNull(node,
                "#btnClose must be present in main.fxml");
        assertInstanceOf(MenuButton.class, node,
                "#btnClose must be a MenuButton");
    }

    // =========================================================================
    //  Test 4 – Content pane (page-swap target) is present
    // =========================================================================

    @Test
    @DisplayName("contentPane (page-swap StackPane) is present in main.fxml")
    void contentPaneIsPresent() {
        final var node = stage.getScene().getRoot().lookup("#contentPane");
        assertNotNull(node,
                "#contentPane must be present in main.fxml as the page-swap target");
    }
}


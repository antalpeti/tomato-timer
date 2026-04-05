package com.tomatotimer.controller;

import com.tomatotimer.IconFactory;
import com.tomatotimer.NeonGlowProfile;
import com.tomatotimer.NeonPreset;
import com.tomatotimer.TimerBackgroundHelper;
import com.tomatotimer.TimerMode;
import com.tomatotimer.UiScaleHelper;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

/**
 * Controller for buttons.fxml – the main timer face.
 * Equivalent to Page_Buttons.xaml.cs.
 */
public class ButtonsController {

    @FXML private StackPane  rootPane;
    @FXML private ProgressBar progressBar;
    @FXML private Label      labelTime;
    @FXML private Label      labelInfo;
    @FXML private Label      labelTimeSmall;
    @FXML private Button     btnSettings;
    @FXML private HBox       buttonsBox;
    @FXML private Button     btnReset;
    @FXML private Button     btnPlay;
    @FXML private Button     btnPause;
    @FXML private Button     btnWork;
    @FXML private Button     btnFinishWork;
    @FXML private Button     btnRelax;

    private MainController mainController;

    // Long-press tracking for "Long Rest" on the Relax button
    private boolean mouseDown      = false;
    private long    mouseDownEpoch = 0;

    // ── SVG icon references (kept for proportional resize) ───────────────────
    private Group iconSettings;
    private Group iconReset;
    private Group iconPlay;
    private Group iconPause;
    private Group iconWork;
    private Group iconFinishWork;
    private Group iconRelax;

    // =========================================================================
    //  Setup
    // =========================================================================

    public void setMainController(MainController mc) { this.mainController = mc; }

    @FXML
    public void initialize() {
        // ── Assign vivid SVG icons ────────────────────────────────────────────
        iconSettings  = IconFactory.create(IconFactory.PATH_SETTINGS, IconFactory.COLOR_SETTINGS);
        iconReset     = IconFactory.create(IconFactory.PATH_RESET,    IconFactory.COLOR_RESET);
        iconPlay      = IconFactory.create(IconFactory.PATH_PLAY,     IconFactory.COLOR_PLAY);
        iconPause     = IconFactory.create(IconFactory.PATH_PAUSE,    IconFactory.COLOR_PAUSE);
        iconWork      = IconFactory.create(IconFactory.PATH_WORK,     IconFactory.COLOR_WORK);
        iconFinishWork= IconFactory.create(IconFactory.PATH_CALENDAR, IconFactory.COLOR_CALENDAR);
        iconRelax     = IconFactory.create(IconFactory.PATH_RELAX,    IconFactory.COLOR_RELAX);

        btnSettings.setGraphic(iconSettings);
        btnReset.setGraphic(iconReset);
        btnPlay.setGraphic(iconPlay);
        btnPause.setGraphic(iconPause);
        btnWork.setGraphic(iconWork);
        btnFinishWork.setGraphic(iconFinishWork);
        btnRelax.setGraphic(iconRelax);

        // Controls are hidden until the mouse enters
        setOpacity(0, buttonsBox, btnSettings, labelTimeSmall);

        rootPane.setOnMouseEntered(e -> showControls());
        rootPane.setOnMouseExited (e -> hideControls());

        rootPane.widthProperty().addListener((obs, oldV, newV) -> updateDynamicSizing());
        rootPane.heightProperty().addListener((obs, oldV, newV) -> updateDynamicSizing());
        Platform.runLater(this::updateDynamicSizing);
    }

    // =========================================================================
    //  Show / hide overlay controls
    // =========================================================================

    private void showControls() {
        fade(1.0, 200, buttonsBox, btnSettings, labelTimeSmall);
        fade(0.0, 200, labelTime);
    }

    private void hideControls() {
        fade(0.0, 200, buttonsBox, btnSettings, labelTimeSmall);
        fade(1.0, 200, labelTime);
    }

    private void fade(double to, double ms, Node... nodes) {
        for (Node n : nodes) {
            FadeTransition ft = new FadeTransition(Duration.millis(ms), n);
            ft.setToValue(to);
            ft.play();
        }
    }

    private void setOpacity(double v, Node... nodes) {
        for (Node n : nodes) n.setOpacity(v);
    }

    // =========================================================================
    //  UI update (called from MainController every second)
    // =========================================================================

    public void updateUI(NeonPreset preset, NeonGlowProfile profile,
                         TimerMode mode, boolean isPaused, boolean isOverTime,
                         double progressPct, String timeStr, String infoStr) {

        // ---- Root-pane ambient gradient (preset + profile + state + mode aware)
        rootPane.setStyle("-fx-background-color: " +
                TimerBackgroundHelper.computeRootGradientCss(
                        preset, profile, mode, progressPct, isPaused, isOverTime) + ";");

        // ---- Progress bar value ---------------------------------------------
        if (isPaused) {
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        } else if (isOverTime) {
            progressBar.setProgress(1.0);
        } else {
            progressBar.setProgress(progressPct / 100.0);
        }

        // ---- Progress-bar fallback accent (used before sub-node lookup succeeds)
        final Color accent = TimerBackgroundHelper.computeAccentColor(
                preset, mode, progressPct, isPaused, isOverTime);
        progressBar.setStyle("-fx-accent: " + TimerBackgroundHelper.toCssHex(accent) + ";");

        // ---- .bar sub-node: vertical 3-D gradient fill ----------------------
        final Node bar = progressBar.lookup(".bar");
        if (bar != null) {
            bar.setStyle(TimerBackgroundHelper.computeBarCss(
                    preset, profile, mode, progressPct, isPaused, isOverTime));
        }

        // ---- .track sub-node: near-transparent overlay ----------------------
        final Node track = progressBar.lookup(".track");
        if (track != null) {
            track.setStyle(TimerBackgroundHelper.computeTrackCss());
        }

        // ---- Labels ---------------------------------------------------------
        labelTime.setText(timeStr);
        labelTimeSmall.setText(timeStr);
        labelInfo.setText(infoStr);

        if (isPaused) labelTime.setOpacity(0.5); else labelTime.setOpacity(1.0);

        // ---- Button visibility ----------------------------------------------
        if (mode == TimerMode.WORK) {
            setVisible(false, btnWork);
            setVisible(true,  btnFinishWork, btnRelax);
            if (isPaused) {
                setVisible(false, btnPause);
                setVisible(true,  btnPlay);
            } else {
                setVisible(true,  btnPause);
                setVisible(false, btnPlay);
            }
        } else {
            setVisible(true,  btnWork);
            setVisible(false, btnFinishWork, btnRelax, btnPause, btnPlay);
        }
    }

    private void setVisible(boolean v, Node... nodes) {
        for (Node n : nodes) { n.setVisible(v); n.setManaged(v); }
    }

    private void updateDynamicSizing() {
        final double width  = rootPane.getWidth();
        final double height = rootPane.getHeight();
        if (width <= 0 || height <= 0) return;

        // ── Font sizes via UiScaleHelper ──────────────────────────────────────
        labelTime.setStyle(String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.mainTimeFontPx(width, height)));
        labelTimeSmall.setStyle(String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.smallTimeFontPx(width, height)));
        labelInfo.setStyle(String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.infoFontPx(width, height)));

        // ── Proportional icon scaling ─────────────────────────────────────────
        final double iconSize = UiScaleHelper.mainIconPx(width, height);
        for (final var icon : List.of(iconSettings, iconReset, iconPlay, iconPause, iconWork, iconFinishWork, iconRelax)) {
            IconFactory.resize(icon, iconSize);
        }
    }


    // =========================================================================
    //  Button handlers
    // =========================================================================

    @FXML private void onSettings() { mainController.showSettings(); }
    @FXML private void onReset()    { mainController.reset(); }
    @FXML private void onPlay()     { mainController.resume(); }
    @FXML private void onPause()    { mainController.pause(); }
    @FXML private void onWork()     { mainController.startWork(); }

    /** Finishes WORK early: creates a GCal event (when enabled) and starts a short REST. */
    @FXML private void onFinishWork() { mainController.finishWork(); }

    @FXML
    private void onRelaxPressed() {
        mouseDown      = true;
        mouseDownEpoch = System.currentTimeMillis();
    }

    @FXML
    private void onRelax() {
        boolean longPress = mouseDown && (System.currentTimeMillis() - mouseDownEpoch) >= 2000;
        mainController.startRelax(longPress);
        mouseDown = false;
    }

    @FXML
    private void onMouseReleased() { mouseDown = false; }
}

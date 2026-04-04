package com.tomatotimer.controller;

import com.tomatotimer.IconFactory;
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
    private Group iconRelax;

    // =========================================================================
    //  Setup
    // =========================================================================

    public void setMainController(MainController mc) { this.mainController = mc; }

    @FXML
    public void initialize() {
        // ── Assign vivid SVG icons ────────────────────────────────────────────
        iconSettings = IconFactory.create(IconFactory.PATH_SETTINGS, IconFactory.COLOR_SETTINGS);
        iconReset    = IconFactory.create(IconFactory.PATH_RESET,    IconFactory.COLOR_RESET);
        iconPlay     = IconFactory.create(IconFactory.PATH_PLAY,     IconFactory.COLOR_PLAY);
        iconPause    = IconFactory.create(IconFactory.PATH_PAUSE,    IconFactory.COLOR_PAUSE);
        iconWork     = IconFactory.create(IconFactory.PATH_WORK,     IconFactory.COLOR_WORK);
        iconRelax    = IconFactory.create(IconFactory.PATH_RELAX,    IconFactory.COLOR_RELAX);

        btnSettings.setGraphic(iconSettings);
        btnReset.setGraphic(iconReset);
        btnPlay.setGraphic(iconPlay);
        btnPause.setGraphic(iconPause);
        btnWork.setGraphic(iconWork);
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

    public void updateUI(TimerMode mode, boolean isPaused, boolean isOverTime,
                         double progressPct, String timeStr, String infoStr) {

        // ---- Progress bar accent colour (matches icon palette) ---------------
        if (isPaused)
            progressBar.setStyle("-fx-accent: " + IconFactory.COLOR_SETTINGS + ";");
        else if (isOverTime)
            progressBar.setStyle("-fx-accent: " + IconFactory.COLOR_WORK + ";");
        else if (progressPct > 80)
            progressBar.setStyle("-fx-accent: " + IconFactory.COLOR_PAUSE + ";");
        else
            progressBar.setStyle("-fx-accent: " + IconFactory.COLOR_PLAY + ";");

        // ---- Labels ---------------------------------------------------------
        labelTime.setText(timeStr);
        labelTimeSmall.setText(timeStr);
        labelInfo.setText(infoStr);

        if (isPaused) labelTime.setOpacity(0.5); else labelTime.setOpacity(1.0);

        // ---- Button visibility ---------------------------------------------
        if (mode == TimerMode.WORK) {
            setVisible(false, btnWork);
            setVisible(true,  btnRelax);
            if (isPaused) {
                setVisible(false, btnPause);
                setVisible(true,  btnPlay);
            } else {
                setVisible(true,  btnPause);
                setVisible(false, btnPlay);
            }
        } else {
            setVisible(true,  btnWork);
            setVisible(false, btnRelax, btnPause, btnPlay);
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
        for (final var icon : List.of(iconSettings, iconReset, iconPlay, iconPause, iconWork, iconRelax)) {
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

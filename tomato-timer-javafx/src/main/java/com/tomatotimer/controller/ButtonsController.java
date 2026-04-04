package com.tomatotimer.controller;

import com.tomatotimer.TimerMode;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

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

    // =========================================================================
    //  Setup
    // =========================================================================

    public void setMainController(MainController mc) { this.mainController = mc; }

    @FXML
    public void initialize() {
        // Controls are hidden until the mouse enters
        setOpacity(0, buttonsBox, btnSettings, labelTimeSmall);

        rootPane.setOnMouseEntered(e -> showControls());
        rootPane.setOnMouseExited (e -> hideControls());
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

        // ---- Progress bar ---------------------------------------------------
        progressBar.setProgress(progressPct / 100.0);
        if (isPaused)
            progressBar.setStyle("-fx-accent: #4CAF50;");
        else if (isOverTime)
            progressBar.setStyle("-fx-accent: #f44336;");
        else if (progressPct > 80)
            progressBar.setStyle("-fx-accent: #FFEB3B;");
        else
            progressBar.setStyle("-fx-accent: #4CAF50;");

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


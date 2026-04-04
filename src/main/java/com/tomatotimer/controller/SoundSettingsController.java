package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import com.tomatotimer.IconFactory;
import com.tomatotimer.SoundType;
import com.tomatotimer.UiScaleHelper;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Controller for sound_settings.fxml – equivalent to Page_SoundSettings.xaml.cs.
 */
public class SoundSettingsController {

    // Back navigation
    @FXML private HBox   rootBox;
    @FXML private HBox   soundRow;
    @FXML private Button btnBack;

    // Section labels
    @FXML private Label lblResume;
    @FXML private Label lblPause;
    @FXML private Label lblWorkDone;
    @FXML private Label lblRestEnd;

    // Resume row
    @FXML private Button btnResumePlay;
    @FXML private Button btnResumeStop;
    @FXML private Button btnResumeMute;
    @FXML private Button btnResumeOpenFile;

    // Pause row
    @FXML private Button btnPausePlay;
    @FXML private Button btnPauseStop;
    @FXML private Button btnPauseMute;
    @FXML private Button btnPauseOpenFile;

    // Work Done row
    @FXML private Button btnWorkPlay;
    @FXML private Button btnWorkStop;
    @FXML private Button btnWorkMute;
    @FXML private Button btnWorkOpenFile;

    // Rest Timeout row
    @FXML private Button btnRestPlay;
    @FXML private Button btnRestStop;
    @FXML private Button btnRestMute;
    @FXML private Button btnRestOpenFile;

    private MainController mainController;
    private AppSettings    settings = AppSettings.getInstance();
    private MediaPlayer    mediaPlayer;

    // ── SVG icon references ───────────────────────────────────────────────────
    private Group iconBack;

    /** buttons[soundType.index] = { play, stop, mute, openFile } */
    private Button[][] buttons;

    public void setMainController(MainController mc) { this.mainController = mc; }

    @FXML
    public void initialize() {
        // ── Assign vivid SVG icon for back button ─────────────────────────────
        iconBack = IconFactory.create(IconFactory.PATH_CLOCK, IconFactory.COLOR_BACK);
        btnBack.setGraphic(iconBack);

        // Resize all scalable elements once laid out, and whenever height changes
        rootBox.heightProperty().addListener((obs, ov, nv) -> updateDynamicSizing(nv.doubleValue()));
        javafx.application.Platform.runLater(() -> updateDynamicSizing(rootBox.getHeight()));
        // Deferred – see ensureButtons()
    }

    private void ensureButtons() {
        if (buttons == null) {
            buttons = new Button[][] {
                { btnResumePlay, btnResumeStop, btnResumeMute, btnResumeOpenFile },
                { btnPausePlay,  btnPauseStop,  btnPauseMute,  btnPauseOpenFile  },
                { btnWorkPlay,   btnWorkStop,   btnWorkMute,   btnWorkOpenFile   },
                { btnRestPlay,   btnRestStop,   btnRestMute,   btnRestOpenFile   }
            };
            for (SoundType t : SoundType.values()) syncUIForType(t);
        }
    }

    /**
     * Applies all dynamic scaling for the sound-settings page based on the current
     * container height. Called on every height change and once after first layout.
     */
    private void updateDynamicSizing(double height) {
        final double eff = Math.max(height, UiScaleHelper.REF_HEIGHT);

        // ── Back icon ─────────────────────────────────────────────────────────
        IconFactory.resize(iconBack, UiScaleHelper.navIconPx(eff));

        // ── Section labels ────────────────────────────────────────────────────
        final String lblStyle = String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.soundLabelFontPx(eff));
        lblResume.setStyle(lblStyle);
        lblPause.setStyle(lblStyle);
        lblWorkDone.setStyle(lblStyle);
        lblRestEnd.setStyle(lblStyle);

        // ── Small buttons (font + min-size) ───────────────────────────────────
        ensureButtons();
        final String btnStyle = String.format(
                "-fx-font-size: %.1fpx; -fx-min-width: %.1f; -fx-min-height: %.1f;",
                UiScaleHelper.smallBtnFontPx(eff),
                UiScaleHelper.smallBtnMinWidthPx(eff),
                UiScaleHelper.smallBtnMinHeightPx(eff));
        for (final Button[] row : buttons) {
            for (final Button btn : row) {
                btn.setStyle(btnStyle);
            }
        }

        // ── Row spacing / padding ─────────────────────────────────────────────
        soundRow.setSpacing(UiScaleHelper.soundGroupSpacingPx(eff));
        soundRow.setStyle(UiScaleHelper.rowPaddingStyle(eff));

        // ── VBox and inner-HBox spacing ───────────────────────────────────────
        for (final Node child : soundRow.getChildren()) {
            if (child instanceof VBox vbox) {
                vbox.setSpacing(UiScaleHelper.soundVboxSpacingPx(eff));
                for (final Node vchild : vbox.getChildren()) {
                    if (vchild instanceof HBox hbox) {
                        hbox.setSpacing(UiScaleHelper.soundBtnRowSpacingPx(eff));
                    }
                }
            }
        }
    }

    // =========================================================================
    //  State helpers
    // =========================================================================

    private boolean hasSoundFile(SoundType type) {
        String path = settings.getSoundPath(type);
        return !path.isEmpty() && !path.equals("Mute") && new File(path).exists();
    }

    private void syncUIForType(SoundType type) {
        ensureButtons();
        int i = type.getIndex();
        boolean hasFile = hasSoundFile(type);
        buttons[i][0].setVisible(hasFile);   // play
        buttons[i][1].setVisible(false);     // stop – only while playing
        buttons[i][2].setVisible(hasFile);   // mute
        buttons[i][3].setVisible(true);      // open file – always
    }

    // =========================================================================
    //  Public API used by MainController
    // =========================================================================

    public void playSound(SoundType type) {
        ensureButtons();
        if (!hasSoundFile(type)) return;
        String path = settings.getSoundPath(type);
        try {
            if (mediaPlayer != null) mediaPlayer.stop();
            mediaPlayer = new MediaPlayer(new Media(new File(path).toURI().toString()));
            mediaPlayer.setOnEndOfMedia(this::onPlaybackEnded);
            mediaPlayer.play();
            // Show Stop buttons for all rows while playing
            for (Button[] row : buttons) {
                row[0].setVisible(false);
                row[1].setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPlaybackEnded() {
        for (SoundType t : SoundType.values()) syncUIForType(t);
    }

    // =========================================================================
    //  Private helpers
    // =========================================================================

    private void stopPlayback() {
        if (mediaPlayer != null) mediaPlayer.stop();
        for (SoundType t : SoundType.values()) syncUIForType(t);
    }

    private void muteType(SoundType type) {
        if (mediaPlayer != null) mediaPlayer.stop();
        settings.setSoundPath(type, "Mute");
        syncUIForType(type);
    }

    private void openFileForType(SoundType type) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select sound file");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Sound files", "*.mp3", "*.wav", "*.ogg", "*.wma"));
        File file = (mainController != null)
                ? fc.showOpenDialog(mainController.getStage())
                : fc.showOpenDialog(null);
        if (file != null) {
            settings.setSoundPath(type, file.getAbsolutePath());
            syncUIForType(type);
            playSound(type);
        }
    }

    // =========================================================================
    //  FXML handlers
    // =========================================================================

    @FXML private void onResumePlay()     { playSound(SoundType.RESUME); }
    @FXML private void onResumeStop()     { stopPlayback(); }
    @FXML private void onResumeMute()     { muteType(SoundType.RESUME); }
    @FXML private void onResumeOpenFile() { openFileForType(SoundType.RESUME); }

    @FXML private void onPausePlay()      { playSound(SoundType.PAUSE); }
    @FXML private void onPauseStop()      { stopPlayback(); }
    @FXML private void onPauseMute()      { muteType(SoundType.PAUSE); }
    @FXML private void onPauseOpenFile()  { openFileForType(SoundType.PAUSE); }

    @FXML private void onWorkPlay()       { playSound(SoundType.WORK_DONE); }
    @FXML private void onWorkStop()       { stopPlayback(); }
    @FXML private void onWorkMute()       { muteType(SoundType.WORK_DONE); }
    @FXML private void onWorkOpenFile()   { openFileForType(SoundType.WORK_DONE); }

    @FXML private void onRestPlay()       { playSound(SoundType.REST_TIMEOUT); }
    @FXML private void onRestStop()       { stopPlayback(); }
    @FXML private void onRestMute()       { muteType(SoundType.REST_TIMEOUT); }
    @FXML private void onRestOpenFile()   { openFileForType(SoundType.REST_TIMEOUT); }

    @FXML
    private void onBack() {
        if (mediaPlayer != null) mediaPlayer.stop();
        mainController.showSettings();
    }
}


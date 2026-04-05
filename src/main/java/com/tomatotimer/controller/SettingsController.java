package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import com.tomatotimer.IconFactory;
import com.tomatotimer.NeonPreset;
import com.tomatotimer.UiScaleHelper;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;

/**
 * Controller for settings.fxml – equivalent to Page_Settings.xaml.cs.
 */
public class SettingsController {

    @FXML private HBox             rootBox;
    @FXML private HBox             settingsRow;
    @FXML private Button           btnBack;
    @FXML private Button           btnTestGCal;
    @FXML private Button           btnSoundSettings;
    @FXML private Button           btnTaskbarSettings;
    @FXML private Spinner<Integer> spWorkTime;
    @FXML private Spinner<Integer> spRelaxTime;
    @FXML private Spinner<Integer> spLongRelaxTime;
    @FXML private Label            lblWork;
    @FXML private Label            lblRest;
    @FXML private Label            lblLong;
    @FXML private Label            lblPreset;
    @FXML private ComboBox<NeonPreset> cbNeonPreset;
    @FXML private CheckBox         cbEnableGCal;
    @FXML private CheckBox         cbCopyToClipboard;
    @FXML private TextField        tfGCalSrc;
    @FXML private TextField        tfGCalText;
    @FXML private GridPane         gpGCalDetails;
    @FXML private Label            labelVersion;

    private MainController mainController;
    private AppSettings    settings = AppSettings.getInstance();

    // ── SVG icon references ───────────────────────────────────────────────────
    private Group iconBack;
    private Group iconCalendar;
    private Group iconVolume;
    private Group iconTaskbar;

    public void setMainController(MainController mc) { this.mainController = mc; }

    @FXML
    public void initialize() {
        // ── Assign vivid SVG icons ────────────────────────────────────────────
        iconBack     = IconFactory.create(IconFactory.PATH_CLOCK,    IconFactory.COLOR_BACK);
        iconCalendar = IconFactory.create(IconFactory.PATH_CALENDAR, IconFactory.COLOR_CALENDAR);
        iconVolume   = IconFactory.create(IconFactory.PATH_VOLUME,   IconFactory.COLOR_VOLUME);
        iconTaskbar  = IconFactory.create(IconFactory.PATH_TASKBAR,  IconFactory.COLOR_TASKBAR);

        btnBack.setGraphic(iconBack);
        btnTestGCal.setGraphic(iconCalendar);
        btnSoundSettings.setGraphic(iconVolume);
        btnTaskbarSettings.setGraphic(iconTaskbar);

        // Resize all scalable elements once laid out, and whenever height changes
        rootBox.heightProperty().addListener((obs, ov, nv) -> updateDynamicSizing(nv.doubleValue()));
        javafx.application.Platform.runLater(() -> updateDynamicSizing(rootBox.getHeight()));

        // Spinners: value-factory is defined in FXML; hook change-listeners here
        spWorkTime.valueProperty().addListener((o, ov, nv) -> settings.setWorkTime(nv));
        spRelaxTime.valueProperty().addListener((o, ov, nv) -> settings.setRelaxTime(nv));
        spLongRelaxTime.valueProperty().addListener((o, ov, nv) -> settings.setRelaxTimeLong(nv));

        // Neon preset ComboBox – populate, set current value, apply immediately on change
        cbNeonPreset.getItems().addAll(NeonPreset.values());
        cbNeonPreset.setValue(settings.getNeonPreset());
        cbNeonPreset.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                settings.setNeonPreset(nv);
                // Trigger an immediate re-render so the timer face updates in the background
                if (mainController != null) mainController.updateUI();
            }
        });

        String ver = getClass().getPackage().getImplementationVersion();
        if (labelVersion != null)
            labelVersion.setText("v" + (ver != null ? ver : "1.0.0"));
    }

    /**
     * Applies all dynamic scaling for the settings page based on the current
     * container height. Called on every height change and once after first layout.
     */
    private void updateDynamicSizing(double height) {
        final double eff = Math.max(height, UiScaleHelper.REF_HEIGHT);

        // ── Icons ─────────────────────────────────────────────────────────────
        final double navSz = UiScaleHelper.navIconPx(eff);
        IconFactory.resize(iconBack,     navSz);
        IconFactory.resize(iconCalendar, navSz * 0.88);
        IconFactory.resize(iconVolume,   navSz * 0.88);
        IconFactory.resize(iconTaskbar,  navSz * 0.88);

        // ── Setting labels (Work / Rest / Long / Theme) ───────────────────────────
        final String lblStyle = String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.settingLabelFontPx(eff));
        lblWork.setStyle(lblStyle);
        lblRest.setStyle(lblStyle);
        lblLong.setStyle(lblStyle);
        lblPreset.setStyle(lblStyle);

        // ── CheckBox labels ───────────────────────────────────────────────────
        cbEnableGCal.setStyle(lblStyle);
        cbCopyToClipboard.setStyle(lblStyle);

        // ── Version label ─────────────────────────────────────────────────────
        labelVersion.setStyle(String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.versionLabelFontPx(eff)));

        // ── Spinners ──────────────────────────────────────────────────────────
        final double spinW = UiScaleHelper.spinnerWidthPx(eff);
        spWorkTime.setPrefWidth(spinW);
        spRelaxTime.setPrefWidth(spinW);
        spLongRelaxTime.setPrefWidth(spinW);

        // ── Preset ComboBox – proportional width, min 90 px ──────────────────
        cbNeonPreset.setPrefWidth(UiScaleHelper.clamp(spinW * 2.2, 90.0, 140.0));

        // ── Row spacing and padding ───────────────────────────────────────────
        settingsRow.setSpacing(UiScaleHelper.settingsSpacingPx(eff));
        settingsRow.setStyle(UiScaleHelper.rowPaddingStyle(eff));
    }

    /** Load current settings into UI controls. */
    public void syncFromSettings() {
        spWorkTime.getValueFactory().setValue(settings.getWorkTime());
        spRelaxTime.getValueFactory().setValue(settings.getRelaxTime());
        spLongRelaxTime.getValueFactory().setValue(settings.getRelaxTimeLong());

        cbNeonPreset.setValue(settings.getNeonPreset());

        tfGCalSrc.setText(settings.getGCalSrc());
        tfGCalText.setText(settings.getGCalText());
        cbEnableGCal.setSelected(settings.isGCalEnable());
        cbCopyToClipboard.setSelected(settings.isGCalCopyToClipboard());
        updateGCalControls();
    }

    /** Push current UI values into settings. */
    private void syncToSettings() {
        settings.setWorkTime(spWorkTime.getValue());
        settings.setRelaxTime(spRelaxTime.getValue());
        settings.setRelaxTimeLong(spLongRelaxTime.getValue());
        if (cbNeonPreset.getValue() != null) settings.setNeonPreset(cbNeonPreset.getValue());
        settings.setGCalSrc(tfGCalSrc.getText());
        settings.setGCalText(tfGCalText.getText());
        settings.setGCalEnable(cbEnableGCal.isSelected());
        settings.setGCalCopyToClipboard(cbCopyToClipboard.isSelected());
    }

    private void updateGCalControls() {
        boolean en = cbEnableGCal.isSelected();
        gpGCalDetails.setDisable(!en);
    }

    // =========================================================================
    //  FXML handlers
    // =========================================================================

    @FXML
    private void onBack() {
        syncToSettings();
        mainController.showButtons();
    }

    @FXML
    private void onGCalEnableChanged() {
        updateGCalControls();
        syncToSettings();
    }

    @FXML
    private void onSoundSettings() {
        syncToSettings();
        mainController.showSoundSettings();
    }

    @FXML
    private void onTaskbarSettings() {
        syncToSettings();
        mainController.showTaskbarSettings();
    }

    @FXML
    private void onTestGCal() {
        syncToSettings();
        mainController.openGoogleCalendar(
                LocalDateTime.now().minusMinutes(settings.getWorkTime()),
                LocalDateTime.now());
    }
}

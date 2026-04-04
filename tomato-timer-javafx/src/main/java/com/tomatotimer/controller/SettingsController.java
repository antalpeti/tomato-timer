package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;

/**
 * Controller for settings.fxml – equivalent to Page_Settings.xaml.cs.
 */
public class SettingsController {

    @FXML private Spinner<Integer> spWorkTime;
    @FXML private Spinner<Integer> spRelaxTime;
    @FXML private Spinner<Integer> spLongRelaxTime;
    @FXML private CheckBox         cbEnableGCal;
    @FXML private CheckBox         cbCopyToClipboard;
    @FXML private TextField        tfGCalSrc;
    @FXML private TextField        tfGCalText;
    @FXML private GridPane         gpGCalDetails;
    @FXML private Label            labelVersion;

    private MainController mainController;
    private AppSettings    settings = AppSettings.getInstance();

    public void setMainController(MainController mc) { this.mainController = mc; }

    @FXML
    public void initialize() {
        // Spinners: value-factory is defined in FXML; hook change-listeners here
        spWorkTime.valueProperty().addListener((o, ov, nv) -> settings.setWorkTime(nv));
        spRelaxTime.valueProperty().addListener((o, ov, nv) -> settings.setRelaxTime(nv));
        spLongRelaxTime.valueProperty().addListener((o, ov, nv) -> settings.setRelaxTimeLong(nv));

        String ver = getClass().getPackage().getImplementationVersion();
        if (labelVersion != null)
            labelVersion.setText("v" + (ver != null ? ver : "1.0.0"));
    }

    /** Load current settings into UI controls. */
    public void syncFromSettings() {
        spWorkTime.getValueFactory().setValue(settings.getWorkTime());
        spRelaxTime.getValueFactory().setValue(settings.getRelaxTime());
        spLongRelaxTime.getValueFactory().setValue(settings.getRelaxTimeLong());

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
    private void onTestGCal() {
        syncToSettings();
        mainController.openGoogleCalendar(
                LocalDateTime.now().minusMinutes(settings.getWorkTime()),
                LocalDateTime.now());
    }
}


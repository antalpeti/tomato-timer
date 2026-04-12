package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import com.tomatotimer.IconFactory;
import com.tomatotimer.NeonGlowProfile;
import com.tomatotimer.NeonPreset;
import com.tomatotimer.ThemeSelectionMode;
import com.tomatotimer.UiScaleHelper;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


/**
 * Controller for settings.fxml – equivalent to Page_Settings.xaml.cs.
 */
public class SettingsController {

    @FXML private HBox             rootBox;
    @FXML private HBox             settingsRow;
    @FXML private Button           btnBack;
    @FXML private Button           btnCalendarSettings;
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
    @FXML private Label            lblGlow;
    @FXML private ComboBox<NeonGlowProfile> cbNeonGlowProfile;
    @FXML private Label            lblSelection;
    @FXML private ComboBox<ThemeSelectionMode> cbThemeSelectionMode;
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
        btnCalendarSettings.setGraphic(iconCalendar);
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

        // Neon glow profile ComboBox – populate, set current value, apply immediately on change
        cbNeonGlowProfile.getItems().addAll(NeonGlowProfile.values());
        cbNeonGlowProfile.setValue(settings.getNeonGlowProfile());
        cbNeonGlowProfile.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                settings.setNeonGlowProfile(nv);
                if (mainController != null) mainController.updateUI();
            }
        });

        // Theme selection mode ComboBox – populate, set saved value, persist on change
        cbThemeSelectionMode.getItems().addAll(ThemeSelectionMode.values());
        cbThemeSelectionMode.setValue(settings.getThemeSelectionMode());
        cbThemeSelectionMode.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null) settings.setThemeSelectionMode(nv);
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

        // ── Setting labels (Work / Rest / Long / Theme / Glow / Selection) ──────────
        final String lblStyle = String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.settingLabelFontPx(eff));
        lblWork.setStyle(lblStyle);
        lblRest.setStyle(lblStyle);
        lblLong.setStyle(lblStyle);
        lblPreset.setStyle(lblStyle);
        lblGlow.setStyle(lblStyle);
        lblSelection.setStyle(lblStyle);


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

        // ── Glow Profile ComboBox – slightly narrower (3 short values) ───────
        cbNeonGlowProfile.setPrefWidth(UiScaleHelper.clamp(spinW * 1.8, 80.0, 110.0));

        // ── Selection mode ComboBox – similar width to Glow (4 short values) ─
        cbThemeSelectionMode.setPrefWidth(UiScaleHelper.clamp(spinW * 1.8, 80.0, 115.0));

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
        cbNeonGlowProfile.setValue(settings.getNeonGlowProfile());
        cbThemeSelectionMode.setValue(settings.getThemeSelectionMode());
    }

    /**
     * Syncs only the preset ComboBox from persisted settings.
     * Called by {@link MainController} when the theme advances via the selection mode
     * (Sequential / Random / Shuffle) so the combo box stays in sync even while the
     * settings panel is visible.
     */
    public void syncPresetComboBox() {
        cbNeonPreset.setValue(settings.getNeonPreset());
    }

    /** Push current UI values into settings. */
    private void syncToSettings() {
        settings.setWorkTime(spWorkTime.getValue());
        settings.setRelaxTime(spRelaxTime.getValue());
        settings.setRelaxTimeLong(spLongRelaxTime.getValue());
        if (cbNeonPreset.getValue() != null)          settings.setNeonPreset(cbNeonPreset.getValue());
        if (cbNeonGlowProfile.getValue() != null)     settings.setNeonGlowProfile(cbNeonGlowProfile.getValue());
        if (cbThemeSelectionMode.getValue() != null)  settings.setThemeSelectionMode(cbThemeSelectionMode.getValue());
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
    private void onCalendarSettings() {
        syncToSettings();
        mainController.showCalendarSettings();
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
}

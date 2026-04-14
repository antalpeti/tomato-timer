package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import com.tomatotimer.IconFactory;
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
    @FXML private Button           btnThemeSettings;
    @FXML private Button           btnCalendarSettings;
    @FXML private Button           btnSoundSettings;
    @FXML private Button           btnTaskbarSettings;
    @FXML private Spinner<Integer> spWorkTime;
    @FXML private Spinner<Integer> spRelaxTime;
    @FXML private Spinner<Integer> spLongRelaxTime;
    @FXML private Label            lblWork;
    @FXML private Label            lblRest;
    @FXML private Label            lblLong;
    @FXML private Label            labelVersion;

    private MainController mainController;
    private AppSettings    settings = AppSettings.getInstance();

    // ── SVG icon references ───────────────────────────────────────────────────
    private Group iconBack;
    private Group iconTheme;
    private Group iconCalendar;
    private Group iconVolume;
    private Group iconTaskbar;

    public void setMainController(MainController mc) { this.mainController = mc; }

    @FXML
    public void initialize() {
        // ── Assign vivid SVG icons ────────────────────────────────────────────
        iconBack     = IconFactory.create(IconFactory.PATH_CLOCK,    IconFactory.COLOR_BACK);
        iconTheme    = IconFactory.create(IconFactory.PATH_PALETTE,  IconFactory.COLOR_THEME);
        iconCalendar = IconFactory.create(IconFactory.PATH_CALENDAR, IconFactory.COLOR_CALENDAR);
        iconVolume   = IconFactory.create(IconFactory.PATH_VOLUME,   IconFactory.COLOR_VOLUME);
        iconTaskbar  = IconFactory.create(IconFactory.PATH_TASKBAR,  IconFactory.COLOR_TASKBAR);

        btnBack.setGraphic(iconBack);
        btnThemeSettings.setGraphic(iconTheme);
        btnCalendarSettings.setGraphic(iconCalendar);
        btnSoundSettings.setGraphic(iconVolume);
        btnTaskbarSettings.setGraphic(iconTaskbar);

        // Resize all scalable elements once laid out, and whenever height changes
        rootBox.heightProperty().addListener((obs, ov, nv) -> updateDynamicSizing(nv.doubleValue()));
        javafx.application.Platform.runLater(() -> updateDynamicSizing(rootBox.getHeight()));

        // ── Pre-populate spinners from AppSettings BEFORE attaching change-listeners.
        //    This ensures that even if syncToSettings() is called (e.g. via onBack / onThemeSettings
        //    etc.) before an explicit syncFromSettings() call, the spinner values already hold the
        //    correct persisted values rather than the FXML stub initialValue.  Without this guard
        //    the FXML stubs (work=30, relax=5, etc.) would be written to java.util.prefs.Preferences
        //    and therefore to the Windows Registry, silently overriding the migration defaults.
        syncFromSettings();

        // Spinners: value-factory is defined in FXML; hook change-listeners here
        spWorkTime.valueProperty().addListener((o, ov, nv) -> settings.setWorkTime(nv));
        spRelaxTime.valueProperty().addListener((o, ov, nv) -> settings.setRelaxTime(nv));
        spLongRelaxTime.valueProperty().addListener((o, ov, nv) -> settings.setRelaxTimeLong(nv));


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
        IconFactory.resize(iconTheme,    navSz * 0.88);
        IconFactory.resize(iconCalendar, navSz * 0.88);
        IconFactory.resize(iconVolume,   navSz * 0.88);
        IconFactory.resize(iconTaskbar,  navSz * 0.88);

        // ── Setting labels (Work / Rest / Long) ───────────────────────────────
        final String lblStyle = String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.settingLabelFontPx(eff));
        lblWork.setStyle(lblStyle);
        lblRest.setStyle(lblStyle);
        lblLong.setStyle(lblStyle);

        // ── Version label ─────────────────────────────────────────────────────
        labelVersion.setStyle(String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.versionLabelFontPx(eff)));

        // ── Spinners ──────────────────────────────────────────────────────────
        final double spinW = UiScaleHelper.spinnerWidthPx(eff);
        spWorkTime.setPrefWidth(spinW);
        spRelaxTime.setPrefWidth(spinW);
        spLongRelaxTime.setPrefWidth(spinW);


        // ── Row spacing and padding ───────────────────────────────────────────
        settingsRow.setSpacing(UiScaleHelper.settingsSpacingPx(eff));
        settingsRow.setStyle(UiScaleHelper.rowPaddingStyle(eff));
    }

    /** Load current settings into UI controls. */
    public void syncFromSettings() {
        spWorkTime.getValueFactory().setValue(settings.getWorkTime());
        spRelaxTime.getValueFactory().setValue(settings.getRelaxTime());
        spLongRelaxTime.getValueFactory().setValue(settings.getRelaxTimeLong());
    }

    /** Push current UI values into settings. */
    private void syncToSettings() {
        settings.setWorkTime(spWorkTime.getValue());
        settings.setRelaxTime(spRelaxTime.getValue());
        settings.setRelaxTimeLong(spLongRelaxTime.getValue());
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
    private void onThemeSettings() {
        syncToSettings();
        mainController.showThemeSettings();
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

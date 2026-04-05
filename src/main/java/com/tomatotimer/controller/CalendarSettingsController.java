package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import com.tomatotimer.IconFactory;
import com.tomatotimer.UiScaleHelper;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

/**
 * Controller for calendar_settings.fxml – manages all Google Calendar integration settings.
 *
 * <p>Mirrors the pattern of {@link TaskbarSettingsController}: a dedicated sub-page reached
 * from the main Settings page via a single calendar icon button, with a Back button that
 * returns to Settings.</p>
 */
public class CalendarSettingsController {

    @FXML private HBox      rootBox;
    @FXML private HBox      calendarRow;
    @FXML private Button    btnBack;
    @FXML private Button    btnTestGCal;
    @FXML private CheckBox  cbEnableGCal;
    @FXML private CheckBox  cbCopyToClipboard;
    @FXML private TextField tfGCalSrc;
    @FXML private TextField tfGCalText;
    @FXML private GridPane  gpGCalDetails;

    private MainController    mainController;
    private final AppSettings settings = AppSettings.getInstance();

    // ── SVG icon references ───────────────────────────────────────────────────
    private Group iconBack;
    private Group iconCalendar;

    // =========================================================================
    //  Lifecycle
    // =========================================================================

    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    @FXML
    public void initialize() {
        iconBack     = IconFactory.create(IconFactory.PATH_CLOCK,    IconFactory.COLOR_BACK);
        iconCalendar = IconFactory.create(IconFactory.PATH_CALENDAR, IconFactory.COLOR_CALENDAR_TEST);

        btnBack.setGraphic(iconBack);
        btnTestGCal.setGraphic(iconCalendar);

        // Resize all scalable elements once laid out, and whenever height changes
        rootBox.heightProperty().addListener((obs, ov, nv) -> updateDynamicSizing(nv.doubleValue()));
        javafx.application.Platform.runLater(() -> updateDynamicSizing(rootBox.getHeight()));
    }

    // =========================================================================
    //  Dynamic sizing
    // =========================================================================

    /**
     * Applies all dynamic scaling for the calendar-settings page based on the current
     * container height. Called on every height change and once after first layout.
     */
    private void updateDynamicSizing(double height) {
        final double eff = Math.max(height, UiScaleHelper.REF_HEIGHT);

        // ── Icons ─────────────────────────────────────────────────────────────
        final double navSz = UiScaleHelper.navIconPx(eff);
        IconFactory.resize(iconBack,     navSz);
        IconFactory.resize(iconCalendar, navSz * 0.88);

        // ── CheckBox labels ───────────────────────────────────────────────────
        final String lblStyle = String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.settingLabelFontPx(eff));
        cbEnableGCal.setStyle(lblStyle);
        cbCopyToClipboard.setStyle(lblStyle);

        // ── Row spacing and padding ───────────────────────────────────────────
        calendarRow.setSpacing(UiScaleHelper.settingsSpacingPx(eff));
        calendarRow.setStyle(UiScaleHelper.rowPaddingStyle(eff));

        // ── VBox spacing (GCal + Copy URL checkboxes) ─────────────────────────
        for (final var child : calendarRow.getChildren()) {
            if (child instanceof VBox vbox) {
                vbox.setSpacing(UiScaleHelper.clamp(eff * 0.04, 1.0, 4.0));
            }
        }
    }

    // =========================================================================
    //  Settings sync
    // =========================================================================

    /** Loads current persisted settings into the UI controls. */
    public void syncFromSettings() {
        tfGCalSrc.setText(settings.getGCalSrc());
        tfGCalText.setText(settings.getGCalText());
        cbEnableGCal.setSelected(settings.isGCalEnable());
        cbCopyToClipboard.setSelected(settings.isGCalCopyToClipboard());
        updateGCalControls();
    }

    /** Pushes current UI values into persisted settings. */
    private void syncToSettings() {
        settings.setGCalSrc(tfGCalSrc.getText());
        settings.setGCalText(tfGCalText.getText());
        settings.setGCalEnable(cbEnableGCal.isSelected());
        settings.setGCalCopyToClipboard(cbCopyToClipboard.isSelected());
    }

    /** Enables/disables the detail grid depending on whether GCal integration is on. */
    private void updateGCalControls() {
        gpGCalDetails.setDisable(!cbEnableGCal.isSelected());
    }

    // =========================================================================
    //  FXML handlers
    // =========================================================================

    @FXML
    private void onBack() {
        syncToSettings();
        if (mainController != null) mainController.showSettings();
    }

    @FXML
    private void onGCalEnableChanged() {
        updateGCalControls();
        syncToSettings();
    }

    @FXML
    private void onTestGCal() {
        syncToSettings();
        if (mainController != null) {
            mainController.openGoogleCalendar(
                    LocalDateTime.now().minusMinutes(settings.getWorkTime()),
                    LocalDateTime.now());
        }
    }
}


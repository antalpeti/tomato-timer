package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import com.tomatotimer.IconFactory;
import com.tomatotimer.NeonGlowProfile;
import com.tomatotimer.NeonPreset;
import com.tomatotimer.ThemeSelectionMode;
import com.tomatotimer.UiScaleHelper;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Controller for theme_settings.fxml – manages Neon preset, glow profile, and
 * theme-selection mode.
 *
 * <p>Reached from the main Settings page via the Theme icon button.
 * The Back button returns to the Settings page.  All three combos apply their
 * values immediately: preset and glow trigger {@link MainController#updateUI()}
 * so the timer face refreshes in the background; selection mode is persisted
 * and only takes effect on the next WORK-phase start.</p>
 *
 * <p>Mirrors the pattern of {@link TaskbarSettingsController} and
 * {@link CalendarSettingsController}.</p>
 */
public class ThemeSettingsController {

    // ── FXML bindings ─────────────────────────────────────────────────────────
    @FXML private HBox rootBox;
    @FXML private HBox themeRow;
    @FXML private Button btnBack;
    @FXML private Label lblPreset;
    @FXML private ComboBox<NeonPreset> cbNeonPreset;
    @FXML private Label lblGlow;
    @FXML private ComboBox<NeonGlowProfile> cbNeonGlowProfile;
    @FXML private Label lblSelection;
    @FXML private ComboBox<ThemeSelectionMode> cbThemeSelectionMode;

    private MainController mainController;
    private final AppSettings settings = AppSettings.getInstance();

    // ── SVG icon ──────────────────────────────────────────────────────────────
    private Group iconBack;

    // =========================================================================
    //  Lifecycle
    // =========================================================================

    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    @FXML
    public void initialize() {
        iconBack = IconFactory.create(IconFactory.PATH_CLOCK, IconFactory.COLOR_BACK);
        btnBack.setGraphic(iconBack);

        // Resize all scalable elements once laid out, and whenever height changes
        rootBox.heightProperty().addListener((obs, ov, nv) -> updateDynamicSizing(nv.doubleValue()));
        javafx.application.Platform.runLater(() -> updateDynamicSizing(rootBox.getHeight()));

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
    }

    // =========================================================================
    //  Dynamic sizing
    // =========================================================================

    /**
     * Applies all dynamic scaling for the theme-settings page based on the current
     * container height. Called on every height change and once after first layout.
     */
    private void updateDynamicSizing(double height) {
        final double eff = Math.max(height, UiScaleHelper.REF_HEIGHT);

        // ── Back icon ─────────────────────────────────────────────────────────
        IconFactory.resize(iconBack, UiScaleHelper.navIconPx(eff));

        // ── Labels ────────────────────────────────────────────────────────────
        final String lblStyle = String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.settingLabelFontPx(eff));
        lblPreset.setStyle(lblStyle);
        lblGlow.setStyle(lblStyle);
        lblSelection.setStyle(lblStyle);

        // ── ComboBox widths ───────────────────────────────────────────────────
        final double spinW = UiScaleHelper.spinnerWidthPx(eff);
        // Preset ComboBox – proportional width, min 90 px
        cbNeonPreset.setPrefWidth(UiScaleHelper.clamp(spinW * 2.2, 90.0, 140.0));
        // Glow Profile ComboBox – slightly narrower (3 short values)
        cbNeonGlowProfile.setPrefWidth(UiScaleHelper.clamp(spinW * 1.8, 80.0, 110.0));
        // Selection mode ComboBox – similar width to Glow (4 short values)
        cbThemeSelectionMode.setPrefWidth(UiScaleHelper.clamp(spinW * 1.8, 80.0, 115.0));

        // ── Row spacing and padding ───────────────────────────────────────────
        themeRow.setSpacing(UiScaleHelper.settingsSpacingPx(eff));
        themeRow.setStyle(UiScaleHelper.rowPaddingStyle(eff));
    }

    // =========================================================================
    //  Settings sync
    // =========================================================================

    /** Loads current persisted settings into the UI controls. */
    public void syncFromSettings() {
        cbNeonPreset.setValue(settings.getNeonPreset());
        cbNeonGlowProfile.setValue(settings.getNeonGlowProfile());
        cbThemeSelectionMode.setValue(settings.getThemeSelectionMode());
    }

    /**
     * Syncs only the preset ComboBox from persisted settings.
     * Called by {@link MainController} when the theme advances via the selection mode
     * (Sequential / Random / Shuffle) so the combo box stays in sync even while the
     * theme-settings panel is visible.
     */
    public void syncPresetComboBox() {
        cbNeonPreset.setValue(settings.getNeonPreset());
    }

    /** Pushes current UI values into persisted settings. */
    private void syncToSettings() {
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
        if (mainController != null) mainController.showSettings();
    }
}


package com.tomatotimer.controller;

import com.tomatotimer.AppSettings;
import com.tomatotimer.IconFactory;
import com.tomatotimer.TaskbarTimeLayout;
import com.tomatotimer.UiScaleHelper;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

/**
 * Controller for taskbar_settings.fxml.
 *
 * <p>Manages three persisted taskbar icon settings:
 * <ul>
 *   <li><em>Enable</em> – whether the dynamic countdown icon is shown at all.</li>
 *   <li><em>Font size</em> – base font size in pixels at the 64 px reference canvas
 *       (range {@value #FONT_MIN}–{@value #FONT_MAX}, default 17).</li>
 *   <li><em>Layout</em> – {@link TaskbarTimeLayout#VERTICAL} (stacked MM/SS or HH/MM/SS)
 *       versus {@link TaskbarTimeLayout#HORIZONTAL} (single-line MM:SS or HH:MM:SS).</li>
 * </ul>
 *
 * <p>All changes are persisted immediately via {@link AppSettings} and take effect on
 * the next taskbar icon redraw (within 1 second, or instantly when confirmed via
 * {@link MainController#updateUI()}).</p>
 */
public class TaskbarSettingsController {

    // ── Font size range ───────────────────────────────────────────────────────
    /** Minimum allowed base font size (px at 64 px canvas). */
    static final int FONT_MIN = 8;
    /** Maximum allowed base font size (px at 64 px canvas). */
    static final int FONT_MAX = 28;

    // ── FXML bindings ─────────────────────────────────────────────────────────
    @FXML private HBox            rootBox;
    @FXML private HBox            taskbarRow;
    @FXML private Button          btnBack;
    @FXML private CheckBox        cbTaskbarEnable;
    @FXML private Label           lblFontSize;
    @FXML private Label           lblLayout;
    @FXML private Spinner<Integer> spFontSize;
    @FXML private RadioButton     rbVertical;
    @FXML private RadioButton     rbHorizontal;

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

        // Resize all elements whenever the container height changes
        rootBox.heightProperty().addListener((obs, ov, nv) -> updateDynamicSizing(nv.doubleValue()));
        javafx.application.Platform.runLater(() -> updateDynamicSizing(rootBox.getHeight()));

        // Persist font size on each spinner commit; trigger immediate icon redraw
        spFontSize.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                final int clamped = (int) UiScaleHelper.clamp(nv, FONT_MIN, FONT_MAX);
                settings.setTaskbarFontSize(clamped);
                if (mainController != null) mainController.updateUI();
            }
        });
    }

    // =========================================================================
    //  Dynamic sizing
    // =========================================================================

    /**
     * Applies all dynamic scaling for the taskbar-settings page based on the current
     * container height. Called on every height change and once after first layout.
     */
    private void updateDynamicSizing(double height) {
        final double eff = Math.max(height, UiScaleHelper.REF_HEIGHT);

        // ── Back icon ─────────────────────────────────────────────────────────
        IconFactory.resize(iconBack, UiScaleHelper.navIconPx(eff));

        // ── Labels ────────────────────────────────────────────────────────────
        final String lblStyle = String.format("-fx-font-size: %.1fpx;",
                UiScaleHelper.settingLabelFontPx(eff));
        lblFontSize.setStyle(lblStyle);
        lblLayout.setStyle(lblStyle);
        cbTaskbarEnable.setStyle(lblStyle);
        rbVertical.setStyle(lblStyle);
        rbHorizontal.setStyle(lblStyle);

        // ── Font-size spinner width ───────────────────────────────────────────
        spFontSize.setPrefWidth(UiScaleHelper.spinnerWidthPx(eff));

        // ── Row spacing and padding ───────────────────────────────────────────
        taskbarRow.setSpacing(UiScaleHelper.settingsSpacingPx(eff));
        taskbarRow.setStyle(UiScaleHelper.rowPaddingStyle(eff));
    }

    // =========================================================================
    //  Settings sync
    // =========================================================================

    /** Loads current persisted settings into the UI controls. */
    public void syncFromSettings() {
        cbTaskbarEnable.setSelected(settings.isTaskbarIconEnable());

        final int savedSize = (int) UiScaleHelper.clamp(
                settings.getTaskbarFontSize(), FONT_MIN, FONT_MAX);
        spFontSize.getValueFactory().setValue(savedSize);

        final boolean vertical = settings.getTaskbarLayout() == TaskbarTimeLayout.VERTICAL;
        rbVertical.setSelected(vertical);
        rbHorizontal.setSelected(!vertical);
    }

    /** Pushes current UI values into persisted settings. */
    private void syncToSettings() {
        settings.setTaskbarIconEnable(cbTaskbarEnable.isSelected());

        if (spFontSize.getValue() != null) {
            final int clamped = (int) UiScaleHelper.clamp(spFontSize.getValue(), FONT_MIN, FONT_MAX);
            settings.setTaskbarFontSize(clamped);
        }

        settings.setTaskbarLayout(
                rbHorizontal.isSelected() ? TaskbarTimeLayout.HORIZONTAL : TaskbarTimeLayout.VERTICAL);
    }

    // =========================================================================
    //  FXML handlers
    // =========================================================================

    @FXML
    private void onBack() {
        syncToSettings();
        if (mainController != null) {
            mainController.updateUI();
            mainController.showSettings();
        }
    }

    @FXML
    private void onEnableChanged() {
        settings.setTaskbarIconEnable(cbTaskbarEnable.isSelected());
        if (mainController != null) mainController.updateUI();
    }

    @FXML
    private void onLayoutChanged() {
        settings.setTaskbarLayout(
                rbHorizontal.isSelected() ? TaskbarTimeLayout.HORIZONTAL : TaskbarTimeLayout.VERTICAL);
        if (mainController != null) mainController.updateUI();
    }
}


package com.tomatotimer;

/**
 * Central utility for computing scaled UI metric values from current window dimensions.
 *
 * <p>All methods are pure functions; no mutable state is held here. Reference
 * dimensions are the default window size ({@value #REF_WIDTH} × {@value #REF_HEIGHT} px).
 * Every formula is clamped so the UI remains usable at both very small and very large
 * window sizes.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 *   double sz = UiScaleHelper.mainTimeFontPx(rootPane.getWidth(), rootPane.getHeight());
 *   label.setStyle(String.format("-fx-font-size: %.1fpx;", sz));
 * }</pre>
 */
public final class UiScaleHelper {

    /** Default window width in pixels (FXML {@code prefWidth}). */
    public static final double REF_WIDTH  = 260.0;
    /** Default window height in pixels (FXML {@code prefHeight}). */
    public static final double REF_HEIGHT =  44.0;

    private UiScaleHelper() { /* utility class */ }

    // ── General ────────────────────────────────────────────────────────────────

    /** Clamps {@code v} to the inclusive range [{@code min}, {@code max}]. */
    public static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // ── Font sizes ─────────────────────────────────────────────────────────────

    /**
     * Main timer-face label ("Work 30:00").
     * Uses both axes so the text stays readable in landscape and portrait layouts.
     * Clamped to [18, 72] px.
     */
    public static double mainTimeFontPx(double width, double height) {
        return clamp(Math.min(height * 0.42, width * 0.095), 18.0, 72.0);
    }

    /**
     * Small time label shown on hover (bottom-right of the timer face).
     * Clamped to [11, 42] px.
     */
    public static double smallTimeFontPx(double width, double height) {
        return clamp(mainTimeFontPx(width, height) * 0.58, 11.0, 42.0);
    }

    /**
     * Info label (start time + cumulated pause, bottom-left of the timer face).
     * Clamped to [9, 30] px.
     */
    public static double infoFontPx(double width, double height) {
        return clamp(mainTimeFontPx(width, height) * 0.50, 9.0, 30.0);
    }

    /**
     * Row labels on the settings page ("Work:", "Rest:", "Long:", …).
     * Height-driven only – the settings page scrolls horizontally.
     * Clamped to [9, 16] px.
     */
    public static double settingLabelFontPx(double height) {
        return clamp(height * 0.26, 9.0, 16.0);
    }

    /**
     * Column-header labels on the sound-settings page ("Resume", "Pause", …).
     * Clamped to [8, 13] px.
     */
    public static double soundLabelFontPx(double height) {
        return clamp(height * 0.20, 8.0, 13.0);
    }

    /**
     * Version label (bottom-right of the settings page).
     * Clamped to [8, 13] px.
     */
    public static double versionLabelFontPx(double height) {
        return clamp(height * 0.21, 8.0, 13.0);
    }

    // ── Icon sizes ─────────────────────────────────────────────────────────────

    /**
     * Main timer-face action icons (play, pause, reset, settings, work, relax).
     * Uses both axes – same reasoning as {@link #mainTimeFontPx}.
     * Clamped to [16, 52] px.
     */
    public static double mainIconPx(double width, double height) {
        return clamp(Math.min(height * 0.52, width * 0.09), 16.0, 52.0);
    }

    /**
     * Navigation / action icons on settings and sound-settings pages.
     * Height-driven; the effective height is floored at {@link #REF_HEIGHT} so
     * icons never fall below the default size.
     * Clamped to [16, 52] px.
     */
    public static double navIconPx(double height) {
        return clamp(Math.max(height, REF_HEIGHT) * 0.52, 16.0, 52.0);
    }

    /**
     * Window-control (pin / close) icons overlaid at the top-right of the root pane.
     * Clamped to [10, 14] px.
     */
    public static double winCtrlIconPx(double height) {
        return clamp(height * 0.28, 10.0, 14.0);
    }

    // ── Small buttons (sound-settings ▶ ⏹ 📂 🔇 rows) ──────────────────────────

    /**
     * Font size for small emoji/text buttons in sound-settings rows.
     * Clamped to [9, 16] px.
     */
    public static double smallBtnFontPx(double height) {
        return clamp(height * 0.25, 9.0, 16.0);
    }

    /**
     * Minimum width for a small sound-settings button.
     * Clamped to [20, 34] px.
     */
    public static double smallBtnMinWidthPx(double height) {
        return clamp(height * 0.55, 20.0, 34.0);
    }

    /**
     * Minimum height for a small sound-settings button.
     * Clamped to [18, 30] px.
     */
    public static double smallBtnMinHeightPx(double height) {
        return clamp(height * 0.45, 18.0, 30.0);
    }

    // ── Layout metrics ─────────────────────────────────────────────────────────

    /**
     * Preferred width of the time spinners (Work / Rest / Long) on the settings page.
     * Clamped to [44, 80] px.
     */
    public static double spinnerWidthPx(double height) {
        return clamp(height * 1.5, 44.0, 80.0);
    }

    /**
     * Horizontal gap between items in the settings scrollable row.
     * Clamped to [2, 10] px.
     */
    public static double settingsSpacingPx(double height) {
        return clamp(height * 0.14, 2.0, 10.0);
    }

    /**
     * Horizontal gap between sound-type groups in the sound-settings row.
     * Clamped to [3, 14] px.
     */
    public static double soundGroupSpacingPx(double height) {
        return clamp(height * 0.23, 3.0, 14.0);
    }

    /**
     * Vertical spacing inside each sound-type VBox (label → button row).
     * Clamped to [1, 4] px.
     */
    public static double soundVboxSpacingPx(double height) {
        return clamp(height * 0.05, 1.0, 4.0);
    }

    /**
     * Horizontal spacing between the ▶ ⏹ 📂 🔇 buttons inside each sound group.
     * Clamped to [1, 4] px.
     */
    public static double soundBtnRowSpacingPx(double height) {
        return clamp(height * 0.05, 1.0, 4.0);
    }

    /**
     * Builds an inline {@code -fx-padding} style for scrollable content rows on
     * the settings and sound-settings pages.
     *
     * @param height current container height in pixels
     * @return a CSS snippet ready for {@code Node.setStyle()}, e.g.
     *         {@code "-fx-padding: 2 4 2 4;"}
     */
    public static String rowPaddingStyle(double height) {
        final double v = clamp(height * 0.06, 1.0, 4.0);
        final double h = clamp(height * 0.09, 2.0, 6.0);
        return String.format("-fx-padding: %.0f %.0f %.0f %.0f;", v, h, v, h);
    }
}


package com.tomatotimer;

import javafx.scene.paint.Color;

/**
 * Computes progress-bar accent and track colours for every timer state,
 * mirroring the colour-change logic of the original WPF implementation
 * (green → yellow at 80 % → red at overtime) while adding:
 * <ul>
 *   <li>Mode-aware base colours (WORK = blue-violet, RELAX = teal, RELAX_LONG = mint).</li>
 *   <li>Smooth, continuous interpolation in the warning zone (80 – 100 %).</li>
 *   <li>A subtle dark track tint per mode that distinguishes WORK from REST.</li>
 * </ul>
 *
 * <h3>WPF correspondence</h3>
 * <pre>
 *   WPF (MainWindow.xaml.cs, UpdateUI):
 *     paused    → Green + Indeterminate
 *     overtime  → Red   + 100 %
 *     progress &gt; 80 % → Yellow
 *     otherwise → Green
 * </pre>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 *   Color accent   = TimerBackgroundHelper.computeAccentColor(mode, pct, paused, over);
 *   String track   = TimerBackgroundHelper.computeTrackColor(mode, paused, over);
 *   progressBar.setStyle("-fx-accent: " + TimerBackgroundHelper.toCssHex(accent) + ";");
 * }</pre>
 */
public final class TimerBackgroundHelper {

    // ── Threshold ─────────────────────────────────────────────────────────────

    /**
     * Progress percentage at which the warning phase begins.
     * Matches the original WPF threshold (80 %).
     */
    public static final double WARNING_THRESHOLD_PCT = 80.0;

    // ── Accent colours ────────────────────────────────────────────────────────

    /** WORK normal phase accent (0 – 80 %): blue-violet. */
    private static final Color WORK_NORMAL       = Color.web("#5B8DEF");

    /** RELAX normal phase accent (0 – 80 %): teal (matches IconFactory.COLOR_PLAY). */
    private static final Color RELAX_NORMAL      = Color.web("#4ECDC4");

    /** RELAX_LONG normal phase accent (0 – 80 %): mint-green. */
    private static final Color RELAX_LONG_NORMAL = Color.web("#00B894");

    /** Warning phase target colour (> 80 %): warm yellow – mirrors WPF {@code Brushes.Yellow}. */
    private static final Color WARNING_COLOR     = Color.web("#FFDE57");

    /** Overtime accent: coral-red – mirrors WPF {@code Brushes.Red}. */
    private static final Color OVER_COLOR        = Color.web("#FF6B6B");

    /** Paused accent: muted lavender – mirrors WPF {@code Brushes.Green} indeterminate. */
    private static final Color PAUSED_COLOR      = Color.web("#A29BFE");

    // ── Track (background of the unfilled bar area) colours ──────────────────

    /** Track colour for WORK mode: very dark blue-black. */
    public static final String TRACK_WORK       = "#0D1117";

    /** Track colour for RELAX mode: very dark teal-black. */
    public static final String TRACK_RELAX      = "#0D1A19";

    /** Track colour for RELAX_LONG mode: very dark mint-black. */
    public static final String TRACK_RELAX_LONG = "#0A1A15";

    /** Track colour when paused: dark navy. */
    public static final String TRACK_PAUSED     = "#1A1A2E";

    /** Track colour when overtime: dark red tint. */
    public static final String TRACK_OVER       = "#1A0D0D";

    private TimerBackgroundHelper() { /* utility class – no instances */ }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Computes the progress-bar <em>accent</em> (filled bar) colour for the given state.
     *
     * <p>Colour zones (WPF-aligned):
     * <ol>
     *   <li><b>Paused</b> → {@link #PAUSED_COLOR} (lavender).</li>
     *   <li><b>OverTime</b> → {@link #OVER_COLOR} (red).</li>
     *   <li><b>0 – {@link #WARNING_THRESHOLD_PCT} %</b> → mode-specific base colour,
     *       constant (matches WPF green staying constant in this zone).</li>
     *   <li><b>{@link #WARNING_THRESHOLD_PCT} – 100 %</b> → continuous interpolation
     *       from the base colour toward {@link #WARNING_COLOR} (yellow).</li>
     * </ol>
     *
     * @param mode        current {@link TimerMode}
     * @param progressPct elapsed percentage in [0, 100]
     * @param isPaused    whether the timer is paused (only possible in WORK mode)
     * @param isOverTime  whether the allocated time has fully elapsed
     * @return a JavaFX {@link Color} suitable for {@code -fx-accent}
     */
    public static Color computeAccentColor(TimerMode mode, double progressPct,
                                           boolean isPaused, boolean isOverTime) {
        if (isPaused)   return PAUSED_COLOR;
        if (isOverTime) return OVER_COLOR;

        final Color baseColor = normalColorFor(mode);

        if (progressPct <= WARNING_THRESHOLD_PCT) {
            return baseColor;
        }

        // Smooth, continuous interpolation in the warning zone (80 → 100 %)
        final double t = Math.min(1.0,
                (progressPct - WARNING_THRESHOLD_PCT) / (100.0 - WARNING_THRESHOLD_PCT));
        return baseColor.interpolate(WARNING_COLOR, t);
    }

    /**
     * Returns the CSS hex-colour string for the progress-bar <em>track</em>
     * (the unfilled part that acts as the window background).
     *
     * <p>Each mode has a subtle dark tint so WORK and REST are visually distinct
     * even before any progress has been made.</p>
     *
     * @param mode       current {@link TimerMode}
     * @param isPaused   whether the timer is paused
     * @param isOverTime whether the allocated time has fully elapsed
     * @return CSS hex string, e.g. {@code "#0D1117"}
     */
    public static String computeTrackColor(TimerMode mode, boolean isPaused, boolean isOverTime) {
        if (isPaused)   return TRACK_PAUSED;
        if (isOverTime) return TRACK_OVER;
        return switch (mode) {
            case WORK       -> TRACK_WORK;
            case RELAX      -> TRACK_RELAX;
            case RELAX_LONG -> TRACK_RELAX_LONG;
        };
    }

    /**
     * Converts a JavaFX {@link Color} to an opaque CSS hex string (e.g. {@code "#FF6B6B"}).
     *
     * @param color non-null JavaFX colour
     * @return six-digit uppercase hex colour string prefixed with {@code #}
     */
    public static String toCssHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(color.getRed()   * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue()  * 255));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private static Color normalColorFor(TimerMode mode) {
        return switch (mode) {
            case WORK       -> WORK_NORMAL;
            case RELAX      -> RELAX_NORMAL;
            case RELAX_LONG -> RELAX_LONG_NORMAL;
        };
    }
}


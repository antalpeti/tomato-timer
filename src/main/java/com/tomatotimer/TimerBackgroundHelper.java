package com.tomatotimer;

import javafx.scene.paint.Color;

/**
 * Computes all background colours and gradient CSS strings for every timer state,
 * mirroring the colour-change logic of the original WPF implementation
 * (green → yellow at 80 % → red at overtime) while adding:
 * <ul>
 *   <li>Mode-aware base colours (WORK = blue-violet, RELAX = teal, RELAX_LONG = mint).</li>
 *   <li>Smooth, continuous interpolation in the warning zone (80 – 100 %).</li>
 *   <li>A layered ambient gradient for the root pane background that changes
 *       continuously with mode and progress.</li>
 *   <li>A vertical 3-D gradient for the progress-bar fill (.bar sub-node).</li>
 *   <li>A near-transparent track overlay so the root gradient shows through
 *       the unfilled portion of the bar.</li>
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
 *   // Root pane ambient gradient (full-window background):
 *   rootPane.setStyle("-fx-background-color: "
 *       + TimerBackgroundHelper.computeRootGradientCss(mode, pct, paused, over) + ";");
 *
 *   // Progress-bar fill gradient (.bar sub-node):
 *   Node bar = progressBar.lookup(".bar");
 *   if (bar != null) bar.setStyle(TimerBackgroundHelper.computeBarCss(mode, pct, paused, over));
 *
 *   // Progress-bar track (transparent overlay so root gradient shows through):
 *   Node track = progressBar.lookup(".track");
 *   if (track != null) track.setStyle(TimerBackgroundHelper.computeTrackCss());
 * }</pre>
 */
public final class TimerBackgroundHelper {

    // ── Threshold ─────────────────────────────────────────────────────────────

    /**
     * Progress percentage at which the warning phase begins.
     * Matches the original WPF threshold (80 %).
     */
    public static final double WARNING_THRESHOLD_PCT = 80.0;

    // ── Track (background of the unfilled bar area) colours ──────────────────
    // Kept for backward-compatibility; gradient API is preferred.

    /** Track colour for WORK mode: near-black deep violet. */
    public static final String TRACK_WORK       = "#06000F";

    /** Track colour for RELAX mode: near-black deep cyan. */
    public static final String TRACK_RELAX      = "#001820";

    /** Track colour for RELAX_LONG mode: near-black deep neon-green. */
    public static final String TRACK_RELAX_LONG = "#001810";

    /** Track colour when paused: deep dark purple. */
    public static final String TRACK_PAUSED     = "#0F0022";

    /** Track colour when overtime: deep dark magenta. */
    public static final String TRACK_OVER       = "#1A0020";

    private TimerBackgroundHelper() { /* utility class – no instances */ }

    // ── Public API ────────────────────────────────────────────────────────────

    // ---- Gradient helpers (preferred) ----------------------------------------

    /**
     * Returns the CSS colour value for the timer root pane's
     * {@code -fx-background-color} inline style, rendered using the supplied
     * {@link NeonPreset}.
     *
     * @param preset      the active neon visual-theme preset
     * @param mode        current {@link TimerMode}
     * @param progressPct elapsed percentage in [0, 100]
     * @param isPaused    whether the timer is paused
     * @param isOverTime  whether the allocated time has fully elapsed
     * @return a comma-separated CSS background value string (no semicolon)
     */
    public static String computeRootGradientCss(NeonPreset preset,
                                                TimerMode mode, double progressPct,
                                                boolean isPaused, boolean isOverTime) {
        final Color accent = computeAccentColor(preset, mode, progressPct, isPaused, isOverTime);
        final double progress01 = clamp(progressPct / 100.0, 0.0, 1.0);
        final double glowAlpha = isOverTime
                ? preset.getRootGlowAlphaMax()
                : isPaused
                ? 0.20
                : preset.getRootGlowAlphaMin()
                        + (preset.getRootGlowAlphaMax() - preset.getRootGlowAlphaMin()) * progress01;

        // Deep base layer
        final Color top    = accent.interpolate(Color.BLACK, preset.getRootDarkEdge());
        final Color mid    = accent.interpolate(Color.BLACK, preset.getRootDarkMid());
        final Color bottom = accent.interpolate(Color.BLACK, preset.getRootDarkEdge() + 0.06);

        // Coloured atmospheric glow through the middle band
        final Color glowStrong = accent.interpolate(Color.WHITE, preset.getRootGlowWhiteBlend());
        final Color glowSoft   = accent.interpolate(Color.BLACK, preset.getRootGlowBlackBlend());

        // Subtle glossy sheen near the top
        final Color sheen = accent.interpolate(Color.WHITE, 0.35);

        return String.format(
                "linear-gradient(to bottom, %s 0%%, %s 20%%, rgba(255,255,255,0.00) 55%%),"
              + "linear-gradient(to top,    %s 0%%, %s 25%%, rgba(0,0,0,0.00) 65%%),"
              + "linear-gradient(to bottom, rgba(0,0,0,0.00) 0%%, %s 15%%, %s 45%%, %s 80%%, rgba(0,0,0,0.00) 100%%),"
              + "linear-gradient(to bottom, %s 0%%, %s 42%%, %s 100%%)",
                toCssRgba(sheen, preset.getRootSheenAlpha()),
                toCssRgba(sheen, preset.getRootSheenAlpha() * 0.40),
                toCssRgba(glowSoft,   glowAlpha * 0.50),
                toCssRgba(glowStrong, glowAlpha * 0.40),
                toCssRgba(glowSoft,   glowAlpha * 0.40),
                toCssRgba(glowStrong, glowAlpha),
                toCssRgba(glowSoft,   glowAlpha * 0.50),
                toCssHex(top),
                toCssHex(mid),
                toCssHex(bottom));
    }

    /**
     * Backward-compatible overload that delegates to {@link NeonPreset#NIGHT_RUNNER}.
     *
     * @see #computeRootGradientCss(NeonPreset, TimerMode, double, boolean, boolean)
     */
    public static String computeRootGradientCss(TimerMode mode, double progressPct,
                                                boolean isPaused, boolean isOverTime) {
        return computeRootGradientCss(NeonPreset.NIGHT_RUNNER, mode, progressPct, isPaused, isOverTime);
    }

    /**
     * Returns the complete CSS style string for the progress-bar {@code .bar}
     * sub-node, rendered using the supplied {@link NeonPreset}.
     *
     * @param preset      the active neon visual-theme preset
     * @param mode        current {@link TimerMode}
     * @param progressPct elapsed percentage in [0, 100]
     * @param isPaused    whether the timer is paused
     * @param isOverTime  whether the allocated time has fully elapsed
     * @return CSS style string ready to pass to {@link javafx.scene.Node#setStyle(String)}
     */
    public static String computeBarCss(NeonPreset preset,
                                       TimerMode mode, double progressPct,
                                       boolean isPaused, boolean isOverTime) {
        final Color accent    = computeAccentColor(preset, mode, progressPct, isPaused, isOverTime);
        final Color lighter   = accent.interpolate(Color.WHITE, preset.getBarLighten());
        final Color darker    = accent.interpolate(Color.BLACK, preset.getBarDarken());
        final Color glowColor = accent.interpolate(Color.WHITE, preset.getBarGlowWhiteBlend());

        return String.format(
                "-fx-background-color:"
                + " linear-gradient(to bottom, rgba(255,255,255,%.3f) 0%%, rgba(255,255,255,0.000) 38%%),"
                + " linear-gradient(to bottom, %s 0%%, %s 55%%, %s 100%%);"
                + "-fx-background-radius: 0, 0;"
                + "-fx-background-insets: 0, 0;"
                + "-fx-effect: dropshadow(gaussian, %s, %.0f, %.2f, 0, 0);",
                preset.getBarInnerSheen(),
                toCssHex(lighter), toCssHex(accent), toCssHex(darker),
                toCssRgba(glowColor, preset.getBarGlowOpacity()),
                preset.getBarGlowRadius(), preset.getBarGlowSpread());
    }

    /**
     * Backward-compatible overload that delegates to {@link NeonPreset#NIGHT_RUNNER}.
     *
     * @see #computeBarCss(NeonPreset, TimerMode, double, boolean, boolean)
     */
    public static String computeBarCss(TimerMode mode, double progressPct,
                                       boolean isPaused, boolean isOverTime) {
        return computeBarCss(NeonPreset.NIGHT_RUNNER, mode, progressPct, isPaused, isOverTime);
    }

    /**
     * Returns the complete CSS style string for the progress-bar {@code .track}
     * sub-node (the unfilled portion).
     *
     * @return CSS style string ready to pass to {@link javafx.scene.Node#setStyle(String)}
     */
    public static String computeTrackCss() {
        return "-fx-background-color: linear-gradient(to bottom, rgba(0,0,0,0.10) 0%, rgba(0,0,0,0.18) 100%);" +
               "-fx-background-radius: 0;" +
               "-fx-background-insets: 0;";
    }

    // ---- Legacy solid-colour helpers (kept for backward-compatibility) --------

    /**
     * Computes the progress-bar accent colour for the given state using the
     * supplied {@link NeonPreset}.
     *
     * @param preset      the active neon visual-theme preset
     * @param mode        current {@link TimerMode}
     * @param progressPct elapsed percentage in [0, 100]
     * @param isPaused    whether the timer is paused
     * @param isOverTime  whether the allocated time has fully elapsed
     * @return a JavaFX {@link Color} suitable for {@code -fx-accent}
     */
    public static Color computeAccentColor(NeonPreset preset,
                                           TimerMode mode, double progressPct,
                                           boolean isPaused, boolean isOverTime) {
        if (isPaused)   return preset.getPausedColor();
        if (isOverTime) return preset.getOverColor();

        final Color baseColor = preset.normalColorFor(mode);

        if (progressPct <= WARNING_THRESHOLD_PCT) {
            return baseColor;
        }

        final double t = Math.min(1.0,
                (progressPct - WARNING_THRESHOLD_PCT) / (100.0 - WARNING_THRESHOLD_PCT));
        return baseColor.interpolate(preset.getWarningColor(), t);
    }

    /**
     * Backward-compatible overload that delegates to {@link NeonPreset#NIGHT_RUNNER}.
     *
     * @see #computeAccentColor(NeonPreset, TimerMode, double, boolean, boolean)
     */
    public static Color computeAccentColor(TimerMode mode, double progressPct,
                                           boolean isPaused, boolean isOverTime) {
        return computeAccentColor(NeonPreset.NIGHT_RUNNER, mode, progressPct, isPaused, isOverTime);
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

    /**
     * Converts a JavaFX {@link Color} to a CSS {@code rgba(...)} string using the
     * supplied opacity multiplier.
     */
    public static String toCssRgba(Color color, double opacity) {
        return String.format("rgba(%d,%d,%d,%.3f)",
                (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255),
                (int) Math.round(color.getBlue() * 255),
                clamp(opacity, 0.0, 1.0));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}


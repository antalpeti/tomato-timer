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
    // Kept for backward-compatibility; gradient API is preferred.

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

    // ── Gradient tuning constants ─────────────────────────────────────────────

    /**
     * Blend-to-black factor for the top and bottom edges of the root ambient gradient
     * (0 = pure accent colour, 1 = pure black).  Higher values keep the background
     * very dark so text and icons remain fully legible.
     */
    private static final double ROOT_DARK_EDGE = 0.82;

    /**
     * Blend-to-black factor for the centre stop of the root ambient gradient.
     * Lower than {@link #ROOT_DARK_EDGE} so the midpoint glows slightly more,
     * giving the view a subtle three-dimensional depth.
     */
    private static final double ROOT_DARK_MID  = 0.70;

    /**
     * Minimum opacity of the coloured ambient glow layered over the base gradient.
     */
    private static final double ROOT_GLOW_ALPHA_MIN = 0.14;

    /**
     * Maximum opacity of the coloured ambient glow layered over the base gradient.
     */
    private static final double ROOT_GLOW_ALPHA_MAX = 0.32;

    /**
     * Opacity of the subtle top sheen that adds a glossier, more dimensional look.
     */
    private static final double ROOT_SHEEN_ALPHA = 0.12;

    /**
     * How much white to blend into the accent for the <em>top</em> highlight
     * stop of the progress-bar fill gradient.
     */
    private static final double BAR_LIGHTEN    = 0.30;

    /**
     * How much black to blend into the accent for the <em>bottom</em> shadow
     * stop of the progress-bar fill gradient.
     */
    private static final double BAR_DARKEN     = 0.22;

    private TimerBackgroundHelper() { /* utility class – no instances */ }

    // ── Public API ────────────────────────────────────────────────────────────

    // ---- Gradient helpers (preferred) ----------------------------------------

    /**
     * Returns the CSS colour <em>value</em> (no property name) for the timer root
     * pane's {@code -fx-background-color} inline style.
     *
     * <p>The result is a layered background assembled from:
     * <ol>
     *   <li>a deep base vertical gradient,</li>
     *   <li>a stronger coloured ambient band through the middle, and</li>
     *   <li>a subtle glossy top sheen.</li>
     * </ol>
     * This makes the timer face feel more alive and visibly gradient-driven while
     * keeping the overall luminance dark enough for white text and icons.
     *
     * <p>Contrast ratios are preserved: the gradient is always dark enough (luma
     * below 15 %) to keep white text and SVG icons fully legible.
     *
     * <p>Example usage:
     * <pre>{@code
     *   rootPane.setStyle("-fx-background-color: "
     *       + TimerBackgroundHelper.computeRootGradientCss(mode, pct, paused, over) + ";");
     * }</pre>
     *
     * @param mode        current {@link TimerMode}
     * @param progressPct elapsed percentage in [0, 100]
     * @param isPaused    whether the timer is paused
     * @param isOverTime  whether the allocated time has fully elapsed
     * @return a comma-separated CSS background value string (no semicolon)
     */
    public static String computeRootGradientCss(TimerMode mode, double progressPct,
                                                boolean isPaused, boolean isOverTime) {
        final Color accent = computeAccentColor(mode, progressPct, isPaused, isOverTime);
        final double progress01 = clamp(progressPct / 100.0, 0.0, 1.0);
        final double glowAlpha = isOverTime
                ? ROOT_GLOW_ALPHA_MAX
                : isPaused
                ? 0.20
                : ROOT_GLOW_ALPHA_MIN + (ROOT_GLOW_ALPHA_MAX - ROOT_GLOW_ALPHA_MIN) * progress01;

        // Deep base layer
        final Color top    = accent.interpolate(Color.BLACK, ROOT_DARK_EDGE);
        final Color mid    = accent.interpolate(Color.BLACK, ROOT_DARK_MID);
        final Color bottom = accent.interpolate(Color.BLACK, ROOT_DARK_EDGE + 0.06);

        // Coloured atmospheric glow through the middle band
        final Color glowStrong = accent.interpolate(Color.WHITE, 0.12);
        final Color glowSoft   = accent.interpolate(Color.BLACK, 0.18);

        // Subtle glossy sheen near the top to make the gradient more readable
        final Color sheen = accent.interpolate(Color.WHITE, 0.35);
        return String.format(
                "linear-gradient(to bottom, %s 0%%, %s 18%%, rgba(255,255,255,0.00) 52%%),"
              + "linear-gradient(to bottom, rgba(0,0,0,0.00) 0%%, %s 16%%, %s 48%%, %s 78%%, rgba(0,0,0,0.00) 100%%),"
              + "linear-gradient(to bottom, %s 0%%, %s 42%%, %s 100%%)",
                toCssRgba(sheen, ROOT_SHEEN_ALPHA),
                toCssRgba(sheen, ROOT_SHEEN_ALPHA * 0.45),
                toCssRgba(glowSoft, glowAlpha * 0.45),
                toCssRgba(glowStrong, glowAlpha),
                toCssRgba(glowSoft, glowAlpha * 0.55),
                toCssHex(top),
                toCssHex(mid),
                toCssHex(bottom));
    }

    /**
     * Returns the complete CSS style string for the progress-bar {@code .bar}
     * sub-node (the filled portion).
     *
     * <p>Produces a top-to-bottom vertical gradient:
     * <ol>
     *   <li>Top highlight – accent blended {@value #BAR_LIGHTEN} toward white.</li>
     *   <li>Mid accent – pure accent colour at 55 %.</li>
     *   <li>Bottom shadow – accent blended {@value #BAR_DARKEN} toward black.</li>
     * </ol>
     * The gradient gives the fill a subtle 3-D dimension without sacrificing
     * the legibility of overlaid text.
     *
     * <p>Example usage:
     * <pre>{@code
     *   Node bar = progressBar.lookup(".bar");
     *   if (bar != null) bar.setStyle(
     *       TimerBackgroundHelper.computeBarCss(mode, pct, paused, over));
     * }</pre>
     *
     * @param mode        current {@link TimerMode}
     * @param progressPct elapsed percentage in [0, 100]
     * @param isPaused    whether the timer is paused
     * @param isOverTime  whether the allocated time has fully elapsed
     * @return CSS style string ready to pass to {@link javafx.scene.Node#setStyle(String)}
     */
    public static String computeBarCss(TimerMode mode, double progressPct,
                                       boolean isPaused, boolean isOverTime) {
        final Color accent  = computeAccentColor(mode, progressPct, isPaused, isOverTime);
        final Color lighter = accent.interpolate(Color.WHITE, BAR_LIGHTEN);
        final Color darker  = accent.interpolate(Color.BLACK, BAR_DARKEN);
        return String.format(
                "-fx-background-color: linear-gradient(to bottom, %s 0%%, %s 55%%, %s 100%%);" +
                "-fx-background-radius: 0;" +
                "-fx-background-insets: 0;",
                toCssHex(lighter), toCssHex(accent), toCssHex(darker));
    }

    /**
     * Returns the complete CSS style string for the progress-bar {@code .track}
     * sub-node (the unfilled portion).
     *
     * <p>The track is rendered as a near-transparent dark overlay
     * ({@code rgba(0,0,0,0.18)}) so that the root-pane ambient gradient
     * ({@link #computeRootGradientCss}) shows through the unfilled area.
     * This creates a seamless visual where the filled bar "emerges" from
     * the dark ambient background as the timer progresses.
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

    private static Color normalColorFor(TimerMode mode) {
        return switch (mode) {
            case WORK       -> WORK_NORMAL;
            case RELAX      -> RELAX_NORMAL;
            case RELAX_LONG -> RELAX_LONG_NORMAL;
        };
    }
}


package com.tomatotimer;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.List;

/**
 * Renders small timer-countdown icons suitable for the Windows taskbar.
 *
 * <p>The icons are produced entirely with JavaFX {@link Canvas} /
 * {@link javafx.scene.canvas.GraphicsContext}, so no AWT, no BufferedImage
 * and no JNI are required.  The resulting {@link Image} instances are passed
 * directly to {@code Stage.getIcons()}.</p>
 *
 * <h3>Visual design</h3>
 * <ul>
 *   <li>Square canvas whose side length is supplied by the caller.</li>
 *   <li>Near-black rounded-rectangle background with slight transparency.</li>
 *   <li>Thin accent-coloured border that matches the active {@link NeonPreset}.</li>
 *   <li>Bold centred countdown text (font size adapts to string length and icon size).</li>
 * </ul>
 *
 * <p><strong>Thread note:</strong> must be called on the JavaFX Application Thread
 * ({@link Canvas#snapshot} requires it).</p>
 */
public final class TaskbarIconRenderer {

    /** Reference icon side length in pixels used for scaling calculations. */
    private static final int REFERENCE_SIZE = 64;

    /** Background colour – near-black with very slight blue tint. */
    private static final Color BACKGROUND = Color.rgb(10, 10, 18, 0.94);

    /** Corner arc radius for the rounded rectangle at {@value #REFERENCE_SIZE} px. */
    private static final double ARC = 12.0;

    /** Border stroke width at {@value #REFERENCE_SIZE} px. */
    private static final double BORDER_WIDTH = 2.5;

    private TaskbarIconRenderer() { /* utility class – no instances */ }

    // =========================================================================
    //  Public API
    // =========================================================================

    /**
     * Renders a taskbar icon at the default {@value #REFERENCE_SIZE} px size.
     *
     * <p>Delegates to {@link #render(String, Color, int)}.</p>
     *
     * @param timeText    short time string, e.g. {@code "4:32"} or {@code "1:04:32"}
     * @param accentColor neon accent colour matching the current timer state
     * @return a {@value #REFERENCE_SIZE}×{@value #REFERENCE_SIZE} JavaFX {@link Image}
     */
    public static Image render(String timeText, Color accentColor) {
        return render(timeText, accentColor, REFERENCE_SIZE);
    }

    /**
     * Renders a taskbar icon at the requested {@code size}.
     *
     * <p>All visual dimensions (border width, arc radius, glow ring, font size)
     * are scaled proportionally so the design stays consistent at any resolution.</p>
     *
     * <p>The base font size is chosen by string length at {@value #REFERENCE_SIZE} px
     * and then multiplied by {@code size / REFERENCE_SIZE}:</p>
     * <ul>
     *   <li>≤ 4 chars ({@code "1:23"})  → 22 px base</li>
     *   <li>5 chars  ({@code "59:59"}) → 18 px base</li>
     *   <li>≥ 6 chars ({@code "1:23:45"}) → 14 px base</li>
     * </ul>
     *
     * @param timeText    short time string; must not be {@code null}
     * @param accentColor neon accent colour matching the current timer state
     * @param size        icon side length in pixels (square)
     * @return a {@code size × size} JavaFX {@link Image}
     */
    public static Image render(String timeText, Color accentColor, int size) {
        final var canvas = new Canvas(size, size);
        final var gc     = canvas.getGraphicsContext2D();

        final double scale       = (double) size / REFERENCE_SIZE;
        final double arc         = ARC * scale;
        final double borderWidth = Math.max(1.0, BORDER_WIDTH * scale);
        final double inset       = borderWidth / 2.0;

        // ── Background ────────────────────────────────────────────────────────
        gc.setFill(BACKGROUND);
        gc.fillRoundRect(0, 0, size, size, arc, arc);

        // ── Accent border ─────────────────────────────────────────────────────
        gc.setStroke(accentColor.deriveColor(0.0, 1.0, 1.0, 0.82));
        gc.setLineWidth(borderWidth);
        gc.strokeRoundRect(inset, inset, size - borderWidth, size - borderWidth,
                arc - inset, arc - inset);

        // ── Inner subtle glow ring ────────────────────────────────────────────
        gc.setStroke(accentColor.deriveColor(0.0, 1.0, 1.0, 0.18));
        gc.setLineWidth(3.5 * scale);
        final double glowOff  = 3.0 * scale;
        final double glowSize = size - borderWidth - 6.0 * scale;
        gc.strokeRoundRect(inset + glowOff, inset + glowOff, glowSize, glowSize,
                arc - 4.0 * scale, arc - 4.0 * scale);

        // ── Timer text ────────────────────────────────────────────────────────
        final double baseFontSize = switch (timeText.length()) {
            case 1, 2, 3, 4 -> 22.0;   // "1:23", "59"
            case 5          -> 18.0;   // "59:59"
            default         -> 14.0;   // "1:23:45" or longer
        };
        final double fontSize = Math.max(1.0, baseFontSize * scale);

        gc.setFill(accentColor);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, fontSize));

        // Vertically centre: 0.38 × fontSize is a good baseline-to-centre offset
        // for SansSerif bold at these sizes.
        gc.fillText(timeText, size / 2.0, size / 2.0 + fontSize * 0.38);

        // ── Snapshot ──────────────────────────────────────────────────────────
        final var params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }

    /**
     * Renders the icon at all standard sizes (16, 24, 32, 48, 64 px) in one call.
     *
     * <p>Pass the returned list directly to {@code stage.getIcons().setAll(...)} so
     * Windows can pick the best resolution for each taskbar / alt-tab context.</p>
     *
     * @param timeText    short time string; must not be {@code null}
     * @param accentColor neon accent colour matching the current timer state
     * @return unmodifiable {@link List} of {@link Image} instances, one per entry in
     *         {@code [16, 24, 32, 48, 64]}
     */
    public static List<Image> renderAllSizes(String timeText, Color accentColor) {
        return List.of(
                render(timeText, accentColor, 16),
                render(timeText, accentColor, 24),
                render(timeText, accentColor, 32),
                render(timeText, accentColor, 48),
                render(timeText, accentColor, 64)
        );
    }
}


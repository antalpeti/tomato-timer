package com.tomatotimer;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Renders a small timer-countdown icon suitable for the Windows taskbar.
 *
 * <p>The icon is produced entirely with JavaFX {@link Canvas} / {@link javafx.scene.canvas.GraphicsContext},
 * so no AWT, no BufferedImage and no JNI are required.  The resulting
 * {@link Image} is passed directly to {@code Stage.getIcons()}.</p>
 *
 * <h3>Visual design</h3>
 * <ul>
 *   <li>64 × 64 px square canvas.</li>
 *   <li>Near-black rounded-rectangle background with slight transparency.</li>
 *   <li>Thin accent-coloured border that matches the active {@link NeonPreset}.</li>
 *   <li>Bold centred countdown text (font size adapts to string length).</li>
 * </ul>
 *
 * <p><strong>Thread note:</strong> must be called on the JavaFX Application Thread
 * ({@link Canvas#snapshot} requires it).</p>
 */
public final class TaskbarIconRenderer {

    /** Rendered icon side length in pixels (square). */
    private static final int SIZE = 64;

    /** Background colour – near-black with very slight blue tint. */
    private static final Color BACKGROUND = Color.rgb(10, 10, 18, 0.94);

    /** Corner arc radius for the rounded rectangle. */
    private static final double ARC = 12.0;

    /** Border stroke width. */
    private static final double BORDER_WIDTH = 2.5;

    private TaskbarIconRenderer() { /* utility class – no instances */ }

    /**
     * Renders a taskbar icon showing the remaining-time countdown.
     *
     * <p>The font size is automatically adjusted to fit the time string:</p>
     * <ul>
     *   <li>≤ 4 chars ({@code "1:23"})  → 22 px</li>
     *   <li>5 chars  ({@code "59:59"}) → 18 px</li>
     *   <li>≥ 6 chars ({@code "1:23:45"}) → 14 px</li>
     * </ul>
     *
     * @param timeText    short time string, e.g. {@code "4:32"} or {@code "1:04:32"};
     *                    must not be {@code null}
     * @param accentColor neon accent colour that matches the current timer state
     *                    (derived from the active {@link NeonPreset})
     * @return a {@value SIZE}×{@value SIZE} JavaFX {@link Image} ready to be
     *         placed in {@code stage.getIcons()}
     */
    public static Image render(String timeText, Color accentColor) {
        final var canvas = new Canvas(SIZE, SIZE);
        final var gc     = canvas.getGraphicsContext2D();

        // ── Background ────────────────────────────────────────────────────────
        gc.setFill(BACKGROUND);
        gc.fillRoundRect(0, 0, SIZE, SIZE, ARC, ARC);

        // ── Accent border ─────────────────────────────────────────────────────
        gc.setStroke(accentColor.deriveColor(0.0, 1.0, 1.0, 0.82));
        gc.setLineWidth(BORDER_WIDTH);
        final double inset = BORDER_WIDTH / 2.0;
        gc.strokeRoundRect(inset, inset, SIZE - BORDER_WIDTH, SIZE - BORDER_WIDTH,
                ARC - inset, ARC - inset);

        // ── Inner subtle glow ring ────────────────────────────────────────────
        gc.setStroke(accentColor.deriveColor(0.0, 1.0, 1.0, 0.18));
        gc.setLineWidth(3.5);
        gc.strokeRoundRect(inset + 3, inset + 3, SIZE - BORDER_WIDTH - 6, SIZE - BORDER_WIDTH - 6,
                ARC - 4, ARC - 4);

        // ── Timer text ────────────────────────────────────────────────────────
        final double fontSize = switch (timeText.length()) {
            case 1, 2, 3, 4 -> 22.0;   // "1:23", "59"
            case 5          -> 18.0;   // "59:59"
            default         -> 14.0;   // "1:23:45" or longer
        };

        gc.setFill(accentColor);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, fontSize));

        // Vertically centre: 0.38 × fontSize is a good baseline-to-centre offset for
        // SansSerif bold at these sizes.
        gc.fillText(timeText, SIZE / 2.0, SIZE / 2.0 + fontSize * 0.38);

        // ── Snapshot ──────────────────────────────────────────────────────────
        final var params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }
}


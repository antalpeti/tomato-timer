package com.tomatotimer;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

/**
 * Central factory for scalable, vector-based application icons.
 *
 * <p>All paths use a 24 × 24 nominal viewbox (Material Design convention).
 * Call {@link #resize(Group, double)} to change the rendered size at any time;
 * the underlying {@link Scale} transform is stored as the icon {@link Group}'s
 * {@code userData} for convenient retrieval.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 *   Group icon = IconFactory.create(IconFactory.PATH_PLAY, IconFactory.COLOR_PLAY);
 *   myButton.setGraphic(icon);
 *   // later, on resize:
 *   IconFactory.resize(icon, 28.0);
 * }</pre>
 */
public final class IconFactory {

    /** Nominal SVG viewbox side length (pixels). */
    public static final double NOMINAL = 24.0;

    // ── Vivid colour palette (dark-background–optimised) ──────────────────────

    /** Lavender – settings gear. */
    public static final String COLOR_SETTINGS = "#A29BFE";
    /** Salmon-orange – reset / replay. */
    public static final String COLOR_RESET    = "#FFA07A";
    /** Teal – play / resume. */
    public static final String COLOR_PLAY     = "#4ECDC4";
    /** Warm yellow – pause. */
    public static final String COLOR_PAUSE    = "#FFDE57";
    /** Coral / tomato – start work. */
    public static final String COLOR_WORK     = "#FF6B6B";
    /** Sky blue – relax / break. */
    public static final String COLOR_RELAX    = "#74B9FF";
    /** Light grey – back / navigation neutral. */
    public static final String COLOR_BACK     = "#B2BEC3";
    /** Mint – Google Calendar. */
    public static final String COLOR_CALENDAR = "#55EFC4";
    /** Gold – volume / sound. */
    public static final String COLOR_VOLUME   = "#FDCB6E";
    /** Near-white – always-on-top pin (inactive). */
    public static final String COLOR_PIN      = "#DFE6E9";
    /** Lavender – always-on-top pin (active). */
    public static final String COLOR_PIN_ON   = "#A29BFE";
    /** Red – close / cancel. */
    public static final String COLOR_CLOSE    = "#FF7675";
    /** Ice teal – taskbar / display settings. */
    public static final String COLOR_TASKBAR  = "#81ECEC";

    // ── SVG path data (Material Design spec, 24 × 24 viewbox) ─────────────────

    /** Gear / settings. */
    public static final String PATH_SETTINGS =
        "M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5" +
        "A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5" +
        "M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11" +
        "L21.54,9.37C21.73,9.22 21.78,8.95 21.67,8.72L19.67,5.28" +
        "C19.54,5.05 19.27,4.96 19.04,5.05L16.56,6.05" +
        "C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42" +
        "C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07" +
        "C8.5,5.32 7.96,5.66 7.44,6.05L4.96,5.05" +
        "C4.73,4.96 4.46,5.05 4.33,5.28L2.33,8.72" +
        "C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11" +
        "C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97" +
        "L2.46,14.63C2.27,14.78 2.21,15.05 2.33,15.28L4.33,18.72" +
        "C4.46,18.95 4.73,19.03 4.96,18.95L7.44,17.94" +
        "C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58" +
        "C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58" +
        "L14.87,18.93C15.5,18.68 16.04,18.34 16.56,17.94L19.04,18.95" +
        "C19.27,19.03 19.54,18.95 19.67,18.72L21.67,15.28" +
        "C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z";

    /** Play triangle. */
    public static final String PATH_PLAY =
        "M8,5.14V19.14L19,12.14L8,5.14Z";

    /** Pause (two bars). */
    public static final String PATH_PAUSE =
        "M14,19H18V5H14M6,19H10V5H6Z";

    /** Replay / reset arrow. */
    public static final String PATH_RESET =
        "M12,5V1L7,6L12,11V7A6,6 0 0,1 18,13A6,6 0 0,1 12,19" +
        "A6,6 0 0,1 6,13H4A8,8 0 0,0 12,21A8,8 0 0,0 20,13A8,8 0 0,0 12,5Z";

    /** Briefcase – work / pomodoro. */
    public static final String PATH_WORK =
        "M20,6H16V4C16,2.89 15.11,2 14,2H10C8.89,2 8,2.89 8,4V6" +
        "H4C2.89,6 2,6.89 2,8V19C2,20.11 2.89,21 4,21H20" +
        "C21.11,21 22,20.11 22,19V8C22,6.89 21.11,6 20,6M10,4H14V6H10V4Z";

    /** Gamepad – relax / break. */
    public static final String PATH_RELAX =
        "M7,6H9V9H12V11H9V14H7V11H4V9H7V6" +
        "M18,9A1,1 0 0,1 19,10A1,1 0 0,1 18,11A1,1 0 0,1 17,10A1,1 0 0,1 18,9" +
        "M15,12A1,1 0 0,1 16,13A1,1 0 0,1 15,14A1,1 0 0,1 14,13A1,1 0 0,1 15,12" +
        "M21,6H3C1.89,6 1,6.89 1,8V16C1,17.11 1.89,18 3,18H21" +
        "C22.11,18 23,17.11 23,16V8C23,6.89 22.11,6 21,6Z";

    /** Analog clock – back-to-timer navigation. */
    public static final String PATH_CLOCK =
        "M12,20A8,8 0 0,0 20,12A8,8 0 0,0 12,4A8,8 0 0,0 4,12A8,8 0 0,0 12,20" +
        "M12,2A10,10 0 0,1 22,12A10,10 0 0,1 12,22C6.47,22 2,17.5 2,12" +
        "A10,10 0 0,1 12,2M12.5,7V12.25L17,14.92L16.25,16.15L11,13V7H12.5Z";

    /** Calendar / today – Google Calendar. */
    public static final String PATH_CALENDAR =
        "M19,19H5V8H19M16,1V3H8V1H6V3H5C3.89,3 3,3.9 3,5V19" +
        "C3,20.11 3.9,21 5,21H19C20.11,21 21,20.11 21,19V5" +
        "C21,3.9 20.11,3 19,3H18V1M17,13H12V18H17V13Z";

    /** Speaker / volume on. */
    public static final String PATH_VOLUME =
        "M14,3.23V5.29C16.89,6.15 19,8.83 19,12C19,15.17 16.89,17.84 14,18.7V20.77" +
        "C18,19.86 21,16.28 21,12C21,7.72 18,4.14 14,3.23" +
        "M16.5,12C16.5,10.23 15.5,8.71 14,7.97V16C15.5,15.29 16.5,13.76 16.5,12" +
        "M3,9V15H7L12,20V4L7,9H3Z";

    /** Drawing pin – always on top. */
    public static final String PATH_PIN =
        "M16,12V4H17V2H7V4H8V12L6,14V16H11.2V22H12.8V16H18V14L16,12Z";

    /** × – close / cancel. */
    public static final String PATH_CLOSE =
        "M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12" +
        "L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z";

    /** Monitor / display – taskbar display settings. */
    public static final String PATH_TASKBAR =
        "M21,2H3C1.9,2 1,2.9 1,4V16C1,17.1 1.9,18 3,18H10L8,21V22H16V21L14,18H21" +
        "C22.1,18 23,17.1 23,16V4C23,2.9 22.1,2 21,2M21,16H3V4H21V16Z";

    // ── Factory ───────────────────────────────────────────────────────────────

    private IconFactory() { /* utility class */ }

    /**
     * Creates a scalable icon wrapped in a {@link Group}.
     *
     * <p>The icon path is drawn at {@link #NOMINAL} (24 px) and can be scaled
     * at any time via {@link #resize(Group, double)}.  The {@link Scale}
     * transform is stored as the group's {@code userData}.</p>
     *
     * @param pathData SVG path data, Material Design 24 × 24 viewbox
     * @param hexColor CSS colour string, e.g. {@code "#FF6B6B"}
     * @return a {@link Group} containing the icon
     */
    public static Group create(String pathData, String hexColor) {
        var svg = new SVGPath();
        svg.setContent(pathData);
        svg.setFill(Color.web(hexColor));
        svg.setStroke(null);

        var scale = new Scale(1.0, 1.0, 0.0, 0.0);
        svg.getTransforms().add(scale);

        var group = new Group(svg);
        group.setUserData(scale);
        return group;
    }

    /**
     * Resizes a scalable icon to the given pixel size.
     *
     * @param icon the {@link Group} returned by {@link #create}; {@code null} is ignored
     * @param size target side length in pixels (both axes)
     */
    public static void resize(Group icon, double size) {
        if (icon == null) return;
        if (icon.getUserData() instanceof Scale scale) {
            final double factor = size / NOMINAL;
            scale.setX(factor);
            scale.setY(factor);
        }
    }

    /**
     * Changes the fill colour of a previously created icon.
     *
     * @param icon     the {@link Group} returned by {@link #create}; {@code null} is ignored
     * @param hexColor new CSS colour string
     */
    public static void recolor(Group icon, String hexColor) {
        if (icon == null) return;
        for (var child : icon.getChildren()) {
            if (child instanceof SVGPath svg) {
                svg.setFill(Color.web(hexColor));
            }
        }
    }

    /**
     * Convenience: derives a suitable icon pixel size from a container's height.
     *
     * @param containerHeight height of the parent container (pixels)
     * @return icon size clamped to [16, 52]
     */
    public static double iconSizeForHeight(double containerHeight) {
        return Math.max(16.0, Math.min(52.0, containerHeight * 0.52));
    }
}


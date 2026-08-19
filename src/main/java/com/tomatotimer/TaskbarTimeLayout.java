package com.tomatotimer;

/**
 * Taskbar icon layout orientation for the countdown display.
 *
 * <ul>
 *   <li>{@link #VERTICAL} – stacked multi-line layout:
 *       2 lines (MM / SS) when hours == 0; 3 lines (HH / MM / SS) when hours &gt; 0.</li>
 *   <li>{@link #HORIZONTAL} – single-line layout:
 *       {@code "MM:SS"} when hours == 0; {@code "HH:MM:SS"} when hours &gt; 0.</li>
 * </ul>
 */
public enum TaskbarTimeLayout {

    /** Stacked multi-line display (current default behaviour). */
    VERTICAL,

    /** Single-line colon-separated display. */
    HORIZONTAL;

    /**
     * Returns the layout matching {@code name}, falling back to {@link #VERTICAL} on any error.
     *
     * @param name case-sensitive enum name, e.g. {@code "HORIZONTAL"}
     * @return the matching layout, or {@link #VERTICAL} if {@code name} is unknown or {@code null}
     */
    public static TaskbarTimeLayout fromName(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException | NullPointerException e) {
            return VERTICAL;
        }
    }
}


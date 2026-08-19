package com.tomatotimer;

/**
 * Controls how the active {@link NeonPreset} is chosen at the start of each WORK phase.
 *
 * <ul>
 *   <li>{@link #STATIC}     – keep the currently selected preset.</li>
 *   <li>{@link #SEQUENTIAL} – advance to the next preset in {@link NeonPreset#values()} order.</li>
 *   <li>{@link #RANDOM}     – pick a uniformly-random preset that differs from the current one.</li>
 *   <li>{@link #SHUFFLE}    – walk a randomised, non-repeating cycle of all presets; reshuffle
 *                             automatically when the cycle is exhausted.</li>
 * </ul>
 *
 * <p>The active mode is persisted via {@link AppSettings#getThemeSelectionMode()} /
 * {@link AppSettings#setThemeSelectionMode(ThemeSelectionMode)} and applied inside
 * {@link com.tomatotimer.controller.MainController#startWork()}.</p>
 */
public enum ThemeSelectionMode {

    /** Keep the currently selected {@link NeonPreset}. */
    STATIC("Static"),

    /** Advance to the next item in {@link NeonPreset#values()} order on each WORK start. */
    SEQUENTIAL("Sequential"),

    /** Pick a uniformly-random preset different from the currently active one. */
    RANDOM("Random"),

    /**
     * Walk a randomised, non-repeating permutation of all presets.
     * A new permutation is generated automatically after the current one is exhausted.
     */
    SHUFFLE("Shuffle");

    // ── Fields ────────────────────────────────────────────────────────────────

    private final String displayName;

    // ── Constructor ───────────────────────────────────────────────────────────

    ThemeSelectionMode(String displayName) {
        this.displayName = displayName;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /** Human-readable label shown in the Settings ComboBox. */
    public String getDisplayName() { return displayName; }

    // ── Utility ───────────────────────────────────────────────────────────────

    /**
     * Resolves a saved enum constant name back to a {@link ThemeSelectionMode},
     * returning {@link #STATIC} as the safe fallback for unknown/corrupt data.
     * The first-run prefs default is {@link #SHUFFLE} (set in {@link AppSettings}).
     *
     * @param savedName the value previously returned by {@link #name()} and stored in prefs
     * @return the matching mode, never {@code null}
     */
    public static ThemeSelectionMode fromName(String savedName) {
        for (final var m : values()) {
            if (m.name().equals(savedName)) return m;
        }
        return STATIC;
    }

    /** Returns {@link #getDisplayName()} so JavaFX ComboBox renders the label automatically. */
    @Override
    public String toString() { return displayName; }
}


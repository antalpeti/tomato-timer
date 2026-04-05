package com.tomatotimer;

/**
 * Global glow intensity profile applied on top of every {@link NeonPreset}.
 *
 * <p>Each profile supplies multiplicative scale factors for the visual glow
 * parameters computed by {@link TimerBackgroundHelper}.  The preset parameters
 * stored in {@link NeonPreset} are treated as the <strong>BALANCED</strong>
 * baseline (all factors = {@code 1.00}).
 *
 * <table>
 *   <caption>Profile overview</caption>
 *   <tr><th>Profile</th><th>Effect</th></tr>
 *   <tr><td>{@link #SOFT}</td>
 *       <td>Reduced alpha, spread, sheen and white-blend; slightly heavier darken.</td></tr>
 *   <tr><td>{@link #BALANCED}</td>
 *       <td>No scaling – identical to the raw preset values (application default).</td></tr>
 *   <tr><td>{@link #VIVID}</td>
 *       <td>Boosted alpha, spread, sheen and white-blend; slightly lighter darken.</td></tr>
 * </table>
 *
 * <p>The active profile is persisted via {@link AppSettings#getNeonGlowProfile()} /
 * {@link AppSettings#setNeonGlowProfile(NeonGlowProfile)} and applied at
 * render-time inside {@link TimerBackgroundHelper}.</p>
 */
public enum NeonGlowProfile {

    /**
     * Subdued glow: reduced alpha/opacity, spread, sheen and white-blend;
     * slightly heavier darken produces a calmer, less distracting appearance.
     */
    SOFT(0.65, 0.70, 0.70, 0.65, 1.15),

    /**
     * Neutral baseline – all scale factors are {@code 1.00} so the
     * {@link NeonPreset} parameters are used verbatim.
     * This is the application default.
     */
    BALANCED(1.00, 1.00, 1.00, 1.00, 1.00),

    /**
     * Intensified glow: boosted alpha/opacity, spread, sheen and white-blend;
     * slightly lighter darken produces a brighter, more saturated neon effect.
     */
    VIVID(1.30, 1.25, 1.25, 1.25, 0.85);

    // ── Scale factors ─────────────────────────────────────────────────────────

    /** Multiplier for opacity / alpha values (glow strength). */
    private final double factorAlpha;

    /** Multiplier for glow spread. */
    private final double factorSpread;

    /** Multiplier for white-blend luminosity boost. */
    private final double factorWhiteBlend;

    /** Multiplier for sheen alpha. */
    private final double factorSheen;

    /**
     * Multiplier for darken factor ({@code > 1.0} = darker edge / heavier shadow;
     * {@code < 1.0} = lighter edge / less shadow).
     */
    private final double factorDarken;

    // ── Constructor ───────────────────────────────────────────────────────────

    NeonGlowProfile(double factorAlpha, double factorSpread, double factorWhiteBlend,
                    double factorSheen, double factorDarken) {
        this.factorAlpha      = factorAlpha;
        this.factorSpread     = factorSpread;
        this.factorWhiteBlend = factorWhiteBlend;
        this.factorSheen      = factorSheen;
        this.factorDarken     = factorDarken;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    /** Scale factor for opacity / alpha values. */
    public double getFactorAlpha()      { return factorAlpha; }

    /** Scale factor for glow spread. */
    public double getFactorSpread()     { return factorSpread; }

    /** Scale factor for white-blend luminosity boost. */
    public double getFactorWhiteBlend() { return factorWhiteBlend; }

    /** Scale factor for sheen alpha. */
    public double getFactorSheen()      { return factorSheen; }

    /** Scale factor for the darken edge shadow. */
    public double getFactorDarken()     { return factorDarken; }

    // ── Utility ───────────────────────────────────────────────────────────────

    /**
     * Resolves a saved enum constant name back to a {@link NeonGlowProfile},
     * returning {@link #BALANCED} as the safe default if no match is found.
     *
     * @param savedName the value previously returned by {@link #name()} and stored in prefs
     * @return the matching profile, never {@code null}
     */
    public static NeonGlowProfile fromName(String savedName) {
        for (final var p : values()) {
            if (p.name().equals(savedName)) return p;
        }
        return BALANCED;
    }
}


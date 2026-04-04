package com.tomatotimer;

import javafx.scene.paint.Color;

/**
 * Neon visual-theme presets for the timer.
 *
 * <p>Each constant bundles the complete set of accent colours and gradient-tuning
 * parameters consumed by {@link TimerBackgroundHelper}.  Adding a new preset
 * requires only a new enum constant – no changes to the rendering code.</p>
 *
 * <h3>Presets</h3>
 * <ul>
 *   <li>{@link #NEON_BALANCED} – the original balanced cyberpunk-neon palette
 *       with moderate glow intensity (default).</li>
 *   <li>{@link #ULTRA_NEON} – maximum-intensity mode: purer accent colours,
 *       wider halos, stronger bloom, and higher contrast base gradient.</li>
 * </ul>
 */
public enum NeonPreset {

    // ── NEON_BALANCED ─────────────────────────────────────────────────────────
    /**
     * Balanced neon colour scheme with moderate glow intensity.
     * Colours and tuning values exactly match the original implementation.
     */
    NEON_BALANCED(
            "Neon Balanced",
            // ── Accent colours ────────────────────────────────────────────────
            Color.web("#7000FF"),   // WORK normal  – electric indigo-violet
            Color.web("#00E5FF"),   // RELAX normal – cyber cyan
            Color.web("#00FF88"),   // RELAX_LONG   – vivid neon mint
            Color.web("#FFE600"),   // WARNING      – pure neon yellow
            Color.web("#FF0066"),   // OVERTIME     – cyberpunk hot-pink
            Color.web("#C070FF"),   // PAUSED       – vivid neon purple
            // ── Root gradient tuning ──────────────────────────────────────────
            0.88, 0.36,             // ROOT_DARK_EDGE, ROOT_DARK_MID
            0.38, 0.80,             // ROOT_GLOW_ALPHA_MIN, ROOT_GLOW_ALPHA_MAX
            0.32,                   // ROOT_SHEEN_ALPHA
            0.45, 0.03,             // ROOT_GLOW_WHITE_BLEND, ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning ────────────────────────────────────────────────────
            0.68, 0.08,             // BAR_LIGHTEN, BAR_DARKEN
            0.42,                   // BAR_INNER_SHEEN
            18.0, 0.72, 0.97, 0.35  // BAR_GLOW_RADIUS, BAR_GLOW_SPREAD, BAR_GLOW_OPACITY, BAR_GLOW_WHITE_BLEND
    ),

    // ── ULTRA_NEON ────────────────────────────────────────────────────────────
    /**
     * Maximum-intensity neon theme.
     * Purer, more saturated accent colours; higher contrast base gradient (darker
     * edges, brighter centre glow band); wider and stronger progress-bar halo.
     */
    ULTRA_NEON(
            "Ultra Neon",
            // ── Accent colours – purer and more saturated ─────────────────────
            Color.web("#5500FF"),   // WORK normal  – deeper electric violet
            Color.web("#00F5FF"),   // RELAX normal – brighter pure cyan
            Color.web("#00FF55"),   // RELAX_LONG   – purer neon green
            Color.web("#FFEE00"),   // WARNING      – vivid warm yellow
            Color.web("#FF0055"),   // OVERTIME     – vivid hot-pink-red
            Color.web("#EE00FF"),   // PAUSED       – pure neon magenta
            // ── Root gradient tuning – higher contrast / stronger bloom ────────
            0.84, 0.18,             // ROOT_DARK_EDGE darker gap → extreme bloom contrast
            0.52, 0.95,             // ROOT_GLOW_ALPHA_MIN / MAX – much stronger glow
            0.48,                   // ROOT_SHEEN_ALPHA – stronger sheen
            0.62, 0.01,             // ROOT_GLOW_WHITE_BLEND (aggressive bloom) / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – brighter, wider, more intense ────────────────────
            0.78, 0.05,             // BAR_LIGHTEN (brighter top) / BAR_DARKEN (shallower shadow)
            0.58,                   // BAR_INNER_SHEEN – strong neon-tube glass effect
            26.0, 0.82, 1.0, 0.50   // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    );

    // ── Fields ────────────────────────────────────────────────────────────────

    private final String displayName;

    // Accent colours
    private final Color workNormal;
    private final Color relaxNormal;
    private final Color relaxLongNormal;
    private final Color warningColor;
    private final Color overColor;
    private final Color pausedColor;

    // Root gradient tuning
    private final double rootDarkEdge;
    private final double rootDarkMid;
    private final double rootGlowAlphaMin;
    private final double rootGlowAlphaMax;
    private final double rootSheenAlpha;
    private final double rootGlowWhiteBlend;
    private final double rootGlowBlackBlend;

    // Bar tuning
    private final double barLighten;
    private final double barDarken;
    private final double barInnerSheen;
    private final double barGlowRadius;
    private final double barGlowSpread;
    private final double barGlowOpacity;
    private final double barGlowWhiteBlend;

    // ── Constructor ───────────────────────────────────────────────────────────

    @SuppressWarnings("java:S107") // many parameters intentional for a value-object enum
    NeonPreset(String displayName,
               Color workNormal, Color relaxNormal, Color relaxLongNormal,
               Color warningColor, Color overColor, Color pausedColor,
               double rootDarkEdge,  double rootDarkMid,
               double rootGlowAlphaMin, double rootGlowAlphaMax,
               double rootSheenAlpha,
               double rootGlowWhiteBlend, double rootGlowBlackBlend,
               double barLighten,  double barDarken,
               double barInnerSheen,
               double barGlowRadius, double barGlowSpread,
               double barGlowOpacity, double barGlowWhiteBlend) {

        this.displayName        = displayName;
        this.workNormal         = workNormal;
        this.relaxNormal        = relaxNormal;
        this.relaxLongNormal    = relaxLongNormal;
        this.warningColor       = warningColor;
        this.overColor          = overColor;
        this.pausedColor        = pausedColor;
        this.rootDarkEdge       = rootDarkEdge;
        this.rootDarkMid        = rootDarkMid;
        this.rootGlowAlphaMin   = rootGlowAlphaMin;
        this.rootGlowAlphaMax   = rootGlowAlphaMax;
        this.rootSheenAlpha     = rootSheenAlpha;
        this.rootGlowWhiteBlend = rootGlowWhiteBlend;
        this.rootGlowBlackBlend = rootGlowBlackBlend;
        this.barLighten         = barLighten;
        this.barDarken          = barDarken;
        this.barInnerSheen      = barInnerSheen;
        this.barGlowRadius      = barGlowRadius;
        this.barGlowSpread      = barGlowSpread;
        this.barGlowOpacity     = barGlowOpacity;
        this.barGlowWhiteBlend  = barGlowWhiteBlend;
    }

    // ── Public accessors ──────────────────────────────────────────────────────

    /**
     * Human-readable display name shown in the Settings ComboBox.
     * Also used by {@link #toString()} so JavaFX renders it directly.
     */
    public String getDisplayName() { return displayName; }

    /**
     * Accent colour for the normal (0 – 80 %) phase of the given timer mode.
     *
     * @param mode current {@link TimerMode}
     * @return the base accent colour for that mode
     */
    public Color normalColorFor(TimerMode mode) {
        return switch (mode) {
            case WORK       -> workNormal;
            case RELAX      -> relaxNormal;
            case RELAX_LONG -> relaxLongNormal;
        };
    }

    public Color   getWarningColor()        { return warningColor; }
    public Color   getOverColor()           { return overColor; }
    public Color   getPausedColor()         { return pausedColor; }

    public double  getRootDarkEdge()        { return rootDarkEdge; }
    public double  getRootDarkMid()         { return rootDarkMid; }
    public double  getRootGlowAlphaMin()    { return rootGlowAlphaMin; }
    public double  getRootGlowAlphaMax()    { return rootGlowAlphaMax; }
    public double  getRootSheenAlpha()      { return rootSheenAlpha; }
    public double  getRootGlowWhiteBlend()  { return rootGlowWhiteBlend; }
    public double  getRootGlowBlackBlend()  { return rootGlowBlackBlend; }

    public double  getBarLighten()          { return barLighten; }
    public double  getBarDarken()           { return barDarken; }
    public double  getBarInnerSheen()       { return barInnerSheen; }
    public double  getBarGlowRadius()       { return barGlowRadius; }
    public double  getBarGlowSpread()       { return barGlowSpread; }
    public double  getBarGlowOpacity()      { return barGlowOpacity; }
    public double  getBarGlowWhiteBlend()   { return barGlowWhiteBlend; }

    // ── Utility ───────────────────────────────────────────────────────────────

    /**
     * Resolves a saved enum constant name back to a {@link NeonPreset},
     * returning {@link #NEON_BALANCED} as the safe default if no match is found.
     *
     * @param savedName the value previously returned by {@link #name()} and stored in prefs
     * @return the matching preset, never {@code null}
     */
    public static NeonPreset fromName(String savedName) {
        for (final var p : values()) {
            if (p.name().equals(savedName)) return p;
        }
        return NEON_BALANCED;
    }

    /** Returns {@link #getDisplayName()} so JavaFX ComboBox renders the label automatically. */
    @Override
    public String toString() { return displayName; }
}


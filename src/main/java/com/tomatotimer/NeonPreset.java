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
 *   <li>{@link #NIGHT_RUNNER} – retrowave/synthwave night-racing atmosphere:
 *       warm sunset oranges and deep teals on an ultra-dark background with
 *       soft atmospheric sheen and medium-radius glow halos.</li>
 *   <li>{@link #SOLAR_FLARE} – stellar-fire palette of fiery oranges, amber and
 *       solar gold with an aggressive bloom centre and hot glowing bar.</li>
 *   <li>{@link #ARCTIC_PULSE} – crisp polar-ice blues and cold whites on a
 *       near-black background with delicate frosty sheen.</li>
 *   <li>{@link #TOXIC_LIME} – radioactive acid-green and chartreuse hazard
 *       colours with a high-contrast industrial glow.</li>
 *   <li>{@link #SYNTH_SUNSET} – 80s synthwave with hot neon-pink, deep electric
 *       purple and warm peach on a very dark dusk background.</li>
 *   <li>{@link #DEEP_OCEAN} – bioluminescent deep-sea palette of electric blue,
 *       aquamarine and indigo on an ultra-dark abyss background.</li>
 *   <li>{@link #CRIMSON_REACTOR} – nuclear-reactor danger theme with deep
 *       crimson, hot coral and amber warning tones.</li>
 *   <li>{@link #MONOCHROME_PLASMA} – minimalist silver-white plasma on dark,
 *       soft bloom and low-saturation accent colours.</li>
 *   <li>{@link #AURORA_DRIFT} – aurora borealis shifting greens, soft cyan and
 *       violet on a midnight-dark sky background.</li>
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
     ),

    // ── NIGHT_RUNNER ──────────────────────────────────────────────────────────
    /**
     * Retrowave / synthwave night-racing atmosphere.
     * Warm sunset oranges and deep teals on an ultra-dark background with
     * a soft atmospheric sheen and medium-radius glow halos – a clearly
     * distinct mood from both {@link #NEON_BALANCED} and {@link #ULTRA_NEON}.
     */
    NIGHT_RUNNER(
            "Night Runner",
            // ── Accent colours – retrowave sunset palette ─────────────────────
            Color.web("#FF6B35"),   // WORK normal  – warm retro sunset-orange
            Color.web("#00CED1"),   // RELAX normal – deep dark-turquoise teal
            Color.web("#3DFFD0"),   // RELAX_LONG   – electric mint-teal
            Color.web("#FF8C00"),   // WARNING      – deep amber
            Color.web("#DC143C"),   // OVERTIME     – classic crimson
            Color.web("#FF69B4"),   // PAUSED       – hot-pink rose
            // ── Root gradient – ultra-dark edges, deep atmospheric centre ─────
            0.93, 0.28,             // ROOT_DARK_EDGE (near-black edges) / ROOT_DARK_MID
            0.28, 0.70,             // ROOT_GLOW_ALPHA_MIN / MAX – softer, more atmospheric glow
            0.22,                   // ROOT_SHEEN_ALPHA – subtle sheen suits the "night" feel
            0.38, 0.05,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – medium radius, warm soft-tube glass effect ───────
            0.64, 0.11,             // BAR_LIGHTEN / BAR_DARKEN
            0.36,                   // BAR_INNER_SHEEN
            22.0, 0.68, 0.88, 0.28  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── SOLAR_FLARE ───────────────────────────────────────────────────────────
    /**
     * Stellar-fire palette: blazing oranges, amber and solar gold.
     * Aggressive bloom centre and a hot, wide glow halo around the progress bar.
     */
    SOLAR_FLARE(
            "Solar Flare",
            // ── Accent colours – stellar combustion palette ───────────────────
            Color.web("#FF4500"),   // WORK normal  – deep solar orange-red
            Color.web("#FF9500"),   // RELAX normal – warm amber-orange
            Color.web("#FFD700"),   // RELAX_LONG   – solar gold
            Color.web("#FFFF33"),   // WARNING      – bright solar white-yellow
            Color.web("#FF2200"),   // OVERTIME     – fire-engine red
            Color.web("#FF6600"),   // PAUSED       – tangerine
            // ── Root gradient – hot bright core, near-black corona ────────────
            0.90, 0.32,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.44, 0.88,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.40,                   // ROOT_SHEEN_ALPHA
            0.50, 0.02,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – bright, wide, hot-tube flare ─────────────────────
            0.75, 0.07,             // BAR_LIGHTEN / BAR_DARKEN
            0.50,                   // BAR_INNER_SHEEN
            24.0, 0.78, 0.95, 0.45  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── ARCTIC_PULSE ──────────────────────────────────────────────────────────
    /**
     * Polar-ice atmosphere: crystal deep-sky blues and cold whites on a
     * near-black background with a delicate frosty sheen and restrained glow.
     */
    ARCTIC_PULSE(
            "Arctic Pulse",
            // ── Accent colours – polar ice and frozen sky ─────────────────────
            Color.web("#00BFFF"),   // WORK normal  – deep sky blue
            Color.web("#87CEEB"),   // RELAX normal – ice sky blue
            Color.web("#AAFFF0"),   // RELAX_LONG   – glacier mint
            Color.web("#FFE4B5"),   // WARNING      – distant ice-sun cream
            Color.web("#FF6688"),   // OVERTIME     – cold alarm rose
            Color.web("#B0E0FF"),   // PAUSED       – powder blue
            // ── Root gradient – crisp dark edges, cool frosty centre ──────────
            0.92, 0.40,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.30, 0.65,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.25,                   // ROOT_SHEEN_ALPHA
            0.45, 0.04,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – crisp, medium halo, icy glass effect ─────────────
            0.65, 0.10,             // BAR_LIGHTEN / BAR_DARKEN
            0.38,                   // BAR_INNER_SHEEN
            20.0, 0.65, 0.82, 0.40  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── TOXIC_LIME ────────────────────────────────────────────────────────────
    /**
     * Radioactive industrial hazard theme: acid chartreuse and neon-green
     * with a high-contrast toxic glow and warning-yellow danger tones.
     */
    TOXIC_LIME(
            "Toxic Lime",
            // ── Accent colours – radioactive acid palette ─────────────────────
            Color.web("#AAFF00"),   // WORK normal  – chartreuse-lime
            Color.web("#00FF44"),   // RELAX normal – pure neon green
            Color.web("#CCFF33"),   // RELAX_LONG   – radioactive yellow-green
            Color.web("#FFFF00"),   // WARNING      – hazard yellow
            Color.web("#FF4400"),   // OVERTIME     – toxic-spill hot orange-red
            Color.web("#66FF00"),   // PAUSED       – bright lime
            // ── Root gradient – near-black base, toxic bloom centre ───────────
            0.89, 0.25,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.45, 0.90,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.35,                   // ROOT_SHEEN_ALPHA
            0.40, 0.02,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – high-intensity halo, sharp glass highlight ───────
            0.72, 0.06,             // BAR_LIGHTEN / BAR_DARKEN
            0.48,                   // BAR_INNER_SHEEN
            20.0, 0.75, 0.94, 0.38  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── SYNTH_SUNSET ──────────────────────────────────────────────────────────
    /**
     * 80s synthwave sunset: hot neon-pink, deep electric purple and warm
     * peach on a very dark dusk background with a dreamy glow halo.
     */
    SYNTH_SUNSET(
            "Synth Sunset",
            // ── Accent colours – synthwave dusk palette ───────────────────────
            Color.web("#FF2D78"),   // WORK normal  – hot neon-pink
            Color.web("#A020F0"),   // RELAX normal – deep electric purple
            Color.web("#FF8C55"),   // RELAX_LONG   – warm peach-orange
            Color.web("#FFD700"),   // WARNING      – sunset gold
            Color.web("#FF0033"),   // OVERTIME     – vivid danger red
            Color.web("#D040FF"),   // PAUSED       – neon violet
            // ── Root gradient – very dark dusk edges, warm glowing core ───────
            0.91, 0.30,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.35, 0.78,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.30,                   // ROOT_SHEEN_ALPHA
            0.42, 0.04,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – dreamy medium-wide glow, warm neon-tube feel ─────
            0.70, 0.09,             // BAR_LIGHTEN / BAR_DARKEN
            0.44,                   // BAR_INNER_SHEEN
            22.0, 0.72, 0.91, 0.38  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── DEEP_OCEAN ────────────────────────────────────────────────────────────
    /**
     * Bioluminescent deep-sea palette: electric blue, aquamarine and deep
     * indigo on an ultra-dark abyss background with a cool restrained glow.
     */
    DEEP_OCEAN(
            "Deep Ocean",
            // ── Accent colours – abyssal bioluminescence palette ──────────────
            Color.web("#0055FF"),   // WORK normal  – electric deep-sea blue
            Color.web("#00E5CC"),   // RELAX normal – bioluminescent teal
            Color.web("#00FFD0"),   // RELAX_LONG   – aquamarine glow
            Color.web("#33DDFF"),   // WARNING      – bright aqua-cyan
            Color.web("#7700FF"),   // OVERTIME     – deep indigo-violet
            Color.web("#0099DD"),   // PAUSED       – ocean cobalt
            // ── Root gradient – near-black abyss, cool restrained centre ──────
            0.95, 0.22,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.25, 0.72,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.20,                   // ROOT_SHEEN_ALPHA
            0.35, 0.06,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – moderate depth-glow, cool submarine glass ────────
            0.62, 0.12,             // BAR_LIGHTEN / BAR_DARKEN
            0.32,                   // BAR_INNER_SHEEN
            21.0, 0.66, 0.85, 0.30  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── CRIMSON_REACTOR ───────────────────────────────────────────────────────
    /**
     * Nuclear-reactor danger theme: deep crimson, hot coral-red and amber
     * warning tones on a dark background with a pulsing danger glow.
     */
    CRIMSON_REACTOR(
            "Crimson Reactor",
            // ── Accent colours – reactor danger palette ───────────────────────
            Color.web("#CC0022"),   // WORK normal  – deep reactor crimson
            Color.web("#FF4455"),   // RELAX normal – hot coral-red
            Color.web("#FF6600"),   // RELAX_LONG   – reactor orange
            Color.web("#FF9900"),   // WARNING      – nuclear amber
            Color.web("#FF0000"),   // OVERTIME     – pure danger red
            Color.web("#880033"),   // PAUSED       – deep blood red
            // ── Root gradient – dark edges, hot danger core ───────────────────
            0.90, 0.28,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.40, 0.85,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.38,                   // ROOT_SHEEN_ALPHA
            0.48, 0.03,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – pulsing danger halo, hot neon-tube glass ─────────
            0.73, 0.08,             // BAR_LIGHTEN / BAR_DARKEN
            0.46,                   // BAR_INNER_SHEEN
            23.0, 0.76, 0.93, 0.42  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── MONOCHROME_PLASMA ─────────────────────────────────────────────────────
    /**
     * Minimalist silver-white plasma on dark: low-saturation cool-white accent
     * colours, soft bloom and a refined glass-tube bar effect.
     */
    MONOCHROME_PLASMA(
            "Monochrome Plasma",
            // ── Accent colours – desaturated cool-white plasma palette ────────
            Color.web("#DDDDFF"),   // WORK normal  – cool white-lavender
            Color.web("#AABBCC"),   // RELAX normal – steel blue-grey
            Color.web("#CCEEEE"),   // RELAX_LONG   – soft ice-white
            Color.web("#FFFF88"),   // WARNING      – pale mellow yellow
            Color.web("#FF7788"),   // OVERTIME     – soft alarm rose
            Color.web("#BBBBCC"),   // PAUSED       – neutral grey-lavender
            // ── Root gradient – dark edges, bright diffuse centre bloom ───────
            0.86, 0.34,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.36, 0.74,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.28,                   // ROOT_SHEEN_ALPHA
            0.55, 0.03,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – subtle wide bloom, polished glass highlight ──────
            0.60, 0.14,             // BAR_LIGHTEN / BAR_DARKEN
            0.35,                   // BAR_INNER_SHEEN
            18.0, 0.60, 0.80, 0.50  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── AURORA_DRIFT ──────────────────────────────────────────────────────────
    /**
     * Aurora borealis atmosphere: shifting neon-greens, soft cyan and violet
     * curtains on a midnight-dark sky with a gentle atmospheric bloom.
     */
    AURORA_DRIFT(
            "Aurora Drift",
            // ── Accent colours – aurora curtain palette ───────────────────────
            Color.web("#00FF99"),   // WORK normal  – aurora green
            Color.web("#44DDFF"),   // RELAX normal – aurora cyan-blue
            Color.web("#88FFCC"),   // RELAX_LONG   – pale aurora mint
            Color.web("#BBFF44"),   // WARNING      – aurora yellow-green
            Color.web("#FF55AA"),   // OVERTIME     – magenta aurora flare
            Color.web("#AA44FF"),   // PAUSED       – aurora violet
            // ── Root gradient – midnight-dark sky, gentle drifting glow ───────
            0.91, 0.26,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.32, 0.76,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.27,                   // ROOT_SHEEN_ALPHA
            0.38, 0.05,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – soft wide curtain glow, gentle aurora tube ───────
            0.66, 0.10,             // BAR_LIGHTEN / BAR_DARKEN
            0.40,                   // BAR_INNER_SHEEN
            22.0, 0.70, 0.88, 0.33  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
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


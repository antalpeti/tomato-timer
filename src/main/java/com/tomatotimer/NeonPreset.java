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
 *   <li>{@link #NIGHT_RUNNER} – retrowave/synthwave night-racing atmosphere:
 *       warm sunset oranges and deep teals on an ultra-dark background with
 *       soft atmospheric sheen and medium-radius glow halos (default).</li>
 *   <li>{@link #ARCTIC_PULSE} – crisp polar-ice blues and cold whites on a
 *       near-black background with delicate frosty sheen.</li>
 *   <li>{@link #MONOCHROME_PLASMA} – minimalist silver-white plasma on dark,
 *       soft bloom and low-saturation accent colours.</li>
 *   <li>{@link #AURORA_DRIFT} – aurora borealis shifting greens, soft cyan and
 *       violet on a midnight-dark sky background.</li>
 *   <li>{@link #RUBY_FLAME} – volcanic crimson fantasy: deep red work accent
 *       with warm amber relaxation tones and a blazing glow halo.</li>
 *   <li>{@link #AMBER_DUNE} – desert-heat fantasy: rich amber-orange work accent
 *       with sun-gold relaxation tones and a warm ember glow.</li>
 *   <li>{@link #SOLAR_CANARY} – radiant solar fantasy: bright canary-yellow work
 *       accent with golden relaxation tones and a sunshine bloom.</li>
 *   <li>{@link #EMERALD_BLOOM} – forest-magic fantasy: vivid emerald-green work
 *       accent with cool cyan relaxation tones and a lush glow halo.</li>
 *   <li>{@link #AZURE_WAVE} – ocean-spirit fantasy: electric azure-blue work
 *       accent with icy cyan relaxation tones and a cool wave glow.</li>
 *   <li>{@link #INDIGO_ORBIT} – cosmic-void fantasy: deep indigo work accent
 *       with soft violet relaxation tones and a mysterious orbital glow.</li>
 *   <li>{@link #VIOLET_NOVA} – stellar-burst fantasy: vivid violet work accent
 *       with soft lavender relaxation tones and a nova bloom halo.</li>
 * </ul>
 */
public enum NeonPreset {

    // ── NIGHT_RUNNER ──────────────────────────────────────────────────────────
    /**
     * Retrowave / synthwave night-racing atmosphere.
     * Warm sunset oranges and deep teals on an ultra-dark background with
     * a soft atmospheric sheen and medium-radius glow halos.
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
    ),

    // ── RUBY_FLAME ────────────────────────────────────────────────────────────
    /**
     * Volcanic crimson fantasy: deep red work accent with warm amber
     * relaxation tones and a blazing glow halo.
     */
    RUBY_FLAME(
            "Ruby Flame",
            // ── Accent colours – volcanic ruby palette ────────────────────────
            Color.web("#FF3B30"),   // WORK normal  – ruby red (rainbow: red)
            Color.web("#FF9500"),   // RELAX normal – warm amber-orange
            Color.web("#FFCC44"),   // RELAX_LONG   – soft coral-gold
            Color.web("#FFCC00"),   // WARNING      – bright yellow-orange
            Color.web("#CC0000"),   // OVERTIME     – deep blood red
            Color.web("#FF7C7C"),   // PAUSED       – soft rose-pink
            // ── Root gradient – near-black edges, volcanic glowing core ───────
            0.91, 0.30,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.09, 0.24,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.09,                   // ROOT_SHEEN_ALPHA
            0.40, 0.04,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – ultra-flat profile, warm neon-tube glass ─────────
            0.65, 0.10,             // BAR_LIGHTEN / BAR_DARKEN
            0.20,                   // BAR_INNER_SHEEN
            9.0, 0.32, 0.43, 0.16  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── AMBER_DUNE ────────────────────────────────────────────────────────────
    /**
     * Desert-heat fantasy: rich amber-orange work accent with sun-gold
     * relaxation tones and a warm ember glow.
     */
    AMBER_DUNE(
            "Amber Dune",
            // ── Accent colours – desert dune palette ─────────────────────────
            Color.web("#FF8C00"),   // WORK normal  – deep amber-orange (rainbow: orange)
            Color.web("#FFD700"),   // RELAX normal – warm amber-yellow
            Color.web("#FFE066"),   // RELAX_LONG   – golden sand
            Color.web("#FFEE33"),   // WARNING      – bright cream-yellow
            Color.web("#E84000"),   // OVERTIME     – deep ember orange-red
            Color.web("#D4A017"),   // PAUSED       – muted amber-tan
            // ── Root gradient – dark desert edges, warm ember core ────────────
            0.90, 0.32,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.08, 0.22,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.08,                   // ROOT_SHEEN_ALPHA
            0.38, 0.04,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – ultra-flat profile, warm soft-tube glass ─────────
            0.64, 0.10,             // BAR_LIGHTEN / BAR_DARKEN
            0.19,                   // BAR_INNER_SHEEN
            8.5, 0.30, 0.41, 0.15  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── SOLAR_CANARY ──────────────────────────────────────────────────────────
    /**
     * Radiant solar fantasy: bright canary-yellow work accent with golden
     * relaxation tones and a sunshine bloom.
     */
    SOLAR_CANARY(
            "Solar Canary",
            // ── Accent colours – solar canary palette ────────────────────────
            Color.web("#FFD60A"),   // WORK normal  – canary yellow (rainbow: yellow)
            Color.web("#FFAA00"),   // RELAX normal – warm amber
            Color.web("#CCEE22"),   // RELAX_LONG   – lime-yellow
            Color.web("#FFFF55"),   // WARNING      – bright warm white-yellow
            Color.web("#FF4400"),   // OVERTIME     – hot orange-red
            Color.web("#E8C000"),   // PAUSED       – muted gold
            // ── Root gradient – dark edges, radiant sunny core ────────────────
            0.89, 0.34,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.07, 0.19,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.07,                   // ROOT_SHEEN_ALPHA
            0.36, 0.04,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – ultra-flat profile, sunshine bloom glass ─────────
            0.63, 0.11,             // BAR_LIGHTEN / BAR_DARKEN
            0.17,                   // BAR_INNER_SHEEN
            8.0, 0.27, 0.37, 0.13  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── EMERALD_BLOOM ─────────────────────────────────────────────────────────
    /**
     * Forest-magic fantasy: vivid emerald-green work accent with cool cyan
     * relaxation tones and a lush glow halo.
     */
    EMERALD_BLOOM(
            "Emerald Bloom",
            // ── Accent colours – emerald forest palette ───────────────────────
            Color.web("#34C759"),   // WORK normal  – emerald green (rainbow: green)
            Color.web("#00E5CC"),   // RELAX normal – cool cyan-teal
            Color.web("#88FFD0"),   // RELAX_LONG   – mint ice
            Color.web("#AAEE22"),   // WARNING      – lime-yellow
            Color.web("#FF4444"),   // OVERTIME     – hot coral red
            Color.web("#44AA66"),   // PAUSED       – sage muted green
            // ── Root gradient – midnight-dark edges, lush glowing core ────────
            0.92, 0.32,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.07, 0.20,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.07,                   // ROOT_SHEEN_ALPHA
            0.37, 0.05,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – ultra-flat profile, cool glass highlight ─────────
            0.64, 0.10,             // BAR_LIGHTEN / BAR_DARKEN
            0.16,                   // BAR_INNER_SHEEN
            7.5, 0.26, 0.35, 0.12  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── AZURE_WAVE ────────────────────────────────────────────────────────────
    /**
     * Ocean-spirit fantasy: electric azure-blue work accent with icy cyan
     * relaxation tones and a cool wave glow.
     */
    AZURE_WAVE(
            "Azure Wave",
            // ── Accent colours – azure ocean palette ─────────────────────────
            Color.web("#0A84FF"),   // WORK normal  – electric azure blue (rainbow: blue)
            Color.web("#00C4E8"),   // RELAX normal – ocean cyan
            Color.web("#88DDFF"),   // RELAX_LONG   – ice-sky blue
            Color.web("#FFD700"),   // WARNING      – pale gold-yellow
            Color.web("#5500CC"),   // OVERTIME     – deep cobalt-violet
            Color.web("#66AEDD"),   // PAUSED       – soft steel blue
            // ── Root gradient – deep dark edges, cool ocean core ──────────────
            0.92, 0.38,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.08, 0.20,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.08,                   // ROOT_SHEEN_ALPHA
            0.40, 0.04,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – ultra-flat profile, icy glass highlight ──────────
            0.64, 0.10,             // BAR_LIGHTEN / BAR_DARKEN
            0.16,                   // BAR_INNER_SHEEN
            7.5, 0.25, 0.33, 0.11  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── INDIGO_ORBIT ──────────────────────────────────────────────────────────
    /**
     * Cosmic-void fantasy: deep indigo work accent with soft violet
     * relaxation tones and a mysterious orbital glow.
     */
    INDIGO_ORBIT(
            "Indigo Orbit",
            // ── Accent colours – cosmic indigo palette ───────────────────────
            Color.web("#4B0082"),   // WORK normal  – deep indigo (rainbow: indigo)
            Color.web("#6644CC"),   // RELAX normal – electric blue-violet
            Color.web("#9977EE"),   // RELAX_LONG   – soft lavender
            Color.web("#FFD700"),   // WARNING      – gold
            Color.web("#CC0055"),   // OVERTIME     – vivid magenta-red
            Color.web("#8855BB"),   // PAUSED       – soft medium indigo
            // ── Root gradient – near-black void, mysterious orbital core ──────
            0.93, 0.35,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.09, 0.21,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.08,                   // ROOT_SHEEN_ALPHA
            0.36, 0.05,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – ultra-flat profile, refined glass highlight ──────
            0.67, 0.09,             // BAR_LIGHTEN / BAR_DARKEN
            0.15,                   // BAR_INNER_SHEEN
            8.0, 0.27, 0.34, 0.12  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
    ),

    // ── VIOLET_NOVA ───────────────────────────────────────────────────────────
    /**
     * Stellar-burst fantasy: vivid violet work accent with soft lavender
     * relaxation tones and a nova bloom halo.
     */
    VIOLET_NOVA(
            "Violet Nova",
            // ── Accent colours – stellar nova palette ────────────────────────
            Color.web("#AF52DE"),   // WORK normal  – vivid violet (rainbow: violet)
            Color.web("#FF77CC"),   // RELAX normal – soft magenta-pink
            Color.web("#CC99FF"),   // RELAX_LONG   – lavender
            Color.web("#FFD700"),   // WARNING      – warm gold-yellow
            Color.web("#EE0088"),   // OVERTIME     – hot magenta
            Color.web("#CC88FF"),   // PAUSED       – soft lilac
            // ── Root gradient – dark cosmos edges, nova bloom core ────────────
            0.91, 0.28,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.08, 0.23,             // ROOT_GLOW_ALPHA_MIN / MAX
            0.09,                   // ROOT_SHEEN_ALPHA
            0.38, 0.04,             // ROOT_GLOW_WHITE_BLEND / ROOT_GLOW_BLACK_BLEND
            // ── Bar tuning – ultra-flat profile, vivid glass highlight ────────
            0.65, 0.10,             // BAR_LIGHTEN / BAR_DARKEN
            0.18,                   // BAR_INNER_SHEEN
            9.0, 0.31, 0.42, 0.15  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND
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
     * returning {@link #NIGHT_RUNNER} as the safe default if no match is found.
     *
     * @param savedName the value previously returned by {@link #name()} and stored in prefs
     * @return the matching preset, never {@code null}
     */
    public static NeonPreset fromName(String savedName) {
        for (final var p : values()) {
            if (p.name().equals(savedName)) return p;
        }
        return NIGHT_RUNNER;
    }

    /** Returns {@link #getDisplayName()} so JavaFX ComboBox renders the label automatically. */
    @Override
    public String toString() { return displayName; }
}


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
 *   <li>{@link #AURORA_DRIFT} – aurora borealis shifting greens, soft cyan and
 *       violet on a midnight-dark sky background (reference / default).</li>
 *   <li>{@link #SCARLET_SURGE} – rainbow red (Work) × orange (Rest): fiery red
 *       neon with a vivid orange break glow.</li>
 *   <li>{@link #CITRUS_SPARK} – rainbow orange (Work) × yellow (Rest): deep
 *       amber neon with a canary-yellow relaxation glow.</li>
 *   <li>{@link #LIME_FLASH} – rainbow yellow (Work) × green (Rest): electric
 *       lime-yellow neon with a vivid neon-green rest glow.</li>
 *   <li>{@link #JADE_MIST} – rainbow green (Work) × cyan (Rest): vivid emerald
 *       neon with a cool electric-cyan break glow.</li>
 *   <li>{@link #OCEAN_GLOW} – rainbow cyan (Work) × blue (Rest): electric cyan
 *       neon with a deep electric-blue rest glow.</li>
 *   <li>{@link #COSMOS_BLAZE} – rainbow blue (Work) × violet (Rest): deep
 *       electric-blue neon with a vivid violet break glow.</li>
 *   <li>{@link #PRISM_VEIL} – rainbow violet (Work) × red (Rest): vivid violet
 *       neon with a vivid red rest glow, completing the rainbow cycle.</li>
 * </ul>
 */
public enum NeonPreset {

    // ── AURORA_DRIFT ──────────────────────────────────────────────────────────
    /**
     * Aurora borealis atmosphere: shifting neon-greens, soft cyan and violet
     * curtains on a midnight-dark sky with a gentle atmospheric bloom.
     * This is the reference preset and the application default.
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
            // Tuned: bright cool green → moderate white-blend / opacity reduction
            0.91, 0.26,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.30, 0.73,             // ROOT_GLOW_ALPHA_MIN / MAX        (↓ 0.32/0.76)
            0.24,                   // ROOT_SHEEN_ALPHA                 (↓ 0.27)
            0.34, 0.05,             // ROOT_GLOW_WHITE_BLEND / BLACK    (↓ 0.38)
            // ── Bar tuning – soft wide curtain glow, gentle aurora tube ───────
            0.64, 0.10,             // BAR_LIGHTEN / BAR_DARKEN         (↓ 0.66)
            0.36,                   // BAR_INNER_SHEEN                  (↓ 0.40)
            22.0, 0.68, 0.84, 0.28  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND  (↓)
    ),

    // ── SCARLET_SURGE ─────────────────────────────────────────────────────────
    /**
     * Rainbow red (Work) × orange (Rest): fiery scarlet-red neon work accent
     * with a vivid orange relaxation glow and blazing atmospheric bloom.
     */
    SCARLET_SURGE(
            "Scarlet Surge",
            // ── Accent colours – fiery red-to-orange palette ──────────────────
            Color.web("#FF2020"),   // WORK normal  – vivid scarlet red (rainbow: red)
            Color.web("#FF7700"),   // RELAX normal – bright neon orange (rainbow: orange)
            Color.web("#FFAA55"),   // RELAX_LONG   – soft peach-orange
            Color.web("#FFDD00"),   // WARNING      – gold-yellow contrast
            Color.web("#CC0022"),   // OVERTIME     – deep blood red alarm
            Color.web("#FF88AA"),   // PAUSED       – soft rose-pink
            // ── Root gradient – midnight-dark sky, warm red bloom ─────────────
            // Tuned: medium-dark red → slight glow boost for better pop
            0.91, 0.26,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.31, 0.77,             // ROOT_GLOW_ALPHA_MIN / MAX        (↑ 0.30/0.74)
            0.27,                   // ROOT_SHEEN_ALPHA                 (↑ 0.26)
            0.38, 0.05,             // ROOT_GLOW_WHITE_BLEND / BLACK    (↑ 0.36)
            // ── Bar tuning – vivid warm tube glow ────────────────────────────
            0.66, 0.11,             // BAR_LIGHTEN / BAR_DARKEN         (↑ 0.65)
            0.40,                   // BAR_INNER_SHEEN                  (↑ 0.38)
            22.0, 0.70, 0.89, 0.33  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND  (↑)
    ),

    // ── CITRUS_SPARK ──────────────────────────────────────────────────────────
    /**
     * Rainbow orange (Work) × yellow (Rest): deep amber-orange neon work accent
     * with a canary-yellow relaxation glow and warm citrus atmospheric bloom.
     */
    CITRUS_SPARK(
            "Citrus Spark",
            // ── Accent colours – amber-to-yellow citrus palette ───────────────
            Color.web("#FF8800"),   // WORK normal  – deep neon amber-orange (rainbow: orange)
            Color.web("#FFEE00"),   // RELAX normal – bright canary yellow (rainbow: yellow)
            Color.web("#FFDD66"),   // RELAX_LONG   – golden yellow
            Color.web("#FF4400"),   // WARNING      – hot orange-red contrast
            Color.web("#CC2200"),   // OVERTIME     – deep ember red alarm
            Color.web("#FFAA33"),   // PAUSED       – warm amber
            // ── Root gradient – dark sky, warm amber-citrus bloom ─────────────
            // Tuned: medium-bright warm orange → mild white-blend reduction
            0.91, 0.25,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.29, 0.72,             // ROOT_GLOW_ALPHA_MIN / MAX        (↓ 0.31/0.75)
            0.23,                   // ROOT_SHEEN_ALPHA                 (↓ 0.26)
            0.32, 0.05,             // ROOT_GLOW_WHITE_BLEND / BLACK    (↓ 0.37)
            // ── Bar tuning – bright warm citrus tube glow ─────────────────────
            0.63, 0.11,             // BAR_LIGHTEN / BAR_DARKEN         (↓ 0.65)
            0.36,                   // BAR_INNER_SHEEN                  (↓ 0.39)
            22.0, 0.67, 0.84, 0.27  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND  (↓)
    ),

    // ── LIME_FLASH ────────────────────────────────────────────────────────────
    /**
     * Rainbow yellow (Work) × green (Rest): electric lime-yellow neon work accent
     * with a vivid neon-green relaxation glow and energetic atmospheric bloom.
     */
    LIME_FLASH(
            "Lime Flash",
            // ── Accent colours – lime-yellow-to-green palette ─────────────────
            Color.web("#EEFF00"),   // WORK normal  – electric lime-yellow (rainbow: yellow)
            Color.web("#44FF66"),   // RELAX normal – vivid neon green (rainbow: green)
            Color.web("#AAFFBB"),   // RELAX_LONG   – pale mint green
            Color.web("#FF8800"),   // WARNING      – orange contrast
            Color.web("#FF2222"),   // OVERTIME     – red alarm
            Color.web("#AADD33"),   // PAUSED       – muted yellow-green
            // ── Root gradient – dark sky, yellow-green bloom ──────────────────
            // Tuned: very bright lime-yellow → strongest white-blend / opacity reduction
            0.91, 0.26,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.27, 0.69,             // ROOT_GLOW_ALPHA_MIN / MAX        (↓ 0.31/0.75)
            0.20,                   // ROOT_SHEEN_ALPHA                 (↓ 0.27)
            0.28, 0.06,             // ROOT_GLOW_WHITE_BLEND / BLACK    (↓ 0.38)
            // ── Bar tuning – bright electric lime tube glow ───────────────────
            0.60, 0.12,             // BAR_LIGHTEN / BAR_DARKEN         (↓ 0.66)
            0.31,                   // BAR_INNER_SHEEN                  (↓ 0.40)
            22.0, 0.64, 0.80, 0.22  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND  (↓↓)
    ),

    // ── JADE_MIST ─────────────────────────────────────────────────────────────
    /**
     * Rainbow green (Work) × cyan (Rest): vivid emerald-green neon work accent
     * with a cool electric-cyan relaxation glow and lush atmospheric bloom.
     */
    JADE_MIST(
            "Jade Mist",
            // ── Accent colours – emerald-to-cyan palette ──────────────────────
            Color.web("#00FF77"),   // WORK normal  – vivid emerald neon green (rainbow: green)
            Color.web("#00DDFF"),   // RELAX normal – electric cyan (rainbow: cyan)
            Color.web("#88FFEE"),   // RELAX_LONG   – pale mint cyan
            Color.web("#FFDD00"),   // WARNING      – gold-yellow contrast
            Color.web("#FF4466"),   // OVERTIME     – hot pink-red alarm
            Color.web("#33DDAA"),   // PAUSED       – sea green
            // ── Root gradient – midnight sky, cool green-cyan bloom ───────────
            // Tuned: bright emerald green → moderate white-blend / opacity reduction
            0.91, 0.26,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.30, 0.73,             // ROOT_GLOW_ALPHA_MIN / MAX        (↓ 0.32/0.76)
            0.24,                   // ROOT_SHEEN_ALPHA                 (↓ 0.27)
            0.33, 0.05,             // ROOT_GLOW_WHITE_BLEND / BLACK    (↓ 0.38)
            // ── Bar tuning – lush jade tube glow ─────────────────────────────
            0.64, 0.10,             // BAR_LIGHTEN / BAR_DARKEN         (↓ 0.66)
            0.36,                   // BAR_INNER_SHEEN                  (↓ 0.40)
            22.0, 0.68, 0.84, 0.27  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND  (↓)
    ),

    // ── OCEAN_GLOW ────────────────────────────────────────────────────────────
    /**
     * Rainbow cyan (Work) × blue (Rest): electric cyan neon work accent with a
     * deep electric-blue relaxation glow and cool oceanic atmospheric bloom.
     */
    OCEAN_GLOW(
            "Ocean Glow",
            // ── Accent colours – cyan-to-blue ocean palette ───────────────────
            Color.web("#00EEFF"),   // WORK normal  – electric cyan (rainbow: cyan)
            Color.web("#2266FF"),   // RELAX normal – electric blue (rainbow: blue)
            Color.web("#88CCFF"),   // RELAX_LONG   – ice sky blue
            Color.web("#FFEE00"),   // WARNING      – yellow contrast
            Color.web("#FF2244"),   // OVERTIME     – red alarm
            Color.web("#4499DD"),   // PAUSED       – steel blue
            // ── Root gradient – deep dark sky, cool ocean bloom ───────────────
            // Tuned: bright cool cyan → moderate white-blend / opacity reduction
            0.92, 0.27,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.30, 0.73,             // ROOT_GLOW_ALPHA_MIN / MAX        (↓ 0.32/0.76)
            0.24,                   // ROOT_SHEEN_ALPHA                 (↓ 0.27)
            0.35, 0.05,             // ROOT_GLOW_WHITE_BLEND / BLACK    (↓ 0.40)
            // ── Bar tuning – cool oceanic tube glow ──────────────────────────
            0.64, 0.10,             // BAR_LIGHTEN / BAR_DARKEN         (↓ 0.66)
            0.36,                   // BAR_INNER_SHEEN                  (↓ 0.40)
            22.0, 0.68, 0.84, 0.29  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND  (↓)
    ),

    // ── COSMOS_BLAZE ──────────────────────────────────────────────────────────
    /**
     * Rainbow blue (Work) × violet (Rest): deep electric-blue neon work accent
     * with a vivid violet relaxation glow and cosmic atmospheric bloom.
     */
    COSMOS_BLAZE(
            "Cosmos Blaze",
            // ── Accent colours – blue-to-violet cosmic palette ────────────────
            Color.web("#1155FF"),   // WORK normal  – deep electric blue (rainbow: blue)
            Color.web("#AA44FF"),   // RELAX normal – electric violet (rainbow: violet)
            Color.web("#AABBFF"),   // RELAX_LONG   – lavender blue
            Color.web("#FFEE00"),   // WARNING      – gold-yellow contrast
            Color.web("#FF2266"),   // OVERTIME     – magenta-red alarm
            Color.web("#6633BB"),   // PAUSED       – medium indigo-violet
            // ── Root gradient – void-dark sky, deep blue-violet bloom ─────────
            // Tuned: deep dark blue → strongest glow boost for neon pop
            0.92, 0.27,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.34, 0.81,             // ROOT_GLOW_ALPHA_MIN / MAX        (↑ 0.32/0.76)
            0.31,                   // ROOT_SHEEN_ALPHA                 (↑ 0.27)
            0.44, 0.05,             // ROOT_GLOW_WHITE_BLEND / BLACK    (↑ 0.38)
            // ── Bar tuning – deep cosmic tube glow ───────────────────────────
            0.69, 0.10,             // BAR_LIGHTEN / BAR_DARKEN         (↑ 0.66)
            0.46,                   // BAR_INNER_SHEEN                  (↑ 0.40)
            22.0, 0.74, 0.93, 0.40  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND  (↑↑)
    ),

    // ── PRISM_VEIL ────────────────────────────────────────────────────────────
    /**
     * Rainbow violet (Work) × red (Rest): vivid violet neon work accent with a
     * vivid red relaxation glow, completing the rainbow cycle with a prism bloom.
     */
    PRISM_VEIL(
            "Prism Veil",
            // ── Accent colours – violet-to-red prism palette ──────────────────
            Color.web("#CC00FF"),   // WORK normal  – vivid electric violet (rainbow: violet)
            Color.web("#FF2244"),   // RELAX normal – vivid red (rainbow: red, cycle wrap)
            Color.web("#FF88CC"),   // RELAX_LONG   – light rose-pink
            Color.web("#FFEE44"),   // WARNING      – gold-yellow contrast
            Color.web("#880066"),   // OVERTIME     – deep dark magenta alarm
            Color.web("#DD44AA"),   // PAUSED       – hot magenta-pink
            // ── Root gradient – midnight sky, violet-red prism bloom ──────────
            // Tuned: dark violet → moderate glow boost for vivid neon depth
            0.91, 0.26,             // ROOT_DARK_EDGE / ROOT_DARK_MID
            0.33, 0.79,             // ROOT_GLOW_ALPHA_MIN / MAX        (↑ 0.31/0.75)
            0.29,                   // ROOT_SHEEN_ALPHA                 (↑ 0.27)
            0.42, 0.05,             // ROOT_GLOW_WHITE_BLEND / BLACK    (↑ 0.38)
            // ── Bar tuning – vivid prism tube glow ───────────────────────────
            0.67, 0.10,             // BAR_LIGHTEN / BAR_DARKEN         (↑ 0.65)
            0.43,                   // BAR_INNER_SHEEN                  (↑ 0.39)
            22.0, 0.72, 0.91, 0.37  // BAR_GLOW_RADIUS / SPREAD / OPACITY / WHITE_BLEND  (↑)
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
     * returning {@link #AURORA_DRIFT} as the safe default if no match is found.
     *
     * @param savedName the value previously returned by {@link #name()} and stored in prefs
     * @return the matching preset, never {@code null}
     */
    public static NeonPreset fromName(String savedName) {
        for (final var p : values()) {
            if (p.name().equals(savedName)) return p;
        }
        return AURORA_DRIFT;
    }

    /** Returns {@link #getDisplayName()} so JavaFX ComboBox renders the label automatically. */
    @Override
    public String toString() { return displayName; }
}

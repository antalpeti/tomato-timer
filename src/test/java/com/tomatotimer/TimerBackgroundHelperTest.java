package com.tomatotimer;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimerBackgroundHelper")
class TimerBackgroundHelperTest extends TimerBackgroundHelperHelper {

    // ── computeAccentColor ────────────────────────────────────────────────────

    @Test
    @DisplayName("computeAccentColor returns paused colour when isPaused=true")
    void testComputeAccentColorReturnsPausedColorWhenPaused() {
        final var color = auroraAccentAtProgress(PCT_HALF, true, false);
        assertEquals(NeonPreset.AURORA_DRIFT.getPausedColor(), color);
    }

    @Test
    @DisplayName("computeAccentColor returns overtime colour when isOverTime=true")
    void testComputeAccentColorReturnsOverColorWhenOvertime() {
        final var color = auroraAccentAtProgress(PCT_FULL, false, true);
        assertEquals(NeonPreset.AURORA_DRIFT.getOverColor(), color);
    }

    @Test
    @DisplayName("computeAccentColor paused takes precedence over overtime")
    void testComputeAccentColorPausedTakesPrecedenceOverOvertime() {
        final var color = auroraAccentAtProgress(PCT_FULL, true, true);
        assertEquals(NeonPreset.AURORA_DRIFT.getPausedColor(), color);
    }

    @Test
    @DisplayName("computeAccentColor returns base colour when progress is below warning threshold")
    void testComputeAccentColorReturnsBaseColorBelowWarning() {
        final var color = auroraAccentAtProgress(PCT_ZERO, false, false);
        assertEquals(NeonPreset.AURORA_DRIFT.normalColorFor(TimerMode.WORK), color);
    }

    @Test
    @DisplayName("computeAccentColor returns base colour exactly at warning threshold")
    void testComputeAccentColorAtExactWarningThreshold() {
        final var color = auroraAccentAtProgress(PCT_WARNING, false, false);
        assertEquals(NeonPreset.AURORA_DRIFT.normalColorFor(TimerMode.WORK), color);
    }

    @Test
    @DisplayName("computeAccentColor interpolates towards warning colour above threshold")
    void testComputeAccentColorInterpolatesAboveWarningThreshold() {
        final var colorAt90 = auroraAccentAtProgress(90.0, false, false);
        final var base    = NeonPreset.AURORA_DRIFT.normalColorFor(TimerMode.WORK);
        final var warning = NeonPreset.AURORA_DRIFT.getWarningColor();
        final var expected = base.interpolate(warning,
                (90.0 - PCT_WARNING) / (100.0 - PCT_WARNING));
        assertEquals(expected.getRed(),   colorAt90.getRed(),   DELTA);
        assertEquals(expected.getGreen(), colorAt90.getGreen(), DELTA);
        assertEquals(expected.getBlue(),  colorAt90.getBlue(),  DELTA);
    }

    @Test
    @DisplayName("computeAccentColor backward-compat overload delegates to AURORA_DRIFT")
    void testComputeAccentColorBackwardCompatOverloadDelegatesToAuroraDrift() {
        final var viaPreset  = TimerBackgroundHelper.computeAccentColor(
                NeonPreset.AURORA_DRIFT, TimerMode.WORK, PCT_HALF, false, false);
        final var viaLegacy  = TimerBackgroundHelper.computeAccentColor(
                TimerMode.WORK, PCT_HALF, false, false);
        assertEquals(viaPreset, viaLegacy);
    }

    @Test
    @DisplayName("computeAccentColor uses correct base colour per TimerMode")
    void testComputeAccentColorUsesCorrectBaseColorPerMode() {
        for (final var mode : TimerMode.values()) {
            final var color = TimerBackgroundHelper.computeAccentColor(
                    NeonPreset.AURORA_DRIFT, mode, PCT_ZERO, false, false);
            assertEquals(NeonPreset.AURORA_DRIFT.normalColorFor(mode), color);
        }
    }

    // ── computeTrackColor ─────────────────────────────────────────────────────

    @Test
    @DisplayName("computeTrackColor returns TRACK_PAUSED when isPaused=true")
    void testComputeTrackColorReturnsPausedWhenPaused() {
        assertEquals(TimerBackgroundHelper.TRACK_PAUSED,
                TimerBackgroundHelper.computeTrackColor(TimerMode.WORK, true, false));
    }

    @Test
    @DisplayName("computeTrackColor returns TRACK_OVER when isOverTime=true")
    void testComputeTrackColorReturnsOverWhenOvertime() {
        assertEquals(TimerBackgroundHelper.TRACK_OVER,
                TimerBackgroundHelper.computeTrackColor(TimerMode.WORK, false, true));
    }

    @Test
    @DisplayName("computeTrackColor returns mode-specific colour for each normal mode")
    void testComputeTrackColorReturnsCorrectColorForEachMode() {
        assertEquals(TimerBackgroundHelper.TRACK_WORK,
                TimerBackgroundHelper.computeTrackColor(TimerMode.WORK, false, false));
        assertEquals(TimerBackgroundHelper.TRACK_RELAX,
                TimerBackgroundHelper.computeTrackColor(TimerMode.RELAX, false, false));
        assertEquals(TimerBackgroundHelper.TRACK_RELAX_LONG,
                TimerBackgroundHelper.computeTrackColor(TimerMode.RELAX_LONG, false, false));
    }

    // ── toCssHex ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("toCssHex converts black to #000000")
    void testToCssHexConvertsBlack() {
        assertEquals("#000000", TimerBackgroundHelper.toCssHex(Color.BLACK));
    }

    @Test
    @DisplayName("toCssHex converts white to #FFFFFF")
    void testToCssHexConvertsWhite() {
        assertEquals("#FFFFFF", TimerBackgroundHelper.toCssHex(Color.WHITE));
    }

    @Test
    @DisplayName("toCssHex converts a known colour correctly")
    void testToCssHexConvertsKnownColor() {
        assertEquals("#FF0000", TimerBackgroundHelper.toCssHex(Color.RED));
    }

    // ── toCssRgba ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("toCssRgba produces correct rgba string for opaque red")
    void testToCssRgbaOpaqueRed() {
        final var result = TimerBackgroundHelper.toCssRgba(Color.RED, 1.0);
        assertEquals("rgba(255,0,0,1.000)", result);
    }

    @Test
    @DisplayName("toCssRgba produces correct rgba string for semi-transparent black")
    void testToCssRgbaSemiTransparentBlack() {
        final var result = TimerBackgroundHelper.toCssRgba(Color.BLACK, 0.5);
        assertEquals("rgba(0,0,0,0.500)", result);
    }

    @Test
    @DisplayName("toCssRgba clamps opacity above 1.0")
    void testToCssRgbaClampedOpacityAboveOne() {
        final var result = TimerBackgroundHelper.toCssRgba(Color.WHITE, 2.0);
        assertTrue(result.endsWith(",1.000)"));
    }

    @Test
    @DisplayName("toCssRgba clamps opacity below 0.0")
    void testToCssRgbaClampedOpacityBelowZero() {
        final var result = TimerBackgroundHelper.toCssRgba(Color.WHITE, -1.0);
        assertTrue(result.endsWith(",0.000)"));
    }

    // ── computeRootGradientCss ────────────────────────────────────────────────

    @Test
    @DisplayName("computeRootGradientCss returns non-empty string with preset+profile overload")
    void testComputeRootGradientCssWithPresetAndProfileReturnsNonEmptyString() {
        final var css = TimerBackgroundHelper.computeRootGradientCss(
                NeonPreset.AURORA_DRIFT, NeonGlowProfile.BALANCED,
                TimerMode.WORK, PCT_HALF, false, false);
        assertNotNull(css);
        assertFalse(css.isBlank());
        assertTrue(css.contains("linear-gradient"));
    }

    @Test
    @DisplayName("computeRootGradientCss returns non-empty string with preset-only overload")
    void testComputeRootGradientCssWithPresetOnlyReturnsNonEmptyString() {
        final var css = TimerBackgroundHelper.computeRootGradientCss(
                NeonPreset.AURORA_DRIFT, TimerMode.RELAX, PCT_ZERO, false, false);
        assertNotNull(css);
        assertFalse(css.isBlank());
    }

    @Test
    @DisplayName("computeRootGradientCss backward-compat overload returns non-empty string")
    void testComputeRootGradientCssBackwardCompatReturnsNonEmptyString() {
        final var css = TimerBackgroundHelper.computeRootGradientCss(
                TimerMode.WORK, PCT_FULL, false, true);
        assertNotNull(css);
        assertFalse(css.isBlank());
    }

    @Test
    @DisplayName("computeRootGradientCss with paused=true produces valid CSS")
    void testComputeRootGradientCssWhenPaused() {
        final var css = TimerBackgroundHelper.computeRootGradientCss(
                NeonPreset.AURORA_DRIFT, TimerMode.WORK, PCT_HALF, true, false);
        assertNotNull(css);
        assertFalse(css.isBlank());
    }

    @Test
    @DisplayName("computeRootGradientCss with overtime=true produces valid CSS")
    void testComputeRootGradientCssWhenOvertime() {
        final var css = TimerBackgroundHelper.computeRootGradientCss(
                NeonPreset.AURORA_DRIFT, TimerMode.WORK, PCT_FULL, false, true);
        assertNotNull(css);
        assertFalse(css.isBlank());
    }

    @Test
    @DisplayName("computeRootGradientCss (preset+profile) with isOverTime=true returns valid CSS")
    void testComputeRootGradientCssWithPresetAndProfileAndOvertimeReturnsValidCss() {
        final var css = TimerBackgroundHelper.computeRootGradientCss(
                NeonPreset.AURORA_DRIFT, NeonGlowProfile.BALANCED,
                TimerMode.WORK, PCT_FULL, false, true);
        assertNotNull(css);
        assertFalse(css.isBlank());
        assertTrue(css.contains("linear-gradient"));
    }

    @Test
    @DisplayName("computeRootGradientCss (preset+profile) with isPaused=true returns valid CSS")
    void testComputeRootGradientCssWithPresetAndProfileAndPausedReturnsValidCss() {
        final var css = TimerBackgroundHelper.computeRootGradientCss(
                NeonPreset.AURORA_DRIFT, NeonGlowProfile.BALANCED,
                TimerMode.RELAX, PCT_HALF, true, false);
        assertNotNull(css);
        assertFalse(css.isBlank());
        assertTrue(css.contains("linear-gradient"));
    }

    // ── computeBarCss ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("computeBarCss with preset+profile returns a CSS string with expected keywords")
    void testComputeBarCssWithPresetAndProfileContainsExpectedKeywords() {
        final var css = TimerBackgroundHelper.computeBarCss(
                NeonPreset.AURORA_DRIFT, NeonGlowProfile.BALANCED,
                TimerMode.WORK, PCT_HALF, false, false);
        assertNotNull(css);
        assertTrue(css.contains("-fx-background-color"));
        assertTrue(css.contains("dropshadow"));
        assertTrue(css.contains("-fx-background-radius"));
    }

    @Test
    @DisplayName("computeBarCss with preset-only returns a CSS string with expected keywords")
    void testComputeBarCssWithPresetOnlyContainsExpectedKeywords() {
        final var css = TimerBackgroundHelper.computeBarCss(
                NeonPreset.AURORA_DRIFT, TimerMode.RELAX, PCT_ZERO, false, false);
        assertNotNull(css);
        assertTrue(css.contains("-fx-background-color"));
        assertTrue(css.contains("dropshadow"));
    }

    @Test
    @DisplayName("computeBarCss backward-compat overload returns a valid CSS string")
    void testComputeBarCssBackwardCompatReturnsValidString() {
        final var css = TimerBackgroundHelper.computeBarCss(
                TimerMode.WORK, PCT_FULL, false, true);
        assertNotNull(css);
        assertFalse(css.isBlank());
    }

    // ── computeTrackCss ───────────────────────────────────────────────────────

    @Test
    @DisplayName("computeTrackCss returns the expected fixed CSS string")
    void testComputeTrackCssReturnsExpectedString() {
        final var css = TimerBackgroundHelper.computeTrackCss();
        assertTrue(css.contains("-fx-background-color"));
        assertTrue(css.contains("-fx-background-radius"));
        assertTrue(css.contains("-fx-background-insets"));
        assertTrue(css.contains("linear-gradient"));
    }
}


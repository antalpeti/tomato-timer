package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("UiScaleHelper")
class UiScaleHelperTest extends UiScaleHelperHelper {

    // ── clamp ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("clamp returns min when value is below range")
    void testClampBelowMin() {
        assertEquals(5.0, UiScaleHelper.clamp(1.0, 5.0, 10.0), DELTA);
    }

    @Test
    @DisplayName("clamp returns max when value is above range")
    void testClampAboveMax() {
        assertEquals(10.0, UiScaleHelper.clamp(100.0, 5.0, 10.0), DELTA);
    }

    @Test
    @DisplayName("clamp returns the value itself when within range")
    void testClampWithinRange() {
        assertEquals(7.5, UiScaleHelper.clamp(7.5, 5.0, 10.0), DELTA);
    }

    @Test
    @DisplayName("clamp returns min when value equals min")
    void testClampAtMin() {
        assertEquals(5.0, UiScaleHelper.clamp(5.0, 5.0, 10.0), DELTA);
    }

    @Test
    @DisplayName("clamp returns max when value equals max")
    void testClampAtMax() {
        assertEquals(10.0, UiScaleHelper.clamp(10.0, 5.0, 10.0), DELTA);
    }

    // ── mainTimeFontPx ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("mainTimeFontPx at reference dimensions is within [18, 72]")
    void testMainTimeFontPxAtReferenceSize() {
        final var result = UiScaleHelper.mainTimeFontPx(REF_W, REF_H);
        assertTrue(result >= 18.0 && result <= 72.0);
    }

    @Test
    @DisplayName("mainTimeFontPx is clamped to 18 for very small inputs")
    void testMainTimeFontPxClampedToMin() {
        assertEquals(18.0, UiScaleHelper.mainTimeFontPx(TINY_WIDTH, TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("mainTimeFontPx is clamped to 72 for very large inputs")
    void testMainTimeFontPxClampedToMax() {
        assertEquals(72.0, UiScaleHelper.mainTimeFontPx(LARGE_WIDTH, LARGE_HEIGHT), DELTA);
    }

    // ── smallTimeFontPx ────────────────────────────────────────────────────────

    @Test
    @DisplayName("smallTimeFontPx is clamped to 11 for very small inputs")
    void testSmallTimeFontPxClampedToMin() {
        assertEquals(11.0, UiScaleHelper.smallTimeFontPx(TINY_WIDTH, TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("smallTimeFontPx at very large inputs returns 0.58 × 72 (max of mainTimeFontPx)")
    void testSmallTimeFontPxAtLargeInputs() {
        // NOTE: the declared upper clamp (42) is unreachable because mainTimeFontPx is
        // capped at 72 and 72 × 0.58 = 41.76 < 42.  The effective max is ≈ 41.76.
        final var result = UiScaleHelper.smallTimeFontPx(LARGE_WIDTH, LARGE_HEIGHT);
        assertEquals(72.0 * 0.58, result, DELTA);
    }

    @Test
    @DisplayName("smallTimeFontPx is 0.58 × mainTimeFontPx when within clamp range")
    void testSmallTimeFontPxIsProportionalToMain() {
        final var main  = UiScaleHelper.mainTimeFontPx(REF_W, REF_H);
        final var small = UiScaleHelper.smallTimeFontPx(REF_W, REF_H);
        assertEquals(UiScaleHelper.clamp(main * 0.58, 11.0, 42.0), small, DELTA);
    }

    // ── infoFontPx ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("infoFontPx is clamped to 9 for very small inputs")
    void testInfoFontPxClampedToMin() {
        assertEquals(9.0, UiScaleHelper.infoFontPx(TINY_WIDTH, TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("infoFontPx is clamped to 30 for very large inputs")
    void testInfoFontPxClampedToMax() {
        assertEquals(30.0, UiScaleHelper.infoFontPx(LARGE_WIDTH, LARGE_HEIGHT), DELTA);
    }

    // ── settingLabelFontPx ─────────────────────────────────────────────────────

    @Test
    @DisplayName("settingLabelFontPx is clamped to 9 for very small height")
    void testSettingLabelFontPxClampedToMin() {
        assertEquals(9.0, UiScaleHelper.settingLabelFontPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("settingLabelFontPx is clamped to 16 for very large height")
    void testSettingLabelFontPxClampedToMax() {
        assertEquals(16.0, UiScaleHelper.settingLabelFontPx(LARGE_HEIGHT), DELTA);
    }

    // ── soundLabelFontPx ───────────────────────────────────────────────────────

    @Test
    @DisplayName("soundLabelFontPx is clamped to 8 for very small height")
    void testSoundLabelFontPxClampedToMin() {
        assertEquals(8.0, UiScaleHelper.soundLabelFontPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("soundLabelFontPx is clamped to 13 for very large height")
    void testSoundLabelFontPxClampedToMax() {
        assertEquals(13.0, UiScaleHelper.soundLabelFontPx(LARGE_HEIGHT), DELTA);
    }

    // ── versionLabelFontPx ─────────────────────────────────────────────────────

    @Test
    @DisplayName("versionLabelFontPx is clamped to 8 for very small height")
    void testVersionLabelFontPxClampedToMin() {
        assertEquals(8.0, UiScaleHelper.versionLabelFontPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("versionLabelFontPx is clamped to 13 for very large height")
    void testVersionLabelFontPxClampedToMax() {
        assertEquals(13.0, UiScaleHelper.versionLabelFontPx(LARGE_HEIGHT), DELTA);
    }

    // ── mainIconPx ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("mainIconPx is clamped to 16 for very small inputs")
    void testMainIconPxClampedToMin() {
        assertEquals(16.0, UiScaleHelper.mainIconPx(TINY_WIDTH, TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("mainIconPx is clamped to 52 for very large inputs")
    void testMainIconPxClampedToMax() {
        assertEquals(52.0, UiScaleHelper.mainIconPx(LARGE_WIDTH, LARGE_HEIGHT), DELTA);
    }

    // ── navIconPx ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("navIconPx uses REF_HEIGHT as floor when actual height is smaller")
    void testNavIconPxUsesRefHeightFloor() {
        final var atTinyHeight = UiScaleHelper.navIconPx(TINY_HEIGHT);
        final var atRefHeight  = UiScaleHelper.navIconPx(REF_H);
        assertEquals(atRefHeight, atTinyHeight, DELTA);
    }

    @Test
    @DisplayName("navIconPx is clamped to 52 for very large height")
    void testNavIconPxClampedToMax() {
        assertEquals(52.0, UiScaleHelper.navIconPx(LARGE_HEIGHT), DELTA);
    }

    // ── winCtrlIconPx ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("winCtrlIconPx is clamped to 10 for very small height")
    void testWinCtrlIconPxClampedToMin() {
        assertEquals(10.0, UiScaleHelper.winCtrlIconPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("winCtrlIconPx is clamped to 14 for very large height")
    void testWinCtrlIconPxClampedToMax() {
        assertEquals(14.0, UiScaleHelper.winCtrlIconPx(LARGE_HEIGHT), DELTA);
    }

    // ── smallBtnFontPx ────────────────────────────────────────────────────────

    @Test
    @DisplayName("smallBtnFontPx is clamped to 9 for very small height")
    void testSmallBtnFontPxClampedToMin() {
        assertEquals(9.0, UiScaleHelper.smallBtnFontPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("smallBtnFontPx is clamped to 16 for very large height")
    void testSmallBtnFontPxClampedToMax() {
        assertEquals(16.0, UiScaleHelper.smallBtnFontPx(LARGE_HEIGHT), DELTA);
    }

    // ── smallBtnMinWidthPx ────────────────────────────────────────────────────

    @Test
    @DisplayName("smallBtnMinWidthPx is clamped to 20 for very small height")
    void testSmallBtnMinWidthPxClampedToMin() {
        assertEquals(20.0, UiScaleHelper.smallBtnMinWidthPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("smallBtnMinWidthPx is clamped to 34 for very large height")
    void testSmallBtnMinWidthPxClampedToMax() {
        assertEquals(34.0, UiScaleHelper.smallBtnMinWidthPx(LARGE_HEIGHT), DELTA);
    }

    // ── smallBtnMinHeightPx ───────────────────────────────────────────────────

    @Test
    @DisplayName("smallBtnMinHeightPx is clamped to 18 for very small height")
    void testSmallBtnMinHeightPxClampedToMin() {
        assertEquals(18.0, UiScaleHelper.smallBtnMinHeightPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("smallBtnMinHeightPx is clamped to 30 for very large height")
    void testSmallBtnMinHeightPxClampedToMax() {
        assertEquals(30.0, UiScaleHelper.smallBtnMinHeightPx(LARGE_HEIGHT), DELTA);
    }

    // ── spinnerWidthPx ────────────────────────────────────────────────────────

    @Test
    @DisplayName("spinnerWidthPx is clamped to 44 for very small height")
    void testSpinnerWidthPxClampedToMin() {
        assertEquals(44.0, UiScaleHelper.spinnerWidthPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("spinnerWidthPx is clamped to 80 for very large height")
    void testSpinnerWidthPxClampedToMax() {
        assertEquals(80.0, UiScaleHelper.spinnerWidthPx(LARGE_HEIGHT), DELTA);
    }

    // ── settingsSpacingPx ─────────────────────────────────────────────────────

    @Test
    @DisplayName("settingsSpacingPx is clamped to 2 for very small height")
    void testSettingsSpacingPxClampedToMin() {
        assertEquals(2.0, UiScaleHelper.settingsSpacingPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("settingsSpacingPx is clamped to 10 for very large height")
    void testSettingsSpacingPxClampedToMax() {
        assertEquals(10.0, UiScaleHelper.settingsSpacingPx(LARGE_HEIGHT), DELTA);
    }

    // ── soundGroupSpacingPx ───────────────────────────────────────────────────

    @Test
    @DisplayName("soundGroupSpacingPx is clamped to 3 for very small height")
    void testSoundGroupSpacingPxClampedToMin() {
        assertEquals(3.0, UiScaleHelper.soundGroupSpacingPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("soundGroupSpacingPx is clamped to 14 for very large height")
    void testSoundGroupSpacingPxClampedToMax() {
        assertEquals(14.0, UiScaleHelper.soundGroupSpacingPx(LARGE_HEIGHT), DELTA);
    }

    // ── soundVboxSpacingPx ────────────────────────────────────────────────────

    @Test
    @DisplayName("soundVboxSpacingPx is clamped to 1 for very small height")
    void testSoundVboxSpacingPxClampedToMin() {
        assertEquals(1.0, UiScaleHelper.soundVboxSpacingPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("soundVboxSpacingPx is clamped to 4 for very large height")
    void testSoundVboxSpacingPxClampedToMax() {
        assertEquals(4.0, UiScaleHelper.soundVboxSpacingPx(LARGE_HEIGHT), DELTA);
    }

    // ── soundBtnRowSpacingPx ──────────────────────────────────────────────────

    @Test
    @DisplayName("soundBtnRowSpacingPx is clamped to 1 for very small height")
    void testSoundBtnRowSpacingPxClampedToMin() {
        assertEquals(1.0, UiScaleHelper.soundBtnRowSpacingPx(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("soundBtnRowSpacingPx is clamped to 4 for very large height")
    void testSoundBtnRowSpacingPxClampedToMax() {
        assertEquals(4.0, UiScaleHelper.soundBtnRowSpacingPx(LARGE_HEIGHT), DELTA);
    }

    // ── rowPaddingStyle ───────────────────────────────────────────────────────

    @Test
    @DisplayName("rowPaddingStyle returns a well-formed -fx-padding CSS snippet")
    void testRowPaddingStyleFormat() {
        final var style = UiScaleHelper.rowPaddingStyle(REF_H);
        assertTrue(style.startsWith("-fx-padding:"));
        assertTrue(style.contains(" "));
        assertTrue(style.endsWith(";"));
    }

    @Test
    @DisplayName("rowPaddingStyle at tiny height clamps both components to minimum values")
    void testRowPaddingStyleAtTinyHeight() {
        final var style = UiScaleHelper.rowPaddingStyle(TINY_HEIGHT);
        assertEquals("-fx-padding: 1 2 1 2;", style);
    }

    @Test
    @DisplayName("rowPaddingStyle at large height clamps both components to maximum values")
    void testRowPaddingStyleAtLargeHeight() {
        final var style = UiScaleHelper.rowPaddingStyle(LARGE_HEIGHT);
        assertEquals("-fx-padding: 4 6 4 6;", style);
    }
}



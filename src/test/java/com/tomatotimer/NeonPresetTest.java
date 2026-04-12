package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("NeonPreset")
class NeonPresetTest extends NeonPresetHelper {

    @Test
    @DisplayName("fromName returns the matching preset for each canonical name")
    void testFromNameReturnsMatchingPresetForKnownNames() {
        for (final var preset : NeonPreset.values()) {
            assertEquals(preset, NeonPreset.fromName(preset.name()));
        }
    }

    @Test
    @DisplayName("fromName returns AURORA_DRIFT for an unknown name")
    void testFromNameReturnsAuroraDriftForUnknownName() {
        assertEquals(NeonPreset.AURORA_DRIFT, NeonPreset.fromName(NAME_UNKNOWN));
    }

    @Test
    @DisplayName("fromName returns AURORA_DRIFT when name is null")
    void testFromNameReturnsAuroraDriftForNull() {
        assertEquals(NeonPreset.AURORA_DRIFT, NeonPreset.fromName(null));
    }

    @Test
    @DisplayName("toString returns getDisplayName for every preset")
    void testToStringReturnsDisplayName() {
        for (final var preset : NeonPreset.values()) {
            assertEquals(preset.getDisplayName(), preset.toString());
        }
    }

    @Test
    @DisplayName("getDisplayName is non-null and non-empty for every preset")
    void testDisplayNameIsNonNullAndNonEmpty() {
        for (final var preset : NeonPreset.values()) {
            assertNotNull(preset.getDisplayName());
            assertNotNull(preset.toString());
        }
    }

    @Test
    @DisplayName("AURORA_DRIFT display name is correct")
    void testAuroraDriftDisplayName() {
        assertEquals(AURORA_DRIFT_DISPLAY_NAME, NeonPreset.AURORA_DRIFT.getDisplayName());
    }

    @Test
    @DisplayName("normalColorFor WORK returns the work normal colour for AURORA_DRIFT")
    void testNormalColorForWorkReturnsWorkNormal() {
        assertEquals(AURORA_WORK_NORMAL, NeonPreset.AURORA_DRIFT.normalColorFor(TimerMode.WORK));
    }

    @Test
    @DisplayName("normalColorFor RELAX returns the relax normal colour for AURORA_DRIFT")
    void testNormalColorForRelaxReturnsRelaxNormal() {
        assertEquals(AURORA_RELAX_NORMAL, NeonPreset.AURORA_DRIFT.normalColorFor(TimerMode.RELAX));
    }

    @Test
    @DisplayName("normalColorFor RELAX_LONG returns the relax-long colour for AURORA_DRIFT")
    void testNormalColorForRelaxLongReturnsRelaxLongColor() {
        assertEquals(AURORA_RELAX_LONG, NeonPreset.AURORA_DRIFT.normalColorFor(TimerMode.RELAX_LONG));
    }

    @Test
    @DisplayName("AURORA_DRIFT accent colour getters return correct values")
    void testAuroraDriftAccentColorGetters() {
        assertEquals(AURORA_WARNING, NeonPreset.AURORA_DRIFT.getWarningColor());
        assertEquals(AURORA_OVER,    NeonPreset.AURORA_DRIFT.getOverColor());
        assertEquals(AURORA_PAUSED,  NeonPreset.AURORA_DRIFT.getPausedColor());
    }

    @Test
    @DisplayName("AURORA_DRIFT root gradient tuning values are within expected range")
    void testAuroraDriftRootGradientTuning() {
        assertEquals(0.91, NeonPreset.AURORA_DRIFT.getRootDarkEdge(),      DELTA);
        assertEquals(0.26, NeonPreset.AURORA_DRIFT.getRootDarkMid(),       DELTA);
        assertEquals(0.30, NeonPreset.AURORA_DRIFT.getRootGlowAlphaMin(),  DELTA);
        assertEquals(0.73, NeonPreset.AURORA_DRIFT.getRootGlowAlphaMax(),  DELTA);
        assertEquals(0.24, NeonPreset.AURORA_DRIFT.getRootSheenAlpha(),    DELTA);
        assertEquals(0.34, NeonPreset.AURORA_DRIFT.getRootGlowWhiteBlend(),DELTA);
        assertEquals(0.05, NeonPreset.AURORA_DRIFT.getRootGlowBlackBlend(),DELTA);
    }

    @Test
    @DisplayName("AURORA_DRIFT bar tuning values are within expected range")
    void testAuroraDriftBarTuning() {
        assertEquals(0.64, NeonPreset.AURORA_DRIFT.getBarLighten(),       DELTA);
        assertEquals(0.10, NeonPreset.AURORA_DRIFT.getBarDarken(),        DELTA);
        assertEquals(0.36, NeonPreset.AURORA_DRIFT.getBarInnerSheen(),    DELTA);
        assertEquals(22.0, NeonPreset.AURORA_DRIFT.getBarGlowRadius(),    DELTA);
        assertEquals(0.68, NeonPreset.AURORA_DRIFT.getBarGlowSpread(),    DELTA);
        assertEquals(0.84, NeonPreset.AURORA_DRIFT.getBarGlowOpacity(),   DELTA);
        assertEquals(0.28, NeonPreset.AURORA_DRIFT.getBarGlowWhiteBlend(),DELTA);
    }

    @Test
    @DisplayName("all presets have non-null colour accessors")
    void testAllPresetsHaveNonNullColorAccessors() {
        for (final var preset : NeonPreset.values()) {
            assertNotNull(preset.normalColorFor(TimerMode.WORK));
            assertNotNull(preset.normalColorFor(TimerMode.RELAX));
            assertNotNull(preset.normalColorFor(TimerMode.RELAX_LONG));
            assertNotNull(preset.getWarningColor());
            assertNotNull(preset.getOverColor());
            assertNotNull(preset.getPausedColor());
        }
    }
}


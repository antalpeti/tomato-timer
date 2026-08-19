package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("NeonGlowProfile")
class NeonGlowProfileTest extends NeonGlowProfileHelper {

    @Test
    @DisplayName("SOFT profile has correct scale factors")
    void testSoftFactors() {
        assertEquals(SOFT_FACTOR_ALPHA,       NeonGlowProfile.SOFT.getFactorAlpha(),      DELTA);
        assertEquals(SOFT_FACTOR_SPREAD,      NeonGlowProfile.SOFT.getFactorSpread(),     DELTA);
        assertEquals(SOFT_FACTOR_WHITE_BLEND, NeonGlowProfile.SOFT.getFactorWhiteBlend(), DELTA);
        assertEquals(SOFT_FACTOR_SHEEN,       NeonGlowProfile.SOFT.getFactorSheen(),      DELTA);
        assertEquals(SOFT_FACTOR_DARKEN,      NeonGlowProfile.SOFT.getFactorDarken(),     DELTA);
    }

    @Test
    @DisplayName("BALANCED profile has all scale factors equal to 1.0")
    void testBalancedFactors() {
        assertEquals(BALANCED_FACTOR_ALL, NeonGlowProfile.BALANCED.getFactorAlpha(),      DELTA);
        assertEquals(BALANCED_FACTOR_ALL, NeonGlowProfile.BALANCED.getFactorSpread(),     DELTA);
        assertEquals(BALANCED_FACTOR_ALL, NeonGlowProfile.BALANCED.getFactorWhiteBlend(), DELTA);
        assertEquals(BALANCED_FACTOR_ALL, NeonGlowProfile.BALANCED.getFactorSheen(),      DELTA);
        assertEquals(BALANCED_FACTOR_ALL, NeonGlowProfile.BALANCED.getFactorDarken(),     DELTA);
    }

    @Test
    @DisplayName("VIVID profile has correct scale factors")
    void testVividFactors() {
        assertEquals(VIVID_FACTOR_ALPHA,       NeonGlowProfile.VIVID.getFactorAlpha(),      DELTA);
        assertEquals(VIVID_FACTOR_SPREAD,      NeonGlowProfile.VIVID.getFactorSpread(),     DELTA);
        assertEquals(VIVID_FACTOR_WHITE_BLEND, NeonGlowProfile.VIVID.getFactorWhiteBlend(), DELTA);
        assertEquals(VIVID_FACTOR_SHEEN,       NeonGlowProfile.VIVID.getFactorSheen(),      DELTA);
        assertEquals(VIVID_FACTOR_DARKEN,      NeonGlowProfile.VIVID.getFactorDarken(),     DELTA);
    }

    @Test
    @DisplayName("fromName returns the matching profile for each canonical name")
    void testFromNameReturnsMatchingProfileForKnownNames() {
        for (final var profile : NeonGlowProfile.values()) {
            assertEquals(profile, NeonGlowProfile.fromName(profile.name()));
        }
    }

    @Test
    @DisplayName("fromName returns BALANCED for an unknown name")
    void testFromNameReturnsBalancedForUnknownName() {
        assertEquals(NeonGlowProfile.BALANCED, NeonGlowProfile.fromName(NAME_UNKNOWN));
    }

    @Test
    @DisplayName("fromName returns BALANCED when name is null")
    void testFromNameReturnsBalancedForNull() {
        assertEquals(NeonGlowProfile.BALANCED, NeonGlowProfile.fromName(null));
    }
}


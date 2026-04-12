package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("IconFactory")
class IconFactoryTest extends IconFactoryHelper {

    // ── iconSizeForHeight ─────────────────────────────────────────────────────

    @Test
    @DisplayName("iconSizeForHeight is clamped to 16 for a very small container height")
    void testIconSizeForHeightClampedToMin() {
        assertEquals(MIN_ICON_SIZE, IconFactory.iconSizeForHeight(TINY_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("iconSizeForHeight is clamped to 52 for a very large container height")
    void testIconSizeForHeightClampedToMax() {
        assertEquals(MAX_ICON_SIZE, IconFactory.iconSizeForHeight(LARGE_HEIGHT), DELTA);
    }

    @Test
    @DisplayName("iconSizeForHeight returns 0.52 × height when within clamp range")
    void testIconSizeForHeightWithinRange() {
        final var height   = MEDIUM_HEIGHT;
        final var expected = Math.max(MIN_ICON_SIZE, Math.min(MAX_ICON_SIZE, height * 0.52));
        assertEquals(expected, IconFactory.iconSizeForHeight(height), DELTA);
    }

    @Test
    @DisplayName("NOMINAL constant equals 24.0")
    void testNominalConstant() {
        assertEquals(24.0, IconFactory.NOMINAL, DELTA);
    }

    // ── Path constants ────────────────────────────────────────────────────────

    @Test
    @DisplayName("all SVG path constants are non-null and non-empty")
    void testPathConstantsAreNonNullAndNonEmpty() {
        assertNonEmpty(IconFactory.PATH_SETTINGS);
        assertNonEmpty(IconFactory.PATH_PLAY);
        assertNonEmpty(IconFactory.PATH_PAUSE);
        assertNonEmpty(IconFactory.PATH_RESET);
        assertNonEmpty(IconFactory.PATH_WORK);
        assertNonEmpty(IconFactory.PATH_RELAX);
        assertNonEmpty(IconFactory.PATH_CLOCK);
        assertNonEmpty(IconFactory.PATH_CALENDAR);
        assertNonEmpty(IconFactory.PATH_VOLUME);
        assertNonEmpty(IconFactory.PATH_PIN);
        assertNonEmpty(IconFactory.PATH_CLOSE);
        assertNonEmpty(IconFactory.PATH_TASKBAR);
        assertNonEmpty(IconFactory.PATH_PALETTE);
    }

    // ── Colour constants ──────────────────────────────────────────────────────

    @Test
    @DisplayName("all colour constants start with '#' and have length 7")
    void testColorConstantsHaveValidHexFormat() {
        assertValidHex(IconFactory.COLOR_SETTINGS);
        assertValidHex(IconFactory.COLOR_RESET);
        assertValidHex(IconFactory.COLOR_PLAY);
        assertValidHex(IconFactory.COLOR_PAUSE);
        assertValidHex(IconFactory.COLOR_WORK);
        assertValidHex(IconFactory.COLOR_RELAX);
        assertValidHex(IconFactory.COLOR_BACK);
        assertValidHex(IconFactory.COLOR_CALENDAR);
        assertValidHex(IconFactory.COLOR_CALENDAR_TEST);
        assertValidHex(IconFactory.COLOR_VOLUME);
        assertValidHex(IconFactory.COLOR_PIN);
        assertValidHex(IconFactory.COLOR_PIN_ON);
        assertValidHex(IconFactory.COLOR_CLOSE);
        assertValidHex(IconFactory.COLOR_TASKBAR);
        assertValidHex(IconFactory.COLOR_THEME);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void assertNonEmpty(String s) {
        assertNotNull(s);
        assertFalse(s.isBlank());
    }

    private static void assertValidHex(String hex) {
        assertNotNull(hex);
        assertTrue(hex.startsWith("#"), "Expected '#' prefix: " + hex);
        assertEquals(7, hex.length(), "Expected 7-char hex: " + hex);
    }
}


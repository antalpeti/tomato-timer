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
@DisplayName("WindowsTaskbarPreviewButtonsHelper (private utilities)")
class WindowsTaskbarPreviewButtonsHelperTest extends WindowsTaskbarPreviewButtonsHelperHelper {

    // ── lowWord ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("lowWord extracts the low 16 bits of a long value")
    void testLowWordExtractsLow16Bits() throws Exception {
        assertEquals(0x1234, invokeLowWord(0x56781234L));
    }

    @Test
    @DisplayName("lowWord returns 0 when low 16 bits are zero")
    void testLowWordReturnsZeroForZeroLowBits() throws Exception {
        assertEquals(0, invokeLowWord(0xFFFF0000L));
    }

    @Test
    @DisplayName("lowWord returns 0xFFFF when low 16 bits are all set")
    void testLowWordReturnsMaxForAllSetLowBits() throws Exception {
        assertEquals(0xFFFF, invokeLowWord(0xFFFFFFFFL));
    }

    // ── highWord ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("highWord extracts bits 16–31 of a long value")
    void testHighWordExtractsBits16to31() throws Exception {
        assertEquals(0x5678, invokeHighWord(0x56781234L));
    }

    @Test
    @DisplayName("highWord returns 0 when high 16 bits are zero")
    void testHighWordReturnsZeroForZeroHighBits() throws Exception {
        assertEquals(0, invokeHighWord(0x0000FFFFL));
    }

    @Test
    @DisplayName("highWord returns 0xFFFF when bits 16–31 are all set")
    void testHighWordReturnsMaxForAllSetHighBits() throws Exception {
        assertEquals(0xFFFF, invokeHighWord(0xFFFF0000L));
    }

    // ── hexToAwtColor ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("hexToAwtColor parses a hex string with leading #")
    void testHexToAwtColorWithHash() throws Exception {
        final var color = invokeHexToAwtColor("#FF0000");
        assertEquals(255, color.getRed());
        assertEquals(0,   color.getGreen());
        assertEquals(0,   color.getBlue());
    }

    @Test
    @DisplayName("hexToAwtColor parses a hex string without leading #")
    void testHexToAwtColorWithoutHash() throws Exception {
        final var color = invokeHexToAwtColor("00FF00");
        assertEquals(0,   color.getRed());
        assertEquals(255, color.getGreen());
        assertEquals(0,   color.getBlue());
    }

    @Test
    @DisplayName("hexToAwtColor parses the IconFactory COLOR_RESET constant correctly")
    void testHexToAwtColorParsesIconFactoryConstant() throws Exception {
        final var color = invokeHexToAwtColor(IconFactory.COLOR_RESET);
        assertNotNull(color);
        assertTrue(color.getRed() > 0);
    }

    // ── parseNumbers ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("parseNumbers returns empty array for null input")
    void testParseNumbersReturnsEmptyForNull() throws Exception {
        final var result = invokeParseNumbers(null);
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("parseNumbers returns empty array for blank input")
    void testParseNumbersReturnsEmptyForBlank() throws Exception {
        final var result = invokeParseNumbers("   ");
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("parseNumbers extracts a single integer token")
    void testParseNumbersSingleInteger() throws Exception {
        final var result = invokeParseNumbers("42");
        assertEquals(1, result.length);
        assertEquals(42.0, result[0]);
    }

    @Test
    @DisplayName("parseNumbers extracts multiple comma-separated integers")
    void testParseNumbersMultipleIntegers() throws Exception {
        final var result = invokeParseNumbers("1,2,3");
        assertEquals(3, result.length);
        assertEquals(1.0, result[0]);
        assertEquals(2.0, result[1]);
        assertEquals(3.0, result[2]);
    }

    @Test
    @DisplayName("parseNumbers extracts decimal numbers")
    void testParseNumbersDecimals() throws Exception {
        final var result = invokeParseNumbers("3.14 2.71");
        assertEquals(2, result.length);
        assertEquals(3.14, result[0], 1e-9);
        assertEquals(2.71, result[1], 1e-9);
    }

    @Test
    @DisplayName("parseNumbers handles negative numbers")
    void testParseNumbersNegativeValues() throws Exception {
        final var result = invokeParseNumbers("-5,10,-3.5");
        assertEquals(3, result.length);
        assertEquals(-5.0,  result[0], 1e-9);
        assertEquals(10.0,  result[1], 1e-9);
        assertEquals(-3.5,  result[2], 1e-9);
    }

    // ── isWindows ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isWindows returns false when os.name is Linux")
    void testIsWindowsReturnsFalseOnLinux() throws Exception {
        final var original = setOsName(OS_LINUX);
        try {
            assertFalse(invokeIsWindows());
        } finally {
            setOsName(original);
        }
    }

    @Test
    @DisplayName("isWindows returns true when os.name contains 'win'")
    void testIsWindowsReturnsTrueOnWindows() throws Exception {
        final var original = setOsName(OS_WINDOWS);
        try {
            assertTrue(invokeIsWindows());
        } finally {
            setOsName(original);
        }
    }

    // ── toTooltip ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("toTooltip returns a char array of exactly 260 elements")
    void testToTooltipReturns260CharArray() throws Exception {
        final var tip = invokeToTooltip("Pause");
        assertEquals(260, tip.length);
    }

    @Test
    @DisplayName("toTooltip copies text into the beginning of the array")
    void testToTooltipCopiesTextAtStart() throws Exception {
        final var text = "Reset";
        final var tip  = invokeToTooltip(text);
        assertEquals('R', tip[0]);
        assertEquals('e', tip[1]);
        assertEquals('s', tip[2]);
        assertEquals('e', tip[3]);
        assertEquals('t', tip[4]);
        assertEquals('\0', tip[5]);
    }

    @Test
    @DisplayName("toTooltip handles empty string without throwing")
    void testToTooltipHandlesEmptyString() throws Exception {
        final var tip = invokeToTooltip("");
        assertEquals(260, tip.length);
        assertEquals('\0', tip[0]);
    }
}


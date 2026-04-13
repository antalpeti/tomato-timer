package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("WindowsNativeWindowIconHelper")
class WindowsNativeWindowIconHelperTest extends WindowsNativeWindowIconHelperHelper {

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
    @DisplayName("isWindows returns true when os.name contains 'win' (case-insensitive)")
    void testIsWindowsReturnsTrueOnWindows() throws Exception {
        final var original = setOsName(OS_WINDOWS);
        try {
            assertTrue(invokeIsWindows());
        } finally {
            setOsName(original);
        }
    }

    // ── computeCrc32 ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("computeCrc32 returns a consistent result for the same input")
    void testComputeCrc32IsConsistentForSameInput() throws Exception {
        final var data = new byte[]{1, 2, 3, 4, 5};
        final var crc1 = invokeComputeCrc32(data);
        final var crc2 = invokeComputeCrc32(data);
        assertEquals(crc1, crc2);
    }

    @Test
    @DisplayName("computeCrc32 returns a non-negative long value")
    void testComputeCrc32ReturnsNonNegativeLong() throws Exception {
        final var data = new byte[]{10, 20, 30};
        final var crc  = invokeComputeCrc32(data);
        assertTrue(crc >= 0);
    }

    @Test
    @DisplayName("computeCrc32 returns different values for different inputs")
    void testComputeCrc32DifferentInputsGiveDifferentResults() throws Exception {
        final var crc1 = invokeComputeCrc32(new byte[]{1, 2, 3});
        final var crc2 = invokeComputeCrc32(new byte[]{4, 5, 6});
        assertNotEquals(crc1, crc2);
    }

    @Test
    @DisplayName("computeCrc32 returns 0 for an empty byte array")
    void testComputeCrc32ForEmptyArray() throws Exception {
        final var crc = invokeComputeCrc32(new byte[0]);
        assertEquals(0L, crc);
    }

    // ── buildBmpIco ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("buildBmpIco produces a byte array of the expected length for a 2×2 image")
    void testBuildBmpIcoExpectedLength() throws Exception {
        final var width  = 2;
        final var height = 2;
        final var argb   = new int[width * height];
        final var ico    = invokeBuildBmpIco(argb, width, height);

        final var andMaskRowBytes = ((width + 31) / 32) * 4;
        final var bmpDataSize     = 40 + width * height * 4 + andMaskRowBytes * height;
        final var expected        = 6 + 16 + bmpDataSize;
        assertEquals(expected, ico.length);
    }

    @Test
    @DisplayName("buildBmpIco ICO header has reserved=0, type=1, count=1")
    void testBuildBmpIcoHeaderSignature() throws Exception {
        final var ico = invokeBuildBmpIco(new int[1], 1, 1);
        final var buf = ByteBuffer.wrap(ico).order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(0,  buf.getShort(0));
        assertEquals(1,  buf.getShort(2));
        assertEquals(1,  buf.getShort(4));
    }

    @Test
    @DisplayName("buildBmpIco ICONDIRENTRY records correct image size for a 1×1 image")
    void testBuildBmpIcoDirEntrySize() throws Exception {
        final var ico = invokeBuildBmpIco(new int[1], 1, 1);
        final var buf = ByteBuffer.wrap(ico).order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(1, buf.get(6) & 0xFF);
        assertEquals(1, buf.get(7) & 0xFF);
    }

    @Test
    @DisplayName("buildBmpIco correctly converts ARGB pixel to BGRA byte order")
    void testBuildBmpIcoArgbToBgraConversion() throws Exception {
        final var argb  = new int[]{ 0xAABBCCDD };
        final var ico   = invokeBuildBmpIco(argb, 1, 1);
        final var xorStart = 6 + 16 + 40;
        assertEquals((byte) 0xDD, ico[xorStart]);
        assertEquals((byte) 0xCC, ico[xorStart + 1]);
        assertEquals((byte) 0xBB, ico[xorStart + 2]);
        assertEquals((byte) 0xAA, ico[xorStart + 3]);
    }

    // ── resetCache ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("resetCache sets cacheInitialized to false regardless of prior state")
    void testResetCacheClearsCacheInitializedFlag() throws Exception {
        // Pre-seed the cache as though a prior successful apply() had run.
        setCacheInitialized(true);
        assertTrue(getCacheInitialized(), "pre-condition: cacheInitialized must be true before reset");

        invokeResetCache();

        assertFalse(getCacheInitialized(),
                "resetCache() must set cacheInitialized to false so the next apply() "
                + "unconditionally writes the ICO file and sends WM_SETICON");
    }

    @Test
    @DisplayName("resetCache is idempotent – calling it twice leaves cacheInitialized false")
    void testResetCacheIsIdempotent() throws Exception {
        setCacheInitialized(true);
        invokeResetCache();
        invokeResetCache(); // second call must not throw or flip back to true
        assertFalse(getCacheInitialized(), "cacheInitialized must remain false after double reset");
    }

    @Test
    @DisplayName("resetCache on an already-false cache is a safe no-op")
    void testResetCacheWhenAlreadyFalseIsNoop() throws Exception {
        setCacheInitialized(false);
        invokeResetCache(); // must not throw
        assertFalse(getCacheInitialized());
    }

    // ── clearNativeIcon (non-Windows no-op) ───────────────────────────────────

    @Test
    @DisplayName("clearNativeIcon is a no-op on non-Windows platforms (does not throw)")
    void testClearNativeIconIsNoOpOnNonWindows() {
        final var original = setOsName(OS_LINUX);
        try {
            // Must complete without exception on non-Windows platforms.
            WindowsNativeWindowIconHelper.clearNativeIcon();
        } finally {
            setOsName(original);
        }
    }

    @Test
    @DisplayName("clearNativeIcon does not mutate cacheInitialized (cache reset is resetCache's job)")
    void testClearNativeIconDoesNotMutateCacheFlag() throws Exception {
        // Run on non-Windows (Linux) so no real native calls are made.
        final var original = setOsName(OS_LINUX);
        try {
            setCacheInitialized(true);
            WindowsNativeWindowIconHelper.clearNativeIcon();
            // clearNativeIcon is a no-op on non-Windows; cache flag unchanged.
            assertTrue(getCacheInitialized(),
                    "clearNativeIcon must not alter the CRC cache flag – that is resetCache()'s responsibility");
        } finally {
            setOsName(original);
        }
    }
}

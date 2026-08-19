package com.tomatotimer;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke / unit tests for {@link TaskbarIconRenderer}.
 *
 * <p>All rendering methods require the JavaFX toolkit and must execute on the
 * FX Application Thread; they are therefore skipped automatically in headless
 * environments where the toolkit cannot be initialised.</p>
 */
@DisplayName("TaskbarIconRenderer")
class TaskbarIconRendererTest {

    private static final Color ACCENT = Color.CYAN;
    private static final double DELTA  = 1.0;

    @BeforeAll
    static void ensureJavaFx() {
        Assumptions.assumeTrue(
                JavaFxTestHelper.ensureToolkitStarted(),
                "JavaFX toolkit unavailable – TaskbarIconRenderer tests skipped");
    }

    // =========================================================================
    //  1-arg single-line: render(String, Color)  →  default 64 px
    // =========================================================================

    @Test
    @DisplayName("render(timeText,color) returns a 64×64 Image")
    void testRenderDefaultSizeReturns64x64() throws Exception {
        final var img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("4:32", ACCENT));
        assertNotNull(img);
        assertEquals(64.0, img.getWidth(),  DELTA);
        assertEquals(64.0, img.getHeight(), DELTA);
    }

    @Test
    @DisplayName("render(timeText,color) – short text (≤4 chars)")
    void testRenderShortText() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("1:23", Color.RED));
        assertNotNull(img);
    }

    @Test
    @DisplayName("render(timeText,color) – 5-char text")
    void testRenderFiveCharText() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("59:59", Color.GREEN));
        assertNotNull(img);
    }

    @Test
    @DisplayName("render(timeText,color) – 6+ char text (HH:MM:SS)")
    void testRenderLongText() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("1:23:45", Color.BLUE));
        assertNotNull(img);
    }

    // =========================================================================
    //  2-arg single-line: render(String, Color, int)  →  explicit size
    // =========================================================================

    @Test
    @DisplayName("render(timeText,color,size) produces an Image of the requested size")
    void testRenderSingleLineExplicitSize() throws Exception {
        for (final int sz : new int[]{16, 24, 32, 48, 64}) {
            final Image img = JavaFxTestHelper.runOnFxThread(
                    () -> TaskbarIconRenderer.render("4:32", Color.MAGENTA, sz));
            assertNotNull(img, "render for size=" + sz + " returned null");
            assertEquals(sz, img.getWidth(),  DELTA, "width  mismatch for size=" + sz);
            assertEquals(sz, img.getHeight(), DELTA, "height mismatch for size=" + sz);
        }
    }

    // =========================================================================
    //  renderAllSizes(String, Color)  →  list of 5 images
    // =========================================================================

    @Test
    @DisplayName("renderAllSizes(timeText,color) returns 5 non-null Images")
    void testRenderAllSizesSingleLine() throws Exception {
        final List<Image> imgs = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.renderAllSizes("4:32", ACCENT));
        assertNotNull(imgs);
        assertEquals(5, imgs.size());
        imgs.forEach(img -> assertNotNull(img));
    }

    // =========================================================================
    //  2-line (minute/second): render(String, String, Color)  →  64 px
    // =========================================================================

    @Test
    @DisplayName("render(minuteText,secondText,color) returns a 64×64 Image")
    void testRenderTwoLineDefault() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("04", "32", ACCENT));
        assertNotNull(img);
        assertEquals(64.0, img.getWidth(),  DELTA);
        assertEquals(64.0, img.getHeight(), DELTA);
    }

    // =========================================================================
    //  2-line: render(String, String, Color, int)  →  explicit size
    // =========================================================================

    @Test
    @DisplayName("render(minuteText,secondText,color,size) returns Image of requested size")
    void testRenderTwoLineExplicitSize() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("04", "32", Color.ORANGE, 32));
        assertNotNull(img);
        assertEquals(32.0, img.getWidth(),  DELTA);
        assertEquals(32.0, img.getHeight(), DELTA);
    }

    // =========================================================================
    //  2-line with font: render(String, String, Color, int, double)
    // =========================================================================

    @Test
    @DisplayName("render(minuteText,secondText,color,size,baseFontSize) returns correct Image")
    void testRenderTwoLineWithFontSize() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("04", "32", Color.TEAL, 48, 20.0));
        assertNotNull(img);
        assertEquals(48.0, img.getWidth(),  DELTA);
        assertEquals(48.0, img.getHeight(), DELTA);
    }

    // =========================================================================
    //  renderAllSizes(String, String, Color, double)  →  5 images
    // =========================================================================

    @Test
    @DisplayName("renderAllSizes(minuteText,secondText,color,baseFontSize) returns 5 Images")
    void testRenderAllSizesTwoLine() throws Exception {
        final List<Image> imgs = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.renderAllSizes("04", "32", ACCENT, 22.0));
        assertNotNull(imgs);
        assertEquals(5, imgs.size());
        imgs.forEach(img -> assertNotNull(img));
    }

    // =========================================================================
    //  3-line (hour/minute/second): render(String, String, String, Color)  →  64 px
    // =========================================================================

    @Test
    @DisplayName("render(hourText,minuteText,secondText,color) returns a 64×64 Image")
    void testRenderThreeLineDefault() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("01", "04", "32", ACCENT));
        assertNotNull(img);
        assertEquals(64.0, img.getWidth(),  DELTA);
        assertEquals(64.0, img.getHeight(), DELTA);
    }

    // =========================================================================
    //  3-line: render(String, String, String, Color, int)  →  explicit size
    // =========================================================================

    @Test
    @DisplayName("render(hourText,minuteText,secondText,color,size) returns Image of correct size")
    void testRenderThreeLineExplicitSize() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("01", "04", "32", Color.PINK, 48));
        assertNotNull(img);
        assertEquals(48.0, img.getWidth(),  DELTA);
        assertEquals(48.0, img.getHeight(), DELTA);
    }

    // =========================================================================
    //  3-line with font: render(String, String, String, Color, int, double)
    // =========================================================================

    @Test
    @DisplayName("render(hourText,minuteText,secondText,color,size,baseFontSize) returns correct Image")
    void testRenderThreeLineWithFontSize() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.render("01", "04", "32", Color.GOLD, 32, 15.0));
        assertNotNull(img);
        assertEquals(32.0, img.getWidth(),  DELTA);
        assertEquals(32.0, img.getHeight(), DELTA);
    }

    // =========================================================================
    //  renderAllSizes(String, String, String, Color)  →  5 images
    // =========================================================================

    @Test
    @DisplayName("renderAllSizes(hourText,minuteText,secondText,color) returns 5 Images")
    void testRenderAllSizesThreeLineNoFont() throws Exception {
        final List<Image> imgs = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.renderAllSizes("01", "04", "32", ACCENT));
        assertNotNull(imgs);
        assertEquals(5, imgs.size());
        imgs.forEach(img -> assertNotNull(img));
    }

    // =========================================================================
    //  renderAllSizes(String, String, String, Color, double)  →  5 images
    // =========================================================================

    @Test
    @DisplayName("renderAllSizes(hourText,minuteText,secondText,color,baseFontSize) returns 5 Images")
    void testRenderAllSizesThreeLineWithFont() throws Exception {
        final List<Image> imgs = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.renderAllSizes("01", "04", "32", ACCENT, 14.0));
        assertNotNull(imgs);
        assertEquals(5, imgs.size());
        imgs.forEach(img -> assertNotNull(img));
    }

    // =========================================================================
    //  renderHorizontal(String, Color, int, double)
    // =========================================================================

    @Test
    @DisplayName("renderHorizontal returns an Image of the requested size")
    void testRenderHorizontal() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.renderHorizontal("04:32", ACCENT, 64, 18.0));
        assertNotNull(img);
        assertEquals(64.0, img.getWidth(),  DELTA);
        assertEquals(64.0, img.getHeight(), DELTA);
    }

    @Test
    @DisplayName("renderHorizontal works for an HH:MM:SS (8-char) string")
    void testRenderHorizontalLongText() throws Exception {
        final Image img = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.renderHorizontal("01:04:32", ACCENT, 64, 13.0));
        assertNotNull(img);
    }

    // =========================================================================
    //  renderAllSizesHorizontal(String, Color, double)  →  5 images
    // =========================================================================

    @Test
    @DisplayName("renderAllSizesHorizontal returns 5 non-null Images")
    void testRenderAllSizesHorizontal() throws Exception {
        final List<Image> imgs = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.renderAllSizesHorizontal("04:32", ACCENT, 18.0));
        assertNotNull(imgs);
        assertEquals(5, imgs.size());
        imgs.forEach(img -> assertNotNull(img));
    }

    // =========================================================================
    //  Sanity: individual sizes in all-sizes lists match the standard set
    // =========================================================================

    @Test
    @DisplayName("renderAllSizes(timeText,color) list has images with sizes [16,24,32,48,64]")
    void testRenderAllSizesDimensions() throws Exception {
        final List<Image> imgs = JavaFxTestHelper.runOnFxThread(
                () -> TaskbarIconRenderer.renderAllSizes("4:32", ACCENT));
        final int[] expected = {16, 24, 32, 48, 64};
        for (int i = 0; i < expected.length; i++) {
            final int exp = expected[i];
            assertEquals(exp, imgs.get(i).getWidth(),  DELTA, "width  at index " + i);
            assertEquals(exp, imgs.get(i).getHeight(), DELTA, "height at index " + i);
        }
    }
}


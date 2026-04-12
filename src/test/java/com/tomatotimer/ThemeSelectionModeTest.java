package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("ThemeSelectionMode")
class ThemeSelectionModeTest extends ThemeSelectionModeHelper {

    @Test
    @DisplayName("getDisplayName returns the human-readable label for each constant")
    void testGetDisplayNameReturnsExpectedLabels() {
        assertEquals(DISPLAY_NAME_STATIC,     ThemeSelectionMode.STATIC.getDisplayName());
        assertEquals(DISPLAY_NAME_SEQUENTIAL, ThemeSelectionMode.SEQUENTIAL.getDisplayName());
        assertEquals(DISPLAY_NAME_RANDOM,     ThemeSelectionMode.RANDOM.getDisplayName());
        assertEquals(DISPLAY_NAME_SHUFFLE,    ThemeSelectionMode.SHUFFLE.getDisplayName());
    }

    @Test
    @DisplayName("toString returns the same value as getDisplayName for each constant")
    void testToStringReturnsDisplayName() {
        for (final var mode : ThemeSelectionMode.values()) {
            assertEquals(mode.getDisplayName(), mode.toString());
        }
    }

    @Test
    @DisplayName("fromName returns the matching mode for each canonical name")
    void testFromNameReturnsCorrectModeForKnownNames() {
        for (final var mode : ThemeSelectionMode.values()) {
            assertEquals(mode, ThemeSelectionMode.fromName(mode.name()));
        }
    }

    @Test
    @DisplayName("fromName returns STATIC for an unknown name")
    void testFromNameReturnsStaticForUnknownName() {
        assertEquals(ThemeSelectionMode.STATIC, ThemeSelectionMode.fromName(NAME_UNKNOWN));
    }

    @Test
    @DisplayName("fromName returns STATIC when name is null")
    void testFromNameReturnsStaticForNull() {
        assertEquals(ThemeSelectionMode.STATIC, ThemeSelectionMode.fromName(null));
    }
}


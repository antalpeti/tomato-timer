package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimerMode")
class TimerModeTest extends TimerModeHelper {

    @Test
    @DisplayName("getValue returns correct value for each constant")
    void testGetValueReturnsCorrectValueForAllConstants() {
        assertEquals(VALUE_WORK,       TimerMode.WORK.getValue());
        assertEquals(VALUE_RELAX,      TimerMode.RELAX.getValue());
        assertEquals(VALUE_RELAX_LONG, TimerMode.RELAX_LONG.getValue());
    }

    @Test
    @DisplayName("fromValue returns the correct mode for each known value")
    void testFromValueReturnsCorrectModeForKnownValues() {
        assertEquals(TimerMode.WORK,       TimerMode.fromValue(VALUE_WORK));
        assertEquals(TimerMode.RELAX,      TimerMode.fromValue(VALUE_RELAX));
        assertEquals(TimerMode.RELAX_LONG, TimerMode.fromValue(VALUE_RELAX_LONG));
    }

    @Test
    @DisplayName("fromValue returns WORK for an unknown value")
    void testFromValueReturnsWorkForUnknownValue() {
        assertEquals(TimerMode.WORK, TimerMode.fromValue(VALUE_UNKNOWN));
    }
}


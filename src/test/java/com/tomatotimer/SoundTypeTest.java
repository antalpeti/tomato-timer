package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("SoundType")
class SoundTypeTest extends SoundTypeHelper {

    @Test
    @DisplayName("getIndex returns correct value for each constant")
    void testGetIndexReturnsCorrectValueForAllConstants() {
        assertEquals(INDEX_RESUME,       SoundType.RESUME.getIndex());
        assertEquals(INDEX_PAUSE,        SoundType.PAUSE.getIndex());
        assertEquals(INDEX_WORK_DONE,    SoundType.WORK_DONE.getIndex());
        assertEquals(INDEX_REST_TIMEOUT, SoundType.REST_TIMEOUT.getIndex());
    }

    @Test
    @DisplayName("values() contains exactly four constants in declaration order")
    void testValuesContainsFourConstantsInOrder() {
        final var values = SoundType.values();
        assertEquals(4, values.length);
        assertEquals(SoundType.RESUME,       values[0]);
        assertEquals(SoundType.PAUSE,        values[1]);
        assertEquals(SoundType.WORK_DONE,    values[2]);
        assertEquals(SoundType.REST_TIMEOUT, values[3]);
    }
}


package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskbarTimeLayout")
class TaskbarTimeLayoutTest extends TaskbarTimeLayoutHelper {

    @Test
    @DisplayName("fromName returns VERTICAL for the canonical name")
    void testFromNameReturnsVerticalForKnownName() {
        assertEquals(TaskbarTimeLayout.VERTICAL, TaskbarTimeLayout.fromName(NAME_VERTICAL));
    }

    @Test
    @DisplayName("fromName returns HORIZONTAL for the canonical name")
    void testFromNameReturnsHorizontalForKnownName() {
        assertEquals(TaskbarTimeLayout.HORIZONTAL, TaskbarTimeLayout.fromName(NAME_HORIZONTAL));
    }

    @Test
    @DisplayName("fromName returns VERTICAL for an unknown name")
    void testFromNameReturnsVerticalForUnknownName() {
        assertEquals(TaskbarTimeLayout.VERTICAL, TaskbarTimeLayout.fromName(NAME_UNKNOWN));
    }

    @Test
    @DisplayName("fromName returns VERTICAL when name is null")
    void testFromNameReturnsVerticalForNull() {
        assertEquals(TaskbarTimeLayout.VERTICAL, TaskbarTimeLayout.fromName(null));
    }

    @Test
    @DisplayName("fromName returns VERTICAL for an empty string")
    void testFromNameReturnsVerticalForEmptyString() {
        assertEquals(TaskbarTimeLayout.VERTICAL, TaskbarTimeLayout.fromName(""));
    }
}


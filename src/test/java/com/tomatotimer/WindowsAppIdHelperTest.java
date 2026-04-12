package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("WindowsAppIdHelper")
class WindowsAppIdHelperTest extends WindowsAppIdHelperHelper {

    @Test
    @DisplayName("APP_USER_MODEL_ID constant has the expected value")
    void testAppUserModelIdConstant() {
        assertEquals(EXPECTED_APP_ID, WindowsAppIdHelper.APP_USER_MODEL_ID);
    }

    @Test
    @DisplayName("apply() does not throw on a non-Windows platform")
    void testApplyDoesNotThrowOnNonWindowsPlatform() {
        final var original = setOsName(OS_LINUX);
        try {
            assertDoesNotThrow(WindowsAppIdHelper::apply);
        } finally {
            setOsName(original);
        }
    }

    @Test
    @DisplayName("apply() does not throw when os.name indicates Windows (JNA may fail silently)")
    void testApplyDoesNotThrowOnWindowsPlatform() {
        final var original = setOsName(OS_WINDOWS);
        try {
            assertDoesNotThrow(WindowsAppIdHelper::apply);
        } finally {
            setOsName(original);
        }
    }
}


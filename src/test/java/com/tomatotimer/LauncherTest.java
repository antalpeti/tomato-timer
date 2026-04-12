package com.tomatotimer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

/**
 * Unit tests for {@link Launcher}.
 *
 * <p>Verifies the static call-chain (WindowsAppIdHelper.apply → App.main) by
 * mocking both callee classes with Mockito's {@code mockStatic} API.  This
 * avoids actually launching the JavaFX Application runtime during testing.</p>
 */
@DisplayName("Launcher")
class LauncherTest {

    // =========================================================================
    //  main() call-chain
    // =========================================================================

    @Test
    @DisplayName("main() calls WindowsAppIdHelper.apply() and then App.main(args)")
    void testMainCallChain() {
        try (MockedStatic<WindowsAppIdHelper> mockWin = Mockito.mockStatic(WindowsAppIdHelper.class);
             MockedStatic<App>                mockApp = Mockito.mockStatic(App.class)) {

            assertDoesNotThrow(() -> Launcher.main(new String[0]));

            // 1. WindowsAppIdHelper.apply() must have been called
            mockWin.verify(WindowsAppIdHelper::apply);

            // 2. App.main(String[]) must have been called with any array
            mockApp.verify(() -> App.main(any(String[].class)));
        }
    }

    @Test
    @DisplayName("main() calls WindowsAppIdHelper.apply() exactly once")
    void testApplyCalledExactlyOnce() {
        try (MockedStatic<WindowsAppIdHelper> mockWin = Mockito.mockStatic(WindowsAppIdHelper.class);
             MockedStatic<App>                mockApp = Mockito.mockStatic(App.class)) {

            Launcher.main(new String[0]);

            mockWin.verify(WindowsAppIdHelper::apply, times(1));
        }
    }

    @Test
    @DisplayName("main() calls App.main() exactly once")
    void testAppMainCalledExactlyOnce() {
        try (MockedStatic<WindowsAppIdHelper> mockWin = Mockito.mockStatic(WindowsAppIdHelper.class);
             MockedStatic<App>                mockApp = Mockito.mockStatic(App.class)) {

            Launcher.main(new String[0]);

            mockApp.verify(() -> App.main(any(String[].class)), times(1));
        }
    }

    @Test
    @DisplayName("main() forwards the same args reference to App.main()")
    void testMainForwardsArguments() {
        final String[] args = {"--arg1", "--arg2"};

        try (MockedStatic<WindowsAppIdHelper> mockWin = Mockito.mockStatic(WindowsAppIdHelper.class);
             MockedStatic<App>                mockApp = Mockito.mockStatic(App.class)) {

            Launcher.main(args);

            // Verify that App.main was called with the exact same array reference
            mockApp.verify(() -> App.main(args));
        }
    }

    @Test
    @DisplayName("main() does not throw when args is empty")
    void testMainDoesNotThrowWithEmptyArgs() {
        try (MockedStatic<WindowsAppIdHelper> mockWin = Mockito.mockStatic(WindowsAppIdHelper.class);
             MockedStatic<App>                mockApp = Mockito.mockStatic(App.class)) {

            assertDoesNotThrow(() -> Launcher.main(new String[0]));
        }
    }
}


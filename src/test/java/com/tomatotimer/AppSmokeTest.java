package com.tomatotimer;

import javafx.application.Application;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Structural smoke tests for {@link App}.
 *
 * <p>These tests verify compile-time and class-loading properties of {@code App}
 * without starting the JavaFX runtime or calling {@code Application.launch()},
 * so they run safely in any environment.</p>
 */
@DisplayName("App – structural smoke tests")
class AppSmokeTest {

    // =========================================================================
    //  Class hierarchy
    // =========================================================================

    @Test
    @DisplayName("App is a subclass of javafx.application.Application")
    void testAppExtendsApplication() {
        assertTrue(Application.class.isAssignableFrom(App.class),
                "App must extend javafx.application.Application");
    }

    // =========================================================================
    //  main() method signature
    // =========================================================================

    @Test
    @DisplayName("App.main(String[]) is a public static void method")
    void testMainMethodIsPublicStatic() throws NoSuchMethodException {
        final var method = App.class.getDeclaredMethod("main", String[].class);
        assertTrue(Modifier.isPublic(method.getModifiers()),
                "App.main must be public");
        assertTrue(Modifier.isStatic(method.getModifiers()),
                "App.main must be static");
        assertTrue(method.getReturnType() == void.class,
                "App.main must return void");
    }

    // =========================================================================
    //  Classpath resources referenced in start()
    // =========================================================================

    @Test
    @DisplayName("main.fxml is accessible as a classpath resource from App")
    void testMainFxmlResourceExists() {
        assertNotNull(App.class.getResource("main.fxml"),
                "main.fxml must be on the classpath next to App.class");
    }

    @Test
    @DisplayName("style.css is accessible as a classpath resource from App")
    void testStyleCssResourceExists() {
        assertNotNull(App.class.getResource("style.css"),
                "style.css must be on the classpath next to App.class");
    }

    // =========================================================================
    //  Non-final, instantiable (required by FX Application contract)
    // =========================================================================

    @Test
    @DisplayName("App can be instantiated via the public no-arg constructor")
    void testAppIsInstantiable() throws Exception {
        assertNotNull(App.class.getDeclaredConstructor().newInstance(),
                "App must have a public no-arg constructor (JavaFX requirement)");
    }
}


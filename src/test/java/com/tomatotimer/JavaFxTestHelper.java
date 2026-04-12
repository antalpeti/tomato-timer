package com.tomatotimer;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Shared utility that initialises the JavaFX toolkit exactly once per JVM
 * (using {@link Platform#startup(Runnable)}) and exposes helpers for
 * executing code on the FX Application Thread.
 *
 * <p>All public methods are thread-safe.  The toolkit initialisation result
 * is cached; once it is known to have failed the helper always returns
 * {@code false} from {@link #ensureToolkitStarted()} so callers can
 * use {@link org.junit.jupiter.api.Assumptions#assumeTrue(boolean)} to
 * skip FX-dependent tests in headless environments.</p>
 */
public final class JavaFxTestHelper {

    private static volatile boolean started = false;
    private static volatile boolean failed  = false;

    private JavaFxTestHelper() {}

    /**
     * Tries to start the JavaFX toolkit if it has not been started yet.
     *
     * @return {@code true} if the toolkit is ready, {@code false} if it could
     *         not be initialised (headless, no display, etc.)
     */
    public static synchronized boolean ensureToolkitStarted() {
        if (started) return true;
        if (failed)  return false;

        try {
            final var latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            if (!latch.await(10, TimeUnit.SECONDS)) {
                failed = true;
                return false;
            }
            // Prevent the FX thread from exiting when there are no open windows,
            // so it stays alive for the rest of the test run.
            Platform.setImplicitExit(false);
            started = true;
        } catch (IllegalStateException ignored) {
            // Toolkit was already started by a previous test class.
            started = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            failed = true;
        } catch (RuntimeException e) {
            // Glass/Prism could not initialise (no display, etc.).
            failed = true;
        }
        return started;
    }

    /**
     * Submits {@code task} to the FX Application Thread, waits up to 10 s for
     * it to complete, and returns its result.
     *
     * @param <T>  return type
     * @param task callable to execute on the FX thread
     * @return the value returned by {@code task}
     * @throws Exception if {@code task} throws, or if the timeout elapses
     */
    public static <T> T runOnFxThread(java.util.concurrent.Callable<T> task) throws Exception {
        final var result = new AtomicReference<T>();
        final var error  = new AtomicReference<Throwable>();
        final var latch  = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                result.set(task.call());
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for FX Application Thread");
        }
        if (error.get() != null) {
            throw new Exception("FX thread threw: " + error.get().getMessage(), error.get());
        }
        return result.get();
    }

    /**
     * Convenience overload for {@code Runnable} tasks that do not return a value.
     */
    public static void runOnFxThread(Runnable task) throws Exception {
        runOnFxThread(() -> {
            task.run();
            return null;
        });
    }
}


package it.unicam.cs.mpgc.rpg130077.controller.UI;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility helper for headless JavaFX testing and FX thread synchronization.
 */
public class JavaFXTestHelper {

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * Initializes the JavaFX toolkit once in a headless or test environment.
     */
    public static synchronized void initPlatform() {
        if (!initialized.get()) {
            try {
                Platform.startup(() -> {});
                Platform.setImplicitExit(false);
            } catch (IllegalStateException ignored) {
                // Platform already initialized in JVM
            }
            initialized.set(true);
        }
    }

    /**
     * Runs the specified action on the JavaFX Application Thread and blocks until completion.
     * Propagates any thrown exception back to the caller.
     *
     * @param runnable the action to execute
     * @throws Exception if execution fails or is interrupted
     */
    public static void runOnFxThread(ThrowingRunnable runnable) throws Exception {
        initPlatform();
        if (Platform.isFxApplicationThread()) {
            try {
                runnable.run();
            } catch (Exception e) {
                throw e;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        Throwable[] thrown = new Throwable[1];

        Platform.runLater(() -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                thrown[0] = t;
            } finally {
                latch.countDown();
            }
        });

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Timeout waiting for JavaFX thread execution");
        }

        if (thrown[0] != null) {
            if (thrown[0] instanceof Exception e) {
                throw e;
            }
            if (thrown[0] instanceof Error err) {
                throw err;
            }
            throw new RuntimeException(thrown[0]);
        }
    }

    /**
     * Waits for all currently queued Platform.runLater tasks to finish.
     *
     * @throws InterruptedException if thread is interrupted
     */
    public static void waitForRunLater() throws InterruptedException {
        initPlatform();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for Platform.runLater drain");
        }
    }
}

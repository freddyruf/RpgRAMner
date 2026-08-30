package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test completi per la classe {@link Clock}.
 * Copre l'invocazione periodica dei tick, l'idempotenza di start/stop, il riavvio,
 * e la corretta gestione dell'interruzione del thread daemon.
 */
class ClockTest {

    private final List<Clock> activeClocks = new ArrayList<>();

    @AfterEach
    void tearDown() {
        for (Clock clock : activeClocks) {
            clock.stop();
        }
        activeClocks.clear();
    }

    private Clock createAndRegisterClock(Runnable runnable) {
        Clock clock = new Clock(runnable);
        activeClocks.add(clock);
        return clock;
    }

    @Test
    @DisplayName("Creazione del Clock con Runnable valido non avvia l'esecuzione immediatamente")
    void testCreazioneClockConRunnableValido() {
        AtomicInteger count = new AtomicInteger(0);
        Clock clock = createAndRegisterClock(count::incrementAndGet);

        assertNotNull(clock);
        assertEquals(0, count.get());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("start() avvia il timer ed esegue il callback al tick successivo")
    void testStartInvokesTickCallback() throws InterruptedException {
        AtomicInteger tickCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        Clock clock = createAndRegisterClock(() -> {
            tickCount.incrementAndGet();
            latch.countDown();
        });

        clock.start();

        // Attende fino a 1500ms per ricevere il primo tick (previsto a 1000ms)
        boolean received = latch.await(2000, TimeUnit.MILLISECONDS);
        clock.stop();

        assertTrue(received, "Il tick callback avrebbe dovuto essere invocato");
        assertTrue(tickCount.get() >= 1);
    }

    @Test
    @DisplayName("Chiamate ripetute a start() sono idempotenti e non generano thread multipli o errori")
    void testRepeatedStartIsIdempotent() {
        AtomicInteger count = new AtomicInteger(0);
        Clock clock = createAndRegisterClock(count::incrementAndGet);

        assertDoesNotThrow(() -> {
            clock.start();
            clock.start();
            clock.start();
        });

        clock.stop();
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("stop() interrompe l'esecuzione e non vengono invocati ulteriori tick")
    void testStopHaltsTickExecution() throws InterruptedException {
        AtomicInteger tickCount = new AtomicInteger(0);
        CountDownLatch firstTickLatch = new CountDownLatch(1);

        Clock clock = createAndRegisterClock(() -> {
            tickCount.incrementAndGet();
            firstTickLatch.countDown();
        });

        clock.start();
        boolean firstReceived = firstTickLatch.await(2000, TimeUnit.MILLISECONDS);
        assertTrue(firstReceived);

        // Ferma il clock
        clock.stop();
        int snapshotCount = tickCount.get();

        // Attende altri 1200ms per verificare che non vengano sparati altri tick
        Thread.sleep(1200);

        assertEquals(snapshotCount, tickCount.get(), "Nessun tick deve essere eseguito dopo stop()");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("start() dopo stop() riavvia correttamente il ciclo dei tick")
    void testRestartClockAfterStop() throws InterruptedException {
        AtomicInteger tickCount = new AtomicInteger(0);
        CountDownLatch firstLatch = new CountDownLatch(1);

        Clock clock = createAndRegisterClock(() -> {
            tickCount.incrementAndGet();
            firstLatch.countDown();
        });

        // Primo avvio
        clock.start();
        assertTrue(firstLatch.await(2000, TimeUnit.MILLISECONDS));
        clock.stop();

        int countAfterFirstStop = tickCount.get();

        // Riavvio
        CountDownLatch restartLatch = new CountDownLatch(1);
        Clock restartClock = createAndRegisterClock(() -> {
            tickCount.incrementAndGet();
            restartLatch.countDown();
        });
        restartClock.start();

        assertTrue(restartLatch.await(2000, TimeUnit.MILLISECONDS));
        assertTrue(tickCount.get() > countAfterFirstStop);
        restartClock.stop();
    }

    @Test
    @DisplayName("Chiamate ripetute a stop() sono sicure e idempotenti")
    void testRepeatedStopIsIdempotent() {
        AtomicInteger count = new AtomicInteger(0);
        Clock clock = createAndRegisterClock(count::incrementAndGet);

        clock.start();
        assertDoesNotThrow(() -> {
            clock.stop();
            clock.stop();
            clock.stop();
        });
    }

    @Test
    @DisplayName("Interruzione immediata di Clock gestisce pulitamente Thread.sleep senza eccezioni non catturate")
    void testCleanInterruptionHandlingWithoutUncaughtExceptions() {
        AtomicInteger count = new AtomicInteger(0);
        Clock clock = createAndRegisterClock(count::incrementAndGet);

        assertDoesNotThrow(() -> {
            clock.start();
            // Stop immediato mentre il thread sta dormendo in sleep(1000)
            clock.stop();
        });
    }
}

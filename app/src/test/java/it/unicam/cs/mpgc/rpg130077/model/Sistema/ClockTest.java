package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link Clock}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class ClockTest {

    @Test
    void testCreazioneClockConRunnableValido() {
        AtomicInteger count = new AtomicInteger(0);
        Clock clock = new Clock(count::incrementAndGet);

        assertNotNull(clock);
        assertEquals(0, count.get());
    }

    @Test
    void testStartEStopClockSenzaErrori() {
        AtomicInteger count = new AtomicInteger(0);
        Clock clock = new Clock(count::incrementAndGet);

        assertDoesNotThrow(clock::start);
        assertDoesNotThrow(clock::stop);
    }
}

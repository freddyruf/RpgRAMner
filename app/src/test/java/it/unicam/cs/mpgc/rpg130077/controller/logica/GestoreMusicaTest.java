package it.unicam.cs.mpgc.rpg130077.controller.logica;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test per la classe {@link GestoreMusica}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class GestoreMusicaTest {

    private GestoreMusica gestoreMusica;

    @BeforeEach
    void setUp() {
        gestoreMusica = new GestoreMusica();
    }

    @AfterEach
    void tearDown() {
        if (gestoreMusica != null) {
            gestoreMusica.stop();
        }
    }

    @Test
    void testInizializzazione() {
        assertNotNull(gestoreMusica);
    }

    @Test
    void testStopSenzaAvvioNonLanciaEccezioni() {
        assertDoesNotThrow(() -> gestoreMusica.stop());
    }

    @Test
    void testAvviaMusicaEStop() {
        assertDoesNotThrow(() -> {
            gestoreMusica.avviaMusicaSemplice();
            Thread.sleep(50);
            gestoreMusica.stop();
        });
    }

    @Test
    void testDoppioAvvioIgnoraSecondoAvvio() {
        assertDoesNotThrow(() -> {
            gestoreMusica.avviaMusicaSemplice();
            gestoreMusica.avviaMusicaSemplice();
            Thread.sleep(50);
            gestoreMusica.stop();
        });
    }

    @Test
    void testDoppioStopIdempotente() {
        assertDoesNotThrow(() -> {
            gestoreMusica.avviaMusicaSemplice();
            gestoreMusica.stop();
            gestoreMusica.stop();
        });
    }
}

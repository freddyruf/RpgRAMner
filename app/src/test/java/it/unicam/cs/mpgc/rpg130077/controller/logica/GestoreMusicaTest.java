package it.unicam.cs.mpgc.rpg130077.controller.logica;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di unità per la classe {@link GestoreMusica}.
 * Verifica l'inizializzazione, la gestione dell'avvio/arresto del thread daemon di riproduzione,
 * l'idempotenza delle chiamate e il teardown sicuro delle risorse audio.
 */
class GestoreMusicaTest {

    private GestoreMusica gestoreMusica;

    @BeforeEach
    void setUp() {
        gestoreMusica = new GestoreMusica();
    }

    @AfterEach
    void tearDown() {
        // Garantisce il teardown sicuro e la terminazione dei thread in background
        if (gestoreMusica != null) {
            gestoreMusica.stop();
        }
    }

    @Test
    @DisplayName("Inizializzazione corretta dell'istanza GestoreMusica")
    void testInizializzazione() {
        assertNotNull(gestoreMusica);
    }

    @Test
    @DisplayName("Invocare stop() senza previa chiamata ad avviaMusicaSemplice() non deve sollevare eccezioni")
    void testStopSenzaAvvioNonLanciaEccezioni() {
        assertDoesNotThrow(() -> gestoreMusica.stop());
    }

    @Test
    @DisplayName("Avvio della riproduzione e successivo stop si completano senza errori")
    void testAvviaMusicaEStop() {
        assertDoesNotThrow(() -> {
            gestoreMusica.avviaMusicaSemplice();
            gestoreMusica.stop();
        });
    }

    @Test
    @DisplayName("Doppio avvio consecutivo è idempotente e ignora la seconda chiamata")
    void testDoppioAvvioIgnoraSecondoAvvio() {
        assertDoesNotThrow(() -> {
            gestoreMusica.avviaMusicaSemplice();
            gestoreMusica.avviaMusicaSemplice();
            gestoreMusica.stop();
        });
    }

    @Test
    @DisplayName("Doppio stop consecutivo è idempotente")
    void testDoppioStopIdempotente() {
        assertDoesNotThrow(() -> {
            gestoreMusica.avviaMusicaSemplice();
            gestoreMusica.stop();
            gestoreMusica.stop();
        });
    }

    @Test
    @DisplayName("Riavvio della musica (avvio, stop e nuovo avvio) si completa correttamente")
    void testRiavvioMusicaDopoStop() {
        assertDoesNotThrow(() -> {
            gestoreMusica.avviaMusicaSemplice();
            gestoreMusica.stop();
            gestoreMusica.avviaMusicaSemplice();
            gestoreMusica.stop();
        });
    }

    @Test
    @DisplayName("Verifica che il thread audio venga istanziato come daemon con nome corretto")
    void testThreadBackgroundDaemonENome() {
        gestoreMusica.avviaMusicaSemplice();

        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        boolean foundAudioThread = threadSet.stream().anyMatch(t ->
                "BackgroundAudio-Thread".equals(t.getName()) && t.isDaemon()
        );

        gestoreMusica.stop();
        assertTrue(foundAudioThread, "Deve essere stato istanziato un thread daemon denominato 'BackgroundAudio-Thread'");
    }
}

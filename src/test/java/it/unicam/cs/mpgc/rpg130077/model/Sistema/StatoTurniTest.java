package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link StatoTurni}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class StatoTurniTest {

    @Test
    void testInizializzazioneStatoTurniParteDaZero() {
        StatoTurni turni = new StatoTurni(1, 1);
        assertEquals(0, turni.getTurno());
    }

    @Test
    void testAvanzaTurnoIncrementaTurnoCorrettamente() {
        StatoTurni turni = new StatoTurni(1, 1);
        turni.avanzaTurno();
        assertEquals(1, turni.getTurno());
    }

    @Test
    void testTurnoCiclicoConModulo() {
        // 1 alleato, 1 nemico -> totale 2 entità
        StatoTurni turni = new StatoTurni(1, 1);

        assertEquals(0, turni.getTurno());
        turni.avanzaTurno();
        assertEquals(1, turni.getTurno());
        turni.avanzaTurno();
        // 2 % 2 = 0
        assertEquals(0, turni.getTurno());
        turni.avanzaTurno();
        // 3 % 2 = 1
        assertEquals(1, turni.getTurno());
    }

    @Test
    void testCostruttoreDiCopiaPreservaStato() {
        StatoTurni originale = new StatoTurni(2, 2);
        originale.avanzaTurno();
        originale.avanzaTurno();

        StatoTurni copia = new StatoTurni(originale);
        assertEquals(originale.getTurno(), copia.getTurno());
    }

    @Test
    void testIndipendenzaDellaCopia() {
        StatoTurni originale = new StatoTurni(1, 1);
        StatoTurni copia = new StatoTurni(originale);

        originale.avanzaTurno();
        assertEquals(1, originale.getTurno());
        assertEquals(0, copia.getTurno());
    }
}

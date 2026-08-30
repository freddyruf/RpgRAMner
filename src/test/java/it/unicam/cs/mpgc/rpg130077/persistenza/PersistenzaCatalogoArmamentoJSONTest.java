package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoSort;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link PersistenzaCatalogoArmamentoJSON}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class PersistenzaCatalogoArmamentoJSONTest {

    private PersistenzaCatalogoArmamentoJSON persistenza;

    @BeforeEach
    void setUp() {
        persistenza = new PersistenzaCatalogoArmamentoJSON();
    }

    @Test
    void testCaricamentoCatalogoArmiDaResourcesNonVuoto() {
        ArrayList<Arma> armi = persistenza.caricamentoCatalogoArmi();

        assertNotNull(armi);
        assertFalse(armi.isEmpty());
        assertTrue(armi.size() >= 2);
    }

    @Test
    void testCaricamentoCatalogoArmiControllaTipiEValori() {
        ArrayList<Arma> armi = persistenza.caricamentoCatalogoArmi();

        boolean trovataPistola = armi.stream().anyMatch(a -> a instanceof Pistola);
        boolean trovataMitragliatrice = armi.stream().anyMatch(a -> a instanceof Mitragliatrice);

        assertTrue(trovataPistola);
        assertTrue(trovataMitragliatrice);
    }

    @Test
    void testCaricamentoCatalogoHackDaResourcesNonVuoto() {
        ArrayList<Hack> hacks = persistenza.caricamentoCatalogoHacks();

        assertNotNull(hacks);
        assertFalse(hacks.isEmpty());
        assertEquals(5, hacks.size());
    }

    @Test
    void testCaricamentoCatalogoHackVerificaEffettiNidificati() {
        ArrayList<Hack> hacks = persistenza.caricamentoCatalogoHacks();

        boolean trovatoDanno = false;
        boolean trovataCura = false;
        boolean trovatoReverse = false;
        boolean trovatoSort = false;

        for (Hack h : hacks) {
            for (var eff : h.getEffetti()) {
                if (eff instanceof EffettoDanno) trovatoDanno = true;
                if (eff instanceof EffettoCura) trovataCura = true;
                if (eff instanceof EffettoReverse) trovatoReverse = true;
                if (eff instanceof EffettoSort) trovatoSort = true;
            }
        }

        assertTrue(trovatoDanno);
        assertTrue(trovataCura);
        assertTrue(trovatoReverse);
        assertTrue(trovatoSort);
    }
}

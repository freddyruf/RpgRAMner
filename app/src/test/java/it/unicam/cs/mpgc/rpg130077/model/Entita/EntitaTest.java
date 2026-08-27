package it.unicam.cs.mpgc.rpg130077.model.Entita;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link Entita}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class EntitaTest {

    private static class EntitaDiTest extends Entita {
        EntitaDiTest(String nome, int MaxPV, String image, int spazioRAM, ArrayList<Hack> hacks, Arma arma, boolean fazione) {
            super(nome, MaxPV, image, spazioRAM, hacks, arma, fazione);
        }

        EntitaDiTest(Entita entita) {
            super(entita);
        }

        @Override
        public Entita copy() {
            return new EntitaDiTest(this);
        }
    }

    private Arma creaArma() {
        return new Pistola("PistolaTest", "Descrizione pistola", 6, 15, 0.2);
    }

    private ArrayList<Hack> creaListaHacks() {
        return new ArrayList<>();
    }

    @Test
    void costruttoreInizializzaCampiECuraAlMassimo() {
        Arma arma = creaArma();
        ArrayList<Hack> hacks = creaListaHacks();
        Entita entita = new EntitaDiTest("Eroe", 100, "eroe.png", 8, hacks, arma, true);

        assertEquals("Eroe", entita.getNome());
        assertEquals(100, entita.getMaxPV());
        assertEquals(100, entita.getPV());
        assertEquals("eroe.png", entita.getImage());
        assertEquals(8, entita.getSpazioRAM());
        assertSame(hacks, entita.getHacks());
        assertSame(arma, entita.getArma());
    }

    @Test
    void costruttoreLanciaNullPointerExceptionSeParametriNulli() {
        Arma arma = creaArma();
        ArrayList<Hack> hacks = creaListaHacks();

        assertThrows(NullPointerException.class, () -> new EntitaDiTest(null, 100, "img.png", 8, hacks, arma, true));
        assertThrows(NullPointerException.class, () -> new EntitaDiTest("Eroe", 100, null, 8, hacks, arma, true));
        assertThrows(NullPointerException.class, () -> new EntitaDiTest("Eroe", 100, "img.png", 8, null, arma, true));
        assertThrows(NullPointerException.class, () -> new EntitaDiTest("Eroe", 100, "img.png", 8, hacks, null, true));
    }

    @Test
    void setPVModificaPuntiVitaNelRangeValido() {
        Entita entita = new EntitaDiTest("Eroe", 100, "img.png", 8, creaListaHacks(), creaArma(), true);

        entita.setPV(50);
        assertEquals(50, entita.getPV());
    }

    @Test
    void setPVCappaAlValoreMaxPVSeSuperato() {
        Entita entita = new EntitaDiTest("Eroe", 100, "img.png", 8, creaListaHacks(), creaArma(), true);

        entita.setPV(150);
        assertEquals(100, entita.getPV());
    }

    @Test
    void setPVValoriNegativiOZero() {
        Entita entita = new EntitaDiTest("Eroe", 100, "img.png", 8, creaListaHacks(), creaArma(), true);

        entita.setPV(0);
        assertEquals(0, entita.getPV());

        entita.setPV(-20);
        assertEquals(0, entita.getPV());
    }

    @Test
    void compareFazioneTrue(){
        Entita entita1 = new EntitaDiTest("Eroe", 100, "img.png", 8, creaListaHacks(), creaArma(), true);
        Entita entita2 = new EntitaDiTest("Eroe", 100, "img.png", 8, creaListaHacks(), creaArma(), true);
        assertTrue(entita1.compareFazione(entita2));
    }

    @Test
    void compareFazioneFalse(){
        Entita entita1 = new EntitaDiTest("Eroe", 100, "img.png", 8, creaListaHacks(), creaArma(), true);
        Entita entita2 = new EntitaDiTest("Eroe", 100, "img.png", 8, creaListaHacks(), creaArma(), false);
        assertFalse(entita1.compareFazione(entita2));
    }
}

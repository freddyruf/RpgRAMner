package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link EffettoSort}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class EffettoSortTest {

    private RAM ram;
    private StatoBattaglia stato;
    private Entita lanciatore;
    private Entita bersaglio;

    private static class FakeStatoBattaglia implements StatoBattaglia {
        private final RAM ram;
        FakeStatoBattaglia(RAM ram) { this.ram = ram; }
        @Override public RAM getRamCondivisa() { return ram; }
        @Override public Giocatore getGiocatore() { return null; }
        @Override public ArrayList<Entita> getFazioneEroi() { return new ArrayList<>(); }
        @Override public ArrayList<Entita> getFazioneNemici() { return new ArrayList<>(); }
        @Override public Entita getEroe(int n) { return null; }
        @Override public Entita getNemico(int n) { return null; }
        @Override public StatoBattaglia copy() { return this; }
    }

    @BeforeEach
    void setUp() {
        ram = new RAM(20);
        stato = new FakeStatoBattaglia(ram);
        lanciatore = new Giocatore("Hero", 100, "hero.png", 8, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), true);
        bersaglio = new Giocatore("Enemy", 100, "enemy.png", 8, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), false);
    }

    @Test
    void costruttoreEIsConclusive() {
        EffettoSort effettoConclusivo = new EffettoSort(true);
        EffettoSort effettoNonConclusivo = new EffettoSort(false);

        assertTrue(effettoConclusivo.isConclusive());
        assertFalse(effettoNonConclusivo.isConclusive());
    }

    @Test
    void eseguiEffettoOrdinaHackPerThickInCodaCrescente() {
        Hack hack8 = new Hack("Hack8", "Desc", 8);
        Hack hack2 = new Hack("Hack2", "Desc", 2);
        Hack hack5 = new Hack("Hack5", "Desc", 5);
        ram.inserisci(hack8, bersaglio, lanciatore);
        ram.inserisci(hack2, bersaglio, lanciatore);
        ram.inserisci(hack5, bersaglio, lanciatore);

        EffettoSort effetto = new EffettoSort(true);
        effetto.eseguiEffetto(stato, lanciatore, bersaglio);

        assertEquals("Hack2", ram.rimuovi().getHack().getNome());
        assertEquals("Hack5", ram.rimuovi().getHack().getNome());
        assertEquals("Hack8", ram.rimuovi().getHack().getNome());
    }

    @Test
    void copyMethodCreaNuovaIstanzaIndipendente() {
        EffettoSort originale = new EffettoSort(true);
        Effetto copia = originale.copy();

        assertTrue(copia instanceof EffettoSort);
        assertNotSame(originale, copia);
        assertEquals(originale.isConclusive(), copia.isConclusive());
    }

    @Test
    void costruttoreDiCopiaCreaCopiaIndipendente() {
        EffettoSort originale = new EffettoSort(false);
        EffettoSort copia = new EffettoSort(originale);

        assertNotSame(originale, copia);
        assertEquals(originale.isConclusive(), copia.isConclusive());
    }
}

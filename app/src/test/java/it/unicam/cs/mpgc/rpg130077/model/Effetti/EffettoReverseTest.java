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
 * Test per la classe {@link EffettoReverse}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class EffettoReverseTest {

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
        ram = new RAM(15);
        stato = new FakeStatoBattaglia(ram);
        lanciatore = new Giocatore("Hero", 100, "hero.png", 8, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), true);
        bersaglio = new Giocatore("Enemy", 100, "enemy.png", 8, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), false);
    }

    @Test
    void costruttoreEIsConclusive() {
        EffettoReverse effettoConclusivo = new EffettoReverse(true);
        EffettoReverse effettoNonConclusivo = new EffettoReverse(false);

        assertTrue(effettoConclusivo.isConclusive());
        assertFalse(effettoNonConclusivo.isConclusive());
    }

    @Test
    void eseguiEffettoInverteOrdineDelleHackNellaRAM() {
        Hack hack1 = new Hack("Hack1", "Desc", 2);
        Hack hack2 = new Hack("Hack2", "Desc", 3);
        Hack hack3 = new Hack("Hack3", "Desc", 4);
        ram.inserisci(hack1, bersaglio, lanciatore);
        ram.inserisci(hack2, bersaglio, lanciatore);
        ram.inserisci(hack3, bersaglio, lanciatore);

        EffettoReverse effetto = new EffettoReverse(true);
        effetto.eseguiEffetto(stato, lanciatore, bersaglio);

        assertEquals("Hack3", ram.rimuovi().getHack().getNome());
        assertEquals("Hack2", ram.rimuovi().getHack().getNome());
        assertEquals("Hack1", ram.rimuovi().getHack().getNome());
    }

    @Test
    void eseguiEffettoConRAMVuotaNonSollevaEccezioni() {
        EffettoReverse effetto = new EffettoReverse(true);
        assertDoesNotThrow(() -> effetto.eseguiEffetto(stato, lanciatore, bersaglio));
    }

    @Test
    void copyMethodCreaNuovaIstanzaIndipendente() {
        EffettoReverse originale = new EffettoReverse(true);
        Effetto copia = originale.copy();

        assertTrue(copia instanceof EffettoReverse);
        assertNotSame(originale, copia);
        assertEquals(originale.isConclusive(), copia.isConclusive());
    }

    @Test
    void costruttoreDiCopiaCreaCopiaIndipendente() {
        EffettoReverse originale = new EffettoReverse(false);
        EffettoReverse copia = new EffettoReverse(originale);

        assertNotSame(originale, copia);
        assertEquals(originale.isConclusive(), copia.isConclusive());
    }

    @Test
    void getEffectTypeRitornaRAM() {
        EffettoReverse effetto = new EffettoReverse(true);
        assertEquals(EffectType.RAM, effetto.getEffectType());
    }
}

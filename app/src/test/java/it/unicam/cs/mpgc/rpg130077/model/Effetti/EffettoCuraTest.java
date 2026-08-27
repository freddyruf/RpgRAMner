package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link EffettoCura}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class EffettoCuraTest {

    private Entita lanciatore;
    private Entita bersaglio;

    @BeforeEach
    void setUp() {
        lanciatore = new Giocatore("Hero", 100, "hero.png", 8, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), true);
        bersaglio = new Giocatore("Enemy", 100, "enemy.png", 8, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), false);
    }

    @Test
    void costruttoreEIsConclusive() {
        EffettoCura effettoConclusivo = new EffettoCura(30, true);
        EffettoCura effettoContinuo = new EffettoCura(5, false);

        assertTrue(effettoConclusivo.isConclusive());
        assertFalse(effettoContinuo.isConclusive());
    }

    @Test
    void eseguiEffettoAumentaPVDelLanciatore() {
        lanciatore.setPV(50);
        EffettoCura effetto = new EffettoCura(30, true);
        effetto.eseguiEffetto(null, lanciatore, bersaglio);

        // 50 + 30 = 80
        assertEquals(80, lanciatore.getPV());
    }

    @Test
    void eseguiEffettoNonSuperaMaxPVDelLanciatore() {
        lanciatore.setPV(90);
        EffettoCura effetto = new EffettoCura(25, true);
        effetto.eseguiEffetto(null, lanciatore, bersaglio);

        // 90 + 25 = 115 -> cappa a 100 (MaxPV)
        assertEquals(100, lanciatore.getPV());
    }

    @Test
    void eseguiEffettoNonModificaPVDelBersaglio() {
        lanciatore.setPV(50);
        EffettoCura effetto = new EffettoCura(30, true);
        effetto.eseguiEffetto(null, lanciatore, bersaglio);

        assertEquals(100, bersaglio.getPV());
    }

    @Test
    void copyMethodCreaNuovaIstanzaIndipendente() {
        EffettoCura originale = new EffettoCura(30, true);
        Effetto copia = originale.copy();

        assertTrue(copia instanceof EffettoCura);
        assertNotSame(originale, copia);
        assertEquals(originale.isConclusive(), copia.isConclusive());
    }

    @Test
    void costruttoreDiCopiaCreaCopiaIndipendente() {
        EffettoCura originale = new EffettoCura(20, false);
        EffettoCura copia = new EffettoCura(originale);

        assertNotSame(originale, copia);
        assertEquals(originale.isConclusive(), copia.isConclusive());
    }
}

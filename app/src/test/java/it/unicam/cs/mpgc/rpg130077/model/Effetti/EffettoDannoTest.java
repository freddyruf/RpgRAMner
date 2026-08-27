package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link EffettoDanno}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class EffettoDannoTest {

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
        EffettoDanno effettoConclusivo = new EffettoDanno(35, true);
        EffettoDanno effettoContinuo = new EffettoDanno(10, false);

        assertTrue(effettoConclusivo.isConclusive());
        assertFalse(effettoContinuo.isConclusive());
    }

    @Test
    void eseguiEffettoRiducePVDelBersaglio() {
        EffettoDanno effetto = new EffettoDanno(35, true);
        effetto.eseguiEffetto(null, lanciatore, bersaglio);

        // 100 - 35 = 65
        assertEquals(65, bersaglio.getPV());
    }

    @Test
    void eseguiEffettoNonModificaPVDelLanciatore() {
        EffettoDanno effetto = new EffettoDanno(35, true);
        effetto.eseguiEffetto(null, lanciatore, bersaglio);

        assertEquals(100, lanciatore.getPV());
    }

    @Test
    void copyMethodCreaNuovaIstanzaIndipendente() {
        EffettoDanno originale = new EffettoDanno(40, true);
        Effetto copia = originale.copy();

        assertTrue(copia instanceof EffettoDanno);
        assertNotSame(originale, copia);
        assertEquals(originale.isConclusive(), copia.isConclusive());
    }

    @Test
    void costruttoreDiCopiaCreaCopiaIndipendente() {
        EffettoDanno originale = new EffettoDanno(25, false);
        EffettoDanno copia = new EffettoDanno(originale);

        assertNotSame(originale, copia);
        assertEquals(originale.isConclusive(), copia.isConclusive());
    }
}

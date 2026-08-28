package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link AzioneSparo}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class AzioneSparoTest {

    private Entita lanciatore;
    private Entita bersaglio;

    @BeforeEach
    void setUp() {
        Arma arma = new Pistola("PistolaTest", "Desc", 6, 30, 0.0);
        lanciatore = new Giocatore("Hero", 100, "hero.png", 8, new ArrayList<>(), arma, true);
        bersaglio = new Giocatore("Enemy", 100, "enemy.png", 8, new ArrayList<>(), arma, false);
    }

    @Test
    void isDamageDealerRitornaTrue() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        assertTrue(azione.isDamageDealer());
    }

    @Test
    void isHealDealerRitornaFalse() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        assertFalse(azione.isHealDealer());
    }

    @Test
    void eseguiRiduceIPVDelBersaglio() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        azione.esegui(null);

        // 100 - 30 = 70
        assertEquals(70, bersaglio.getPv());
    }

    @Test
    void eseguiConArmaCriticaInfliggeDannoRaddoppiato() {
        Arma armaCritica = new Pistola("PistolaCritica", "Desc", 6, 20, 1.0);
        Entita lanciatoreCritico = new Giocatore("HeroCrit", 100, "hero.png", 8, new ArrayList<>(), armaCritica, true);

        AzioneSparo azione = new AzioneSparo(lanciatoreCritico, bersaglio);
        azione.esegui(null);

        // 100 - (20 * 2) = 60
        assertEquals(60, bersaglio.getPv());
    }

    @Test
    void eseguiNonRichiedeStatoBattagliaNonNullo() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        assertDoesNotThrow(() -> azione.esegui(null));
    }
}

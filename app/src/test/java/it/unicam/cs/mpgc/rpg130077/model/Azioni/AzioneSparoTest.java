package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test completi per la classe {@link AzioneSparo}.
 * Copre la verifica degli EffectType, calcolo del danno (normale e critico),
 * esecuzione e validazione dei parametri nulli.
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
    @DisplayName("getEffectTypes restituisce esattamente Set.of(EffectType.DAMAGE)")
    void testEffectTypesContieneDamage() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        Set<EffectType> types = azione.getEffectTypes();

        assertNotNull(types);
        assertEquals(Set.of(EffectType.DAMAGE), types);
        assertTrue(types.contains(EffectType.DAMAGE));
        assertFalse(types.contains(EffectType.HEAL));
        assertFalse(types.contains(EffectType.RAM));
    }

    @Test
    @DisplayName("esegui riduce i punti vita (PV) del bersaglio in base al danno dell'arma")
    void testEseguiRiduceIPVDelBersaglio() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        azione.esegui(null);

        // 100 - 30 = 70
        assertEquals(70, bersaglio.getPv());
    }

    @Test
    @DisplayName("esegui con arma a colpo critico (chance 1.0) infligge danno raddoppiato")
    void testEseguiConArmaCriticaInfliggeDannoRaddoppiato() {
        Arma armaCritica = new Pistola("PistolaCritica", "Desc", 6, 20, 1.0);
        Entita lanciatoreCritico = new Giocatore("HeroCrit", 100, "hero.png", 8, new ArrayList<>(), armaCritica, true);

        AzioneSparo azione = new AzioneSparo(lanciatoreCritico, bersaglio);
        azione.esegui(null);

        // 100 - (20 * 2) = 60
        assertEquals(60, bersaglio.getPv());
    }

    @Test
    @DisplayName("esegui con lanciatore null lancia NullPointerException")
    void testEseguiConLanciatoreNullLanciaNPE() {
        AzioneSparo azione = new AzioneSparo(null, bersaglio);
        NullPointerException ex = assertThrows(NullPointerException.class, () -> azione.esegui(null));
        assertTrue(ex.getMessage().contains("Lanciatore o Bersaglio nullo"));
    }

    @Test
    @DisplayName("esegui con bersaglio null lancia NullPointerException")
    void testEseguiConBersaglioNullLanciaNPE() {
        AzioneSparo azione = new AzioneSparo(lanciatore, null);
        NullPointerException ex = assertThrows(NullPointerException.class, () -> azione.esegui(null));
        assertTrue(ex.getMessage().contains("Lanciatore o Bersaglio nullo"));
    }

    @Test
    @DisplayName("esegui con entrambi i parametri null lancia NullPointerException")
    void testEseguiConEntrambiNullLanciaNPE() {
        AzioneSparo azione = new AzioneSparo(null, null);
        assertThrows(NullPointerException.class, () -> azione.esegui(null));
    }

    @Test
    @DisplayName("esegui accetta StatoBattaglia null senza errori quando lanciatore e bersaglio sono validi")
    void testEseguiNonRichiedeStatoBattagliaNonNullo() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        assertDoesNotThrow(() -> azione.esegui(null));
    }
}

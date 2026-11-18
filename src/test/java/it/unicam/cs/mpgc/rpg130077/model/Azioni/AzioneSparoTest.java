package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("getEffectTypes restituisce una lista immutabile con EffectType.DAMAGE")
    void testEffectTypesContieneDamage() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        ArrayList<EffectType> types = azione.getEffectTypes();

        assertNotNull(types);
        assertEquals(new ArrayList<>(java.util.Arrays.asList(EffectType.DAMAGE)), types);
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
    @DisplayName("arma con effetto cura applica prima il danno diretto e poi cura il lanciatore")
    void testArmaConEffettoCura() {
        Arma armaCurativa = new Pistola("PistolaCura", "Desc", 6, 30, 0.0);
        armaCurativa.addEffetto(new EffettoCura(25, true));
        Entita lanciatoreCurativo = new Giocatore("Healer", 100, "", 8,
                new ArrayList<>(), armaCurativa, true);
        lanciatoreCurativo.setPv(50);

        AzioneSparo azione = new AzioneSparo(lanciatoreCurativo, lanciatoreCurativo);
        azione.esegui(null);

        assertEquals(45, lanciatoreCurativo.getPv());
    }

    @Test
    @DisplayName("arma con effetto danno espone DAMAGE ed esegue l'effetto")
    void testArmaConEffettoDanno() {
        Arma armaConEffetto = new Pistola("PistolaEffetto", "Desc", 6, 30, 0.0);
        armaConEffetto.addEffetto(new EffettoDanno(40, true));
        Entita lanciatoreEffetto = new Giocatore("Caster", 100, "", 8,
                new ArrayList<>(), armaConEffetto, true);

        AzioneSparo azione = new AzioneSparo(lanciatoreEffetto, bersaglio);
        azione.esegui(null);

        assertTrue(azione.getEffectTypes().contains(EffectType.DAMAGE));
        assertEquals(30, bersaglio.getPv());
    }

    @Test
    @DisplayName("esegui accetta StatoBattaglia null senza errori quando lanciatore e bersaglio sono validi")
    void testEseguiNonRichiedeStatoBattagliaNonNullo() {
        AzioneSparo azione = new AzioneSparo(lanciatore, bersaglio);
        assertDoesNotThrow(() -> azione.esegui(null));
    }
}

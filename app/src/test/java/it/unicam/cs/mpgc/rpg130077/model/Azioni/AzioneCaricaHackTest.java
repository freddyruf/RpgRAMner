package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoSort;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test completi per la classe {@link AzioneCaricaHack}.
 * Copre la validazione dei parametri del costruttore (NPE), la coerenza degli EffectType,
 * l'inserimento dell'hack in RAM e la gestione dei limiti di capienza.
 */
class AzioneCaricaHackTest {

    private Entita lanciatore;
    private Entita bersaglio;
    private StatoBattaglia stato;

    @BeforeEach
    void setUp() {
        lanciatore = new Giocatore("Hero", 100, "hero.png", 5, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), true);
        bersaglio = new NPC("Enemy", 100, "enemy.png", 5, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), 10, 0.0, (n, s) -> null, false);
        stato = new StatoBattaglia1v1((Giocatore) lanciatore, (NPC) bersaglio);
    }

    @Test
    @DisplayName("Costruttore e getHack restituiscono l'hack corretto")
    void testCostruttoreEGetterHack() {
        Hack hack = new Hack("Fireball", "Danno", 4);
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        assertSame(hack, azione.getHack());
    }

    @Test
    @DisplayName("Costruttore lancia NullPointerException se uno dei parametri e null")
    void testCostruttoreNullChecks() {
        Hack hack = new Hack("TestHack", "Desc", 3);

        NullPointerException ex1 = assertThrows(NullPointerException.class, () ->
                new AzioneCaricaHack(null, bersaglio, hack));
        assertTrue(ex1.getMessage().contains("non possono essere nulli"));

        NullPointerException ex2 = assertThrows(NullPointerException.class, () ->
                new AzioneCaricaHack(lanciatore, null, hack));
        assertTrue(ex2.getMessage().contains("non possono essere nulli"));

        NullPointerException ex3 = assertThrows(NullPointerException.class, () ->
                new AzioneCaricaHack(lanciatore, bersaglio, null));
        assertTrue(ex3.getMessage().contains("non possono essere nulli"));

        assertThrows(NullPointerException.class, () ->
                new AzioneCaricaHack(null, null, null));
    }

    @Test
    @DisplayName("getEffectTypes contiene DAMAGE se l'hack contiene EffettoDanno")
    void testEffectTypeRestituisceDannoSeHackHaEffettoDanno() {
        Hack hack = new Hack("Fireball", "Danno", 4);
        hack.addEffetto(new EffettoDanno(50, true));
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        Set<EffectType> types = azione.getEffectTypes();
        assertTrue(types.contains(EffectType.DAMAGE));
        assertFalse(types.contains(EffectType.HEAL));
        assertFalse(types.contains(EffectType.RAM));
    }

    @Test
    @DisplayName("getEffectTypes contiene HEAL se l'hack contiene EffettoCura")
    void testEffectTypeRestituisceCuraSeHackHaEffettoCura() {
        Hack hack = new Hack("Firewall", "Cura", 4);
        hack.addEffetto(new EffettoCura(30, true));
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        Set<EffectType> types = azione.getEffectTypes();
        assertTrue(types.contains(EffectType.HEAL));
        assertFalse(types.contains(EffectType.DAMAGE));
        assertFalse(types.contains(EffectType.RAM));
    }

    @Test
    @DisplayName("getEffectTypes contiene RAM per effetti di manipolazione RAM come Reverse e Sort")
    void testEffectTypeRestituisceRAMPerHackDiManipolazione() {
        Hack hackReverse = new Hack("RAM:Reverse", "Inversione", 3);
        hackReverse.addEffetto(new EffettoReverse(true));
        AzioneCaricaHack azioneRev = new AzioneCaricaHack(lanciatore, bersaglio, hackReverse);

        assertTrue(azioneRev.getEffectTypes().contains(EffectType.RAM));
        assertFalse(azioneRev.getEffectTypes().contains(EffectType.DAMAGE));
        assertFalse(azioneRev.getEffectTypes().contains(EffectType.HEAL));

        Hack hackSort = new Hack("RAM:Sort", "Ordinamento", 3);
        hackSort.addEffetto(new EffettoSort(true));
        AzioneCaricaHack azioneSort = new AzioneCaricaHack(lanciatore, bersaglio, hackSort);

        assertTrue(azioneSort.getEffectTypes().contains(EffectType.RAM));
    }

    @Test
    @DisplayName("getEffectTypes supporta combinazioni multiple di effetti (DAMAGE + HEAL + RAM)")
    void testEffectTypeConMoltepliciEffetti() {
        Hack hackMultiplo = new Hack("ComboOverload", "Multi", 5);
        hackMultiplo.addEffetto(new EffettoDanno(20, false));
        hackMultiplo.addEffetto(new EffettoCura(15, true));
        hackMultiplo.addEffetto(new EffettoReverse(true));

        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hackMultiplo);
        Set<EffectType> types = azione.getEffectTypes();

        assertEquals(Set.of(EffectType.DAMAGE, EffectType.HEAL, EffectType.RAM), types);
    }

    @Test
    @DisplayName("getEffectTypes restituisce un set vuoto per hack senza effetti")
    void testEffectTypeVuotoPerHackSenzaEffetti() {
        Hack hackVuoto = new Hack("EmptyHack", "Nessun effetto", 2);
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hackVuoto);

        assertTrue(azione.getEffectTypes().isEmpty());
    }

    @Test
    @DisplayName("esegui inserisce l'hack nella RAM condivisa con lanciatore e bersaglio corretti")
    void testEseguiInserisceHackNellaRAMCondivisa() {
        Hack hack = new Hack("Fireball", "Danno", 4);
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        azione.esegui(stato);

        assertEquals(1, stato.getRamCondivisa().getHacks().size());
        QueuedHack queued = stato.getRamCondivisa().visualizzaTesta();
        assertNotNull(queued);
        assertSame(hack, queued.getHack());
        assertSame(lanciatore, queued.getLanciatore());
        assertSame(bersaglio, queued.getBersaglio());
    }

    @Test
    @DisplayName("esegui con hack che supera la capienza della RAM lancia IllegalArgumentException")
    void testEseguiConRAMPienaLanciaEccezione() {
        Hack hackGrande = new Hack("MegaFireball", "Danno enorme", 15);
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hackGrande);

        assertThrows(IllegalArgumentException.class, () -> azione.esegui(stato));
    }
}

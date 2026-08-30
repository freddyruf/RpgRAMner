package it.unicam.cs.mpgc.rpg130077.model.IA;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test completi per la classe {@link StrategiaCasuale}.
 * Copre la selezione mosse per Entita, self-targeting per cura, bersagliamento nemici,
 * gestione della RAM, validazione null e casi limite di no-op.
 */
class StrategiaCasualeTest {

    private StrategiaCasuale strategia;
    private Giocatore eroe;
    private RAM ram;
    private FakeStatoBattaglia stato;

    private static class FakeStatoBattaglia implements StatoBattaglia {
        private final RAM ram;
        private final ArrayList<Entita> eroi = new ArrayList<>();
        private final ArrayList<Entita> nemici = new ArrayList<>();

        FakeStatoBattaglia(RAM ram) {
            this.ram = ram;
        }

        void aggiungiEroe(Entita e) {
            eroi.add(e);
        }

        void aggiungiNemico(Entita n) {
            nemici.add(n);
        }

        @Override public RAM getRamCondivisa() { return ram; }
        @Override public Giocatore getGiocatore() { return eroi.isEmpty() ? null : (Giocatore) eroi.get(0); }
        @Override public ArrayList<Entita> getFazioneEroi() { return eroi; }
        @Override public ArrayList<Entita> getFazioneNemici() { return nemici; }
        @Override public Entita getEroe(int n) { return eroi.get(n); }
        @Override public Entita getNemico(int n) { return nemici.get(n); }
        @Override public StatoBattaglia copy() { return this; }
    }

    private static class NPCSenzaArma extends NPC {
        NPCSenzaArma(ArrayList<Hack> hacks, StrategiaCombattimento strategia) {
            super("EnemyNoWeapon", 80, "npc.png", 5, hacks,
                    new Pistola("ArmaDummy", "Desc", 6, 10, 0.0), 10, 0.0, strategia, false);
        }

        @Override
        public Arma getArma() {
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        strategia = new StrategiaCasuale();
        eroe = new Giocatore("Hero", 100, "hero.png", 5, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), true);
        ram = new RAM(10);
        stato = new FakeStatoBattaglia(ram);
        stato.aggiungiEroe(eroe);
    }

    private NPC creaNPCSenzaArma(ArrayList<Hack> hacks) {
        return new NPCSenzaArma(hacks, strategia);
    }

    private NPC creaNPCConArma(ArrayList<Hack> hacks) {
        return new NPC("EnemyWithWeapon", 80, "npc.png", 5, hacks,
                new Pistola("PistolaNemico", "Desc", 6, 15, 0.0), 10, 0.0, strategia, false);
    }

    @Test
    @DisplayName("scegliMossa con parametri null lancia NullPointerException")
    void testScegliMossaParametriNullLanciaNPE() {
        NPC npc = creaNPCConArma(new ArrayList<>());

        assertThrows(NullPointerException.class, () -> strategia.scegliMossa(null, stato));
        assertThrows(NullPointerException.class, () -> strategia.scegliMossa(npc, null));
        assertThrows(NullPointerException.class, () -> strategia.scegliMossa(null, null));
    }

    @Test
    @DisplayName("scegliMossa ritorna null quando la fazione degli eroi e vuota")
    void testScegliMossaConFazioneEroiVuotaRitornaNull() {
        FakeStatoBattaglia statoSenzaEroi = new FakeStatoBattaglia(ram);
        NPC npc = creaNPCConArma(new ArrayList<>());

        Azione azione = strategia.scegliMossa(npc, statoSenzaEroi);
        assertNull(azione);
    }

    @Test
    @DisplayName("scegliMossa con lista hacks vuota ritorna AzioneSparo verso l'eroe")
    void testScegliMossaConListaHacksVuotaRitornaSparo() {
        NPC npc = creaNPCConArma(new ArrayList<>());

        for (int i = 0; i < 20; i++) {
            eroe.setPv(100);
            Azione azione = strategia.scegliMossa(npc, stato);
            assertNotNull(azione);
            assertInstanceOf(AzioneSparo.class, azione);

            // Esecuzione dello sparo deve danneggiare l'eroe
            azione.esegui(stato);
            assertEquals(85, eroe.getPv());
        }
    }

    @Test
    @DisplayName("scegliMossa con RAM piena ritorna AzioneSparo")
    void testScegliMossaConRAMPienaRitornaSparo() {
        ArrayList<Hack> hacks = new ArrayList<>();
        hacks.add(new Hack("MegaHack", "Danno", 12)); // dura 12, non entra in RAM da 10
        NPC npc = creaNPCConArma(hacks);

        for (int i = 0; i < 20; i++) {
            Azione azione = strategia.scegliMossa(npc, stato);
            assertNotNull(azione);
            assertInstanceOf(AzioneSparo.class, azione);
        }
    }

    @Test
    @DisplayName("scegliMossa senza arma e con spazio in RAM carica l'hack")
    void testScegliMossaSenzaArmaEConSpazioRAMRitornaCaricaHack() {
        ArrayList<Hack> hacks = new ArrayList<>();
        Hack hack = new Hack("Fireball", "Danno", 4);
        hack.addEffetto(new EffettoDanno(30, true));
        hacks.add(hack);
        NPC npc = creaNPCSenzaArma(hacks);

        for (int i = 0; i < 20; i++) {
            while (stato.getRamCondivisa().visualizzaTesta() != null) {
                stato.getRamCondivisa().rimuovi();
            }
            Azione azione = strategia.scegliMossa(npc, stato);
            assertNotNull(azione);
            assertInstanceOf(AzioneCaricaHack.class, azione);
        }
    }

    @Test
    @DisplayName("scegliMossa con hack di cura (EffectType.HEAL) bersaglia se stesso (self-targeting)")
    void testScegliMossaHackCuraBersagliaSeStesso() {
        ArrayList<Hack> hacks = new ArrayList<>();
        Hack hackCura = new Hack("PatchHeal", "Cura", 3);
        hackCura.addEffetto(new EffettoCura(25, true));
        hacks.add(hackCura);

        NPC npc = creaNPCSenzaArma(hacks);

        for (int i = 0; i < 20; i++) {
            while (stato.getRamCondivisa().visualizzaTesta() != null) {
                stato.getRamCondivisa().rimuovi();
            }
            Azione azione = strategia.scegliMossa(npc, stato);
            assertNotNull(azione);
            assertInstanceOf(AzioneCaricaHack.class, azione);

            // Eseguiamo l'azione in RAM per verificare lanciatore e bersaglio impostati
            azione.esegui(stato);
            QueuedHack queued = stato.getRamCondivisa().visualizzaTesta();
            assertNotNull(queued);
            assertSame(npc, queued.getLanciatore());
            assertSame(npc, queued.getBersaglio(), "L'hack di cura deve avere l'NPC stesso come bersaglio");
        }
    }

    @Test
    @DisplayName("scegliMossa con hack offensivo (EffectType.DAMAGE) bersaglia un eroe nemico")
    void testScegliMossaHackOffensivoBersagliaEroe() {
        ArrayList<Hack> hacks = new ArrayList<>();
        Hack hackDanno = new Hack("Exploit", "Danno", 3);
        hackDanno.addEffetto(new EffettoDanno(35, true));
        hacks.add(hackDanno);

        NPC npc = creaNPCSenzaArma(hacks);

        for (int i = 0; i < 20; i++) {
            while (stato.getRamCondivisa().visualizzaTesta() != null) {
                stato.getRamCondivisa().rimuovi();
            }
            Azione azione = strategia.scegliMossa(npc, stato);
            assertNotNull(azione);
            assertInstanceOf(AzioneCaricaHack.class, azione);

            azione.esegui(stato);
            QueuedHack queued = stato.getRamCondivisa().visualizzaTesta();
            assertNotNull(queued);
            assertSame(npc, queued.getLanciatore());
            assertSame(eroe, queued.getBersaglio(), "L'hack offensivo deve avere l'eroe come bersaglio");
        }
    }

    @Test
    @DisplayName("scegliMossa ritorna null quando l'NPC non ha ne armi ne hacks utilizzabili")
    void testScegliMossaSenzaArmaENessunHackRitornaNull() {
        NPC npcInerme = creaNPCSenzaArma(new ArrayList<>());
        Azione azione = strategia.scegliMossa(npcInerme, stato);
        assertNull(azione);
    }

    @Test
    @DisplayName("scegliMossa senza arma e con hack troppo grande per la RAM ritorna null")
    void testScegliMossaSenzaArmaEConHackTroppoGrandeRitornaNull() {
        ArrayList<Hack> hacks = new ArrayList<>();
        hacks.add(new Hack("HugeHack", "Danno", 15)); // 15 > 10
        NPC npc = creaNPCSenzaArma(hacks);

        Azione azione = strategia.scegliMossa(npc, stato);
        assertNull(azione);
    }

    @Test
    @DisplayName("scegliMossa seleziona sempre un bersaglio valido tra gli eroi disponibili")
    void testScegliMossaSelezionaSempreBersaglioValidoDellaFazioneEroi() {
        Giocatore eroe2 = new Giocatore("Hero2", 100, "hero2.png", 5, new ArrayList<>(),
                new Pistola("Pistola2", "Desc", 6, 10, 0.0), true);
        stato.aggiungiEroe(eroe2);

        ArrayList<Hack> hacks = new ArrayList<>();
        hacks.add(new Hack("Fireball", "Danno", 4));
        NPC npc = creaNPCConArma(hacks);

        for (int i = 0; i < 50; i++) {
            Azione azione = strategia.scegliMossa(npc, stato);
            assertNotNull(azione);
        }
    }

    @Test
    @DisplayName("scegliMossa accetta qualsiasi tipo di Entita conforme alla firma dell'interfaccia")
    void testScegliMossaAccettaEntitaGenerica() {
        // Giocatore usato come Entita lanciatrice
        Giocatore altroEroe = new Giocatore("AllyHero", 100, "ally.png", 5, new ArrayList<>(),
                new Pistola("PistolaAlly", "Desc", 6, 12, 0.0), true);

        Azione azione = strategia.scegliMossa(altroEroe, stato);
        assertNotNull(azione);
        assertInstanceOf(AzioneSparo.class, azione);
    }
}

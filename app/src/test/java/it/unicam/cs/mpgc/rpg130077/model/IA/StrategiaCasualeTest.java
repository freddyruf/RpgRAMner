package it.unicam.cs.mpgc.rpg130077.model.IA;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;

import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link StrategiaCasuale}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
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

        FakeStatoBattaglia(RAM ram, Giocatore eroe) {
            this.ram = ram;
            this.eroi.add(eroe);
        }

        void aggiungiEroe(Entita e) {
            eroi.add(e);
        }

        @Override public RAM getRamCondivisa() { return ram; }
        @Override public Giocatore getGiocatore() { return (Giocatore) eroi.get(0); }
        @Override public ArrayList<Entita> getFazioneEroi() { return eroi; }
        @Override public ArrayList<Entita> getFazioneNemici() { return nemici; }
        @Override public Entita getEroe(int n) { return eroi.get(n); }
        @Override public Entita getNemico(int n) { return nemici.get(n); }
        @Override public StatoBattaglia copy() { return this; }
    }

    @BeforeEach
    void setUp() {
        strategia = new StrategiaCasuale();
        eroe = new Giocatore("Hero", 100, "hero.png", 5, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), true);
        ram = new RAM(10);
        stato = new FakeStatoBattaglia(ram, eroe);
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

    private NPC creaNPCSenzaArma(ArrayList<Hack> hacks) {
        return new NPCSenzaArma(hacks, strategia);
    }

    private NPC creaNPCConArma(ArrayList<Hack> hacks) {
        return new NPC("EnemyWithWeapon", 80, "npc.png", 5, hacks,
                new Pistola("Pistola", "Desc", 6, 10, 0.0), 10, 0.0, strategia, false);
    }

    @Test
    void scegliMossaConListaHacksVuotaRitornaSparo() {
        NPC npc = creaNPCConArma(new ArrayList<>());

        for (int i = 0; i < 20; i++) {
            Azione azione = strategia.scegliMossa(npc, stato);
            assertTrue(azione instanceof AzioneSparo);
        }
    }

    @Test
    void scegliMossaConRAMPienaRitornaSparo() {
        ArrayList<Hack> hacks = new ArrayList<>();
        hacks.add(new Hack("FireballGrande", "Danno", 12));
        NPC npc = creaNPCConArma(hacks);

        for (int i = 0; i < 20; i++) {
            Azione azione = strategia.scegliMossa(npc, stato);
            assertTrue(azione instanceof AzioneSparo);
        }
    }

    @Test
    void scegliMossaSenzaArmaEConSpazioRAMRitornaCaricaHack() {
        ArrayList<Hack> hacks = new ArrayList<>();
        hacks.add(new Hack("Fireball", "Danno", 4));
        NPC npc = creaNPCSenzaArma(hacks);

        for (int i = 0; i < 20; i++) {
            Azione azione = strategia.scegliMossa(npc, stato);
            assertTrue(azione instanceof AzioneCaricaHack);
        }
    }

    @Test
    void scegliMossaSelezionaSempreBersaglioValidoDellaFazioneEroi() {
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
    void scegliMossaNonRitornaMaiNull() {
        ArrayList<Hack> hacks = new ArrayList<>();
        hacks.add(new Hack("Fireball", "Danno", 3));
        NPC npc = creaNPCConArma(hacks);

        for (int i = 0; i < 50; i++) {
            assertNotNull(strategia.scegliMossa(npc, stato));
        }
    }
}

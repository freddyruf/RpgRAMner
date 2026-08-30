package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.*;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoTurni;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Adversarial Stress & Invariant Test Suite for Model & State Layer (Milestone 1).
 */
public class ModelAdversarialStressTest {

    @Nested
    @DisplayName("Adversarial Deep Clone & State Mutation Isolation")
    class DeepClonePreservationTests {

        @Test
        @DisplayName("Deep copy of Entita protects nested Hacks, Effects, and Weapon from external mutations")
        void testEntitaDeepCloneIsolation() {
            Hack hack1 = new Hack("Firewall", "Def", 3);
            hack1.addEffetto(new EffettoCura(15, false));
            hack1.addEffetto(new EffettoDanno(10, true));

            ArrayList<Hack> hacks = new ArrayList<>();
            hacks.add(hack1);

            Pistola pistola = new Pistola("Magnum", "Heavy", 6, 25, 0.3);
            Giocatore player = new Giocatore("Hero", 100, "hero.png", 8, hacks, pistola, true);

            // Create deep copy
            Giocatore playerCopy = new Giocatore(player);

            // Mutate original hack in original player
            player.getHacks().get(0).addEffetto(new EffettoReverse(true));
            // Mutate original hacks list
            player.getHacks().add(new Hack("ExtraHack", "Desc", 2));
            // Mutate original player PV
            player.setPv(40);

            // Verify playerCopy is completely unmutated
            assertEquals(100, playerCopy.getPv(), "Copy PV should remain 100");
            assertEquals(1, playerCopy.getHacks().size(), "Copy should only have 1 hack");
            assertEquals(2, playerCopy.getHacks().get(0).getEffetti().size(), "Copy hack should only have 2 effects");

            // Mutate copy weapon and verify original is unchanged
            assertNotSame(player.getArma(), playerCopy.getArma());
        }

        @Test
        @DisplayName("Deep copy of QueuedHack isolates inner Hack and preserves tick counter")
        void testQueuedHackDeepCloneIsolation() {
            Entita caster = new Giocatore("Caster", 80, "c.png", 6, new ArrayList<>(), new Pistola("P", "D", 6, 10, 0), true);
            Entita target = new Giocatore("Target", 80, "t.png", 6, new ArrayList<>(), new Pistola("P", "D", 6, 10, 0), false);

            Hack hack = new Hack("Overload", "Electric", 5);
            hack.addEffetto(new EffettoDanno(20, true));

            QueuedHack queuedOriginal = new QueuedHack(hack, target, caster);
            queuedOriginal.setTickInCoda(3);

            QueuedHack queuedCopy = new QueuedHack(queuedOriginal);

            // Mutate original hack
            hack.addEffetto(new EffettoCura(10, false));
            queuedOriginal.setTickInCoda(1);

            // Verify copy remains untouched
            assertEquals(3, queuedCopy.getTickInCoda());
            assertEquals(1, queuedCopy.getHack().getEffetti().size());
            assertSame(target, queuedCopy.getBersaglio());
            assertSame(caster, queuedCopy.getLanciatore());
        }

        @Test
        @DisplayName("RAM copy constructor produces independent empty queue with matching capacity")
        void testRAMCopyIndependence() {
            RAM originalRAM = new RAM(20);
            Entita dummy = new Giocatore("D", 50, "d.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 5, 0), true);
            originalRAM.inserisci(new Hack("H1", "D", 5), dummy, dummy);
            originalRAM.inserisci(new Hack("H2", "D", 10), dummy, dummy);

            assertEquals(15, originalRAM.getSpazioOccupato());

            RAM copyRAM = new RAM(originalRAM);
            assertEquals(20, copyRAM.getSpazioMassimoInSecondi());
            assertEquals(0, copyRAM.getSpazioOccupato(), "RAM copy starts with empty queue");
            assertTrue(copyRAM.getHacks().isEmpty());

            copyRAM.inserisci(new Hack("H3", "D", 8), dummy, dummy);
            assertEquals(15, originalRAM.getSpazioOccupato());
            assertEquals(8, copyRAM.getSpazioOccupato());
        }

        @Test
        @DisplayName("StatoBattaglia1v1 deep copy preserves full graph isolation")
        void testStatoBattaglia1v1Isolation() {
            Giocatore hero = new Giocatore("Hero", 100, "hero.png", 10, new ArrayList<>(), new Pistola("P", "D", 6, 10, 0), true);
            NPC enemy = new NPC("Enemy", 80, "enemy.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 10, 0), 10, 0.0, (n, s) -> null, false);
            StatoBattaglia1v1 state = new StatoBattaglia1v1(hero, enemy);

            StatoBattaglia1v1 stateCopy = new StatoBattaglia1v1(state);

            stateCopy.getGiocatore().setPv(10);
            ((NPC) stateCopy.getNemico(0)).setPv(15);

            assertEquals(100, state.getGiocatore().getPv());
            assertEquals(80, state.getNemico(0).getPv());
            assertEquals(10, stateCopy.getGiocatore().getPv());
            assertEquals(15, stateCopy.getNemico(0).getPv());
        }
    }

    @Nested
    @DisplayName("Adversarial Zero/Negative Bounds & Invariants")
    class ZeroAndNegativeBoundsTests {

        @Test
        @DisplayName("Entita setPv clamps accurately under extreme integers")
        void testEntitaPvClampingUnderExtremeValues() {
            Giocatore g = new Giocatore("Hero", 100, "h.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 10, 0), true);

            g.setPv(Integer.MAX_VALUE);
            assertEquals(100, g.getPv());

            g.setPv(101);
            assertEquals(100, g.getPv());

            g.setPv(0);
            assertEquals(0, g.getPv());

            g.setPv(-1);
            assertEquals(0, g.getPv());

            g.setPv(Integer.MIN_VALUE);
            assertEquals(0, g.getPv());
        }

        @Test
        @DisplayName("Entita with MaxPv = 0 clamps all positive and negative PV to 0")
        void testEntitaZeroMaxPv() {
            Giocatore zeroMax = new Giocatore("Zero", 0, "z.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 10, 0), true);
            assertEquals(0, zeroMax.getMaxPv());
            assertEquals(0, zeroMax.getPv());

            zeroMax.setPv(50);
            assertEquals(0, zeroMax.getPv());

            zeroMax.setPv(-50);
            assertEquals(0, zeroMax.getPv());
        }

        @Test
        @DisplayName("EffettoCura and EffettoDanno boundary limits and overkill clamping")
        void testEffectsBoundaryAndOverkill() {
            Giocatore hero = new Giocatore("Hero", 100, "h.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 10, 0), true);
            Giocatore target = new Giocatore("Target", 50, "t.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 10, 0), false);

            hero.setPv(20);
            EffettoCura massiveHeal = new EffettoCura(999999, true);
            massiveHeal.eseguiEffetto(null, hero, target);
            assertEquals(100, hero.getPv(), "Overheal clamps to MaxPV");

            EffettoDanno massiveDamage = new EffettoDanno(999999, true);
            massiveDamage.eseguiEffetto(null, hero, target);
            assertEquals(0, target.getPv(), "Overkill clamps to 0");
        }

        @Test
        @DisplayName("RAM exact capacity boundaries, overfill by 1 tick, and decrementing past zero")
        void testRAMExactBoundaries() {
            RAM ram = new RAM(10);
            Entita dummy = new Giocatore("D", 50, "d.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 5, 0), true);

            ram.inserisci(new Hack("H1", "D", 5), dummy, dummy);
            ram.inserisci(new Hack("H2", "D", 5), dummy, dummy);
            assertEquals(10, ram.getSpazioOccupato());

            // 10 + 1 = 11 > 10 throws IAE
            assertThrows(IllegalArgumentException.class, () -> ram.inserisci(new Hack("H3", "D", 1), dummy, dummy));

            // Decrement head until 0 and below
            for (int i = 0; i < 7; i++) {
                ram.decrementaTesta();
            }
            assertEquals(-2, ram.visualizzaTesta().getTickInCoda());
        }

        @Test
        @DisplayName("RAM stress reverse and sort with 1000 elements")
        void testRAMMassiveReverseAndSort() {
            RAM massiveRAM = new RAM(100000);
            Entita dummy = new Giocatore("D", 50, "d.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 5, 0), true);

            Random rng = new Random(42);
            for (int i = 0; i < 1000; i++) {
                int duration = rng.nextInt(50) + 1;
                massiveRAM.inserisci(new Hack("H" + i, "D", duration), dummy, dummy);
            }

            assertEquals(1000, massiveRAM.getHacks().size());

            // Sort
            massiveRAM.sort();
            ArrayList<QueuedHack> sorted = massiveRAM.getHacks();
            for (int i = 0; i < sorted.size() - 1; i++) {
                assertTrue(sorted.get(i).getTickInCoda() <= sorted.get(i + 1).getTickInCoda());
            }

            // Reverse
            massiveRAM.reverse();
            ArrayList<QueuedHack> reversed = massiveRAM.getHacks();
            for (int i = 0; i < reversed.size() - 1; i++) {
                assertTrue(reversed.get(i).getTickInCoda() >= reversed.get(i + 1).getTickInCoda());
            }
        }

        @Test
        @DisplayName("StatoTurni handles 0 total entities gracefully without divide-by-zero")
        void testStatoTurniZeroEntities() {
            StatoTurni turni = new StatoTurni(0, 0);
            assertEquals(0, turni.getTurno());
            turni.avanzaTurno();
            assertEquals(0, turni.getTurno());
        }

        @Test
        @DisplayName("StatoTurni stress cycling 10,000 iterations")
        void testStatoTurniStressCycles() {
            StatoTurni turni = new StatoTurni(3, 2); // 5 entities total
            for (int i = 0; i < 10000; i++) {
                assertEquals(i % 5, turni.getTurno());
                turni.avanzaTurno();
            }
        }

        @Test
        @DisplayName("Pistola and Mitragliatrice statistical damage ranges under crit chances")
        void testWeaponsDamageRanges() {
            Pistola p0 = new Pistola("P0", "D", 6, 15, 0.0);
            Pistola p1 = new Pistola("P1", "D", 6, 15, 1.0);
            Mitragliatrice m0 = new Mitragliatrice("M0", "D", 30, 10, 0.0);
            Mitragliatrice m1 = new Mitragliatrice("M1", "D", 30, 10, 1.0);

            for (int i = 0; i < 100; i++) {
                assertEquals(15, p0.calcolaDanno());
                assertEquals(30, p1.calcolaDanno());
                assertEquals(50, m0.calcolaDanno());
                assertEquals(100, m1.calcolaDanno());
            }
        }

        @Test
        @DisplayName("NPC surprise attack boundary stress test")
        void testNPCSurpriseAttackBounds() {
            NPC npcZero = new NPC("N0", 50, "n.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 5, 0), 10, 0.0, (n, s) -> null, false);
            NPC npcOne = new NPC("N1", 50, "n.png", 5, new ArrayList<>(), new Pistola("P", "D", 6, 5, 0), 10, 1.0, (n, s) -> null, false);

            for (int i = 0; i < 1000; i++) {
                assertFalse(npcZero.controllaAttaccoASorpresa());
                assertTrue(npcOne.controllaAttaccoASorpresa());
            }
        }
    }

    @Nested
    @DisplayName("Adversarial Exception Contracts")
    class ExceptionContractTests {

        @Test
        @DisplayName("Constructors throw required NullPointerExceptions and IllegalArgumentExceptions")
        void testConstructorExceptionContracts() {
            // RAM
            assertThrows(IllegalArgumentException.class, () -> new RAM(0));
            assertThrows(IllegalArgumentException.class, () -> new RAM(-10));

            // Entita / Giocatore
            ArrayList<Hack> hacks = new ArrayList<>();
            Pistola arma = new Pistola("P", "D", 6, 10, 0);
            assertThrows(NullPointerException.class, () -> new Giocatore(null, 100, "img", 5, hacks, arma, true));
            assertThrows(NullPointerException.class, () -> new Giocatore("G", 100, null, 5, hacks, arma, true));
            assertThrows(NullPointerException.class, () -> new Giocatore("G", 100, "img", 5, null, arma, true));
            assertThrows(NullPointerException.class, () -> new Giocatore("G", 100, "img", 5, hacks, null, true));
            assertThrows(IllegalArgumentException.class, () -> new Giocatore("G", -1, "img", 5, hacks, arma, true));

            // NPC
            assertThrows(IllegalArgumentException.class, () -> new NPC("N", 100, "img", 5, hacks, arma, 10, -0.01, (n, s) -> null, false));
            assertThrows(IllegalArgumentException.class, () -> new NPC("N", 100, "img", 5, hacks, arma, 10, 1.01, (n, s) -> null, false));
            assertThrows(NullPointerException.class, () -> new NPC("N", 100, "img", 5, hacks, arma, 10, 0.2, null, false));
            assertThrows(IllegalArgumentException.class, () -> new NPC("N", 100, "img", 5, hacks, arma, 0, 0.2, (n, s) -> null, false));
            assertThrows(IllegalArgumentException.class, () -> new NPC("N", 100, "img", 5, hacks, arma, -5, 0.2, (n, s) -> null, false));

            // Weapons
            assertThrows(NullPointerException.class, () -> new Pistola(null, "D", 6, 10, 0));
            assertThrows(IllegalArgumentException.class, () -> new Pistola("P", "D", 0, 10, 0));
            assertThrows(IllegalArgumentException.class, () -> new Pistola("P", "D", -5, 10, 0));
            assertThrows(NullPointerException.class, () -> new Mitragliatrice(null, "D", 30, 10, 0));
            assertThrows(IllegalArgumentException.class, () -> new Mitragliatrice("M", "D", 0, 10, 0));
            assertThrows(IllegalArgumentException.class, () -> new Mitragliatrice("M", "D", -5, 10, 0));

            // EffectType
            assertThrows(NullPointerException.class, () -> EffectType.valueOf(null));
            assertThrows(IllegalArgumentException.class, () -> EffectType.valueOf("nonexistent"));
            assertThrows(IllegalArgumentException.class, () -> EffectType.valueOf("damage"));

            // StatoBattaglia1v1
            Giocatore g = new Giocatore("G", 100, "img", 5, hacks, arma, true);
            NPC n = new NPC("N", 100, "img", 5, hacks, arma, 10, 0.2, (npc, s) -> null, false);
            assertThrows(NullPointerException.class, () -> new StatoBattaglia1v1(null, n));
            assertThrows(NullPointerException.class, () -> new StatoBattaglia1v1(g, null));

            // AzioneCaricaHack
            Hack hack = new Hack("H", "D", 2);
            assertThrows(NullPointerException.class, () -> new AzioneCaricaHack(null, n, hack));
            assertThrows(NullPointerException.class, () -> new AzioneCaricaHack(g, null, hack));
            assertThrows(NullPointerException.class, () -> new AzioneCaricaHack(g, n, null));

            // AzioneSparo
            AzioneSparo sparoNullLanciatore = new AzioneSparo(null, n);
            AzioneSparo sparoNullBersaglio = new AzioneSparo(g, null);
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(g, n);
            assertThrows(NullPointerException.class, () -> sparoNullLanciatore.esegui(stato));
            assertThrows(NullPointerException.class, () -> sparoNullBersaglio.esegui(stato));
        }
    }
}

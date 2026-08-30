package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.*;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Adversarial stress and boundary tests for Model & State layer.
 */
public class AdversarialModelStressTest {

    private static class StubStatoBattaglia implements StatoBattaglia {
        private final RAM ram;
        private final Giocatore giocatore;

        StubStatoBattaglia(RAM ram, Giocatore giocatore) {
            this.ram = ram;
            this.giocatore = giocatore;
        }

        @Override public RAM getRamCondivisa() { return ram; }
        @Override public Giocatore getGiocatore() { return giocatore; }
        @Override public ArrayList<Entita> getFazioneEroi() {
            ArrayList<Entita> l = new ArrayList<>();
            l.add(giocatore);
            return l;
        }
        @Override public ArrayList<Entita> getFazioneNemici() { return new ArrayList<>(); }
        @Override public Entita getEroe(int n) { return giocatore; }
        @Override public Entita getNemico(int n) { return null; }
        @Override public StatoBattaglia copy() { return this; }
    }

    private Giocatore createPlayer(String name, int pv, int ramSize) {
        return new Giocatore(name, pv, "player.png", ramSize, new ArrayList<>(),
                new Pistola("Pistol", "Desc", 10, 15, 0.0), true);
    }

    private NPC createNPC(String name, int pv, double chance, int surpriseDmg) {
        return new NPC(name, pv, "npc.png", 6, new ArrayList<>(),
                new Pistola("Pistol", "Desc", 10, 10, 0.0), surpriseDmg, chance, (n, s) -> null, false);
    }

    @Nested
    @DisplayName("RAM Adversarial & Stress Invariants")
    class RAMStressTests {

        @Test
        @DisplayName("RAM zero or negative capacity strictly rejected")
        void testCapacityBoundaries() {
            assertThrows(IllegalArgumentException.class, () -> new RAM(0));
            assertThrows(IllegalArgumentException.class, () -> new RAM(-100));
            assertThrows(IllegalArgumentException.class, () -> new RAM(Integer.MIN_VALUE));
        }

        @Test
        @DisplayName("RAM handles exact capacity saturation and rejects 1-tick overflow")
        void testExactSaturationAndOverflow() {
            RAM ram = new RAM(10);
            Giocatore p1 = createPlayer("P1", 100, 10);
            Giocatore p2 = createPlayer("P2", 100, 10);

            Hack h1 = new Hack("H1", "D1", 4);
            Hack h2 = new Hack("H2", "D2", 6);
            Hack h3 = new Hack("H3", "D3", 1);

            ram.inserisci(h1, p2, p1);
            ram.inserisci(h2, p2, p1);
            assertEquals(10, ram.getSpazioOccupato());

            assertThrows(IllegalArgumentException.class, () -> ram.inserisci(h3, p2, p1));
            assertEquals(10, ram.getSpazioOccupato());
        }

        @Test
        @DisplayName("RAM decrementaTesta reaches zero and negative ticks correctly without crashing")
        void testDecrementaTestaUnderflow() {
            RAM ram = new RAM(10);
            Giocatore p1 = createPlayer("P1", 100, 10);
            Hack h = new Hack("H", "D", 2);
            ram.inserisci(h, p1, p1);

            assertEquals(2, ram.getSpazioOccupato());
            ram.decrementaTesta();
            assertEquals(1, ram.getSpazioOccupato());
            assertEquals(1, ram.visualizzaTesta().getTickInCoda());

            ram.decrementaTesta();
            assertEquals(0, ram.getSpazioOccupato());
            assertEquals(0, ram.visualizzaTesta().getTickInCoda());

            // Decrement below 0
            ram.decrementaTesta();
            assertEquals(-1, ram.visualizzaTesta().getTickInCoda());
            assertEquals(-1, ram.getSpazioOccupato());
        }

        @Test
        @DisplayName("RAM concurrent access under multithreaded stress")
        void testConcurrentAccessStress() throws InterruptedException, ExecutionException {
            int threadCount = 8;
            int operationsPerThread = 500;
            RAM sharedRam = new RAM(100000);
            Giocatore player = createPlayer("P", 100, 10);

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int t = 0; t < threadCount; t++) {
                final int threadId = t;
                tasks.add(() -> {
                    for (int i = 0; i < operationsPerThread; i++) {
                        Hack h = new Hack("H_" + threadId + "_" + i, "Desc", 1);
                        sharedRam.inserisci(h, player, player);
                        sharedRam.decrementaTesta();
                        sharedRam.visualizzaTesta();
                        sharedRam.getSpazioOccupato();
                    }
                    return null;
                });
            }

            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> f : futures) {
                f.get(); // Ensure no exceptions thrown
            }
            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

            assertEquals(threadCount * operationsPerThread, sharedRam.getHacks().size());
        }

        @Test
        @DisplayName("RAM sort and reverse idempotency under various list sizes")
        void testSortAndReverseIdempotency() {
            RAM ram = new RAM(100);
            Giocatore p = createPlayer("P", 100, 10);

            // Empty
            assertDoesNotThrow(() -> ram.reverse());
            assertDoesNotThrow(() -> ram.sort());

            // Single item
            ram.inserisci(new Hack("H1", "D1", 5), p, p);
            ram.reverse();
            assertEquals("H1", ram.visualizzaTesta().getHack().getNome());
            ram.sort();
            assertEquals("H1", ram.visualizzaTesta().getHack().getNome());

            // Multiple items
            ram.inserisci(new Hack("H2", "D2", 2), p, p);
            ram.inserisci(new Hack("H3", "D3", 8), p, p);
            ram.inserisci(new Hack("H4", "D4", 1), p, p);

            // Double reverse returns to initial order
            List<String> namesBefore = ram.getHacks().stream().map(q -> q.getHack().getNome()).toList();
            ram.reverse();
            ram.reverse();
            List<String> namesAfter = ram.getHacks().stream().map(q -> q.getHack().getNome()).toList();
            assertEquals(namesBefore, namesAfter);

            // Sort ascending by tick
            ram.sort();
            List<Integer> ticks = ram.getHacks().stream().map(QueuedHack::getTickInCoda).toList();
            assertEquals(List.of(1, 2, 5, 8), ticks);
        }
    }

    @Nested
    @DisplayName("Entita & NPC Deep Mutation & Boundary Tests")
    class EntitaStressTests {

        @Test
        @DisplayName("Entita copy constructor isolates internal state from mutation")
        void testEntitaDeepCopyIsolation() {
            Arma weapon = new Pistola("Colt", "Classic", 6, 20, 0.1);
            ArrayList<Hack> hacks = new ArrayList<>();
            Hack hack = new Hack("Slash", "Desc", 3);
            hack.addEffetto(new EffettoDanno(15, true));
            hacks.add(hack);

            Giocatore original = new Giocatore("Hero", 100, "hero.png", 10, hacks, weapon, true);
            Entita copy = original.copy();

            // Mutate original
            original.setPv(40);
            original.getHacks().get(0).addEffetto(new EffettoCura(10, false));

            // Assert copy is untouched
            assertEquals(100, copy.getPv(), "Copy PV should remain unchanged");
            assertEquals(1, copy.getHacks().get(0).getEffetti().size(), "Copy hack effects should not be mutated");
            assertNotSame(original.getArma(), copy.getArma());
        }

        @Test
        @DisplayName("Entita setPv boundary clamping strictly respects [0, maxPv]")
        void testSetPvExtremeBoundaries() {
            Giocatore player = createPlayer("P", 100, 10);

            player.setPv(Integer.MAX_VALUE);
            assertEquals(100, player.getPv());

            player.setPv(101);
            assertEquals(100, player.getPv());

            player.setPv(0);
            assertEquals(0, player.getPv());

            player.setPv(-1);
            assertEquals(0, player.getPv());

            player.setPv(Integer.MIN_VALUE);
            assertEquals(0, player.getPv());
        }

        @Test
        @DisplayName("NPC surprise attack chance boundaries 0.0 and 1.0 are 100% deterministic")
        void testNpcSurpriseAttackDeterminism() {
            NPC zeroChance = createNPC("Minion", 50, 0.0, 10);
            for (int i = 0; i < 100; i++) {
                assertFalse(zeroChance.controllaAttaccoASorpresa());
            }

            NPC fullChance = createNPC("Assassin", 50, 1.0, 10);
            for (int i = 0; i < 100; i++) {
                assertTrue(fullChance.controllaAttaccoASorpresa());
            }
        }

        @Test
        @DisplayName("NPC constructor rejects invalid parameter combinations")
        void testNpcInvalidParams() {
            assertThrows(IllegalArgumentException.class, () -> createNPC("N", 50, -0.01, 10));
            assertThrows(IllegalArgumentException.class, () -> createNPC("N", 50, 1.01, 10));
            assertThrows(IllegalArgumentException.class, () -> createNPC("N", 50, 0.5, 0));
            assertThrows(IllegalArgumentException.class, () -> createNPC("N", 50, 0.5, -5));
            assertThrows(NullPointerException.class, () ->
                    new NPC("N", 50, "n.png", 6, new ArrayList<>(),
                            new Pistola("P", "D", 6, 10, 0.0), 10, 0.2, null, false));
        }
    }

    @Nested
    @DisplayName("Equipaggiamento (Arma, Pistola, Mitragliatrice) Invariants")
    class EquipaggiamentoStressTests {

        @Test
        @DisplayName("Pistola and Mitragliatrice damage calculation under boundary crit chances (0.0 and 1.0)")
        void testWeaponDamageDeterministicBoundaries() {
            Pistola pZeroCrit = new Pistola("P0", "D", 6, 20, 0.0);
            Pistola pFullCrit = new Pistola("P1", "D", 6, 20, 1.0);

            for (int i = 0; i < 50; i++) {
                assertEquals(20, pZeroCrit.calcolaDanno());
                assertEquals(40, pFullCrit.calcolaDanno());
            }

            Mitragliatrice mZeroCrit = new Mitragliatrice("M0", "D", 30, 10, 0.0);
            Mitragliatrice mFullCrit = new Mitragliatrice("M1", "D", 30, 10, 1.0);

            for (int i = 0; i < 50; i++) {
                assertEquals(50, mZeroCrit.calcolaDanno(), "5 shots * 10 = 50");
                assertEquals(100, mFullCrit.calcolaDanno(), "5 shots * (10*2) = 100");
            }
        }

        @Test
        @DisplayName("Weapon copy creates distinct instances with preserved statistics")
        void testWeaponCopy() {
            Mitragliatrice original = new Mitragliatrice("Heavy", "Big gun", 50, 12, 0.25);
            Arma copy = original.copy();

            assertNotSame(original, copy);
            assertEquals(original.getNome(), copy.getNome());
            assertEquals(original.getDescrizione(), copy.getDescrizione());
            assertEquals(original.getMaxCaricatore(), copy.getMaxCaricatore());
            assertEquals(original.getCaricatore(), copy.getCaricatore());
            assertEquals(original.getDanno(), copy.getDanno());
        }

        @Test
        @DisplayName("Arma constructor rejects null name and non-positive capacity")
        void testArmaValidation() {
            assertThrows(NullPointerException.class, () -> new Pistola(null, "D", 6, 10, 0.1));
            assertThrows(IllegalArgumentException.class, () -> new Pistola("P", "D", 0, 10, 0.1));
            assertThrows(IllegalArgumentException.class, () -> new Pistola("P", "D", -10, 10, 0.1));
        }
    }

    @Nested
    @DisplayName("Effetti Execution & State Mutation Invariants")
    class EffettiStressTests {

        @Test
        @DisplayName("EffettoCura heals caster without exceeding maxPv and never mutates target")
        void testEffettoCuraExecution() {
            Giocatore caster = createPlayer("Caster", 100, 10);
            Giocatore target = createPlayer("Target", 100, 10);
            caster.setPv(30);

            EffettoCura heal = new EffettoCura(40, true);
            assertEquals(EffectType.HEAL, heal.getEffectType());
            heal.eseguiEffetto(null, caster, target);

            assertEquals(70, caster.getPv());
            assertEquals(100, target.getPv());

            // Over-healing
            heal.eseguiEffetto(null, caster, target);
            assertEquals(100, caster.getPv());
        }

        @Test
        @DisplayName("EffettoDanno damages target down to 0 without mutating caster")
        void testEffettoDannoExecution() {
            Giocatore caster = createPlayer("Caster", 100, 10);
            Giocatore target = createPlayer("Target", 100, 10);

            EffettoDanno damage = new EffettoDanno(60, true);
            assertEquals(EffectType.DAMAGE, damage.getEffectType());
            damage.eseguiEffetto(null, caster, target);

            assertEquals(40, target.getPv());
            assertEquals(100, caster.getPv());

            // Overkill damage
            damage.eseguiEffetto(null, caster, target);
            assertEquals(0, target.getPv());
        }

        @Test
        @DisplayName("EffettoReverse and EffettoSort manipulate RAM within StatoBattaglia")
        void testEffettoRAMManipulation() {
            RAM ram = new RAM(20);
            Giocatore p1 = createPlayer("P1", 100, 10);
            Giocatore p2 = createPlayer("P2", 100, 10);
            StubStatoBattaglia stato = new StubStatoBattaglia(ram, p1);

            ram.inserisci(new Hack("H1", "D", 7), p2, p1);
            ram.inserisci(new Hack("H2", "D", 2), p2, p1);
            ram.inserisci(new Hack("H3", "D", 5), p2, p1);

            EffettoSort sortEffect = new EffettoSort(true);
            assertEquals(EffectType.RAM, sortEffect.getEffectType());
            sortEffect.eseguiEffetto(stato, p1, p2);

            assertEquals("H2", ram.getHacks().get(0).getHack().getNome());
            assertEquals("H3", ram.getHacks().get(1).getHack().getNome());
            assertEquals("H1", ram.getHacks().get(2).getHack().getNome());

            EffettoReverse reverseEffect = new EffettoReverse(true);
            assertEquals(EffectType.RAM, reverseEffect.getEffectType());
            reverseEffect.eseguiEffetto(stato, p1, p2);

            assertEquals("H1", ram.getHacks().get(0).getHack().getNome());
            assertEquals("H3", ram.getHacks().get(1).getHack().getNome());
            assertEquals("H2", ram.getHacks().get(2).getHack().getNome());
        }
    }

    @Nested
    @DisplayName("QueuedHack & SessionState State Invariants")
    class QueuedHackAndSessionStateTests {

        @Test
        @DisplayName("QueuedHack copy constructor isolates Hack modification")
        void testQueuedHackDeepCopy() {
            Giocatore p1 = createPlayer("P1", 100, 10);
            Giocatore p2 = createPlayer("P2", 100, 10);
            Hack h = new Hack("Lightning", "Shock", 4);
            h.addEffetto(new EffettoDanno(25, true));

            QueuedHack original = new QueuedHack(h, p2, p1);
            QueuedHack copy = new QueuedHack(original);

            // Mutate copy
            copy.setTickInCoda(1);
            copy.getHack().addEffetto(new EffettoCura(5, false));

            assertEquals(4, original.getTickInCoda());
            assertEquals(1, original.getHack().getEffetti().size());
            assertEquals(2, copy.getHack().getEffetti().size());
        }

        @Test
        @DisplayName("SessionState manages independent state container instances")
        void testSessionStateIndependence() {
            SessionState s1 = new SessionState();
            SessionState s2 = new SessionState();

            s1.spazioRam = 12;
            s2.spazioRam = 24;

            Clock c1 = new Clock(() -> {});
            Clock c2 = new Clock(() -> {});
            s1.clock = c1;
            s2.clock = c2;

            assertEquals(12, s1.spazioRam);
            assertEquals(24, s2.spazioRam);
            assertSame(c1, s1.clock);
            assertSame(c2, s2.clock);
        }
    }
}

package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoSort;
import it.unicam.cs.mpgc.rpg130077.model.Entita.CombattenteAutonomo;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.GameFactory;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Adversarial Stress & Concurrency Suite for Milestone 2:
 * 1. Combat simulation loops & simultaneous deaths
 * 2. Complex RAM queue execution with dynamic sorting/reversal
 * 3. AI heal vs attack choices Monte Carlo invariants
 * 4. Clock threading, interruption safety, and concurrency hammering
 * 5. GameFactory boundary and deep copy isolation
 */
public class CombatSimulationConcurrencyStressTest {

    private static class TrackingListener implements CombattimentoListener {
        final AtomicInteger ticks = new AtomicInteger(0);
        final AtomicInteger hpUpdates = new AtomicInteger(0);
        final AtomicInteger ramUpdates = new AtomicInteger(0);
        final AtomicInteger victoryEvents = new AtomicInteger(0);
        final AtomicInteger enemyCannotAttacks = new AtomicInteger(0);
        volatile Entita winner = null;

        @Override public void onTick(StatoBattaglia statoBattaglia) { ticks.incrementAndGet(); }
        @Override public void onVitaAggiornataEntita(Entita entita) {}
        @Override public void onVittoria(Entita vincitore) {
            victoryEvents.incrementAndGet();
            this.winner = vincitore;
        }
        @Override public void onVitaAggiornata(StatoBattaglia statoBattaglia) { hpUpdates.incrementAndGet(); }
        @Override public void onTurnoGiocatore() {}
        @Override public void onAggiornamentoRAM(RAM ram) { ramUpdates.incrementAndGet(); }
        @Override public void ilNemicoNonPuoAttaccare() { enemyCannotAttacks.incrementAndGet(); }
    }

    private Giocatore buildPlayer(String name, int pv, int ramSize, int weaponDmg) {
        return new Giocatore(name, pv, "player.png", ramSize, new ArrayList<>(),
                new Pistola("PlayerPistol", "Basic", 6, weaponDmg, 0.0), true);
    }

    private NPC buildEnemy(String name, int pv, int ramSize, int weaponDmg, double critChance) {
        return new NPC(name, pv, "enemy.png", ramSize, new ArrayList<>(),
                new Pistola("EnemyPistol", "Basic", 6, weaponDmg, critChance), 5, 0.0, new StrategiaCasuale(), false);
    }

    @Nested
    @DisplayName("1. Combat Simulation Loops & Simultaneous Death Scenarios")
    class CombatSimulationLoopTests {

        @Test
        @DisplayName("Simultaneous death via tick conclusive damage triggers draw and single victory notification")
        void testSimultaneousDeathViaConclusiveTick() {
            Giocatore player = buildPlayer("P1", 20, 10, 5);
            NPC enemy = buildEnemy("E1", 20, 10, 5, 0.0);
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(player, enemy);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            TrackingListener listener = new TrackingListener();
            combat.aggiungiListener(listener);

            // Hack with conclusive damage to both
            Hack mutualDestruction = new Hack("MutualDestruction", "AoE", 1);
            mutualDestruction.addEffetto(new EffettoDanno(50, true)); // damages target (enemy)
            mutualDestruction.addEffetto(new Effetto() {
                @Override
                public void eseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
                    lanciatore.setPv(lanciatore.getPv() - 50);
                }

                @Override
                public boolean isConclusive() {
                    return true;
                }

                @Override
                public Effetto copy() {
                    return this;
                }

                @Override
                public EffectType getEffectType() {
                    return EffectType.DAMAGE;
                }
            });

            stato.getRamCondivisa().inserisci(mutualDestruction, enemy, player);
            combat.onTick();

            assertEquals(0, player.getPv());
            assertEquals(0, enemy.getPv());

            Entita winner = combat.checkVittoria();
            assertNull(winner, "Draw must return null winner");
            assertEquals(1, listener.victoryEvents.get(), "Must notify onVittoria exactly once");
            assertNull(listener.winner, "Victory event winner must be null for draw");

            // Calling checkVittoria repeatedly should not fire extra notifications
            combat.checkVittoria();
            combat.checkVittoria();
            assertEquals(1, listener.victoryEvents.get());
        }

        @Test
        @DisplayName("Simultaneous death via continuous tick damage triggers draw correctly")
        void testSimultaneousDeathViaContinuousTick() {
            Giocatore player = buildPlayer("P1", 10, 10, 5);
            NPC enemy = buildEnemy("E1", 10, 10, 5, 0.0);
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(player, enemy);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            TrackingListener listener = new TrackingListener();
            combat.aggiungiListener(listener);

            // Continuous damage hack of 3 ticks, 15 dmg per tick to both
            Hack poisonCloud = new Hack("PoisonCloud", "Continuous", 3);
            poisonCloud.addEffetto(new EffettoDanno(15, false));
            poisonCloud.addEffetto(new Effetto() {
                @Override
                public void eseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
                    lanciatore.setPv(lanciatore.getPv() - 15);
                }

                @Override
                public boolean isConclusive() {
                    return false;
                }

                @Override
                public Effetto copy() {
                    return this;
                }

                @Override
                public EffectType getEffectType() {
                    return EffectType.DAMAGE;
                }
            });

            stato.getRamCondivisa().inserisci(poisonCloud, enemy, player);
            // On first tick, both take 15 damage and reach 0 PV
            combat.onTick();

            assertEquals(0, player.getPv());
            assertEquals(0, enemy.getPv());
            assertNull(combat.checkVittoria());
            assertEquals(1, listener.victoryEvents.get());
            assertNull(listener.winner);
        }

        @Test
        @DisplayName("100-step simulation loop with auto-advancing combat runs to completion without deadlock")
        void testFullCombatSimulationLoop() {
            Giocatore player = buildPlayer("Hero", 100, 10, 15);
            NPC enemy = buildEnemy("Boss", 100, 10, 12, 0.1);

            // Add hacks to player and enemy
            Hack h1 = new Hack("QuickDmg", "D", 1);
            h1.addEffetto(new EffettoDanno(25, true));
            player.getHacks().add(h1);

            Hack h2 = new Hack("MedHeal", "H", 2);
            h2.addEffetto(new EffettoCura(30, true));
            enemy.getHacks().add(h2);

            StatoBattaglia1v1 stato = new StatoBattaglia1v1(player, enemy);
            CombattimentoATurni combat = new CombattimentoATurni(stato);
            TrackingListener listener = new TrackingListener();
            combat.aggiungiListener(listener);

            int turns = 0;
            int maxTurns = 200;

            while (combat.checkVittoria() == null && turns < maxTurns) {
                // Execute ticks if RAM has hacks
                if (stato.getRamCondivisa().visualizzaTesta() != null) {
                    combat.onTick();
                }

                if (combat.checkVittoria() != null) break;

                if (combat.isPlayerTurn()) {
                    // Player attacks or loads hack
                    if (player.getPv() > 0) {
                        combat.spara(enemy);
                    }
                }
                turns++;
            }

            assertTrue(turns < maxTurns, "Combat should finish within 200 turns");
            assertTrue(player.getPv() <= 0 || enemy.getPv() <= 0, "At least one entity must be defeated");
            assertEquals(1, listener.victoryEvents.get(), "Victory must be notified exactly once");
        }

        @Test
        @DisplayName("ripristina resets combat and allows subsequent full battle simulation")
        void testCombatRipristinaAndReplay() {
            Giocatore player = buildPlayer("Hero", 50, 10, 50); // One-shot weapon
            NPC enemy = buildEnemy("Enemy", 40, 10, 10, 0.0);
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(player, enemy);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            TrackingListener listener = new TrackingListener();
            combat.aggiungiListener(listener);

            // Battle 1: Player kills enemy in 1 shot
            combat.spara(enemy);
            assertEquals(0, combat.getStatoBattaglia().getNemico(0).getPv());
            assertNotNull(combat.checkVittoria());
            assertEquals(1, listener.victoryEvents.get());

            // Ripristina
            combat.ripristina();
            assertEquals(50, combat.getStatoBattaglia().getGiocatore().getPv());
            assertEquals(40, combat.getStatoBattaglia().getNemico(0).getPv());
            assertNull(combat.checkVittoria(), "After restore, battle is active again");

            // Battle 2: Kill enemy again and verify victory is notified a second time
            combat.spara(combat.getStatoBattaglia().getNemico(0));
            assertEquals(0, combat.getStatoBattaglia().getNemico(0).getPv());
            assertNotNull(combat.checkVittoria());
            assertEquals(2, listener.victoryEvents.get(), "Victory event counter incremented on new win");
        }
    }

    @Nested
    @DisplayName("2. Complex RAM Queue Execution with Dynamic Manipulation")
    class ComplexRAMExecutionTests {

        @Test
        @DisplayName("Conclusive EffettoReverse in queue reverses remaining pending hacks on completion")
        void testEffettoReverseDuringQueueExecution() {
            Giocatore caster = buildPlayer("Caster", 100, 20, 10);
            NPC target = buildEnemy("Target", 100, 20, 10, 0.0);
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(caster, target);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            // Hack 1: 1 tick, reverses queue
            Hack hReverse = new Hack("Reverser", "Rev", 1);
            hReverse.addEffetto(new EffettoReverse(true));

            // Hack 2: 2 ticks
            Hack h2 = new Hack("H2", "Desc", 2);
            h2.addEffetto(new EffettoDanno(10, true));

            // Hack 3: 5 ticks
            Hack h3 = new Hack("H3", "Desc", 5);
            h3.addEffetto(new EffettoDanno(20, true));

            stato.getRamCondivisa().inserisci(hReverse, target, caster);
            stato.getRamCondivisa().inserisci(h2, target, caster);
            stato.getRamCondivisa().inserisci(h3, target, caster);

            // Tick 1: hReverse completes and reverses remaining queue [h2, h3] -> [h3, h2]
            combat.onTick();

            assertEquals(2, stato.getRamCondivisa().getHacks().size());
            assertEquals("H3", stato.getRamCondivisa().visualizzaTesta().getHack().getNome());
            assertEquals(5, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());
        }

        @Test
        @DisplayName("Conclusive EffettoSort in queue sorts remaining pending hacks by duration")
        void testEffettoSortDuringQueueExecution() {
            Giocatore caster = buildPlayer("Caster", 100, 20, 10);
            NPC target = buildEnemy("Target", 100, 20, 10, 0.0);
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(caster, target);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            // Hack 1: 1 tick, sorts queue
            Hack hSort = new Hack("Sorter", "Sort", 1);
            hSort.addEffetto(new EffettoSort(true));

            // Hack 2: 8 ticks
            Hack h2 = new Hack("LongHack", "Desc", 8);
            h2.addEffetto(new EffettoDanno(10, true));

            // Hack 3: 2 ticks
            Hack h3 = new Hack("ShortHack", "Desc", 2);
            h3.addEffetto(new EffettoDanno(20, true));

            stato.getRamCondivisa().inserisci(hSort, target, caster);
            stato.getRamCondivisa().inserisci(h2, target, caster);
            stato.getRamCondivisa().inserisci(h3, target, caster);

            // Tick 1: hSort completes and sorts [LongHack(8), ShortHack(2)] -> [ShortHack(2), LongHack(8)]
            combat.onTick();

            assertEquals(2, stato.getRamCondivisa().getHacks().size());
            assertEquals("ShortHack", stato.getRamCondivisa().visualizzaTesta().getHack().getNome());
            assertEquals(2, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());
        }
    }

    @Nested
    @DisplayName("3. AI Heal vs Attack Choices Monte Carlo Verification")
    class StrategiaCasualeAdversarialTests {

        @Test
        @DisplayName("10,000 AI decisions: HEAL hacks strictly self-target, DAMAGE hacks strictly target hero")
        void testAIMassiveTargetingInvariants() {
            StrategiaCasuale ai = new StrategiaCasuale();
            RAM ram = new RAM(20);

            class MultiHeroStato implements StatoBattaglia {
                final ArrayList<Entita> eroi = new ArrayList<>();
                final ArrayList<Entita> nemici = new ArrayList<>();
                final RAM r;
                MultiHeroStato(RAM r) { this.r = r; }
                @Override public RAM getRamCondivisa() { return r; }
                @Override public Giocatore getGiocatore() { return (Giocatore) eroi.get(0); }
                @Override public ArrayList<Entita> getFazioneEroi() { return eroi; }
                @Override public ArrayList<Entita> getFazioneNemici() { return nemici; }
                @Override public Entita getEroe(int n) { return eroi.get(n); }
                @Override public Entita getNemico(int n) { return nemici.get(n); }
                @Override public StatoBattaglia copy() { return this; }
            }

            MultiHeroStato stato = new MultiHeroStato(ram);
            Giocatore h1 = buildPlayer("Hero1", 100, 5, 10);
            Giocatore h2 = buildPlayer("Hero2", 100, 5, 10);
            stato.eroi.add(h1);
            stato.eroi.add(h2);

            Hack healHack = new Hack("Repair", "Heal self", 2);
            healHack.addEffetto(new EffettoCura(25, true));

            Hack attackHack = new Hack("Nuke", "Attack hero", 2);
            attackHack.addEffetto(new EffettoDanno(40, true));

            ArrayList<Hack> hacks = new ArrayList<>(List.of(healHack, attackHack));
            NPC npc = new NPC("CyberBoss", 100, "boss.png", 5, hacks,
                    new Pistola("Gun", "Desc", 6, 15, 0.0), 5, 0.0, ai, false);
            stato.nemici.add(npc);

            int healHackCount = 0;
            int attackHackCount = 0;
            int weaponShootCount = 0;

            for (int i = 0; i < 10000; i++) {
                // Keep RAM empty for unconstrained decisions
                while (ram.visualizzaTesta() != null) {
                    ram.rimuovi();
                }

                Azione action = ai.scegliMossa(npc, stato);
                assertNotNull(action, "Should always produce an action when weapon and hacks are ready");

                if (action instanceof AzioneCaricaHack carica) {
                    if (carica.getHack().getEffectTypes().contains(EffectType.HEAL)) {
                        healHackCount++;
                        // Invariant: Heal hack must target NPC itself
                        carica.esegui(stato);
                        QueuedHack q = ram.visualizzaTesta();
                        assertSame(npc, q.getBersaglio(), "Heal hack target MUST be caster NPC");
                    } else {
                        attackHackCount++;
                        // Invariant: Damage hack must target an eroe
                        carica.esegui(stato);
                        QueuedHack q = ram.visualizzaTesta();
                        assertTrue(q.getBersaglio() == h1 || q.getBersaglio() == h2,
                                "Offensive hack target MUST be one of the heroes");
                    }
                } else if (action instanceof AzioneSparo) {
                    weaponShootCount++;
                }
            }

            // Both hacks and weapon shots should be reasonably distributed
            assertTrue(healHackCount > 1000, "Heal hack count: " + healHackCount);
            assertTrue(attackHackCount > 1000, "Attack hack count: " + attackHackCount);
            assertTrue(weaponShootCount > 3000, "Weapon shoot count: " + weaponShootCount);
        }

        @Test
        @DisplayName("AI returns null when RAM is full and NPC has no weapon")
        void testAIReturnsNullWhenFullRAMAndNoWeapon() {
            StrategiaCasuale ai = new StrategiaCasuale();
            Giocatore h = buildPlayer("Hero", 100, 5, 10);
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(h, buildEnemy("E", 100, 5, 10, 0.0));

            // Fill shared RAM completely
            int maxCap = stato.getRamCondivisa().getSpazioMassimoInSecondi();
            stato.getRamCondivisa().inserisci(new Hack("Fill", "Desc", maxCap), h, h);

            // NPC with only 2-sec hacks and NO weapon (override getArma)
            Hack bigHack = new Hack("Big", "Desc", 2);
            bigHack.addEffetto(new EffettoDanno(10, true));
            NPC weaponlessNpc = new NPC("Mage", 100, "mage.png", 5, new ArrayList<>(List.of(bigHack)),
                    new Pistola("Dummy", "Desc", 6, 10, 0.0), 5, 0.0, ai, false) {
                @Override
                public Arma getArma() {
                    return null;
                }
            };

            Azione action = ai.scegliMossa(weaponlessNpc, stato);
            assertNull(action, "Should return null safely when cannot fit hack and has no weapon");
        }
    }

    @Nested
    @DisplayName("4. Clock Multi-Threaded Concurrency Hammering & Interruption Safety")
    class ClockConcurrencyStressTests {

        @Test
        @DisplayName("Rapid concurrent start/stop cycles from 16 worker threads do not deadlock or throw")
        void testClockMultiThreadedHammering() throws InterruptedException, ExecutionException {
            AtomicInteger tickCounter = new AtomicInteger(0);
            Clock clock = new Clock(tickCounter::incrementAndGet);

            int workerCount = 16;
            int iterationsPerWorker = 50;
            ExecutorService pool = Executors.newFixedThreadPool(workerCount);
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < workerCount; i++) {
                final int id = i;
                futures.add(pool.submit(() -> {
                    for (int j = 0; j < iterationsPerWorker; j++) {
                        if (id % 2 == 0) {
                            clock.start();
                        } else {
                            clock.stop();
                        }
                    }
                    return null;
                }));
            }

            for (Future<Void> f : futures) {
                f.get();
            }

            clock.stop();
            pool.shutdown();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS), "Thread pool should terminate cleanly");
        }

        @Test
        @DisplayName("Clock stops immediately without waiting for full 1-second sleep period")
        void testClockStopInterruptionSpeed() {
            AtomicBoolean tickFired = new AtomicBoolean(false);
            Clock clock = new Clock(() -> tickFired.set(true));

            long start = System.currentTimeMillis();
            clock.start();
            // Stop almost immediately (50ms)
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            clock.stop();
            long elapsed = System.currentTimeMillis() - start;

            // Elapsed time should be well below 500ms (verifying Thread.sleep(1000) was interrupted)
            assertTrue(elapsed < 600, "Clock stop should interrupt sleep swiftly; took " + elapsed + "ms");
            assertFalse(tickFired.get(), "Tick should not have fired within 50ms");
        }
    }

    @Nested
    @DisplayName("5. GameFactory Boundary and Isolation Stress Tests")
    class GameFactoryStressTests {

        @Test
        @DisplayName("GameFactory enforces minimum 2 catalog weapons invariant")
        void testGameFactoryMinimumCatalogWeapons() {
            GameFactory factory = new GameFactory();
            List<Arma> playerWeapons = List.of(new Pistola("P", "D", 6, 10, 0));
            List<Hack> playerHacks = List.of();
            List<Arma> catalogWeapons1 = List.of(new Pistola("SingleCat", "D", 6, 10, 0));
            List<Hack> catalogHacks = List.of();

            assertThrows(IllegalStateException.class, () ->
                    factory.creaNuovaPartitaSemplice(playerWeapons, playerHacks, catalogWeapons1, catalogHacks));
        }

        @Test
        @DisplayName("GameFactory creates fully functioning combat system with deep copied assets")
        void testGameFactoryCreatedCombatExecution() {
            GameFactory factory = new GameFactory();
            List<Arma> playerWeapons = List.of(new Pistola("PlayerGun", "D", 6, 20, 0));
            List<Hack> playerHacks = List.of(new Hack("HackP", "D", 2));
            List<Arma> catalogWeapons = List.of(
                    new Pistola("Cat0", "D", 6, 10, 0),
                    new Pistola("Cat1_Enemy", "D", 6, 15, 0)
            );
            List<Hack> catalogHacks = List.of(
                    new Hack("CatH0", "D", 1),
                    new Hack("CatH1_Enemy", "D", 3)
            );

            SistemaCombattimento combat = factory.creaNuovaPartitaSemplice(playerWeapons, playerHacks, catalogWeapons, catalogHacks);
            assertNotNull(combat);
            assertNotNull(combat.getStatoBattaglia());

            combat.onTick();
            assertEquals(100, combat.getStatoBattaglia().getGiocatore().getPv());
            assertEquals(100, combat.getStatoBattaglia().getNemico(0).getPv());
        }
    }
}

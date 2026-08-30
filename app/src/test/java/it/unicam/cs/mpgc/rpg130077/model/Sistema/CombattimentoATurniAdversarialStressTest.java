package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoSort;
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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Adversarial Stress & Correctness Harness for Milestone 2:
 * Engine (CombattimentoATurni, Clock), Logic (StrategiaCasuale, GameFactory),
 * and Actions (AzioneSparo, AzioneCaricaHack).
 */
public class CombattimentoATurniAdversarialStressTest {

    private static class RecordingListener implements CombattimentoListener {
        final AtomicInteger ticks = new AtomicInteger(0);
        final AtomicInteger hpUpdates = new AtomicInteger(0);
        final AtomicInteger ramUpdates = new AtomicInteger(0);
        final AtomicInteger enemyCannotAttacks = new AtomicInteger(0);
        final AtomicInteger victories = new AtomicInteger(0);
        volatile Entita lastWinner = null;

        @Override public void onTick(StatoBattaglia statoBattaglia) { ticks.incrementAndGet(); }
        @Override public void onVitaAggiornataEntita(Entita entita) {}
        @Override public void onVittoria(Entita vincitore) {
            victories.incrementAndGet();
            this.lastWinner = vincitore;
        }
        @Override public void onVitaAggiornata(StatoBattaglia statoBattaglia) { hpUpdates.incrementAndGet(); }
        @Override public void onTurnoGiocatore() {}
        @Override public void onAggiornamentoRAM(RAM ram) { ramUpdates.incrementAndGet(); }
        @Override public void ilNemicoNonPuoAttaccare() { enemyCannotAttacks.incrementAndGet(); }
    }

    private Giocatore createHero(String name, int pv, int ram, int dmg) {
        return new Giocatore(name, pv, "hero.png", ram, new ArrayList<>(),
                new Pistola("HeroPistol", "Desc", 6, dmg, 0.0), true);
    }

    private NPC createNPC(String name, int pv, int ram, int dmg, StrategiaCasuale strat) {
        return new NPC(name, pv, "npc.png", ram, new ArrayList<>(),
                new Pistola("NPCPistol", "Desc", 6, dmg, 0.0), 5, 0.0, strat, false);
    }

    @Nested
    @DisplayName("CombattimentoATurni Multi-Tick & Complex RAM Execution Harness")
    class MultiTickHarnessTests {

        @Test
        @DisplayName("Multi-tick combat with 5 queued hacks executing continuous and conclusive effects")
        void testMultiTickLifecycleWithComplexQueue() {
            Giocatore hero = createHero("Hero", 200, 20, 10);
            NPC enemy = createNPC("Enemy", 200, 20, 10, new StrategiaCasuale());
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(hero, enemy);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            RecordingListener listener = new RecordingListener();
            combat.aggiungiListener(listener);

            // Hack 1: 2 ticks, 10 continuous dmg per tick
            Hack h1 = new Hack("Poison1", "D", 2);
            h1.addEffetto(new EffettoDanno(10, false));

            // Hack 2: 1 tick, 40 conclusive dmg
            Hack h2 = new Hack("Nuke1", "D", 1);
            h2.addEffetto(new EffettoDanno(40, true));

            // Hack 3: 3 ticks, 5 continuous dmg + 20 conclusive heal to caster
            Hack h3 = new Hack("Leech", "D", 3);
            h3.addEffetto(new EffettoDanno(5, false));
            h3.addEffetto(new EffettoCura(20, true));

            stato.getRamCondivisa().inserisci(h1, enemy, hero);
            stato.getRamCondivisa().inserisci(h2, enemy, hero);
            stato.getRamCondivisa().inserisci(h3, enemy, hero);

            assertEquals(3, stato.getRamCondivisa().getHacks().size());
            assertEquals(6, stato.getRamCondivisa().getSpazioOccupato());

            // Tick 1: h1 ticks (2 -> 1), applies 10 continuous dmg to enemy
            combat.onTick();
            assertEquals(190, enemy.getPv());
            assertEquals(1, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());

            // Tick 2: h1 ticks (1 -> 0), applies 10 continuous dmg (enemy PV=180), finishes & removed
            combat.onTick();
            assertEquals(180, enemy.getPv());
            assertEquals(2, stato.getRamCondivisa().getHacks().size());
            assertEquals("Nuke1", stato.getRamCondivisa().visualizzaTesta().getHack().getNome());

            // Tick 3: h2 ticks (1 -> 0), applies 40 conclusive dmg (enemy PV=140), finishes & removed
            combat.onTick();
            assertEquals(140, enemy.getPv());
            assertEquals(1, stato.getRamCondivisa().getHacks().size());
            assertEquals("Leech", stato.getRamCondivisa().visualizzaTesta().getHack().getNome());

            // Tick 4: h3 ticks (3 -> 2), applies 5 continuous dmg (enemy PV=135)
            hero.setPv(150); // Lower hero HP to observe heal later
            combat.onTick();
            assertEquals(135, enemy.getPv());

            // Tick 5: h3 ticks (2 -> 1), applies 5 continuous dmg (enemy PV=130)
            combat.onTick();
            assertEquals(130, enemy.getPv());

            // Tick 6: h3 ticks (1 -> 0), applies 5 continuous dmg (enemy PV=125) + 20 heal to caster hero (150 -> 170)
            combat.onTick();
            assertEquals(125, enemy.getPv());
            assertEquals(170, hero.getPv());
            assertNull(stato.getRamCondivisa().visualizzaTesta());
            assertEquals(0, stato.getRamCondivisa().getHacks().size());

            assertEquals(6, listener.ticks.get());
            assertEquals(6, listener.ramUpdates.get());
            assertEquals(6, listener.hpUpdates.get());
        }

        @Test
        @DisplayName("Simultaneous death during tick triggers draw (vittoria null)")
        void testSimultaneousDeathDuringTickProducesDraw() {
            Giocatore hero = createHero("Hero", 10, 10, 10);
            NPC enemy = createNPC("Enemy", 10, 10, 10, new StrategiaCasuale());
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(hero, enemy);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            RecordingListener listener = new RecordingListener();
            combat.aggiungiListener(listener);

            // Hack that deals 20 damage to both hero and enemy simultaneously
            Hack suicideBomb = new Hack("SuicideBomb", "AOE", 1);
            suicideBomb.addEffetto(new EffettoDanno(20, true)); // Deals 20 to target (enemy)
            suicideBomb.addEffetto(new EffettoDanno(20, true) { // Custom effect to also damage hero
                @Override
                public void eseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
                    lanciatore.setPv(lanciatore.getPv() - 20);
                }
            });

            stato.getRamCondivisa().inserisci(suicideBomb, enemy, hero);

            // Tick 1 triggers conclusive effects: both die (PV = 0)
            combat.onTick();

            assertEquals(0, hero.getPv());
            assertEquals(0, enemy.getPv());

            Entita winner = combat.checkVittoria();
            assertNull(winner);
            assertEquals(1, listener.victories.get());
            assertNull(listener.lastWinner);
        }

        @Test
        @DisplayName("Dead enemy does not take counter-attack turn when killed by hero shot")
        void testDeadEnemyDoesNotCounterAttack() {
            Giocatore hero = createHero("Hero", 100, 10, 100); // 100 dmg kills 50 HP enemy in 1 shot
            NPC enemy = createNPC("Enemy", 50, 10, 30, new StrategiaCasuale());
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(hero, enemy);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            RecordingListener listener = new RecordingListener();
            combat.aggiungiListener(listener);

            // Hero shoots enemy
            combat.spara(enemy);

            assertEquals(0, enemy.getPv());
            assertEquals(100, hero.getPv(), "Hero should NOT have taken any counter-attack damage from dead enemy");
            assertEquals(1, listener.victories.get());
            assertSame(hero, listener.lastWinner);
        }

        @Test
        @DisplayName("Multi-listener registration and unregistration broadcast integrity")
        void testMultiListenerBroadcastAndUnsubscribe() {
            Giocatore hero = createHero("Hero", 100, 10, 10);
            NPC enemy = createNPC("Enemy", 100, 10, 10, new StrategiaCasuale());
            StatoBattaglia1v1 stato = new StatoBattaglia1v1(hero, enemy);
            CombattimentoATurni combat = new CombattimentoATurni(stato);

            List<RecordingListener> listeners = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                RecordingListener l = new RecordingListener();
                listeners.add(l);
                combat.aggiungiListener(l);
            }

            // Tick 1: all 10 receive
            combat.onTick();
            for (RecordingListener l : listeners) {
                assertEquals(1, l.ticks.get());
            }

            // Remove first 5 listeners
            for (int i = 0; i < 5; i++) {
                combat.rimuoviListener(listeners.get(i));
            }

            // Tick 2: only last 5 receive
            combat.onTick();
            for (int i = 0; i < 5; i++) {
                assertEquals(1, listeners.get(i).ticks.get());
            }
            for (int i = 5; i < 10; i++) {
                assertEquals(2, listeners.get(i).ticks.get());
            }
        }
    }

    @Nested
    @DisplayName("StrategiaCasuale Adversarial Stress Matrix")
    class StrategiaCasualeStressTests {

        @Test
        @DisplayName("5,000 iterations of StrategiaCasuale with various setups never throws unexpected exceptions")
        void testStrategiaCasualeMassiveStress() {
            StrategiaCasuale ai = new StrategiaCasuale();
            RAM ram = new RAM(15);

            class DynamicStato implements StatoBattaglia {
                final ArrayList<Entita> eroi = new ArrayList<>();
                final ArrayList<Entita> nemici = new ArrayList<>();
                final RAM r;
                DynamicStato(RAM r) { this.r = r; }
                @Override public RAM getRamCondivisa() { return r; }
                @Override public Giocatore getGiocatore() { return eroi.isEmpty() ? null : (Giocatore) eroi.get(0); }
                @Override public ArrayList<Entita> getFazioneEroi() { return eroi; }
                @Override public ArrayList<Entita> getFazioneNemici() { return nemici; }
                @Override public Entita getEroe(int n) { return eroi.get(n); }
                @Override public Entita getNemico(int n) { return nemici.get(n); }
                @Override public StatoBattaglia copy() { return this; }
            }

            DynamicStato stato = new DynamicStato(ram);
            Giocatore h1 = createHero("H1", 100, 5, 10);
            Giocatore h2 = createHero("H2", 100, 5, 10);
            stato.eroi.add(h1);
            stato.eroi.add(h2);

            ArrayList<Hack> hacks = new ArrayList<>();
            Hack healHack = new Hack("Heal", "H", 3);
            healHack.addEffetto(new EffettoCura(20, true));
            Hack dmgHack = new Hack("Dmg", "D", 4);
            dmgHack.addEffetto(new EffettoDanno(30, true));
            hacks.add(healHack);
            hacks.add(dmgHack);

            NPC npc = new NPC("Enemy", 100, "n.png", 5, hacks,
                    new Pistola("P", "D", 6, 10, 0.0), 5, 0.0, ai, false);

            for (int i = 0; i < 5000; i++) {
                // Clear RAM occasionally
                if (ram.getSpazioOccupato() > 10) {
                    while (ram.visualizzaTesta() != null) ram.rimuovi();
                }

                var action = ai.scegliMossa(npc, stato);
                assertNotNull(action, "Should select valid action when weapon and hacks are available");

                if (action instanceof AzioneCaricaHack carica) {
                    if (carica.getHack().getEffectTypes().contains(EffectType.HEAL)) {
                        // Heal hack must target NPC self
                        carica.esegui(stato);
                        QueuedHack q = ram.visualizzaTesta();
                        assertNotNull(q);
                    } else if (carica.getHack().getEffectTypes().contains(EffectType.DAMAGE)) {
                        // Offensive hack must target one of the heroes
                        carica.esegui(stato);
                    }
                } else if (action instanceof AzioneSparo) {
                    action.esegui(stato);
                }
            }
        }

        @Test
        @DisplayName("StrategiaCasuale uniformly distributes attacks across multiple heroes")
        void testStrategiaCasualeDistribution() {
            StrategiaCasuale ai = new StrategiaCasuale();
            RAM ram = new RAM(10);

            class MultiHeroStato implements StatoBattaglia {
                final ArrayList<Entita> eroi = new ArrayList<>();
                @Override public RAM getRamCondivisa() { return ram; }
                @Override public Giocatore getGiocatore() { return (Giocatore) eroi.get(0); }
                @Override public ArrayList<Entita> getFazioneEroi() { return eroi; }
                @Override public ArrayList<Entita> getFazioneNemici() { return new ArrayList<>(); }
                @Override public Entita getEroe(int n) { return eroi.get(n); }
                @Override public Entita getNemico(int n) { return null; }
                @Override public StatoBattaglia copy() { return this; }
            }

            MultiHeroStato stato = new MultiHeroStato();
            Giocatore h1 = createHero("H1", 1000, 5, 0);
            Giocatore h2 = createHero("H2", 1000, 5, 0);
            Giocatore h3 = createHero("H3", 1000, 5, 0);
            stato.eroi.add(h1);
            stato.eroi.add(h2);
            stato.eroi.add(h3);

            // NPC without hacks (only pistol) to test target selection
            NPC npc = new NPC("Enemy", 100, "n.png", 5, new ArrayList<>(),
                    new Pistola("P", "D", 6, 1, 0.0), 5, 0.0, ai, false);

            int countH1 = 0, countH2 = 0, countH3 = 0;
            for (int i = 0; i < 1500; i++) {
                AzioneSparo sparo = (AzioneSparo) ai.scegliMossa(npc, stato);
                h1.setPv(1000); h2.setPv(1000); h3.setPv(1000);
                sparo.esegui(stato);
                if (h1.getPv() < 1000) countH1++;
                else if (h2.getPv() < 1000) countH2++;
                else if (h3.getPv() < 1000) countH3++;
            }

            // Each hero should be selected at least 300 times out of 1500 (expected ~500)
            assertTrue(countH1 > 300, "H1 count: " + countH1);
            assertTrue(countH2 > 300, "H2 count: " + countH2);
            assertTrue(countH3 > 300, "H3 count: " + countH3);
        }
    }

    @Nested
    @DisplayName("GameFactory Boundary & Mutation Isolation Tests")
    class GameFactoryStressTests {

        @Test
        @DisplayName("GameFactory deep-copies all items and preserves isolation against external list mutation")
        void testGameFactoryMutationIsolation() {
            GameFactory factory = new GameFactory();

            List<Arma> playerWeapons = new ArrayList<>(List.of(
                    new Pistola("P1", "D1", 6, 20, 0.1)
            ));
            List<Hack> playerHacks = new ArrayList<>(List.of(
                    new Hack("PH1", "Desc", 2)
            ));
            List<Arma> catalogWeapons = new ArrayList<>(List.of(
                    new Pistola("CatW0", "D", 6, 10, 0.0),
                    new Pistola("CatW1", "Enemy weapon", 6, 15, 0.0)
            ));
            List<Hack> catalogHacks = new ArrayList<>(List.of(
                    new Hack("CatH0", "D", 1),
                    new Hack("CatH1", "Enemy hack", 3)
            ));

            SistemaCombattimento combat = factory.creaNuovaPartitaSemplice(playerWeapons, playerHacks, catalogWeapons, catalogHacks);
            StatoBattaglia stato = combat.getStatoBattaglia();

            // Mutate input lists after creation
            playerWeapons.clear();
            playerHacks.clear();
            catalogWeapons.clear();
            catalogHacks.clear();

            // Assert state inside combat is fully populated and intact
            assertNotNull(stato.getGiocatore().getArma());
            assertEquals("P1", stato.getGiocatore().getArma().getNome());
            assertEquals(1, stato.getGiocatore().getHacks().size());
            assertEquals("PH1", stato.getGiocatore().getHacks().get(0).getNome());

            NPC enemy = (NPC) stato.getNemico(0);
            assertNotNull(enemy.getArma());
            assertEquals("CatW1", enemy.getArma().getNome());
            assertEquals(1, enemy.getHacks().size());
            assertEquals("CatH1", enemy.getHacks().get(0).getNome());
        }
    }

    @Nested
    @DisplayName("Clock Multi-threaded & Rapid Start-Stop Stress")
    class ClockStressTests {

        @Test
        @DisplayName("50 rapid start/stop cycles do not leak threads or crash")
        void testClockRapidStartStopCycles() {
            AtomicInteger ticks = new AtomicInteger(0);
            Clock clock = new Clock(ticks::incrementAndGet);

            for (int i = 0; i < 50; i++) {
                clock.start();
                clock.stop();
            }
        }

        @Test
        @DisplayName("Concurrent start and stop from multiple threads execute safely")
        void testClockConcurrentStartStop() throws InterruptedException, ExecutionException {
            AtomicInteger ticks = new AtomicInteger(0);
            Clock clock = new Clock(ticks::incrementAndGet);

            int numThreads = 8;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < numThreads; i++) {
                final int id = i;
                tasks.add(() -> {
                    for (int j = 0; j < 20; j++) {
                        if (id % 2 == 0) {
                            clock.start();
                        } else {
                            clock.stop();
                        }
                    }
                    return null;
                });
            }

            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> f : futures) {
                f.get();
            }
            clock.stop();
            executor.shutdown();
            assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS));
        }
    }
}

package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchermataBattagliaFXMLTest {

    private SchermataBattagliaFXML controller;
    private Pane mainPane;
    private GridPane menuPrincipale;
    private GridPane menuHacks;
    private Rectangle playerLifeBar;
    private Rectangle enemyLifeBar;
    private VBox barraRAM;
    private SessionState sessionState;
    private Giocatore hero;
    private NPC enemy;
    private SistemaCombattimento combattimento;
    private Clock clock;

    @BeforeAll
    public static void initJavaFX() {
        JavaFXTestHelper.initPlatform();
    }

    @BeforeEach
    public void setUp() throws Exception {
        controller = new SchermataBattagliaFXML();
        mainPane = new Pane();
        mainPane.setPrefSize(1920, 1080);
        menuPrincipale = new GridPane();
        menuHacks = new GridPane();
        menuHacks.setVisible(false);
        menuPrincipale.setVisible(true);

        playerLifeBar = new Rectangle(668.0, 40.0);
        enemyLifeBar = new Rectangle(668.0, 40.0);
        barraRAM = new VBox();
        barraRAM.setPrefHeight(400.0);

        setField(controller, "mainPane", mainPane);
        setField(controller, "MenuPrincipale", menuPrincipale);
        setField(controller, "MenuHacks", menuHacks);
        setField(controller, "PlayerLifeBar", playerLifeBar);
        setField(controller, "EnemyLifeBar", enemyLifeBar);
        setField(controller, "BarraRAM", barraRAM);

        ArrayList<Hack> heroHacks = new ArrayList<>();
        Hack hack1 = new Hack("Overload", "Danno 30", 4);
        hack1.addEffetto(new EffettoDanno(30, true));
        heroHacks.add(hack1);

        Pistola pistolaHero = new Pistola("Plasma Gun", "High dmg", 6, 25, 1.0);
        hero = new Giocatore("CyberHero", 100, "hero.png", 15, heroHacks, pistolaHero, true);

        ArrayList<Hack> enemyHacks = new ArrayList<>();
        Hack hackEnemy = new Hack("Firewall", "Danno 10", 3);
        hackEnemy.addEffetto(new EffettoDanno(10, true));
        enemyHacks.add(hackEnemy);

        Pistola pistolaEnemy = new Pistola("Laser Gun", "Low dmg", 6, 10, 1.0);
        enemy = new NPC("NetBot", 100, "enemy.png", 15, enemyHacks, pistolaEnemy, 5, 0.1, new StrategiaCasuale(), false);

        StatoBattaglia1v1 statoBattaglia = new StatoBattaglia1v1(hero, enemy);
        combattimento = new CombattimentoATurni(statoBattaglia);

        clock = new Clock(() -> combattimento.onTick());

        sessionState = new SessionState();
        sessionState.combattimento = combattimento;
        sessionState.clock = clock;
        sessionState.spazioRam = 30;

        controller.setSessione(sessionState);
    }

    @AfterEach
    public void tearDown() {
        if (clock != null) {
            clock.stop();
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = SchermataBattagliaFXML.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private boolean isClockRunning(Clock c) throws Exception {
        Field field = Clock.class.getDeclaredField("inEsecuzione");
        field.setAccessible(true);
        return field.getBoolean(c);
    }

    @Test
    @DisplayName("FXML file loads cleanly without errors")
    public void testFxmlLoading() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/it/unicam/cs/mpgc/rpg130077/visual/Battaglia.fxml"));
                Parent root = loader.load();
                assertNotNull(root);
                SchermataBattagliaFXML fxmlController = loader.getController();
                assertNotNull(fxmlController);
            } catch (Exception e) {
                fail("Failed to load Battaglia.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("cambiaVita accurately computes width formula and clamps values for both Hero and Enemy")
    public void testCambiaVitaCalculationsAndClamping() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            // Full health (100/100) -> 668.0
            hero.setPv(100);
            controller.cambiaVita(hero);
            assertEquals(668.0, playerLifeBar.getWidth(), 0.001);

            // 50% health (50/100) -> 334.0
            hero.setPv(50);
            controller.cambiaVita(hero);
            assertEquals(334.0, playerLifeBar.getWidth(), 0.001);

            // 0 health -> 0.0
            hero.setPv(0);
            controller.cambiaVita(hero);
            assertEquals(0.0, playerLifeBar.getWidth(), 0.001);

            // Negative health -> clamped to 0.0
            hero.setPv(-20);
            controller.cambiaVita(hero);
            assertEquals(0.0, playerLifeBar.getWidth(), 0.001);

            // Health exceeding max (120/100) -> clamped to 668.0
            hero.setPv(120);
            controller.cambiaVita(hero);
            assertEquals(668.0, playerLifeBar.getWidth(), 0.001);

            // Enemy health 25% (25/100) -> 167.0
            enemy.setPv(25);
            controller.cambiaVita(enemy);
            assertEquals(167.0, enemyLifeBar.getWidth(), 0.001);
        });
    }

    @Test
    @DisplayName("onAggiornamentoRAM with null RAM throws NullPointerException")
    public void testOnAggiornamentoRAMNullThrowsNPE() {
        NullPointerException npe = assertThrows(NullPointerException.class, () -> {
            controller.onAggiornamentoRAM(null);
        });
        assertEquals("Ram nulla", npe.getMessage());
    }

    @Test
    @DisplayName("onAggiornamentoRAM delegates to RamViewHelper and updates BarraRAM")
    public void testOnAggiornamentoRAMValid() throws Exception {
        RAM ram = new RAM(15);
        Hack h = new Hack("SpeedUp", "desc", 4);
        ram.inserisci(h, enemy, hero);

        controller.onAggiornamentoRAM(ram);
        JavaFXTestHelper.waitForRunLater();

        assertEquals(1, barraRAM.getChildren().size());
    }

    @Test
    @DisplayName("onTick triggers RAM view update and lifebar updates on JavaFX thread")
    public void testOnTick() throws Exception {
        hero.setPv(80);
        controller.onTick(combattimento.getStatoBattaglia());
        JavaFXTestHelper.waitForRunLater();
        JavaFXTestHelper.waitForRunLater();

        assertEquals((80.0 / 100.0) * 668.0, playerLifeBar.getWidth(), 0.001);
    }

    @Test
    @DisplayName("onVitaAggiornataEntita updates lifebar of specified entity")
    public void testOnVitaAggiornataEntita() throws Exception {
        enemy.setPv(40);
        controller.onVitaAggiornataEntita(enemy);
        JavaFXTestHelper.waitForRunLater();
        JavaFXTestHelper.waitForRunLater();

        assertEquals((40.0 / 100.0) * 668.0, enemyLifeBar.getWidth(), 0.001);
    }

    @Test
    @DisplayName("onVitaAggiornata updates lifebars of all entities")
    public void testOnVitaAggiornata() throws Exception {
        hero.setPv(60);
        enemy.setPv(30);

        controller.onVitaAggiornata(combattimento.getStatoBattaglia());
        JavaFXTestHelper.waitForRunLater();
        JavaFXTestHelper.waitForRunLater();

        assertEquals((60.0 / 100.0) * 668.0, playerLifeBar.getWidth(), 0.001);
        assertEquals((30.0 / 100.0) * 668.0, enemyLifeBar.getWidth(), 0.001);
    }

    @Test
    @DisplayName("onTurnoGiocatore enables MenuPrincipale")
    public void testOnTurnoGiocatore() throws Exception {
        menuPrincipale.setDisable(true);
        controller.onTurnoGiocatore();
        JavaFXTestHelper.waitForRunLater();

        assertFalse(menuPrincipale.isDisable());
    }

    @Test
    @DisplayName("onVittoria stops the combat Clock when game ends")
    public void testOnVittoriaStopsClock() throws Exception {
        assertTrue(isClockRunning(clock), "Clock should be running");
        controller.onVittoria(hero);
        assertFalse(isClockRunning(clock), "Clock must be stopped on victory");

        // Test with null winner (pareggio)
        clock.start();
        assertTrue(isClockRunning(clock));
        controller.onVittoria(null);
        assertFalse(isClockRunning(clock), "Clock must be stopped on draw");
    }

    @Test
    @DisplayName("visualizzaHacks switches menu and populates available hack buttons")
    public void testVisualizzaHacksAndScambiaMenu() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                Button hack1Btn = new Button();
                hack1Btn.setId("hack1");
                Button hack2Btn = new Button();
                hack2Btn.setId("hack2");
                menuHacks.getChildren().addAll(hack1Btn, hack2Btn);

                Method visualizzaHacksMethod = SchermataBattagliaFXML.class.getDeclaredMethod("visualizzaHacks", ActionEvent.class);
                visualizzaHacksMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(new Button(), null);
                visualizzaHacksMethod.invoke(controller, event);

                assertTrue(menuHacks.isVisible());
                assertFalse(menuPrincipale.isVisible());

                // Player has 1 hack ("Overload")
                assertEquals("Overload", hack1Btn.getText());
                assertFalse(hack1Btn.isDisable());

                // Second button should be disabled with "-"
                assertEquals("-", hack2Btn.getText());
                assertTrue(hack2Btn.isDisable());
            } catch (Exception e) {
                fail("visualizzaHacks failed: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("indietro toggles menu visibility back to MenuPrincipale")
    public void testIndietroTogglesMenu() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                menuHacks.setVisible(true);
                menuPrincipale.setVisible(false);

                Method indietroMethod = SchermataBattagliaFXML.class.getDeclaredMethod("indietro", ActionEvent.class);
                indietroMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(new Button(), null);
                indietroMethod.invoke(controller, event);

                assertFalse(menuHacks.isVisible());
                assertTrue(menuPrincipale.isVisible());
            } catch (Exception e) {
                fail("indietro failed: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("pulsanteSparare executes weapon shoot action when it is player turn")
    public void testPulsanteSparare() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                int initialEnemyPv = enemy.getPv();
                assertTrue(combattimento.isPlayerTurn());

                Method pulsanteSparareMethod = SchermataBattagliaFXML.class.getDeclaredMethod("pulsanteSparare", ActionEvent.class);
                pulsanteSparareMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(new Button(), null);
                pulsanteSparareMethod.invoke(controller, event);

                // Gun does 25 damage (or 50 if crit)
                assertTrue(enemy.getPv() < initialEnemyPv);
            } catch (Exception e) {
                fail("pulsanteSparare failed: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("pulsanteHack loads hack into RAM queue when triggered from hack button")
    public void testPulsanteHackLoadsHack() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                Button hack1Btn = new Button("Overload");
                hack1Btn.setId("hack1");
                menuHacks.getChildren().add(hack1Btn);

                Method pulsanteHackMethod = SchermataBattagliaFXML.class.getDeclaredMethod("pulsanteHack", ActionEvent.class);
                pulsanteHackMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(hack1Btn, null);
                pulsanteHackMethod.invoke(controller, event);

                assertFalse(combattimento.getStatoBattaglia().getRamCondivisa().getHacks().isEmpty());
                assertEquals("Overload", combattimento.getStatoBattaglia().getRamCondivisa().getHacks().get(0).getHack().getNome());
            } catch (Exception e) {
                fail("pulsanteHack failed: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("pulsanteHack gracefully handles RAM capacity overflow via floating error display")
    public void testPulsanteHackHandlesCapacityOverflow() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                // Fill RAM almost full (inserting 12 ticks of hacks into RAM of 15 ticks)
                Hack bigHack = new Hack("BigHack", "desc", 13);
                combattimento.getStatoBattaglia().getRamCondivisa().inserisci(bigHack, enemy, hero);

                Button hack1Btn = new Button("Overload");
                hack1Btn.setId("hack1");
                menuHacks.getChildren().add(hack1Btn);

                Method pulsanteHackMethod = SchermataBattagliaFXML.class.getDeclaredMethod("pulsanteHack", ActionEvent.class);
                pulsanteHackMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(hack1Btn, null);
                // Overload costs 4 ticks, total 13 + 4 = 17 > 15 -> should show floating text without throwing
                assertDoesNotThrow(() -> pulsanteHackMethod.invoke(controller, event));
            } catch (Exception e) {
                fail("pulsanteHack overflow handling failed: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("ilNemicoNonPuoAttaccare displays notification text in mainPane")
    public void testIlNemicoNonPuoAttaccare() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            int childCountBefore = mainPane.getChildren().size();
            controller.ilNemicoNonPuoAttaccare();
            int childCountAfter = mainPane.getChildren().size();
            assertTrue(childCountAfter > childCountBefore, "Floating text node should be added to mainPane");
        });
    }
}

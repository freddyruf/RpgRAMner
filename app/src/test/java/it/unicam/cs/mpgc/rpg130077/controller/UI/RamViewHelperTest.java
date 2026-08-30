package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RamViewHelperTest {

    private VBox barraRAM;
    private RAM ram;
    private ArrayList<Entita> eroi;
    private Giocatore hero;
    private NPC enemy;

    @BeforeAll
    public static void initJavaFX() {
        JavaFXTestHelper.initPlatform();
    }

    @BeforeEach
    public void setUp() {
        barraRAM = new VBox();
        barraRAM.setPrefHeight(400.0);
        ram = new RAM(15);
        eroi = new ArrayList<>();
        hero = new Giocatore("Hero", 100, "hero.png", 15, new ArrayList<>(),
                new Pistola("Pistola", "Arma standard", 6, 10, 0.2), true);
        enemy = new NPC("Enemy", 100, "enemy.png", 15, new ArrayList<>(),
                new Pistola("PistolaN", "Arma nemica", 6, 10, 0.2), 5, 0.1, new StrategiaCasuale(), false);
        eroi.add(hero);
    }

    @Test
    @DisplayName("Empty RAM results in cleared VBox with proper padding and spacing")
    public void testDisegnaBarraVuota() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            RamViewHelper.disegnaBarra(barraRAM, ram, eroi);
        });
        JavaFXTestHelper.waitForRunLater();

        assertTrue(barraRAM.getChildren().isEmpty(), "VBox should have no children when RAM is empty");
        assertEquals(5.0, barraRAM.getSpacing(), 0.001);
        assertEquals(new Insets(12.5, 12.5, 12.5, 12.5), barraRAM.getPadding());
    }

    @Test
    @DisplayName("Single hero hack renders with White rectangle and Black text")
    public void testDisegnaBarraSingoloHackEroe() throws Exception {
        Hack hack = new Hack("Overload", "Danno massiccio", 5);
        ram.inserisci(hack, enemy, hero);

        JavaFXTestHelper.runOnFxThread(() -> {
            RamViewHelper.disegnaBarra(barraRAM, ram, eroi);
        });
        JavaFXTestHelper.waitForRunLater();

        assertEquals(1, barraRAM.getChildren().size());
        Node child = barraRAM.getChildren().get(0);
        assertInstanceOf(StackPane.class, child);

        StackPane blocco = (StackPane) child;
        assertEquals(2, blocco.getChildren().size());

        Rectangle rect = (Rectangle) blocco.getChildren().get(0);
        Text text = (Text) blocco.getChildren().get(1);

        assertEquals(110.0, rect.getWidth(), 0.001);
        assertEquals(Color.WHITE, rect.getFill(), "Hero hack rectangle should be WHITE");
        assertEquals(Color.BLACK, text.getFill(), "Hero hack text should be BLACK");
        assertEquals("Overload", text.getText());

        // Calculation: altezzaUtile = 400 - 25 - 0 = 375. Proporzione = 5/15 = 1/3. Height = 125.0
        double expectedHeight = (5.0 / 15.0) * (400.0 - 25.0);
        assertEquals(expectedHeight, rect.getHeight(), 0.001);
    }

    @Test
    @DisplayName("Single enemy hack renders with Black rectangle and White text")
    public void testDisegnaBarraSingoloHackNemico() throws Exception {
        Hack hack = new Hack("CyberVirus", "Infezione nemica", 6);
        ram.inserisci(hack, hero, enemy);

        JavaFXTestHelper.runOnFxThread(() -> {
            RamViewHelper.disegnaBarra(barraRAM, ram, eroi);
        });
        JavaFXTestHelper.waitForRunLater();

        assertEquals(1, barraRAM.getChildren().size());
        StackPane blocco = (StackPane) barraRAM.getChildren().get(0);
        Rectangle rect = (Rectangle) blocco.getChildren().get(0);
        Text text = (Text) blocco.getChildren().get(1);

        assertEquals(Color.BLACK, rect.getFill(), "Enemy hack rectangle should be BLACK");
        assertEquals(Color.WHITE, text.getFill(), "Enemy hack text should be WHITE");
        assertEquals("CyberVirus", text.getText());

        double expectedHeight = (6.0 / 15.0) * (400.0 - 25.0);
        assertEquals(expectedHeight, rect.getHeight(), 0.001);
    }

    @Test
    @DisplayName("Multiple hacks calculate proportional heights taking spacing gaps into account")
    public void testCalcoloAltezzaProporzionaleMultipleHacks() throws Exception {
        barraRAM.setPrefHeight(500.0);
        ram = new RAM(20);

        Hack h1 = new Hack("Hack1", "desc1", 4);
        Hack h2 = new Hack("Hack2", "desc2", 6);
        Hack h3 = new Hack("Hack3", "desc3", 10);

        ram.inserisci(h1, enemy, hero);
        ram.inserisci(h2, hero, enemy);
        ram.inserisci(h3, enemy, hero);

        JavaFXTestHelper.runOnFxThread(() -> {
            RamViewHelper.disegnaBarra(barraRAM, ram, eroi);
        });
        JavaFXTestHelper.waitForRunLater();

        assertEquals(3, barraRAM.getChildren().size());

        // altezzaTotale = 500.0, padding = 12.5*2 = 25.0, gaps = (3 - 1) * 5.0 = 10.0
        // altezzaUtile = 500.0 - 25.0 - 10.0 = 465.0
        // spazioMassimo = 20
        double altezzaUtile = 465.0;

        StackPane b1 = (StackPane) barraRAM.getChildren().get(0);
        StackPane b2 = (StackPane) barraRAM.getChildren().get(1);
        StackPane b3 = (StackPane) barraRAM.getChildren().get(2);

        Rectangle r1 = (Rectangle) b1.getChildren().get(0);
        Rectangle r2 = (Rectangle) b2.getChildren().get(0);
        Rectangle r3 = (Rectangle) b3.getChildren().get(0);

        assertEquals((4.0 / 20.0) * altezzaUtile, r1.getHeight(), 0.001);
        assertEquals((6.0 / 20.0) * altezzaUtile, r2.getHeight(), 0.001);
        assertEquals((10.0 / 20.0) * altezzaUtile, r3.getHeight(), 0.001);

        // Verify color mapping order: Hero (White), Enemy (Black), Hero (White)
        assertEquals(Color.WHITE, r1.getFill());
        assertEquals(Color.BLACK, r2.getFill());
        assertEquals(Color.WHITE, r3.getFill());
    }

    @Test
    @DisplayName("Boundary: Full capacity hack gets full usable height")
    public void testBoundaryFullCapacityRatio() throws Exception {
        barraRAM.setPrefHeight(300.0);
        ram = new RAM(10);
        Hack hack = new Hack("MegaHack", "full", 10);
        ram.inserisci(hack, enemy, hero);

        JavaFXTestHelper.runOnFxThread(() -> {
            RamViewHelper.disegnaBarra(barraRAM, ram, eroi);
        });
        JavaFXTestHelper.waitForRunLater();

        StackPane blocco = (StackPane) barraRAM.getChildren().get(0);
        Rectangle rect = (Rectangle) blocco.getChildren().get(0);

        // altezzaUtile = 300.0 - 25.0 = 275.0, proporzione = 10 / 10 = 1.0
        assertEquals(275.0, rect.getHeight(), 0.001);
    }

    @Test
    @DisplayName("Boundary: Zero remaining ticks clamps height to 0.0")
    public void testBoundaryZeroTicksClamping() throws Exception {
        barraRAM.setPrefHeight(300.0);
        ram = new RAM(10);
        Hack hack = new Hack("ZeroHack", "zero", 5);
        ram.inserisci(hack, enemy, hero);
        ram.visualizzaTesta().setTickInCoda(0);

        JavaFXTestHelper.runOnFxThread(() -> {
            RamViewHelper.disegnaBarra(barraRAM, ram, eroi);
        });
        JavaFXTestHelper.waitForRunLater();

        StackPane blocco = (StackPane) barraRAM.getChildren().get(0);
        Rectangle rect = (Rectangle) blocco.getChildren().get(0);

        assertEquals(0.0, rect.getHeight(), 0.001);
    }

    @Test
    @DisplayName("Redrawing clears previous nodes and applies updated queue")
    public void testRedrawingClearsPreviousNodes() throws Exception {
        Hack h1 = new Hack("H1", "d1", 5);
        ram.inserisci(h1, enemy, hero);

        JavaFXTestHelper.runOnFxThread(() -> {
            RamViewHelper.disegnaBarra(barraRAM, ram, eroi);
        });
        JavaFXTestHelper.waitForRunLater();
        assertEquals(1, barraRAM.getChildren().size());

        // Empty RAM queue
        while (ram.visualizzaTesta() != null) {
            ram.rimuovi();
        }

        JavaFXTestHelper.runOnFxThread(() -> {
            RamViewHelper.disegnaBarra(barraRAM, ram, eroi);
        });
        JavaFXTestHelper.waitForRunLater();
        assertEquals(0, barraRAM.getChildren().size());
    }
}

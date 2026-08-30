package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SchermataGenericaTest {

    private static class DummySchermata extends SchermataGenericaJavaFX {
        public SessionState getSessionState() {
            return this.sessionState;
        }

        public SchermataGenerica invokeCaricaSchermata(String path, Stage stage) {
            return caricaSchermata(path, stage);
        }

        public SchermataGenerica invokeCaricaSchermata(String path, ActionEvent event) {
            return caricaSchermata(path, event);
        }
    }

    @BeforeAll
    public static void initJavaFX() {
        JavaFXTestHelper.initPlatform();
    }

    @Test
    @DisplayName("setSessione sets and retrieves SessionState reference")
    public void testSetSessione() {
        DummySchermata schermata = new DummySchermata();
        SessionState session = new SessionState();
        schermata.setSessione(session);

        assertSame(session, schermata.getSessionState());
    }

    @Test
    @DisplayName("setSpazioRam mutates sessionState.spazioRam")
    public void testSetSpazioRam() {
        DummySchermata schermata = new DummySchermata();
        SessionState session = new SessionState();
        schermata.setSessione(session);

        schermata.setSpazioRam(32);
        assertEquals(32, session.spazioRam);
    }

    @Test
    @DisplayName("setSistemaCombattimento mutates sessionState.combattimento")
    public void testSetSistemaCombattimento() {
        DummySchermata schermata = new DummySchermata();
        SessionState session = new SessionState();
        schermata.setSessione(session);

        Giocatore hero = new Giocatore("H", 100, "h.png", 10, new ArrayList<>(),
                new Pistola("P", "D", 6, 10, 0.1), true);
        NPC enemy = new NPC("N", 100, "n.png", 10, new ArrayList<>(),
                new Pistola("P2", "D2", 6, 10, 0.1), 5, 0.1, new StrategiaCasuale(), false);
        SistemaCombattimento dummyCombat = new CombattimentoATurni(new StatoBattaglia1v1(hero, enemy));
        schermata.setSistemaCombattimento(dummyCombat);
        assertSame(dummyCombat, session.combattimento);
    }

    @Test
    @DisplayName("setClock mutates sessionState.clock")
    public void testSetClock() {
        DummySchermata schermata = new DummySchermata();
        SessionState session = new SessionState();
        schermata.setSessione(session);

        Clock clock = new Clock(() -> {});
        schermata.setClock(clock);
        assertSame(clock, session.clock);
    }

    @Test
    @DisplayName("caricaSchermata throws RuntimeException on non-existent FXML path")
    public void testCaricaSchermataPercorsoInesistente() throws Exception {
        DummySchermata schermata = new DummySchermata();
        SessionState session = new SessionState();
        schermata.setSessione(session);

        JavaFXTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                assertThrows(RuntimeException.class, () -> {
                    schermata.invokeCaricaSchermata("/percorso/totalmente/inesistente.fxml", stage);
                });
            } finally {
                stage.close();
            }
        });
    }

    @Test
    @DisplayName("caricaSchermata loads valid FXML, assigns Scene to Stage, and propagates SessionState")
    public void testCaricaSchermataValida() throws Exception {
        DummySchermata schermata = new DummySchermata();
        SessionState session = new SessionState();
        session.spazioRam = 25;
        schermata.setSessione(session);

        JavaFXTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                SchermataGenerica controller = schermata.invokeCaricaSchermata(
                        "/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml", stage);

                assertNotNull(controller, "Controller should be instantiated from FXML");
                assertInstanceOf(SchermataInizialeFXML.class, controller);
                assertSame(session, controller.sessionState, "SessionState must be passed to the loaded controller");
                assertNotNull(stage.getScene(), "Stage should have a scene set");
                assertNotNull(stage.getScene().getRoot(), "Scene should have a root");
            } finally {
                stage.close();
            }
        });
    }

    @Test
    @DisplayName("caricaSchermata with ActionEvent extracts stage from event source and loads scene")
    public void testCaricaSchermataDaActionEvent() throws Exception {
        DummySchermata schermata = new DummySchermata();
        SessionState session = new SessionState();
        schermata.setSessione(session);

        JavaFXTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                Button sourceButton = new Button("Click me");
                StackPane root = new StackPane(sourceButton);
                Scene scene = new Scene(root, 400, 300);
                stage.setScene(scene);
                stage.show();

                ActionEvent event = new ActionEvent(sourceButton, null);
                SchermataGenerica controller = schermata.invokeCaricaSchermata(
                        "/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml", event);

                assertNotNull(controller);
                assertSame(session, controller.sessionState);
                assertNotNull(stage.getScene());
                assertNotSame(scene, stage.getScene(), "Stage scene should be updated to the new loaded scene");
            } finally {
                stage.close();
            }
        });
    }
}

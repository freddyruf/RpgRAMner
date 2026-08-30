package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SceltaArmamentoFXMLTest {

    private static class MockCaricatoreCatalogo implements CaricatoreCatalogo {
        private final ArrayList<Arma> armi = new ArrayList<>();
        private final ArrayList<Hack> hacks = new ArrayList<>();

        @Override
        public ArrayList<Arma> caricamentoCatalogoArmi() {
            return new ArrayList<>(armi);
        }

        @Override
        public ArrayList<Hack> caricamentoCatalogoHacks() {
            return new ArrayList<>(hacks);
        }
    }

    private static class MockPersistenzaArmamento implements PersistenzaArmamento {
        private final ArrayList<Arma> armiSalvate = new ArrayList<>();
        private final ArrayList<Hack> hacksSalvati = new ArrayList<>();
        private final MockCaricatoreCatalogo catalogo = new MockCaricatoreCatalogo();
        private boolean savedCalled = false;

        @Override
        public ArrayList<Arma> getArmi() {
            return armiSalvate;
        }

        @Override
        public ArrayList<Hack> getHacks() {
            return hacksSalvati;
        }

        @Override
        public void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
            savedCalled = true;
            this.armiSalvate.clear();
            this.armiSalvate.addAll(armi);
            this.hacksSalvati.clear();
            this.hacksSalvati.addAll(hacks);
        }

        @Override
        public CaricatoreCatalogo getCatalogo() {
            return catalogo;
        }
    }

    @BeforeAll
    public static void initJavaFX() {
        JavaFXTestHelper.initPlatform();
    }

    @Test
    @DisplayName("FXML view loads successfully with initial components and controller")
    public void testFxmlLoading() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/it/unicam/cs/mpgc/rpg130077/visual/SceltaArmamento.fxml"));
                Parent root = loader.load();
                assertNotNull(root);
                SceltaArmamentoFXML controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load SceltaArmamento.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("setSessione throws NullPointerException when GestoreArmamento is null")
    public void testSetSessioneNullGestoreThrowsNPE() {
        SceltaArmamentoFXML controller = new SceltaArmamentoFXML();
        controller.mainPane = new Pane();
        SessionState session = new SessionState();
        session.gestoreArmamento = null;

        NullPointerException npe = assertThrows(NullPointerException.class, () -> {
            controller.setSessione(session);
        });
        assertEquals("GestoreArmamento non trovata", npe.getMessage());
    }

    @Test
    @DisplayName("popolaMenuDaCatalogo populates weapon and hack menu buttons from GestoreArmamento")
    public void testPopolaMenuDaCatalogo() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            SceltaArmamentoFXML controller = new SceltaArmamentoFXML();
            Pane pane = new Pane();
            controller.mainPane = pane;

            MenuButton mbArma = new MenuButton("Arma");
            mbArma.setId("MenuButtonArma");
            MenuButton mbHack1 = new MenuButton("HACK 1");
            mbHack1.setId("MenuButton1");
            MenuButton mbHack2 = new MenuButton("HACK 2");
            mbHack2.setId("MenuButton2");

            pane.getChildren().addAll(mbArma, mbHack1, mbHack2);

            MockPersistenzaArmamento mockPersistenza = new MockPersistenzaArmamento();
            mockPersistenza.catalogo.armi.add(new Pistola("Pistola Plasma", "Arma al plasma", 6, 20, 0.5));
            mockPersistenza.catalogo.hacks.add(new Hack("HackShield", "Scudo protettivo", 3));
            mockPersistenza.catalogo.hacks.add(new Hack("HackDrain", "Drena energia", 4));

            GestoreArmamento gestore = new GestoreArmamento(mockPersistenza);
            SessionState session = new SessionState();
            session.gestoreArmamento = gestore;
            controller.sessionState = session;

            controller.popolaMenuDaCatalogo();

            assertEquals(1, mbArma.getItems().size());
            assertEquals("Pistola Plasma", mbArma.getItems().get(0).getText());

            assertEquals(2, mbHack1.getItems().size());
            assertEquals("HackShield", mbHack1.getItems().get(0).getText());
            assertEquals("HackDrain", mbHack1.getItems().get(1).getText());
        });
    }

    @Test
    @DisplayName("caricaArmamento loads previously saved weapons and hacks and updates labels")
    public void testCaricaArmamento() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            SceltaArmamentoFXML controller = new SceltaArmamentoFXML();
            Pane pane = new Pane();
            controller.mainPane = pane;

            MenuButton mbArma = new MenuButton("Arma");
            mbArma.setId("MenuButtonArma");
            Label labelArma = new Label("placeholder");
            labelArma.setId("LabelArma");

            MenuButton mbHack1 = new MenuButton("HACK 1");
            mbHack1.setId("MenuButton1");
            Label label1 = new Label("placeholder");
            label1.setId("Label1");

            pane.getChildren().addAll(mbArma, labelArma, mbHack1, label1);

            MockPersistenzaArmamento mockPersistenza = new MockPersistenzaArmamento();
            Pistola savedPistola = new Pistola("Pistola Base", "Pistola di ordinanza", 6, 15, 0.9);
            Hack savedHack = new Hack("IniezioneRAM", "Aumenta la memoria RAM", 2);

            mockPersistenza.catalogo.armi.add(savedPistola);
            mockPersistenza.catalogo.hacks.add(savedHack);

            mockPersistenza.armiSalvate.add(savedPistola);
            mockPersistenza.hacksSalvati.add(savedHack);

            GestoreArmamento gestore = new GestoreArmamento(mockPersistenza);
            SessionState session = new SessionState();
            session.gestoreArmamento = gestore;
            controller.sessionState = session;

            controller.caricaArmamento();

            assertEquals("Pistola Base", mbArma.getText());
            assertEquals("Pistola di ordinanza", labelArma.getText());

            assertEquals("IniezioneRAM", mbHack1.getText());
            assertEquals("Aumenta la memoria RAM", label1.getText());
        });
    }

    @Test
    @DisplayName("isPlaceholder correctly identifies placeholders vs valid item names")
    public void testIsPlaceholder() throws Exception {
        SceltaArmamentoFXML controller = new SceltaArmamentoFXML();
        Method isPlaceholderMethod = SceltaArmamentoFXML.class.getDeclaredMethod("isPlaceholder", String.class);
        isPlaceholderMethod.setAccessible(true);

        assertTrue((Boolean) isPlaceholderMethod.invoke(controller, (Object) null));
        assertTrue((Boolean) isPlaceholderMethod.invoke(controller, "Arma"));
        assertTrue((Boolean) isPlaceholderMethod.invoke(controller, "HACK 1"));
        assertTrue((Boolean) isPlaceholderMethod.invoke(controller, "HACK 2"));

        assertFalse((Boolean) isPlaceholderMethod.invoke(controller, "Pistola"));
        assertFalse((Boolean) isPlaceholderMethod.invoke(controller, "Mitragliatrice"));
        assertFalse((Boolean) isPlaceholderMethod.invoke(controller, "Firewall"));
    }

    @Test
    @DisplayName("PulsanteEsci with incomplete placeholders does not save or navigate")
    public void testPulsanteEsciIncompletoNonSalva() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                SceltaArmamentoFXML controller = new SceltaArmamentoFXML();
                Pane pane = new Pane();
                controller.mainPane = pane;

                MenuButton mbArma = new MenuButton("Arma"); // Placeholder!
                mbArma.setId("MenuButtonArma");
                MenuButton mbHack1 = new MenuButton("Firewall");
                mbHack1.setId("MenuButton1");

                javafx.scene.control.Button btnEsci = new javafx.scene.control.Button("Esci");
                pane.getChildren().addAll(mbArma, mbHack1, btnEsci);

                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.show();

                MockPersistenzaArmamento mockPersistenza = new MockPersistenzaArmamento();
                GestoreArmamento gestore = new GestoreArmamento(mockPersistenza);
                SessionState session = new SessionState();
                session.gestoreArmamento = gestore;
                controller.sessionState = session;

                Method pulsanteEsciMethod = SceltaArmamentoFXML.class.getDeclaredMethod("PulsanteEsci", ActionEvent.class);
                pulsanteEsciMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(btnEsci, null);
                pulsanteEsciMethod.invoke(controller, event);

                assertFalse(mockPersistenza.savedCalled, "Save should not be called when loadout is incomplete");
                assertSame(pane, stage.getScene().getRoot(), "Scene root should not change");
            } finally {
                stage.close();
            }
        });
    }

    @Test
    @DisplayName("PulsanteEsci with complete choices saves configuration and navigates to SchermataIniziale")
    public void testPulsanteEsciCompletoSalvaENaviga() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            try {
                SceltaArmamentoFXML controller = new SceltaArmamentoFXML();
                Pane pane = new Pane();
                controller.mainPane = pane;

                MenuButton mbArma = new MenuButton("Pistola Base");
                mbArma.setId("MenuButtonArma");
                MenuButton mbHack1 = new MenuButton("IniezioneRAM");
                mbHack1.setId("MenuButton1");

                javafx.scene.control.Button btnEsci = new javafx.scene.control.Button("Esci");
                pane.getChildren().addAll(mbArma, mbHack1, btnEsci);

                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.show();

                MockPersistenzaArmamento mockPersistenza = new MockPersistenzaArmamento();
                Pistola savedPistola = new Pistola("Pistola Base", "Pistola di ordinanza", 6, 15, 0.9);
                Hack savedHack = new Hack("IniezioneRAM", "Aumenta la memoria RAM", 2);
                mockPersistenza.catalogo.armi.add(savedPistola);
                mockPersistenza.catalogo.hacks.add(savedHack);

                GestoreArmamento gestore = new GestoreArmamento(mockPersistenza);
                SessionState session = new SessionState();
                session.gestoreArmamento = gestore;
                controller.sessionState = session;

                Method pulsanteEsciMethod = SceltaArmamentoFXML.class.getDeclaredMethod("PulsanteEsci", ActionEvent.class);
                pulsanteEsciMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(btnEsci, null);
                pulsanteEsciMethod.invoke(controller, event);

                assertTrue(mockPersistenza.savedCalled, "Save must be called when all items are selected");
                assertNotNull(stage.getScene());
                assertNotSame(pane, stage.getScene().getRoot(), "Scene root should transition to SchermataIniziale");
            } finally {
                stage.close();
            }
        });
    }

    @Test
    @DisplayName("getAllMenuButtonsFromThis returns only MenuButtons in the mainPane")
    public void testGetAllMenuButtonsFromThis() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            SceltaArmamentoFXML controller = new SceltaArmamentoFXML();
            Pane pane = new Pane();
            controller.mainPane = pane;

            MenuButton mb1 = new MenuButton("B1");
            MenuButton mb2 = new MenuButton("B2");
            Label lbl = new Label("L1");
            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle();

            pane.getChildren().addAll(mb1, lbl, mb2, rect);

            ArrayList<MenuButton> result = controller.getAllMenuButtonsFromThis();
            assertEquals(2, result.size());
            assertTrue(result.contains(mb1));
            assertTrue(result.contains(mb2));
            assertFalse(result.contains(lbl));
        });
    }
}

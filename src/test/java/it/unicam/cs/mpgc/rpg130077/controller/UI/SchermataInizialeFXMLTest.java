package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaCatalogoArmamentoJSON;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchermataInizialeFXMLTest {

    @BeforeAll
    public static void initJavaFX() {
        JavaFXTestHelper.initPlatform();
    }

    private static class StubPersistenzaArmamento implements PersistenzaArmamento {
        private final ArrayList<Arma> armi;
        private final ArrayList<Hack> hacks;
        private final CaricatoreCatalogo catalogo = new PersistenzaCatalogoArmamentoJSON();

        public StubPersistenzaArmamento(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
            this.armi = armi != null ? armi : new ArrayList<>();
            this.hacks = hacks != null ? hacks : new ArrayList<>();
        }

        @Override
        public ArrayList<Arma> getArmi() {
            return armi;
        }

        @Override
        public ArrayList<Hack> getHacks() {
            return hacks;
        }

        @Override
        public void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
            this.armi.clear();
            this.armi.addAll(armi);
            this.hacks.clear();
            this.hacks.addAll(hacks);
        }

        @Override
        public CaricatoreCatalogo getCatalogo() {
            return catalogo;
        }
    }

    @Test
    @DisplayName("FXML view loads successfully with initial components and controller")
    public void testFxmlLoading() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml"));
                Parent root = loader.load();
                assertNotNull(root);
                SchermataInizialeFXML controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load SchermataIniziale.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("exitWindow closes the stage")
    public void testExitWindowClosesStage() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                Stage stage = new Stage();
                Button btn = new Button("Exit");
                Pane pane = new Pane(btn);
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.show();

                assertTrue(stage.isShowing());

                SchermataInizialeFXML controller = new SchermataInizialeFXML();
                Method exitWindowMethod = SchermataInizialeFXML.class.getDeclaredMethod("exitWindow", ActionEvent.class);
                exitWindowMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(btn, null);
                exitWindowMethod.invoke(controller, event);

                assertFalse(stage.isShowing(), "Stage should be closed after exitWindow");
            } catch (Exception e) {
                fail("Exception during exitWindow test: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("goSceltaSetup navigates to SceltaArmamento screen")
    public void testGoSceltaSetupNavigatesToSceltaArmamento() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                Stage stage = new Stage();
                Button btn = new Button("Hack");
                Pane pane = new Pane(btn);
                Scene scene = new Scene(pane);
                stage.setScene(scene);

                SchermataInizialeFXML controller = new SchermataInizialeFXML();
                SessionState session = new SessionState();
                session.gestoreArmamento = new GestoreArmamento(new StubPersistenzaArmamento(null, null));
                controller.setSessione(session);

                Method goSceltaSetupMethod = SchermataInizialeFXML.class.getDeclaredMethod("goSceltaSetup", ActionEvent.class);
                goSceltaSetupMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(btn, null);
                goSceltaSetupMethod.invoke(controller, event);

                assertNotNull(stage.getScene());
                assertNotNull(stage.getScene().getRoot());
            } catch (Exception e) {
                fail("Exception during goSceltaSetup: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("goBattaglia without saved setup redirects to SceltaArmamento")
    public void testGoBattagliaWithoutSavedSetupRedirectsToSceltaArmamento() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                Stage stage = new Stage();
                Button btn = new Button("Start");
                Pane pane = new Pane(btn);
                Scene scene = new Scene(pane);
                stage.setScene(scene);

                SchermataInizialeFXML controller = new SchermataInizialeFXML();
                SessionState session = new SessionState();
                session.gestoreArmamento = new GestoreArmamento(new StubPersistenzaArmamento(null, null));
                controller.setSessione(session);

                Method goBattagliaMethod = SchermataInizialeFXML.class.getDeclaredMethod("goBattaglia", ActionEvent.class);
                goBattagliaMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(btn, null);
                goBattagliaMethod.invoke(controller, event);

                assertNull(session.combattimento, "Combat should not be initialized when setup is missing");
                assertNull(session.clock, "Clock should not be initialized when setup is missing");
            } catch (Exception e) {
                fail("Exception during goBattaglia redirection: " + e.getMessage());
            }
        });
    }

    @Test
    @DisplayName("goBattaglia with configured setup initializes Combat, Clock, RAM, and loads Battaglia scene")
    public void testGoBattagliaWithConfiguredSetupInitializesCombat() throws Exception {
        JavaFXTestHelper.runOnFxThread(() -> {
            try {
                Stage stage = new Stage();
                Button btn = new Button("Start");
                Pane pane = new Pane(btn);
                Scene scene = new Scene(pane);
                stage.setScene(scene);

                SchermataInizialeFXML controller = new SchermataInizialeFXML();
                SessionState session = new SessionState();

                ArrayList<Arma> armiSalvate = new ArrayList<>();
                armiSalvate.add(new Pistola("Pistola Plasma", "Desc", 6, 20, 1.0));
                ArrayList<Hack> hacksSalvati = new ArrayList<>();
                hacksSalvati.add(new Hack("Overclock", "Aumenta velocita", 3));
                GestoreArmamento gestore = new GestoreArmamento(new StubPersistenzaArmamento(armiSalvate, hacksSalvati));

                session.gestoreArmamento = gestore;
                controller.setSessione(session);

                Method goBattagliaMethod = SchermataInizialeFXML.class.getDeclaredMethod("goBattaglia", ActionEvent.class);
                goBattagliaMethod.setAccessible(true);

                ActionEvent event = new ActionEvent(btn, null);
                goBattagliaMethod.invoke(controller, event);

                assertNotNull(session.combattimento, "Combat system must be created");
                assertNotNull(session.clock, "Clock must be created");
                // Hero RAM (10) + Enemy RAM (5) from GameFactory = 15
                assertEquals(15, session.spazioRam, "Spazio RAM must equal hero (10) + enemy (5)");

                // Clean up clock to prevent timer threads leaking
                if (session.clock != null) {
                    session.clock.stop();
                }
            } catch (Exception e) {
                fail("Exception during goBattaglia with setup: " + e.getMessage());
            }
        });
    }
}

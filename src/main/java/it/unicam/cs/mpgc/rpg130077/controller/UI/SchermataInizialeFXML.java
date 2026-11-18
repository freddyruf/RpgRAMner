package it.unicam.cs.mpgc.rpg130077.controller.UI;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;


public class SchermataInizialeFXML extends SchermataGenericaJavaFX {


    @FXML
    private void exitWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @FXML
    private void goSceltaSetup(ActionEvent event) {
        caricaSchermata("/it/unicam/cs/mpgc/rpg130077/visual/SceltaArmamento.fxml", event);
    }

    @FXML
    private void goBattaglia(ActionEvent event) {
        // Avvia la battaglia solo se l'utente ha armamento salvato valido
        if (!sessionState.getGestoreEquipaggiamento().hasConfigurazioneSalvata()) {
            goSceltaSetup(event);
        }
        else{
            if (sessionState.getGestorePartita() == null) {
                throw new IllegalStateException("GestorePartita non configurato nella sessione");
            }
            sessionState.getGestorePartita().avviaPartita(sessionState.getCombattimento());
            SistemaCombattimento combattimento = sessionState.getCombattimento();
            
            // Aggiorna lo stato della sessione per la schermata di battaglia
            sessionState.setCombattimento(combattimento);
            sessionState.setClock(new Clock(() -> combattimento.onTick()));

            caricaSchermata("/it/unicam/cs/mpgc/rpg130077/visual/Battaglia.fxml", event);
        }

    }
    }

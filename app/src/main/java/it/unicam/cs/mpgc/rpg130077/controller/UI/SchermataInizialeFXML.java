package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class SchermataInizialeFXML extends SchermataGenerica {


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
        boolean setupConfigurato = !persistenzaArmamento.getArmi().isEmpty() && !persistenzaArmamento.getHacks().isEmpty();
        if (!setupConfigurato) {
            goSceltaSetup(event);
        }
        else{
            caricaSchermata("/it/unicam/cs/mpgc/rpg130077/visual/Battaglia.fxml", event);
        }

    }
    }


package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class SchermataInizialeFXML extends SchermataGenerica {

    private boolean armamentoCaricato=false;


    @FXML
    private void ExitWindow(ActionEvent event) {
        // Chiude semplicemente l'applicazione
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @FXML
    private void GoSceltaHack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/SceltaArmamento.fxml"));
            Parent nuovaSchermata = loader.load();

            SchermataGenerica controller = loader.getController();
            controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));
            armamentoCaricato=true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void GoBattaglia(ActionEvent event) {
        if(armamentoCaricato){
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/Battaglia.fxml"));
                Parent nuovaSchermata = loader.load();

                SchermataBattagliaFXML controller = loader.getController();
                controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);
                controller.setSpazioRam(spazioRam);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(nuovaSchermata));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    }


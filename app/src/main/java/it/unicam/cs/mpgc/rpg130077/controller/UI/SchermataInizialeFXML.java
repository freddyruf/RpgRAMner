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
import java.util.ArrayList;


public class SchermataInizialeFXML extends SchermataGenerica {


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
            controller.setSpazioRam(this.spazioRam);
            controller.setSistemaCombattimento(this.sistemaCombattimento);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void GoBattaglia(ActionEvent event) {
        //se sono gia stati scelte sia le armi che gli hack
        if(!(persistenzaArmamento.caricamentoCatalogoHacks().isEmpty() || persistenzaArmamento.caricamentoCatalogoHacks().isEmpty())){
            try {
                //TODO: aggiungere il reset del sistema di combattimento quando si torna alla schermata iniziale
                FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/Battaglia.fxml"));
                Parent nuovaSchermata = loader.load();

                SchermataBattagliaFXML controller = loader.getController();
                controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);
                controller.setSistemaCombattimento(this.sistemaCombattimento);
                controller.setSpazioRam(spazioRam);
                sistemaCombattimento.aggiungiListener(controller);


                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(nuovaSchermata));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    }


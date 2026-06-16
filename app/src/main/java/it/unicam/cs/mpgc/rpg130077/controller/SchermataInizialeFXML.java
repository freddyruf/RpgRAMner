package it.unicam.cs.mpgc.rpg130077.controller;

import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamento;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class SchermataInizialeFXML {


    private persistenzaArmamento persistenzaArmamento;
    private CaricatoreCatalogo caricatoreCatalogo;

    public void setPersistenze(persistenzaArmamento p, CaricatoreCatalogo c) {
        this.persistenzaArmamento = p;
        this.caricatoreCatalogo = c;
    }

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

            SceltaArmamento controller = loader.getController();
            controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }


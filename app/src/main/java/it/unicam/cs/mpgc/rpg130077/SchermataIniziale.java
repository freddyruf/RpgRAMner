package it.unicam.cs.mpgc.rpg130077;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class SchermataIniziale {
    @FXML
    private void ExitWindow(ActionEvent event) {
        // Chiude semplicemente l'applicazione
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void GoSceltaHack(ActionEvent event) {
        Parent nuovaSchermata=null;

        try { //se esiste apro il file.fxlm
            nuovaSchermata = FXMLLoader.load(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/SceltaHack.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(nuovaSchermata));
    }
}

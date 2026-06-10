
package it.unicam.cs.mpgc.rpg130077;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Parent root = null;
        try { //se esiste apro il file.fxlm
            root = FXMLLoader.load(App.class.getResource("visual/SchermataIniziale.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //imposto la scena e la visualizzo
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

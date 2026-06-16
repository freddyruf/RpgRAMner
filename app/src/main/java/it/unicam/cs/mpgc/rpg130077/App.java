
package it.unicam.cs.mpgc.rpg130077;
import it.unicam.cs.mpgc.rpg130077.controller.SchermataInizialeFXML;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamentoJSON;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaCatalogoArmamentoJSON;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // DECIDI QUI QUALE METODO DI SALVATAGGIO E DI CATALOGO USARE
            persistenzaArmamento persistenza = new persistenzaArmamentoJSON();
            CaricatoreCatalogo catalogo = new persistenzaCatalogoArmamentoJSON();

            // Carica l'FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("visual/SchermataIniziale.fxml"));
            Parent root = loader.load();

            // passo le dipendenze
            SchermataInizialeFXML controller = loader.getController();
            controller.setPersistenze(persistenza, catalogo);

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

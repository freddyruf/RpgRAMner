
package it.unicam.cs.mpgc.rpg130077;
import it.unicam.cs.mpgc.rpg130077.controller.UI.SchermataInizialeFXML;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreEquipaggiamento;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreMusica;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestorePartita;
import it.unicam.cs.mpgc.rpg130077.model.GameFactory;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import it.unicam.cs.mpgc.rpg130077.controller.SessionState;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamentoJSON;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaCatalogoArmamentoJSON;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private GestoreMusica gestoreMusica;

    @Override
    public void start(Stage stage) {
        try {
            //Musica
            gestoreMusica = new GestoreMusica();
            gestoreMusica.avviaMusicaSemplice();

            // DECIDI QUI QUALE METODO DI SALVATAGGIO E DI CATALOGO USARE
            CaricatoreCatalogo catalogo = new PersistenzaCatalogoArmamentoJSON();
            PersistenzaArmamento persistenza = new PersistenzaArmamentoJSON(catalogo);

            GestoreEquipaggiamento gestoreEquipaggiamento = new GestoreArmamento(persistenza);


            // Carica l'FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("visual/SchermataIniziale.fxml"));
            Parent root = loader.load();

            // passa le dipendenze
            SchermataInizialeFXML controller = loader.getController();
            SessionState sessione = new SessionState();
            sessione.setGestoreEquipaggiamento(gestoreEquipaggiamento);
            sessione.setGestorePartita(new GestorePartita(
                    gestoreEquipaggiamento,
                    new GameFactory(new StatoBattaglia1v1()),
                    catalogo));
            sessione.setCombattimento(new CombattimentoATurni());
            controller.setSessione(sessione);

            // Applica il sistema di Scaling Dinamico per il FullScreen
            controller.rendiSchermoInteroSicuro(stage, root);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        gestoreMusica.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

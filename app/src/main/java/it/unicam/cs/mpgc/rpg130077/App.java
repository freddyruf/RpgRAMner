
package it.unicam.cs.mpgc.rpg130077;
import it.unicam.cs.mpgc.rpg130077.controller.UI.SchermataInizialeFXML;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreMusica;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.GameFactory;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamentoJSON;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaCatalogoArmamentoJSON;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

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


            GameFactory factory = new GameFactory();
            SistemaCombattimento sistemaCombattimento = factory.creaNuovaPartitaSemplice(catalogo);

            int ramTotale = sistemaCombattimento.getStatoBattaglia().getFazioneEroi().get(0).getSpazioRAM() +
                    sistemaCombattimento.getStatoBattaglia().getFazioneNemici().get(0).getSpazioRAM();

            // Carica l'FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("visual/SchermataIniziale.fxml"));
            Parent root = loader.load();

            //Crea il clock
            Clock clock = new Clock(() -> sistemaCombattimento.onTick());

            // passa le dipendenze
            SchermataInizialeFXML controller = loader.getController();
            controller.setPersistenze(persistenza, catalogo);
            controller.setSpazioRam(ramTotale);
            controller.setSistemaCombattimento(sistemaCombattimento);
            controller.setClock(clock);

            stage.setScene(new Scene(root));
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

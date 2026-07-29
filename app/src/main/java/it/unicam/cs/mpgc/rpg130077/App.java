
package it.unicam.cs.mpgc.rpg130077;
import it.unicam.cs.mpgc.rpg130077.controller.UI.SchermataBattagliaFXML;
import it.unicam.cs.mpgc.rpg130077.controller.UI.SchermataGenerica;
import it.unicam.cs.mpgc.rpg130077.controller.UI.SchermataInizialeFXML;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamentoJSON;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaCatalogoArmamentoJSON;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {

            //Musica
            java.net.URL urlMusica = getClass().getResource("/Nightdrive VHS Dreams.mp3");
            if (urlMusica != null) {
                Media media = new Media(urlMusica.toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            }


            // DECIDI QUI QUALE METODO DI SALVATAGGIO E DI CATALOGO USARE
            persistenzaArmamento persistenza = new persistenzaArmamentoJSON();
            CaricatoreCatalogo catalogo = new persistenzaCatalogoArmamentoJSON();
            ArrayList<Arma> catalogoArmi= catalogo.CaricamentoCatalogoArmi();

            //carico le hack del giocatore e del nemico
            ArrayList<Hack> catalogoHack= catalogo.CaricamentoCatalogoHack();
            ArrayList<Hack> catalogoHackNemico=catalogoHack;
            catalogoHackNemico.remove(0); //rimuovo 1 hack cosi ne ha 4

            Arma armaG= catalogoArmi.get(0);

            Arma armaN= catalogoArmi.get(1);


            //Creo giocatore
            Giocatore giocatore = new Giocatore("Giocatore", 100, "", 10, catalogoHack, armaG);

            //Creo nemico
            NPC nemico = new NPC("Cybermorb", 100, "", 5, catalogoHackNemico, armaN, 5, 0.1, new StrategiaCasuale());


            // Creo il sistema di combattimento
            SistemaCombattimento sistemaCombattimento = new CombattimentoATurni(new StatoBattaglia1v1(giocatore, nemico));

            // Carica l'FXML
            FXMLLoader loader = new FXMLLoader(App.class.getResource("visual/SchermataIniziale.fxml"));
            Parent root = loader.load();

            // passo le dipendenze
            SchermataInizialeFXML controller = loader.getController();
            controller.setPersistenze(persistenza, catalogo);
            controller.setSpazioRam(giocatore.getSpazioRAM()+nemico.getSpazioRAM());
            controller.setSistemaCombattimento(sistemaCombattimento);

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

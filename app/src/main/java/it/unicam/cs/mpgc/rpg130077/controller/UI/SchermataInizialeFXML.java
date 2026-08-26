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
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/SceltaArmamento.fxml"));
            Parent nuovaSchermata = loader.load();

            SchermataGenerica controller = (SceltaArmamentoFXML)loader.getController();
            controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);
            controller.setSpazioRam(this.spazioRam);
            controller.setSistemaCombattimento(this.sistemaCombattimento);
            controller.setClock(this.clock);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));
            ((SceltaArmamentoFXML) controller).caricaHack();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void goBattaglia(ActionEvent event) {
        // Avvia la battaglia solo se l'utente ha armamento salvato valido
        boolean setupConfigurato = !persistenzaArmamento.getArmi().isEmpty() && !persistenzaArmamento.getHacks().isEmpty();
        if (!setupConfigurato) {
            // Se non ancora configurato, instrada l'utente alla schermata di scelta
            goSceltaSetup(event);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/Battaglia.fxml"));
            Parent nuovaSchermata = loader.load();

            SchermataBattagliaFXML controller = loader.getController();
            controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);
            this.sistemaCombattimento.ripristina();

            controller.setSistemaCombattimento(this.sistemaCombattimento);
            controller.setSpazioRam(spazioRam);
            controller.setClock(this.clock);
            sistemaCombattimento.aggiungiListener(controller);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));

            clock.start();
        } catch (IOException e) {
            throw new RuntimeException("Errore durante l'avvio della schermata di battaglia", e);
        }
    }
    }


package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class SchermataGenerica {
    protected PersistenzaArmamento persistenzaArmamento;
    protected CaricatoreCatalogo caricatoreCatalogo;
    int spazioRam;
    protected SistemaCombattimento sistemaCombattimento;
    Clock clock;



    public void setSpazioRam(int spazioRam){
        this.spazioRam = spazioRam;
    }

    /**
     * Carica una nuova schermata FXML, le passa tutte le dipendenze condivise
     * e la mostra sullo Stage corrente.
     * @return il controller della nuova schermata
     */
    protected SchermataGenerica caricaSchermata(String percorsoFxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    it.unicam.cs.mpgc.rpg130077.App.class.getResource(percorsoFxml));
            Parent nuovaSchermata = loader.load();

            SchermataGenerica controller = loader.getController();
            controller.setupIniziale(this.persistenzaArmamento, this.caricatoreCatalogo);
            controller.setSpazioRam(this.spazioRam);
            controller.setSistemaCombattimento(this.sistemaCombattimento);
            controller.setClock(this.clock);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));

            return controller;
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento di " + percorsoFxml, e);
        }
    }


    public void setupIniziale(PersistenzaArmamento p, CaricatoreCatalogo c) {
        this.persistenzaArmamento = p;
        this.caricatoreCatalogo = c;
    }

    public void setSistemaCombattimento(SistemaCombattimento s) {
        this.sistemaCombattimento = s;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }
}

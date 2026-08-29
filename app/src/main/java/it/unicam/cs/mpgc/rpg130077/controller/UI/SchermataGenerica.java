package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
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
    SessionState sessionState;



    public void setSpazioRam(int spazioRam){
        sessionState.spazioRam = spazioRam;
    }

    /**
     * Carica una nuova schermata FXML, le passa tutte le dipendenze condivise
     * e la mostra sullo Stage corrente.
     * @return il controller della nuova schermata
     */
    protected SchermataGenerica caricaSchermata(String percorsoFxml, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    it.unicam.cs.mpgc.rpg130077.App.class.getResource(percorsoFxml));
            Parent nuovaSchermata = loader.load();

            SchermataGenerica controller = loader.getController();
            controller.setSessione(this.sessionState);

            stage.setScene(new Scene(nuovaSchermata));

            return controller;
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento di " + percorsoFxml, e);
        }
    }

    protected SchermataGenerica caricaSchermata(String percorsoFxml, ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        return caricaSchermata(percorsoFxml, stage);
    }

    public void setSessione(SessionState sessione) {
        this.sessionState = sessione;
    }




    public void setSistemaCombattimento(SistemaCombattimento s) {
        sessionState.combattimento = s;
    }

    public void setClock(Clock clock) {
        sessionState.clock = clock;
    }
}

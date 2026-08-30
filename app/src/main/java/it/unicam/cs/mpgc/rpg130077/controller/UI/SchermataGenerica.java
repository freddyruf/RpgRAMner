package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import javafx.application.Platform;
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
    protected abstract SchermataGenerica caricaSchermata(String percorsoFxml, Stage stage);

    protected abstract SchermataGenerica caricaSchermata(String percorsoFxml, ActionEvent event);

    public void setSessione(SessionState sessione) {
        this.sessionState = sessione;
    }

    public void setSistemaCombattimento(SistemaCombattimento s) {
        sessionState.combattimento = s;
    }

    public void setClock(Clock clock) {
        sessionState.clock = clock;
    }

    /**
     * Rende lo stage Full Screen e adatta la grafica mantenendo il rapporto 16:9
     * (Letterboxing con bande nere) per evitare di sballare i calcoli dei vari controller.
     *
     * Per la stesura di questo metodo, sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
     */
    public abstract void rendiSchermoInteroSicuro(Stage stage, Parent root);
}

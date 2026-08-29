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
        caricaSchermata("/it/unicam/cs/mpgc/rpg130077/visual/SceltaArmamento.fxml", event);
    }

    @FXML
    private void goBattaglia(ActionEvent event) {
        // Avvia la battaglia solo se l'utente ha armamento salvato valido
        boolean setupConfigurato = sessionState.gestoreArmamento.hasConfigurazioneSalvata();
        if (!setupConfigurato) {
            goSceltaSetup(event);
        }
        else{
            // Crea la nuova partita leggendo l'equipaggiamento dal Gestore (quello scelto dal giocatore)
            // e leggendo il catalogo (per generare l'equipaggiamento del nemico)
            it.unicam.cs.mpgc.rpg130077.model.GameFactory factory = new it.unicam.cs.mpgc.rpg130077.model.GameFactory();
            
            // Reperiamo l'equipaggiamento SALVATO dal giocatore
            java.util.List<it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma> armiGiocatore = sessionState.gestoreArmamento.getArmiSalvate();
            java.util.List<it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack> hacksGiocatore = sessionState.gestoreArmamento.getHacksSalvati();
            
            // Reperiamo l'intero catalogo per il nemico (sfruttiamo il caricatore interno)
            it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo caricatore = new it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaCatalogoArmamentoJSON();
            java.util.List<it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma> catalogoArmi = caricatore.caricamentoCatalogoArmi();
            java.util.List<it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack> catalogoHacks = caricatore.caricamentoCatalogoHacks();
            
            it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento combattimento = factory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, catalogoArmi, catalogoHacks);
            
            // Aggiorna lo stato della sessione per la schermata di battaglia
            sessionState.combattimento = combattimento;
            sessionState.clock = new it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock(() -> combattimento.onTick());
            sessionState.spazioRam = combattimento.getStatoBattaglia().getFazioneEroi().get(0).getSpazioRAM() +
                                     combattimento.getStatoBattaglia().getFazioneNemici().get(0).getSpazioRAM();

            caricaSchermata("/it/unicam/cs/mpgc/rpg130077/visual/Battaglia.fxml", event);
        }

    }
    }


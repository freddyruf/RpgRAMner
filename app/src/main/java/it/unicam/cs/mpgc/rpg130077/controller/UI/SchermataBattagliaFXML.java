package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.controller.logica.CombattimentoListener;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class SchermataBattagliaFXML extends SchermataGenerica implements CombattimentoListener {

        @FXML
        private GridPane MenuHacks;

        @FXML
        private GridPane MenuPrincipale;

        @FXML
        private javafx.scene.shape.Rectangle PlayerLifeBar;

        @FXML
        private javafx.scene.shape.Rectangle EnemyLifeBar;

        @FXML
        private GridPane BarraRAM;

        @FXML
        private javafx.scene.control.Button hack1;
        @FXML
        private javafx.scene.control.Button hack2;
        @FXML
        private javafx.scene.control.Button hack3;
        @FXML
        private javafx.scene.control.Button hack4;

        private boolean turnoGiocatore;


    /**
     *
     * esce dalla schermata e torna all hompage
     */
    @FXML
    private void PulsanteEsci(ActionEvent event) {
        GoSchermataIniziale(event);
    }

    /**
     * vado alla schermata iniziale e passo le dipendenze alla schermata iniziale, cosi che non le perdo
     */
    private void GoSchermataIniziale(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml"));
            Parent nuovaSchermata = loader.load();

            SchermataInizialeFXML controller = loader.getController();
            controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);
            controller.setSpazioRam(this.spazioRam);
            controller.setSistemaCombattimento(this.sistemaCombattimento);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    private void visualizzaHacks(ActionEvent event) {
        //scambio il menu da vedere per mostrare le hacks
        MenuHacks.setVisible(true);
        MenuPrincipale.setVisible(false);

        //carico le hacks
        ArrayList<Hack> catalogo= persistenzaArmamento.getHacks();

        if (catalogo == null || catalogo.size() < 4) {
            System.err.println("Catalogo hacks non valido o incompleto: " + catalogo);
            return;
        }

        hack1.setText(catalogo.get(0).getNome());
        hack2.setText(catalogo.get(1).getNome());
        hack3.setText(catalogo.get(2).getNome());
        hack4.setText(catalogo.get(3).getNome());
    }

    @FXML
    private void visualizzaMenu(ActionEvent event) {
        //scambio il menu da vedere per mostrare il menu principale
        MenuPrincipale.setVisible(true);
        MenuHacks.setVisible(false);
    }

    @FXML
    private void pulsanteSparare(ActionEvent event) {
        if(turnoGiocatore){
            sistemaCombattimento.sparare();
        }
        onVitaAggiornataEntita(sistemaCombattimento.getStatoBattaglia().getNemico(0));
    }

    @FXML
    private void pulsanteHack(ActionEvent event) {
        if(turnoGiocatore){
            //ottengo il button che ha chiamato l'evento
            javafx.scene.control.Button b = (javafx.scene.control.Button) event.getSource();

            //carico l'hack scelta
            if (b==hack1){
                sistemaCombattimento.caricaHack(sistemaCombattimento.getStatoBattaglia().getGiocatore().getHacks().get(0));
            }
            else if (b==hack2){
                sistemaCombattimento.caricaHack(sistemaCombattimento.getStatoBattaglia().getGiocatore().getHacks().get(1));
            }
            else if (b==hack3){
                sistemaCombattimento.caricaHack(sistemaCombattimento.getStatoBattaglia().getGiocatore().getHacks().get(2));
            }
            else if (b==hack4){
                sistemaCombattimento.caricaHack(sistemaCombattimento.getStatoBattaglia().getGiocatore().getHacks().get(3));
            }

        }

        //torno al menu principale
        visualizzaHacks(event);
    }

    /**
     * Cambia la lunghezza della barra della vita in base alla vita attuale dell'entita
     * @param entita
     */
    public void cambiaVita(Entita entita) {
        //calcolo la percentuale di vita che ha l'entita rispetto a la massima
        int percentualeDifferenzaVita = entita.getPV() / entita.getMaxPV();

        if (entita instanceof Giocatore) {
            PlayerLifeBar.setWidth(percentualeDifferenzaVita * 668); // 668 è la larghezza massima della barra della vita
        } else {
            EnemyLifeBar.setWidth(percentualeDifferenzaVita * 668); // 668 è la larghezza massima della barra della vita
        }
    }

    /**
     * Aggiorna la visualizzazione della RAM graficamente
     * @param ram
     */

    public void aggiornaRAM(RAM ram) {
        ObservableList<Node> children = BarraRAM.getChildren();
        children.clear();

        //Definiamo le costanti di layout
        double altezzaTotale = BarraRAM.getPrefHeight(); // Prende il valore dall'FXML
        double gap = 5.0; // Spazio vuoto desiderato

        // Impostiamo il gap e il padding della GridPane
        BarraRAM.setVgap(gap);
        BarraRAM.setPadding(new javafx.geometry.Insets(gap, gap, gap, gap));

        // Calcoliamo l'altezza "utile", ovvero togliendo i bordi
        double altezzaUtile = altezzaTotale - (gap * 2);

        // Otteniamo lo spazio massimo totale della RAM per le proporzioni
        int spazioMassimo = ram.getSpazioMassimoInSecondi();

        //Inizializzo la riga da cui inserire
        int riga = ram.getHacks().size() - 1;

        // Creiamo i rettangoli proporzionali
        for (QueuedHack queuedHack : ram.getHacks()) {
            javafx.scene.shape.Rectangle hackRectangle = new javafx.scene.shape.Rectangle();

            // Calcolo la proporzione dell'hack (es. un hack da 10 in una ram da 100 darà 0.1)
            double proporzione = (double) queuedHack.getHack().getDurata() / spazioMassimo;

            // L'altezza teorica se non ci fossero spazi tra gli elementi
            double altezzaTeorica = proporzione * altezzaUtile;

            // Sottraggo il gap per lasciare fisicamente lo spazio vuoto tra un hack e l'altro
            // Uso Math.max(1, ...) per evitare altezze zero o negative per hack piccolissimi
            double altezzaReale = Math.max(1.0, altezzaTeorica - gap);

            hackRectangle.setWidth(110); // Larghezza fissa del rettangolo
            hackRectangle.setHeight(altezzaReale);
            hackRectangle.setFill(javafx.scene.paint.Color.WHITE);

            // Aggiungo il rettangolo alla GridPane nella prima colonna (0), prossima riga
            BarraRAM.add(hackRectangle, 0, riga);

            //Decrementiamo la riga
            riga--;
        }
    }

    /**
     * Compie le azioni che avvengono a ogni thick
     * @param statoBattaglia
     */
    @Override
    public void onTick(StatoBattaglia statoBattaglia) {

        aggiornaRAM(statoBattaglia.getRamCondivisa());
        onVitaAggiornata(statoBattaglia); //aggiorno anche la vita perche una hack "continua" potrebbe aver cambiato la vita


    }

    /**
     * aggiorna la vita di una singola entita
     * @param entita
     */
    @Override
    public void onVitaAggiornataEntita(Entita entita) {
        Platform.runLater(() -> {
            cambiaVita(entita);
        });

    }

    /**
     * aggiorna la vita di tutte le entità
     * @param statoBattaglia
     */
    @Override
    public void onVitaAggiornata(StatoBattaglia statoBattaglia) {

        Platform.runLater(() -> {
            ArrayList<Entita> listaEntita= ((ArrayList) statoBattaglia.getFazioneEroi().clone());
            listaEntita.addAll(statoBattaglia.getFazioneNemici());
            for(Entita entita : listaEntita){
                onVitaAggiornataEntita(entita);
            }
        });


    }

    @Override
    public void onVittoria(Entita vincitore) {
        Platform.runLater(() -> {
            //TODO
        });
    }

    @Override
    public void onTurnoGiocatore(Giocatore giocatore) {
        Platform.runLater(() -> {
                turnoGiocatore = true;
        });
    }
}

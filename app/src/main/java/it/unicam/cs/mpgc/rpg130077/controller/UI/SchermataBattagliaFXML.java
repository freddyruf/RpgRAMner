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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

//TODO rimpiazzare i javafx.XXX nel codice importando quelle classi specifiche

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
        private VBox BarraRAM;

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
            controller.setSistemaCombattimento(this.sistemaCombattimento);
            controller.setSpazioRam(spazioRam);

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
        if(sistemaCombattimento.isPlayerTurn()){
            sistemaCombattimento.sparare();
        }

    }

    @FXML
    private void pulsanteHack(ActionEvent event) {
        if(sistemaCombattimento.isPlayerTurn()){
            //ottengo il button che ha chiamato l'evento
            javafx.scene.control.Button b = (javafx.scene.control.Button) event.getSource();
            // Uso la stessa lista che viene visualizzata nei pulsanti
            ArrayList<Hack> hacksDisponibili = persistenzaArmamento.getHacks();
            //carico l'hack scelta
            if (b==hack1){
                sistemaCombattimento.caricaHack(hacksDisponibili.get(0));
            }
            else if (b==hack2){
                sistemaCombattimento.caricaHack(hacksDisponibili.get(1));
            }
            else if (b==hack3){
                sistemaCombattimento.caricaHack(hacksDisponibili.get(2));
            }
            else if (b==hack4){
                sistemaCombattimento.caricaHack(hacksDisponibili.get(3));
            }
        }
        turnoGiocatore=false;

        //torno al menu principale
        visualizzaMenu(event);
    }
    /**
     * Cambia la lunghezza della barra della vita in base alla vita attuale dell'entita
     * @param entita
     */
    public void cambiaVita(Entita entita) {
        //calcolo la percentuale di vita che ha l'entita rispetto a la massima
        double percentuale = (double) entita.getPV() / (double) entita.getMaxPV();
        percentuale = Math.max(0.0, Math.min(1.0, percentuale));
        double width = percentuale * 668.0;
        if (entita instanceof Giocatore) {
            PlayerLifeBar.setWidth(width);
        } else {
            EnemyLifeBar.setWidth(width);
        }
    }


    /**
     * Aggiorna la visualizzazione della RAM graficamente
     * @param ram
     */

    public void aggiornaRAM(RAM ram) {
        ObservableList<Node> children = BarraRAM.getChildren();
        children.clear();

        // Costanti di layout
        double altezzaTotale = BarraRAM.getPrefHeight(); // 400.0
        double padding = 12.5; // Bordo esterno
        double spacing = 5.0; // Spazio tra una hack e l'altra

        BarraRAM.setSpacing(spacing);
        BarraRAM.setPadding(new javafx.geometry.Insets(padding, padding, padding, padding));

        // NOVITÀ: Calcoliamo lo spazio extra che sarà preso dai gap
        double spazioExtraGaps = Math.max(0, (ram.getHacks().size() - 1) * spacing);

        // Calcoliamo l'altezza "utile" decurtando sia il padding (sopra e sotto) sia i gaps intermedi
        double altezzaUtile = altezzaTotale - (padding * 2) - spazioExtraGaps;

        // Otteniamo lo spazio massimo totale della RAM per le proporzioni
        int spazioMassimo = ram.getSpazioMassimoInSecondi();

        // Creiamo i rettangoli proporzionali
        for (QueuedHack queuedHack : ram.getHacks()) {
            javafx.scene.shape.Rectangle hackRectangle = new javafx.scene.shape.Rectangle();

            // Calcolo la proporzione dell'hack
            double proporzione = (double) queuedHack.getThickInCoda() / spazioMassimo;

            // L'altezza teorica
            double altezzaTeorica = proporzione * altezzaUtile;

            // Garantiamo un'altezza minima per le hack presenti
            double altezzaReale = Math.max(0.0, altezzaTeorica);

            hackRectangle.setWidth(110); // Larghezza fissa del rettangolo
            hackRectangle.setHeight(altezzaReale);

            // Trovo il nome del hack
            Text hackText = new Text(queuedHack.getHack().getNome());

            //se e' un alleato a lanciarlo il quadrato e' bianco, altrimenti e' nero
            if(sistemaCombattimento.getStatoBattaglia().getFazioneEroi().contains(queuedHack.getLanciatore())){
                hackRectangle.setFill(javafx.scene.paint.Color.WHITE);
                hackText.setFill(javafx.scene.paint.Color.BLACK);
            }
            else{
                hackRectangle.setFill(javafx.scene.paint.Color.BLACK);
                hackText.setFill(javafx.scene.paint.Color.WHITE);
            }



            // Nasconde il testo se l'hack diventa troppo sottile per contenerlo
            if (altezzaReale < 20) {
                hackText.setVisible(false);
            }

            // Creo uno StackPane in modo che si veda il nome del hack sopra il rettangolo
            StackPane hackPane = new StackPane(hackRectangle, hackText);
            // Forza lo StackPane a non espandersi oltre il rettangolo
            hackPane.setMinHeight(altezzaReale);
            hackPane.setMaxHeight(altezzaReale);
            hackPane.setPrefHeight(altezzaReale);

            // Aggiungo il rettangolo alla VBox (vengono impilati dall'alto in basso!)
            children.add(hackPane);
        }
    }

    /**
     * Compie le azioni che avvengono a ogni thick
     * @param statoBattaglia
     */
    @Override
    public void onTick(StatoBattaglia statoBattaglia) {

        Platform.runLater(() -> {
            aggiornaRAM(statoBattaglia.getRamCondivisa());
        });
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
                cambiaVita(entita);
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
    public void onTurnoGiocatore() {
        Platform.runLater(() -> {
                turnoGiocatore = true;
        });
    }
}

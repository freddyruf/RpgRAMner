package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoListener;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;

public class SchermataBattagliaFXML extends SchermataGenerica implements CombattimentoListener, SchermataBattaglia {

        @FXML
        private GridPane MenuHacks;

        @FXML
        private GridPane MenuPrincipale;

        @FXML
        private Rectangle PlayerLifeBar;

        @FXML
        private Rectangle EnemyLifeBar;

        @FXML
        private VBox BarraRAM;
        @FXML
        private Pane mainPane;

        private boolean turnoGiocatore;


    /**
     *
     * esce dalla schermata e torna all hompage
     */
    @FXML
    private void PulsanteEsci(ActionEvent event) {
        goSchermataIniziale(event);
    }

    /**
     * Va alla schermata iniziale e passa le dipendenze alla schermata iniziale, cosi che non le perde
     */
    public void goSchermataIniziale(ActionEvent event) {
        try {
            if (clock != null){
                clock.stop();
            }
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml"));
            Parent nuovaSchermata = loader.load();

            SchermataInizialeFXML controller = loader.getController();
            controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);
            controller.setSistemaCombattimento(this.sistemaCombattimento);
            controller.setSpazioRam(spazioRam);
            controller.setClock(this.clock);

            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Carica le hack che erano immagazinate nella memoria nel interfaccia
     * @param event
     */
    @FXML
    private void visualizzaHacks(ActionEvent event) {
        scambiaMenu();

        //carico le hacks
        ArrayList<Hack> catalogo = sistemaCombattimento.getStatoBattaglia().getGiocatore().getHacks();


        ArrayList<Button> bottoniHacks = new ArrayList<>();

        for (javafx.scene.Node nodo : MenuHacks.getChildren()) {
            if (nodo instanceof Button) {
                Button btn = (Button) nodo;
                if (btn.getId() != null && btn.getId().startsWith("hack")) {
                    bottoniHacks.add(btn);
                }
            }
        }

        for (int i = 0; i < bottoniHacks.size(); i++) {
            if (i < catalogo.size()) {
                bottoniHacks.get(i).setText(catalogo.get(i).getNome());
                bottoniHacks.get(i).setDisable(false);
            } else {
                bottoniHacks.get(i).setText("-");
                bottoniHacks.get(i).setDisable(true);
            }
        }
    }

    /**
     * Scambia il menu principale con quello delle hack o viceversa
     */
    private void scambiaMenu() {
        if(MenuPrincipale.isVisible()) {
            MenuHacks.setVisible(true);
            MenuPrincipale.setVisible(false);
        }
        else {
            MenuHacks.setVisible(false);
            MenuPrincipale.setVisible(true);
        }
    }


    @FXML
    private void indietro(ActionEvent event) {
        scambiaMenu();
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

            Button b = (Button) event.getSource();

            ArrayList<Hack> hacksDisponibili = sistemaCombattimento.getStatoBattaglia().getGiocatore().getHacks();
            ArrayList<Button> bottoniHacks = new ArrayList<>();

            for (javafx.scene.Node nodo : MenuHacks.getChildren()) {
                if (nodo instanceof Button) {
                    bottoniHacks.add((Button) nodo);
                }
            }
            for (int i = 0; i < bottoniHacks.size(); i++) {
                if(b==bottoniHacks.get(i)) {
                    sistemaCombattimento.caricaHack(hacksDisponibili.get(i));
                }
            }

        }
        turnoGiocatore=false;

        //torno al menu principale
        scambiaMenu();
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
        if(ram==null){
            throw new NullPointerException("Ram nulla");
        }
        ObservableList<Node> children = BarraRAM.getChildren();
        children.clear();

        // Costanti di layout
        double altezzaTotale = BarraRAM.getPrefHeight();
        double padding = 12.5; // Bordo esterno
        double spacing = 5.0; // Spazio tra una hack e l'altra

        BarraRAM.setSpacing(spacing);
        BarraRAM.setPadding(new Insets(padding, padding, padding, padding));


        double spazioExtraGaps = Math.max(0, (ram.getHacks().size() - 1) * spacing);


        double altezzaUtile = altezzaTotale - (padding * 2) - spazioExtraGaps;


        int spazioMassimo = ram.getSpazioMassimoInSecondi();

        // Creiamo i rettangoli proporzionali
        for (QueuedHack queuedHack : ram.getHacks()) {
            Rectangle hackRectangle = new Rectangle();


            double proporzione = (double) queuedHack.getTickInCoda() / spazioMassimo;

            double altezzaTeorica = proporzione * altezzaUtile;

            double altezzaReale = Math.max(0.0, altezzaTeorica);

            hackRectangle.setWidth(110);
            hackRectangle.setHeight(altezzaReale);

            Text hackText = new Text(queuedHack.getHack().getNome());

            //Scelgo i colori in base a chi lo ha caricato
            if(sistemaCombattimento.getStatoBattaglia().getFazioneEroi().contains(queuedHack.getLanciatore())){
                hackRectangle.setFill(Color.WHITE);
                hackText.setFill(Color.BLACK);
            }
            else{
                hackRectangle.setFill(Color.BLACK);
                hackText.setFill(Color.WHITE);
            }

            // Nasconde il testo se l'hack diventa troppo sottile per contenerlo
            if (altezzaReale < 20) {
                hackText.setVisible(false);
            }

            // Crea uno StackPane in modo che si veda il nome del hack sopra il rettangolo
            StackPane hackPane = new StackPane(hackRectangle, hackText);
            hackPane.setMinHeight(altezzaReale);
            hackPane.setMaxHeight(altezzaReale);
            hackPane.setPrefHeight(altezzaReale);


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
            onVitaAggiornata(statoBattaglia); //aggiorno anche la vita perche una hack "continua" potrebbe aver cambiato la vita
        });



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

    /**
     * Mostra un pop up che annuncia il vincitore, e presenta un pulsante esci per tornare al menu principale
     * @param vincitore
     */
    @Override
    public void onVittoria(Entita vincitore) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Fine battaglia");
            alert.setHeaderText("Battaglia Conclusa");
            alert.setContentText("Il vincitore è: " + (vincitore != null ? vincitore.getNome() : "Nessuno"));
            ButtonType btnEsci = new ButtonType("Torna al Menu", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(btnEsci);
            alert.showAndWait().ifPresent(buttonType -> goSchermataIniziale(new ActionEvent()));
        });
    }

    @Override
    public void onTurnoGiocatore() {
        Platform.runLater(() -> {
                turnoGiocatore = true;
        });
    }
}

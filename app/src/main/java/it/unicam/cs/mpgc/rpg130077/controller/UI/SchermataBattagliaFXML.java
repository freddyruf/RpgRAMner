package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoListener;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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

        private static final double MAX_LIFEBAR_WIDTH = 668.0;


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
        caricaSchermata("/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml", event);
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
            sistemaCombattimento.spara(sistemaCombattimento.getStatoBattaglia().getNemico(0));
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
                    try{
                        sistemaCombattimento.caricaHack(hacksDisponibili.get(i), sistemaCombattimento.getStatoBattaglia().getNemico(0) );
                    } catch (IllegalArgumentException e) {
                        mostraTestoFluttuante(e.getMessage(), Color.RED);
                    }

                }
            }

        }

        //torno al menu principale
        scambiaMenu();
    }
    /**
     * Cambia la lunghezza della barra della vita in base alla vita attuale dell'entita
     * @param entita
     */
    public void cambiaVita(Entita entita) {
        double percentuale = Math.max(0.0, Math.min(1.0, (double) entita.getPv() / entita.getMaxPv()));
        double width = percentuale * MAX_LIFEBAR_WIDTH;
        if (sistemaCombattimento.getStatoBattaglia().getFazioneEroi().contains(entita)) {
            PlayerLifeBar.setWidth(width);
        } else {
            EnemyLifeBar.setWidth(width);
        }
    }


    /**
     * Aggiorna la visualizzazione della RAM graficamente
     * @param ram
     */
    @Override
    public void onAggiornamentoRAM(RAM ram) {
        if(ram == null){
            throw new NullPointerException("Ram nulla");
        }

        RamViewHelper.disegnaBarra(this.BarraRAM, ram, sistemaCombattimento.getStatoBattaglia().getFazioneEroi());
        }

    /**
     * Compie le azioni che avvengono a ogni thick
     * @param statoBattaglia
     */
    @Override
    public void onTick(StatoBattaglia statoBattaglia) {

        Platform.runLater(() -> {
            onAggiornamentoRAM(statoBattaglia.getRamCondivisa());
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
        if(clock!=null){
            clock.stop();
        }
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Fine battaglia");
            alert.setHeaderText("Battaglia Conclusa");
            if(vincitore==null){
                alert.setContentText("Pareggio!");
            }
            else{
                alert.setContentText("Il vincitore è: " + vincitore.getNome() );
            }
            alert.setContentText("Il vincitore è: " + (vincitore != null ? vincitore.getNome() : "Nessuno"));
            ButtonType btnEsci = new ButtonType("Torna al Menu", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(btnEsci);
            alert.showAndWait().ifPresent(buttonType -> goSchermataIniziale(new ActionEvent()));
        });
    }

    @Override
    public void onTurnoGiocatore() {
        Platform.runLater(() -> {
                MenuPrincipale.setDisable(false);
        });
    }
    @Override
    public void ilNemicoNonPuoAttaccare(){
        mostraTestoFluttuante("Il nemico non può attaccare!", Color.GREEN);
    }

    private void mostraTestoFluttuante(String messaggio, Color colore) {
        Text testo = new Text(messaggio);
        testo.setFill(colore);
        testo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 2px;");

        testo.setLayoutX(mainPane.getWidth() / 2 - 150);
        testo.setLayoutY(mainPane.getHeight() / 2 +50);

        mainPane.getChildren().add(testo);

        PauseTransition ritardo = new PauseTransition(Duration.seconds(2));

        ritardo.setOnFinished(e -> mainPane.getChildren().remove(testo));

        ritardo.play();
    }
}

package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class SchermataBattagliaFXML extends SchermataGenerica {

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
    }

    @FXML
    private void visualizzaMenu(ActionEvent event) {
        //scambio il menu da vedere per mostrare il menu principale
        MenuPrincipale.setVisible(true);
        MenuHacks.setVisible(false);
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


}

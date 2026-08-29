package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

/**
 * Classe che serve a essere un delegato per la creazione e i calcoli della costruzione della barra della RAM graficamente
 */
public class RamViewHelper {

    /**
     * Disegna la barra della RAM
     * @param barraRAM il VBox in cui disegnare la barra
     * @param ram la RAM da visualizzare
     * @param eroi la lista degli eroi
     */
    public synchronized static void disegnaBarra(VBox barraRAM, RAM ram, ArrayList<Entita> eroi) {

        Platform.runLater(() -> {
            barraRAM.getChildren().clear();

            double altezzaTotale = barraRAM.getPrefHeight();
            double padding = 12.5;
            double spacing = 5.0;

            barraRAM.setSpacing(spacing);
            barraRAM.setPadding(new javafx.geometry.Insets(padding, padding, padding, padding));

            double spazioExtraGaps = Math.max(0, (ram.getHacks().size() - 1) * spacing);
            double altezzaUtile = altezzaTotale - (padding * 2) - spazioExtraGaps;
            int spazioMassimo = ram.getSpazioMassimoInSecondi();

            for (QueuedHack queuedHack : ram.getHacks()) {
                double proporzione = (double) queuedHack.getTickInCoda() / spazioMassimo;
                double altezzaTeorica = proporzione * altezzaUtile;
                double altezzaReale = Math.max(0.0, altezzaTeorica);

                Rectangle hackRectangle = new Rectangle(110, altezzaReale);
                Text hackText = new Text(queuedHack.getHack().getNome());

                if (eroi.contains(queuedHack.getLanciatore())) {
                    hackRectangle.setFill(Color.WHITE);
                    hackText.setFill(Color.BLACK);
                } else {
                    hackRectangle.setFill(Color.BLACK);
                    hackText.setFill(Color.WHITE);
                }

                StackPane blocco = new StackPane(hackRectangle, hackText);
                barraRAM.getChildren().add(blocco);
            }
        }
    );}


}
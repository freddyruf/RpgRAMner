package it.unicam.cs.mpgc.rpg130077.controller.UI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class SchermataGenericaJavaFX extends SchermataGenerica {

    private static final double BASE_WIDTH = 1920.0;
    private static final double BASE_HEIGHT = 1080.0;

    protected SchermataGenerica caricaSchermata(String percorsoFxml, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    it.unicam.cs.mpgc.rpg130077.App.class.getResource(percorsoFxml));
            Parent nuovaSchermata = loader.load();

            SchermataGenerica controller = loader.getController();
            controller.setSessione(this.sessionState);

            rendiSchermoInteroSicuro(stage, nuovaSchermata);

            return controller;
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento di " + percorsoFxml, e);
        }
    }

    @Override
    protected SchermataGenerica caricaSchermata(String percorsoFxml, ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        return caricaSchermata(percorsoFxml, stage);
    }

    /**
     * Metodo realizzato principalmente con l'utilizzo del AI
     *
     * Adatta la schermata alla risoluzione disponibile mantenendo il rapporto 16:9.
     * La radice viene inserita in contenitori e trasformata in scala, così il layout
     * originale continua a usare dimensioni base fisse anche in fullscreen.
     *
     * @param stage stage JavaFX da rendere fullscreen
     * @param root contenuto della schermata da adattare
     */
    @Override
    public void rendiSchermoInteroSicuro(Stage stage, Parent root) {
        // Aggiunge la classe CSS root se assente, per applicare gli stili base alla schermata.
        if (!root.getStyleClass().contains("root")) {
            root.getStyleClass().add("root");
        }

        // Inserisce il contenuto originale in un Group per poterlo trasformare in scala.
        Group group = new Group(root);

        // Crea un contenitore centrale che occupa tutta la finestra e contiene il Group.
        StackPane stackPane = new StackPane(group);
        stackPane.setId("MAIN_SCALER_PANE");
        stackPane.setStyle("-fx-background-color: black;");

        // Questa trasformazione ridimensiona proporzionalmente il contenuto 16:9.
        Scale scale = new Scale();
        group.getTransforms().add(scale);

        // Calcola il fattore minimo tra larghezza e altezza disponibili.
        // Le bande nere vengono create dove lo spazio non corrisponde al rapporto 16:9.
        Runnable aggiornaScala = () -> {
            double scaleFactor = Math.min(stackPane.getWidth() / BASE_WIDTH, stackPane.getHeight() / BASE_HEIGHT);
            if (scaleFactor > 0) {
                scale.setX(scaleFactor);
                scale.setY(scaleFactor);
            }
        };

        // Ricalcola la scala ogni volta che cambia la dimensione del contenitore.
        stackPane.widthProperty().addListener((obs, oldVal, newVal) -> aggiornaScala.run());
        stackPane.heightProperty().addListener((obs, oldVal, newVal) -> aggiornaScala.run());

        // Riutilizza la scena esistente se presente.
        if (stage.getScene() != null) {
            stage.getScene().setRoot(stackPane);
        } else {
            // Crea una nuova scena e disabilita il testo mostrato all'uscita dal fullscreen.
            Scene scene = new Scene(stackPane);
            stage.setScene(scene);
            stage.setFullScreenExitHint("");
        }

        // Attiva il fullscreen dello stage.
        stage.setFullScreen(true);

        // Esegue un primo ricalcolo dopo che JavaFX ha completato il layout iniziale.
        Platform.runLater(aggiornaScala);
    }
}
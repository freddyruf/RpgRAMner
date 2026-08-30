package it.unicam.cs.mpgc.rpg130077.controller.UI;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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

    @Override
    public void rendiSchermoInteroSicuro(Stage stage, Parent root) {
        if (!root.getStyleClass().contains("root")) {
            root.getStyleClass().add("root");
        }

        Group group = new Group(root);

        StackPane stackPane = new StackPane(group);
        stackPane.setId("MAIN_SCALER_PANE");
        stackPane.setStyle("-fx-background-color: black;");

        Scale scale = new Scale();
        group.getTransforms().add(scale);

        Runnable aggiornaScala = () -> {
            double scaleFactor = Math.min(stackPane.getWidth() / BASE_WIDTH, stackPane.getHeight() / BASE_HEIGHT);
            if (scaleFactor > 0) {
                scale.setX(scaleFactor);
                scale.setY(scaleFactor);
            }
        };

        ChangeListener<Number> listener = (obs, oldVal, newVal) -> aggiornaScala.run();

        stackPane.widthProperty().addListener(listener);
        stackPane.heightProperty().addListener(listener);

        Scene currentScene = stage.getScene();

        if (currentScene != null) {
            currentScene.setRoot(stackPane);
        } else {
            Scene scene = new Scene(stackPane);
            stage.setScene(scene);
            stage.setFullScreenExitHint("");
        }

        stage.setFullScreen(true);

        Platform.runLater(aggiornaScala);
    }
}
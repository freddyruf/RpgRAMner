package it.unicam.cs.mpgc.rpg130077.controller;
import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamentoJSON;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceltaArmamentoFXML_JSON {

    @FXML
    private Label Label1;

    @FXML
    private Label Label2;

    @FXML
    private Label Label3;

    @FXML
    private Label Label4;

    @FXML
    private MenuButton MenuButtonArma;

    @FXML
    private Label LabelArma;

    Pistola pistola = new Pistola("Colpisci teschi", 6,40, 0.3);
    Mitragliatrice mitragliatrice = new Mitragliatrice("Squarciagole", 50, 8, 0.1);

    private GestoreArmamento gestore = new GestoreArmamento(new persistenzaArmamentoJSON(), "data/Armamento.json");


    private ArrayList<MenuButton> getAllMenuButtonsFromEvent(ActionEvent event) {
        Node source = (Node) event.getSource();
        Pane pane = (Pane) source.getScene().getRoot();

        ArrayList<MenuButton> menuButtons = new ArrayList<>();
        for (javafx.scene.Node child : pane.getChildren()) {
            if (child instanceof MenuButton) {
                menuButtons.add((MenuButton) child);
            }
        }
        return menuButtons;
    }

    private ArrayList<String> getMenuButtonNames(ArrayList<MenuButton> menuButtons) {
        ArrayList<String> menuButtonNames = new ArrayList<>();
        for (MenuButton menuButton : menuButtons) {
            menuButtonNames.add(menuButton.getText());
        }
        return menuButtonNames;
    }


    @FXML
    private void PulsanteEsci(ActionEvent event) {
        gestore.salva(getMenuButtonNames(getAllMenuButtonsFromEvent(event)));

        GoSchermataIniziale(event);
    }


    private void GoSchermataIniziale(ActionEvent event) {
        Parent nuovaSchermata = null;

        try { //se esiste apro il file.fxlm
            nuovaSchermata = FXMLLoader.load(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(nuovaSchermata));
    }


    //FIXME fargli seguire i principi SOLID
    private void CambiaLabel(MenuButton menuButton) {
        Label label = identificaLabel(menuButton.getId());
        if(menuButton.getText().equals("Fireball")){
            label.setText("Infligge 30 danni allo scadere di 5 secondi di caricamento");
        }
        else if(menuButton.getText().equals("Firewall")){
            label.setText("Cura 30 danni allo scadere di 5 secondi di caricamento");
        }
        else if(menuButton.getText().equals("Acid")){
            label.setText("Infligge 6 danni ogni secondo per 5 secondi");
        }
        else if(menuButton.getText().equals("RAM:Sort")){
            label.setText("Riordina i programmi nella RAM in base alla loro durata attuale(Crescente)");
        }
        else if(menuButton.getText().equals("RAM:Reverse")){
            label.setText("Inverte l'ordine dei programmi nella RAM");
        }
        else if(menuButton.getText().equals("Pistola")){
            label.setText("Spara un colpo singolo");
        }
        else if(menuButton.getText().equals("Mitragliatrice")){
            label.setText("Spara colpi a raffica");
        }
    }

    private Label identificaLabel(String idButton){
        switch (idButton){
            case "MenuButton1": return Label1;
            case "MenuButton2": return Label2;
            case "MenuButton3": return Label3;
            case "MenuButton4": return Label4;
            default: return LabelArma;
        }
    }

    @FXML
    private void SelezionaNelMenu(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        MenuButton menuButton = (MenuButton) menuItem.getParentPopup().getOwnerNode();
        menuButton.setText(menuItem.getText());
        CambiaLabel(menuButton);

    }

}


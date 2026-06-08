package it.unicam.cs.mpgc.rpg130077.visual;
import it.unicam.cs.mpgc.rpg130077.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import java.io.IOException;

public class SceltaArmamento {

    @FXML
    private Label Label1;

    @FXML
    private Label Label2;

    @FXML
    private Label Label3;

    @FXML
    private Label Label4;

    @FXML
    private Label LabelArma;

    @FXML
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

    private void CambiaLabel(MenuButton menuButton) {
        Label label=null;
        if(menuButton.getId().equals("MenuButton1")){
            label=Label1;
        }
        else if(menuButton.getId().equals("MenuButton2")){
            label=Label2;
        }
        else if(menuButton.getId().equals("MenuButton3")){
            label=Label3;
        }
        else if (menuButton.getId().equals("MenuButton4")){
            label=Label4;
        }
        else{
            label=LabelArma;
        }

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
    @FXML
    private void SelezionaNelMenu(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        MenuButton menuButton = (MenuButton) menuItem.getParentPopup().getOwnerNode();
        menuButton.setText(menuItem.getText());
        CambiaLabel(menuButton);

    }

}


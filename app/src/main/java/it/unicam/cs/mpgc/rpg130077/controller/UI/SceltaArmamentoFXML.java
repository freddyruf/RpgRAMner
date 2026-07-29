package it.unicam.cs.mpgc.rpg130077.controller.UI;
import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamento;
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

public class SceltaArmamentoFXML extends SchermataGenerica {
    protected GestoreArmamento gestore;

    @FXML
    Pane mainPane;


    @Override
    public void setPersistenze(persistenzaArmamento p, CaricatoreCatalogo c) {
        super.persistenzaArmamento = p;
        this.caricatoreCatalogo = c;

        this.gestore = new GestoreArmamento(p, c);
    }

    /**
        @param event un evento di JAVAFX
        @return un ArrayList di MenuButton presenti nella scena a cui è associato l'evento
     */
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

    /**

      @param menuButtons un array di menu buttons
      @return ArrayList di stringhe contenenti i nomi contenuti nei menu button
     */
    private ArrayList<String> getMenuButtonNames(ArrayList<MenuButton> menuButtons) {
        ArrayList<String> menuButtonNames = new ArrayList<>();
        for (MenuButton menuButton : menuButtons) {
            menuButtonNames.add(menuButton.getText());
        }
        return menuButtonNames;
    }

    /**
     *
     * salva il setup scelto e esce
     */
    @FXML
    private void PulsanteEsci(ActionEvent event) {
        if(tuttoArmamentoèScelto(event)){
            gestore.salva(getMenuButtonNames(getAllMenuButtonsFromEvent(event)));
            GoSchermataIniziale(event);
        }
    }

    private boolean tuttoArmamentoèScelto(ActionEvent event) {
        ArrayList<String> menuButtonNames = getMenuButtonNames(getAllMenuButtonsFromEvent(event));
        for(String menuButtonName : menuButtonNames) {
            if(menuButtonName.contains("HACK") || menuButtonName.contains("Arma")) {
                return false;
            }
        }
        return true;
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

    /**
     *
     * @param menuButton menu button la cui descrizione(Nella label) va cambiata
     */
    private void CambiaLabel(MenuButton menuButton) {
        String idBottone = menuButton.getId();

        String idLabelAttesa = idBottone.replace("MenuButton", "Label");

        //Cerco la Label dinamicamente nella scena (serve il casting a Label)
        Label label = (Label) menuButton.getScene().lookup("#" + idLabelAttesa);

        // Se ho trovato la label, le assegno la descrizione presa dal Model
        if (label != null) {
            String nomeItem = menuButton.getText(); // Es: "Fireball"
            String testoDescrizione = gestore.getDescrizioneItem(nomeItem);
            label.setText(testoDescrizione);
        } else {
            throw new RuntimeException("Errore: Label con ID #" + idLabelAttesa + " non trovata nella scena.");
        }
    }


    /** Si attiva quando un Menu button viene cambiato, aggiorna la Label**/
    @FXML
    private void SelezionaNelMenu(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        MenuButton menuButton = (MenuButton) menuItem.getParentPopup().getOwnerNode();
        menuButton.setText(menuItem.getText());
        CambiaLabel(menuButton);

    }

    public ArrayList<MenuButton> geAllMenuButtonsFromThis(){

        ArrayList<MenuButton> menuButtons = new ArrayList<>();
        for (javafx.scene.Node child : mainPane.getChildren()) {
            if (child instanceof MenuButton) {
                menuButtons.add((MenuButton) child);
            }
        }
        return menuButtons;
    }

    /**
     * Carica le armi e le hacks salvate nei menu button e nelle labels
     */
    public void caricaHack() {
        ArrayList<MenuButton> listaMenuButton = geAllMenuButtonsFromThis();

        //Carico le armi
        int cnt=0;
        for (int i = 0; i<listaMenuButton.size();i++) {
            MenuButton menuButton = listaMenuButton.get(i);
            if(menuButton.getId().contains("Arma")) {
                menuButton.setText(persistenzaArmamento.getArma().get(cnt).getNome());
                CambiaLabel(menuButton);
                cnt++;
            }
        }

        //Carico le hacks
        for (int i = 0; i < listaMenuButton.size(); i++) {
            MenuButton menuButton = listaMenuButton.get(i);
            menuButton.setText(persistenzaArmamento.getHacks().get(i).getNome());
            CambiaLabel(menuButton);
        }

    }
}




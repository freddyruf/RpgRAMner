package it.unicam.cs.mpgc.rpg130077.controller.UI;
import it.unicam.cs.mpgc.rpg130077.App;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
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
    public void setPersistenze(PersistenzaArmamento p, CaricatoreCatalogo c) {
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
        if(checkArmamentoCompletamenteScelto(event)){
            gestore.salva(getMenuButtonNames(getAllMenuButtonsFromEvent(event)));
            GoSchermataIniziale(event);
        }
    }

    private boolean checkArmamentoCompletamenteScelto(ActionEvent event) {
        ArrayList<String> menuButtonNames = getMenuButtonNames(getAllMenuButtonsFromEvent(event));
        for(String menuButtonName : menuButtonNames) {
            if(menuButtonName.contains("HACK") || menuButtonName.contains("Arma")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Va alla schermata iniziale e passa le dipendenze alla schermata iniziale, cosi che non le perde
     */
    private void GoSchermataIniziale(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml"));
            Parent nuovaSchermata = loader.load();

            SchermataInizialeFXML controller = loader.getController();

            controller.setPersistenze(this.persistenzaArmamento, this.caricatoreCatalogo);
            controller.setSpazioRam(this.spazioRam);
            controller.setSistemaCombattimento(this.sistemaCombattimento);
            controller.setClock(this.clock);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(nuovaSchermata));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cambia la descrizione di un Hack o di un Arma in base a cosa si ha scelto
     * @param menuButton menu button la cui descrizione(Nella label) va cambiata
     */
    private void CambiaLabel(MenuButton menuButton) {
        String idBottone = menuButton.getId();

        String idLabelAttesa = idBottone.replace("MenuButton", "Label");

        //Cerca la Label dinamicamente nella scena (serve il casting a Label)
        Label label = (Label) menuButton.getScene().lookup("#" + idLabelAttesa);

        // Se trova la label, le assegno la descrizione presa dal Model
        if (label != null) {
            String nomeItem = menuButton.getText(); // Es: "Fireball"
            String testoDescrizione = gestore.getDescrizioneItem(nomeItem);
            label.setText(testoDescrizione);
        } else {
            throw new RuntimeException("Errore: Label con ID #" + idLabelAttesa + " non trovata nella scena.");
        }
    }


    /**
     * Cambia il testo del menu button con quello del menu item selezionato e aggiorna la label con la descrizione dell'oggetto selezionato
     **/
    @FXML
    private void SelezionaNelMenu(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        MenuButton menuButton = (MenuButton) menuItem.getParentPopup().getOwnerNode();
        menuButton.setText(menuItem.getText());
        CambiaLabel(menuButton);

    }

    /**
     * Raccoglie tutti i MenuButton della pagina in cui e' stato eseguito
     * @return ArrayList dei MenuButton della pagina
     */
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
                menuButton.setText(persistenzaArmamento.getArmi().get(cnt).getNome());
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




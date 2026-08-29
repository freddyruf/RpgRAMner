package it.unicam.cs.mpgc.rpg130077.controller.UI;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SessionState;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class SceltaArmamentoFXML extends SchermataGenerica {

    @FXML
    Pane mainPane;

    @Override
    public void setSessione(SessionState s) {
        super.setSessione(s);
        popolaMenuDaCatalogo();
        caricaArmamento();
    }

    /**
        @param root root di JAVAFX
        @return un ArrayList di MenuButton presenti nella scena a cui è associato l'evento
     */
    private ArrayList<MenuButton> getAllMenuButtons(Parent root) {
        ArrayList<MenuButton> result = new ArrayList<>();
        cercaMenuButtonsRicorsivo(root, result);
        return result;
    }

    private void cercaMenuButtonsRicorsivo(Parent parent, ArrayList<MenuButton> result) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof MenuButton mb) {
                result.add(mb);
            } else if (node instanceof Parent nestedParent) {
                cercaMenuButtonsRicorsivo(nestedParent, result);
            }
        }
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
        if(checkArmamentoCompletamenteScelto()){
            sessionState.gestoreArmamento.salva(getMenuButtonNames(getAllMenuButtons(((Node) event.getSource()).getParent())));
            goSchermataIniziale(event);
        }
    }

    private boolean checkArmamentoCompletamenteScelto() {
        for (MenuButton mb : getAllMenuButtonsFromThis()) {
            if (isPlaceholder(mb.getText())) {
                return false;
            }
        }
        return true;
    }
    private boolean isPlaceholder(String testo) {
        return testo == null || testo.equals("Arma") || testo.equals("HACK 1") || testo.equals("HACK 2");
    }

    /**
     * Va alla schermata iniziale e passa le dipendenze alla schermata iniziale, cosi che non le perde
     */
    private void goSchermataIniziale(ActionEvent event) {
        caricaSchermata("/it/unicam/cs/mpgc/rpg130077/visual/SchermataIniziale.fxml", event);
    }

    /**
     * Cambia la descrizione di un Hack o di un Arma in base a cosa si ha scelto
     * @param menuButton menu button la cui descrizione(Nella label) va cambiata
     */
    private void cambiaLabel(MenuButton menuButton) {
        String idBottone = menuButton.getId();

        String idLabelAttesa = idBottone.replace("MenuButton", "Label");

        Label label = (Label) mainPane.lookup("#" + idLabelAttesa);

        if (label != null) {
            String nomeItem = menuButton.getText();
            String testoDescrizione = sessionState.gestoreArmamento.getDescrizioneItem(nomeItem);
            label.setText(testoDescrizione);
        }
    }


    /**
     * Cambia il testo del menu button con quello del menu item selezionato e aggiorna la label con la descrizione dell'oggetto selezionato
     **/
    @FXML
    private void selezionaNelMenu(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        MenuButton menuButton = (MenuButton) menuItem.getParentPopup().getOwnerNode();
        menuButton.setText(menuItem.getText());
        cambiaLabel(menuButton);

    }

    /**
     * Raccoglie tutti i MenuButton della pagina in cui e' stato eseguito
     * @return ArrayList dei MenuButton della pagina
     */
    public ArrayList<MenuButton> getAllMenuButtonsFromThis(){

        ArrayList<MenuButton> menuButtons = new ArrayList<>();
        for (javafx.scene.Node child : mainPane.getChildren()) {
            if (child instanceof MenuButton) {
                menuButtons.add((MenuButton) child);
            }
        }
        return menuButtons;
    }

    /**
     * Popola dinamicamente i MenuButton leggendo gli oggetti dal catalogo JSON.
     */
    public void popolaMenuDaCatalogo() {
        if (sessionState.gestoreArmamento == null) return;

        ArrayList<Arma> catalogoArmi = sessionState.gestoreArmamento.getCatalogoArmi();
        ArrayList<Hack> catalogoHacks = sessionState.gestoreArmamento.getCatalogoHacks();
        for (MenuButton mb : getAllMenuButtonsFromThis()) {
            mb.getItems().clear();

            if (mb.getId() != null && mb.getId().contains("Arma")) {
                for (Arma arma : catalogoArmi) {
                    MenuItem item = new MenuItem(arma.getNome());
                    item.setOnAction(this::selezionaNelMenu);
                    mb.getItems().add(item);
                }
            } else {
                for (Hack hack : catalogoHacks) {
                    MenuItem item = new MenuItem(hack.getNome());
                    item.setOnAction(this::selezionaNelMenu);
                    mb.getItems().add(item);
                }
            }
        }
    }

    /**
     * Carica le armi e le hacks salvate nei menu button e nelle labels.
     */
    public void caricaArmamento() {
        if (sessionState.gestoreArmamento == null){
            throw new NullPointerException("GestoreArmamento non trovata");
        }

        ArrayList<MenuButton> listaMenuButton = getAllMenuButtonsFromThis();
        ArrayList<it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma> armiSalvate = sessionState.gestoreArmamento.getArmiSalvate();
        ArrayList<it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack> hacksSalvati = sessionState.gestoreArmamento.getHacksSalvati();

        // 1. Divido fisicamente i bottoni Arma dai bottoni Hack per non sovrascriverli male
        ArrayList<MenuButton> hackButtons = new ArrayList<>();
        ArrayList<MenuButton> armaButtons = new ArrayList<>();

        for (MenuButton mb : listaMenuButton) {
            if (mb.getId() != null && mb.getId().contains("Arma")) {
                armaButtons.add(mb);
            } else {
                hackButtons.add(mb);
            }
        }

        // 2. Carico le armi salvate (fermo il ciclo se finisco le armi salvate)
        for (int i = 0; i < armaButtons.size() && armiSalvate != null && i < armiSalvate.size(); i++) {
            MenuButton menuButton = armaButtons.get(i);
            menuButton.setText(armiSalvate.get(i).getNome());
            cambiaLabel(menuButton);
        }

        // 3. Carico le hacks salvate (fermo il ciclo se finisco le hack salvate)
        for (int i = 0; i < hackButtons.size() && hacksSalvati != null && i < hacksSalvati.size(); i++) {
            MenuButton menuButton = hackButtons.get(i);
            menuButton.setText(hacksSalvati.get(i).getNome());
            cambiaLabel(menuButton);
        }
    }
}




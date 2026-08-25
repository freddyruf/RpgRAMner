package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;

import java.util.ArrayList;

public class GestoreArmamento {
    private final ArrayList<Arma> catalogoArmi;
    private final ArrayList<Hack> catalogoHacks;
    private final PersistenzaArmamento gestoreSalvataggi;


    public GestoreArmamento(PersistenzaArmamento gestoreSalvataggi, CaricatoreCatalogo caricatoreCatalogo) {
        this.catalogoArmi = caricatoreCatalogo.caricamentoCatalogoArmi();
        this.catalogoHacks = caricatoreCatalogo.caricamentoCatalogoHack();
        this.gestoreSalvataggi = gestoreSalvataggi;
    }

    /**
     *
     * @param nomeItem
     * @return
     */
    public String getDescrizioneItem(String nomeItem) {
        for (Hack hack : catalogoHacks) {
            if (hack.getNome().equals(nomeItem)) return hack.getDescrizione();
        }
        for (Arma arma : catalogoArmi) {
            if (arma.getNome().equals(nomeItem)) return arma.getDescrizione();
        }
        return "Descrizione non disponibile";
    }


    /**
     *
     * @param setupScelto Lista di nomi di Hack e Armi scelte
     */

    public void salva(ArrayList<String> setupScelto) {
        ArrayList<Hack> hacks = new ArrayList<>();
        ArrayList<Arma> armi = new ArrayList<>();

        // Trasformo le stringhe negli oggetti corrispondenti
        for (String s : setupScelto) {
            for (Hack hack : catalogoHacks) {
                if (s.equals(hack.getNome())) {
                    hacks.add(hack);
                }
            }
            for (Arma arma : catalogoArmi) {
                if (s.equals(arma.getNome())) {
                    armi.add(arma);
                }
            }
        }

        // Delego tutto al nuovo metodo unificato!
        gestoreSalvataggi.salvaEquipaggiamentoScelto(armi, hacks);
    }
}
package it.unicam.cs.mpgc.rpg130077.controller.logica;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;

import java.util.ArrayList;

/**
 * Classe che gestisce l'armamento del giocatore, inclusi armi e hack. Fornisce metodi per ottenere descrizioni degli oggetti e per salvare l'equipaggiamento scelto.
 */
public class GestoreArmamento {
    private final ArrayList<Arma> catalogoArmi;
    private final ArrayList<Hack> catalogoHacks;
    private final PersistenzaArmamento gestoreSalvataggi;


    public GestoreArmamento(PersistenzaArmamento gestoreSalvataggi) {
        this.catalogoArmi = gestoreSalvataggi.getCatalogo().caricamentoCatalogoArmi();
        this.catalogoHacks = gestoreSalvataggi.getCatalogo().caricamentoCatalogoHacks();
        this.gestoreSalvataggi = gestoreSalvataggi;
    }

    /**
     * Restituisce la descrizione di un item dato il suo nome.
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

    /**
     * Controlla se l'utente ha salvato una configurazione valida.
     */
    public boolean hasConfigurazioneSalvata() {
        return !gestoreSalvataggi.getArmi().isEmpty() && !gestoreSalvataggi.getHacks().isEmpty();
    }
    public ArrayList<Arma> getArmiSalvate() {
        return gestoreSalvataggi.getArmi();
    }

    public ArrayList<Hack> getHacksSalvati() {
        return gestoreSalvataggi.getHacks();
    }
    public ArrayList<Arma> getCatalogoArmi() {
        return catalogoArmi;
    }
    public ArrayList<Hack> getCatalogoHacks() {
        return catalogoHacks;
    }


}
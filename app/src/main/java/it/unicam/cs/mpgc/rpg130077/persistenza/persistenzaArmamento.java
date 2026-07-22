package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import java.util.ArrayList;

public interface persistenzaArmamento {


    /**
     * Legge il file e restituisce le armi salvate.
     * Se il file non esiste o è vuoto, restituisce una lista vuota.
     *
     * @return ArrayList di Arma salvate nel file JSON
     */
    ArrayList<Arma> getArma();

    /**
     * Legge il file e restituisce gli hack salvati.
     * @return Arraylist di hack salvati, o null se vuota
     */
    ArrayList<Hack> getHacks();

    // Nuovo metodo unificato per il salvataggio
    void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks);

    ArrayList<Arma> caricamentoCatalogoArmi();
    ArrayList<Hack> caricamentoCatalogoHacks();
}
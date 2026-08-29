package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import java.util.ArrayList;

/**
 * Interfaccia che astrae un oggetto che si occupa di gestire la persistenza del armamento scelto dal giocatore
 */
public interface PersistenzaArmamento {


    /**
     * Legge il file e restituisce le armi salvate.
     * Se il file non esiste o è vuoto, restituisce una lista vuota.
     *
     * @return ArrayList di Arma salvate nel file JSON
     */
    ArrayList<Arma> getArmi();

    /**
     * Legge il file e restituisce gli hack salvati.
     * @return Arraylist di hack salvati, o null se vuota
     */
    ArrayList<Hack> getHacks();


    void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks);

    CaricatoreCatalogo getCatalogo();


}
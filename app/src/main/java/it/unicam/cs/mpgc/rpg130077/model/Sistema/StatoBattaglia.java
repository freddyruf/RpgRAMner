package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.RAM;

import java.util.ArrayList;

/**
 * interfaccia che astrae il concetto di stato di una battaglia: insieme dei suoi dati
 */
public interface StatoBattaglia {

    RAM getRamCondivisa();

    Giocatore getGiocatore();
    ArrayList<Entita> getFazioneEroi();
    ArrayList<Entita> getFazioneNemici();
    Entita getEroe(int n);
    Entita getNemico(int n);
    StatoBattaglia copy();



}

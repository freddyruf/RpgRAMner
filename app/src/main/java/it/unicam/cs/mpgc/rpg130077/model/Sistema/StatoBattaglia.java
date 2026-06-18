package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.RAM;

import java.util.ArrayList;

public interface StatoBattaglia {

    RAM getRamCondivisa();
    int getClock();
    void incrementaClock();

    Giocatore getGiocatore();
    ArrayList<Entita> getFazioneEroi();
    ArrayList<Entita> getFazioneNemici();
    Entita getEroe(int n);
    Entita getNemico(int n);



}

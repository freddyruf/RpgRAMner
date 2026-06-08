package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class Acid extends Hack implements HackContinua {

    public Acid(String nome, String descrizione, int durata) {
        super(nome,descrizione, durata);
    }

    @Override
    public void EseguiAvanzamento(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        //10 di danno ogni thick
    }

}
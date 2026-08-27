package it.unicam.cs.mpgc.rpg130077.model.Entita;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;


import java.util.ArrayList;

public class Giocatore extends Entita {
    public Giocatore(String nome, int MaxPV, String image, int spazioRAM, ArrayList<Hack> hacks, Arma arma, boolean fazione) {
        super(nome, MaxPV, image, spazioRAM, hacks, arma, fazione);
    }
    public Giocatore(Giocatore giocatore){
        super(giocatore);
    }
    public Entita copy(){
        return new Giocatore(this);
    }

}

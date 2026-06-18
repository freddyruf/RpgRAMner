package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

public class QueuedHack {
    Hack hack;
    int thickInCoda;
    Entita bersaglio;
    Entita lanciatore;


    public QueuedHack(Hack hack, Entita bersaglio, Entita lanciatore) {
        this.bersaglio=bersaglio;
        this.lanciatore=lanciatore;
        this.hack = hack;
        this.thickInCoda = hack.getDurata();
    }

    public int getThickInCoda() {
        return thickInCoda;
    }


    public Hack getHack(){
        return hack;
    }
}

package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

/**
 * Contenitore di una hack che serve ad "adattarla" al essere dentro una RAM
 */
public class QueuedHack {
    Hack hack;
    int tickInCoda;
    Entita bersaglio;
    Entita lanciatore;


    public QueuedHack(Hack hack, Entita bersaglio, Entita lanciatore) {
        this.bersaglio=bersaglio;
        this.lanciatore=lanciatore;
        this.hack = hack;
        this.tickInCoda = hack.getDurata();
    }
    public QueuedHack(QueuedHack queuedHack) {
        this.hack = queuedHack.hack.copy();
        this.tickInCoda = queuedHack.tickInCoda;
        this.bersaglio = queuedHack.bersaglio.copy();
        this.lanciatore = queuedHack.lanciatore.copy();
    }

    public int getTickInCoda() {
        return tickInCoda;
    }
    public void setTickInCoda(int tickInCoda) {
        this.tickInCoda = tickInCoda;
    }
    public Hack getHack(){
        return hack;
    }

    public Entita getBersaglio(){
        return bersaglio;
    }
    public Entita getLanciatore(){
        return lanciatore;
    }
}

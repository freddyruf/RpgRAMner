package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

/**
 * Contenitore di una hack che serve ad "adattarla" al essere dentro una RAM
 */
public class QueuedHack {
    private Hack hack;
    private int tickInCoda;
    private Entita bersaglio;
    private Entita lanciatore;


    public QueuedHack(Hack hack, Entita bersaglio, Entita lanciatore) {
        this.bersaglio=bersaglio;
        this.lanciatore=lanciatore;
        this.hack = hack;
        this.tickInCoda = hack.getDurata();
    }
    public QueuedHack(QueuedHack queuedHack) {
        this.hack = queuedHack.hack.copy();
        this.tickInCoda = queuedHack.tickInCoda;
        this.bersaglio = queuedHack.bersaglio;
        this.lanciatore = queuedHack.lanciatore;
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

package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

/**
 * Contenitore di una hack che serve ad "adattarla" al essere dentro una RAM
 */
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
    public QueuedHack(QueuedHack queuedHack) {
        this.hack = queuedHack.hack.Copy();
        this.thickInCoda = queuedHack.thickInCoda;
        this.bersaglio = queuedHack.bersaglio.Copy();
        this.lanciatore = queuedHack.lanciatore.Copy();
    }

    public int getThickInCoda() {
        return thickInCoda;
    }
    public void setThickInCoda(int thickInCoda) {
        this.thickInCoda = thickInCoda;
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

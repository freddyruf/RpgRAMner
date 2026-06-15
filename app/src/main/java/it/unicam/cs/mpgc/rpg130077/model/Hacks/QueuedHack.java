package it.unicam.cs.mpgc.rpg130077.model.Hacks;

public class QueuedHack {
    Hack hack;
    int thickInCoda;


    public QueuedHack(Hack hack){
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

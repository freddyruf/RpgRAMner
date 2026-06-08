package it.unicam.cs.mpgc.rpg130077;

public class QueuedHack {
    Hack hack;
    int thickInCoda;

    public QueuedHack(Hack hack){
        this.hack = hack;
        this.thickInCoda = hack.getDurata();
    }

    public Hack getHack(){
        return hack;
    }
}

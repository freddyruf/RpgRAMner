package it.unicam.cs.mpgc.rpg130077;

import java.util.LinkedList;

public class RAM extends LinkedList<QueuedHack> {

    private int spazioMassimoInSecondi;

    public RAM(int spazioMassimoInSecondi){  // ← no void!
        super();
        this.spazioMassimoInSecondi = spazioMassimoInSecondi;
    }

    public int getSpazioOccupato(){
        int spazioOccupato = 0;
        for(QueuedHack qh : this){
            spazioOccupato += qh.getHack().getDurata();
        }
        return spazioOccupato;
    }

    public void inserisci(Hack hack) throws IllegalAccessException {
        if(hack==null){
            throw new NullPointerException("Hack non può essere null");
        }
        else if(hack.getDurata()+hack.getDurata()>spazioMassimoInSecondi){
            throw new IllegalAccessException("Hack troppo grande per essere inserita");
        }
        else{
            this.offer(new QueuedHack(hack));
        }
    }


}

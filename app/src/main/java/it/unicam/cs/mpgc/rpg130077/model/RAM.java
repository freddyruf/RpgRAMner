package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;

import java.util.LinkedList;

public class RAM {

    private int spazioMassimoInSecondi;
    private LinkedList<QueuedHack> hacks;

    public RAM(int spazioMassimoInSecondi){  // ← no void!
        super();
        this.spazioMassimoInSecondi = spazioMassimoInSecondi;
    }

    public int getSpazioOccupato(){
        int spazioOccupato = 0;
        for(QueuedHack qh : hacks){
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
            hacks.offer(new QueuedHack(hack));
        }
    }

    public QueuedHack rimuovi() { //FIXME: rimuoverlo solo o anche returnarlo?
        return hacks.poll();
    }

    public QueuedHack visualizzaTesta(){
        return hacks.peek();
    }




}

package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class RAM {

    private int spazioMassimoInSecondi;
    private LinkedList<QueuedHack> hacks;

    public RAM(int spazioMassimoInSecondi){  // ← no void!
        super();
        this.spazioMassimoInSecondi = spazioMassimoInSecondi;
    }

    public void avanza(StatoBattaglia statoBattaglia) {
        QueuedHack queuedHack = visualizzaTesta();
        queuedHack.setThickInCoda(queuedHack.getThickInCoda()-1);
        Hack hack = queuedHack.getHack();
        ArrayList<Effetto> effetti= hack.getEffetti();
        for (Effetto effetto : effetti) {
            if(!effetto.isConclusive()){
                effetto.EseguiEffetto(statoBattaglia,queuedHack.getLanciatore(),queuedHack.getBersaglio());
            }
        }
        if(queuedHack.getThickInCoda()<=0){
            for(Effetto effetto : effetti){
                if(effetto.isConclusive()){
                    effetto.EseguiEffetto(statoBattaglia,queuedHack.getLanciatore(),queuedHack.getBersaglio());
                }
            }
            rimuovi(); //rimuove la hack dalla RAM
        }
    }

    public int getSpazioMassimoInSecondi() {
        return spazioMassimoInSecondi;
    }

    public int getSpazioOccupato(){
        int spazioOccupato = 0;
        for(QueuedHack qh : hacks){
            spazioOccupato += qh.getHack().getDurata();
        }
        return spazioOccupato;
    }

    public void inserisci(Hack hack, Entita bersaglio, Entita lanciatore) {
        if(hack==null){
            throw new NullPointerException("Hack non può essere null");
        }
        else if(hack.getDurata()+hack.getDurata()>spazioMassimoInSecondi){
            throw new IllegalArgumentException("Hack troppo grande per essere inserita");
        }
        else{
            hacks.offer(new QueuedHack(hack, bersaglio,lanciatore));
        }
    }

    public QueuedHack rimuovi() { //FIXME: rimuoverlo solo o anche returnarlo?
        return hacks.poll();
    }

    public QueuedHack visualizzaTesta(){
        return hacks.peek();
    }

    public void sort(Comparator<QueuedHack> comparator){
        hacks.sort(comparator);
    }

    public void reverse(){
        LinkedList<QueuedHack> reversed = new LinkedList<>();
        for (int i = hacks.size() - 1; i >= 0; i--) {
            reversed.add(hacks.get(i));
        }
        hacks = reversed;
    }

    public LinkedList<QueuedHack> getHacks(){
        return hacks;
    }




}

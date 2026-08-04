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

    public RAM(int spazioMassimoInSecondi){
        this.spazioMassimoInSecondi = spazioMassimoInSecondi;
        hacks = new LinkedList<QueuedHack>();
    }
    public RAM(RAM ram){
        this.spazioMassimoInSecondi = ram.spazioMassimoInSecondi;
        this.hacks = new LinkedList<QueuedHack>();
    }

    public void avanza(StatoBattaglia statoBattaglia) {
        QueuedHack queuedHack = visualizzaTesta();
        if (queuedHack == null) return;
        queuedHack.setThickInCoda(queuedHack.getThickInCoda()-1);
        Hack hack = queuedHack.getHack();
        ArrayList<Effetto> effetti= hack.getEffetti();

        System.out.println("\n"+hacks.toString()+"\n");
        System.out.println("\n"+effetti.toString()+"\n");

        // Esegue gli effetti non conclusivi
        for (Effetto effetto : effetti) {
            if(!effetto.isConclusive()){
                effetto.EseguiEffetto(statoBattaglia,queuedHack.getLanciatore(),queuedHack.getBersaglio());
            }
        }

        //se un caricamento si e' concluso
        if(queuedHack.getThickInCoda()<=0){
            hacks.remove(queuedHack); // Rimuove PRIMA di eseguire gli effetti conclusivi che potrebbero alterare l'ordine (es. reverse)
            for(Effetto effetto : effetti){
                if(effetto.isConclusive()){
                    effetto.EseguiEffetto(statoBattaglia,queuedHack.getLanciatore(),queuedHack.getBersaglio());
                }
            }
        }
    }

    public int getSpazioMassimoInSecondi() {
        return spazioMassimoInSecondi;
    }

    public int getSpazioOccupato(){
        int spazioOccupato = 0;
        for(QueuedHack hack : hacks){
            spazioOccupato += hack.getThickInCoda();
        }
        return spazioOccupato;
    }

    public void inserisci(Hack hack, Entita bersaglio, Entita lanciatore) {
        if(hack==null){
            throw new NullPointerException("Hack non può essere null");
        }
        else if(getSpazioOccupato() + hack.getDurata() > spazioMassimoInSecondi){
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

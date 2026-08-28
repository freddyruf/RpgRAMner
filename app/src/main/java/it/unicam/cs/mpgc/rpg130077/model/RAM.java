package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Classe che rappresenta una RAM: Un contenitore di programmi attivi
 */
public class RAM {

    private int spazioMassimoInSecondi;
    private LinkedList<QueuedHack> hacks;

    public RAM(int spazioMassimoInSecondi){
        if(spazioMassimoInSecondi <= 0){
            throw new IllegalArgumentException("Massimo In Secondi non valido");
        }
        this.spazioMassimoInSecondi = spazioMassimoInSecondi;
        hacks = new LinkedList<QueuedHack>();
    }
    public RAM(RAM ram){
        this.spazioMassimoInSecondi = ram.spazioMassimoInSecondi;
        this.hacks = new LinkedList<>();//La ram non viene trasferita con le copie
    }

    /**
     * Si attiva a ogni tick
     * @param statoBattaglia
     */
    public synchronized void avanza(StatoBattaglia statoBattaglia) {
        QueuedHack queuedHack = visualizzaTesta();
        if (queuedHack == null) return;
        queuedHack.setTickInCoda(queuedHack.getTickInCoda()-1);
        Hack hack = queuedHack.getHack();
        ArrayList<Effetto> effetti= hack.getEffetti();

        // Esegue gli effetti non conclusivi
        for (Effetto effetto : effetti) {
            if(!effetto.isConclusive()){
                effetto.eseguiEffetto(statoBattaglia,queuedHack.getLanciatore(),queuedHack.getBersaglio());
            }
        }

        //se un caricamento si e' concluso
        if (queuedHack.getTickInCoda() <= 0) {
            hacks.poll();
            for (Effetto effetto : effetti) {
                if (effetto.isConclusive()) {
                    effetto.eseguiEffetto(statoBattaglia, queuedHack.getLanciatore(), queuedHack.getBersaglio());
                }
            }
        }
    }

    public synchronized int getSpazioMassimoInSecondi() {
        return spazioMassimoInSecondi;
    }

    public synchronized int getSpazioOccupato(){
        int spazioOccupato = 0;
        for(QueuedHack hack : hacks){
            spazioOccupato += hack.getTickInCoda();
        }
        return spazioOccupato;
    }

    public synchronized void inserisci(Hack hack, Entita bersaglio, Entita lanciatore) {
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

    public synchronized QueuedHack rimuovi() {
        return hacks.poll();
    }

    public synchronized QueuedHack visualizzaTesta(){
        return hacks.peek();
    }

    public synchronized void sort(Comparator<QueuedHack> comparator) {
        if (comparator != null) {
            hacks.sort(comparator);
        } else {
            hacks.sort(Comparator.comparingInt(QueuedHack::getTickInCoda));
        }
    }
    public synchronized void sort() {
        sort(null);
    }

    /**
     * Inverte l'ordine delle hack in coda
     */
    public synchronized void reverse(){
        java.util.Collections.reverse(hacks);
    }

    public synchronized ArrayList<QueuedHack> getHacks() {
        return new ArrayList<>(hacks);
    }




}

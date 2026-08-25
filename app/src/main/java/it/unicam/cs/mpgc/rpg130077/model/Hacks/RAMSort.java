package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoSort;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.Comparator;

public class RAMSort extends Hack {

    public RAMSort(String nome, String descrizione, int durata, Comparator<QueuedHack> comparator) {
        super(nome,descrizione, durata);
    }
    public RAMSort(RAMSort ramSort) {
        super(ramSort);
    }
    @Override
    public Hack copy() {
        return new RAMSort(this);
    }
}

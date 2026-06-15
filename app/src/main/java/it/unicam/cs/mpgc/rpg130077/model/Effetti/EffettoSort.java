package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.Comparator;

public class EffettoSort implements Effetto {
    private boolean conclusive;
    private Comparator<QueuedHack> comparator;

    public EffettoSort(boolean conclusive, Comparator<QueuedHack> comparator) {
        this.conclusive = conclusive;
        this.comparator = comparator;
    }

    @Override
    public void EseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        b.getRamCondivisa().sort((hack1, hack2) -> {
            if(hack1.getThickInCoda()<hack2.getThickInCoda()) return -1;
            else return 1;
        });
    }



    @Override
    public boolean isConclusive() {
        return conclusive;
    }
}

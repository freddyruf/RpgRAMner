package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.Comparator;

public class EffettoSort implements Effetto {
    private boolean conclusive;
    private Comparator<QueuedHack> comparator=Comparator.comparingInt(QueuedHack::getThickInCoda);

    public EffettoSort(boolean conclusive) {
        this.conclusive = conclusive;
    }
    public EffettoSort(EffettoSort effettoSort) {
        this.conclusive = effettoSort.conclusive;
        this.comparator = effettoSort.comparator;
    }
    public Effetto copy() {
        return new EffettoSort(this);
    }

    @Override
    public void eseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        b.getRamCondivisa().sort(comparator);  // ← usa il comparator passato
    }



    @Override
    public boolean isConclusive() {
        return conclusive;
    }
}

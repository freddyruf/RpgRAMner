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

    @Override
    public void EseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        b.getRamCondivisa().sort(comparator);  // ← usa il comparator passato
    }



    @Override
    public boolean isConclusive() {
        return conclusive;
    }
}

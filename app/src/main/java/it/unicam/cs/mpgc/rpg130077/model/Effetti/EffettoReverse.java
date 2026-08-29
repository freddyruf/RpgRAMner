package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.Comparator;

public class EffettoReverse implements Effetto {
    private boolean conclusive;
    public EffettoReverse(boolean conclusive) {
        this.conclusive = conclusive;
    }
    public EffettoReverse(EffettoReverse effettoReverse) {
        this.conclusive = effettoReverse.conclusive;
    }
    public Effetto copy() {
        return new EffettoReverse(this);
    }

    @Override
    public void eseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        b.getRamCondivisa().reverse();
    }

    @Override
    public boolean isConclusive() {
        return conclusive;
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.RAM;
    }
}

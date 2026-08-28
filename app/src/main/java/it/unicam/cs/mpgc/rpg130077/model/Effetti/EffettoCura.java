package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class EffettoCura implements Effetto {

    private int cura;
    private boolean conclusive;

    public EffettoCura(int cura,boolean conclusive) {
        this.cura = cura;
        this.conclusive = conclusive;
    }
    public EffettoCura(EffettoCura effettoCura) {
        this.cura = effettoCura.cura;
        this.conclusive = effettoCura.conclusive;
    }
    public Effetto copy() {
        return new EffettoCura(this);
    }

    @Override
    public void eseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        lanciatore.setPv(lanciatore.getPv() + cura);
    }

    @Override
    public boolean isConclusive() {
        return conclusive;
    }

    @Override
    public boolean isHealDealer() {
        return true;
    }
}

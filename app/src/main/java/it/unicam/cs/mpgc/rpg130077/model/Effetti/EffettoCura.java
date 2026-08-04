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
    public Effetto Copy() {
        return new EffettoCura(this);
    }

    @Override
    public void EseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        lanciatore.setPV(lanciatore.getPV() + cura);
    }

    @Override
    public boolean isConclusive() {
        return conclusive;
    }
}

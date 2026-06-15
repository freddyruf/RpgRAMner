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

    @Override
    public void EseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        lanciatore.setPV(lanciatore.getPV() + cura);
    }

    @Override
    public boolean isConclusive() {
        return conclusive;
    }
}

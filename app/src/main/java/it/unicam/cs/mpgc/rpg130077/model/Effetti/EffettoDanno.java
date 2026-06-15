package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class EffettoDanno implements Effetto {
    private int danno;
    private boolean conclusive;

    public EffettoDanno(int danno,boolean conclusive) {
        this.danno = danno;
        this.conclusive = conclusive;
    }

    @Override
    public void EseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        bersaglio.setPV(bersaglio.getPV() - danno);
    }

    @Override
    public boolean isConclusive() {
        return conclusive;
    }
}

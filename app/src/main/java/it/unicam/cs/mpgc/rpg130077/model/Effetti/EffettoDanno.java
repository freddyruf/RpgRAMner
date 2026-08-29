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
    public EffettoDanno(EffettoDanno effettoDanno) {
        this.danno = effettoDanno.danno;
        this.conclusive = effettoDanno.conclusive;
    }
    public Effetto copy() {
        return new EffettoDanno(this);
    }

    @Override
    public void eseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
        bersaglio.setPv(bersaglio.getPv() - danno);
    }

    @Override
    public boolean isConclusive() {
        return conclusive;
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.DAMAGE;
    }

}

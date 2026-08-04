package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class RAMReverse extends Hack {
    public RAMReverse(String nome, String descrizione, int durata) {
        super(nome,descrizione, durata);
    }
    public RAMReverse(RAMReverse ramReverse) {
        super(ramReverse);
    }
    @Override
    public Hack Copy() {
        return new RAMReverse(this);
    }
}


package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class Firewall extends Hack {

    public Firewall(String nome, String descrizione, int durata) {

        super(nome,descrizione, durata);
    }

    public Firewall(Firewall firewall) {
        super(firewall);
    }
    @Override
    public Hack Copy() {
        return new Firewall(this);
    }
}



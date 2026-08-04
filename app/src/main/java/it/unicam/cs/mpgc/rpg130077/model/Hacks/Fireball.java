package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class Fireball extends Hack {

    public Fireball(String nome, String descrizione, int durata) {
        super(nome,descrizione, durata);
    }
    public Fireball(Fireball fireball) {
        super(fireball);
    }
    @Override
    public Hack Copy() {
        return new Fireball(this);
    }

}

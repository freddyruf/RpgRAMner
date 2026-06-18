package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public interface Azione {
    void esegui(StatoBattaglia statoBattaglia);
}

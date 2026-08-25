package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

/**
 * Interfaccia che astrae il concetto di Azione: Entita X compie una azione
 */
public interface Azione {
    void esegui(StatoBattaglia statoBattaglia);
    boolean isDamageDealer();
    boolean isHealDealer();
}

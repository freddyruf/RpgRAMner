package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.Set;

/**
 * Interfaccia che astrae il concetto di Azione: Entita X compie una azione
 */

public interface Azione {
    void esegui(StatoBattaglia statoBattaglia);
    Set<EffectType> getEffectTypes();
}

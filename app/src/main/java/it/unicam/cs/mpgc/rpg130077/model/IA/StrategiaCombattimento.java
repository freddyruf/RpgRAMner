package it.unicam.cs.mpgc.rpg130077.model.IA;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public interface StrategiaCombattimento {
    Azione scegliMossa(NPC npc, StatoBattaglia stato);
}

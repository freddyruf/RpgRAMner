package it.unicam.cs.mpgc.rpg130077.model.IA;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

/**
 * interfaccia che rappresenta la strategia che un NPC ha in un combattimento, ovvero la logica con cui sceglie le mosse
 */
public interface StrategiaCombattimento {
    Azione scegliMossa(NPC npc, StatoBattaglia stato);
}

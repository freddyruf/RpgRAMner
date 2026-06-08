package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public interface HackConclusiva {
     void Esegui(StatoBattaglia b, Entita lanciatore, Entita bersaglio);
}

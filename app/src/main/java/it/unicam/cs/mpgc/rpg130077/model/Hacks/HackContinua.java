package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public interface HackContinua {
    void EseguiAvanzamento(StatoBattaglia b, Entita lanciatore, Entita bersaglio);
}

package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public interface Effetto {
        void EseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio);
        boolean isConclusive();
        Effetto Copy();
}

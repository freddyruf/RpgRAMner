package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

/**
 * Interfaccia che astrae il concetto di Effetto: Avvenimento/propieta che influenza la battaglia
 */
public interface Effetto {
        void EseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio);
        boolean isConclusive(); //Un effetto e' conclusivo quando si esegue solo al completamento del caricamento, altrimenti si esegue ad ogni tick
        Effetto Copy();
}

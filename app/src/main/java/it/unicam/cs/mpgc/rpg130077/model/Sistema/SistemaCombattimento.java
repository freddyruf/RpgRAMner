package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

public interface SistemaCombattimento {
    void avanza();
    Entita checkVittoria();
    StatoBattaglia getStatoBattaglia();
    void eseguiMossa(Azione mossa);

}


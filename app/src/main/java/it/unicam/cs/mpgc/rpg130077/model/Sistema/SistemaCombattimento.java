package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.controller.logica.CombattimentoListener;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

public interface SistemaCombattimento {
    void avanza();
    Entita checkVittoria();
    StatoBattaglia getStatoBattaglia();
    void eseguiMossa(Azione mossa);
    void caricaHack(Hack hack);
    void sparare();
    boolean isPlayerTurn();
    void aggiungiListener(CombattimentoListener combattimentoListener);

}


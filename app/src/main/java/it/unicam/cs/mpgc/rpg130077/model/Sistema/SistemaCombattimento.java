package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

/**
 * Interfaccia che astrae il concetto di combattimento nella sua interezza
 */
public interface SistemaCombattimento {
    void avanza();
    Entita checkVittoria();
    StatoBattaglia getStatoBattaglia();
    void eseguiMossa(Azione mossa);
    void caricaHack(Hack hack);
    void sparare();
    boolean isPlayerTurn();
    void aggiungiListener(CombattimentoListener combattimentoListener);
    void onTick();
    void rimuoviListener(CombattimentoListener combattimentoListener);

    /**
     * Ripristina il combattimento allo stato originario
     */
    void ripristina();

}


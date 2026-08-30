package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

/**
 * Interfaccia che astrae il concetto di combattimento nella sua interezza
 */
public interface SistemaCombattimento {
    /**
     * avanza il turno
     */
    void avanza();

    /**
     * Controlla se ci sta un vincitore
     * @return Entita della squadra vincitrice, o null se non ha vinto nessuno
     */
    Entita checkVittoria();
    StatoBattaglia getStatoBattaglia();

    /**
     * Avvia l'esecuzione del azione
     * @param mossa
     */
    void eseguiMossa(Azione mossa);

    /**
     * Carica una hack nella RAM
     * @param hack
     */
    void caricaHack(Hack hack, Entita bersaglio);

    /**
     * Fa sparare l'entita in corso
     */
    void spara(Entita entita);
    boolean isPlayerTurn();
    void aggiungiListener(CombattimentoListener combattimentoListener);

    /**
     * Raccoglie ed esegue tutto cio che va fatto a ogni tick
     */
    void onTick();
    void rimuoviListener(CombattimentoListener combattimentoListener);

    /**
     * Ripristina il combattimento allo stato originario
     */
    void ripristina();

}


package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.RAM;

public interface CombattimentoListener {
    /**
     * Raccoglie ed esegue tutto cio che va fatto a ogni tick
     */
    default void onTick(StatoBattaglia statoBattaglia) {throw new UnsupportedOperationException("Non supportato per questo listener");}
    default void onVitaAggiornataEntita(Entita entita) {throw new UnsupportedOperationException("Non supportato per questo listener");}
    /**
     * Raccoglie ed esegue tutto cio che va fatto quando ci sta una vittoria
     */
    default void onVittoria(Entita vincitore) {throw new UnsupportedOperationException("Non supportato per questo listener");}

    /**
     * Raccoglie ed esegue tutto cio che va fatto a ogni avvenimento che cambia le vite delle Entita
     */
    default void onVitaAggiornata(StatoBattaglia statoBattaglia) {throw new UnsupportedOperationException("Non supportato per questo listener");}
    /**
     * Raccoglie ed esegue tutto cio che va fatto a ogni turno del giocatore
     */
    default void onTurnoGiocatore() {throw new UnsupportedOperationException("Non supportato per questo listener");};

    /**
     * Raccoglie ed esegue tutto cio che va fatto ogni volta che si aggiorna la ram
     */
    default void onAggiornamentoRAM(RAM ram) {throw new UnsupportedOperationException("Non supportato per questo listener");}

    default void ilNemicoNonPuoAttaccare() {throw new UnsupportedOperationException("Non supportato per questo listener");}
}

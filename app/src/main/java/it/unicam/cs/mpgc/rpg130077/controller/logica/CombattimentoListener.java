package it.unicam.cs.mpgc.rpg130077.controller.logica;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public interface CombattimentoListener {
    void onTick(StatoBattaglia statoBattaglia);
    void onVitaAggiornataEntita(Entita entita);
    void onVittoria(Entita vincitore);
    void onVitaAggiornata(StatoBattaglia statoBattaglia);
    void onTurnoGiocatore(Giocatore giocatore);
    void aggiornaRAM(RAM ram);
}

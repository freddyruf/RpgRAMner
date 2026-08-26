package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.RAM;

public interface CombattimentoListener {
    void onTick(StatoBattaglia statoBattaglia);
    void onVitaAggiornataEntita(Entita entita);
    void onVittoria(Entita vincitore);
    void onVitaAggiornata(StatoBattaglia statoBattaglia);
    void onTurnoGiocatore();
    void aggiornaRAM(RAM ram);
}

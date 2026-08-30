package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import javafx.event.ActionEvent;

public interface SchermataBattaglia {
    void onTick(StatoBattaglia statoBattaglia);
    void onVittoria(Entita vincitore);
    void onAggiornamentoRAM(RAM ram);
    void cambiaVita(Entita entita);
}

package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

public interface SistemaCombattimento {
    boolean avanza(StatoBattaglia stato);
    Entita checkVittoria(StatoBattaglia stato);
}


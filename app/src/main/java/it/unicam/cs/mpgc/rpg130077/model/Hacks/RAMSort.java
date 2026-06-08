package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class RAMSort extends Hack implements HackConclusiva {

    public RAMSort(String nome, String descrizione, int durata) {
        super(nome,descrizione, durata);
    }

    @Override
    public void Esegui(StatoBattaglia b, Entita lanciatore, Entita bersagliob) {
        //Riordina la coda in ordine crescente
    }
}

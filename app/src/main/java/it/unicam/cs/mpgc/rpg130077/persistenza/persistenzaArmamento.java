package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import java.util.ArrayList;

public interface persistenzaArmamento {
    ArrayList<Arma> prelevaArma();
    ArrayList<Hack> prelevaHacks();

    // Nuovo metodo unificato per il salvataggio
    void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks);
}
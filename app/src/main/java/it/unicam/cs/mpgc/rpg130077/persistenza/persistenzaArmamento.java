package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

import java.util.ArrayList;

public interface persistenzaArmamento {
    ArrayList<Arma> prelevaArma();
    ArrayList<Hack> prelevaHacks();
    void salvaArmi(ArrayList<Arma> armi);
    void salvaHack(ArrayList<Hack> hack);

    ArrayList<Arma> CaricamentoCatalogoArmi();
    ArrayList<Hack> CaricamentoCatalogoHacks();


}

package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import java.util.ArrayList;

public interface CaricatoreCatalogo {
    ArrayList<Arma> CaricamentoCatalogoArmi();
    ArrayList<Hack> CaricamentoCatalogoHack();
}
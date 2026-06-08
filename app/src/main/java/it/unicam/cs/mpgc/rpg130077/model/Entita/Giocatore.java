package it.unicam.cs.mpgc.rpg130077.model.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Giocatore extends Entita {
    public Giocatore(String nome, int MaxPV, Image image, int spazioRAM, ArrayList<Hack> hacks, Arma arma) {
        super(nome, MaxPV, image, spazioRAM, hacks, arma);
    }
}

package it.unicam.cs.mpgc.rpg130077;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Giocatore extends Entita {
    public Giocatore(String nome, int MaxPV, Image image, int spazioRAM, ArrayList<Hack> hacks, Arma arma) {
        super(nome, MaxPV, image, spazioRAM, hacks, arma);
    }
}

package it.unicam.cs.mpgc.rpg130077;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class NPC extends Entita {
    int dannoAttaccoASorpresa;
    double chanceAttaccoASorpresa; // Min 0 MAX 1.0

    public NPC(String nome, int MaxPV, Image image, int spazioRAM, ArrayList<Hack> hacks, Arma arma,  int dannoAttaccoASorpresa, double chanceAttaccoASorpresa) {
        super(nome, MaxPV, image, spazioRAM, hacks, arma);
        if(chanceAttaccoASorpresa>1 ){
            throw new IllegalArgumentException("Chance attacco ASorpresa non valido");
        }
        this.dannoAttaccoASorpresa = dannoAttaccoASorpresa;
        this.chanceAttaccoASorpresa = chanceAttaccoASorpresa;

    }

    public boolean controllaAttaccoASorpresa(){
        if(chanceAttaccoASorpresa*Math.random() <= 0.5){
            return true;
        }
        else return false;
    }
    public int getDannoAttaccoASorpresa() {
        return dannoAttaccoASorpresa;
    }
}
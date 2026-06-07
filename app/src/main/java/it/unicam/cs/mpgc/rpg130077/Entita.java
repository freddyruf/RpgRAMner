package it.unicam.cs.mpgc.rpg130077;

import javafx.scene.image.Image;

import java.util.ArrayList;

public abstract class Entita {
    String nome;
    int PV;
    int MaxPV;
    Image image;
    int spazioRAM;
    ArrayList<Hack> hacks;
    Arma arma;

    public Entita(String nome, int MaxPV, Image image, int spazioRAM, ArrayList<Hack> hacks, Arma arma) {
        if(nome == null || image == null || hacks == null || arma == null) {
            throw new NullPointerException("I parametri non possono essere nulli");
        }
        this.nome = nome;
        this.MaxPV = MaxPV;
        this.PV=MaxPV;
        this.image = image;
        this.spazioRAM = spazioRAM;
        this.hacks = hacks;
        this.arma = arma;
    }
    public String getNome() {
        return nome;
    }
    public int getPV() {
        return PV;
    }
    public int getMaxPV() {
        return MaxPV;
    }
    public Image getImage() {
        return image;
    }
    public int getSpazioRAM() {
        return spazioRAM;
    }
    public ArrayList<Hack> getHacks(){
        return hacks;
    }
    public Arma getArma() {
        return arma;
    }

    private void setPV(int PV) {
        if(PV > MaxPV) throw new IllegalArgumentException();
        this.PV = PV;
    }

    public void spara(Entita e) {
        if(e==null) throw new NullPointerException();
    }

    public void uploadHack(Hack hack) {
        if(hack==null) throw new NullPointerException();
    }

}

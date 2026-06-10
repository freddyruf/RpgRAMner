package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

public abstract class Arma implements Equipaggiamento {
    String nome;
    int maxCaricatore;
    int caricatore;
    int danno;
    double critChance; //min 0 max 1

    protected Arma(String nome, int maxCaricatore, int danno, double critChance) {
        if(nome == null) {
            throw new NullPointerException("Il nome non può essere nullo");
        }
        this.nome = nome;
        this.maxCaricatore = maxCaricatore;
        this.caricatore = maxCaricatore;
        this.danno = danno;
        this.critChance = critChance;
    }

    @Override
    public String getNome() {
        return nome;
    }


    public int getCaricatore() {
        return caricatore;
    }


    public int getMaxCaricatore() {
        return maxCaricatore;
    }


    public int getDanno() {
        return danno;
    }


    public int calcolaDanno() {
        throw new RuntimeException("Not implemented yet");
    }



}

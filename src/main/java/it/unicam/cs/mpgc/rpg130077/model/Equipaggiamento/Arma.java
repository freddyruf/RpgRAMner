package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

import it.unicam.cs.mpgc.rpg130077.model.RandomNumbersGenerator;

/**
 * Classe astratta che rappresenta un'arma nel gioco.
 */
public abstract class Arma implements Equipaggiamento {
    String nome;
    String descrizione;
    int maxCaricatore;
    int caricatore;
    int danno;
    double critChance; //min 0 max 1
    protected RandomNumbersGenerator random = new RandomNumbersGenerator();

    public Arma(String nome,String descrizione, int maxCaricatore, int danno, double critChance) {
        if(nome == null) {
            throw new NullPointerException("Il nome non può essere nullo");
        }
        if (maxCaricatore <= 0) throw new IllegalArgumentException("Il caricatore deve essere positivo");
        this.nome = nome;
        this.maxCaricatore = maxCaricatore;
        this.caricatore = maxCaricatore;
        this.danno = danno;
        this.critChance = critChance;
        this.descrizione = descrizione;
    }
    public Arma(Arma arma) {
        this.nome = arma.nome;
        this.descrizione = arma.descrizione;
        this.maxCaricatore = arma.maxCaricatore;
        this.caricatore = arma.caricatore;
        this.danno = arma.danno;
        this.critChance = arma.critChance;
        this.random = arma.random;
    }

    public void setRandom(RandomNumbersGenerator random) {
        if (random == null) throw new NullPointerException("Il generatore random non può essere nullo");
        this.random = random;
    }
    public abstract Arma copy();

    public String getDescrizione() {
        return descrizione;
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


    public abstract int calcolaDanno();



}

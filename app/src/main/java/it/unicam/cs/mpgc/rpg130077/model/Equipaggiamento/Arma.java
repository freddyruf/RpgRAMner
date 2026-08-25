package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

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

    public Arma(String nome,String descrizione, int maxCaricatore, int danno, double critChance) {
        if(nome == null) {
            throw new NullPointerException("Il nome non può essere nullo");
        }
        this.nome = nome;
        this.maxCaricatore = maxCaricatore;
        this.caricatore = maxCaricatore;
        this.danno = danno;
        this.critChance = critChance;
    }
    public Arma(Arma arma) {
        this.nome = arma.nome;
        this.descrizione = arma.descrizione;
        this.maxCaricatore = arma.maxCaricatore;
        this.caricatore = arma.caricatore;
        this.danno = arma.danno;
        this.critChance = arma.critChance;
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


    public int calcolaDanno() {
        throw new RuntimeException("Not implemented yet");
    }



}

package it.unicam.cs.mpgc.rpg130077;

public class Mitragliatrice implements Arma {

    String nome;
    int maxCaricatore;
    int caricatore;
    int danno;
    int critChance;

    public Mitragliatrice(String nome, int maxCaricatore, int caricatore, int danno, int critChance) {
        this.nome = nome;
        this.maxCaricatore = maxCaricatore;
        this.caricatore = caricatore;
        this.danno = danno;
        this.critChance = critChance;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public int getCaricatore() {
        return caricatore;
    }

    @Override
    public int getMaxCaricatore() {
        return maxCaricatore;
    }


    @Override
    public int getDanno() {
        return danno;
    }

    /*
    @Override
    public void Sparare(Entita e) {

    }
    */
}

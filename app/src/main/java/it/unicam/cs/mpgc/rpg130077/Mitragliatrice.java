package it.unicam.cs.mpgc.rpg130077;

public class Mitragliatrice implements Arma {

    String nome;
    int maxCaricatore;
    int caricatore;
    int danno;
    double critChance; //min 0 max 1

    public Mitragliatrice(String nome, int maxCaricatore, int caricatore, int danno, double critChance) {
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


    @Override
    public int calcolaDanno() {
        int dannoCalcolato=0;
        for (int i=0;i<5; i++){ //dato che è una mitragliatrice, spara piu volte
            if(Math.random()<critChance){
                dannoCalcolato=+danno*2;
            }
            else dannoCalcolato=+danno;
        }
        return dannoCalcolato;
    }

}

package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

public class Mitragliatrice extends Arma {

    public Mitragliatrice(String nome, String Descrizione, int maxCaricatore, int danno, double critChance) {
        super(nome, Descrizione, maxCaricatore, danno, critChance);
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

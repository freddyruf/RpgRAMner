package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

public class Pistola extends Arma {

    public Pistola(String nome, String Descrizione, int maxCaricatore, int danno, double critChance) {
        super(nome, Descrizione, maxCaricatore, danno, critChance);
    }

    @Override
    public int calcolaDanno() {
        if(Math.random()<critChance){
            return danno*2;
        }
        else return danno;
    }


}

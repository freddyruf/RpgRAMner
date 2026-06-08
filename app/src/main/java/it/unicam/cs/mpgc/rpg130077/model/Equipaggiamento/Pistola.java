package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

public class Pistola extends Arma {

    public Pistola(String nome, int maxCaricatore, int caricatore, int danno, double critChance) {
        super(nome, maxCaricatore, caricatore, danno, critChance);
    }

    @Override
    public int calcolaDanno() {
        if(Math.random()<critChance){
            return danno*2;
        }
        else return danno;
    }


}

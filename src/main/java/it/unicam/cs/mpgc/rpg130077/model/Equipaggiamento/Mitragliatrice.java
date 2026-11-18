package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

public class Mitragliatrice extends Arma {
    private static final int NUMERO_COLPI = 5;
    private static final int MOLTIPLICATORE_DANNO_CRITICO = 2;

    public Mitragliatrice(String nome, String Descrizione, int maxCaricatore, int danno, double critChance) {
        super(nome, Descrizione, maxCaricatore, danno, critChance);
    }
    public Mitragliatrice(Mitragliatrice mitragliatrice) {
        super(mitragliatrice);
    }
    public Arma copy(){
        return new Mitragliatrice(this);
    }

    @Override
    public int calcolaDanno() {
        int dannoCalcolato=0;
        for (int i=0;i<NUMERO_COLPI; i++){ //dato che è una mitragliatrice, spara piu volte
            if(getRandom().getRandom()<critChance){
                dannoCalcolato+=(danno*MOLTIPLICATORE_DANNO_CRITICO);
            }
            else dannoCalcolato+=danno;
        }
        return dannoCalcolato;
    }

}

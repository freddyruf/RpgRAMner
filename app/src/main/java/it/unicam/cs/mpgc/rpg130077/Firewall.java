package it.unicam.cs.mpgc.rpg130077;

public class Firewall implements Hack{
    private String nome;
    private String descrizione;
    private int durata;

    public String getNome() {
        return nome;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public int getDurata() {return durata;}

    public Firewall(String nome, String descrizione, int durata) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.durata = durata;
    }

    public void Esegui(Battaglia b) {

    }
    public void EseguiAvanzamento(Battaglia b) {

    }

}

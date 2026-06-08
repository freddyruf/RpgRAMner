package it.unicam.cs.mpgc.rpg130077.model.Hacks;

public abstract class Hack {
    private String nome;
    private String descrizione;
    private int durata;

    public Hack(String nome, String descrizione, int durata) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.durata = durata;
    }

    public String getNome() {
        return nome;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public int getDurata() {return durata;}


}

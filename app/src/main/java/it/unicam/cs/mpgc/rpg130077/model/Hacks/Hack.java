package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;

import java.util.List;

public abstract class Hack {
    private String nome;
    private String descrizione;
    private int durata;
    protected List<Effetto> effetti;

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
    public List<Effetto> getEffetti() {return effetti;}
    public void addEffetto(Effetto effetto){
        effetti.add(effetto);
    }
    public void removeEffetto(Effetto effetto){
        effetti.remove(effetto);
    }


}

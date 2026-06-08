package it.unicam.cs.mpgc.rpg130077;

public interface Hack {
    String getNome();
    String getDescrizione();
    int getDurata();
    void Esegui(Battaglia b);
    void EseguiAvanzamento(Battaglia b);
}

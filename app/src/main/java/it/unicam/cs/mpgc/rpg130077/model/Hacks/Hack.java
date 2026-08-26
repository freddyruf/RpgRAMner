package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;

import java.util.ArrayList;

/**
 * Classe che rappresenta il concetto di Hack: Software che interagisce con la battaglia. E' l'equivalente di un incantesimo per i classici RPG
 */
public class Hack {
    private String nome;
    private String descrizione;
    private int durata;
    protected ArrayList<Effetto> effetti;

    public Hack(String nome, String descrizione, int durata) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.durata = durata;
        this.effetti = new ArrayList<>();
    }
    public Hack(Hack hack) {
        this.nome = hack.getNome();
        this.descrizione = hack.getDescrizione();
        this.durata = hack.getDurata();
        this.effetti = new ArrayList<>();
        for(Effetto effetto : hack.getEffetti()) {
            this.effetti.add(effetto.copy());
        }
    }
    public Hack copy(){
        return new Hack(this);
    }

    public String getNome() {
        return nome;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public int getDurata() {return durata;}
    public ArrayList<Effetto> getEffetti() {return effetti;}
    public void addEffetto(Effetto effetto){
        effetti.add(effetto);
    }
    public void removeEffetto(Effetto effetto){
        effetti.remove(effetto);
    }

    /**
     * Dice se la hack fa danno
     * @return true se causa danni false se non
     */
    public boolean isDamageDealer() {
        for (Effetto effetto : effetti) {
            if (effetto.isDamageDealer())
                return true;
        }
        return false;
    }


    /**
     * Dice se la hack cura
     * @return true se cura e false se non
     */
    public boolean isHealDealer(){
        for(Effetto effetto : effetti){
            if(effetto.isHealDealer())
                return true;
        }
        return false;
    }


}

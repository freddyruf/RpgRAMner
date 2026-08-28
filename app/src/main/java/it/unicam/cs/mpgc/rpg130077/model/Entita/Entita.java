package it.unicam.cs.mpgc.rpg130077.model.Entita;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

import java.util.ArrayList;

/**
 * Classe che astrae il concetto di "Entita": Essere vivente, o perlomeno, dotato di una coscienza
 */
public abstract class Entita {
    private String nome;
    private int pv;
    private int maxPv;
    private String image;
    private int spazioRAM;
    private ArrayList<Hack> hacks;
    private Arma arma;
    private boolean fazione;

    public Entita(String nome, int MaxPv, String image, int spazioRAM, ArrayList<Hack> hacks, Arma arma, boolean fazione) {
        if(nome == null || image == null || hacks == null || arma == null) {
            throw new NullPointerException("I parametri non possono essere nulli");
        }
        if (MaxPv < 0) {
            throw new IllegalArgumentException("Max PV non possono essere negativo");
        }
        this.nome = nome;
        this.maxPv = MaxPv;
        this.pv =MaxPv;
        this.image = image;
        this.spazioRAM = spazioRAM;
        this.hacks = hacks;
        this.arma = arma;
        this.fazione = fazione;
    }
    public Entita(Entita entita) {
        this.nome = entita.nome;
        this.maxPv = entita.maxPv;
        this.pv =entita.pv;
        this.image = entita.image;
        this.spazioRAM = entita.spazioRAM;
        this.hacks = new ArrayList<Hack>(entita.hacks.size());
        for (Hack hack : entita.hacks) {
            this.hacks.add(hack.copy());
        }
        this.arma = entita.arma.copy();
        this.fazione = entita.fazione;
    }
    public abstract Entita copy();
    public String getNome() {
        return nome;
    }
    public int getPv() {
        return pv;
    }
    public int getMaxPv() {
        return maxPv;
    }
    public String getImage() {
        return image;
    }
    public int getSpazioRAM() {
        return spazioRAM;
    }
    public ArrayList<Hack> getHacks(){
        return hacks;
    }
    public Arma getArma() {
        return arma;
    }

    public boolean getFazione(){
        return fazione;
    }
    /**
     * Compara la fazione di questa entità con quella di un'altra entità.
     * @param entita
     * @return true se sono della stessa fazione, false altrimenti
     */
    public boolean compareFazione(Entita entita){
        return fazione == entita.getFazione();
    }
    public void setPv(int newPV) {
        if(newPV > maxPv){
            this.pv = maxPv;
        }
        else if(newPV < 0){
            this.pv = 0;
        }
        else {
            this.pv = newPV;
        }
    }

}

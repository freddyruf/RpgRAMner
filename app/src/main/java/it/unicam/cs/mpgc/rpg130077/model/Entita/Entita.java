package it.unicam.cs.mpgc.rpg130077.model.Entita;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.ArrayList;

/**
 * Classe che astrae il concetto di "Entita": Essere vivente, o perlomeno, dotato di una coscienza
 */
public abstract class Entita {
    private String nome;
    private int PV;
    private int MaxPV;
    private String image;
    private int spazioRAM;
    private ArrayList<Hack> hacks;
    private Arma arma;
    public boolean fazione;

    public Entita(String nome, int MaxPV, String image, int spazioRAM, ArrayList<Hack> hacks, Arma arma, boolean fazione) {
        if(nome == null || image == null || hacks == null || arma == null) {
            throw new NullPointerException("I parametri non possono essere nulli");
        }
        this.nome = nome;
        this.MaxPV = MaxPV;
        this.PV=MaxPV;
        this.image = image;
        this.spazioRAM = spazioRAM;
        this.hacks = hacks;
        this.arma = arma;
        this.fazione = fazione;
    }
    public Entita(Entita entita) {
        this.nome = entita.nome;
        this.MaxPV = entita.MaxPV;
        this.PV=entita.PV;
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
    public int getPV() {
        return PV;
    }
    public int getMaxPV() {
        return MaxPV;
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
    public void setPV(int newPV) {
        if(newPV > MaxPV){
            this.PV = MaxPV;
        }
        else if(newPV < 0){
            this.PV = 0;
        }
        else {
            this.PV = newPV;
        }
    }

}

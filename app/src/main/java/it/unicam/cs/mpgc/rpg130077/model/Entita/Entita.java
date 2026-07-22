package it.unicam.cs.mpgc.rpg130077.model.Entita;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import javafx.scene.image.Image;

import java.util.ArrayList;

public abstract class Entita {
    private String nome;
    private int PV;
    private int MaxPV;
    private String image;
    private int spazioRAM;
    private ArrayList<Hack> hacks;
    private Arma arma;

    public Entita(String nome, int MaxPV, String image, int spazioRAM, ArrayList<Hack> hacks, Arma arma) {
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
    }
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

    public void setPV(int PV) {
        if(PV > MaxPV) throw new IllegalArgumentException();
        this.PV = PV;
    }

    public void spara(Entita entita){
        Azione azione = new AzioneSparo(this, entita);
        azione.esegui(null);
    }
    public void caricaHack(Hack hack, Entita entita, StatoBattaglia stato){
        Azione azione= new AzioneCaricaHack(this, entita, hack);
        azione.esegui(stato);
    }

    public void richiediMossa(SistemaCombattimento sistemaTurni, StatoBattaglia stato){
        throw new UnsupportedOperationException("Not supported.");
    }

}

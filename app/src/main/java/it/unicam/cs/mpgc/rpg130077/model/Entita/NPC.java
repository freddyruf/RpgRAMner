package it.unicam.cs.mpgc.rpg130077.model.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.ArrayList;

public class NPC extends Entita {
    int dannoAttaccoASorpresa;
    double chanceAttaccoASorpresa; // Min 0 MAX 1.0
    StrategiaCombattimento strategia;

    public NPC(String nome, int MaxPV, String image, int spazioRAM, ArrayList<Hack> hacks, Arma arma, int dannoAttaccoASorpresa, double chanceAttaccoASorpresa,  StrategiaCombattimento strategia) {
        super(nome, MaxPV, image, spazioRAM, hacks, arma);
        if(chanceAttaccoASorpresa>1 ){
            throw new IllegalArgumentException("Chance attacco ASorpresa non valido");
        }
        this.dannoAttaccoASorpresa = dannoAttaccoASorpresa;
        this.chanceAttaccoASorpresa = chanceAttaccoASorpresa;
        this.strategia = strategia;

    }
    public NPC(NPC npc){
        super(npc);
        this.dannoAttaccoASorpresa = npc.dannoAttaccoASorpresa;
        this.chanceAttaccoASorpresa = npc.chanceAttaccoASorpresa;
        this.strategia = npc.strategia;
    }

    public Entita copy(){
        return new NPC(this);
    }

    public boolean controllaAttaccoASorpresa(){
        if(chanceAttaccoASorpresa*Math.random() <= 0.5){
            return true;
        }
        else return false;
    }
    public int getDannoAttaccoASorpresa() {
        return dannoAttaccoASorpresa;
    }

    /**
     * Sceglie e ritorna una mossa
     *
     * @param sistemaCombattimento
     * @param stato                stato della batttaglia
     * @return mossa scelta
     *
     */
    @Override
    public Azione richiediMossa(SistemaCombattimento sistemaCombattimento, StatoBattaglia stato) {
        return strategia.scegliMossa(this, stato);

    }
}
package it.unicam.cs.mpgc.rpg130077.model.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.ArrayList;

public class NPC extends Entita implements CombattenteAutonomo {
    int dannoAttaccoASorpresa;
    double chanceAttaccoASorpresa; // Min 0 MAX 0.4, dopo 0.4 diventa troppo frequente
    StrategiaCombattimento strategia;

    public NPC(String nome, int MaxPV, String image, int spazioRAM, ArrayList<Hack> hacks, Arma arma, int dannoAttaccoASorpresa, double chanceAttaccoASorpresa,  StrategiaCombattimento strategia, boolean fazione) {
        super(nome, MaxPV, image, spazioRAM, hacks, arma, fazione);
        if (chanceAttaccoASorpresa < 0.0 || chanceAttaccoASorpresa > 1.0) {
            throw new IllegalArgumentException("La probabilità di attacco a sorpresa deve essere tra 0.0 e 1.0");
        }
        if (strategia == null) {
            throw new NullPointerException("La strategia non può essere nulla");
        }
        if (chanceAttaccoASorpresa > 0.0 && dannoAttaccoASorpresa <= 0) {
            throw new IllegalArgumentException("Se c'è probabilità di attacco a sorpresa, il danno deve essere > 0");
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
        return Math.random() < chanceAttaccoASorpresa;
    }
    public int getDannoAttaccoASorpresa() {
        return dannoAttaccoASorpresa;
    }

    /**
     * Sceglie e ritorna una mossa
     *
     * @param stato stato della batttaglia
     * @return mossa scelta
     *
     */
    @Override
    public Azione richiediMossa(StatoBattaglia stato) {
        // L'IA decide in base allo stato attuale (hp, nemici)
        return strategia.scegliMossa(this, stato);
    }
}
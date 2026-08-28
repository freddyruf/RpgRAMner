package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class AzioneSparo implements Azione {
    private Entita lanciatore;
    private Entita bersaglio;

    public AzioneSparo(Entita lanciatore, Entita bersaglio) {
        this.lanciatore = lanciatore;
        this.bersaglio = bersaglio;
    }
    public boolean isDamageDealer() {
        return true;
    }
    public boolean isHealDealer() {
        return false;
    }

    @Override
    public void esegui( StatoBattaglia stato) {
        if(lanciatore==null || bersaglio==null){
            throw new NullPointerException("Lanciatore o Bersaglio nullo");
        }
        bersaglio.setPv(bersaglio.getPv()-lanciatore.getArma().calcolaDanno());
    }
}

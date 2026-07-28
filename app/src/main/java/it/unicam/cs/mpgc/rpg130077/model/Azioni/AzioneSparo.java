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

    @Override
    public void esegui( StatoBattaglia stato) {
        bersaglio.setPV(bersaglio.getPV()-lanciatore.getArma().calcolaDanno());
    }
}

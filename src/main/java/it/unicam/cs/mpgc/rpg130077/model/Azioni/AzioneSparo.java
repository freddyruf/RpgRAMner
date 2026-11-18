package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.ArrayList;

public class AzioneSparo implements Azione {
    private Entita lanciatore;
    private Entita bersaglio;

    public AzioneSparo(Entita lanciatore, Entita bersaglio) {
        this.lanciatore = lanciatore;
        this.bersaglio = bersaglio;
    }

    @Override
    public void esegui( StatoBattaglia stato) {
        if(lanciatore==null || bersaglio==null){
            throw new NullPointerException("Lanciatore o Bersaglio nullo");
        }
        Arma arma = lanciatore.getArma();
        bersaglio.setPv(bersaglio.getPv() - arma.calcolaDanno());

        for (Effetto effetto : arma.getEffetti()) {
            effetto.eseguiEffetto(stato, lanciatore, bersaglio);
        }
    }

    @Override
    public ArrayList<EffectType> getEffectTypes() {
        return lanciatore != null && lanciatore.getArma() != null
                ? lanciatore.getArma().getEffectTypes()
                : new ArrayList<>(java.util.Arrays.asList(EffectType.DAMAGE));
    }

}

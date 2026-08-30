package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.EnumSet;
import java.util.Set;


public class AzioneCaricaHack implements Azione {
    private Entita lanciatore;
    private Entita bersaglio;
    private Hack hack;

    public AzioneCaricaHack(Entita lanciatore, Entita bersaglio, Hack hack) {
        if (lanciatore == null || bersaglio == null || hack == null) {
            throw new NullPointerException("Lanciatore, bersaglio e hack non possono essere nulli");
        }
        this.lanciatore = lanciatore;
        this.bersaglio = bersaglio;
        this.hack = hack;

    }

    @Override
    public Set<EffectType> getEffectTypes() {
        return hack.getEffectTypes();
    }

    public Hack getHack() {
        return hack;
    }

    @Override
    public void esegui(StatoBattaglia stato) {
        stato.getRamCondivisa().inserisci(hack, bersaglio, lanciatore);
    }
}

package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class AzioneCaricaHack implements Azione {
    private Entita lanciatore;
    private Entita bersaglio;
    private Hack hack;

    public AzioneCaricaHack(Entita lanciatore, Entita bersaglio, Hack hack) {
        this.lanciatore = lanciatore;
        this.bersaglio = bersaglio;
        this.hack = hack;

    }

    @Override
    public void esegui(StatoBattaglia stato) {
        stato.getRamCondivisa().inserisci(hack, bersaglio, lanciatore);
    }
}

package it.unicam.cs.mpgc.rpg130077.model.IA;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

public class StrategiaCasuale implements StrategiaCombattimento{

    @Override
    public Azione scegliMossa(NPC npc, StatoBattaglia stato) {
        //Scelgo un bersaglio a caso
        int BersaglioIndex = (int) (Math.random() * stato.getFazioneEroi().size());
        Entita entitaBersaglio=stato.getFazioneEroi().get(BersaglioIndex);

        double sceltaMossa=Math.random();

        if(npc.getArma()==null || sceltaMossa<0.5){ //hack
            if (npc.getHacks().isEmpty()) {
                return new AzioneSparo(npc, entitaBersaglio);
            }

            int indiceHack = (int) (Math.random() * npc.getHacks().size());
            Hack hack = npc.getHacks().get(indiceHack);

            int spazioOccupato = stato.getRamCondivisa().getSpazioOccupato();
            int spazioMassimo = stato.getRamCondivisa().getSpazioMassimoInSecondi();
            if (spazioOccupato + hack.getDurata() > spazioMassimo) {
                return new AzioneSparo(npc, entitaBersaglio);
            }
            return new AzioneCaricaHack(npc, entitaBersaglio, hack);
        }
        else{ //arma
            return new AzioneSparo(npc, entitaBersaglio);
        }
    }
}

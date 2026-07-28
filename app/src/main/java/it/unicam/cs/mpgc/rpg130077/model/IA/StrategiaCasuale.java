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
        //Scelgo un bersaglio alleato a caso
        int alleatoIndex=(int) Math.random()*stato.getFazioneEroi().size();
        Entita entitaBersaglio=stato.getFazioneEroi().get(alleatoIndex);

        double sceltaMossa=Math.random();
        if(npc.getArma()==null || sceltaMossa<0.5){ //hack
            //Randomizzo l'Hack
            double sceltaHack=Math.random();
            Hack hack;
            if(sceltaMossa<0.25){
                hack=npc.getHacks().get(0);
            }
            else if(sceltaMossa<0.5){
                hack=npc.getHacks().get(1);
            }
            else if(sceltaMossa<0.75){
                hack=npc.getHacks().get(2);
            }
            else{
                hack=npc.getHacks().get(3);
            }
            return new AzioneCaricaHack(npc, entitaBersaglio, hack);
        }
        else{ //arma
            return new AzioneSparo(npc, entitaBersaglio);
        }
    }
}

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

        boolean puoCaricareHack = false;
        Hack hackSelezionato = null;

        if (!npc.getHacks().isEmpty()) {
            int indiceHack = (int) (Math.random() * npc.getHacks().size());
            Hack candidato = npc.getHacks().get(indiceHack);
            int spazioOccupato = stato.getRamCondivisa().getSpazioOccupato();
            int spazioMassimo = stato.getRamCondivisa().getSpazioMassimoInSecondi();
            if (spazioOccupato + candidato.getDurata() <= spazioMassimo) {
                puoCaricareHack = true;
                hackSelezionato = candidato;
            }
        }

        if (puoCaricareHack && (npc.getArma() == null || sceltaMossa < 0.5)) {
            return new AzioneCaricaHack(npc, entitaBersaglio, hackSelezionato);
        } else {
            if (npc.getArma() == null) { //Non puo ne sparare ne caricare una hack
                return null;
            }
            return new AzioneSparo(npc, entitaBersaglio);
        }
        }
    }

package it.unicam.cs.mpgc.rpg130077.model.IA;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;

import java.util.ArrayList;

public class StrategiaCasuale implements StrategiaCombattimento {

    @Override
    public Azione scegliMossa(NPC npc, StatoBattaglia stato) {
        if (npc == null || stato == null) {
            throw new NullPointerException("Parametri non validi");
        }
        ArrayList<Entita> eroi = stato.getFazioneEroi();
        if (eroi.isEmpty()) return null;

        int bersaglioIndex = (int) (Math.random() * eroi.size());
        Entita entitaBersaglio = eroi.get(bersaglioIndex);

        boolean puoCaricareHack = false;
        Hack hackSelezionato = null;

        if (!npc.getHacks().isEmpty()) {
            int indiceHack = (int) (Math.random() * npc.getHacks().size());
            Hack candidato = npc.getHacks().get(indiceHack);
            if (stato.getRamCondivisa().getSpazioOccupato() + candidato.getDurata() <= stato.getRamCondivisa().getSpazioMassimoInSecondi()) {
                puoCaricareHack = true;
                hackSelezionato = candidato;
            }
        }

        if (puoCaricareHack && (npc.getArma() == null || Math.random() < 0.5)) {
            Entita target = hackSelezionato.isHealDealer() ? npc : entitaBersaglio;
            return new AzioneCaricaHack(npc, target, hackSelezionato);
        } else if (npc.getArma() != null) {
            return new AzioneSparo(npc, entitaBersaglio);
        }
        return null; // Azione No-Op sicura
    }
}

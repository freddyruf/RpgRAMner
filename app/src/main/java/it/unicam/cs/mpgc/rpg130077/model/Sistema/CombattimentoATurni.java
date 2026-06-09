package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

import java.util.ArrayList;

public class CombattimentoATurni implements SistemaCombattimento {


    @Override
    public boolean avanza(StatoBattaglia stato) {
        return false;
    }


    //@return l'entita che ha vinto o null
    @Override
    public Entita checkVittoria(StatoBattaglia stato) {
        ArrayList<Entita> eroi = stato.getFazioneEroi();
        ArrayList<Entita> nemici = stato.getFazioneNemici();

        boolean eroiSconfitti = true;
        boolean nemiciSconfitti = true;

        // Controlla se c'è almeno un eroe vivo
        for (Entita eroe : eroi) {
            if (eroe.getPV() > 0) {
                eroiSconfitti = false;
                break;
            }
        }

        // Controlla se c'è almeno un nemico vivo
        for (Entita nemico : nemici) {
            if (nemico.getPV() > 0) {
                nemiciSconfitti = false;
                break;
            }
        }

        // Determina il risultato
        if (eroiSconfitti) {
            // Se gli eroi sono morti, restituiamo il primo nemico come vincitore simbolico
            return nemici.get(0);
        } else if (nemiciSconfitti) {
            // Se i nemici sono morti, restituiamo il giocatore
            return eroi.get(0);
        }

        // Nessuno ha ancora vinto, lo scontro continua
        return null;
    }
}

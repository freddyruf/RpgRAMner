package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

import java.util.ArrayList;

public class CombattimentoATurni implements SistemaCombattimento {

    //TODO
    @Override
    public boolean avanza(StatoBattaglia stato) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    /**
     *
     * @param stato stato della battaglia
     * @return true se il giocatore ha vinto, false se ha perso o null se non ha vinto nessuno ancora
     */
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

package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

import java.util.ArrayList;

public class CombattimentoATurni implements SistemaCombattimento {

    StatoBattaglia stato;
    StatoTurni statoTurni;

    public CombattimentoATurni(StatoBattaglia stato) {
        this.stato = stato;
        statoTurni=new StatoTurni(stato.getFazioneEroi().size(), stato.getFazioneNemici().size());
    }

    /**
     *
     * @return Entita che sta svolgendo il turno
     */
    public Entita getEntitaInCorso(){
        int turno=statoTurni.getTurno();
        if(turno<stato.getFazioneEroi().size()){
            return stato.getEroe(turno);
        }else{
            return stato.getNemico(turno-stato.getFazioneEroi().size());
        }
    }

    //TODO
    @Override
    public void avanza() {

        statoTurni.avanzaTurno();
        Entita entitaInCorso=getEntitaInCorso();
        if(entitaInCorso instanceof NPC){ //il nemico fa una mossa
            entitaInCorso.richiediMossa(this,stato);
        }
        else{

        }
    }

    @Override
    public StatoBattaglia getStatoBattaglia() {
        return stato;
    }

    public void eseguiMossa(Azione azione) {
        azione.esegui(stato);
        statoTurni.avanzaTurno();

        if (checkVittoria() == null) {
            avanza();
        }
    }


    /**
     * Controlla se il giocatore ha vinto o perso lo scontro.
     * @return true se il giocatore ha vinto, false se ha perso o null se non ha vinto nessuno ancora
     */
    @Override
    public Entita checkVittoria() {
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

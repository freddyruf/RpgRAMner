package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.controller.logica.CombattimentoListener;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;

public class CombattimentoATurni  implements SistemaCombattimento {

    StatoBattaglia stato;
    StatoTurni statoTurni;

    private Timeline clock;

    private ArrayList<CombattimentoListener> listeners= new ArrayList<>();

    public void aggiungiListener(CombattimentoListener combattimentoListener) {
        listeners.add(combattimentoListener);
    }

    private void notificaThick(){
        for(CombattimentoListener combattimentoListener : listeners){
            combattimentoListener.onTick(stato);
        }
    }

    public CombattimentoATurni(StatoBattaglia stato) {
        this.stato = stato;
        statoTurni=new StatoTurni(stato.getFazioneEroi().size(), stato.getFazioneNemici().size());
        inizializzaClock();
    }

    private void inizializzaClock() {
        clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            stato.getRamCondivisa().avanza(stato);
            notificaThick();
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
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
            for(CombattimentoListener listener : listeners){
                listener.onTurnoGiocatore((Giocatore) entitaInCorso);
            }
        }
    }

    @Override
    public StatoBattaglia getStatoBattaglia() {
        return stato;
    }

    public void sparare(){
        Azione azione=new AzioneSparo(getEntitaInCorso(),getStatoBattaglia().getNemico(0));
        eseguiMossa(azione);
    }

    public void caricaHack(Hack hack) {
        Azione azione = new AzioneCaricaHack(getEntitaInCorso(), getStatoBattaglia().getNemico(0), hack);
        eseguiMossa(azione);
    }

    public void eseguiMossa(Azione azione) {
        azione.esegui(stato);
        statoTurni.avanzaTurno();

        if (checkVittoria() == null) {
            avanza();
        }
        //se l'azione fa danno o cura
        if((azione instanceof AzioneSparo) || ((azione instanceof AzioneCaricaHack) && ((AzioneCaricaHack) azione).getHack().isHealDealer() || ((AzioneCaricaHack) azione).getHack().isDamageDealer())){
            //aggiorno le barre della vita
            for(CombattimentoListener combattimentoListener : listeners){
                combattimentoListener.onVitaAggiornata(stato);
            }
        }
        else{
            for(CombattimentoListener combattimentoListener : listeners){
                combattimentoListener.aggiornaRAM(stato.getRamCondivisa());
            }
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
            for (CombattimentoListener listener : listeners) {
                listener.onVittoria(nemici.get(0));
            }
            return nemici.get(0);
        } else if (nemiciSconfitti) {
            // Se i nemici sono morti, restituiamo il giocatore
            for (CombattimentoListener listener : listeners) {
                listener.onVittoria(eroi.get(0));
            }
            return eroi.get(0);
        }

        // Nessuno ha ancora vinto, lo scontro continua
        return null;
    }
}

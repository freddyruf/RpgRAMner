package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.controller.logica.CombattimentoListener;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;



import java.util.ArrayList;

public class CombattimentoATurni  implements SistemaCombattimento {

    private StatoBattaglia stato;
    private StatoTurni statoTurni;
    private Clock clock;


    private ArrayList<CombattimentoListener> listeners= new ArrayList<>();

    private CombattimentoATurni backup;

    public void aggiungiListener(CombattimentoListener combattimentoListener) {
        listeners.add(combattimentoListener);
    }

    public boolean isPlayerTurn() {
        return statoTurni.getTurno() < stato.getFazioneEroi().size();
    }

    public void onTick(){
        stato.getRamCondivisa().avanza(stato);
        notificaThick();
    }

    private void notificaThick(){

        for(CombattimentoListener combattimentoListener : listeners){
            combattimentoListener.onTick(stato);
        }
    }

    public CombattimentoATurni(StatoBattaglia stato) {
        this.stato = stato;
        statoTurni=new StatoTurni(stato.getFazioneEroi().size(), stato.getFazioneNemici().size());

        //clono il combattimento per salvarlo allo stato originario
        backup = new CombattimentoATurni(this);
    }
    public CombattimentoATurni(CombattimentoATurni combattimentoATurni){
        this.stato=combattimentoATurni.stato.copy();
        this.statoTurni=new StatoTurni(combattimentoATurni.statoTurni);
    }
    public void ripristina(){

        this.stato=backup.getStatoBattaglia().copy();
        this.statoTurni=new StatoTurni(stato.getFazioneEroi().size(), stato.getFazioneNemici().size());

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

    @Override
    public void avanza() {

        statoTurni.avanzaTurno();
        Entita entitaInCorso=getEntitaInCorso();

        if(entitaInCorso instanceof NPC){ //il nemico fa una mossa
            Azione a=entitaInCorso.richiediMossa(this,stato);
            eseguiMossa(a);
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

        if (checkVittoria() == null) {
            avanza();
        }

        //se l'azione fa danno o cura
        if(azione.isDamageDealer() || azione.isHealDealer()){
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

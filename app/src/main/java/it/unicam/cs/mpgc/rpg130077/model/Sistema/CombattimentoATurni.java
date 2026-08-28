package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Entita.CombattenteAutonomo;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;



import java.util.ArrayList;

public class CombattimentoATurni  implements SistemaCombattimento {

    private StatoBattaglia stato;
    private StatoTurni statoTurni;
    private boolean vittoriaNotificata=false;


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
        notificaTick();
        checkVittoria();
    }

    private void notificaTick(){

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
        vittoriaNotificata=false;

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

        if(entitaInCorso instanceof CombattenteAutonomo){ //il nemico fa una mossa
            Azione a=((CombattenteAutonomo)entitaInCorso).richiediMossa(stato);
            if(a==null){
                for(CombattimentoListener listener : listeners){
                    listener.ilNemicoNonPuoAttaccare();
                }
            }
            else eseguiMossa(a);
        }
    }

    @Override
    public StatoBattaglia getStatoBattaglia() {
        return stato;
    }

    public void spara(Entita bersaglio) {
        if (bersaglio == null) {
            throw new NullPointerException("Il bersaglio non può essere nullo");
        }
        eseguiMossa(new AzioneSparo(getEntitaInCorso(), bersaglio));
    }

    public void caricaHack(Hack hack, Entita bersaglio) {
        if (hack == null || bersaglio == null) {
            throw new NullPointerException("Hack e bersaglio non possono essere nulli");
        }
        eseguiMossa(new AzioneCaricaHack(getEntitaInCorso(), bersaglio, hack));
    }

    public void eseguiMossa(Azione azione) {
        azione.esegui(stato);
        if(azione instanceof AzioneCaricaHack) {
            for(CombattimentoListener combattimentoListener : listeners){
                combattimentoListener.onAggiornamentoRAM(stato.getRamCondivisa());
            }
        }
        //se l'azione fa danno o cura
        if(azione.isDamageDealer() || azione.isHealDealer()){
            //aggiorno le barre della vita
            for(CombattimentoListener combattimentoListener : listeners){
                combattimentoListener.onVitaAggiornata(stato);
            }
        }
        if (checkVittoria() == null) {
            avanza();
        }


    }


    /**
     * Controlla se una delle fazioni ha vinto lo scontro.
     *
     * @return L'istanza di Entita vincitrice, o null se lo scontro è ancora in corso.
     */
    public Entita checkVittoria() {
        ArrayList<Entita> eroi = stato.getFazioneEroi();
        ArrayList<Entita> nemici = stato.getFazioneNemici();

        boolean eroiSconfitti = true;
        boolean nemiciSconfitti = true;

        // Controlla se c'è almeno un eroe vivo
        for (Entita eroe : eroi) {
            if (eroe.getPv() > 0) {
                eroiSconfitti = false;
                break;
            }
        }
        // Controlla se c'è almeno un nemico vivo
        for (Entita nemico : nemici) {
            if (nemico.getPv() > 0) {
                nemiciSconfitti = false;
                break;
            }
        }

        if (eroiSconfitti && nemiciSconfitti) { //pareggio
            if (!vittoriaNotificata) {
                vittoriaNotificata = true;
                for (CombattimentoListener l : listeners) {
                    l.onVittoria(null);
                }
            }
            return null;
        } else if (eroiSconfitti) { //sconfitta
            if (!vittoriaNotificata) {
                vittoriaNotificata = true;
                for (CombattimentoListener l : listeners) {
                    l.onVittoria(nemici.get(0));
                }
            }
            return nemici.get(0);
        } else if (nemiciSconfitti) { //vittoria
            if (!vittoriaNotificata) {
                vittoriaNotificata = true;
                for (CombattimentoListener l : listeners) {
                    l.onVittoria(eroi.get(0));
                }
            }
            return eroi.get(0);
        }
        // Nessuno ha ancora vinto, lo scontro continua
        return null;
    }
    @Override
    public void rimuoviListener(CombattimentoListener combattimentoListener) {
        if (combattimentoListener != null) {
            listeners.remove(combattimentoListener);
        }
    }
}

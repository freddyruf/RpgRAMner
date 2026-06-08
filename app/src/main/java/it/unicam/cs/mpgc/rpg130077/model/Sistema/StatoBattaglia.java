package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.RAM;

public class StatoBattaglia {
    private Giocatore giocatore;
    private NPC avversario;
    private RAM ram;
    private int clock;
    private double velocita;

    private boolean turno; //0 giocatore 1 avversario

    public StatoBattaglia(Giocatore giocatore, NPC avversario, int clock, double velocita) {
        // Controllo che i parametri non sono nulli
        if(giocatore == null || avversario == null) {
            throw new NullPointerException("I parametri non possono essere nulli");
        }

        this.giocatore = giocatore;
        this.avversario = avversario;
        this.clock = clock;
        this.velocita = velocita;
        this.ram=new RAM(giocatore.getSpazioRAM()+avversario.getSpazioRAM());
    }

    public Giocatore getGiocatore() {
        return giocatore;
    }
    public NPC getAvversario() {
        return avversario;
    }

    public void cambiaTurno(){
        this.turno= !turno;
    }
    public boolean getTurno() {
        return turno;
    }
    public Boolean avanza(){
        //da implementare

        return checkVittoria();
    }
    public Boolean checkVittoria(){
        if(giocatore.getPV()<=0){
            return false;
        }
        else if(avversario.getPV()<=0){
            return true;
        }
        else return null;
    }


}

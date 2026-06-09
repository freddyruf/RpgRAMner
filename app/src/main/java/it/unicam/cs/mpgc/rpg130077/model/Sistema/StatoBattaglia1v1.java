package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.RAM;

import java.util.ArrayList;

public class StatoBattaglia1v1 implements StatoBattaglia {
    private Giocatore giocatore;
    private NPC avversario;
    private RAM ram;
    private int clock;
    private double velocita;
    private boolean turno; //0 giocatore 1 avversario

    public StatoBattaglia1v1(Giocatore giocatore, NPC avversario) {
        // Controllo che i parametri non sono nulli
        if(giocatore == null || avversario == null) {
            throw new NullPointerException("I parametri non possono essere nulli");
        }

        this.giocatore = giocatore;
        this.avversario = avversario;
        this.clock = 0;
        this.velocita = 1;
        this.ram=new RAM(giocatore.getSpazioRAM()+avversario.getSpazioRAM());
    }

    public double getVelocita() {
        return velocita;
    }
    public void setVelocita(double velocita) {
        this.velocita = velocita;
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

    @Override
    public RAM getRamCondivisa() {
        return ram;
    }

    @Override
    public int getClock() {
        return clock;
    }

    @Override
    public void incrementaClock() {
        clock=clock+1;
    }

    @Override
    public ArrayList<Entita> getFazioneEroi() {
        ArrayList<Entita> eroi = new ArrayList<>();
        eroi.add(giocatore);
        return eroi;
    }

    @Override
    public ArrayList<Entita> getFazioneNemici() {
        ArrayList<Entita> nemici = new ArrayList<>();
        nemici.add(avversario);
        return nemici;
    }


}

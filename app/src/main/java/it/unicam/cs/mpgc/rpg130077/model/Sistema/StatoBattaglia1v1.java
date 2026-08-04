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
    public StatoBattaglia1v1(StatoBattaglia1v1 s){
        this.giocatore=(Giocatore) s.giocatore.Copy();
        this.avversario=(NPC) s.avversario.Copy();
        this.clock=s.clock;
        this.velocita=s.velocita;
        this.ram=new RAM(s.getRamCondivisa());
    }
    public StatoBattaglia Copy(){
        return new StatoBattaglia1v1(this);
    }

    public double getVelocita() {
        return velocita;
    }
    public void setVelocita(double velocita) {
        this.velocita = velocita;
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
    public Giocatore getGiocatore() {
        return giocatore;
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

    @Override
    public Entita getEroe(int n) {
        return giocatore;
    }

    @Override
    public Entita getNemico(int n) {
        return avversario;
    }


}

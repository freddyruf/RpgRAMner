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

    public StatoBattaglia1v1(Giocatore giocatore, NPC avversario) {
        // Controllo che i parametri non sono nulli
        if(giocatore == null || avversario == null) {
            throw new NullPointerException("I parametri non possono essere nulli");
        }

        this.giocatore = giocatore;
        this.avversario = avversario;
        this.ram=new RAM(giocatore.getSpazioRAM()+avversario.getSpazioRAM());
    }
    public StatoBattaglia1v1(StatoBattaglia1v1 s){
        this.giocatore=(Giocatore) s.giocatore.copy();
        this.avversario=(NPC) s.avversario.copy();
        this.ram=new RAM(s.getRamCondivisa());
    }
    public StatoBattaglia copy(){
        return new StatoBattaglia1v1(this);
    }


    @Override
    public RAM getRamCondivisa() {
        return ram;
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

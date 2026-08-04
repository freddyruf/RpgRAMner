package it.unicam.cs.mpgc.rpg130077.model.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Giocatore extends Entita {
    public Giocatore(String nome, int MaxPV, String image, int spazioRAM, ArrayList<Hack> hacks, Arma arma) {
        super(nome, MaxPV, image, spazioRAM, hacks, arma);
    }
    public Giocatore(Giocatore giocatore){
        super(giocatore);
    }
    public Entita Copy(){
        return new Giocatore(this);
    }

}

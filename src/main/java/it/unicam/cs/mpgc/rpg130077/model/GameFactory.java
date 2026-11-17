package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameFactory {
    List<Arma> equipaggiamentoGiocatoreArmi;
    List<Hack> equipaggiamentoGiocatoreHacks;
    List<Arma> equipaggiamentoNemicoArmi;
    List<Hack> equipaggiamentoNemicoHacks;
    List<Arma> catalogoArmi;
    List<Hack> catalogoHack;
    NPC nemico=null;
    StrategiaCasuale strategia;

    public SistemaCombattimento creaNuovaPartitaSemplice() {
        RandomNumbersGenerator random = null;

        if (nemico==null){
            //Se non sono state caricate le hacks le sceglie a random
            if(equipaggiamentoNemicoHacks.isEmpty()){
                //Genero le hack del nemico
                for (int i = 0; i < 5; i++) {
                    Hack h=catalogoHack.get(random.getInt(0,catalogoHack.size()));
                    if(!equipaggiamentoNemicoHacks.contains(h)){
                        equipaggiamentoNemicoHacks.add(h);
                    }
                    else  { //se gia è dentro ripeto il processo
                        i--;
                        continue;
                    }
                }
            }
            //Se non sono state caricate le armi le sceglie a random
            if(equipaggiamentoNemicoArmi.isEmpty()){
                //Genero l'arma del nemico
                equipaggiamentoNemicoArmi.add(catalogoArmi.get(random.getInt(0,catalogoArmi.size())));
            }

        }


        Giocatore giocatore = new Giocatore("Giocatore", 100, "", 10, (ArrayList<Hack>) equipaggiamentoGiocatoreHacks, equipaggiamentoGiocatoreArmi.get(0).copy(), true);
        //TODO NPC nemico = new NPC("Cybermorb", 100, "", 5, (ArrayList<Hack>) equipaggiamentoNemicoHacks, equipaggiamentoNemicoArmi.get(0), 5, 0.1, strategia, false);

        return new CombattimentoATurni(new StatoBattaglia1v1(giocatore, nemico));
    }

    public void setEquipaggiamentoGiocatoreArmi(List<Arma> equipaggiamentoGiocatoreArmi) {
        this.equipaggiamentoGiocatoreArmi = equipaggiamentoGiocatoreArmi;
    }
    public void setCatalogoArmi(List<Arma> catalogoArmi) {
        this.catalogoArmi = catalogoArmi;
    }
    public void setCatalogoHack(List<Hack> catalogoHack) {
        this.catalogoHack = catalogoHack;
    }
    public void equipaggiamentoGiocatoreHacks(List<Hack> equipaggiamentoGiocatoreHacks) {
        this.equipaggiamentoGiocatoreHacks = equipaggiamentoGiocatoreHacks;
    }
    public void setStrategia(StrategiaCasuale strategia) {
        this.strategia = strategia;
    }
}
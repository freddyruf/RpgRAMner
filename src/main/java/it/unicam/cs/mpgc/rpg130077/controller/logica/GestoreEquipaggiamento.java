package it.unicam.cs.mpgc.rpg130077.controller.logica;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

import java.util.ArrayList;

public interface GestoreEquipaggiamento {
    String getDescrizioneItem(String nomeItem);
    boolean hasConfigurazioneSalvata();
    ArrayList<Arma> getArmiSalvate();
    ArrayList<Hack> getHacksSalvati();
    ArrayList<Arma> getCatalogoArmi();
    ArrayList<Hack> getCatalogoHacks();
    void salva(ArrayList<String> configurazione);
}
package it.unicam.cs.mpgc.rpg130077.controller;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

import java.util.ArrayList;

public class GestoreArmamento {
    private final ArrayList<Arma> catalogoArmi;
    private final ArrayList<Hack> catalogoHacks;

    public GestoreArmamento() {
        this.catalogoArmi = new ArrayList<>();
        this.catalogoHacks = new ArrayList<>();
        inizializzaCatalogo(); // Riempie i cataloghi appena l'oggetto viene creato
    }


    private void inizializzaCatalogo() {

    }



}

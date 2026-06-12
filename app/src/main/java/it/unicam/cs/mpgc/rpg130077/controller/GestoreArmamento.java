package it.unicam.cs.mpgc.rpg130077.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamento;

import java.awt.event.ActionEvent;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestoreArmamento {
    private final ArrayList<Arma> catalogoArmi;
    private final ArrayList<Hack> catalogoHacks;
    private final persistenzaArmamento gestoreSalvataggi;
    private String FILE;

    public GestoreArmamento(persistenzaArmamento gestoreSalvataggi, String FILE) {
        this.catalogoArmi = gestoreSalvataggi.CaricamentoCatalogoArmi();
        this.catalogoHacks = gestoreSalvataggi.CaricamentoCatalogoHacks();
        this.gestoreSalvataggi = gestoreSalvataggi;
        this.FILE = FILE;
    }

    public void salva(ArrayList<String> setupScelto) {
        ArrayList<Hack> hacks = new ArrayList<>();
        ArrayList<Arma> armi = new ArrayList<>();

        System.out.println("Armi: "+ catalogoArmi.toString());
        System.out.println("Hacks: "+ catalogoHacks.toString());

        for (String s : setupScelto) {
            for(Hack hack : catalogoHacks){
                if(s.equals(hack.getNome())){
                    System.out.println(hack.getNome());
                    System.out.println(s);
                    hacks.add(hack);
                }
            }
            for(Arma arma : catalogoArmi){
                if(s.equals(arma.getNome())){
                    armi.add(arma);
                }
            }
        }
        Map<String, Object> datiDaSalvare = new HashMap<>();
        datiDaSalvare.put("armi", armi);
        datiDaSalvare.put("hacks", hacks);


        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(FILE));
            new Gson().toJson(datiDaSalvare, out);
            out.close();

        } catch (IOException e) {
            // Se il file non esiste (es. prima partita), restituisce una lista vuota invece di crashare
            System.out.println("FILE non trovato");
        }

    }



}

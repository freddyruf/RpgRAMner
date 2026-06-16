package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.*;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg130077.util.RuntimeTypeAdapterFactory;

import java.lang.reflect.Type;

public class persistenzaCatalogoArmamentoJSON implements CaricatoreCatalogo {
    private static InputStreamReader fileReaderArmi;
    private static InputStreamReader fileReaderHacks;

    public persistenzaCatalogoArmamentoJSON() {
        try {
            // Carica i file dai resources
            InputStream isArmi = getClass().getResourceAsStream("/catalogo_armi.json");
            InputStream isHacks = getClass().getResourceAsStream("/catalogo_hacks.json");

            if (isArmi == null || isHacks == null) {
                throw new RuntimeException("File di catalogo non trovati nei resources!");
            }

            this.fileReaderArmi = new InputStreamReader(isArmi, StandardCharsets.UTF_8);
            this.fileReaderHacks = new InputStreamReader(isHacks, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: cambiare metodo per i principi SOLID

    public ArrayList<Arma> CaricamentoCatalogoArmi() {
        RuntimeTypeAdapterFactory<Arma> armaAdapter = RuntimeTypeAdapterFactory.of(Arma.class, "tipo")
                .registerSubtype(Pistola.class, "Pistola")
                .registerSubtype(Mitragliatrice.class, "Mitragliatrice");

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(armaAdapter)
                .create();

        Type tipoListaArmi = new TypeToken<ArrayList<Arma>>(){}.getType();
        ArrayList<Arma> catalogoArmi = gson.fromJson(fileReaderArmi, tipoListaArmi);
        return catalogoArmi != null ? catalogoArmi : new ArrayList<>();
    }

    //TODO: cambiare metodo per i principi SOLID
    public ArrayList<Hack> CaricamentoCatalogoHack() {
        RuntimeTypeAdapterFactory<Hack> hackAdapter = RuntimeTypeAdapterFactory.of(Hack.class, "tipo")
                .registerSubtype(Acid.class, "Acid")
                .registerSubtype(Fireball.class, "Fireball")
                .registerSubtype(Firewall.class, "Firewall")
                .registerSubtype(RAMReverse.class, "RAM:Reverse")
                .registerSubtype(RAMSort.class, "RAM:Sort");

        RuntimeTypeAdapterFactory<Effetto> effettoAdapter = RuntimeTypeAdapterFactory.of(Effetto.class, "tipoEffetto")
                .registerSubtype(EffettoDanno.class, "Danno")
                .registerSubtype(EffettoCura.class, "Cura")
                .registerSubtype(EffettoReverse.class, "Reverse")
                .registerSubtype(EffettoSort.class, "Sort");

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(hackAdapter)
                .registerTypeAdapterFactory(effettoAdapter)
                .create();

        Type tipoListaHack = new TypeToken<ArrayList<Hack>>(){}.getType();
        ArrayList<Hack> catalogoHack = gson.fromJson(fileReaderHacks, tipoListaHack);
        return catalogoHack != null ? catalogoHack : new ArrayList<>();
    }
}
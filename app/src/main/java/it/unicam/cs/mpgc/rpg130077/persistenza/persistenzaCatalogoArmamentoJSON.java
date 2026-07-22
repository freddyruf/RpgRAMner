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
    // Percorsi dei file di catalogo nei resources
    private static final String CATALOGO_ARMI_PATH = "/catalogo_armi.json";
    private static final String CATALOGO_HACKS_PATH = "/catalogo_hacks.json";


    public persistenzaCatalogoArmamentoJSON() {
        // Nulla da fare: i file verranno caricati dai metodi
    }

    //TODO: cambiare metodo per i principi SOLID

    /**
     *
     * @return ArrayList di armi scelte
     */
    @Override
    public ArrayList<Arma> CaricamentoCatalogoArmi() {
        try (InputStream isArmi = getClass().getResourceAsStream(CATALOGO_ARMI_PATH)) {
            if (isArmi == null) {
                throw new RuntimeException("catalogo_armi.json non trovato nei resources!");
            }

            try (InputStreamReader reader = new InputStreamReader(isArmi, StandardCharsets.UTF_8)) {
                RuntimeTypeAdapterFactory<Arma> armaAdapter = RuntimeTypeAdapterFactory.of(Arma.class, "tipo")
                        .registerSubtype(Pistola.class, "Pistola")
                        .registerSubtype(Mitragliatrice.class, "Mitragliatrice");

                Gson gson = new GsonBuilder()
                        .registerTypeAdapterFactory(armaAdapter)
                        .create();

                Type tipoListaArmi = new TypeToken<ArrayList<Arma>>(){}.getType();
                ArrayList<Arma> catalogoArmi = gson.fromJson(reader, tipoListaArmi);
                return catalogoArmi != null ? catalogoArmi : new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento del catalogo armi: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    //TODO: cambiare metodo per i principi SOLID

    /**
     *
     * @return ArrayList di hack scelte
     */
    @Override
    public ArrayList<Hack> CaricamentoCatalogoHack() {
        try (InputStream isHacks = getClass().getResourceAsStream(CATALOGO_HACKS_PATH)) {
            if (isHacks == null) {
                throw new RuntimeException("catalogo_hacks.json non trovato nei resources!");
            }

            try (InputStreamReader reader = new InputStreamReader(isHacks, StandardCharsets.UTF_8)) {
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
                ArrayList<Hack> catalogoHack = gson.fromJson(reader, tipoListaHack);
                return catalogoHack != null ? catalogoHack : new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento del catalogo hacks: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    }
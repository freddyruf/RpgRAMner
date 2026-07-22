package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.*;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.*;
import it.unicam.cs.mpgc.rpg130077.util.RuntimeTypeAdapterFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class persistenzaArmamentoJSON implements persistenzaArmamento {


    private static class ArmamentoSalvato {
        ArrayList<Arma> armi;
        ArrayList<Hack> hacks;

        ArmamentoSalvato(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
            this.armi = armi;
            this.hacks = hacks;
        }
    }

    // L'UNICO FILE CHE VERRA UTILIZZATO PER IL SALVATAGGIO
    private final String FILE = "data/Armamento.json";

    private persistenzaCatalogoArmamentoJSON catalogo = new persistenzaCatalogoArmamentoJSON();
    private Gson gson;

    //TODO: Da rifare per i principi SOLID
    public persistenzaArmamentoJSON() {
        RuntimeTypeAdapterFactory<Arma> armaAdapter = RuntimeTypeAdapterFactory.of(Arma.class, "tipo")
                .registerSubtype(Pistola.class, "Pistola")
                .registerSubtype(Mitragliatrice.class, "Mitragliatrice");

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

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapterFactory(armaAdapter)
                .registerTypeAdapterFactory(hackAdapter)
                .registerTypeAdapterFactory(effettoAdapter)
                .create();
    }

    /**
     * salva nel file l'equipaggiamento
     * @param armi lista di armi
     * @param hacks lista di hack
     */
    @Override
    public void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
        File file = new File(FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        ArmamentoSalvato dato = new ArmamentoSalvato(armi, hacks);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(dato, writer);
            System.out.println("Salvataggio dell'armamento completato nel file: " + FILE);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    /**
     * Legge il file JSON e restituisce le armi salvate.
     * Se il file non esiste o è vuoto, restituisce una lista vuota.
     *
     * @return ArrayList di Arma salvate nel file JSON
     */
    @Override
    public ArrayList<Arma> getArma() {
        try (FileReader reader = new FileReader(FILE)) {
            // Leggiamo tutto il file come oggetto JSON
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            Type tipoLista = new TypeToken<ArrayList<Arma>>(){}.getType();

            // Estraiamo solo l'array associato alla chiave "armi"
            ArrayList<Arma> armi = gson.fromJson(root.get("armi"), tipoLista);
            return armi != null ? armi : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
            return null;
        }
    }


    /**
     * Legge il file JSON e restituisce gli hack salvati.
     * @return Arraylist di hack salvati, o null se vuota
     */
    @Override
    public ArrayList<Hack> getHacks() {
        try (FileReader reader = new FileReader(FILE)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root == null || !root.has("hacks") || root.get("hacks").isJsonNull()) {
                return new ArrayList<>();
            }

            Type tipoLista = new TypeToken<ArrayList<Hack>>() {}.getType();
            ArrayList<Hack> hacks = gson.fromJson(root.get("hacks"), tipoLista);
            return hacks != null ? hacks : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento degli hacks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public ArrayList<Arma> caricamentoCatalogoArmi() {
        return catalogo.CaricamentoCatalogoArmi();
    }

    public ArrayList<Hack> caricamentoCatalogoHacks() {
        return catalogo.CaricamentoCatalogoHack();
    }
}
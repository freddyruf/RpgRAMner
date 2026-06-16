package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.*;
import it.unicam.cs.mpgc.rpg130077.util.RuntimeTypeAdapterFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class persistenzaArmamentoJSON implements persistenzaArmamento {

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
                .registerSubtype(RAMReverse.class, "RAMReverse")
                .registerSubtype(RAMSort.class, "RAMSort");

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapterFactory(armaAdapter)
                .registerTypeAdapterFactory(hackAdapter)
                .create();
    }

    /**
     * salva nel file l'equipaggiamento
     * @param armi lista di armi
     * @param hacks lista di hack
     */
    @Override
    public void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
        // Uniamo le due liste in una Mappa
        Map<String, Object> datiDaSalvare = new HashMap<>();
        datiDaSalvare.put("armi", armi);
        datiDaSalvare.put("hacks", hacks);

        try (FileWriter writer = new FileWriter(FILE)) {
            gson.toJson(datiDaSalvare, writer);
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
    public ArrayList<Arma> prelevaArma() {
        try (FileReader reader = new FileReader(FILE)) {
            // Leggiamo tutto il file come oggetto JSON
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            Type tipoLista = new TypeToken<ArrayList<Arma>>(){}.getType();

            // Estraiamo solo l'array associato alla chiave "armi"
            ArrayList<Arma> armi = gson.fromJson(root.get("armi"), tipoLista);
            return armi != null ? armi : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    /**
     * Legge il file JSON e restituisce gli hack salvati.
     * @return Arraylist di hack salvati, o null se vuota
     */
    @Override
    public ArrayList<Hack> prelevaHacks() {
        try (FileReader reader = new FileReader(FILE)) {

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            Type tipoLista = new TypeToken<ArrayList<Hack>>(){}.getType();

            ArrayList<Hack> hacks = gson.fromJson(root.get("hacks"), tipoLista);
            return hacks != null ? hacks : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ArrayList<Arma> CaricamentoCatalogoArmi() {
        return catalogo.CaricamentoCatalogoArmi();
    }

    public ArrayList<Hack> CaricamentoCatalogoHacks() {
        return catalogo.CaricamentoCatalogoHack();
    }
}
package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

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

    private final String FILE = "data/Armamento.json";
    private final persistenzaCatalogoArmamentoJSON catalogo;
    private final Gson gson;

    public persistenzaArmamentoJSON() {
        this.catalogo = new persistenzaCatalogoArmamentoJSON();
        this.gson = GsonProvider.getGson();
    }

    @Override
    public void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
        File file = new File(FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        ArmamentoSalvato dato = new ArmamentoSalvato(armi, hacks);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(dato, writer);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Arma> getArma() {
        try (FileReader reader = new FileReader(FILE)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            Type tipoLista = new TypeToken<ArrayList<Arma>>(){}.getType();

            ArrayList<Arma> armi = gson.fromJson(root.get("armi"), tipoLista);
            return armi != null ? armi : new ArrayList<>();
        } catch (Exception e) {
            // È normale che fallisca alla prima esecuzione se il file non c'è, restituiamo vuoto.
            return new ArrayList<>();
        }
    }

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
            return new ArrayList<>();
        }
    }

    @Override
    public ArrayList<Arma> caricamentoCatalogoArmi() {
        return catalogo.CaricamentoCatalogoArmi();
    }

    @Override
    public ArrayList<Hack> caricamentoCatalogoHacks() {
        return catalogo.CaricamentoCatalogoHack();
    }
}
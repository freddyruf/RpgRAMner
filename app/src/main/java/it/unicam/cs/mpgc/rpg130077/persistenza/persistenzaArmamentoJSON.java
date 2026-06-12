package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class persistenzaArmamentoJSON implements persistenzaArmamento {

    // Salviamo i percorsi come costanti. Usa percorsi relativi (senza / iniziale)
    private final String FILE = "data/Armamento.json";

    private persistenzaCatalogoArmamentoJSON catalogo = new persistenzaCatalogoArmamentoJSON();
    private Gson gson;

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

    @Override
    public ArrayList<Arma> prelevaArma() {
        // Legge e deserializza dal file
        try (FileReader reader = new FileReader(FILE)) {
            Type tipoLista = new TypeToken<ArrayList<Arma>>(){}.getType();
            ArrayList<Arma> armi = gson.fromJson(reader, tipoLista);
            return armi != null ? armi : new ArrayList<>();
        } catch (IOException e) {
            // Se il file non esiste (es. prima partita), restituisce una lista vuota invece di crashare
            System.out.println("Nessun salvataggio armi trovato. Inizializzo inventario vuoto.");
            return new ArrayList<>();
        }
    }

    @Override
    public ArrayList<Hack> prelevaHacks() {
        try (FileReader reader = new FileReader(FILE)) {
            Type tipoLista = new TypeToken<ArrayList<Hack>>(){}.getType();
            ArrayList<Hack> hacks = gson.fromJson(reader, tipoLista);
            return hacks != null ? hacks : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Nessun salvataggio hack trovato. Inizializzo inventario vuoto.");
            return new ArrayList<>();
        }
    }

    @Override
    public void salvaArmi(ArrayList<Arma> armi) {
        // Il try-with-resources crea/sovrascrive il file e lo chiude alla fine
        try (FileWriter writer = new FileWriter(FILE)) {
            gson.toJson(armi, writer);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio delle armi: " + e.getMessage());
        }
    }

    @Override
    public void salvaHack(ArrayList<Hack> hack) {
        try (FileWriter writer = new FileWriter(FILE)) {
            gson.toJson(hack, writer);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio degli hack: " + e.getMessage());
        }
    }

    public ArrayList<Arma> CaricamentoCatalogoArmi() {
        return catalogo.CaricamentoCatalogoArmi();
    }
    public ArrayList<Hack> CaricamentoCatalogoHacks() {
        return  catalogo.CaricamentoCatalogoHack();
    }

}
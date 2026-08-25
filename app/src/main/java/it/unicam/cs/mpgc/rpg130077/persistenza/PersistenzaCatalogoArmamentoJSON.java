package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Classe che implementa CaricatoreCatalogo che usa JSON per gestire la persistenza del catalogo
 */
public class PersistenzaCatalogoArmamentoJSON implements CaricatoreCatalogo {

    private static final String CATALOGO_ARMI_PATH = "/catalogo_armi.json";
    private static final String CATALOGO_HACKS_PATH = "/catalogo_hacks.json";
    private final Gson gson;

    public PersistenzaCatalogoArmamentoJSON() {
        this.gson = GsonProvider.getGson();
    }

    @Override
    public ArrayList<Arma> caricamentoCatalogoArmi() {
        return caricaDaResources(CATALOGO_ARMI_PATH, new TypeToken<ArrayList<Arma>>(){}.getType());
    }

    @Override
    public ArrayList<Hack> caricamentoCatalogoHack() {
        return caricaDaResources(CATALOGO_HACKS_PATH, new TypeToken<ArrayList<Hack>>(){}.getType());
    }

    private <T> ArrayList<T> caricaDaResources(String path, Type tipoLista) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Risorsa non trovata nei resources: " + path);
            }

            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                ArrayList<T> catalogo = gson.fromJson(reader, tipoLista);
                return catalogo != null ? catalogo : new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento dal catalogo (" + path + "): " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
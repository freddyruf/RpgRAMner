package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;

public class GsonProvider {

    /**
     * Fornisce l'ogetto di classe Gson con cui si eseguiranno le operazioni di serializzazione e deserializzazione dei dati. COn degli aattatori
     * @return un ogetto Gson con gli adattatori.
     */
    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();

        // Adapter per Arma (Mappatura diretta 1:1)
        builder.registerTypeAdapter(Arma.class, new PolymorphicAdapter<Arma>(
                "it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento",
                "tipo",
                tipoJson -> tipoJson,
                nomeClasse -> nomeClasse
        ));

        // Adapter per Hack (Gestisce la punteggiatura: "RAM:Sort" <-> "RAMSort")
        builder.registerTypeAdapter(Hack.class, new PolymorphicAdapter<Hack>(
                "it.unicam.cs.mpgc.rpg130077.model.Hacks",
                "tipo",
                tipoJson -> tipoJson.replace(":", ""),
                nomeClasse -> nomeClasse.startsWith("RAM") ? nomeClasse.replace("RAM", "RAM:") : nomeClasse
        ));

        // Adapter per Effetto (Gestisce il prefisso: "Danno" <-> "EffettoDanno")
        builder.registerTypeAdapter(Effetto.class, new PolymorphicAdapter<Effetto>(
                "it.unicam.cs.mpgc.rpg130077.model.Effetti",
                "tipoEffetto",
                tipoJson -> "Effetto" + tipoJson,
                nomeClasse -> nomeClasse.replace("Effetto", "")
        ));

        return builder.create();
    }
}
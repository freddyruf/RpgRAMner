package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Adapter generico per serializzare e deserializzare classi polimorfiche dinamicamente.
 */
public class PolymorphicAdapter<T> implements JsonDeserializer<T>, JsonSerializer<T> {

    private final String basePackage;
    private final String typeFieldName;
    private final Function<String, String> toClassName;
    private final Function<String, String> toTypeName;

    public PolymorphicAdapter(String basePackage, String typeFieldName,
                              Function<String, String> toClassName,
                              Function<String, String> toTypeName) {
        this.basePackage = basePackage;
        this.typeFieldName = typeFieldName;
        this.toClassName = toClassName;
        this.toTypeName = toTypeName;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has(typeFieldName)) {
            throw new JsonParseException("Campo mancante: '" + typeFieldName + "'");
        }

        // Legge il tipo dal JSON (es. "RAM:Sort") e lo converte nel nome della classe (es. "RAMSort")
        String typeValue = jsonObject.get(typeFieldName).getAsString();
        String className = basePackage + "." + toClassName.apply(typeValue);

        try {
            // Istanzia dinamicamente la classe
            Class<?> clazz = Class.forName(className);
            return context.deserialize(json, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Classe non trovata per il tipo polimorfico: " + className, e);
        }
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        // Serializza la classe concreta (usiamo src.getClass() per evitare loop infiniti di serializzazione)
        JsonElement elem = context.serialize(src, src.getClass());
        JsonObject jsonObject = elem.getAsJsonObject();

        // Ricava il nome dal tipo della classe (es. da "EffettoDanno" a "Danno") e lo inietta nel JSON
        String typeValue = toTypeName.apply(src.getClass().getSimpleName());
        jsonObject.addProperty(typeFieldName, typeValue);

        return jsonObject;
    }
}
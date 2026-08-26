package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoSort;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link PolymorphicAdapter}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class PolymorphicAdapterTest {

    private Gson gsonArma;
    private Gson gsonEffetto;

    @BeforeEach
    void setUp() {
        gsonArma = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Arma.class, new PolymorphicAdapter<Arma>(
                        "it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento",
                        "tipo",
                        t -> t,
                        c -> c
                )).create();

        gsonEffetto = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Effetto.class, new PolymorphicAdapter<Effetto>(
                        "it.unicam.cs.mpgc.rpg130077.model.Effetti",
                        "tipoEffetto",
                        t -> "Effetto" + t,
                        c -> c.replace("Effetto", "")
                )).create();
    }

    @Test
    void testSerializzazioneEDeserializzazionePistola() {
        Pistola pistola = new Pistola("Glock", "Desc", 6, 15, 0.2);
        String json = gsonArma.toJson(pistola, Arma.class);

        assertTrue(json.contains("\"tipo\": \"Pistola\""));

        Arma deserializzata = gsonArma.fromJson(json, Arma.class);
        assertTrue(deserializzata instanceof Pistola);
        assertEquals("Glock", deserializzata.getNome());
        assertEquals(15, deserializzata.getDanno());
    }

    @Test
    void testSerializzazioneEDeserializzazioneMitragliatrice() {
        Mitragliatrice mitragliatrice = new Mitragliatrice("Minigun", "Desc", 30, 8, 0.1);
        String json = gsonArma.toJson(mitragliatrice, Arma.class);

        assertTrue(json.contains("\"tipo\": \"Mitragliatrice\""));

        Arma deserializzata = gsonArma.fromJson(json, Arma.class);
        assertTrue(deserializzata instanceof Mitragliatrice);
        assertEquals("Minigun", deserializzata.getNome());
    }

    @Test
    void testSerializzazioneEDeserializzazioneEffettoDanno() {
        EffettoDanno effetto = new EffettoDanno(50, true);
        String json = gsonEffetto.toJson(effetto, Effetto.class);

        assertTrue(json.contains("\"tipoEffetto\": \"Danno\""));

        Effetto deserializzato = gsonEffetto.fromJson(json, Effetto.class);
        assertTrue(deserializzato instanceof EffettoDanno);
        assertTrue(deserializzato.isConclusive());
    }

    @Test
    void testSerializzazioneEDeserializzazioneEffettoCura() {
        EffettoCura effetto = new EffettoCura(30, true);
        String json = gsonEffetto.toJson(effetto, Effetto.class);

        assertTrue(json.contains("\"tipoEffetto\": \"Cura\""));

        Effetto deserializzato = gsonEffetto.fromJson(json, Effetto.class);
        assertTrue(deserializzato instanceof EffettoCura);
    }

    @Test
    void testSerializzazioneEDeserializzazioneEffettoReverse() {
        EffettoReverse effetto = new EffettoReverse(true);
        String json = gsonEffetto.toJson(effetto, Effetto.class);

        assertTrue(json.contains("\"tipoEffetto\": \"Reverse\""));

        Effetto deserializzato = gsonEffetto.fromJson(json, Effetto.class);
        assertTrue(deserializzato instanceof EffettoReverse);
    }

    @Test
    void testDeserializzazioneEffettoSort() {
        String json = "{\"tipoEffetto\":\"Sort\",\"conclusive\":true}";
        Effetto deserializzato = gsonEffetto.fromJson(json, Effetto.class);

        assertTrue(deserializzato instanceof EffettoSort);
        assertTrue(deserializzato.isConclusive());
    }

    @Test
    void testDeserializzazioneLanciaEccezioneSeCampoTipoMancante() {
        String jsonSenzaTipo = "{\"nome\":\"Pistola\",\"danno\":10}";
        assertThrows(JsonParseException.class, () -> gsonArma.fromJson(jsonSenzaTipo, Arma.class));
    }

    @Test
    void testDeserializzazioneLanciaEccezioneSeClasseNonEsiste() {
        String jsonClasseInesistente = "{\"tipo\":\"ArmaInesistenteXX\",\"nome\":\"Pistola\"}";
        assertThrows(JsonParseException.class, () -> gsonArma.fromJson(jsonClasseInesistente, Arma.class));
    }
}

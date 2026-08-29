package it.unicam.cs.mpgc.rpg130077.persistenza;

import com.google.gson.Gson;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link GsonProvider}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class GsonProviderTest {

    @Test
    void testGetGsonRitornaOggettoConfigurato() {
        Gson gson = GsonProvider.getGson();
        assertNotNull(gson);
    }

    @Test
    void testRoundtripArma() {
        Gson gson = GsonProvider.getGson();

        Pistola pistola = new Pistola("PistolaStandard", "Descrizione", 6, 20, 0.2);
        String jsonPistola = gson.toJson(pistola, Arma.class);
        Arma armaPistola = gson.fromJson(jsonPistola, Arma.class);

        assertTrue(armaPistola instanceof Pistola);
        assertEquals("PistolaStandard", armaPistola.getNome());
        assertEquals(20, armaPistola.getDanno());

        Mitragliatrice mitragliatrice = new Mitragliatrice("Mitra", "Desc", 40, 10, 0.1);
        String jsonMitra = gson.toJson(mitragliatrice, Arma.class);
        Arma armaMitra = gson.fromJson(jsonMitra, Arma.class);

        assertTrue(armaMitra instanceof Mitragliatrice);
        assertEquals("Mitra", armaMitra.getNome());
    }

    @Test
    void testRoundtripHackConEffetti() {
        Gson gson = GsonProvider.getGson();

        Hack fireball = new Hack("Fireball", "Palla di fuoco", 4);
        fireball.addEffetto(new EffettoDanno(50, true));

        String jsonFireball = gson.toJson(fireball, Hack.class);
        Hack hackFireball = gson.fromJson(jsonFireball, Hack.class);

        assertEquals("Fireball", hackFireball.getNome());
        assertEquals(1, hackFireball.getEffetti().size());
        assertTrue(hackFireball.getEffetti().get(0) instanceof EffettoDanno);
        assertTrue(hackFireball.getEffectTypes().contains(it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType.DAMAGE));

        Hack firewall = new Hack("Firewall", "Muro di difesa", 4);
        firewall.addEffetto(new EffettoCura(30, true));

        String jsonFirewall = gson.toJson(firewall, Hack.class);
        Hack hackFirewall2 = gson.fromJson(jsonFirewall, Hack.class);

        assertTrue(hackFirewall2.getEffectTypes().contains(it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType.HEAL));
    }

    @Test
    void testRoundtripHackRAMReverseConEffetto() {
        Gson gson = GsonProvider.getGson();

        Hack ramReverse = new Hack("RAM:Reverse", "Inverte la coda", 6);
        ramReverse.addEffetto(new EffettoReverse(true));

        String json = gson.toJson(ramReverse, Hack.class);
        Hack hack = gson.fromJson(json, Hack.class);

        assertEquals("RAM:Reverse", hack.getNome());
        assertEquals(1, hack.getEffetti().size());
        assertTrue(hack.getEffetti().get(0) instanceof EffettoReverse);
    }
}

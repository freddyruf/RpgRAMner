package it.unicam.cs.mpgc.rpg130077.model.Entita;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link Giocatore}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class GiocatoreTest {

    private Arma creaArma() {
        return new Pistola("PistolaPlayer", "Arma standard", 6, 20, 0.1);
    }

    private ArrayList<Hack> creaListaHacks() {
        ArrayList<Hack> hacks = new ArrayList<>();
        hacks.add(new Hack("Fireball", "Palla di fuoco", 4));
        return hacks;
    }

    private Giocatore creaGiocatore() {
        return new Giocatore("Player1", 100, "player.png", 10, creaListaHacks(), creaArma());
    }

    @Test
    void costruttoreGiocatoreInizializzaCorrettamente() {
        Giocatore g = creaGiocatore();

        assertEquals("Player1", g.getNome());
        assertEquals(100, g.getMaxPV());
        assertEquals(100, g.getPV());
        assertEquals("player.png", g.getImage());
        assertEquals(10, g.getSpazioRAM());
        assertEquals(1, g.getHacks().size());
        assertEquals("PistolaPlayer", g.getArma().getNome());
    }

    @Test
    void costruttoreDiCopiaCreaCopiaProfondaIndipendente() {
        Giocatore originale = creaGiocatore();
        Giocatore copia = new Giocatore(originale);

        assertNotSame(originale, copia);
        assertEquals(originale.getNome(), copia.getNome());
        assertEquals(originale.getPV(), copia.getPV());
        assertNotSame(originale.getArma(), copia.getArma());
        assertNotSame(originale.getHacks(), copia.getHacks());

        copia.setPV(50);
        assertEquals(100, originale.getPV());
        assertEquals(50, copia.getPV());
    }

    @Test
    void copyMethodRitornaNuovaIstanzaIndipendente() {
        Giocatore originale = creaGiocatore();
        Entita copia = originale.copy();

        assertTrue(copia instanceof Giocatore);
        assertNotSame(originale, copia);
        assertEquals(originale.getNome(), copia.getNome());
        assertNotSame(originale.getArma(), copia.getArma());
    }
}

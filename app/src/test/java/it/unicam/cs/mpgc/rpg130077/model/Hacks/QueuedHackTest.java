package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link QueuedHack}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class QueuedHackTest {

    private Entita lanciatore;
    private Entita bersaglio;

    @BeforeEach
    void setUp() {
        lanciatore = new Giocatore("Hero", 100, "hero.png", 8, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), true);
        bersaglio = new Giocatore("Enemy", 100, "enemy.png", 8, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), false);
    }

    @Test
    void costruttoreInizializzaThickInCodaPariADurataDellaHack() {
        Hack hack = new Hack("Fireball", "Palla di fuoco", 5);
        QueuedHack queued = new QueuedHack(hack, bersaglio, lanciatore);

        assertEquals(5, queued.getTickInCoda());
        assertSame(hack, queued.getHack());
        assertSame(bersaglio, queued.getBersaglio());
        assertSame(lanciatore, queued.getLanciatore());
    }

    @Test
    void setEGetThickInCoda() {
        Hack hack = new Hack("Fireball", "Palla di fuoco", 5);
        QueuedHack queued = new QueuedHack(hack, bersaglio, lanciatore);

        queued.setTickInCoda(3);
        assertEquals(3, queued.getTickInCoda());
    }

}

package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link StatoBattaglia1v1}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class StatoBattaglia1v1Test {

    private Giocatore giocatore;
    private NPC avversario;

    @BeforeEach
    void setUp() {
        giocatore = new Giocatore("Hero", 100, "hero.png", 6, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), true);
        avversario = new NPC("Enemy", 80, "enemy.png", 4, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), 15, 0.2, (n, s) -> null, false);
    }

    @Test
    void testCostruttoreInizializzaGiocatoreENemicoERAMTotale() {
        StatoBattaglia1v1 stato = new StatoBattaglia1v1(giocatore, avversario);

        assertSame(giocatore, stato.getGiocatore());
        assertSame(giocatore, stato.getEroe(0));
        assertSame(avversario, stato.getNemico(0));
        // 6 + 4 = 10 (RAM totale combinata)
        assertEquals(10, stato.getRamCondivisa().getSpazioMassimoInSecondi());
    }

    @Test
    void testCostruttoreLanciaNullPointerExceptionSeGiocatoreNullo() {
        assertThrows(NullPointerException.class, () -> new StatoBattaglia1v1(null, avversario));
    }

    @Test
    void testCostruttoreLanciaNullPointerExceptionSeNemicoNullo() {
        assertThrows(NullPointerException.class, () -> new StatoBattaglia1v1(giocatore, null));
    }

    @Test
    void testFazioneEroiRitornaListaConSoloGiocatore() {
        StatoBattaglia1v1 stato = new StatoBattaglia1v1(giocatore, avversario);
        ArrayList<Entita> eroi = stato.getFazioneEroi();

        assertEquals(1, eroi.size());
        assertSame(giocatore, eroi.get(0));
    }

    @Test
    void testFazioneNemiciRitornaListaConSoloAvversario() {
        StatoBattaglia1v1 stato = new StatoBattaglia1v1(giocatore, avversario);
        ArrayList<Entita> nemici = stato.getFazioneNemici();

        assertEquals(1, nemici.size());
        assertSame(avversario, nemici.get(0));
    }

    @Test
    void testCopyEsegueCopiaProfonda() {
        StatoBattaglia1v1 originale = new StatoBattaglia1v1(giocatore, avversario);
        StatoBattaglia copia = originale.copy();

        assertNotSame(originale, copia);
        assertNotSame(originale.getGiocatore(), copia.getGiocatore());
        assertNotSame(originale.getNemico(0), copia.getNemico(0));
        assertNotSame(originale.getRamCondivisa(), copia.getRamCondivisa());

        copia.getGiocatore().setPv(20);
        assertEquals(100, originale.getGiocatore().getPv());
        assertEquals(20, copia.getGiocatore().getPv());
    }
}

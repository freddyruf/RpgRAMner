package it.unicam.cs.mpgc.rpg130077.model.Azioni;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link AzioneCaricaHack}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class AzioneCaricaHackTest {

    private Entita lanciatore;
    private Entita bersaglio;
    private StatoBattaglia stato;

    @BeforeEach
    void setUp() {
        lanciatore = new Giocatore("Hero", 100, "hero.png", 5, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0));
        bersaglio = new NPC("Enemy", 100, "enemy.png", 5, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), 10, 0.0, (n, s) -> null);
        stato = new StatoBattaglia1v1((Giocatore) lanciatore, (NPC) bersaglio);
    }

    @Test
    void costruttoreEGetterHack() {
        Hack hack = new Hack("Fireball", "Danno", 4);
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        assertSame(hack, azione.getHack());
    }

    @Test
    void isDamageDealerRitornaTrueSeHackHaEffettoDanno() {
        Hack hack = new Hack("Fireball", "Danno", 4);
        hack.addEffetto(new EffettoDanno(50, true));
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        assertTrue(azione.isDamageDealer());
        assertFalse(azione.isHealDealer());
    }

    @Test
    void isHealDealerRitornaTrueSeHackHaEffettoCura() {
        Hack hack = new Hack("Firewall", "Cura", 4);
        hack.addEffetto(new EffettoCura(30, true));
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        assertTrue(azione.isHealDealer());
        assertFalse(azione.isDamageDealer());
    }

    @Test
    void isDamageDealerEHealDealerRitornanoFalsePerHackDiUtilita() {
        Hack hack = new Hack("RAM:Reverse", "Inversione", 6);
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        assertFalse(azione.isDamageDealer());
        assertFalse(azione.isHealDealer());
    }

    @Test
    void eseguiInserisceHackNellaRAMCondivisa() {
        Hack hack = new Hack("Fireball", "Danno", 4);
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hack);

        azione.esegui(stato);

        assertEquals(1, stato.getRamCondivisa().getHacks().size());
        assertSame(hack, stato.getRamCondivisa().visualizzaTesta().getHack());
    }

    @Test
    void eseguiConRAMPienaLanciaEccezione() {
        Hack hackGrande = new Hack("MegaFireball", "Danno enorme", 15);
        AzioneCaricaHack azione = new AzioneCaricaHack(lanciatore, bersaglio, hackGrande);

        assertThrows(IllegalArgumentException.class, () -> azione.esegui(stato));
    }
}

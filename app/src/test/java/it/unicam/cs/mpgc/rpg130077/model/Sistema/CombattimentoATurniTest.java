package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;

import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link CombattimentoATurni}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class CombattimentoATurniTest {

    private Giocatore giocatore;
    private NPC nemico;
    private StatoBattaglia1v1 stato;
    private CombattimentoATurni combattimento;

    private static class SpyCombattimentoListener implements CombattimentoListener {
        int conteggioTick = 0;
        int conteggioVitaAggiornata = 0;
        int conteggioAggiornaRAM = 0;
        Entita vincitoreNotificato;

        @Override public void onTick(StatoBattaglia statoBattaglia) { conteggioTick++; }
        @Override public void onVitaAggiornataEntita(Entita entita) {}
        @Override public void onVittoria(Entita vincitore) { this.vincitoreNotificato = vincitore; }
        @Override public void onVitaAggiornata(StatoBattaglia statoBattaglia) { conteggioVitaAggiornata++; }
        @Override public void onTurnoGiocatore() {}
        @Override public void aggiornaRAM(RAM ram) { conteggioAggiornaRAM++; }
    }

    @BeforeEach
    void setUp() {
        giocatore = new Giocatore("Hero", 100, "hero.png", 5, new ArrayList<>(),
                new Pistola("PistolaEroe", "Desc", 6, 20, 0.0));
        nemico = new NPC("Enemy", 100, "enemy.png", 5, new ArrayList<>(),
                new Pistola("PistolaNemico", "Desc", 6, 10, 0.0), 10, 0.0,
                (npc, st) -> new AzioneSparo(npc, st.getEroe(0)));
        stato = new StatoBattaglia1v1(giocatore, nemico);
        combattimento = new CombattimentoATurni(stato);
    }

    @Test
    void testInizializzazioneCombattimentoParteConTurnoGiocatore() {
        assertTrue(combattimento.isPlayerTurn());
        assertEquals("Hero", combattimento.getEntitaInCorso().getNome());
        assertNotNull(combattimento.getStatoBattaglia());
    }

    @Test
    void testEseguiMossaSparoDanneggiaNemicoEInnescaTurnoNemico() {
        // Eroe spara (20 danni al nemico), poi il nemico risponde automaticamente (10 danni all'eroe)
        combattimento.sparare();

        // 100 - 20 = 80 per il nemico
        assertEquals(80, nemico.getPV());
        // 100 - 10 = 90 per l'eroe
        assertEquals(90, giocatore.getPV());
        // Il controllo torna all'eroe
        assertTrue(combattimento.isPlayerTurn());
    }

    @Test
    void testEseguiMossaCaricaHackInserisceHackNellaRAM() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        Hack hack = new Hack("Fireball", "Danno", 3);
        hack.addEffetto(new EffettoDanno(40, true));
        combattimento.caricaHack(hack);

        assertEquals(1, stato.getRamCondivisa().getHacks().size());
        assertEquals("Fireball", stato.getRamCondivisa().visualizzaTesta().getHack().getNome());
        assertTrue(spy.conteggioVitaAggiornata > 0 || spy.conteggioAggiornaRAM > 0);
    }

    @Test
    void testOnTickAvanzaRAMENotificaListeners() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        Hack hack = new Hack("Fireball", "Danno", 3);
        stato.getRamCondivisa().inserisci(hack, nemico, giocatore);

        combattimento.onTick();

        // 3 - 1 = 2
        assertEquals(2, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());
        assertEquals(1, spy.conteggioTick);
    }

    @Test
    void testCheckVittoriaRitornaEroeSeNemicoMuore() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        nemico.setPV(0);
        Entita vincitore = combattimento.checkVittoria();

        assertSame(giocatore, vincitore);
        assertSame(giocatore, spy.vincitoreNotificato);
    }

    @Test
    void testCheckVittoriaRitornaNemicoSeEroeMuore() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        giocatore.setPV(0);
        Entita vincitore = combattimento.checkVittoria();

        assertSame(nemico, vincitore);
        assertSame(nemico, spy.vincitoreNotificato);
    }

    @Test
    void testCheckVittoriaRitornaNullSeEntrambiVivi() {
        assertNull(combattimento.checkVittoria());
    }

    @Test
    void testRipristinaRiportaStatoOriginario() {
        combattimento.sparare();
        assertEquals(80, nemico.getPV());
        assertEquals(90, giocatore.getPV());

        combattimento.ripristina();

        assertEquals(100, combattimento.getStatoBattaglia().getNemico(0).getPV());
        assertEquals(100, combattimento.getStatoBattaglia().getEroe(0).getPV());
        assertTrue(combattimento.isPlayerTurn());
    }

    @Test
    void testCostruttoreDiCopiaCopiaStatoETurni() {
        CombattimentoATurni copia = new CombattimentoATurni(combattimento);

        assertNotNull(copia.getStatoBattaglia());
        assertEquals(combattimento.isPlayerTurn(), copia.isPlayerTurn());
    }
}

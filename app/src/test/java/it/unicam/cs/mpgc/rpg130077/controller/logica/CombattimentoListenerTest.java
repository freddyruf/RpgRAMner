package it.unicam.cs.mpgc.rpg130077.controller.logica;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoListener;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link CombattimentoListener}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class CombattimentoListenerTest {

    private static class TestCombattimentoListener implements CombattimentoListener {
        boolean onTickChiamato = false;
        boolean onVitaAggiornataChiamato = false;
        boolean onVitaAggiornataEntitaChiamato = false;
        boolean onVittoriaChiamato = false;
        boolean onTurnoGiocatoreChiamato = false;
        boolean aggiornaRAMChiamato = false;

        StatoBattaglia ultimoStato;
        Entita ultimoVincitore;
        Entita ultimaEntita;
        RAM ultimaRAM;

        @Override
        public void onTick(StatoBattaglia statoBattaglia) {
            this.onTickChiamato = true;
            this.ultimoStato = statoBattaglia;
        }

        @Override
        public void onVitaAggiornataEntita(Entita entita) {
            this.onVitaAggiornataEntitaChiamato = true;
            this.ultimaEntita = entita;
        }

        @Override
        public void onVittoria(Entita vincitore) {
            this.onVittoriaChiamato = true;
            this.ultimoVincitore = vincitore;
        }

        @Override
        public void onVitaAggiornata(StatoBattaglia statoBattaglia) {
            this.onVitaAggiornataChiamato = true;
            this.ultimoStato = statoBattaglia;
        }

        @Override
        public void onTurnoGiocatore() {
            this.onTurnoGiocatoreChiamato = true;
        }

        @Override
        public void aggiornaRAM(RAM ram) {
            this.aggiornaRAMChiamato = true;
            this.ultimaRAM = ram;
        }
    }

    private Giocatore giocatore;
    private NPC nemico;
    private StatoBattaglia1v1 stato;
    private CombattimentoATurni combattimento;
    private TestCombattimentoListener listener;

    @BeforeEach
    void setUp() {
        giocatore = new Giocatore("Hero", 100, "hero.png", 5, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 20, 0.0), true);
        nemico = new NPC("Enemy", 100, "enemy.png", 5, new ArrayList<>(),
                new Pistola("Pistola", "Desc", 6, 10, 0.0), 10, 0.0,
                (npc, st) -> new AzioneSparo(npc, st.getEroe(0)), false);
        stato = new StatoBattaglia1v1(giocatore, nemico);
        combattimento = new CombattimentoATurni(stato);
        listener = new TestCombattimentoListener();
        combattimento.aggiungiListener(listener);
    }

    @Test
    void testNotificaOnTickRiceveStatoBattaglia() {
        combattimento.onTick();

        assertTrue(listener.onTickChiamato);
        assertSame(stato, listener.ultimoStato);
    }

    @Test
    void testNotificaOnVitaAggiornataSuAzioneDanno() {
        combattimento.sparare();

        assertTrue(listener.onVitaAggiornataChiamato);
        assertSame(stato, listener.ultimoStato);
    }

    @Test
    void testNotificaAggiornaRAMSuCaricaHackDiUtilita() {
        Hack hackUtility = new Hack("RAM:Reverse", "Inversione", 4);
        hackUtility.addEffetto(new EffettoReverse(true));

        AzioneCaricaHack azione = new AzioneCaricaHack(giocatore, nemico, hackUtility);
        combattimento.eseguiMossa(azione);

        assertTrue(listener.aggiornaRAMChiamato);
        assertSame(stato.getRamCondivisa(), listener.ultimaRAM);
    }

    @Test
    void testNotificaOnVittoriaQuandoNemicoSconfitto() {
        nemico.setPV(0);
        combattimento.checkVittoria();

        assertTrue(listener.onVittoriaChiamato);
        assertSame(giocatore, listener.ultimoVincitore);
    }

    @Test
    void testNotificaOnVittoriaQuandoEroeSconfitto() {
        giocatore.setPV(0);
        combattimento.checkVittoria();

        assertTrue(listener.onVittoriaChiamato);
        assertSame(nemico, listener.ultimoVincitore);
    }

    @Test
    void testMetodiDirettiListener() {
        listener.onVitaAggiornataEntita(giocatore);
        assertTrue(listener.onVitaAggiornataEntitaChiamato);
        assertSame(giocatore, listener.ultimaEntita);

        listener.onTurnoGiocatore();
        assertTrue(listener.onTurnoGiocatoreChiamato);
    }
}

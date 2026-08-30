package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneCaricaHack;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.RAM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test completi per la classe {@link CombattimentoATurni}.
 * Copre il lifecycle del tick, effetti continui e conclusivi, gestione RAM,
 * condizioni di vittoria/pareggio, listener e validazione input.
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
        int conteggioNemicoNonPuoAttaccare = 0;
        int conteggioVittoria = 0;
        Entita vincitoreNotificato;

        @Override public void onTick(StatoBattaglia statoBattaglia) { conteggioTick++; }
        @Override public void onVitaAggiornataEntita(Entita entita) {}
        @Override public void onVittoria(Entita vincitore) {
            conteggioVittoria++;
            this.vincitoreNotificato = vincitore;
        }
        @Override public void onVitaAggiornata(StatoBattaglia statoBattaglia) { conteggioVitaAggiornata++; }
        @Override public void onTurnoGiocatore() {}
        @Override public void onAggiornamentoRAM(RAM ram) { conteggioAggiornaRAM++; }
        @Override public void ilNemicoNonPuoAttaccare() { conteggioNemicoNonPuoAttaccare++; }
    }

    @BeforeEach
    void setUp() {
        giocatore = new Giocatore("Hero", 100, "hero.png", 10, new ArrayList<>(),
                new Pistola("PistolaEroe", "Desc", 6, 20, 0.0), true);
        nemico = new NPC("Enemy", 100, "enemy.png", 10, new ArrayList<>(),
                new Pistola("PistolaNemico", "Desc", 6, 10, 0.0), 10, 0.0,
                (npc, st) -> new AzioneSparo(npc, st.getEroe(0)), false);
        stato = new StatoBattaglia1v1(giocatore, nemico);
        combattimento = new CombattimentoATurni(stato);
    }

    @Test
    @DisplayName("Inizializzazione corretta del combattimento con turno del giocatore")
    void testInizializzazioneCombattimentoParteConTurnoGiocatore() {
        assertTrue(combattimento.isPlayerTurn());
        assertEquals("Hero", combattimento.getEntitaInCorso().getNome());
        assertNotNull(combattimento.getStatoBattaglia());
        assertSame(giocatore, combattimento.getStatoBattaglia().getGiocatore());
    }

    @Test
    @DisplayName("Sparo danneggia bersaglio e passa al turno nemico che risponde")
    void testEseguiMossaSparoDanneggiaNemicoEInnescaTurnoNemico() {
        combattimento.spara(nemico);

        // Hero spara 20 danni al nemico (100 - 20 = 80)
        assertEquals(80, nemico.getPv());
        // Nemico risponde automaticamente con 10 danni all'eroe (100 - 10 = 90)
        assertEquals(90, giocatore.getPv());
        // Il turno torna al giocatore
        assertTrue(combattimento.isPlayerTurn());
    }

    @Test
    @DisplayName("Carica hack inserisce il programma in RAM e notifica i listener")
    void testEseguiMossaCaricaHackInserisceHackNellaRAM() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        Hack hack = new Hack("Fireball", "Danno", 3);
        hack.addEffetto(new EffettoDanno(40, true));
        combattimento.caricaHack(hack, nemico);

        assertEquals(1, stato.getRamCondivisa().getHacks().size());
        assertEquals("Fireball", stato.getRamCondivisa().visualizzaTesta().getHack().getNome());
        assertTrue(spy.conteggioAggiornaRAM > 0);
    }

    @Test
    @DisplayName("onTick esegue effetti continui ad ogni tick fino a completamento")
    void testOnTickEffettiContinuiAdOgniTick() {
        Hack hackContinuo = new Hack("Poison", "Danno continuo", 3);
        hackContinuo.addEffetto(new EffettoDanno(10, false)); // false = continuous (non conclusivo)

        stato.getRamCondivisa().inserisci(hackContinuo, nemico, giocatore);
        assertEquals(3, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());
        assertEquals(100, nemico.getPv());

        // Tick 1: decrementa a 2, applica 10 danni
        combattimento.onTick();
        assertEquals(2, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());
        assertEquals(90, nemico.getPv());

        // Tick 2: decrementa a 1, applica 10 danni
        combattimento.onTick();
        assertEquals(1, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());
        assertEquals(80, nemico.getPv());

        // Tick 3: decrementa a 0, applica 10 danni, rimuove dalla RAM
        combattimento.onTick();
        assertNull(stato.getRamCondivisa().visualizzaTesta());
        assertEquals(0, stato.getRamCondivisa().getHacks().size());
        assertEquals(70, nemico.getPv());
    }

    @Test
    @DisplayName("onTick esegue effetti conclusivi solo all'azzeramento dei tick e rimuove l'hack")
    void testOnTickEffettiConclusiviSoloAlCompletamento() {
        Hack hackConclusivo = new Hack("Nuke", "Danno conclusivo", 2);
        hackConclusivo.addEffetto(new EffettoDanno(50, true)); // true = conclusive

        stato.getRamCondivisa().inserisci(hackConclusivo, nemico, giocatore);
        assertEquals(2, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());
        assertEquals(100, nemico.getPv());

        // Tick 1: decrementa a 1, nessun danno inflitto perche' e' conclusivo
        combattimento.onTick();
        assertEquals(1, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());
        assertEquals(100, nemico.getPv());

        // Tick 2: decrementa a 0, rimuove dalla RAM ed esegue l'effetto conclusivo (50 danni)
        combattimento.onTick();
        assertNull(stato.getRamCondivisa().visualizzaTesta());
        assertEquals(0, stato.getRamCondivisa().getHacks().size());
        assertEquals(50, nemico.getPv());
    }

    @Test
    @DisplayName("onTick gestisce hack con effetti sia continui che conclusivi")
    void testOnTickEffettiMistiContinuiEConclusivi() {
        Hack hackMisto = new Hack("BleedExplode", "Danno continuo e finale", 2);
        hackMisto.addEffetto(new EffettoDanno(15, false)); // continuous: 15 per tick
        hackMisto.addEffetto(new EffettoDanno(30, true));  // conclusive: 30 al termine

        stato.getRamCondivisa().inserisci(hackMisto, nemico, giocatore);

        // Tick 1: applica 15 danni continui (100 - 15 = 85)
        combattimento.onTick();
        assertEquals(85, nemico.getPv());
        assertEquals(1, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());

        // Tick 2: applica 15 danni continui (85 - 15 = 70) + 30 conclusivi (70 - 30 = 40)
        combattimento.onTick();
        assertEquals(40, nemico.getPv());
        assertNull(stato.getRamCondivisa().visualizzaTesta());
    }

    @Test
    @DisplayName("onTick gestisce la progressione sequenziale di piu hack in coda nella RAM")
    void testOnTickProgressioneMultipliHackInCoda() {
        Hack hack1 = new Hack("Hack1", "Danno rapido", 1);
        hack1.addEffetto(new EffettoDanno(25, true)); // Conclusivo 1 tick

        Hack hack2 = new Hack("Hack2", "Danno continuo", 2);
        hack2.addEffetto(new EffettoDanno(10, false)); // Continuo 2 tick

        stato.getRamCondivisa().inserisci(hack1, nemico, giocatore);
        stato.getRamCondivisa().inserisci(hack2, nemico, giocatore);
        assertEquals(2, stato.getRamCondivisa().getHacks().size());
        assertEquals("Hack1", stato.getRamCondivisa().visualizzaTesta().getHack().getNome());

        // Tick 1: Hack1 scende a 0, viene rimosso, infligge 25 danni (100 - 25 = 75)
        combattimento.onTick();
        assertEquals(75, nemico.getPv());
        assertEquals(1, stato.getRamCondivisa().getHacks().size());
        assertEquals("Hack2", stato.getRamCondivisa().visualizzaTesta().getHack().getNome());

        // Tick 2: Hack2 scende da 2 a 1, infligge 10 danni continui (75 - 10 = 65)
        combattimento.onTick();
        assertEquals(65, nemico.getPv());
        assertEquals(1, stato.getRamCondivisa().visualizzaTesta().getTickInCoda());

        // Tick 3: Hack2 scende da 1 a 0, infligge 10 danni continui (65 - 10 = 55), rimosso
        combattimento.onTick();
        assertEquals(55, nemico.getPv());
        assertNull(stato.getRamCondivisa().visualizzaTesta());
        assertEquals(0, stato.getRamCondivisa().getHacks().size());
    }

    @Test
    @DisplayName("onTick con RAM vuota non lancia eccezioni e notifica comunque i listener")
    void testOnTickConRAMVuota() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        assertDoesNotThrow(() -> combattimento.onTick());
        assertEquals(1, spy.conteggioTick);
        assertEquals(1, spy.conteggioAggiornaRAM);
        assertEquals(1, spy.conteggioVitaAggiornata);
    }

    @Test
    @DisplayName("checkVittoria ritorna null e notifica null in caso di pareggio (tutti a 0 PV)")
    void testCheckVittoriaPareggio() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        giocatore.setPv(0);
        nemico.setPv(0);

        Entita vincitore = combattimento.checkVittoria();

        assertNull(vincitore);
        assertEquals(1, spy.conteggioVittoria);
        assertNull(spy.vincitoreNotificato);
    }

    @Test
    @DisplayName("checkVittoria ritorna Eroe quando il nemico e sconfitto")
    void testCheckVittoriaRitornaEroeSeNemicoMuore() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        nemico.setPv(0);
        Entita vincitore = combattimento.checkVittoria();

        assertSame(giocatore, vincitore);
        assertEquals(1, spy.conteggioVittoria);
        assertSame(giocatore, spy.vincitoreNotificato);
    }

    @Test
    @DisplayName("checkVittoria ritorna Nemico quando l'eroe e sconfitto")
    void testCheckVittoriaRitornaNemicoSeEroeMuore() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        giocatore.setPv(0);
        Entita vincitore = combattimento.checkVittoria();

        assertSame(nemico, vincitore);
        assertEquals(1, spy.conteggioVittoria);
        assertSame(nemico, spy.vincitoreNotificato);
    }

    @Test
    @DisplayName("checkVittoria ritorna null se entrambi sono vivi")
    void testCheckVittoriaRitornaNullSeEntrambiVivi() {
        assertNull(combattimento.checkVittoria());
    }

    @Test
    @DisplayName("checkVittoria e idempotente nelle notifiche (notifica vittoria esattamente una volta)")
    void testCheckVittoriaNotificaEsattamenteUnaVolta() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        nemico.setPv(0);

        // Chiamata 1
        Entita v1 = combattimento.checkVittoria();
        assertSame(giocatore, v1);
        assertEquals(1, spy.conteggioVittoria);

        // Chiamate successive non devono ri-notificare
        Entita v2 = combattimento.checkVittoria();
        assertSame(giocatore, v2);
        assertEquals(1, spy.conteggioVittoria);

        Entita v3 = combattimento.checkVittoria();
        assertSame(giocatore, v3);
        assertEquals(1, spy.conteggioVittoria);
    }

    @Test
    @DisplayName("avanza notifica ilNemicoNonPuoAttaccare se l'AI nemica ritorna null")
    void testAvanzaNotificaNemicoNonPuoAttaccareSeMossaNulla() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();

        // Nemico la cui IA ritorna null (nessuna mossa disponibile)
        NPC nemicoInerte = new NPC("PassiveEnemy", 100, "enemy.png", 5, new ArrayList<>(),
                new Pistola("PistolaDummy", "Desc", 6, 10, 0.0), 10, 0.0, (npc, st) -> null, false);
        StatoBattaglia1v1 statoInerte = new StatoBattaglia1v1(giocatore, nemicoInerte);
        CombattimentoATurni combattimentoInerte = new CombattimentoATurni(statoInerte);
        combattimentoInerte.aggiungiListener(spy);

        // Avanza passa il turno al nemico inerte
        combattimentoInerte.avanza();

        assertEquals(1, spy.conteggioNemicoNonPuoAttaccare);
    }

    @Test
    @DisplayName("spara con bersaglio null lancia NullPointerException")
    void testSparaConBersaglioNullLanciaNPE() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> combattimento.spara(null));
        assertTrue(ex.getMessage().contains("bersaglio"));
    }

    @Test
    @DisplayName("caricaHack con hack o bersaglio null lancia NullPointerException")
    void testCaricaHackConParametriNullLanciaNPE() {
        Hack hack = new Hack("TestHack", "Desc", 2);

        assertThrows(NullPointerException.class, () -> combattimento.caricaHack(null, nemico));
        assertThrows(NullPointerException.class, () -> combattimento.caricaHack(hack, null));
        assertThrows(NullPointerException.class, () -> combattimento.caricaHack(null, null));
    }

    @Test
    @DisplayName("rimuoviListener disiscrive correttamente il listener")
    void testRimuoviListenerNonRicevePiuNotifiche() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        combattimento.onTick();
        assertEquals(1, spy.conteggioTick);

        combattimento.rimuoviListener(spy);
        combattimento.onTick();
        // Il conteggio non deve incrementare ulteriormente
        assertEquals(1, spy.conteggioTick);

        // Rimozione di null non lancia eccezione
        assertDoesNotThrow(() -> combattimento.rimuoviListener(null));
    }

    @Test
    @DisplayName("eseguiMossa con azione non dannosa e non curativa non notifica vita")
    void testEseguiMossaAzioneSenzaEffettiDiVita() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();

        // Usiamo un nemico inerte per non innescare contrattacchi dannosi durante avanza()
        NPC nemicoPassivo = new NPC("Passive", 100, "enemy.png", 5, new ArrayList<>(),
                new Pistola("PistolaDummy", "Desc", 6, 10, 0.0), 10, 0.0, (n, s) -> null, false);
        CombattimentoATurni combattimentoPassivo = new CombattimentoATurni(new StatoBattaglia1v1(giocatore, nemicoPassivo));
        combattimentoPassivo.aggiungiListener(spy);

        Azione azioneNeutro = new Azione() {
            @Override
            public void esegui(StatoBattaglia stato) {}

            @Override
            public Set<EffectType> getEffectTypes() {
                return Set.of(EffectType.RAM);
            }
        };

        combattimentoPassivo.eseguiMossa(azioneNeutro);
        assertEquals(0, spy.conteggioVitaAggiornata);
    }

    @Test
    @DisplayName("ripristina riporta lo stato originario e resetta il flag di vittoria notificata")
    void testRipristinaRiportaStatoOriginarioEResettaVittoria() {
        SpyCombattimentoListener spy = new SpyCombattimentoListener();
        combattimento.aggiungiListener(spy);

        // Danneggia e uccidi nemico
        nemico.setPv(0);
        combattimento.checkVittoria();
        assertEquals(1, spy.conteggioVittoria);

        // Ripristina
        combattimento.ripristina();

        assertEquals(100, combattimento.getStatoBattaglia().getNemico(0).getPv());
        assertEquals(100, combattimento.getStatoBattaglia().getEroe(0).getPv());
        assertTrue(combattimento.isPlayerTurn());

        // Ora un nuovo checkVittoria (se nemico torna a 0) deve poter notificare di nuovo
        combattimento.getStatoBattaglia().getNemico(0).setPv(0);
        combattimento.checkVittoria();
        assertEquals(2, spy.conteggioVittoria);
    }

    @Test
    @DisplayName("Costruttore di copia clona stato e turni in modo indipendente")
    void testCostruttoreDiCopiaCopiaStatoETurni() {
        CombattimentoATurni copia = new CombattimentoATurni(combattimento);

        assertNotNull(copia.getStatoBattaglia());
        assertEquals(combattimento.isPlayerTurn(), copia.isPlayerTurn());
        assertNotSame(combattimento.getStatoBattaglia(), copia.getStatoBattaglia());
    }
}

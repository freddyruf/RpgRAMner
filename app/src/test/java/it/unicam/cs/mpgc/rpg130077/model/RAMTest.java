package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.QueuedHack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link RAM}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class RAMTest {

    private RAM ram;
    private Entita lanciatore;
    private Entita bersaglio;

    @BeforeEach
    void setUp() {
        ram = new RAM(10);
        lanciatore = creaEntitaDummy("Lanciatore");
        bersaglio = creaEntitaDummy("Bersaglio");
    }

    private Entita creaEntitaDummy(String nome) {
        return new Giocatore(nome, 100, "image.png", 10, new ArrayList<>(),
                new Pistola("PistolaTest", "Descrizione", 6, 10, 0.0));
    }

    private Hack creaHackDiTest(String nome, int durata) {
        return new Hack(nome, "Descrizione " + nome, durata) {
            @Override
            public Hack copy() {
                Hack copia = creaHackDiTest(getNome(), getDurata());
                for (Effetto e : getEffetti()) {
                    copia.addEffetto(e.copy());
                }
                return copia;
            }
        };
    }

    private static class SpyEffetto implements Effetto {
        private final boolean conclusive;
        int conteggioEsecuzioni = 0;
        StatoBattaglia ultimoStato;
        Entita ultimoLanciatore;
        Entita ultimoBersaglio;

        SpyEffetto(boolean conclusive) {
            this.conclusive = conclusive;
        }

        @Override
        public void eseguiEffetto(StatoBattaglia b, Entita lanciatore, Entita bersaglio) {
            conteggioEsecuzioni++;
            ultimoStato = b;
            ultimoLanciatore = lanciatore;
            ultimoBersaglio = bersaglio;
        }

        @Override
        public boolean isConclusive() {
            return conclusive;
        }

        @Override
        public Effetto copy() {
            return new SpyEffetto(conclusive);
        }
    }

    private static class FakeStatoBattaglia implements StatoBattaglia {
        private final RAM ram;
        private final Giocatore giocatore;

        FakeStatoBattaglia(RAM ram, Giocatore giocatore) {
            this.ram = ram;
            this.giocatore = giocatore;
        }

        @Override public RAM getRamCondivisa() { return ram; }
        @Override public Giocatore getGiocatore() { return giocatore; }
        @Override public ArrayList<Entita> getFazioneEroi() {
            ArrayList<Entita> eroi = new ArrayList<>();
            eroi.add(giocatore);
            return eroi;
        }
        @Override public ArrayList<Entita> getFazioneNemici() { return new ArrayList<>(); }
        @Override public Entita getEroe(int n) { return giocatore; }
        @Override public Entita getNemico(int n) { return null; }
        @Override public StatoBattaglia copy() { return this; }
    }

    @Test
    void costruttoreInizializzaCapacitaECodaVuota() {
        assertEquals(10, ram.getSpazioMassimoInSecondi());
        assertEquals(0, ram.getSpazioOccupato());
        assertNull(ram.visualizzaTesta());
        assertTrue(ram.getHacks().isEmpty());
    }

    @Test
    void costruttoreDiCopiaCopiaCapacitaECreaCodaVuota() {
        Hack hack = creaHackDiTest("Hack1", 4);
        ram.inserisci(hack, bersaglio, lanciatore);
        RAM ramCopia = new RAM(ram);

        assertEquals(ram.getSpazioMassimoInSecondi(), ramCopia.getSpazioMassimoInSecondi());
        assertEquals(0, ramCopia.getSpazioOccupato());
        assertTrue(ramCopia.getHacks().isEmpty());
    }

    @Test
    void inserisciHackValidaSpazioEAccoda() {
        Hack hack = creaHackDiTest("HackValida", 4);
        ram.inserisci(hack, bersaglio, lanciatore);

        assertEquals(4, ram.getSpazioOccupato());
        assertNotNull(ram.visualizzaTesta());
        assertEquals("HackValida", ram.visualizzaTesta().getHack().getNome());
        assertEquals(1, ram.getHacks().size());
    }

    @Test
    void inserisciHackNullLanciaNullPointerException() {
        assertThrows(NullPointerException.class, () -> ram.inserisci(null, bersaglio, lanciatore));
    }

    @Test
    void inserisciHackTroppoGrandeLanciaIllegalArgumentException() {
        Hack hackTroppoGrande = creaHackDiTest("HackGigante", 11);
        assertThrows(IllegalArgumentException.class, () -> ram.inserisci(hackTroppoGrande, bersaglio, lanciatore));
    }

    @Test
    void inserisciHackFinoAlLimiteEsattoConsentito() {
        Hack hack1 = creaHackDiTest("Hack1", 4);
        Hack hack2 = creaHackDiTest("Hack2", 6);
        ram.inserisci(hack1, bersaglio, lanciatore);
        ram.inserisci(hack2, bersaglio, lanciatore);

        // 4 + 6 = 10 (capacità massima esatta)
        assertEquals(10, ram.getSpazioOccupato());
        assertEquals(2, ram.getHacks().size());
    }

    @Test
    void inserisciHackOltreIlLimiteDopoPrecedentiInserimentiLanciaEccezione() {
        Hack hack1 = creaHackDiTest("Hack1", 5);
        ram.inserisci(hack1, bersaglio, lanciatore);
        Hack hack2 = creaHackDiTest("Hack2", 6);

        // 5 + 6 = 11 > 10
        assertThrows(IllegalArgumentException.class, () -> ram.inserisci(hack2, bersaglio, lanciatore));
    }

    @Test
    void rimuoviEstraeInOrdineFIFO() {
        Hack hack1 = creaHackDiTest("Hack1", 2);
        Hack hack2 = creaHackDiTest("Hack2", 3);
        ram.inserisci(hack1, bersaglio, lanciatore);
        ram.inserisci(hack2, bersaglio, lanciatore);

        QueuedHack rimosso1 = ram.rimuovi();
        assertNotNull(rimosso1);
        assertEquals("Hack1", rimosso1.getHack().getNome());

        QueuedHack rimosso2 = ram.rimuovi();
        assertNotNull(rimosso2);
        assertEquals("Hack2", rimosso2.getHack().getNome());

        assertNull(ram.rimuovi());
    }

    @Test
    void rimuoviSuRAMVuotaRitornaNull() {
        assertNull(ram.rimuovi());
    }

    @Test
    void visualizzaTestaRitornaElementoInTestaSenzaRimuoverlo() {
        Hack hack = creaHackDiTest("HackTesta", 3);
        ram.inserisci(hack, bersaglio, lanciatore);

        QueuedHack testa1 = ram.visualizzaTesta();
        QueuedHack testa2 = ram.visualizzaTesta();

        assertNotNull(testa1);
        assertSame(testa1, testa2);
        assertEquals(1, ram.getHacks().size());
    }

    @Test
    void visualizzaTestaSuRAMVuotaRitornaNull() {
        assertNull(ram.visualizzaTesta());
    }

    @Test
    void reverseInverteOrdineDelleHack() {
        Hack hack1 = creaHackDiTest("Hack1", 1);
        Hack hack2 = creaHackDiTest("Hack2", 2);
        Hack hack3 = creaHackDiTest("Hack3", 3);
        ram.inserisci(hack1, bersaglio, lanciatore);
        ram.inserisci(hack2, bersaglio, lanciatore);
        ram.inserisci(hack3, bersaglio, lanciatore);

        ram.reverse();

        assertEquals("Hack3", ram.rimuovi().getHack().getNome());
        assertEquals("Hack2", ram.rimuovi().getHack().getNome());
        assertEquals("Hack1", ram.rimuovi().getHack().getNome());
        assertNull(ram.rimuovi());
    }

    @Test
    void reverseSuRAMVuotaNonProduceErrori() {
        assertDoesNotThrow(() -> ram.reverse());
        assertTrue(ram.getHacks().isEmpty());
    }

    @Test
    void reverseConSingoloElementoMantieneStessoElemento() {
        Hack hack = creaHackDiTest("HackSingola", 2);
        ram.inserisci(hack, bersaglio, lanciatore);

        ram.reverse();

        assertEquals("HackSingola", ram.visualizzaTesta().getHack().getNome());
    }

    @Test
    void sortOrdinaCodaConComparator() {
        RAM ram20 = new RAM(20);
        Hack hack8 = creaHackDiTest("Hack8", 8);
        Hack hack2 = creaHackDiTest("Hack2", 2);
        Hack hack5 = creaHackDiTest("Hack5", 5);
        ram20.inserisci(hack8, bersaglio, lanciatore);
        ram20.inserisci(hack2, bersaglio, lanciatore);
        ram20.inserisci(hack5, bersaglio, lanciatore);

        ram20.sort(Comparator.comparingInt(QueuedHack::getTickInCoda));

        assertEquals("Hack2", ram20.rimuovi().getHack().getNome());
        assertEquals("Hack5", ram20.rimuovi().getHack().getNome());
        assertEquals("Hack8", ram20.rimuovi().getHack().getNome());
    }

    @Test
    void avanzaSuRAMVuotaNonProduceErrori() {
        FakeStatoBattaglia fakeStato = new FakeStatoBattaglia(ram, (Giocatore) lanciatore);
        assertDoesNotThrow(() -> ram.avanza(fakeStato));
    }

    @Test
    void avanzaDecrementaTickInCodaERiduceSpazioOccupato() {
        FakeStatoBattaglia fakeStato = new FakeStatoBattaglia(ram, (Giocatore) lanciatore);
        Hack hack = creaHackDiTest("HackTick", 3);
        ram.inserisci(hack, bersaglio, lanciatore);

        ram.avanza(fakeStato);

        // 3 - 1 = 2
        assertEquals(2, ram.visualizzaTesta().getTickInCoda());
        assertEquals(2, ram.getSpazioOccupato());
    }

    @Test
    void avanzaEsegueEffettiNonConclusiviAdOgniTick() {
        FakeStatoBattaglia fakeStato = new FakeStatoBattaglia(ram, (Giocatore) lanciatore);
        Hack hack = creaHackDiTest("HackTickContinuo", 3);
        SpyEffetto spyEffetto = new SpyEffetto(false);
        hack.addEffetto(spyEffetto);
        ram.inserisci(hack, bersaglio, lanciatore);

        ram.avanza(fakeStato);

        assertEquals(1, spyEffetto.conteggioEsecuzioni);
        assertSame(fakeStato, spyEffetto.ultimoStato);
        assertSame(lanciatore, spyEffetto.ultimoLanciatore);
        assertSame(bersaglio, spyEffetto.ultimoBersaglio);
    }

    @Test
    void avanzaEsegueEffettiConclusiviERimuoveQuandoTickZero() {
        FakeStatoBattaglia fakeStato = new FakeStatoBattaglia(ram, (Giocatore) lanciatore);
        Hack hack = creaHackDiTest("HackFinale", 1);
        SpyEffetto spyContinuo = new SpyEffetto(false);
        SpyEffetto spyConclusivo = new SpyEffetto(true);
        hack.addEffetto(spyContinuo);
        hack.addEffetto(spyConclusivo);
        ram.inserisci(hack, bersaglio, lanciatore);

        ram.avanza(fakeStato);

        assertEquals(1, spyContinuo.conteggioEsecuzioni);
        assertEquals(1, spyConclusivo.conteggioEsecuzioni);
        assertTrue(ram.getHacks().isEmpty());
        assertEquals(0, ram.getSpazioOccupato());
    }

    @Test
    void avanzaProcessaSoloTestaDellaCoda() {
        FakeStatoBattaglia fakeStato = new FakeStatoBattaglia(ram, (Giocatore) lanciatore);
        Hack hack1 = creaHackDiTest("Hack1", 2);
        Hack hack2 = creaHackDiTest("Hack2", 3);
        ram.inserisci(hack1, bersaglio, lanciatore);
        ram.inserisci(hack2, bersaglio, lanciatore);

        ram.avanza(fakeStato);

        // Solo la testa viene decrementata: 2 - 1 = 1, la seconda resta a 3
        assertEquals(1, ram.getHacks().get(0).getTickInCoda());
        assertEquals(3, ram.getHacks().get(1).getTickInCoda());
        // 1 + 3 = 4
        assertEquals(4, ram.getSpazioOccupato());
    }

    @Test
    void getHacksRitornaListaHacks() {
        assertNotNull(ram.getHacks());
        assertTrue(ram.getHacks().isEmpty());
    }
}

package it.unicam.cs.mpgc.rpg130077.controller.logica;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di unità per la classe {@link GestoreArmamento}.
 * Verifica il caricamento dei cataloghi, la risoluzione delle descrizioni,
 * il salvataggio dell'armamento scelto, e le query sullo stato della configurazione salvata.
 */
class GestoreArmamentoTest {

    private static class FakeCaricatoreCatalogo implements CaricatoreCatalogo {
        private final ArrayList<Arma> armi = new ArrayList<>();
        private final ArrayList<Hack> hacks = new ArrayList<>();

        FakeCaricatoreCatalogo() {
            armi.add(new Pistola("Glock", "Pistola rapida", 6, 15, 0.2));
            armi.add(new Mitragliatrice("Minigun", "Arma pesante a raffica", 30, 25, 0.1));
            armi.add(new Pistola("Revolver", "Pistola pesante", 2, 35, 0.5));

            Hack fireball = new Hack("Fireball", "Danno infuocato ad area", 4);
            fireball.addEffetto(new EffettoDanno(40, true));
            hacks.add(fireball);

            Hack heal = new Hack("HealPatch", "Ripristina punti vita", 3);
            heal.addEffetto(new EffettoCura(30, true));
            hacks.add(heal);

            Hack scan = new Hack("Scan", "Scansione dati memoria", 2);
            hacks.add(scan);
        }

        @Override
        public ArrayList<Arma> caricamentoCatalogoArmi() {
            return new ArrayList<>(armi);
        }

        @Override
        public ArrayList<Hack> caricamentoCatalogoHacks() {
            return new ArrayList<>(hacks);
        }
    }

    private static class SpyPersistenzaArmamento implements PersistenzaArmamento {
        private final CaricatoreCatalogo catalogo;
        ArrayList<Arma> armiSalvate = new ArrayList<>();
        ArrayList<Hack> hacksSalvati = new ArrayList<>();
        boolean salvataggioInvocato = false;

        SpyPersistenzaArmamento(CaricatoreCatalogo catalogo) {
            this.catalogo = catalogo;
        }

        @Override
        public ArrayList<Arma> getArmi() {
            return new ArrayList<>(armiSalvate);
        }

        @Override
        public ArrayList<Hack> getHacks() {
            return new ArrayList<>(hacksSalvati);
        }

        @Override
        public void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
            this.salvataggioInvocato = true;
            this.armiSalvate = (armi != null) ? new ArrayList<>(armi) : new ArrayList<>();
            this.hacksSalvati = (hacks != null) ? new ArrayList<>(hacks) : new ArrayList<>();
        }

        @Override
        public CaricatoreCatalogo getCatalogo() {
            return catalogo;
        }
    }

    private GestoreArmamento gestore;
    private SpyPersistenzaArmamento spyPersistenza;
    private FakeCaricatoreCatalogo fakeCaricatore;

    @BeforeEach
    void setUp() {
        fakeCaricatore = new FakeCaricatoreCatalogo();
        spyPersistenza = new SpyPersistenzaArmamento(fakeCaricatore);
        gestore = new GestoreArmamento(spyPersistenza);
    }

    @Test
    @DisplayName("Il costruttore carica correttamente i cataloghi armi e hacks dal caricatore")
    void testInizializzazioneCaricaCataloghiDaPersistenza() {
        ArrayList<Arma> catalogoArmi = gestore.getCatalogoArmi();
        ArrayList<Hack> catalogoHacks = gestore.getCatalogoHacks();

        assertNotNull(catalogoArmi, "Il catalogo armi non deve essere null");
        assertEquals(3, catalogoArmi.size(), "Il catalogo armi deve contenere 3 elementi");
        assertEquals("Glock", catalogoArmi.get(0).getNome());
        assertEquals("Minigun", catalogoArmi.get(1).getNome());
        assertEquals("Revolver", catalogoArmi.get(2).getNome());

        assertNotNull(catalogoHacks, "Il catalogo hacks non deve essere null");
        assertEquals(3, catalogoHacks.size(), "Il catalogo hacks deve contenere 3 elementi");
        assertEquals("Fireball", catalogoHacks.get(0).getNome());
        assertEquals("HealPatch", catalogoHacks.get(1).getNome());
        assertEquals("Scan", catalogoHacks.get(2).getNome());
    }

    @Test
    @DisplayName("getDescrizioneItem restituisce la descrizione corretta di un Hack")
    void testGetDescrizioneItemHackEsistente() {
        String descFireball = gestore.getDescrizioneItem("Fireball");
        assertEquals("Danno infuocato ad area", descFireball);

        String descHeal = gestore.getDescrizioneItem("HealPatch");
        assertEquals("Ripristina punti vita", descHeal);

        String descScan = gestore.getDescrizioneItem("Scan");
        assertEquals("Scansione dati memoria", descScan);
    }

    @Test
    @DisplayName("getDescrizioneItem restituisce la descrizione corretta di un'Arma")
    void testGetDescrizioneItemArmaEsistente() {
        String descGlock = gestore.getDescrizioneItem("Glock");
        assertEquals("Pistola rapida", descGlock);

        String descMinigun = gestore.getDescrizioneItem("Minigun");
        assertEquals("Arma pesante a raffica", descMinigun);

        String descRevolver = gestore.getDescrizioneItem("Revolver");
        assertEquals("Pistola pesante", descRevolver);
    }

    @Test
    @DisplayName("getDescrizioneItem restituisce 'Descrizione non disponibile' per nomi inesistenti")
    void testGetDescrizioneItemNonPresente() {
        String desc = gestore.getDescrizioneItem("ArmaInesistente");
        assertEquals("Descrizione non disponibile", desc);

        String descVuota = gestore.getDescrizioneItem("");
        assertEquals("Descrizione non disponibile", descVuota);
    }

    @Test
    @DisplayName("hasConfigurazioneSalvata restituisce false quando non ci sono salvataggi")
    void testHasConfigurazioneSalvataInizialmenteFalse() {
        assertFalse(gestore.hasConfigurazioneSalvata(),
                "Inizialmente senza dati salvati deve restituire false");
        assertTrue(gestore.getArmiSalvate().isEmpty(), "Le armi salvate devono essere vuote");
        assertTrue(gestore.getHacksSalvati().isEmpty(), "Gli hack salvati devono essere vuoti");
    }

    @Test
    @DisplayName("hasConfigurazioneSalvata restituisce false se sono presenti solo armi")
    void testHasConfigurazioneSalvataSoloArmi() {
        spyPersistenza.armiSalvate.add(new Pistola("Glock", "Desc", 6, 15, 0.2));
        spyPersistenza.hacksSalvati.clear();

        assertFalse(gestore.hasConfigurazioneSalvata(),
                "Se mancano gli hack la configurazione salvata deve essere considerata non completa");
    }

    @Test
    @DisplayName("hasConfigurazioneSalvata restituisce false se sono presenti solo hacks")
    void testHasConfigurazioneSalvataSoloHacks() {
        spyPersistenza.armiSalvate.clear();
        spyPersistenza.hacksSalvati.add(new Hack("Fireball", "Desc", 4));

        assertFalse(gestore.hasConfigurazioneSalvata(),
                "Se mancano le armi la configurazione salvata deve essere considerata non completa");
    }

    @Test
    @DisplayName("hasConfigurazioneSalvata restituisce true quando entrambi sono presenti")
    void testHasConfigurazioneSalvataTrueConEntrambi() {
        spyPersistenza.armiSalvate.add(new Pistola("Glock", "Desc", 6, 15, 0.2));
        spyPersistenza.hacksSalvati.add(new Hack("Fireball", "Desc", 4));

        assertTrue(gestore.hasConfigurazioneSalvata(),
                "Deve restituire true quando sia armi che hacks sono salvati");
    }

    @Test
    @DisplayName("getArmiSalvate e getHacksSalvati riflettono i dati salvati nella persistenza")
    void testGetArmiSalvateEGetHacksSalvati() {
        Arma arma1 = new Pistola("Glock", "Desc", 6, 15, 0.2);
        Arma arma2 = new Mitragliatrice("Minigun", "Desc", 30, 25, 0.1);
        Hack hack1 = new Hack("Fireball", "Desc", 4);
        Hack hack2 = new Hack("HealPatch", "Desc", 3);

        spyPersistenza.armiSalvate.add(arma1);
        spyPersistenza.armiSalvate.add(arma2);
        spyPersistenza.hacksSalvati.add(hack1);
        spyPersistenza.hacksSalvati.add(hack2);

        ArrayList<Arma> armi = gestore.getArmiSalvate();
        ArrayList<Hack> hacks = gestore.getHacksSalvati();

        assertEquals(2, armi.size());
        assertEquals("Glock", armi.get(0).getNome());
        assertEquals("Minigun", armi.get(1).getNome());

        assertEquals(2, hacks.size());
        assertEquals("Fireball", hacks.get(0).getNome());
        assertEquals("HealPatch", hacks.get(1).getNome());
    }

    @Test
    @DisplayName("salva mappa i nomi su oggetti di catalogo e li invia alla persistenza")
    void testSalvaSeparaArmiEHackEInviaAPersistenza() {
        ArrayList<String> setup = new ArrayList<>(List.of("Glock", "Fireball", "Minigun", "HealPatch"));
        gestore.salva(setup);

        assertTrue(spyPersistenza.salvataggioInvocato, "Il salvataggio deve essere stato invocato");
        assertEquals(2, spyPersistenza.armiSalvate.size());
        assertEquals("Glock", spyPersistenza.armiSalvate.get(0).getNome());
        assertEquals("Minigun", spyPersistenza.armiSalvate.get(1).getNome());

        assertEquals(2, spyPersistenza.hacksSalvati.size());
        assertEquals("Fireball", spyPersistenza.hacksSalvati.get(0).getNome());
        assertEquals("HealPatch", spyPersistenza.hacksSalvati.get(1).getNome());

        assertTrue(gestore.hasConfigurazioneSalvata());
    }

    @Test
    @DisplayName("salva con lista vuota invia liste vuote alla persistenza")
    void testSalvaConSetupVuotoInviaListeVuote() {
        gestore.salva(new ArrayList<>());

        assertTrue(spyPersistenza.salvataggioInvocato);
        assertTrue(spyPersistenza.armiSalvate.isEmpty());
        assertTrue(spyPersistenza.hacksSalvati.isEmpty());
        assertFalse(gestore.hasConfigurazioneSalvata());
    }

    @Test
    @DisplayName("salva ignora nomi non presenti nei cataloghi")
    void testSalvaIgnoraNomiNonPresentiNeiCataloghi() {
        ArrayList<String> setup = new ArrayList<>(List.of("Glock", "Sconosciuto1", "Fireball", "Sconosciuto2"));
        gestore.salva(setup);

        assertTrue(spyPersistenza.salvataggioInvocato);
        assertEquals(1, spyPersistenza.armiSalvate.size());
        assertEquals("Glock", spyPersistenza.armiSalvate.get(0).getNome());

        assertEquals(1, spyPersistenza.hacksSalvati.size());
        assertEquals("Fireball", spyPersistenza.hacksSalvati.get(0).getNome());
    }

    @Test
    @DisplayName("salva con nomi duplicati aggiunge ogni occorrenza trovata nel catalogo")
    void testSalvaConNomiDuplicati() {
        ArrayList<String> setup = new ArrayList<>(List.of("Glock", "Glock", "Fireball"));
        gestore.salva(setup);

        assertTrue(spyPersistenza.salvataggioInvocato);
        assertEquals(2, spyPersistenza.armiSalvate.size());
        assertEquals("Glock", spyPersistenza.armiSalvate.get(0).getNome());
        assertEquals("Glock", spyPersistenza.armiSalvate.get(1).getNome());
        assertEquals(1, spyPersistenza.hacksSalvati.size());
        assertEquals("Fireball", spyPersistenza.hacksSalvati.get(0).getNome());
    }

    @Test
    @DisplayName("salva con setup contenente solo armi invia lista hacks vuota")
    void testSalvaSoloArmi() {
        ArrayList<String> setup = new ArrayList<>(List.of("Glock", "Revolver"));
        gestore.salva(setup);

        assertTrue(spyPersistenza.salvataggioInvocato);
        assertEquals(2, spyPersistenza.armiSalvate.size());
        assertTrue(spyPersistenza.hacksSalvati.isEmpty());
        assertFalse(gestore.hasConfigurazioneSalvata());
    }

    @Test
    @DisplayName("salva con setup contenente solo hacks invia lista armi vuota")
    void testSalvaSoloHacks() {
        ArrayList<String> setup = new ArrayList<>(List.of("Fireball", "HealPatch"));
        gestore.salva(setup);

        assertTrue(spyPersistenza.salvataggioInvocato);
        assertTrue(spyPersistenza.armiSalvate.isEmpty());
        assertEquals(2, spyPersistenza.hacksSalvati.size());
        assertFalse(gestore.hasConfigurazioneSalvata());
    }

    @Test
    @DisplayName("Test isolamento e cataloghi vuoti nel caricatore")
    void testCataloghiVuoti() {
        CaricatoreCatalogo caricatoreVuoto = new CaricatoreCatalogo() {
            @Override
            public ArrayList<Arma> caricamentoCatalogoArmi() {
                return new ArrayList<>();
            }

            @Override
            public ArrayList<Hack> caricamentoCatalogoHacks() {
                return new ArrayList<>();
            }
        };
        SpyPersistenzaArmamento persistenzaVuota = new SpyPersistenzaArmamento(caricatoreVuoto);
        GestoreArmamento gestoreVuoto = new GestoreArmamento(persistenzaVuota);

        assertTrue(gestoreVuoto.getCatalogoArmi().isEmpty());
        assertTrue(gestoreVuoto.getCatalogoHacks().isEmpty());
        assertEquals("Descrizione non disponibile", gestoreVuoto.getDescrizioneItem("Glock"));

        gestoreVuoto.salva(new ArrayList<>(List.of("Glock", "Fireball")));
        assertTrue(persistenzaVuota.armiSalvate.isEmpty());
        assertTrue(persistenzaVuota.hacksSalvati.isEmpty());
    }
}

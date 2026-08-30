package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoSort;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Mitragliatrice;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di unità per {@link PersistenzaArmamentoJSON}.
 * Verifica il salvataggio e caricamento polimorfico dell'armamento in formato JSON,
 * l'accesso al catalogo tramite CaricatoreCatalogo, e la gestione degli errori per file assenti o malformati.
 */
class PersistenzaArmamentoJSONTest {

    private static final File FILE_DATA = new File("data/Armamento.json");
    private static final File BACKUP_DATA = new File("data/Armamento.json.test_bak");

    private PersistenzaArmamentoJSON persistenza;
    private CaricatoreCatalogo catalogo;

    @BeforeEach
    void setUp() throws IOException {
        catalogo = new PersistenzaCatalogoArmamentoJSON();
        persistenza = new PersistenzaArmamentoJSON(catalogo);

        // Backup existing data file if present
        if (FILE_DATA.exists()) {
            if (BACKUP_DATA.getParentFile() != null) {
                BACKUP_DATA.getParentFile().mkdirs();
            }
            Files.copy(FILE_DATA.toPath(), BACKUP_DATA.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FILE_DATA.delete();
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Restore backup if existed, otherwise delete test-generated file
        if (BACKUP_DATA.exists()) {
            if (FILE_DATA.getParentFile() != null) {
                FILE_DATA.getParentFile().mkdirs();
            }
            Files.copy(BACKUP_DATA.toPath(), FILE_DATA.toPath(), StandardCopyOption.REPLACE_EXISTING);
            BACKUP_DATA.delete();
        } else if (FILE_DATA.exists()) {
            FILE_DATA.delete();
        }
    }

    @Test
    @DisplayName("getCatalogo restituisce l'istanza di CaricatoreCatalogo iniettata con armi e hack")
    void testGetCatalogoRitornaCaricatoreIniettato() {
        CaricatoreCatalogo cat = persistenza.getCatalogo();
        assertNotNull(cat, "Il caricatore catalogo non deve essere null");
        assertSame(catalogo, cat, "Deve restituire la medesima istanza di CaricatoreCatalogo fornita nel costruttore");

        ArrayList<Arma> armiCatalogo = cat.caricamentoCatalogoArmi();
        ArrayList<Hack> hacksCatalogo = cat.caricamentoCatalogoHacks();

        assertNotNull(armiCatalogo);
        assertFalse(armiCatalogo.isEmpty());
        assertNotNull(hacksCatalogo);
        assertFalse(hacksCatalogo.isEmpty());
    }

    @Test
    @DisplayName("Salvataggio e caricamento di armi e hack con polimorfismo completo")
    void testSalvaECaricaEquipaggiamentoSceltoPolimorfico() {
        ArrayList<Arma> armiDaSalvare = new ArrayList<>();
        armiDaSalvare.add(new Pistola("PistolaCustom", "Desc Pistola", 6, 25, 0.25));
        armiDaSalvare.add(new Mitragliatrice("MitraCustom", "Desc Mitra", 30, 12, 0.1));

        ArrayList<Hack> hacksDaSalvare = new ArrayList<>();

        Hack hack1 = new Hack("MegaBurst", "Danno e Cura", 5);
        hack1.addEffetto(new EffettoDanno(50, true));
        hack1.addEffetto(new EffettoCura(20, false));
        hacksDaSalvare.add(hack1);

        Hack hack2 = new Hack("MemoryControl", "Reverse", 4);
        hack2.addEffetto(new EffettoReverse(true));
        hacksDaSalvare.add(hack2);

        persistenza.salvaEquipaggiamentoScelto(armiDaSalvare, hacksDaSalvare);

        ArrayList<Arma> armiCaricate = persistenza.getArmi();
        ArrayList<Hack> hacksCaricati = persistenza.getHacks();

        assertNotNull(armiCaricate);
        assertEquals(2, armiCaricate.size());
        assertTrue(armiCaricate.get(0) instanceof Pistola);
        assertEquals("PistolaCustom", armiCaricate.get(0).getNome());
        assertEquals(25, armiCaricate.get(0).getDanno());
        assertTrue(armiCaricate.get(1) instanceof Mitragliatrice);
        assertEquals("MitraCustom", armiCaricate.get(1).getNome());
        assertEquals(12, armiCaricate.get(1).getDanno());

        assertNotNull(hacksCaricati);
        assertEquals(2, hacksCaricati.size());

        Hack loadedHack1 = hacksCaricati.get(0);
        assertEquals("MegaBurst", loadedHack1.getNome());
        assertEquals(2, loadedHack1.getEffetti().size());
        assertTrue(loadedHack1.getEffetti().get(0) instanceof EffettoDanno);
        assertTrue(loadedHack1.getEffetti().get(1) instanceof EffettoCura);
        assertTrue(loadedHack1.getEffectTypes().contains(EffectType.DAMAGE));
        assertTrue(loadedHack1.getEffectTypes().contains(EffectType.HEAL));

        Hack loadedHack2 = hacksCaricati.get(1);
        assertEquals("MemoryControl", loadedHack2.getNome());
        assertEquals(1, loadedHack2.getEffetti().size());
        assertTrue(loadedHack2.getEffetti().get(0) instanceof EffettoReverse);
        assertTrue(loadedHack2.getEffectTypes().contains(EffectType.RAM));
    }

    @Test
    @DisplayName("Salvataggio e caricamento con liste vuote")
    void testSalvaECaricaListeVuote() {
        ArrayList<Arma> armiVuote = new ArrayList<>();
        ArrayList<Hack> hacksVuoti = new ArrayList<>();

        persistenza.salvaEquipaggiamentoScelto(armiVuote, hacksVuoti);

        ArrayList<Arma> armiCaricate = persistenza.getArmi();
        ArrayList<Hack> hacksCaricati = persistenza.getHacks();

        assertNotNull(armiCaricate, "Le armi caricate non devono essere null");
        assertTrue(armiCaricate.isEmpty(), "Le armi caricate devono essere vuote");

        assertNotNull(hacksCaricati, "Gli hack caricati non devono essere null");
        assertTrue(hacksCaricati.isEmpty(), "Gli hack caricati devono essere vuoti");
    }

    @Test
    @DisplayName("Salvataggio e caricamento solo armi (hacks vuoti)")
    void testSalvaECaricaSoloArmi() {
        ArrayList<Arma> armi = new ArrayList<>();
        armi.add(new Pistola("PistolaSolo", "Desc", 8, 18, 0.15));

        persistenza.salvaEquipaggiamentoScelto(armi, new ArrayList<>());

        ArrayList<Arma> armiCaricate = persistenza.getArmi();
        ArrayList<Hack> hacksCaricati = persistenza.getHacks();

        assertEquals(1, armiCaricate.size());
        assertEquals("PistolaSolo", armiCaricate.get(0).getNome());
        assertTrue(hacksCaricati.isEmpty());
    }

    @Test
    @DisplayName("Salvataggio e caricamento solo hack (armi vuote)")
    void testSalvaECaricaSoloHacks() {
        ArrayList<Hack> hacks = new ArrayList<>();
        hacks.add(new Hack("HackSolo", "Desc", 3));

        persistenza.salvaEquipaggiamentoScelto(new ArrayList<>(), hacks);

        ArrayList<Arma> armiCaricate = persistenza.getArmi();
        ArrayList<Hack> hacksCaricati = persistenza.getHacks();

        assertTrue(armiCaricate.isEmpty());
        assertEquals(1, hacksCaricati.size());
        assertEquals("HackSolo", hacksCaricati.get(0).getNome());
    }

    @Test
    @DisplayName("File inesistente: getArmi lancia RuntimeException")
    void testFileInesistenteGetArmiLanciaEccezione() {
        if (FILE_DATA.exists()) {
            FILE_DATA.delete();
        }

        assertThrows(RuntimeException.class, () -> persistenza.getArmi(),
                "getArmi deve propagare un'eccezione se il file non esiste");
    }

    @Test
    @DisplayName("File inesistente: getHacks restituisce lista vuota senza eccezioni")
    void testFileInesistenteGetHacksRitornaListaVuota() {
        if (FILE_DATA.exists()) {
            FILE_DATA.delete();
        }

        ArrayList<Hack> hacks = persistenza.getHacks();
        assertNotNull(hacks);
        assertTrue(hacks.isEmpty());
    }

    @Test
    @DisplayName("JSON malformato: getArmi lancia RuntimeException")
    void testJsonMalformatoGetArmiLanciaEccezione() throws IOException {
        if (FILE_DATA.getParentFile() != null) {
            FILE_DATA.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(FILE_DATA)) {
            writer.write("{ questo non e un json valido !!! }");
        }

        assertThrows(RuntimeException.class, () -> persistenza.getArmi(),
                "getArmi deve propagare un'eccezione se il JSON è malformato");
    }

    @Test
    @DisplayName("JSON malformato: getHacks restituisce lista vuota")
    void testJsonMalformatoGetHacksRitornaListaVuota() throws IOException {
        if (FILE_DATA.getParentFile() != null) {
            FILE_DATA.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(FILE_DATA)) {
            writer.write("{ questo non e un json valido !!! }");
        }

        ArrayList<Hack> hacks = persistenza.getHacks();
        assertNotNull(hacks);
        assertTrue(hacks.isEmpty());
    }

    @Test
    @DisplayName("JSON con campi null o oggetto vuoto gestito senza eccezioni")
    void testJsonConCampiNullOVuoto() throws IOException {
        if (FILE_DATA.getParentFile() != null) {
            FILE_DATA.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(FILE_DATA)) {
            writer.write("{\"armi\": null, \"hacks\": null}");
        }

        ArrayList<Arma> armi = persistenza.getArmi();
        ArrayList<Hack> hacks = persistenza.getHacks();

        assertNotNull(armi);
        assertTrue(armi.isEmpty());
        assertNotNull(hacks);
        assertTrue(hacks.isEmpty());
    }

    @Test
    @DisplayName("Salvataggio crea la cartella 'data/' genitore se non esiste")
    void testSalvataggioCreaCartellaGenitore() {
        if (FILE_DATA.exists()) {
            FILE_DATA.delete();
        }

        ArrayList<Arma> armi = new ArrayList<>();
        armi.add(new Pistola("PistolaTestDir", "Desc", 5, 10, 0.1));

        persistenza.salvaEquipaggiamentoScelto(armi, new ArrayList<>());

        assertTrue(FILE_DATA.exists(), "Il file Armamento.json deve essere stato creato");
        ArrayList<Arma> armiCaricate = persistenza.getArmi();
        assertEquals(1, armiCaricate.size());
        assertEquals("PistolaTestDir", armiCaricate.get(0).getNome());
    }
}

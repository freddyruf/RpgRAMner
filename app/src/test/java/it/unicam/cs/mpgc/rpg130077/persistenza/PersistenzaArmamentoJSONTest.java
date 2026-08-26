package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link PersistenzaArmamentoJSON}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class PersistenzaArmamentoJSONTest {

    private static final File FILE_DATA = new File("data/Armamento.json");
    private static final File BACKUP_DATA = new File("data/Armamento.json.test_bak");

    private PersistenzaArmamentoJSON persistenza;

    @BeforeEach
    void setUp() throws IOException {
        persistenza = new PersistenzaArmamentoJSON();
        if (FILE_DATA.exists()) {
            if (BACKUP_DATA.getParentFile() != null) {
                BACKUP_DATA.getParentFile().mkdirs();
            }
            Files.copy(FILE_DATA.toPath(), BACKUP_DATA.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (BACKUP_DATA.exists()) {
            Files.copy(BACKUP_DATA.toPath(), FILE_DATA.toPath(), StandardCopyOption.REPLACE_EXISTING);
            BACKUP_DATA.delete();
        } else if (FILE_DATA.exists()) {
            FILE_DATA.delete();
        }
    }

    @Test
    void testSalvaECaricaEquipaggiamentoScelto() {
        ArrayList<Arma> armiDaSalvare = new ArrayList<>();
        armiDaSalvare.add(new Pistola("PistolaCustom", "Desc", 6, 25, 0.2));

        ArrayList<Hack> hacksDaSalvare = new ArrayList<>();
        hacksDaSalvare.add(new Hack("FireballCustom", "Desc", 4));

        persistenza.salvaEquipaggiamentoScelto(armiDaSalvare, hacksDaSalvare);

        ArrayList<Arma> armiCaricate = persistenza.getArmi();
        ArrayList<Hack> hacksCaricati = persistenza.getHacks();

        assertNotNull(armiCaricate);
        assertEquals(1, armiCaricate.size());
        assertEquals("PistolaCustom", armiCaricate.get(0).getNome());

        assertNotNull(hacksCaricati);
        assertEquals(1, hacksCaricati.size());
        assertEquals("FireballCustom", hacksCaricati.get(0).getNome());
    }

    @Test
    void testCaricamentoConFileInesistenteRitornaListeVuote() {
        if (FILE_DATA.exists()) {
            FILE_DATA.delete();
        }

        ArrayList<Arma> armi = persistenza.getArmi();
        ArrayList<Hack> hacks = persistenza.getHacks();

        assertNotNull(armi);
        assertTrue(armi.isEmpty());
        assertNotNull(hacks);
        assertTrue(hacks.isEmpty());
    }

    @Test
    void testDelegazioneCaricamentoCataloghi() {
        ArrayList<Arma> catalogoArmi = persistenza.caricamentoCatalogoArmi();
        ArrayList<Hack> catalogoHacks = persistenza.caricamentoCatalogoHacks();

        assertNotNull(catalogoArmi);
        assertFalse(catalogoArmi.isEmpty());
        assertNotNull(catalogoHacks);
        assertFalse(catalogoHacks.isEmpty());
    }
}

package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link GestoreArmamento}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class GestoreArmamentoTest {

    private static class ArmaConDescrizione extends Arma {
        ArmaConDescrizione(String nome, String desc, int maxCaricatore, int danno, double critChance) {
            super(nome, desc, maxCaricatore, danno, critChance);
            this.descrizione = desc;
        }

        @Override
        public Arma copy() {
            return new ArmaConDescrizione(nome, descrizione, maxCaricatore, danno, critChance);
        }

        @Override
        public int calcolaDanno() {
            return getDanno();
        }
    }

     private static class FakeCaricatoreCatalogo implements CaricatoreCatalogo {
        private final ArrayList<Arma> armi = new ArrayList<>();
        private final ArrayList<Hack> hacks = new ArrayList<>();
        FakeCaricatoreCatalogo() {
            armi.add(new Pistola("Glock", "Pistola rapida", 6, 15, 0.2));
            armi.add(new Pistola("Revolver", "Pistol pesante", 2, 8, 0.5));
            hacks.add(new Hack("Fireball", "Danno infuocato", 4));
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
        ArrayList<Arma> armiSalvate;
        ArrayList<Hack> hacksSalvati;
        boolean salvataggioInvocato = false;

        @Override
        public ArrayList<Arma> getArmi() {
            return new ArrayList<>();
        }

        @Override
        public ArrayList<Hack> getHacks() {
            return new ArrayList<>();
        }

        @Override
        public void salvaEquipaggiamentoScelto(ArrayList<Arma> armi, ArrayList<Hack> hacks) {
            this.salvataggioInvocato = true;
            this.armiSalvate = armi;
            this.hacksSalvati = hacks;
        }



        private CaricatoreCatalogo catalogo;
        public SpyPersistenzaArmamento(CaricatoreCatalogo catalogo) {
            this.catalogo = catalogo;
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
    void getDescrizioneItemRestituisceDescrizioneHack() {
        String desc = gestore.getDescrizioneItem("Fireball");
        assertEquals("Danno infuocato", desc);
    }

    @Test
    void getDescrizioneItemRestituisceDescrizioneArma() {
        String desc = gestore.getDescrizioneItem("Glock");
        assertEquals("Pistola rapida", desc);
    }

    @Test
    void getDescrizioneItemNonPresenteRestituisceStringaDefault() {
        String desc = gestore.getDescrizioneItem("ItemInesistente");
        assertEquals("Descrizione non disponibile", desc);
    }

    @Test
    void salvaSeparaArmiEHackEInviaAPersistenza() {
        ArrayList<String> setup = new ArrayList<>(List.of("Glock", "Fireball"));
        gestore.salva(setup);

        assertTrue(spyPersistenza.salvataggioInvocato);
        assertEquals(1, spyPersistenza.armiSalvate.size());
        assertEquals("Glock", spyPersistenza.armiSalvate.get(0).getNome());
        assertEquals(1, spyPersistenza.hacksSalvati.size());
        assertEquals("Fireball", spyPersistenza.hacksSalvati.get(0).getNome());
    }

    @Test
    void salvaConSetupVuotoInviaListeVuote() {
        gestore.salva(new ArrayList<>());

        assertTrue(spyPersistenza.salvataggioInvocato);
        assertTrue(spyPersistenza.armiSalvate.isEmpty());
        assertTrue(spyPersistenza.hacksSalvati.isEmpty());
    }

    @Test
    void salvaIgnoraNomiNonPresentiNeiCataloghi() {
        ArrayList<String> setup = new ArrayList<>(List.of("Glock", "NomeSconosciuto"));
        gestore.salva(setup);

        assertTrue(spyPersistenza.salvataggioInvocato);
        assertEquals(1, spyPersistenza.armiSalvate.size());
        assertEquals("Glock", spyPersistenza.armiSalvate.get(0).getNome());
        assertTrue(spyPersistenza.hacksSalvati.isEmpty());
    }
}

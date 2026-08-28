package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaCatalogoArmamentoJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link GameFactory}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class GameFactoryTest {

    private GameFactory gameFactory;

    @BeforeEach
    void setUp() {
        gameFactory = new GameFactory();
    }

    @Test
    void testCreaNuovaPartitaSempliceConCatalogoReale() {
        CaricatoreCatalogo catalogo = new PersistenzaCatalogoArmamentoJSON();
        SistemaCombattimento combattimento = gameFactory.creaNuovaPartitaSemplice(catalogo.caricamentoCatalogoArmi(), catalogo.caricamentoCatalogoHacks());

        assertNotNull(combattimento);
        assertInstanceOf(CombattimentoATurni.class, combattimento);

        StatoBattaglia stato = combattimento.getStatoBattaglia();
        assertNotNull(stato);

        Giocatore giocatore = stato.getGiocatore();
        assertNotNull(giocatore);
        assertEquals("Giocatore", giocatore.getNome());
        assertEquals(100, giocatore.getPv());
        assertEquals(100, giocatore.getMaxPv());
        assertEquals(10, giocatore.getSpazioRAM());
        assertNotNull(giocatore.getArma());
        assertFalse(giocatore.getHacks().isEmpty());

        assertEquals(1, stato.getFazioneNemici().size());
        assertInstanceOf(NPC.class, stato.getNemico(0));

        NPC nemico = (NPC) stato.getNemico(0);
        assertEquals("Cybermorb", nemico.getNome());
        assertEquals(100, nemico.getPv());
        assertEquals(100, nemico.getMaxPv());
        assertEquals(5, nemico.getSpazioRAM());
        assertEquals(5, nemico.getDannoAttaccoASorpresa());
        assertNotNull(nemico.getArma());

        // Il nemico ha un hack in meno rispetto al giocatore (il primo rimosso per differenziare)
        assertEquals(giocatore.getHacks().size() - 1, nemico.getHacks().size());

        // La RAM totale condivisa deve corrispondere alla somma degli spazi RAM (10 + 5 = 15)
        assertEquals(15, stato.getRamCondivisa().getSpazioMassimoInSecondi());
    }

    @Test
    void testCreaNuovaPartitaSempliceConStubCatalogo() {
        Arma armaGiocatore = new Pistola("PistolaEroe", "Arma eroe", 6, 25, 0.0);
        Arma armaNemico = new Pistola("PistolaNemico", "Arma nemico", 4, 15, 0.0);
        Hack hack1 = new Hack("Hack1", "Desc1", 2);
        Hack hack2 = new Hack("Hack2", "Desc2", 3);

        CaricatoreCatalogo catalogoStub = new CaricatoreCatalogo() {
            @Override
            public ArrayList<Arma> caricamentoCatalogoArmi() {
                return new ArrayList<>(List.of(armaGiocatore, armaNemico));
            }

            @Override
            public ArrayList<Hack> caricamentoCatalogoHacks() {
                return new ArrayList<>(List.of(hack1, hack2));
            }
        };

        SistemaCombattimento combattimento = gameFactory.creaNuovaPartitaSemplice(catalogoStub.caricamentoCatalogoArmi(), catalogoStub.caricamentoCatalogoHacks());
        StatoBattaglia stato = combattimento.getStatoBattaglia();

        assertEquals(armaGiocatore.getNome(), stato.getGiocatore().getArma().getNome());
        assertEquals(armaNemico.getNome(), stato.getNemico(0).getArma().getNome());

        // Giocatore possiede entrambi gli hack
        assertEquals(2, stato.getGiocatore().getHacks().size());
        assertEquals("Hack1", stato.getGiocatore().getHacks().get(0).getNome());
        assertEquals("Hack2", stato.getGiocatore().getHacks().get(1).getNome());

        // Nemico possiede solo il secondo hack (indice 0 rimosso)
        assertEquals(1, stato.getNemico(0).getHacks().size());
        assertEquals("Hack2", stato.getNemico(0).getHacks().get(0).getNome());
    }

    @Test
    void testPartitaCreataProntaAlCombattimento() {
        CaricatoreCatalogo catalogo = new PersistenzaCatalogoArmamentoJSON();
        SistemaCombattimento combattimento = gameFactory.creaNuovaPartitaSemplice(catalogo.caricamentoCatalogoArmi(), catalogo.caricamentoCatalogoHacks());

        assertNull(combattimento.checkVittoria());
        assertDoesNotThrow(combattimento::avanza);
    }
}

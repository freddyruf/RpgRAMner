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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test completi per la classe {@link GameFactory}.
 * Copre la creazione di partite semplici, validazioni con eccezioni (NPE, ISE),
 * e la verifica delle copie profonde (deep copy) degli equipaggiamenti.
 */
class GameFactoryTest {

    private GameFactory gameFactory;
    private List<Arma> armiGiocatore;
    private List<Hack> hacksGiocatore;
    private List<Arma> catalogoArmi;
    private List<Hack> catalogoHacks;

    @BeforeEach
    void setUp() {
        gameFactory = new GameFactory();

        armiGiocatore = new ArrayList<>(List.of(
                new Pistola("PistolaPlayer", "Arma eroe", 6, 25, 0.0)
        ));
        hacksGiocatore = new ArrayList<>(List.of(
                new Hack("PlayerHack1", "Desc1", 2),
                new Hack("PlayerHack2", "Desc2", 3)
        ));
        catalogoArmi = new ArrayList<>(List.of(
                new Pistola("PistolaCat0", "Cat 0", 6, 10, 0.0),
                new Pistola("PistolaCat1", "Cat 1 (Enemy)", 6, 15, 0.0),
                new Pistola("PistolaCat2", "Cat 2", 6, 20, 0.0)
        ));
        catalogoHacks = new ArrayList<>(List.of(
                new Hack("CatHack0", "Cat Hack 0", 2),
                new Hack("CatHack1", "Cat Hack 1 (Enemy 0)", 3),
                new Hack("CatHack2", "Cat Hack 2 (Enemy 1)", 4)
        ));
    }

    @Test
    @DisplayName("Crea partita semplice con catalogo reale da file JSON")
    void testCreaNuovaPartitaSempliceConCatalogoReale() {
        CaricatoreCatalogo catalogo = new PersistenzaCatalogoArmamentoJSON();
        List<Arma> armi = catalogo.caricamentoCatalogoArmi();
        List<Hack> hacks = catalogo.caricamentoCatalogoHacks();

        SistemaCombattimento combattimento = gameFactory.creaNuovaPartitaSemplice(armi, hacks, armi, hacks);

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

        // Il nemico riceve gli hack a partire dall'indice 1 del catalogo (size - 1)
        assertEquals(hacks.size() - 1, nemico.getHacks().size());

        // La RAM condivisa ha capacita pari a 10 + 5 = 15
        assertEquals(15, stato.getRamCondivisa().getSpazioMassimoInSecondi());
    }

    @Test
    @DisplayName("Crea partita semplice mappando correttamente gli oggetti forniti")
    void testCreaNuovaPartitaSempliceConListeConfigurate() {
        SistemaCombattimento combattimento = gameFactory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, catalogoArmi, catalogoHacks);
        StatoBattaglia stato = combattimento.getStatoBattaglia();

        Giocatore eroe = stato.getGiocatore();
        NPC nemico = (NPC) stato.getNemico(0);

        // Verifica arma eroe da armiGiocatore.get(0)
        assertEquals("PistolaPlayer", eroe.getArma().getNome());
        // Verifica arma nemico da catalogoArmi.get(1)
        assertEquals("PistolaCat1", nemico.getArma().getNome());

        // Verifica hack eroe
        assertEquals(2, eroe.getHacks().size());
        assertEquals("PlayerHack1", eroe.getHacks().get(0).getNome());
        assertEquals("PlayerHack2", eroe.getHacks().get(1).getNome());

        // Verifica hack nemico (dal catalogo indice 1 in poi)
        assertEquals(2, nemico.getHacks().size());
        assertEquals("CatHack1", nemico.getHacks().get(0).getNome());
        assertEquals("CatHack2", nemico.getHacks().get(1).getNome());
    }

    @Test
    @DisplayName("Lancia NullPointerException se uno qualsiasi dei 4 parametri e null")
    void testCreaNuovaPartitaParametriNullLanciaNPE() {
        assertThrows(NullPointerException.class, () ->
                gameFactory.creaNuovaPartitaSemplice(null, hacksGiocatore, catalogoArmi, catalogoHacks));
        assertThrows(NullPointerException.class, () ->
                gameFactory.creaNuovaPartitaSemplice(armiGiocatore, null, catalogoArmi, catalogoHacks));
        assertThrows(NullPointerException.class, () ->
                gameFactory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, null, catalogoHacks));
        assertThrows(NullPointerException.class, () ->
                gameFactory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, catalogoArmi, null));
    }

    @Test
    @DisplayName("Lancia IllegalStateException se la lista armi del giocatore e vuota")
    void testCreaNuovaPartitaArmiGiocatoreVuotaLanciaISE() {
        List<Arma> armiVuote = new ArrayList<>();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                gameFactory.creaNuovaPartitaSemplice(armiVuote, hacksGiocatore, catalogoArmi, catalogoHacks));
        assertTrue(ex.getMessage().contains("Armi giocatore insufficienti"));
    }

    @Test
    @DisplayName("Lancia IllegalStateException se il catalogo armi ha meno di 2 armi")
    void testCreaNuovaPartitaCatalogoArmiInsufficienteLanciaISE() {
        // Catalogo vuoto
        assertThrows(IllegalStateException.class, () ->
                gameFactory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, new ArrayList<>(), catalogoHacks));

        // Catalogo con solo 1 arma
        List<Arma> catalogoUnaSolaArma = new ArrayList<>(List.of(
                new Pistola("UnicaArma", "Desc", 6, 10, 0.0)
        ));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                gameFactory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, catalogoUnaSolaArma, catalogoHacks));
        assertTrue(ex.getMessage().contains("Catalogo armi insufficiente"));
    }

    @Test
    @DisplayName("Verifica che le armi e gli hack assegnati siano copie profonde (deep copy)")
    void testCreaNuovaPartitaDeepCopyEquipaggiamento() {
        SistemaCombattimento combattimento = gameFactory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, catalogoArmi, catalogoHacks);
        StatoBattaglia stato = combattimento.getStatoBattaglia();

        Giocatore eroe = stato.getGiocatore();
        NPC nemico = (NPC) stato.getNemico(0);

        // Le istanze assegnate devono essere distinte (clonate) rispetto alle liste originali
        assertNotSame(armiGiocatore.get(0), eroe.getArma());
        assertNotSame(hacksGiocatore.get(0), eroe.getHacks().get(0));
        assertNotSame(hacksGiocatore.get(1), eroe.getHacks().get(1));

        assertNotSame(catalogoArmi.get(1), nemico.getArma());
        assertNotSame(catalogoHacks.get(1), nemico.getHacks().get(0));
        assertNotSame(catalogoHacks.get(2), nemico.getHacks().get(1));
    }

    @Test
    @DisplayName("Crea partita con catalogo hack di 1 solo elemento genera nemico senza hack")
    void testCreaNuovaPartitaCatalogoHackMinimo() {
        List<Hack> catalogoUnSoloHack = new ArrayList<>(List.of(
                new Hack("SoloHack0", "Desc", 2)
        ));

        SistemaCombattimento combattimento = gameFactory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, catalogoArmi, catalogoUnSoloHack);
        NPC nemico = (NPC) combattimento.getStatoBattaglia().getNemico(0);

        // Dal momento che l'indice 0 viene scartato per il nemico, la lista deve essere vuota
        assertTrue(nemico.getHacks().isEmpty());
    }

    @Test
    @DisplayName("La partita creata e pronta al combattimento")
    void testPartitaCreataProntaAlCombattimento() {
        SistemaCombattimento combattimento = gameFactory.creaNuovaPartitaSemplice(armiGiocatore, hacksGiocatore, catalogoArmi, catalogoHacks);

        assertNull(combattimento.checkVittoria());
        assertDoesNotThrow(combattimento::avanza);
    }
}

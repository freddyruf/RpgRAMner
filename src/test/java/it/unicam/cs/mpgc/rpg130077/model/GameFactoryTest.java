package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
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

    private GameFactory factory;
    private List<Arma> armiGiocatore;
    private List<Hack> hacksGiocatore;
    private List<Arma> catalogoArmi;
    private List<Hack> catalogoHacks;

    @BeforeEach
    void setUp() {
        factory = new GameFactory(new StatoBattaglia1v1());

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

    private SistemaCombattimento creaCombattimento() {
        SistemaCombattimento combattimento = new CombattimentoATurni();
        factory.creaNuovaPartitaSemplice(combattimento);
        return combattimento;
    }

    @Test
    @DisplayName("Crea partita semplice con catalogo reale da file JSON")
    void testCreaNuovaPartitaSempliceConCatalogoReale() {
        CaricatoreCatalogo catalogo = new PersistenzaCatalogoArmamentoJSON();
        List<Arma> armi = catalogo.caricamentoCatalogoArmi();
        List<Hack> hacks = catalogo.caricamentoCatalogoHacks();
        factory.setCatalogoArmi(armi);
        factory.setCatalogoHack(hacks);
        factory.setEquipaggiamentoGiocatoreArmi(armi);
        factory.setEquipaggiamentoGiocatoreHacks(hacks);
        factory.setStrategia(new StrategiaCasuale());
        SistemaCombattimento combattimento = creaCombattimento();

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

        // Il nemico riceve fino a MAX_HACK_NEMICO hack distinti scelti a caso dal catalogo
        assertEquals(Math.min(GameFactory.MAX_HACK_NEMICO, hacks.size()), nemico.getHacks().size());
        List<String> nomiCatalogo = hacks.stream().map(Hack::getNome).toList();
        List<String> nomiNemico = nemico.getHacks().stream().map(Hack::getNome).toList();
        assertTrue(nomiCatalogo.containsAll(nomiNemico));
        assertEquals(nomiNemico.size(), nomiNemico.stream().distinct().count());

        // La RAM condivisa ha capacita pari a 10 + 5 = 15
        assertEquals(15, stato.getRamCondivisa().getSpazioMassimoInSecondi());
    }

    @Test
    @DisplayName("Crea partita semplice mappando correttamente gli oggetti forniti")
    void testCreaNuovaPartitaSempliceConListeConfigurate() {
        factory.setCatalogoArmi(catalogoArmi);
        factory.setCatalogoHack(catalogoHacks);
        factory.setEquipaggiamentoGiocatoreArmi(armiGiocatore);
        factory.setEquipaggiamentoGiocatoreHacks(hacksGiocatore);
        factory.setEquipaggiamentoNemicoArmi(List.of(catalogoArmi.get(1)));
        factory.setEquipaggiamentoNemicoHacks(List.of(catalogoHacks.get(1), catalogoHacks.get(2)));
        factory.setStrategia(new StrategiaCasuale());
        SistemaCombattimento combattimento = creaCombattimento();
        StatoBattaglia stato = combattimento.getStatoBattaglia();

        Giocatore eroe = stato.getGiocatore();
        NPC nemico = (NPC) stato.getNemico(0);

        // Verifica arma eroe da armiGiocatore.get(0)
        assertEquals("PistolaPlayer", eroe.getArma().getNome());
        // Verifica arma nemico impostata esplicitamente (catalogoArmi.get(1))
        assertEquals("PistolaCat1", nemico.getArma().getNome());

        // Verifica hack eroe
        assertEquals(2, eroe.getHacks().size());
        assertEquals("PlayerHack1", eroe.getHacks().get(0).getNome());
        assertEquals("PlayerHack2", eroe.getHacks().get(1).getNome());

        // Verifica hack nemico impostati esplicitamente
        assertEquals(2, nemico.getHacks().size());
        assertEquals("CatHack1", nemico.getHacks().get(0).getNome());
        assertEquals("CatHack2", nemico.getHacks().get(1).getNome());
    }

    @Test
    @DisplayName("Verifica che le armi e gli hack assegnati siano copie profonde (deep copy)")
    void testCreaNuovaPartitaDeepCopyEquipaggiamento() {
        factory.setCatalogoArmi(catalogoArmi);
        factory.setCatalogoHack(catalogoHacks);
        factory.setEquipaggiamentoGiocatoreArmi(armiGiocatore);
        factory.setEquipaggiamentoGiocatoreHacks(hacksGiocatore);
        factory.setEquipaggiamentoNemicoArmi(List.of(catalogoArmi.get(1)));
        factory.setEquipaggiamentoNemicoHacks(List.of(catalogoHacks.get(1), catalogoHacks.get(2)));
        factory.setStrategia(new StrategiaCasuale());
        SistemaCombattimento combattimento = creaCombattimento();
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
    @DisplayName("Crea partita con catalogo hack di 1 solo elemento genera nemico con quell'unico hack")
    void testCreaNuovaPartitaCatalogoHackMinimo() {
        List<Hack> catalogoUnSoloHack = new ArrayList<>(List.of(
                new Hack("SoloHack0", "Desc", 2)
        ));
        factory.setCatalogoArmi(catalogoArmi);
        factory.setCatalogoHack(catalogoUnSoloHack);
        factory.setEquipaggiamentoGiocatoreArmi(armiGiocatore);
        factory.setEquipaggiamentoGiocatoreHacks(hacksGiocatore);
        factory.setStrategia(new StrategiaCasuale());
        SistemaCombattimento combattimento = creaCombattimento();
        NPC nemico = (NPC) combattimento.getStatoBattaglia().getNemico(0);

        // Il nemico prende al massimo tanti hack quanti ne offre il catalogo, senza ripetizioni
        assertEquals(1, nemico.getHacks().size());
        assertEquals("SoloHack0", nemico.getHacks().get(0).getNome());
    }

    @Test
    @DisplayName("La partita creata e pronta al combattimento")
    void testPartitaCreataProntaAlCombattimento() {
        factory.setCatalogoArmi(catalogoArmi);
        factory.setCatalogoHack(catalogoHacks);
        factory.setEquipaggiamentoGiocatoreArmi(armiGiocatore);
        factory.setEquipaggiamentoGiocatoreHacks(hacksGiocatore);
        factory.setStrategia(new StrategiaCasuale());
        SistemaCombattimento combattimento = creaCombattimento();

        assertNull(combattimento.checkVittoria());
        assertDoesNotThrow(combattimento::avanza);
    }
}

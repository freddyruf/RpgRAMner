package it.unicam.cs.mpgc.rpg130077.model.Entita;

import it.unicam.cs.mpgc.rpg130077.model.Azioni.Azione;
import it.unicam.cs.mpgc.rpg130077.model.Azioni.AzioneSparo;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Pistola;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCombattimento;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link NPC}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class NPCTest {

    private Arma creaArma() {
        return new Pistola("PistolaNPC", "Descrizione", 6, 12, 0.1);
    }

    private NPC creaNPC(StrategiaCombattimento strategia, double chanceASorpresa) {
        return new NPC("Boss", 120, "boss.png", 8, new ArrayList<>(), creaArma(), 25, chanceASorpresa, strategia, false);
    }

    @Test
    void costruttoreNPCInizializzaCampiSpecifici() {
        StrategiaCombattimento dummyStrategia = (npc, stato) -> null;
        NPC npc = creaNPC(dummyStrategia, 0.4);

        assertEquals("Boss", npc.getNome());
        assertEquals(120, npc.getMaxPv());
        assertEquals(120, npc.getPv());
        assertEquals(25, npc.getDannoAttaccoASorpresa());
    }

    @Test
    void costruttoreNPCLanciaEccezioneSeChanceMaggioreDiUno() {
        StrategiaCombattimento dummyStrategia = (npc, stato) -> null;
        assertThrows(IllegalArgumentException.class, () ->
                new NPC("Boss", 100, "boss.png", 8, new ArrayList<>(), creaArma(), 20, 1.5, dummyStrategia, false));
    }

    @Test
    void costruttoreDiCopiaCopiaCampiSpecifici() {
        StrategiaCombattimento dummyStrategia = (npc, stato) -> null;
        NPC originale = creaNPC(dummyStrategia, 0.3);
        NPC copia = new NPC(originale);

        assertNotSame(originale, copia);
        assertEquals(originale.getNome(), copia.getNome());
        assertEquals(originale.getDannoAttaccoASorpresa(), copia.getDannoAttaccoASorpresa());
        assertNotSame(originale.getArma(), copia.getArma());
    }

    @Test
    void copyMethodRitornaNuovaIstanzaNPC() {
        StrategiaCombattimento dummyStrategia = (npc, stato) -> null;
        NPC originale = creaNPC(dummyStrategia, 0.3);
        Entita copia = originale.copy();

        assertTrue(copia instanceof NPC);
        assertNotSame(originale, copia);
        assertEquals(originale.getDannoAttaccoASorpresa(), ((NPC) copia).getDannoAttaccoASorpresa());
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Con chance 0.0 l'attacco a sorpresa non deve mai verificarsi")
    void controllaAttaccoASorpresaConChanceZeroRitornaSempreFalse() {
        StrategiaCombattimento dummyStrategia = (npc, stato) -> null;
        NPC npc = creaNPC(dummyStrategia, 0.0);

        for (int i = 0; i < 50; i++) {
            assertFalse(npc.controllaAttaccoASorpresa(), "Con chance 0.0 deve restituire sempre false");
        }
    }

    @Test
    void richiediMossaDelegaAStrategiaCombattimento() {
        boolean[] strategiaInvocata = {false};
        Azione azionePrevista = new AzioneSparo(null, null);

        StrategiaCombattimento spyStrategia = (npc, stato) -> {
            strategiaInvocata[0] = true;
            return azionePrevista;
        };

        NPC npc = creaNPC(spyStrategia, 0.2);
        Azione mossa = npc.richiediMossa( null);

        assertTrue(strategiaInvocata[0]);
        assertSame(azionePrevista, mossa);
    }
}

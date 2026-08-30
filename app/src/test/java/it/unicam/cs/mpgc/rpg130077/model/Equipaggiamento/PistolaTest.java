package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link Pistola}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class PistolaTest {

    private Pistola creaPistola(double critChance) {
        return new Pistola("Glock", "Pistola semiautomatica", 6, 15, critChance);
    }

    @Test
    void costruttoreInizializzaCampiECaricatore() {
        Pistola pistola = creaPistola(0.2);

        assertEquals("Glock", pistola.getNome());
        assertEquals("Pistola semiautomatica", pistola.getDescrizione());
        assertEquals(6, pistola.getCaricatore());
        assertEquals(6, pistola.getMaxCaricatore());
        assertEquals(15, pistola.getDanno());
    }

    @Test
    void costruttoreLanciaNullPointerExceptionSeNomeNullo() {
        assertThrows(NullPointerException.class, () -> new Pistola(null, "Desc", 6, 15, 0.2));
    }

    @Test
    void costruttoreLanciaIllegalArgumentExceptionSeMaxCaricatoreMinoreOUgualeAZero() {
        assertThrows(IllegalArgumentException.class, () -> new Pistola("Pistola", "Desc", 0, 15, 0.2));
        assertThrows(IllegalArgumentException.class, () -> new Pistola("Pistola", "Desc", -1, 15, 0.2));
    }


    @Test
    void copyMethodCreaNuovaIstanzaIndipendente() {
        Pistola originale = creaPistola(0.3);
        Arma copia = originale.copy();

        assertTrue(copia instanceof Pistola);
        assertNotSame(originale, copia);
        assertEquals(originale.getNome(), copia.getNome());
        assertEquals(originale.getDanno(), copia.getDanno());
        assertEquals(originale.getMaxCaricatore(), copia.getMaxCaricatore());
        assertEquals(originale.getCaricatore(), copia.getCaricatore());
    }

    @Test
    void calcolaDannoSenzaCriticoRestituisceDannoBase() {
        Pistola pistola = creaPistola(0.0);

        for (int i = 0; i < 20; i++) {
            assertEquals(15, pistola.calcolaDanno());
        }
    }

    @Test
    void calcolaDannoConCriticoSicuroRestituisceDoppioDanno() {
        Pistola pistola = creaPistola(1.0);

        for (int i = 0; i < 20; i++) {
            // 15 * 2 = 30
            assertEquals(30, pistola.calcolaDanno());
        }
    }

    @Test
    void calcolaDannoProbabilisticoProduceValoriValidi() {
        Pistola pistola = creaPistola(0.5);

        for (int i = 0; i < 50; i++) {
            int danno = pistola.calcolaDanno();
            assertTrue(danno == 15 || danno == 30);
        }
    }
}

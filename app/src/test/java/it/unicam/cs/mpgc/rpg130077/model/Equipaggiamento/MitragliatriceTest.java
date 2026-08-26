package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe {@link Mitragliatrice}.
 *
 * Per la stesura di questi test sono stati utilizzati strumenti di intelligenza artificiale generativa, in accordo con le linee guida del corso.
 */
class MitragliatriceTest {

    private Mitragliatrice creaMitragliatrice(double critChance) {
        return new Mitragliatrice("Minigun", "Mitragliatrice a canne rotanti", 30, 10, critChance);
    }

    @Test
    void costruttoreInizializzaCampiECaricatore() {
        Mitragliatrice mitragliatrice = creaMitragliatrice(0.1);

        assertEquals("Minigun", mitragliatrice.getNome());
        assertEquals(30, mitragliatrice.getCaricatore());
        assertEquals(30, mitragliatrice.getMaxCaricatore());
        assertEquals(10, mitragliatrice.getDanno());
    }

    @Test
    void copyMethodCreaNuovaIstanzaIndipendente() {
        Mitragliatrice originale = creaMitragliatrice(0.2);
        Arma copia = originale.copy();

        assertTrue(copia instanceof Mitragliatrice);
        assertNotSame(originale, copia);
        assertEquals(originale.getNome(), copia.getNome());
        assertEquals(originale.getDanno(), copia.getDanno());
        assertEquals(originale.getMaxCaricatore(), copia.getMaxCaricatore());
        assertEquals(originale.getCaricatore(), copia.getCaricatore());
    }

    @Test
    void calcolaDannoSenzaCriticoRestituisceCinqueVolteDannoBase() {
        Mitragliatrice mitragliatrice = creaMitragliatrice(0.0);

        for (int i = 0; i < 20; i++) {
            // 5 colpi * 10 danno = 50
            assertEquals(50, mitragliatrice.calcolaDanno());
        }
    }

    @Test
    void calcolaDannoConCriticoSicuroRestituisceDieciVolteDannoBase() {
        Mitragliatrice mitragliatrice = creaMitragliatrice(1.0);

        for (int i = 0; i < 20; i++) {
            // 5 colpi * (10 * 2) danno = 100
            assertEquals(100, mitragliatrice.calcolaDanno());
        }
    }

    @Test
    void calcolaDannoProbabilisticoProduceMultipliNelRangeValido() {
        Mitragliatrice mitragliatrice = creaMitragliatrice(0.4);

        for (int i = 0; i < 50; i++) {
            int danno = mitragliatrice.calcolaDanno();
            assertTrue(danno >= 50 && danno <= 100);
            assertEquals(0, danno % 10);
        }
    }
}

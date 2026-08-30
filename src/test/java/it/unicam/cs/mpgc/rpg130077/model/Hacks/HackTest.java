package it.unicam.cs.mpgc.rpg130077.model.Hacks;

import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoCura;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoDanno;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffettoReverse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HackTest {

    @Test
    @DisplayName("Inizializzazione corretta delle proprietà di un Hack")
    void testCreazioneHack() {
        Hack hack = new Hack("Firewall", "Difesa temporanea", 3);
        assertEquals("Firewall", hack.getNome());
        assertEquals("Difesa temporanea", hack.getDescrizione());
        assertEquals(3, hack.getDurata());
        assertTrue(hack.getEffetti().isEmpty());
    }

    @Test
    @DisplayName("Aggiunta e rimozione effetti")
    void testAggiuntaERimozioneEffetti() {
        Hack hack = new Hack("Combattente", "Desc", 2);
        EffettoDanno danno = new EffettoDanno(20, true);
        hack.addEffetto(danno);

        assertEquals(1, hack.getEffetti().size());
        assertTrue(hack.getEffectTypes().contains(it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType.DAMAGE));
        assertFalse(hack.getEffectTypes().contains(it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType.HEAL));

        hack.removeEffetto(danno);
        assertEquals(0, hack.getEffetti().size());
        assertFalse(hack.getEffectTypes().contains(it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType.DAMAGE));
    }

    @Test
    @DisplayName("Identificazione polimorfica di effetti di cura")
    void testIsHealDealer() {
        Hack hack = new Hack("HealPatch", "Desc", 1);
        hack.addEffetto(new EffettoCura(30, true));

        assertTrue(hack.getEffectTypes().contains(it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType.HEAL));
        assertFalse(hack.getEffectTypes().contains(it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType.DAMAGE));
    }

    @Test
    @DisplayName("Copia profonda di un Hack")
    void testCopiaProfondaHack() {
        Hack originale = new Hack("ReverseHack", "Inverte RAM", 4);
        originale.addEffetto(new EffettoReverse(true));

        Hack copia = originale.copy();
        assertNotSame(originale, copia);
        assertEquals(originale.getNome(), copia.getNome());
        assertEquals(originale.getDurata(), copia.getDurata());
        assertEquals(1, copia.getEffetti().size());
        assertNotSame(originale.getEffetti().get(0), copia.getEffetti().get(0));
    }
}
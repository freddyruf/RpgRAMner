package it.unicam.cs.mpgc.rpg130077.model.Effetti;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffectTypeTest {

    @Test
    @DisplayName("EffectType enum values contain DAMAGE, HEAL, RAM with exact length 3")
    void testEnumValues() {
        EffectType[] values = EffectType.values();
        assertEquals(3, values.length, "EffectType should contain exactly 3 constants");
        assertArrayEquals(new EffectType[]{EffectType.DAMAGE, EffectType.HEAL, EffectType.RAM}, values);
    }

    @Test
    @DisplayName("EffectType valueOf returns correct constants for valid names")
    void testValueOfValid() {
        assertSame(EffectType.DAMAGE, EffectType.valueOf("DAMAGE"));
        assertSame(EffectType.HEAL, EffectType.valueOf("HEAL"));
        assertSame(EffectType.RAM, EffectType.valueOf("RAM"));
    }

    @Test
    @DisplayName("EffectType valueOf throws IllegalArgumentException for unknown constant name")
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> EffectType.valueOf("INVALID_EFFECT"));
        assertThrows(IllegalArgumentException.class, () -> EffectType.valueOf("damage"));
    }

    @Test
    @DisplayName("EffectType valueOf throws NullPointerException for null argument")
    void testValueOfNull() {
        assertThrows(NullPointerException.class, () -> EffectType.valueOf(null));
    }

    @Test
    @DisplayName("EffectType ordinals are consistent and non-negative")
    void testOrdinals() {
        assertEquals(0, EffectType.DAMAGE.ordinal());
        assertEquals(1, EffectType.HEAL.ordinal());
        assertEquals(2, EffectType.RAM.ordinal());
    }
}

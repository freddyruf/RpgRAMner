package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.controller.SessionState;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreEquipaggiamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamentoJSON;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaCatalogoArmamentoJSON;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionStateTest {

    @Test
    @DisplayName("SessionState default constructor initializes fields to default null and zero")
    void testDefaultInitialization() {
        SessionState sessione = new SessionState();
        assertNull(sessione.getCombattimento(), "combattimento should be null by default");
        assertNull(sessione.getClock(), "clock should be null by default");
        assertNull(sessione.getGestoreEquipaggiamento(), "gestoreEquipaggiamento should be null by default");
        assertEquals(0, sessione.getMaxSpazioRam(), "spazioRam should be 0 by default");
    }

    @Test
    @DisplayName("SessionState fields can be assigned and retrieved accurately")
    void testFieldAssignmentAndRetrieval() {
        SessionState sessione = new SessionState();

        Clock clock = new Clock(() -> {});
        sessione.setClock(clock);
        assertSame(clock, sessione.getClock());


        CaricatoreCatalogo catalogo = new PersistenzaCatalogoArmamentoJSON();
        PersistenzaArmamento persistenza = new PersistenzaArmamentoJSON(catalogo);
        GestoreEquipaggiamento gestore = new GestoreArmamento(persistenza);
        sessione.setGestoreEquipaggiamento(gestore);
        assertSame(gestore, sessione.getGestoreEquipaggiamento());

        // Test reassignment to null
        sessione.setClock(null);
        assertNull(sessione.getClock());
        sessione.setGestoreEquipaggiamento(null);
        assertNull(sessione.getGestoreEquipaggiamento());
    }

    @Test
    @DisplayName("SessionState supports multiple distinct instances without shared static state")
    void testMultipleInstancesIndependence() {
        SessionState session1 = new SessionState();
        SessionState session2 = new SessionState();

        assertEquals(0, session1.getMaxSpazioRam());
        assertEquals(0, session2.getMaxSpazioRam());
    }
}

package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
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
        assertNull(sessione.combattimento, "combattimento should be null by default");
        assertNull(sessione.clock, "clock should be null by default");
        assertNull(sessione.gestoreArmamento, "gestoreArmamento should be null by default");
        assertEquals(0, sessione.spazioRam, "spazioRam should be 0 by default");
    }

    @Test
    @DisplayName("SessionState fields can be assigned and retrieved accurately")
    void testFieldAssignmentAndRetrieval() {
        SessionState sessione = new SessionState();

        sessione.spazioRam = 15;
        assertEquals(15, sessione.spazioRam);

        Clock clock = new Clock(() -> {});
        sessione.clock = clock;
        assertSame(clock, sessione.clock);


        CaricatoreCatalogo catalogo = new PersistenzaCatalogoArmamentoJSON();
        PersistenzaArmamento persistenza = new PersistenzaArmamentoJSON(catalogo);
        GestoreArmamento gestore = new GestoreArmamento(persistenza);
        sessione.gestoreArmamento = gestore;
        assertSame(gestore, sessione.gestoreArmamento);

        // Test reassignment to null
        sessione.clock = null;
        assertNull(sessione.clock);
        sessione.gestoreArmamento = null;
        assertNull(sessione.gestoreArmamento);
    }

    @Test
    @DisplayName("SessionState supports multiple distinct instances without shared static state")
    void testMultipleInstancesIndependence() {
        SessionState session1 = new SessionState();
        SessionState session2 = new SessionState();

        session1.spazioRam = 10;
        session2.spazioRam = 20;

        assertEquals(10, session1.spazioRam);
        assertEquals(20, session2.spazioRam);
        assertNotEquals(session1.spazioRam, session2.spazioRam);
    }
}

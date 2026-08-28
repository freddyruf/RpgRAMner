package it.unicam.cs.mpgc.rpg130077.persistenza;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import java.util.ArrayList;

/**
 * Interfaccia che astrae un oggetto che serve a interfacciarsi con la persistenza del catalogo(insieme di Armi e hack disponibili nel gioco)
 */
public interface CaricatoreCatalogo {
    ArrayList<Arma> caricamentoCatalogoArmi();
    ArrayList<Hack> caricamentoCatalogoHacks();
}
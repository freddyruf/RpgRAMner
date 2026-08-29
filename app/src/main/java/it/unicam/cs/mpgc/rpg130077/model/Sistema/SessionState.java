package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreArmamento;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;

/**
 * Classe che fa da banca di informazioni
 */
public class SessionState { // Da creare nel package model/Sistema/
    public SistemaCombattimento combattimento;
    public Clock clock;
    public int spazioRam;
    public GestoreArmamento gestoreArmamento;
}
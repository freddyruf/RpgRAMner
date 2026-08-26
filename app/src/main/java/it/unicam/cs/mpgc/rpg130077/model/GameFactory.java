package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;

import java.util.ArrayList;

/**
 * Classe che si occupa di gestire l'inizializzazione di una battaglia
 */
public class GameFactory {

    /**
     * Funzione che restituisce un sistema di combattiento "generico" e prefatto
     * @param catalogo
     * @return un SistemaCombattimento pronto al uso
     */
    public SistemaCombattimento creaNuovaPartitaSemplice(CaricatoreCatalogo catalogo) {

        ArrayList<Arma> catalogoArmi = catalogo.caricamentoCatalogoArmi();
        ArrayList<Hack> catalogoHack = catalogo.caricamentoCatalogoHack();

        ArrayList<Hack> catalogoHackNemico = new ArrayList<>(catalogoHack);
        if (!catalogoHackNemico.isEmpty()) {
            catalogoHackNemico.remove(0); //RImuovo 1 hack dal nemico per creare un po di differenze
        }

        Arma armaG = catalogoArmi.get(0);
        Arma armaN = catalogoArmi.get(1);


        Giocatore giocatore = new Giocatore("Giocatore", 100, "", 10, catalogoHack, armaG);
        NPC nemico = new NPC("Cybermorb", 100, "", 5, catalogoHackNemico, armaN, 5, 0.1, new StrategiaCasuale());

        return new CombattimentoATurni(new StatoBattaglia1v1(giocatore, nemico));
    }
}
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
        if (catalogo == null) {
            throw new NullPointerException("Il caricatore catalogo non può essere nullo");
        }

        ArrayList<Arma> catalogoArmi = catalogo.caricamentoCatalogoArmi();
        ArrayList<Hack> catalogoHack = catalogo.caricamentoCatalogoHack();

        if (catalogoArmi.size() < 2) {
            throw new IllegalStateException("Catalogo insufficiente per creare una partita");
        }

        ArrayList<Hack> hacksGiocatore = new ArrayList<>();
        for (Hack h : catalogoHack) hacksGiocatore.add(h.copy());

        ArrayList<Hack> hacksNemico = new ArrayList<>();
        for (int i = 1; i < catalogoHack.size(); i++) {
            hacksNemico.add(catalogoHack.get(i).copy());
        }

        Arma armaG = catalogoArmi.get(0).copy();
        Arma armaN = catalogoArmi.get(1).copy();

        Giocatore giocatore = new Giocatore("Giocatore", 100, "", 10, hacksGiocatore, armaG, true);
        NPC nemico = new NPC("Cybermorb", 100, "", 5, hacksNemico, armaN, 5, 0.1, new StrategiaCasuale(), false);

        return new CombattimentoATurni(new StatoBattaglia1v1(giocatore, nemico));
    }
}
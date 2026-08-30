package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia1v1;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import java.util.List;
import java.util.ArrayList;

public class GameFactory {
    public SistemaCombattimento creaNuovaPartitaSemplice(List<Arma> equipaggiamentoGiocatoreArmi, List<Hack> equipaggiamentoGiocatoreHacks, List<Arma> catalogoArmi, List<Hack> catalogoHack) {
        if (equipaggiamentoGiocatoreArmi == null || equipaggiamentoGiocatoreHacks == null || catalogoArmi == null || catalogoHack == null) {
            throw new NullPointerException("I cataloghi o l'equipaggiamento non possono essere nulli");
        }
        if (equipaggiamentoGiocatoreArmi.isEmpty()) {
            throw new IllegalStateException("Armi giocatore insufficienti");
        }
        if (catalogoArmi.size() < 2) {
            throw new IllegalStateException("Catalogo armi insufficiente per creare una partita");
        }

        ArrayList<Hack> hacksGiocatore = new ArrayList<>();
        for (Hack h : equipaggiamentoGiocatoreHacks) hacksGiocatore.add(h.copy());

        ArrayList<Hack> hacksNemico = new ArrayList<>();
        for (int i = 1; i < catalogoHack.size(); i++) {
            hacksNemico.add(catalogoHack.get(i).copy());
        }

        Arma armaG = equipaggiamentoGiocatoreArmi.get(0).copy();
        Arma armaN = catalogoArmi.get(1).copy();

        Giocatore giocatore = new Giocatore("Giocatore", 100, "", 10, hacksGiocatore, armaG, true);
        NPC nemico = new NPC("Cybermorb", 100, "", 5, hacksNemico, armaN, 5, 0.1, new StrategiaCasuale(), false);

        return new CombattimentoATurni(new StatoBattaglia1v1(giocatore, nemico));
    }
}
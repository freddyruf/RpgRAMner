package it.unicam.cs.mpgc.rpg130077.controller.logica;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.GameFactory;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Servizio applicativo che prepara e avvia una partita.
 */
public class GestorePartita {
    private final GestoreEquipaggiamento gestoreEquipaggiamento;
    private final GameFactory gameFactory;
    private final CaricatoreCatalogo caricatoreCatalogo;

    public GestorePartita(GestoreEquipaggiamento gestoreEquipaggiamento, GameFactory gameFactory, CaricatoreCatalogo caricatoreCatalogo) {
        this.gestoreEquipaggiamento = Objects.requireNonNull(gestoreEquipaggiamento);
        this.gameFactory = Objects.requireNonNull(gameFactory);
        this.caricatoreCatalogo = Objects.requireNonNull(caricatoreCatalogo);
    }

    /**
     * Avvia una nuova partita delegando a GameFactory la generazione di SistemaDiCombattimento
     * @return SistemaDiCombattimento
     */
    public void avviaPartita(SistemaCombattimento sistemaCombattimento) {
        ArrayList<Arma> armiGiocatore = gestoreEquipaggiamento.getArmiSalvate();
        ArrayList<Hack> hacksGiocatore = gestoreEquipaggiamento.getHacksSalvati();
        ArrayList<Arma> catalogoArmi = caricatoreCatalogo.caricamentoCatalogoArmi();
        ArrayList<Hack> catalogoHacks = caricatoreCatalogo.caricamentoCatalogoHacks();

        gameFactory.setCatalogoArmi(catalogoArmi);
        gameFactory.setCatalogoHack(catalogoHacks);
        gameFactory.setEquipaggiamentoGiocatoreArmi(armiGiocatore);
        gameFactory.setEquipaggiamentoGiocatoreHacks(hacksGiocatore);
        gameFactory.creaNuovaPartitaSemplice(sistemaCombattimento);
    }
}

package it.unicam.cs.mpgc.rpg130077.controller;

import it.unicam.cs.mpgc.rpg130077.controller.logica.GestoreEquipaggiamento;
import it.unicam.cs.mpgc.rpg130077.controller.logica.GestorePartita;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;

/**
 * Classe che fa da banca di informazioni
 */
public class SessionState {
    private SistemaCombattimento combattimento;
    private Clock clock;
    private GestoreEquipaggiamento gestoreEquipaggiamento;
    private GestorePartita gestorePartita;

    public SistemaCombattimento getCombattimento() {
        return combattimento;
    }

    public void setCombattimento(SistemaCombattimento combattimento) {
        this.combattimento = combattimento;
    }

    public Clock getClock() {
        return clock;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public int getMaxSpazioRam() {
        if (combattimento != null) {
            return combattimento.getStatoBattaglia().getRamCondivisa()
                    .getSpazioMassimoInSecondi();
        }
        return 0;
    }

    public GestoreEquipaggiamento getGestoreEquipaggiamento() {
        return gestoreEquipaggiamento;
    }

    public void setGestoreEquipaggiamento(GestoreEquipaggiamento gestoreEquipaggiamento) {
        this.gestoreEquipaggiamento = gestoreEquipaggiamento;
    }

    public GestorePartita getGestorePartita() {
        return gestorePartita;
    }

    public void setGestorePartita(GestorePartita gestorePartita) {
        this.gestorePartita = gestorePartita;
    }
}
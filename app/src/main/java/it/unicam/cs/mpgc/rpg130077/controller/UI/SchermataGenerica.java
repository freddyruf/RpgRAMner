package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Sistema.Clock;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.PersistenzaArmamento;

public abstract class SchermataGenerica {
    protected PersistenzaArmamento persistenzaArmamento;
    protected CaricatoreCatalogo caricatoreCatalogo;
    int spazioRam;
    protected SistemaCombattimento sistemaCombattimento;
    Clock clock;



    public void setSpazioRam(int spazioRam){
        this.spazioRam = spazioRam;
    }


    public void setPersistenze(PersistenzaArmamento p, CaricatoreCatalogo c) {
        this.persistenzaArmamento = p;
        this.caricatoreCatalogo = c;
    }

    public void setSistemaCombattimento(SistemaCombattimento s) {
        this.sistemaCombattimento = s;
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }
}

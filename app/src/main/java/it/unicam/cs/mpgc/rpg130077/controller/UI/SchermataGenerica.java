package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamento;

public abstract class SchermataGenerica {
    protected persistenzaArmamento persistenzaArmamento;
    protected CaricatoreCatalogo caricatoreCatalogo;
    int spazioRam;



    public void setSpazioRam(int spazioRam){
        this.spazioRam = spazioRam;
    }


    public void setPersistenze(persistenzaArmamento p, CaricatoreCatalogo c) {
        this.persistenzaArmamento = p;
        this.caricatoreCatalogo = c;
    }
}

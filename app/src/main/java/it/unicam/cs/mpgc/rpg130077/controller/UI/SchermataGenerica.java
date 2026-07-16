package it.unicam.cs.mpgc.rpg130077.controller.UI;

import it.unicam.cs.mpgc.rpg130077.persistenza.CaricatoreCatalogo;
import it.unicam.cs.mpgc.rpg130077.persistenza.persistenzaArmamento;

public abstract class SchermataGenerica {
    protected persistenzaArmamento persistenzaArmamento;
    protected CaricatoreCatalogo caricatoreCatalogo;


    public void setPersistenze(persistenzaArmamento p, CaricatoreCatalogo c) {
        this.persistenzaArmamento = p;
        this.caricatoreCatalogo = c;
    }
}

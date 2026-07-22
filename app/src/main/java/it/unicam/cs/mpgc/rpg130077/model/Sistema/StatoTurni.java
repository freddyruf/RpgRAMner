package it.unicam.cs.mpgc.rpg130077.model.Sistema;

import it.unicam.cs.mpgc.rpg130077.model.Entita.Entita;

import java.util.ArrayList;

public class StatoTurni {
    int numeroTurniAlleati;
    int numeroTurniNemici;
    int turno;
    public StatoTurni(int numeroTurniAlleati, int numeroTurniNemici) {
        this.numeroTurniAlleati = numeroTurniAlleati;
        this.numeroTurniNemici = numeroTurniNemici;
        turno=0;
    }
    public void avanzaTurno(){
        turno++;
    }

    /**
     *
     * @return un numero compresto tra 0 e il numero di entità in gioco, da 0 al numero di alleati in gioco, da 0 a Numero Alleati indica l'indice dal alleato, altrimenti facendo -Numero Alleati è quello dei nemici
     */
    public int getTurno() {
        int entitaTotali=numeroTurniAlleati+numeroTurniNemici;
        return turno%entitaTotali;
    }

}

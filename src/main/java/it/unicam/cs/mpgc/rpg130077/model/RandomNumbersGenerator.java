package it.unicam.cs.mpgc.rpg130077.model;

/**
 * Generatore di numeri casuali. Isolato in una classe propria per poterlo
 * sostituire (es. con uno deterministico) nei test.
 */
public class RandomNumbersGenerator {

    /**
     * @return un double casuale tra 0 e 1, è il "core" della randomicità in questa classe
     */
    public double getRandom() {
        return Math.random();
    }

    /**
     * @return un intero casuale tra min e max(escluso)
     * @throws IllegalArgumentException se max <=min
     */
    public int getInt(int min, int max) {
        if (max <= min) {
            throw new IllegalArgumentException("max deve essere maggiore di min");
        }
        return min + (int) (getRandom() * (max - min));
    }
}

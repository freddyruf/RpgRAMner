package it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento;

import it.unicam.cs.mpgc.rpg130077.model.RandomNumbersGenerator;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.EffectType;
import it.unicam.cs.mpgc.rpg130077.model.Effetti.Effetto;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Classe astratta che rappresenta un'arma nel gioco.
 */
public abstract class  Arma implements Equipaggiamento {
    String nome;
    String descrizione;
    int maxCaricatore;
    int caricatore;
    int danno;
    double critChance; //min 0 max 1
    protected RandomNumbersGenerator random = new RandomNumbersGenerator();
    protected ArrayList<Effetto> effetti;

    public Arma(String nome,String descrizione, int maxCaricatore, int danno, double critChance) {
        if(nome == null) {
            throw new NullPointerException("Il nome non può essere nullo");
        }
        if (maxCaricatore <= 0) throw new IllegalArgumentException("Il caricatore deve essere positivo");
        this.nome = nome;
        this.maxCaricatore = maxCaricatore;
        this.caricatore = maxCaricatore;
        this.danno = danno;
        this.critChance = critChance;
        this.descrizione = descrizione;
        this.effetti = new ArrayList<>();
    }
    public Arma(Arma arma) {
        this.nome = arma.nome;
        this.descrizione = arma.descrizione;
        this.maxCaricatore = arma.maxCaricatore;
        this.caricatore = arma.caricatore;
        this.danno = arma.danno;
        this.critChance = arma.critChance;
        this.random = arma.random;
        this.effetti = new ArrayList<>();
        if (arma.effetti != null) {
            this.effetti = arma.effetti.stream()
                    // Crea una copia indipendente di ogni effetto.
                    .map(Effetto::copy)
                    // Raccoglie le copie nella lista dell'arma.
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public void setRandom(RandomNumbersGenerator random) {
        if (random == null) throw new NullPointerException("Il generatore random non può essere nullo");
        this.random = random;
    }

    protected RandomNumbersGenerator getRandom() {
        if (random == null) {
            random = new RandomNumbersGenerator();
        }
        return random;
    }
    public abstract Arma copy();

    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public String getNome() {
        return nome;
    }


    public int getCaricatore() {
        return caricatore;
    }


    public int getMaxCaricatore() {
        return maxCaricatore;
    }


    public int getDanno() {
        return danno;
    }


    public abstract int calcolaDanno();

    public ArrayList<Effetto> getEffetti() {
        if (effetti == null) {
            effetti = new ArrayList<>();
        }
        return effetti;
    }

    public void addEffetto(Effetto effetto) {
        if (effetto == null) {
            throw new NullPointerException("L'effetto non può essere nullo");
        }
        effetti.add(effetto);
    }

    public void removeEffetto(Effetto effetto) {
        effetti.remove(effetto);
    }

    public ArrayList<EffectType> getEffectTypes() {
        if (effetti == null || effetti.isEmpty()) {
            return new ArrayList<>(java.util.Arrays.asList(EffectType.DAMAGE));
        }

        return effetti.stream()
                // Converte ogni effetto nel relativo tipo.
                .map(Effetto::getEffectType)
                // Elimina i tipi duplicati mantenendo il primo trovato.
                .distinct()
                // Raccoglie i risultati in una nuova ArrayList.
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

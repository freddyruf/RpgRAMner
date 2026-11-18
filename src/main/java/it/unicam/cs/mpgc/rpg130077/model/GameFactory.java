package it.unicam.cs.mpgc.rpg130077.model;

import it.unicam.cs.mpgc.rpg130077.model.Equipaggiamento.Arma;
import it.unicam.cs.mpgc.rpg130077.model.Hacks.Hack;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.SistemaCombattimento;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.CombattimentoATurni;
import it.unicam.cs.mpgc.rpg130077.model.Sistema.StatoBattaglia;
import it.unicam.cs.mpgc.rpg130077.model.Entita.Giocatore;
import it.unicam.cs.mpgc.rpg130077.model.Entita.NPC;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg130077.model.IA.StrategiaCombattimento;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Crea una nuova partita a partire dall'equipaggiamento del giocatore e dai cataloghi.
 * <p>
 * Se l'equipaggiamento del nemico non viene impostato, viene scelto casualmente dal catalogo:
 * una arma e fino a {@value #MAX_HACK_NEMICO} hack distinti.
 * Lo stato della battaglia viene fornito dal chiamante, così questa classe non dipende
 * da una specifica modalita di combattimento.
 * Tutti gli oggetti assegnati alle entita sono copie profonde.
 */
public class GameFactory {
    public static final int MAX_HACK_NEMICO = 4;

    private List<Arma> equipaggiamentoGiocatoreArmi;
    private List<Hack> equipaggiamentoGiocatoreHacks;
    private List<Arma> equipaggiamentoNemicoArmi = new ArrayList<>();
    private List<Hack> equipaggiamentoNemicoHacks = new ArrayList<>();
    private List<Arma> catalogoArmi = new ArrayList<>();
    private List<Hack> catalogoHack = new ArrayList<>();
    private NPC nemico;
    private StatoBattaglia statoBattaglia;
    private StrategiaCombattimento strategia;
    private RandomNumbersGenerator random = new RandomNumbersGenerator();

    public GameFactory(StatoBattaglia statoBattaglia) {
        this.statoBattaglia = Objects.requireNonNull(statoBattaglia,
                "Lo stato della battaglia non puo essere nullo");
    }

    public void creaNuovaPartitaSemplice(SistemaCombattimento sistemaCombattimento) {
        if (equipaggiamentoGiocatoreArmi == null || equipaggiamentoGiocatoreHacks == null) {
            throw new NullPointerException("L'equipaggiamento del giocatore non puo essere nullo");
        }
        if (equipaggiamentoGiocatoreArmi.isEmpty()) {
            throw new IllegalStateException("Armi giocatore insufficienti");
        }

        Giocatore giocatore = new Giocatore("Giocatore", 100, "", 10,
                copiaHacks(equipaggiamentoGiocatoreHacks), equipaggiamentoGiocatoreArmi.get(0).copy(), true);
        NPC avversario = nemico != null ? (NPC) nemico.copy() : creaNemico();

        StatoBattaglia stato = statoBattaglia != null
                ? statoBattaglia
                : statoBattaglia;
        if (stato == null) {
            throw new IllegalStateException("Lo stato della battaglia deve essere configurato");
        }
        stato.setEroe(giocatore);
        stato.setNemico(avversario);
        sistemaCombattimento.setStato(stato);
    }

    private NPC creaNemico() {
        //Scelgo le hack
        ArrayList<Hack> hacksNemico=null;
        if(equipaggiamentoNemicoHacks.isEmpty()){
            hacksNemico=scegliHackCasuali();
        }
        else{
            hacksNemico=copiaHacks(equipaggiamentoNemicoHacks);
        }

        //Scelgo le armi
        Arma armaNemico;
        if (!equipaggiamentoNemicoArmi.isEmpty()) {
            armaNemico = equipaggiamentoNemicoArmi.get(0).copy();
        } else if (!catalogoArmi.isEmpty()) {
            armaNemico = catalogoArmi.get(random.getInt(0, catalogoArmi.size())).copy();
        } else {
            throw new IllegalStateException("Catalogo armi insufficiente per creare il nemico");
        }

        StrategiaCombattimento s = strategia != null ? strategia : new StrategiaCasuale();
        return new NPC("Cybermorb", 100, "", 5, hacksNemico, armaNemico, 5, 0.1, s, false);
    }

    /** Sceglie fino a MAX_HACK_NEMICO hack distinti dal catalogo, senza ripetizioni. */
    private ArrayList<Hack> scegliHackCasuali() {
        List<Hack> disponibili = new ArrayList<>(catalogoHack);
        ArrayList<Hack> scelti = new ArrayList<>();
        int quanti = Math.min(MAX_HACK_NEMICO, disponibili.size());
        for (int i = 0; i < quanti; i++) {
            scelti.add(disponibili.remove(random.getInt(0, disponibili.size())).copy());
        }
        return scelti;
    }

    private static ArrayList<Hack> copiaHacks(List<Hack> hacks) {
        return hacks.stream()
                // Crea una copia indipendente di ogni hack.
                .map(Hack::copy)
                // Raccoglie le copie in una nuova ArrayList.
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setEquipaggiamentoGiocatoreArmi(List<Arma> equipaggiamentoGiocatoreArmi) {
        this.equipaggiamentoGiocatoreArmi = equipaggiamentoGiocatoreArmi;
    }
    public void setEquipaggiamentoGiocatoreHacks(List<Hack> equipaggiamentoGiocatoreHacks) {
        this.equipaggiamentoGiocatoreHacks = equipaggiamentoGiocatoreHacks;
    }
    public void setEquipaggiamentoNemicoArmi(List<Arma> equipaggiamentoNemicoArmi) {
        this.equipaggiamentoNemicoArmi = equipaggiamentoNemicoArmi != null ? equipaggiamentoNemicoArmi : new ArrayList<>();
    }
    public void setEquipaggiamentoNemicoHacks(List<Hack> equipaggiamentoNemicoHacks) {
        this.equipaggiamentoNemicoHacks = equipaggiamentoNemicoHacks != null ? equipaggiamentoNemicoHacks : new ArrayList<>();
    }
    public void setCatalogoArmi(List<Arma> catalogoArmi) {
        this.catalogoArmi = catalogoArmi != null ? catalogoArmi : new ArrayList<>();
    }
    public void setCatalogoHack(List<Hack> catalogoHack) {
        this.catalogoHack = catalogoHack != null ? catalogoHack : new ArrayList<>();
    }
    public void setStrategia(StrategiaCombattimento strategia) {
        this.strategia = strategia;
    }
    public void setNemico(NPC nemico) {
        this.nemico = nemico;
    }
    public void setRandom(RandomNumbersGenerator random) {
        this.random = random;
    }
}

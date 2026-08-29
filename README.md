# RpgRAMner

RpgRAMner è un RPG a turni sviluppato in Java con JavaFX dove il combattimento ruota attorno a una meccanica basata sulla RAM.
Il progetto è stato realizzato per il corso di Metodologie di Programmazione e Modellazione e Gestione della Conoscenza (UNICAM, AA 2025/26).

## Prerequisiti
- Java 25 (LTS)
- Gradle

## Come eseguire

```
git clone https://github.com/<tuo-username>/RpgRAMner.git
cd RpgRAMner
```

### Build
```
./gradlew build
```

### Avvio
```
./gradlew run
```

### Test
```
./gradlew test
```

## Funzionalità
- Combattimento a turni 1v1
- Sistema di hacking basato sulla RAM (gli hack vengono caricati nella RAM come una coda e si attivano dopo un certo numero di tick)
- Scelta dell'armamento prima della battaglia (armi e hack)
- IA nemica con strategia di combattimento
- Attacco a sorpresa degli NPC
- Persistenza dei dati in formato JSON
- Interfaccia grafica con JavaFX (3 schermate: iniziale, scelta armamento, battaglia)
- Musica di sottofondo

## Struttura del progetto

```
it.unicam.cs.mpgc.rpg130077
├── controller
│   ├── UI          # Controller delle schermate JavaFX
│   └── logica      # Gestione armamento e musica
├── model
│   ├── Azioni      # Comandi di combattimento (sparo, carica hack)
│   ├── Effetti     # Effetti degli hack (danno, cura, reverse, sort)
│   ├── Entita      # Giocatore, NPC e relative interfacce
│   ├── Equipaggiamento  # Armi (pistola, mitragliatrice)
│   ├── Hacks       # Hack e coda degli hack
│   ├── IA          # Strategie di combattimento per gli NPC
│   └── Sistema     # Cuore del gioco: turni, clock, stato battaglia
└── persistenza     # Salvataggio/caricamento dati JSON
```

## 🤖 Uso di strumenti di AI

Durante lo sviluppo ho usato diversi strumenti di AI, con ruoli diversi a seconda del punto del progetto:

- **GitHub Copilot** (integrato nell'IDE) per l'autocompletamento e per velocizzare la scrittura del codice
- **Gemini 3.1**, **Claude Opus 4.7** e **Antigravity** come assistenti per discutere le scelte architetturali, applicare i principi SOLID, fare debugging e capire meglio alcuni pattern di design

L'uso più intenso è concentrato in punti precisi: **tutti i file di test** sono stati scritti con l'aiuto dell'AI, e nel codice di produzione due classi del package `persistenza` (`PolymorphicAdapter` e `GsonProvider`) hanno richiesto un supporto più consistente per via della complessità della serializzazione polimorfica — è dichiarato direttamente nel Javadoc di quelle classi. Per il resto del codice l'AI è stata usata soprattutto come assistente di scrittura e come confronto per rispettare i principi SOLID.

Ogni suggerimento è stato letto, capito e verificato prima di essere integrato. Le decisioni progettuali sono state prese da me.

📌 Per la dichiarazione dettagliata, vedere la pagina [Uso dell'AI](../../wiki/Uso-AI) della Wiki.

## Documentazione

La documentazione completa del progetto (architettura, classi, pattern, persistenza, estendibilità) si trova nella **[Wiki](../../wiki)** di questo repository.

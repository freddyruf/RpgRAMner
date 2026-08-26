package it.unicam.cs.mpgc.rpg130077.controller.logica;

import javazoom.jl.player.Player;
import java.io.InputStream;

/**
 * Classe che si occupa di gestire la musica
 */
public class GestoreMusica {

    private Thread playerThread;
    private boolean isPlaying = false;

    /**
     * Avvia una sola canzone in loop
     */
    public void avviaMusicaSemplice() {
        String resourcePath= "/Nightdrive VHS Dreams.mp3";
        if (isPlaying) return;
        isPlaying = true;
        playerThread = new Thread(() -> {
            while (isPlaying) {
                try {
                    InputStream is = getClass().getResourceAsStream(resourcePath);
                    if (is != null) {
                        Player player = new Player(is);
                        player.play();
                    } else {
                        System.err.println("File audio non trovato: " + resourcePath);
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Errore durante la riproduzione audio: " + e.getMessage());
                    break;
                }
            }
        });
        playerThread.setDaemon(true);
        playerThread.start();
    }

    //No MUSIC!!!!
    public void stop() {
        isPlaying = false;
        if (playerThread != null) {
            playerThread.interrupt();
        }
    }
}

package it.unicam.cs.mpgc.rpg130077.controller.logica;

import javazoom.jl.player.Player;
import java.io.InputStream;

/**
 * Gestore per la riproduzione audio in streaming concorrente.
 */
public class GestoreMusica {

    private Thread playerThread;
    private volatile boolean isPlaying = false;
    private Player currentPlayer;

    public synchronized void avviaMusicaSemplice() {
        String resourcePath = "/Nightdrive VHS Dreams.mp3";
        if (isPlaying) return;
        isPlaying = true;

        playerThread = new Thread(() -> {
            while (isPlaying) {
                try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                    if (is != null) {
                        synchronized (this) {
                            if (!isPlaying) break;
                            currentPlayer = new Player(is);
                        }
                        currentPlayer.play();
                    } else {
                        System.err.println("File audio non trovato: " + resourcePath);
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        }, "BackgroundAudio-Thread");

        playerThread.setDaemon(true);
        playerThread.start();
    }

    public synchronized void stop() {
        isPlaying = false;
        if (currentPlayer != null) {
            try {
                currentPlayer.close();
            } catch (Exception ignored) {}
            currentPlayer = null;
        }
        if (playerThread != null) {
            playerThread.interrupt();
            playerThread = null;
        }
    }
}
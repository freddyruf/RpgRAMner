package it.unicam.cs.mpgc.rpg130077.model.Sistema;

public class Clock {
    private Thread threadTimer;
    private volatile boolean inEsecuzione;
    private final Runnable runnable;

    public Clock(Runnable onTick) {
        this.runnable = onTick;
        this.inEsecuzione = false;
    }

    public void start() {
        if (inEsecuzione) {
            return;
        }

        inEsecuzione = true;

        threadTimer = new Thread(() -> {
            while (inEsecuzione) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Se il thread viene fermato mentre dorme, esce dal ciclo
                    break;
                }

                if (inEsecuzione) {
                    runnable.run();
                }
            }
        });

        threadTimer.setDaemon(true);
        threadTimer.start();
    }

    public void stop() {
        inEsecuzione = false;
        if (threadTimer != null) {
            threadTimer.interrupt();
            threadTimer = null;
        }
    }
}
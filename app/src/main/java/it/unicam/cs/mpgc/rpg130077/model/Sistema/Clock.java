package it.unicam.cs.mpgc.rpg130077.model.Sistema;
import java.util.Timer;
import java.util.TimerTask;

public class Clock {
    Timer timer;
    private final Runnable runnable;

    public Clock(Runnable onTick) {
        timer = new Timer();
        this.runnable = onTick;

    }
    public void start(){
        //Creo una task che viene eseguita ogni 1s(o 1000ms)
        timer.scheduleAtFixedRate(new TimerTask() {
            //Questo viene eseguito ogni volta che viene chiamata la task
            @Override
            public void run() {
                runnable.run();
            }
        }, 1000, 1000);
    }
    public void stop(){
        timer.cancel();
    }
}

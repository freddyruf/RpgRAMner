package it.unicam.cs.mpgc.rpg130077.model.Sistema;
import java.util.Timer;
import java.util.TimerTask;

public class Clock {
    Timer timer;

    public Clock(Runnable onTick) {
        timer = new Timer();
        //Creo una task che viene eseguita ogni 1s(o 1000ms)
        timer.scheduleAtFixedRate(new TimerTask() {
            //Questo viene eseguito ogni volta che viene chiamata la task
            @Override
            public void run() {
                onTick.run();
            }
        }, 1000, 1000);
    }
}

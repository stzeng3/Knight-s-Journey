package tomn114.com.game;

import android.os.Handler;

public class Stopwatch implements Runnable{

    private long startTime = 0;
    private int minutes;
    private int seconds;
    private String timeStr;
    private Handler timerHandler;

    public Stopwatch(){
        timerHandler = new Handler();
    }

    @Override
    public void run() {
        long millis = System.currentTimeMillis() - startTime;
        seconds = (int) (millis/1000);
        minutes = seconds / 60;
        seconds = seconds % 60;
        if(seconds < 10)
            timeStr = minutes + ":0" + seconds;
        else
            timeStr = minutes + ":" + seconds;
        timerHandler.postDelayed(this, 500);
    }

    public void startTimer(){
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(this, 0);
    }

    public void stopTimer(){
        timerHandler.removeCallbacks(this);
    }

    public void resetTimer(){
        minutes = 0;
        seconds = 0;
    }

    public int getSeconds(){
        return seconds;
    }
    public int getMinutes(){
        return minutes;
    }
    public String getTimeStr(){ return timeStr; }
}

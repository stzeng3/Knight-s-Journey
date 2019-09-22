package tomn114.com.game;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class Stopwatch implements Runnable{

    private long startTime;
    private long millisElapsed;
    private long millis;
    private int minutes;
    private int seconds;
    private long pauseTime;
    private String timeStr;
    private Handler timerHandler;
    private boolean stopped;
    //private double test = Math.random() * 100;

    public Stopwatch(){
        timerHandler = new Handler();
    }

    @Override
    public void run() {
        //Log.d(Stopwatch.class.getSimpleName(), "Pause time: " + pauseTime + " , Milliselapsed: " + millisElapsed + ", Millis: " + millis);
        millisElapsed = System.currentTimeMillis() - startTime;
        millis = pauseTime + millisElapsed;
        seconds = (int) (millis/1000);
        minutes = seconds / 60;
        seconds = seconds % 60;
        if(seconds < 10)
            timeStr = minutes + ":0" + seconds;
        else
            timeStr = minutes + ":" + seconds;
        timerHandler.postDelayed(this, 0);
    }

    public void startTimer(){
        stopped = false;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(this, 0);
    }

    public void stopTimer(){
        if(!stopped) {
            stopped = true;
            pauseTime = millis;
            timerHandler.removeCallbacks(this);
        }
    }

    public void resetTimer(){
        millisElapsed = 0;
        millis = 0;
        seconds = 0;
        minutes = 0;
        startTime = 0;
        pauseTime = 0;
    }

    public long getMillis(){ return millis; }
    public int getSeconds(){ return seconds; }
    public int getMinutes(){ return minutes; }
    public String getTimeStr(){ return timeStr; }
    public void setTimeStr(String timeStr){ this.timeStr = timeStr; }
    public long getStartTime(){ return startTime; }
    public long getPauseTime(){ return pauseTime; }
    public void setPauseTime(long pauseTime){ this.pauseTime = pauseTime; }
}

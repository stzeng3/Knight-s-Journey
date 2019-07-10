package tomn114.com.game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity extends Activity {
    GamePanel gp;
    boolean wasStopped;
    long pausedTime = 0;
    int savedKnightX;
    int savedKnightY;
    int[] savedMoves;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wasStopped = false;

        //Turns title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(gp = new GamePanel(this));
    }
    protected void onStop(){
        //gp.pause();
        super.onStop();
        wasStopped = true;
        if(gp.getLevelMoves() != null)
            savedMoves = gp.getLevelMoves();
        if(!gp.isDoneWithLevel()) {
            pausedTime = gp.getStopwatch().getPauseTime();
            savedKnightX = gp.getKnight().getRow();
            savedKnightY = gp.getKnight().getCol();
        }
        //Log.d("GameActivity", "os, pt: " + pausedTime);
        //User exits
    }

    protected void onPause(){
        //gp.pause();
        super.onPause();
    }

    protected void onStart(){
        super.onStart();
        //User goes back
    }

    protected void onResume(){
        super.onResume();

        if(wasStopped && gp.isPaused() == false){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if(wasStopped){
            gp.loadMoves(savedMoves);
        }
        if(gp.isPaused() && !gp.isDoneWithLevel()){
            gp.setStopwatchPauseTime(pausedTime);
            gp.loadKnightXY(savedKnightX, savedKnightY);
        }
    }

    public void onBackPressed(){
        gp.pause();
        //super.onBackPressed();
    }

    public long getPausedTime(){ return pausedTime; }

}

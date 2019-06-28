package tomn114.com.game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity extends Activity {
    GamePanel gp;
    boolean wasStopped;

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
        super.onStop();
        wasStopped = true;
        //User exits
    }

    protected void onStart(){
        super.onStart();
        //User goes back
    }

    protected void onResume(){
        super.onResume();
        if(wasStopped){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}

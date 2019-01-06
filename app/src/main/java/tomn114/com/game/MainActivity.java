package tomn114.com.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private boolean isReady = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Turns title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(new GamePanel(this));
        setContentView(R.layout.activity_main);
    }

    public void play(View view){
        isReady = true;
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }

    public void instructions(View view){
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
    }

    public void onStop(){
        super.onStop();
        if(isReady) {
            makeBoard();
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }
    }

    public void makeBoard(){
        BoardMaker bm = new BoardMaker();
    }
}

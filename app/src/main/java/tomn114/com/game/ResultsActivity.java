package tomn114.com.game;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Turns title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_results);

        TextView text2 = (TextView) findViewById(R.id.pMoves);
        text2.setText("Your Total Moves: "+GamePanel.totalMoves);

        TextView text3 = (TextView) findViewById(R.id.mMoves);
        text3.setText("Minimum Moves: "+GamePanel.minTotal);

        TextView text4 = (TextView) findViewById(R.id.pTime);
        text4.setText("Your Total Time: "+GamePanel.totalTime);

        TextView text5 = (TextView) findViewById(R.id.mTime);
        int time = 2*GamePanel.minTotal;
        text5.setText("Expected Time: "+time);

        TextView text6 = (TextView) findViewById(R.id.Score);
        int score=0;
        if(GamePanel.totalMoves > GamePanel.minTotal);
            score = 100 - (GamePanel.totalMoves-GamePanel.minTotal);
        if(GamePanel.totalTime-time>0)
            score -= (GamePanel.totalTime-time);
        if(score<0)
            score = 0;
        text6.setText("Final Score: "+score);
        GamePanel.minTotal=0;
    }

    public void restart(View view){
        GamePanel.totalTime = 0;
        GamePanel.totalMoves = 0;

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

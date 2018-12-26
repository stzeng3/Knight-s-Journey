package tomn114.com.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread thread;
    public static final int WIDTH = 480;
    public static final int HEIGHT = 800;

    public GamePanel(Context context){
        super(context);

        //Intercept Events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //Focusable to handle events
        setFocusable(true);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry){
            try{
                thread.setRunning(false);
                thread.join();
            }catch(InterruptedException e){ e.printStackTrace(); }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return false;
    }

    public void update(){

    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        final float scaleX = getWidth() / WIDTH;
        final float scaleY = getHeight() / HEIGHT;

        if(canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleX, scaleY);
            canvas.restoreToCount(savedState);
        }

    }
}

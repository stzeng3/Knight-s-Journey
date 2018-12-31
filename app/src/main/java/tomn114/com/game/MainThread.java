package tomn114.com.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread{

    private boolean running;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private static Canvas canvas;
    private static final int MAX_FPS = 30;
    private static final int MAX_FRAME_SKIPS = 5;
    //private double averageFPS;

    public MainThread(SurfaceHolder sh, GamePanel gp){
        super();
        surfaceHolder = sh;
        gamePanel = gp;
    }

    @Override
    //The game loop
    public void run(){
        long startTime; // In millis
        long elapsed; // In millis
        long wait; // In millis
        long totalTime = 0;
        int frames = 0;
        int framesSkipped = 0;
        long targetTime = 1000/MAX_FPS; // In millis

        while(running){
            startTime = System.currentTimeMillis();
            framesSkipped = 0;

            //Updating and rendering portion of game loop
            try{
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    gamePanel.update();
                    gamePanel.draw(canvas);
                }
            }catch(Exception e){ e.printStackTrace(); }
            //Unlock canvas again
            finally{
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch(Exception e){ e.printStackTrace(); }
                }
            }

            /*Measures the time it took to update and render, so it can either have the
            thread sleep to match the target fps of 30 or it can catch up if it's behind*/
            elapsed = System.currentTimeMillis() - startTime;
            wait = targetTime - elapsed;

            if(wait > 0) {
                try {
                    this.sleep(wait);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            while(wait < 0 && framesSkipped < MAX_FRAME_SKIPS){
                gamePanel.update();
                wait += targetTime;
                framesSkipped++;
            }

            totalTime += System.currentTimeMillis() - startTime;
            frames++;

            //Measures the fps
            /*
            if(frames == MAX_FPS){
                averageFPS = 1000/(totalTime/frames);
                frames = 0;
                totalTime = 0;
                //System.out.println(averageFPS);
            }*/
        }
    }

    public void setRunning(boolean b){
        running = b;
    }
}

package tomn114.com.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    public static final int NUM_OF_LEVELS = 3;
    public static final int DEFAULT_WIDTH = 1080;
    public static final int DEFAULT_HEIGHT = 1920;

    public static final int DEFAULT_TEXT_SIZE = 80;

    public static int tileSize;
    public static int boardOffset;
    public static int totalTime = 0, totalMoves = 0, minTotal = 0;
    public static int[] levelTimes, levelMoves;
    public static int[] storeSX = new int[NUM_OF_LEVELS*2];
    public static int[] storeSY = new int[NUM_OF_LEVELS*2];
    public static int[] storeEX = new int[NUM_OF_LEVELS*2];
    public static int[] storeEY = new int[NUM_OF_LEVELS*2];

    private MainThread thread;
    private boolean[][] board;
    private boolean[][][] allBoards;

    private int lvlCounter=0;
    private int displayC=0;
    private int startX, startY, endX, endY;
    private int boardLength = 5;
    private int boardWidth = 5;
    private Knight knight;
    private boolean doneWithLevel = false;
    private Paint white, black;
    private ClickableText nextLevel, youWon, resumeGame, exitToMenu, tempPause;

    private Bitmap tile, river, castle;
    private Stopwatch s;
    private long stopwatchPauseTime = 0;

    private boolean paused;

    private int loadedKnightX = -1, loadedKnightY = -1;
    private int[] loadedMoves = null;

    public GamePanel(Context context){
        super(context);
        //Intercept Events
        getHolder().addCallback(this);
        getHolder().setFixedSize(getWidth(), getHeight());
        //Focusable to handle events

        levelTimes = new int[NUM_OF_LEVELS];
        allBoards = BoardMaker.allBoards;
        board = allBoards[lvlCounter];

        setFocusable(true);
    }

    public Stopwatch getStopwatch(){
        return s;
    }
    /*
    public void deleteStopwatch(){
        s = null;
    }
    */
    public int getPhoneWidth(){ return getWidth(); }
    public int getPhoneHeight(){ return getHeight(); }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Log.d("GamePanel", "Surface Created, Min total: " + minTotal);
        startX = storeSX[lvlCounter];
        startY = storeSY[lvlCounter];
        endX = storeEX[lvlCounter];
        endY = storeEY[lvlCounter];

        tileSize = getWidth()/boardWidth;
        boardOffset = (getHeight() - tileSize*boardLength) / 2;

        white = new Paint();
        white.setColor(Color.WHITE);

        black = new Paint();
        black.setColor(Color.BLACK);

        BoardUtilities.createRects(boardLength, boardWidth);
        tile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tile), tileSize, tileSize, false);
        river = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.river2), tileSize, tileSize, false);
        castle = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.castle), tileSize, tileSize, false);
        nextLevel = new ClickableText(this,"Next Level", getWidth() * 9 / 16, getHeight() - boardOffset/2, black);
        youWon = new ClickableText(this, "Results", getWidth() * 9 / 16 , getHeight() - boardOffset/2, black);
        resumeGame = new ClickableText(this, "Resume", getWidth() / 2, 100, black);
        exitToMenu = new ClickableText(this, "Exit to main menu", getWidth() / 2, 360, black);
        tempPause = new ClickableText(this, "||", getWidth() * 13/16, boardOffset/2, black);

        if(paused){
            exitToMenu.setVisible(true);
            resumeGame.setVisible(true);
        }
        else {
            tempPause.setVisible(true);
        }

        totalMoves = 0;
        totalTime = 0;


        if(loadedMoves == null)
            levelMoves = new int[NUM_OF_LEVELS];
        else
            levelMoves = loadedMoves;
        knight = new Knight(startX, startY, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.right), tileSize, tileSize, false), getContext());

        s = new Stopwatch();

        if(!isDoneWithLevel())
            s.startTimer();

        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry){
            try{
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            }catch(InterruptedException e){ e.printStackTrace(); }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            return true;
        }

        if(event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if(paused){
                if(resumeGame.clicked(x,y)){
                    resume();
                }
                else if(exitToMenu.clicked(x,y)){
                    paused = false;
                    Intent intent = new Intent();
                    intent.setClass(this.getContext(), MainActivity.class);
                    this.getContext().startActivity(intent);
                }
            }
            else {
                if (!doneWithLevel) {
                    int[] rowColClicked = BoardUtilities.whichTileClicked(x, y);

                    if (rowColClicked == null) {
                        //Clicked outside board
                    } else if (board[rowColClicked[0]][rowColClicked[1]] == false) {
                        Toast.makeText(getContext(), "You can't go to a river!", Toast.LENGTH_SHORT
                        ).show();
                    } else if (board[rowColClicked[0]][rowColClicked[1]]) {
                        knight.move(rowColClicked[0], rowColClicked[1], lvlCounter);
                        checkWin();
                    }
                } else {
                    if (youWon.clicked(x, y)) {
                        //getHolder().unlockCanvasAndPost(c);
                        //Test
                        for (int i = 0; i < NUM_OF_LEVELS; i++) {
                            totalTime += levelTimes[i];
                        }

                        for (int i = 0; i < NUM_OF_LEVELS; i++){
                            totalMoves += levelMoves[i];
                        }

                        Intent intent = new Intent();
                        intent.setClass(this.getContext(), ResultsActivity.class);
                        this.getContext().startActivity(intent);
                    }
                    if (nextLevel.clicked(x, y)) {
                        //New level
                        resetLevel();
                    }
                }

                if(tempPause.clicked(x,y))
                    pause();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        if(canvas != null && paused != true) {
            //Draw stuff here
            canvas.drawRect(0, 0, getWidth(), getHeight(), white);
            canvas.drawText("Level: " + (displayC+1), getWidth()/8, getHeight() - boardOffset/2, black);
            canvas.drawText("Moves: " + (levelMoves[displayC]), getWidth()/8, boardOffset/2, black);
            canvas.drawText("Time: " + s.getTimeStr(), (getWidth()*9)/16, boardOffset/2, black);
            drawBoard(canvas);
            knight.draw(canvas);
            nextLevel.draw(canvas);
            youWon.draw(canvas);
            tempPause.draw(canvas);
        }
        if(paused){
            canvas.drawRect(0, 0, getWidth(), getHeight(), white);
            canvas.drawText("PAUSED", 300, 500, black);
            resumeGame.draw(canvas);
            exitToMenu.draw(canvas);
        }
    }

    public void drawBoard(Canvas canvas){
        int x = 0;
        int y = boardOffset;

        for(int i = 0; i<boardLength; i++){
            for(int j = 0; j<boardWidth; j++){
                if(board[i][j])
                    canvas.drawBitmap(tile, x, y, null);
                else
                    canvas.drawBitmap(river, x, y, null);

                if(i == endX && j == endY && !doneWithLevel)
                    canvas.drawBitmap(castle, x, y, null);

                x += tileSize;
            }
            y += tileSize;
            x = 0;
        }
    }

    public void checkWin(){
        if(knight.getRow() == endX && knight.getCol() == endY){
            //totalMoves += levelMoves[lvlCounter];
            levelTimes[lvlCounter] = s.getMinutes()*60 + s.getSeconds();
            doneWithLevel = true;
            s.stopTimer();
            s.resetTimer();
            lvlCounter++;
            if(lvlCounter == NUM_OF_LEVELS)
                youWon.setVisible(true);
            else{
                nextLevel.setVisible(true);
            }
        }
    }

    public void resetLevel() {

        displayC++;
        startX = storeSX[lvlCounter];
        startY = storeSY[lvlCounter];
        endX = storeEX[lvlCounter];
        endY = storeEY[lvlCounter];
        board = allBoards[lvlCounter];
        nextLevel.setVisible(false);
        doneWithLevel = false;
        knight.setRow(startX);
        knight.setCol(startY);
        s.startTimer();
    }

    public void update(){
        //Future animations go here
    }

    public void pause(){

        paused = true;
        if(!doneWithLevel) {
            s.stopTimer();
        }
        resumeGame.setVisible(true);
        exitToMenu.setVisible(true);
    }

    public void resume(){
        paused = false;

        //System.out.println("Isdonewithlevel: " + doneWithLevel);

        if(doneWithLevel){
            int timeBeforeExited = levelTimes[lvlCounter - 1];
            //System.out.println("Levelmovesbeforeexited: " + levelMoves[lvlCounter - 1]);
            //System.out.println("timebeforeexited: " + timeBeforeExited );
            int minutesBeforeExited = timeBeforeExited / 60;
            int secondsBeforeExited = timeBeforeExited % 60;

            if(secondsBeforeExited < 10)
                s.setTimeStr(minutesBeforeExited + ":0" + secondsBeforeExited);
            else
                s.setTimeStr(minutesBeforeExited + ":" + secondsBeforeExited);

            knight.setRow(storeEX[lvlCounter - 1]);
            knight.setCol(storeEY[lvlCounter - 1]);
            if(lvlCounter == NUM_OF_LEVELS)
                youWon.setVisible(true);
            else
                nextLevel.setVisible(true);
        }

        if(!doneWithLevel) {
            if(stopwatchPauseTime != 0) {
                s.setPauseTime(stopwatchPauseTime);
                stopwatchPauseTime = 0;
            }
            if(loadedKnightX >= 0 && loadedKnightY >= 0){
                knight.setRow(loadedKnightX);
                knight.setCol(loadedKnightY);
            }

            s.startTimer();
        }
        resumeGame.setVisible(false);
        exitToMenu.setVisible(false);
        tempPause.setVisible(true);
    }

    public boolean isPaused(){return paused;}
    public boolean isDoneWithLevel(){return doneWithLevel;}
    public void setStopwatchPauseTime(long stopwatchPauseTime){ this.stopwatchPauseTime = stopwatchPauseTime; }
    public Knight getKnight(){ return knight; }
    public void loadKnightXY(int knightX, int knightY){
        loadedKnightX = knightX;
        loadedKnightY = knightY;
    }
    public int[] getLevelMoves(){ return levelMoves; }
    public void loadMoves(int[] moves){ loadedMoves = moves; }
}
package tomn114.com.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    public static final int NUM_OF_LEVELS = 7;
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

    private long millisWhenPaused = 0;
    private int lvlCounter=0;
    private int displayC=0;
    private int startX, startY, endX, endY;
    private int boardLength = 5;
    private int boardWidth = 5;
    private Knight knight;
    private boolean doneWithLevel = false;
    private Paint white, black;
    private ClickableText nextLevel, youWon;

    private Bitmap tile, river, castle;
    private Stopwatch s;

    public GamePanel(Context context){
        super(context);

        //Intercept Events
        getHolder().addCallback(this);
        //Focusable to handle events

        levelTimes = new int[NUM_OF_LEVELS];
        allBoards = BoardMaker.allBoards;
        board = allBoards[lvlCounter];

        setFocusable(true);
    }

    public Stopwatch getStopwatch(){
        return s;
    }
    public void deleteStopwatch(){
        s = null;
    }
    public void setMillisWhenPaused(long millisWhenPaused){
        this.millisWhenPaused = millisWhenPaused;
    }

    public int getPhoneWidth(){ return getWidth(); }
    public int getPhoneHeight(){ return getHeight(); }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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

        levelMoves = new int[NUM_OF_LEVELS];

        knight = new Knight(startX, startY, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.right), tileSize, tileSize, false), getContext());

        s = new Stopwatch(millisWhenPaused);
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

            if(!doneWithLevel) {
                int[] rowColClicked = BoardUtilities.whichTileClicked(x, y);

                if(rowColClicked == null) {
                    //Clicked outside board
                }
                else if (board[rowColClicked[0]][rowColClicked[1]] == false) {
                    Toast.makeText(getContext(), "You can't go to a river!", Toast.LENGTH_SHORT
                    ).show();
                }
                else if (board[rowColClicked[0]][rowColClicked[1]]) {
                    knight.move(rowColClicked[0], rowColClicked[1],lvlCounter);
                    checkWin();
                }
            }
            else {
                if(youWon.clicked(x,y)){
                    //getHolder().unlockCanvasAndPost(c);
                    //Test
                    for(int i = 0; i<NUM_OF_LEVELS; i++){
                        totalTime += levelTimes[i];
                    }
                    Intent intent = new Intent();
                    intent.setClass(this.getContext(), ResultsActivity.class);
                    this.getContext().startActivity(intent);
                }
                if(nextLevel.clicked(x, y)){
                    //New level
                    resetLevel();
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        if(canvas != null) {
            //Draw stuff here
            canvas.drawRect(0, 0, getWidth(), getHeight(), white);
            canvas.drawText("Level: " + (displayC+1), getWidth()/8, getHeight() - boardOffset/2, black);
            canvas.drawText("Moves: " + (levelMoves[displayC]), getWidth()/8, boardOffset/2, black);
            canvas.drawText("Time: " + s.getTimeStr(), (getWidth()*9)/16, boardOffset/2, black);
            drawBoard(canvas);
            knight.draw(canvas);
            nextLevel.draw(canvas);
            youWon.draw(canvas);
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
            totalMoves += levelMoves[lvlCounter];
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

    }
}
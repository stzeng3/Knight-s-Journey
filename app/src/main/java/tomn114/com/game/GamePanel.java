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

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    public static final int NUM_OF_LEVELS = 2;
    public static int tileSize;
    public static int boardOffset;
    public static int totalTime = 0, totalMoves = 0, minTotal = 0;
    public static int[] levelTimes, levelMoves;

    private MainThread thread;
    private boolean[][] board;
    private boolean[][][] allBoards;

    private int lvlCounter=0;
    private int displayC=0;
    private int startX = 0, startY = 0;
    private int endX = 4, endY = 4;
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
        setFocusable(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        tileSize = getWidth()/boardWidth;
        boardOffset = (getHeight() - tileSize*boardLength) / 2;

        white = new Paint();
        white.setColor(Color.WHITE);

        black = new Paint();
        black.setColor(Color.BLACK);

        board = new boolean[boardLength][boardWidth];

        BoardUtilities.createRects(boardLength, boardWidth);
        tile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tile), tileSize, tileSize, false);
        river = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.river2), tileSize, tileSize, false);
        castle = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.castle), tileSize, tileSize, false);
        nextLevel = new ClickableText("Next Level", getWidth() * 4 / 6, getHeight() - boardOffset/2, black);
        youWon = new ClickableText("Go to Results", getWidth() * 4 / 6 , getHeight() - boardOffset/2, black);

        allBoards = BoardMaker.allBoards;
        board = allBoards[lvlCounter];
        levelTimes = new int[NUM_OF_LEVELS];
        levelMoves = new int[NUM_OF_LEVELS];

        knight = new Knight(startX, startY, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.right), tileSize, tileSize, false));
        s = new Stopwatch();
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

                if (rowColClicked == null) {
                    System.out.println("Clicked outside board");
                }
                else if (board[rowColClicked[0]][rowColClicked[1]] == false) {
                    System.out.println("Clicked river");
                }
                else if (board[rowColClicked[0]][rowColClicked[1]]) {
                    knight.move(rowColClicked[0], rowColClicked[1]);
                    levelMoves[lvlCounter]++;
                    checkWin();
                }
            }
            else {
                if(youWon.clicked(x,y)){
                    //getHolder().unlockCanvasAndPost(c);
                    //Test
                    for(int i = 0; i<NUM_OF_LEVELS; i++){
                        totalTime += levelTimes[i];
                        totalMoves += levelMoves[i];
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
            canvas.drawText("Level: " + (displayC+1), getWidth()*1/6, getHeight() - boardOffset/2, black);
            canvas.drawText("Moves: " + (levelMoves[displayC]), getWidth()*1/6, boardOffset/2, black);
            canvas.drawText("Time: " + s.getTimeStr(), getWidth()*4/6, boardOffset/2, black);
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
        board = allBoards[lvlCounter];
        nextLevel.setVisible(false);
        doneWithLevel = false;
        knight.setRow(0);
        knight.setCol(0);
        s.startTimer();
    }

    public String prepareResults(){
        String results = "";
        results += "Your total moves =";
        return results;
    }

    public void update(){

    }
}
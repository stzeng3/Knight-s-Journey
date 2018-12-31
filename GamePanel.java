package tomn114.com.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread thread;
    private boolean[][] board;
    private boolean[][][] allBoards;

    private int barrierCounter, counter = 0, runCounter = 0, lvlCounter=0; // How many times makeIt() runs
    private int barrierNum = 15;
    private int difficulty = 2;
    private int startX, startY;
    private int endX, endY;
    private int minMoves;
    private int moves;
    private int currX, currY;
    private int boardLength = 5;
    private int boardWidth = 5;
    private int temp;
    private Knight knight;
    private boolean doneWithLevel = false;
    private Paint white, black;
    private BlinkingText nextLevel, youWon;
    public static final int NUM_OF_LEVELS = 8;
    public static int tileSize;
    public static int boardOffset;

    private Bitmap tile, river, castle;

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
        allBoards = new boolean[NUM_OF_LEVELS][boardLength][boardWidth];

        BoardUtilities.createRects(boardLength, boardWidth);
        tile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tile), tileSize, tileSize, false);
        river = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.river2), tileSize, tileSize, false);
        castle = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.castle), tileSize, tileSize, false);
        nextLevel = new BlinkingText("Next Level", getWidth() / 4, getHeight() - boardOffset/2, black);
        youWon = new BlinkingText("You Won!", getWidth() / 4, getHeight() - boardOffset/2, black);
        for(int i = 0;i<NUM_OF_LEVELS;i++){
            makeIt();
        }
        board = allBoards[lvlCounter];
        knight = new Knight(startX, startY, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.right), tileSize, tileSize, false));

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
                    checkWin();
                }
            }
            else {
                if(nextLevel.clicked(x, y)){
                    //New level
                    resetLevel();
                }

            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void update(){

    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        if(canvas != null) {
            //Draw stuff here
            canvas.drawRect(0, 0, getWidth(), getHeight(), white);
            canvas.drawText("Level: " + lvlCounter, 50, 50, black);
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

    //Creates the board by calling makeBoard
    public void makeIt(){
        runCounter++;
        //Generates random start and end points
        startX = 0;
        startY = 0;
        endX = 4;
        endY = 4;
        barrierCounter = barrierNum;
        //generates random barriers
        for(int i=0; i < boardLength; i++){
            for(int j=0; j < boardWidth; j++){
                temp = (int)(Math.random()*3);
                if(temp==0 && barrierCounter!=0){
                    allBoards[counter][i][j] = false;
                    barrierCounter--;
                }
                else allBoards[counter][i][j] = true;
            }
        }
        //Lowers the difficulty if a level cannot be generated by 8000 levels
        //Prevents StackOverflow
        if(runCounter == 8000){
            runCounter = 0;
            difficulty--;
        }

        allBoards[counter][startX][startY] = true;
        allBoards[counter][endX][endY] = true;
        minMoves = BoardUtilities.checkBoard(allBoards[counter],startX, startY, endX, endY, boardLength, boardWidth);

        //If the board is not possible to complete it will re-call the method
        if(minMoves==boardLength*boardWidth || minMoves<=difficulty)makeIt();
        else{
            barrierNum = 15;
            difficulty++;
            counter++;
        }
    }

    public void checkWin(){
        if(knight.getRow() == endX && knight.getCol() == endY){
            doneWithLevel = true;
            if(lvlCounter == NUM_OF_LEVELS)
                youWon.setVisible(true);
            else {
                lvlCounter++;
                nextLevel.setVisible(true);
            }
        }
    }

    public void resetLevel(){
        board = allBoards[lvlCounter];
        nextLevel.setVisible(false);
        doneWithLevel = false;
        knight.setRow(0);
        knight.setCol(0);
    }
}
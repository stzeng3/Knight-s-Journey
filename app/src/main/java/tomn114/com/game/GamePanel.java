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
    private boolean[][] board;

    private int barrierCounter, counter = 0, runCounter = 0; // How many times makeIt() runs
    private int barrierNum = 17;
    private int difficulty = 2;
    private int startX, startY;
    private int endX, endY;
    private int minMoves;
    private int moves;
    private int currX, currY;
    private int boardLength = 5; //
    private int boardWidth = 5;
    private int temp; // Wtf is this for
    private int tileSize;
    private Knight knight;
    private boolean won = false;

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
        tileSize = getWidth()/5;

        board = new boolean[boardLength][boardWidth];
        BoardUtilities.createRects(boardLength, boardWidth, tileSize);
        tile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tile), tileSize, tileSize, false);
        river = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.river2), tileSize, tileSize, false);
        castle = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.castle), tileSize, tileSize, false);
        makeIt();

        knight = new Knight(startX, startY, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.right), tileSize, tileSize, false));

        thread = new MainThread(getHolder(), this);
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

            int[] rowColClicked = BoardUtilities.whichTileClicked(x, y);

            if(rowColClicked == null){
                System.out.println("Clicked outside board");
            }
            else if(board[rowColClicked[0]][rowColClicked[1]] == false){
                System.out.println("Clicked river");
            }
            else {
                knight.move(rowColClicked[0], rowColClicked[1]);
                checkWin();
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
            drawBoard(canvas);
            knight.draw(canvas);
        }
    }

    public void drawBoard(Canvas canvas){
        int x = 0;
        int y = 0;

        for(int i = 0; i<boardLength; i++){
            for(int j = 0; j<boardWidth; j++){
                if(board[i][j])
                    canvas.drawBitmap(tile, x, y, null);
                else
                    canvas.drawBitmap(river, x, y, null);

                if(i == endX && j == endY)
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
        startX = (int)(Math.random()*2)+boardWidth-2;
        startY = (int)(Math.random()*2);
        endX = (int)(Math.random()*2);
        endY = (int)(Math.random()*2)+boardLength-2;
        barrierCounter = barrierNum;
        //generates random barriers
        for(int i=0; i < boardLength; i++){
            for(int j=0; j < boardWidth; j++){
                temp = (int)(Math.random()*3);
                if(temp==0 && barrierCounter!=0){
                    board[i][j] = false;
                    barrierCounter--;
                }
                else board[i][j] = true;
            }
        }
        //Lowers the difficulty if a level cannot be generated by 8000 levels
        //Prevents StackOverflow
        if(runCounter == 8000){
            runCounter = 0;
            difficulty--;
        }

        board[startX][startY] = true;
        board[endX][endY] = true;
        minMoves = BoardUtilities.checkBoard(board,startX, startY, endX, endY, boardLength, boardWidth);

        //If the board is not possible to complete it will re-call the method
        if(minMoves==boardLength*boardWidth || minMoves<=difficulty)makeIt();
        else{
            difficulty++;
            counter++;
        }
    }

    public void checkWin(){
        if(knight.getRow() == endX && knight.getCol() == endY){
            won = true;
        }
    }
}
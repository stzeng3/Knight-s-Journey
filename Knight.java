package tomn114.com.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Knight {

    private int row, col;
    private Bitmap knightImg;

    public Knight(int row, int col, Bitmap knightImg){
        this.row  = row;
        this.col = col;
        this.knightImg = knightImg;
    }

    public int getRow(){ return row; }
    public int getCol(){ return col; }
    public void setRow(int row){ this.row = row; }
    public void setCol(int col){ this.col = col; }

    public void update(){
        //Animation
    }

    public void move(int newRow, int newCol){
        if(valid(newRow, newCol)){
            row = newRow;
            col = newCol;
        }
    }

    public boolean valid(int newRow, int newCol){
        if((row == newRow+2 && col ==newCol+1) ||
        (row == newRow+2 && col == newCol-1) ||
        (row == newRow+1 && col == newCol+2) ||
        (row == newRow-1 && col == newCol+2) ||
        (row == newRow+1 && col == newCol-2) ||
        (row == newRow-1 && col == newCol-2) ||
        (row == newRow-2 && col == newCol+1) ||
        (row == newRow-2 && col == newCol-1)) return true;

        return false;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(knightImg,col*knightImg.getWidth(), GamePanel.boardOffset + row*knightImg.getHeight(), null);
    }
}

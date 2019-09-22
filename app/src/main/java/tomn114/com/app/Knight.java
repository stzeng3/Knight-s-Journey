package tomn114.com.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.Toast;

public class Knight {

    private int row, col;
    private Bitmap knightImg;
    private Context context;

    public Knight(int row, int col, Bitmap knightImg, Context context){
        this.row  = row;
        this.col = col;
        this.knightImg = knightImg;
        this.context = context;
    }

    public int getRow(){ return row; }
    public int getCol(){ return col; }
    public void setRow(int row){ this.row = row; }
    public void setCol(int col){ this.col = col; }

    public void update(){
        //Animation
    }

    public void move(int newRow, int newCol, int lvlCounter){
        if(valid(newRow, newCol)){
            row = newRow;
            col = newCol;
            GamePanel.levelMoves[lvlCounter]++;
        }
        else
            Toast.makeText(context, "Invalid move!", Toast.LENGTH_SHORT).show();
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

package tomn114.com.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BlinkingText {

    private String text;
    private int x, y; // Top Left
    private Paint paint;
    private boolean visible;
    private Rect textBounds;

    public BlinkingText(String text, int x, int y, Paint paint){
        this.text = text;
        this.x = x;
        this.y = y;
        this.paint = paint;
        visible = false;

        this.paint.setTextSize(48);
        Rect bounds = new Rect();
        this.paint.getTextBounds(text, 0, text.length(), bounds);
        textBounds = new Rect(bounds.left + x , bounds.top + y, bounds.right + x, bounds.bottom + y);
    }

    public void update(){
        //Todo: add blinking animation
    }

    public void draw(Canvas canvas){
        if(!visible) return;
        canvas.drawText(text, x, y, paint);
    }

    public boolean clicked(int touchX, int touchY){
        if(!visible) return false;
        if(textBounds.contains(touchX, touchY))
            return true;
        return false;
    }

    public void setVisible(boolean b){ visible = b; }
}

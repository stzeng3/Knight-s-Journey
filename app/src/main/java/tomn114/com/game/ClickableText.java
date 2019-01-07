package tomn114.com.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ClickableText {

    private String text;
    private int x, y; // Top Left
    private Paint paint;
    private Paint rectPaint;
    private boolean visible;
    private Rect textBounds;
    private GamePanel gp;

    public ClickableText(GamePanel gp, String text, int x, int y, Paint paint){
        this.gp = gp;
        this.text = text;
        this.x = x;
        this.y = y;
        this.paint = paint;
        visible = false;
        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.STROKE);

        this.paint.setTextSize(GamePanel.DEFAULT_TEXT_SIZE / ((GamePanel.DEFAULT_HEIGHT+GamePanel.DEFAULT_WIDTH) / (gp.getPhoneHeight() + gp.getPhoneWidth()) ));
        Rect bounds = new Rect();
        this.paint.getTextBounds(text, 0, text.length(), bounds);
        textBounds = new Rect(bounds.left + x - 10 , bounds.top + y - 10, bounds.right + x + 10, bounds.bottom + y + 10);
    }

    public void update(){
        //Todo: add blinking animation if needed
    }

    public void draw(Canvas canvas){
        if(!visible) return;
        canvas.drawText(text, x, y, paint);
        canvas.drawRect(textBounds, rectPaint);
    }

    public boolean clicked(int touchX, int touchY){
        if(!visible) return false;
        if(textBounds.contains(touchX, touchY))
            return true;
        return false;
    }

    public void setVisible(boolean b){ visible = b; }
}

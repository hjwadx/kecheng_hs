package fm.jihua.kecheng.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class SemiCircleDrawable extends Drawable {
	private Paint paint;
    private RectF rectF;
    private int color;
    private Direction angle;
    private int alpha;
    

    public enum Direction
    {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }

    public SemiCircleDrawable() {
        this(Color.WHITE, Direction.TOP);
    }

    public SemiCircleDrawable(int color, Direction angle) {
        this.color = color;
        this.angle = angle;
        paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL);
        rectF = new RectF();
    }

    public int getColor() {
        return color;
    }

    /**
     * A 32bit color not a color resources.
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        Rect bounds = getBounds();
        rectF.set(bounds);

		if (angle == Direction.LEFT)
			canvas.drawCircle(bounds.right, bounds.bottom / 2,
					bounds.bottom / 2, paint);
		else if (angle == Direction.TOP)
			canvas.drawCircle(bounds.right / 2, bounds.bottom,
					bounds.right / 2, paint);
		else if (angle == Direction.RIGHT)
			canvas.drawCircle(bounds.left, bounds.bottom / 2,
					bounds.bottom / 2, paint);
		else if (angle == Direction.BOTTOM)
			canvas.drawCircle(bounds.right / 2, bounds.top,
					bounds.right / 2, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        paint.setAlpha(this.alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // Has no effect
    }

    @Override
    public int getOpacity() {
        // Not Implemented
        return 0;
    }

}

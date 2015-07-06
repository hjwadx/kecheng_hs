package fm.jihua.kecheng.rest.entities.weekstyle;

import fm.jihua.kecheng.ui.widget.weekview.WeekCanvasUtils;
import fm.jihua.kecheng.ui.widget.weekview.WeekViewParams;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

/**
 * @date 2013-7-26
 * @introduce
 */
public class LeftTopCornerConfig {
	public String color = "214, 213, 208";

	@Override
	public String toString() {
		return "LeftTopCornerConfig [color=" + color + "]";
	}
	
	public Bitmap onDrawView(WeekViewParams weekViewParams, boolean haveShadowForPaste) {
		Bitmap bmp = Bitmap.createBitmap(weekViewParams.offsetLeft, weekViewParams.offsetTop, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paintTopLeftCorner = new Paint();
		paintTopLeftCorner.setColor(WeekCanvasUtils.getInstance().getColorFromString(color));
		int left = 0;// getScrollLeft();
		int top = 0;// getScrollTop();
		canvas.drawRect(left, top, weekViewParams.offsetLeft + left, weekViewParams.offsetTop + top, paintTopLeftCorner);

		if (haveShadowForPaste) {
			weekViewParams.drawableShadow.setBounds(left, top, weekViewParams.offsetLeft + left, weekViewParams.offsetTop + top);
			weekViewParams.drawableShadow.draw(canvas);
		}
		return bmp;
	}

}

package fm.jihua.kecheng.rest.entities.weekstyle;

import fm.jihua.kecheng.ui.widget.weekview.WeekCanvasUtils;
import fm.jihua.kecheng.ui.widget.weekview.WeekViewParams;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Shader.TileMode;

/**
 * @date 2013-7-26
 * @introduce
 */
public class CourseIndicatorConfig {

	public String oddColor = "235,234,231";
	public String evenColor = "235,234,231";
	public String seperatorLineColor = "214,213,208";
	public String textColor = "153,153,153";
	public String hasSeperatorLine = "";
	public Shadow shadow;

	public boolean hasSeperatorLine() {
		return "1".equals(hasSeperatorLine);
	}

	@Override
	public String toString() {
		return "CourseIndicatorConfig [oddColor=" + oddColor + ", evenColor=" + evenColor + ", seperatorLineColor=" + seperatorLineColor + ", textColor=" + textColor + ", hasSeperatorLine="
				+ hasSeperatorLine + ", shadow=" + shadow + "]";
	}

	public Bitmap onDrawView(WeekViewParams weekViewParams, boolean haveShadowForPaste) {
		int leftTextSize = (int) (((double) weekViewParams.offsetLeft) / 5 * 3);
		Paint lPaint = new Paint();
		lPaint.setColor(WeekCanvasUtils.getInstance().getColorFromString(seperatorLineColor));
		lPaint.setStrokeWidth(2);

		Paint paintBack = new Paint();
		paintBack.setAntiAlias(true);

		Bitmap bmp = Bitmap.createBitmap(weekViewParams.offsetLeft + (shadow != null ? shadow.x : 0), weekViewParams.weekViewHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		int left = 0;// getScrollLeft();
		int top = weekViewParams.offsetTop;
		// int bottom = rect.bottom;

		Paint paintText = new Paint();
		paintText.setAntiAlias(true);
		paintText.setColor(WeekCanvasUtils.getInstance().getColorFromString(textColor));
		paintText.setTextAlign(Align.CENTER);
		paintText.setTextSize(leftTextSize);

		for (int i = 1; i <= weekViewParams.timeSlot; i++) {

			double currentBottom = weekViewParams.blockHeight * i + top;
			double currentTop = currentBottom - weekViewParams.blockHeight;
			// if (currentTop > bottom) {
			// break;
			// }
			int textBottom = (int) (currentBottom - (weekViewParams.blockHeight / 2 - leftTextSize / 2));
			int textLeft = left + weekViewParams.offsetLeft / 2;

			if (i % 2 == 1) {
				paintBack.setColor(WeekCanvasUtils.getInstance().getColorFromString(oddColor));
			} else {
				paintBack.setColor(WeekCanvasUtils.getInstance().getColorFromString(evenColor));
			}
			Rect rect2 = new Rect(left, (int) currentTop, left + weekViewParams.offsetLeft, (int) currentBottom);
			canvas.drawRect(rect2, paintBack);

			canvas.drawText(String.valueOf(i), textLeft, textBottom, paintText);
			if (hasSeperatorLine()) {
				// add back line
				canvas.drawLine(left, (float) currentBottom, left + weekViewParams.offsetLeft, (float) currentBottom, lPaint);
			}
		}
		if (shadow != null) {
			Paint paintShadow = new Paint();
			LinearGradient shader = new LinearGradient(0, 0, shadow.x, shadow.y, WeekCanvasUtils.getInstance().getColorFromString(shadow.shadowColor), 0x00000000, TileMode.CLAMP);
			paintShadow.setShader(shader);
			canvas.drawRect(weekViewParams.offsetLeft, (int) weekViewParams.blockHeight, weekViewParams.offsetLeft + shadow.x, (int) weekViewParams.blockHeight * weekViewParams.timeSlot, paintShadow);
		}

		if (haveShadowForPaste) {
			int bottom = (int) (weekViewParams.offsetTop + weekViewParams.blockHeight * weekViewParams.timeSlot);
			weekViewParams.drawableShadow.setBounds(0, weekViewParams.offsetTop, weekViewParams.offsetLeft, bottom);
			weekViewParams.drawableShadow.draw(canvas);
		}
		return bmp;
	}

}

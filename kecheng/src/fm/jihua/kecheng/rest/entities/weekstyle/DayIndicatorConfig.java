package fm.jihua.kecheng.rest.entities.weekstyle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import fm.jihua.kecheng.ui.widget.WeekView;
import fm.jihua.kecheng.ui.widget.weekview.WeekCanvasUtils;
import fm.jihua.kecheng.ui.widget.weekview.WeekViewParams;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;

/**
 * @date 2013-7-26
 * @introduce
 */
public class DayIndicatorConfig {

	public String oddColor = "235,234,231";
	public String evenColor = "235,234,231";
	public String seperatorLineColor = "214,213,208";
	public String textColor = "153,153,153";
	public String currentWeekdayColor = "0,0,0";
	public String currentWeekdayTextColor = "255,255,255";
	public String hasSeperatorLine = "";
	public Shadow shadow;

	public boolean hasSeperatorLine() {
		return "1".equals(hasSeperatorLine);
	}

	@Override
	public String toString() {
		return "DayIndicatorConfig [oddColor=" + oddColor + ", evenColor=" + evenColor + ", seperatorLineColor=" + seperatorLineColor + ", textColor=" + textColor + ", currentWeekdayColor="
				+ currentWeekdayColor + ", currentWeekdayTextColor=" + currentWeekdayTextColor + ", hasSeperatorLine=" + hasSeperatorLine + ", shadow=" + shadow + "]";
	}

	public Bitmap onDrawView(WeekViewParams weekViewParams, boolean haveShadowForPaste, WeekView weekView) {
		int topTextSize = (int) (((double) weekViewParams.offsetTop) / 5 * 3);
		int left = weekViewParams.offsetLeft;
		int top = 0;// getScrollTop();
		Bitmap bmp = Bitmap.createBitmap((int) (weekViewParams.weekViewWidth), weekViewParams.offsetTop + (shadow != null ? shadow.y : 0), Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint lPaint = new Paint();
		lPaint.setColor(WeekCanvasUtils.getInstance().getColorFromString(seperatorLineColor));
		lPaint.setStrokeWidth(2);
		Paint paintText = new Paint();
		paintText.setTextSize(topTextSize);
		paintText.setAntiAlias(true);
		Paint paintBack = new Paint();
		paintBack.setAntiAlias(true);
		for (int i = 0; i < weekViewParams.dayCount; i++) {
			int textBottom = top + weekViewParams.offsetTop / 2 + topTextSize / 3;
			int textleft = left + (int) (weekViewParams.blockWidth * i + (weekViewParams.blockWidth / 2 - topTextSize / 2) - topTextSize);

			double currentLeft = left + weekViewParams.blockWidth * i;
			double currentRight = currentLeft + weekViewParams.blockWidth;

			if (i % 2 == 0) {
				paintBack.setColor(WeekCanvasUtils.getInstance().getColorFromString(oddColor));
			} else {
				paintBack.setColor(WeekCanvasUtils.getInstance().getColorFromString(evenColor));
			}
			paintText.setColor(WeekCanvasUtils.getInstance().getColorFromString(textColor));
			// today
			if (CourseHelper.getWeekIndexFromDayOfWeek(CourseHelper.getDayOfWeek()) == i) {
				paintBack.setColor(WeekCanvasUtils.getInstance().getColorFromString(currentWeekdayColor));
				paintText.setColor(WeekCanvasUtils.getInstance().getColorFromString(currentWeekdayTextColor));
			}
			Rect rect2 = new Rect((int) currentLeft, (int) top, (int) currentRight, top + weekViewParams.offsetTop);
			canvas.drawRect(rect2, paintBack);
			canvas.drawText(Const.WEEKS_XINGQI[i], textleft, textBottom, paintText);

			double currentLineLeft = currentLeft + weekViewParams.blockWidth - 1;
			if (hasSeperatorLine()) {
				// add back line
				canvas.drawLine((float) currentLineLeft, 0, (float) currentLineLeft, top + weekViewParams.offsetTop, lPaint);
			}
		}
		if (shadow != null) {
			Paint paintShadow = new Paint();
			LinearGradient shader = new LinearGradient(0, 0, shadow.x, shadow.y, WeekCanvasUtils.getInstance().getColorFromString(shadow.shadowColor), 0x00000000, TileMode.CLAMP);
			paintShadow.setShader(shader);
			canvas.drawRect(0, weekViewParams.offsetTop, weekView.getScreenWidth(), weekViewParams.offsetTop + shadow.y, paintShadow);
		}

		if (haveShadowForPaste) {
			int right = (int) (weekViewParams.offsetLeft + weekViewParams.blockWidth * weekViewParams.dayCount);
			weekViewParams.drawableShadow.setBounds(weekViewParams.offsetLeft, 0, right, weekViewParams.offsetTop);
			weekViewParams.drawableShadow.draw(canvas);
		}

		return bmp;
	}

}

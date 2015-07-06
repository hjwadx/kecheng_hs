package fm.jihua.kecheng.ui.widget.weekview;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.ImageHlp;

public class WeekViewParams {
	protected double titleHeightPercent = 22.0 / 52.0;
	protected double titleWidthPercent = 20.0 / 60.0;
	public double blockWidth;
	public double blockHeight;
	public int weekViewWidth;
	public int weekViewHeight;
	public int offsetTop;
	public int offsetLeft;
	public int timeSlot;
	public int dayCount = 7;
	
	
	//关于stylecontroller的绘制
	public int pasterHintViewSize = (int) ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 22);
	public int coursePadding = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 5);
	public int courseSpacing = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 2);
	public int courseHintLineHeight = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 1.5);
	public Drawable drawableShadow = ImageHlp.getRepeatShadowBitmap(App.getInstance(), R.drawable.paster_edit_class_cover_repeat);
	public int tukedWidth = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 12);
	
	public WeekViewParams(int screenWidth, int timeSlot) {
		this.timeSlot = timeSlot;
		initBaseParams(screenWidth);
	}
	
	public void setTimeSlot(int timeSlot){
		this.timeSlot = timeSlot;
		initBaseHeight();
	}
	
	public Point changeActualXYToSlotXY(Point pt) {
		if (pt.x < offsetLeft || pt.y < offsetTop) {
			return null;
		}
		int x = (int) ((pt.x - offsetLeft) / blockWidth);
		int y = (int) ((pt.y - offsetTop) / blockHeight);
		return new Point(x, y);
	}
	
	public void initBaseParams(int screenWidth) {
		blockWidth = (screenWidth / (titleWidthPercent + 5));
		blockHeight = blockWidth / 120 * 96;
		offsetTop = (int) (titleHeightPercent * blockHeight);
		offsetLeft = (int) (titleWidthPercent * blockWidth);
		weekViewWidth = (int) (offsetLeft + blockWidth * dayCount);
		initBaseHeight();
	}

	public void initBaseHeight() {
		weekViewHeight = (int) (offsetTop + blockHeight * timeSlot);
	}
	
	public Rect getWeekViewRect(int leftNumber, int topNumber, int rightNumber, int bottomNumber) {
		int left = (int) (offsetLeft + blockWidth * leftNumber);
		int top = (int) (offsetTop + blockHeight * topNumber);
		int right = (int) (offsetLeft + blockWidth * rightNumber);
		int bottom = (int) (offsetTop + blockHeight * bottomNumber);
		return new Rect(left, top, right, bottom);
	}
	
	
//	public int getTimeSlot(){
//		return this.timeSlot;
//	}

}

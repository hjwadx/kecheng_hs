package fm.jihua.kecheng.rest.entities.sticker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import fm.jihua.kecheng.utils.CourseHelper;

/**
 * @date 2013-7-18
 * @introduce 用户贴纸
 */
public class UserSticker implements Serializable, Cloneable {

	private static final long serialVersionUID = -3061251158792494341L;

	public static final int DEFAULT_ID = 0;
	
	public int id = DEFAULT_ID;
	
	public int screen_x;
	public int screen_y;
	public Sticker sticker;

	private Point point ;
	
	private boolean status;
	
	public UserSticker(Sticker sticker) {
		super();
		this.sticker = sticker;
	}

	public UserSticker(int id, int screen_x, int screen_y, Sticker sticker) {
		super();
		this.id = id;
		this.screen_x = screen_x;
		this.screen_y = screen_y;
		this.sticker = sticker;
	}
	
	public void setPoint(int x, int y) {
		this.screen_x = CourseHelper.getDayOfWeekFromWeekIndex(x);;
		this.screen_y = y + 1;
		this.point = new Point(x, y);
	}

	public void setPoint(Point pt) {
		this.screen_x = CourseHelper.getDayOfWeekFromWeekIndex(pt.x);
		this.screen_y = pt.y + 1;
		this.point = pt;
	}
	
	public Point getPoint(){
		point = new Point(CourseHelper.getWeekIndexFromDayOfWeek(screen_x), screen_y - 1);
		return point;
	}
	
	public int getWeekIndexX(){
		return CourseHelper.getWeekIndexFromDayOfWeek(screen_x);
	}
	
	public int getTimeSlotY(){
		return screen_y - 1;
	}
	
	public void setHiddenStatus(boolean isHidden){
		this.status = isHidden;
	}
	
	public boolean isHidden(){
		return status;
	}

	public List<Point> getPointList() {
		List<Point> points = new ArrayList<Point>();
		for (int i = 0; i < this.sticker.width; i++) {
			for (int j = 0; j < this.sticker.height; j++) {
				points.add(new Point(CourseHelper.getWeekIndexFromDayOfWeek(screen_x) + i, screen_y - 1 + j));
			}
		}
		return points;
	}
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Drawable getDrawable(Context context) {
		return this.sticker.getDrawable(context);
	}
}

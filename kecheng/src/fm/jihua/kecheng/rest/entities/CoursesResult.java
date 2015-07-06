package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.util.Arrays;

import fm.jihua.kecheng.rest.entities.sticker.UserSticker;

public class CoursesResult extends BaseResult implements Serializable {

	private static final long serialVersionUID = 9100696094477217911L;

	public Course[] courses;
	public Calendar calendar;
	// public stickers;
	public String theme_name = "";
	public int theme_product_id;

	public UserSticker[] stickers;
	public Event[] events;
	

	@Override
	public String toString() {
		return "CoursesResult [courses=" + Arrays.toString(courses) + ", calendar=" + calendar + ", theme_name=" + theme_name + ", stickers=" + Arrays.toString(stickers) + "]";
	}

}

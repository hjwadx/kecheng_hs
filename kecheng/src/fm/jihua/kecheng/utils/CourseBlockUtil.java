package fm.jihua.kecheng.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;

/**
 * @date 2013-7-9
 * @introduce 定时工具类
 */
public class CourseBlockUtil {

	private static CourseBlockUtil courseBlockUtil;

	public static CourseBlockUtil getInstance() {

		if (courseBlockUtil == null) {
			courseBlockUtil = new CourseBlockUtil();
		}
		return courseBlockUtil;
	}

	public List<CourseBlock> getCourseBlocks(Context context, Date dt) {
		return getCourseBlocksOfDayOfWeek(context, dt);
	}
	
	private List<CourseBlock> getCourseBlocksOfDayOfWeek(Context context, Date date) {
 		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
 		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
    	App app = (App)context.getApplicationContext();
    	List<CourseBlock> blocks = new ArrayList<CourseBlock>();
        List<Course> courses = app.getDBHelper().getCourses(app.getUserDB());
        if (courses.size() > 0) {
        	int currentWeek = app.getCurrentWeek(true);
        	int index = CourseHelper.getWeekdayFromSystem(dayOfWeek);
        	int trueWeek = currentWeek;
        	// Monday
        	if (index == 1) {
        		Calendar firstDayOfThisWeek = Calendar.getInstance();
        		int dayInWeek = CourseHelper.getWeekdayFromSystem(firstDayOfThisWeek.get(Calendar.DAY_OF_WEEK));
        		firstDayOfThisWeek.add(Calendar.DATE, 1 - dayInWeek);
        		if (firstDayOfThisWeek.compareTo(calendar) < 0) {
        			trueWeek += 1;
        		}
        	}
        	CourseBlock[][] curriculum = CourseHelper.getCurriculum(courses, trueWeek);
        	blocks = Arrays.asList(curriculum[index-1]);
        	blocks = CourseHelper.merge(blocks, false);
		}
        return blocks;
    }
	
	public List<CourseBlock> getCourseBlocksOfDayOfWeek(Context context, int dayOfWeek){
    	App app = (App)context.getApplicationContext();
    	List<CourseBlock> blocks = new ArrayList<CourseBlock>();
        List<Course> courses = app.getDBHelper().getCourses(app.getUserDB());
        if (courses.size() > 0) {
        	CourseBlock[][] curriculum = CourseHelper.getCurriculum(courses, app.getCurrentWeek(true));
        	int index = CourseHelper.getWeekdayFromSystem(dayOfWeek);
        	blocks = Arrays.asList(curriculum[index-1]);
        	blocks = CourseHelper.merge(blocks, false);
		}
        return blocks;
    }
}

package fm.jihua.kecheng.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.graphics.Point;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.CourseUnit;

public class CourseHelper {
	
	
      //暂时没用到
//    public static List<CourseBlock> getCoursesOfWeek(List<Course> courses, int week){
//    	List<CourseBlock> result = new ArrayList<CourseBlock>();
//    	CourseBlock[][] curriculum = getCurriculum(courses, week);
//    	for (CourseBlock[] courseBlocks : curriculum) {
//			result.addAll(Arrays.asList(courseBlocks));
//		}
//    	return result;
//    }
	
    //用于小插件
    public static CourseBlock getNextCourse(List<CourseBlock> blocks){
    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MINUTE, 30);
		int time_slot = 0;
		if (App.getInstance().getTimeModeList() == null || App.getInstance().getTimeModeList().size() == 0) {
			time_slot = getTimeSlot(calendar.getTime());
		} else {
			time_slot = getTimeSlotByTimeMode(calendar.getTime());
		}
    	CourseBlock course = null;
    	int preSlot = 0;
    	for (CourseBlock courseItem : blocks) {
    		if (courseItem != null && !courseItem.empty) {
    			if ((courseItem.start_slot <= time_slot && courseItem.end_slot >= time_slot) || (courseItem.start_slot >time_slot && preSlot <= time_slot)) {
    				course = courseItem;
    				break;
    			}
    			preSlot = courseItem.end_slot;
			}
		}
    	if (course == null && blocks.size() > 0) {
			course = blocks.get(blocks.size()-1);
		}
    	return course;
    }
    
    public static List<CourseBlock> getFullCourseBlocks(List<Course> courses, int week){
    	List<CourseBlock> result = new ArrayList<CourseBlock>();
    	for (Course course: courses) {
    		result.addAll(getCourseBlocksFromCourse(course, week));
		}
    	return result;
    }
    
    public static List<CourseBlock> getCourseBlocksFromCourse(Course course, int week){
		List<CourseBlock> result = new ArrayList<CourseBlock>();
		List<CourseUnit> courseUnits = CourseUnit.mergeCourseUnits(course.course_units);
		for (CourseUnit courseUnit : courseUnits) {
			for (String slot : courseUnit.getTimeSlotsList()) {
				result.add(new CourseBlock(course, courseUnit, slot, week));
			}
		}
    	return result;
    }
    
    public static List<CourseBlock> getCourseBlocksFromCourse(Course course, boolean merge){
		List<CourseBlock> result = new ArrayList<CourseBlock>();
		List<CourseUnit> courseUnits = merge ? CourseUnit.mergeCourseUnits(course.course_units) : course.course_units;
		for (CourseUnit courseUnit : courseUnits) {
			for (String slot : courseUnit.getTimeSlotsList()) {
				result.add(new CourseBlock(course, courseUnit, slot));
			}
		}
    	return result;
    }
    
    public static CourseBlock[][] getCurriculum(List<Course> courses, int week){
    	CourseBlock[][] curriculum = new CourseBlock[7][App.mTimeSlotLength];
		for (int j = 0; j < curriculum.length; j++) {
			for (int k = 0; k < curriculum[j].length; k++) {
				CourseBlock courseBlock = new CourseBlock();
				courseBlock.start_slot = k + 1;
				courseBlock.end_slot = k + 1;
				courseBlock.day_of_week = j;
				curriculum[j][k] = courseBlock;
			}
		}
    	if (courses != null) {
    		for (Course course : courses) {
        		if(course.course_units == null){
        			continue;
        		}
    			for (CourseUnit courseUnit : course.course_units) {
//					if (CourseBlock.isActive(week, courseUnit.weeks)) {
						for (String slot : courseUnit.getTimeSlotsList()) {
							for (Integer i : CourseUnit.changeStringToList(slot)) {
								if(i >= App.mTimeSlotLength){
									break;
								}
								int index = getWeekIndexFromDayOfWeek(courseUnit.day_of_week);
								curriculum[index][i] = new CourseBlock(course, courseUnit, slot, week);
							}
						}
//					}
    			}
    		}
		}
    	return curriculum;
    }
    
    public static List<CourseBlock> changeToCourseBlocks(List<Course> courses) {
		List<CourseBlock> _blocks = new ArrayList<CourseBlock>();
		for (Course course : courses) {
			if (course.course_units != null) {
				List<CourseBlock> courseBlocks = getCourseBlocksFromCourse(course, true);
				courseBlocks = merge(courseBlocks, false);
				_blocks.addAll(courseBlocks);
			}
		}
		return _blocks;
	}
    
    public static List<CourseBlock> merge(List<CourseBlock> courseBlocks, boolean enableEmpty){
    	List<CourseBlock> result = new ArrayList<CourseBlock>();
    	for (CourseBlock courseBlock : courseBlocks) {
    		if (enableEmpty && courseBlock.empty) {
    			result.add(courseBlock);
    			continue;
			}
    		boolean merged = false;
    		for (CourseBlock _courseBlock : result) {
    			if (_courseBlock.merge(courseBlock)) {
    				merged = true;
    				break;
    			}
    		}
			if (!merged && !courseBlock.empty) {
				result.add(courseBlock);
			}
		}
    	return result;
    }
    
    public static int getMaxTimeSlot(List<CourseBlock> blocks){
    	int max = 0;
    	for (CourseBlock courseBlock : blocks) {
			max = Math.max(courseBlock.end_slot, max);
		}
    	return max;
    }
    
    public static int getTimeSlot(Date date){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	int hour = Integer.valueOf(Const.START_SLOT_TIME.split(":")[0]);
    	int minute = Integer.valueOf(Const.START_SLOT_TIME.split(":")[1]);
    	calendar.set(Calendar.HOUR_OF_DAY, hour);
    	calendar.set(Calendar.MINUTE, minute);
    	int mins = (int) ((date.getTime() - calendar.getTimeInMillis())/(1000*60));
    	int slot = mins / Const.PER_SLOT_TIME + 1;
    	return Math.max(0, Math.min(App.mTimeSlotLength, slot));
    }
    
	public static int getTimeSlotByTimeMode(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int slot = calculateTimeSlot(calendar.getTimeInMillis(), false);
		return Math.max(0, Math.min(App.mTimeSlotLength, slot));
	}

    public static int getWeekdayFromSystem(int weekday){
    	if(weekday == Calendar.SUNDAY){
    		weekday = 7;
    	}else {
			weekday -= 1;
		}
    	return weekday;
    }
    
    public static int getSystemWeekdayFromWeekday(int weekday){
    	return weekday == 7 ? 1 : weekday + 1;
    }
    
    public static String getDay () {
        return Const.WEEKS_XINGQI[getDayOfWeek() - 1];		
	}
    
    public static int getDayOfWeek(){
    	Calendar calendar = Calendar.getInstance();
    	return getWeekdayFromSystem(calendar.get(Calendar.DAY_OF_WEEK));
    }
    
    public static int getIndexFromDayOfWeek(int day_of_week){
    	return day_of_week == 7 ? 0 : day_of_week;
    }
    
	public static int getWeekIndexFromDayOfWeek(int day_of_week) {
		return (day_of_week + 6) % 7;
	}
	
	public static Set<Point> resetListForWeekIndex(Set<Point> notValidPoints){
		for (Point point : notValidPoints) {
			point.set(getWeekIndexFromDayOfWeek(point.x), point.y);
		}
		return notValidPoints;
	}
    
	public static int getDayOfWeekFromWeekIndex(int week_index) {
		return (week_index + 1) % 7;
	}
    
	public static long getFirstDayOfWeek(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int dayOfWeek = getWeekdayFromSystem(calendar.get(Calendar.DAY_OF_WEEK));
		calendar.add(Calendar.DATE, getWeekdayFromSystem(Calendar.MONDAY)-dayOfWeek);
		return calendar.getTimeInMillis();
    }
    
    public static int getTrueWeek(int weekPast, long startWeekTime){
    	double diff = ((Calendar.getInstance().getTimeInMillis() - getFirstDayOfWeek(startWeekTime))/(7d*24*60*60*1000));
		return weekPast+(int)Math.ceil(diff);
    }
    

	
	public static int calculateTimeSlot(long time, boolean is_end_time){
		int slot = 0;
		List<List<String>> timeLists = App.getInstance().getTimeModeList();
		SimpleDateFormat dateFormat;
		String pattern="HH:mm";
		dateFormat=new SimpleDateFormat(pattern);
		for(List<String> slotsList : timeLists){
			Date date0 = new Date(time);
			Date date = null;
			try {
				date = dateFormat.parse(slotsList.get(0));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			date.setYear(date0.getYear());
			date.setMonth(date0.getMonth());
			date.setDate(date0.getDate());
			if(date.getTime() > time || (is_end_time && date.getTime() == time)){
				slot = timeLists.indexOf(slotsList);
				break;
			}
			if(timeLists.indexOf(slotsList) == timeLists.size() - 1){
				slot = timeLists.size();
			}
		}
		if(slot < 1){
			slot = 1;
		}
		return slot;
	}
    
//  public static List<CourseBlock> mergeWithPlans(List<CourseBlock> blocks, List<Plan> plans){
//  	for (Plan plan : plans) {
//  		CourseBlock block = getBlock(blocks, plan.dayOfWeek, plan.timeSlot);
//			if (block != null) {
//				if (block.empty) {
//					block.id = plan.id;
//					block.name = plan.name;
//					block.type = plan.type;
//					block.empty = false;
//				}
//			}else {
//				block = new CourseBlock(plan);
//				blocks.add(block);
//			}
//		}
//  	return blocks;
//  }
  
//  public static List<Plan> getPlansOfWeekday(List<Plan> plans, int dayOfWeek){
//  	List<Plan> result = new ArrayList<Plan>();
//  	if (plans != null) {
//			for (Plan plan : plans) {
//				if (plan.dayOfWeek == dayOfWeek) {
//					result.add(plan);
//				}
//			}
//		}
//  	return result;
//  }
  
//  public static Plan getPlan(List<Plan> plans, int dayOfWeek, int timeSlot){
//  	if (plans != null) {
//  		for (Plan plan : plans) {
//  			if (plan.dayOfWeek == dayOfWeek && plan.timeSlot == timeSlot) {
//  				return plan;
//  			}
//  		}
//		}
//  	return null;
//  }
  
//  private static CourseBlock getBlock(List<CourseBlock> blocks, int dayOfWeek, int timeSlot){
//  	for (CourseBlock courseBlock : blocks) {
//			if (courseBlock.day_of_week == dayOfWeek && courseBlock.start_slot <= timeSlot && courseBlock.end_slot >= timeSlot) {
//				return courseBlock;
//			}
//		}
//  	return null;
//  }
}

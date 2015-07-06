package fm.jihua.kecheng.rest.entities;

import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;

public class CourseBlock extends Course implements Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9154645951195918369L;
	public int start_slot;
	public int end_slot;
//	public int occurance;
	public int day_of_week;
	public String room;
	public String weeks;
	public boolean empty = true;
	public int type;
	public boolean active = true;
	
	public int backColor;
	
	//new
	public Event event;

	public CourseBlock(){
		
	}
	
	//将来用作活动的
//	public CourseBlock(Plan plan){
//		this.id = plan.id;
//		this.name = plan.name;
//		this.day_of_week = plan.dayOfWeek;
//		this.start_slot = plan.timeSlot;
//		this.end_slot = plan.timeSlot;
//		this.type = plan.type;
//		this.empty = false;
//	}
	
	public CourseBlock(Event event, boolean local_slots) {
		this.name = event.name;
		this.id = event.id;
		this.empty = false;
		String time_slots = local_slots ? event.calculateTimeSlots() : event.time_slots;
		if(time_slots != null){
			String[] str = time_slots.split("-");
			if(str.length > 0){
				this.start_slot = Integer.valueOf(str[0]);
				this.end_slot = Integer.valueOf(str[str.length - 1]);
			}
		}
		this.room = event.location;
		this.event = event;
		this.day_of_week = event.getDayOfWeek();
		this.active = event.getActive();
	}
	
	public CourseBlock(Course course) {
		this.name = course.name;
		this.teacher = course.teacher;
		this.description = course.description;
		this.course_times_string = course.getCourseTimeString();
		this.id = course.id;
		this.end_week = course.end_week;
		this.start_week = course.start_week;
		this.school_id = course.school_id;
		this.empty = false;
		this.students_count =  course.students_count;
		this.course_units = course.course_units;
	}
	
	public CourseBlock(Course course, CourseUnit courseUnit, String time_slots) {
		this(course);
		this.day_of_week = courseUnit.day_of_week;
		this.room = courseUnit.room;
		this.weeks = courseUnit.weeks;
		setSlot(time_slots);
	}

	public CourseBlock(Course course, CourseUnit courseUnit, String time_slots, int week) {
		this(course, courseUnit, time_slots);
		//课程暂时都是active的
//		this.active = isActive(week);
	}
	
	private void setSlot(String time_slots) {
		String[] slots = time_slots.split("-");
		this.start_slot = CommonUtils.parseInt(slots[0]);
		this.end_slot = CommonUtils.parseInt(slots[slots.length - 1]);
	}
;
	public String getSlotString(){
		if (start_slot == end_slot) {
			return "" + start_slot;
		}else {
			return start_slot + "-" + end_slot;
		}
	}
	
	public boolean merge(CourseBlock block){
		if(this.name != null && this.name.equals(block.name) && this.day_of_week == block.day_of_week && ObjectUtils.nullSafeEquals(this.room, block.room) && ObjectUtils.nullSafeEquals(this.weeks, block.weeks)){
			if (!CourseUnit.mergeString(getSlotString(), block.getSlotString()).contains(",")) {
				this.start_slot = Math.min(this.start_slot, block.start_slot);
				this.end_slot = Math.max(this.end_slot, block.end_slot);
				return true;
			}
		}
		return false;
	}
	
//	public boolean isActive(int week){
//		return isActive(week, weeks);
//	}
	
//	public static boolean isActive(int week, String weeks){
//		return CourseUnit.isStringConflict(weeks, String.valueOf(week));
//	}
	
	//周日-周六  0-6
	public int getWeekIndexStartWithSunday(){
		return CourseHelper.getIndexFromDayOfWeek(this.day_of_week);
	}
	
	//周一-周日  0-6
	public int getWeekIndex(){
		return CourseHelper.getWeekIndexFromDayOfWeek(this.day_of_week);
	}
	
	public Object clone(){
		return super.clone();
    }

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		String[] weekDays = Const.SERVER_WEEKS;
		int index = day_of_week;
		if (start_slot == end_slot) {
			return weekDays[index % 7] + " 第" + start_slot + "节";
		} else {
			return weekDays[index % 7] + " " + start_slot + "-" + end_slot + "节";
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CourseBlock) {
			return ((CourseBlock)o).id == this.id;
		}
		return false;
	}
}

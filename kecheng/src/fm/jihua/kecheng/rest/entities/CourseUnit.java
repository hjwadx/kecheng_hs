package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import android.graphics.Point;

import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.utils.CommonUtils;

public class CourseUnit implements Serializable {
	
	private static final long serialVersionUID = -3783484018720224567L;
	
	public Integer course_id;
	
	public int day_of_week;
	public String time_slots;
	public String weeks;
	public String room;
	

	public Course course;
	
	public CourseUnit() {
	}
	
	public CourseUnit(int day_of_week, String time_slots, String room, String weeks) {
		super();
		this.day_of_week = day_of_week;
		this.time_slots = time_slots;
		this.weeks = weeks;
		this.room = room;
	}
	
	public CourseUnit(int day_of_week, String time_slots, String room, String weeks, int course_id) {
		super();
		this.day_of_week = day_of_week;
		this.time_slots = time_slots;
		this.weeks = weeks;
		this.room = room;
		this.course_id = course_id;
	}
	
	//暂时没用上
//	public CourseUnit getConflictUnits(List<CourseUnit> courseUnits) {
//		CourseUnit courseUnit;
//		for(int j=courseUnits.size()-1; j>=0; j--){
//			courseUnit = courseUnits.get(j);
//			if(courseUnit.day_of_week == day_of_week){
//				if(isStringConflict(courseUnit.weeks, weeks) && isStringConflict(courseUnit.time_slots, time_slots)){
//					return courseUnit;
//				}
//			}
//		}
//		return null;
//	}
	
	public static Point getConflictUnitsPoint(List<CourseUnit> units) {
		for (int i = 0; i < units.size(); i++) {
			for (int j = i + 1; j < units.size(); j++) {
				CourseUnit t1 = units.get(i);
				CourseUnit t2 = units.get(j);
				if(t1.day_of_week == t2.day_of_week){
					if(CourseUnit.isStringConflict(t1.weeks, t2.weeks) && CourseUnit.isStringConflict(t1.time_slots, t2.time_slots)){
						return new Point(i + 1, j + 1);
					}
				}
			}
		}
		return null;
	}
	
	public static boolean isStringConflict(String str1, String str2){
		List<Integer> list1 = changeStringToList(str1);
		List<Integer> list2 = changeStringToList(str2);
		for(Integer integer : list2){
			if(list1.contains(integer)){
				return true;
			}
		}
		return false;
	}
	
	public List<CourseUnit> mergeToCourseUnits(List<CourseUnit> courseUnits) {
		for(int j=courseUnits.size()-1; j>=0; j--){
			CourseUnit courseUnit = courseUnits.get(j);
			if(courseUnit.day_of_week == day_of_week && ObjectUtils.nullSafeEquals(courseUnit.room, room)){
//				if(courseUnit.weeks.equals(weeks)){
//					this.time_slots = mergeString(courseUnit.time_slots, this.time_slots);
//					courseUnits.remove(courseUnit);
//				} else 
				if(courseUnit.time_slots.equals(time_slots)){
					this.weeks = mergeString(courseUnit.weeks, this.weeks);
					courseUnits.remove(courseUnit);
				}
			}
		}
		courseUnits.add(this);
		return courseUnits;
	}
	
	public static List<CourseUnit> mergeCourseUnits(List<CourseUnit> courseUnits) {
		List<CourseUnit> result = new ArrayList<CourseUnit>();
		for (CourseUnit courseUnit : courseUnits) {
			courseUnit.mergeToCourseUnits(result);
		}
		return result;
	}
	
	public static String mergeString(String str1, String str2) {
		String string = str1 + "," + str2;
		List<Integer> list = changeStringToList(string);
		String result = getWeekStringFromList(list);
		return result;
	}
	
	public static String getWeekStringFromList(List<Integer> list) {
		StringBuffer buffer = new StringBuffer();
		final int FIRST = 1;
		final int CONTINUED = 2;
		int type = 0;
		for(Integer i : list){
			if(list.contains(i - 1)){
				if(list.indexOf(i) == list.size() - 1){
					buffer.append("-" + i);
				}
				type = CONTINUED;
				continue;
			} else {
				if(type == FIRST){
					buffer.append("," + i);
				} else if(type == CONTINUED){
					buffer.append("-" + (i-2) + "," + i);
				} else {
					buffer.append(String.valueOf(i));
				}
				type = FIRST;
			}
		}
		return buffer.toString();
	}
	
	public static List<Integer> changeStringToList(String str){
		LinkedHashSet<Integer> list = new LinkedHashSet<Integer>();
		for(String str_with_ : str.split(",")){
			String[] slots = str_with_.split("-");
			if (slots != null && slots.length > 0) {
				int min = CommonUtils.parseInt(slots[0]);
				int max = CommonUtils.parseInt(slots[slots.length - 1]);
				if (min == 0 || max == 0) {
					break;
				}
				for(int i = min; i <= max ; i++){
					list.add(i);
				}
			}
		}
		Integer[] integers = list.toArray(new Integer[list.size()]);
        Arrays.sort(integers);
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(Arrays.asList(integers));
		return result;
	}
	
	public static String getWeekStringWithOccurance(String str){
		List<Integer> list = new ArrayList<Integer>();
		list = changeStringToList(str);
		int min = list.get(0);
		int max = list.get(list.size() - 1);
		String weekString = str + "周";
		int type = -1;
		if((max - min)/2 + 1 == list.size() && list.size() > 2){
			for(Integer integer : list){
				if(type == -1){
					type = integer%2;
				} else {
					type = type == integer%2 ? integer%2 : -1;
				}
				if(type == -1){
					break;
				}
			}
		}
		switch(type){
		case 0:
			weekString = min + "-" + max + "周(双周)";
			break;
		case 1:
			weekString = min + "-" + max + "周(单周)";
			break;
		default:
			weekString = list.size() == 1 ?"第" + min + "周" : str + "周";
			break;
		}
		return weekString;
	}
	
	public static CourseUnit findCourseUnitByDayAndSlots(List<CourseUnit> course_units, String day_and_slots){
		if (course_units != null) {
			for (int i=0; i<course_units.size(); i++) {
				CourseUnit unit = course_units.get(i);
				if (ObjectUtils.nullSafeEquals(unit.day_of_week + "&" + unit.time_slots, day_and_slots)) {
					return unit;
				}
			}
		}
		return null;
	}
	
	public List<String> getTimeSlotsList() {
		List<String> result = new ArrayList<String>();
		result.addAll(Arrays.asList(time_slots.split(",")));
		return result;
	}
	
	public String getTimeString() {
		String result = "";
		String[] slots = time_slots.split("-");
		int start_slot = CommonUtils.parseInt(slots[0]);
		int end_slot = CommonUtils.parseInt(slots[slots.length - 1]);
		List<List<String>> timeLists = App.getInstance().getTimeModeList();
		if(timeLists != null && end_slot > timeLists.size()){
			return "很晚...(请检查您的节数时间设置)";
		}
		if (start_slot * end_slot != 0 && timeLists != null
				&& timeLists.size() > 0) {
			result = timeLists.get(start_slot - 1).get(0) + "-"
					+ timeLists.get(end_slot - 1).get(1);
		}
		return result;
	}

	@Override
	public String toString() {
		return "CourseUnit [course_id=" + course_id + ", day_of_week=" + day_of_week + ", time_slots=" + time_slots + "]";
//				", weeks=" + weeks + ", room=" + room + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CourseUnit) {
			CourseUnit b = (CourseUnit)o;
			return day_of_week == b.day_of_week && ObjectUtils.nullSafeEquals(time_slots, b.time_slots);
//					&& ObjectUtils.nullSafeEquals(weeks, b.weeks) && ObjectUtils.nullSafeEquals(room, b.room);
		}
		return false;
	}
}

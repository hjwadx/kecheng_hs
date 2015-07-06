package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.graphics.Point;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;

public class Course implements Serializable, Cloneable {

	private static final long serialVersionUID = 6098905102545841366L;

	public int id = 0;

	public String name;
	public String teacher;
	public String description;
	public Integer start_week;
	public Integer end_week;
	public Integer school_id;
	public String school_name;
	public String course_type;
	public Integer semester_id;
//	public List<CourseTime> course_times;
	public String course_times_string;
	public Integer students_count;
	public List<CourseUnit> course_units;

	public Course() {

	}
	
	//创建和更新课程的时候用
	public Course(int _id, String _name, String _teacher) {
		this.id = _id;
		this.name = _name;
		this.teacher = _teacher;
	}

	public Course(int _id, String _name, String _teacher, String _description,
			Integer _start_week, Integer _end_week, String _course_type) {
		this.id = _id;
		this.name = _name;
		this.teacher = _teacher;
		this.description = _description;
//		this.start_week = _start_week;
//		this.end_week = _end_week;
		this.course_type = _course_type;
	}

	public Course(int _id, String _name, String _teacher, String _description,
			Integer _start_week, Integer _end_week, String _course_type,
			String _course_time_string, Integer _students_count) {
		this.id = _id;
		this.name = _name;
		this.teacher = _teacher;
		this.description = _description;
		this.start_week = _start_week;
		this.end_week = _end_week;
		this.course_type = _course_type;
		this.course_times_string = _course_time_string;
		this.students_count = _students_count;
	}
	
	public Course(Cursor cursor){
		this(cursor.getInt(0), cursor.getString(1),
				cursor.getString(2), cursor.getString(3),
				cursor.getInt(4), cursor.getInt(5),
				cursor.getString(6), cursor.getString(8),
				cursor.getInt(9));
	}
	
	public Course getSuccinctCourse(){
		return new Course(this.id, this.name, this.teacher);
	}

	public String getCourseTimeString() {
		if (this.course_times_string == null
				|| this.course_times_string.length() == 0) {

		} else {
			this.course_times_string = this.course_times_string.replaceAll(
					"\\d{1,2}~\\d{1,2}周", "" + start_week + "~" + end_week
							+ "周");
		}
		return this.course_times_string;
	}
	
	public String getUnitString(boolean showRoom) {
		String result = "";
		if (course_units != null && course_units.size() > 0) {
			int i = 0;
			for (CourseUnit unit : course_units) {
				String str = ""
						+ Const.SERVER_WEEKS[unit.day_of_week] + unit.time_slots + "节";
				if (showRoom) {
					str += " " + unit.room;
				}
				if(i != 0){
					str = "  " + str;
				}
				i++;
				result += str;
			}
		}
//		result = result.substring(0, result.length() - 1);
		return result;
	}

	public String getTimeString() {
		String result = "";
		if (course_units != null && course_units.size() > 0) {
			for (CourseUnit unit : course_units) {
				String str = "";
				if (!CommonUtils.isNullString(result)) {
					str += ";";
				}
				str += unit.getTimeString();
				result += str;
			}
		}
		return result;
	}
	
	//暂时没用上
//	public List<String> getAllRooms() {
//		List<String> rooms = new ArrayList<String>();
//		if (course_units != null && course_units.size() > 0) {
//			for (CourseUnit courseUnit : course_units) {
//				if (!ObjectUtils.containsElement(rooms, courseUnit.room)
//						&& courseUnit.room != null) {
//					rooms.add(courseUnit.room);
//				}
//			}
//		}
//		return rooms;
//	}
	
	//暂时没用上
//	public static boolean isConflict(List<CourseUnit> units) {
//		boolean conflict = false;
//		for (int i = 0; i < units.size(); i++) {
//			for (int j = i + 1; j < units.size(); j++) {
//				CourseUnit t1 = units.get(i);
//				CourseUnit t2 = units.get(j);
//				if (t1.day_of_week == t2.day_of_week
//						&& t1.time_slots == t2.time_slots
//						&& t1.weeks == t2.weeks) {
//					conflict = true;
//					break;
//				}
//			}
//		}
//		return conflict;
//	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Course) {
			if (((Course) o).id == this.id && ObjectUtils.nullSafeEquals(((Course) o).teacher, this.teacher) && ObjectUtils.nullSafeEquals(((Course) o).course_units, this.course_units)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + ", teacher=" + teacher
				+ ", description=" + description + ", start_week=" + start_week
				+ ", end_week=" + end_week + ", school_id=" + school_id
				+ ", school_name=" + school_name + ", course_type="
				+ course_type + ", semester_id=" + semester_id + ", course_times_string="
				+ course_times_string + ", students_count=" + students_count
				+  ", course_units=" + course_units + "]";
	}

//	private List<List<CourseTime>> get_time_array(List<CourseTime> course_times) {
//		List<List<CourseTime>> time_array = new ArrayList<List<CourseTime>>();
//		for (int i = 0; i < course_times.size(); i++) {
//			CourseTime course_time = course_times.get(i);
//			boolean same = false;
//			for (List<CourseTime> list : time_array) {
//				CourseTime t = list.get(0);
//				if (t.occurance == course_time.occurance
//						&& t.day_of_week == course_time.day_of_week
//						&& ObjectUtils.nullSafeEquals(t.room, course_time.room)) {
//					list.add(course_time);
//					same = true;
//				}
//			}
//			if (!same) {
//				List<CourseTime> courseTimes = new ArrayList<CourseTime>();
//				courseTimes.add(course_time);
//				time_array.add(courseTimes);
//			}
//		}
//		Collections.sort(time_array, new Comparator<List<CourseTime>>() {
//
//			@Override
//			public int compare(List<CourseTime> lhs, List<CourseTime> rhs) {
//				CourseTime t1 = lhs.get(0);
//				CourseTime t2 = rhs.get(0);
//				return t1.day_of_week - t2.day_of_week;
//			}
//
//		});
//		return time_array;
//	}

//	private List<Integer> get_orderd_slot_array(List<CourseTime> courseTimes) {
//		List<Integer> slot_array = new ArrayList<Integer>();
//		for (CourseTime t : courseTimes) {
//			slot_array.add(t.time_slot);
//		}
//		Collections.sort(slot_array, new Comparator<Integer>() {
//
//			@Override
//			public int compare(Integer lhs, Integer rhs) {
//				return lhs.compareTo(rhs);
//			}
//
//		});
//		return slot_array;
//	}
//
//	private List<List<Integer>> get_continuous_slots(
//			List<CourseTime> courseTimes) {
//		List<List<Integer>> result = new ArrayList<List<Integer>>();
//		List<Integer> orderd = get_orderd_slot_array(courseTimes);
//		for (Integer slot : orderd) {
//			boolean added = false;
//			for (List<Integer> array : result) {
//				if (slot.equals(array.get(array.size() - 1) + 1)) {
//					array.add(slot);
//					added = true;
//				}
//			}
//			if (!added) {
//				List<Integer> array = new ArrayList<Integer>();
//				array.add(slot);
//				result.add(array);
//			}
//		}
//		return result;
//	}
	


//	public String getTimeString(boolean showRoom) {
//		Map<Integer, String> occurances = new HashMap<Integer, String>();
//		occurances.put(CourseTime.EVERY_WEEK, "每");
//		occurances.put(CourseTime.ODD_WEEKS, "单");
//		occurances.put(CourseTime.EVEN_WEEKS, "双");
//
//		String result = "";
//		if (course_times != null && course_times.size() > 0) {
//			List<List<CourseTime>> time_array = get_time_array(course_times);
//			for (List<CourseTime> array : time_array) {
//				CourseTime courseTime = array.get(0);
//				String str = "" + start_week + "~" + end_week + "周 "
//						+ occurances.get(courseTime.occurance) + "周"
//						+ Const.WEEKS[courseTime.day_of_week - 1];
//				List<List<Integer>> slotArray = get_continuous_slots(array);
//				for (List<Integer> slots : slotArray) {
//					if (slots.size() > 1) {
//						str += "" + slots.get(0) + "~"
//								+ slots.get(slots.size() - 1) + ",";
//					} else {
//						if (slotArray.size() > 1) {
//							str += "" + slots.get(0) + ",";
//						} else {
//							str += "第" + slots.get(0) + ",";
//						}
//					}
//				}
//				str = str.substring(0, str.length() - 1);
//				str += "节";
//				if (showRoom) {
//					str += " " + courseTime.room;
//				}
//				str += "\n";
//				result += str;
//			}
//		}
//		result = result.substring(0, result.length() - 1);
//		return result;
//	}

}

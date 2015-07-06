package fm.jihua.kecheng.rest.entities;

import java.util.Arrays;

public class AddCoursesResult extends BaseResult {
	
	public int num_courses_added;
	public Calendar calendar;
	public Course[] courses;
	@Override
	public String toString() {
		return "AddCoursesResult [num_courses_added=" + num_courses_added + ", calendar=" + calendar + ", courses=" + Arrays.toString(courses) + "]";
	}
	
	
	

}

package fm.jihua.kecheng.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import fm.jihua.kecheng.utils.CourseHelper;

public class CourseHelperTest extends TestCase{
	
	List<Course> courses = new ArrayList<Course>();
	List<CourseBlock> courseblocks = new ArrayList<CourseBlock>();
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		List<CourseUnit> courseunits1 = new ArrayList<CourseUnit>();
		courseunits1.add(new CourseUnit(1, "1-2", "room", "1-17"));
		courseunits1.add(new CourseUnit(2, "1-2", "room", "1-17"));
		Course course1 = new Course(1, "数学", "teacher");
		course1.course_units = courseunits1;
		courses.add(course1);
		
		List<CourseUnit> courseunits2 = new ArrayList<CourseUnit>();
		courseunits2.add(new CourseUnit(3, "7-8", "room", "1-17"));
		courseunits2.add(new CourseUnit(4, "9-10", "room", "1-17"));
		Course course2 = new Course(1, "英语", "teacher");
		course2.course_units = courseunits2;
		courses.add(course2);
		
		List<CourseUnit> courseunits3 = new ArrayList<CourseUnit>();
		courseunits3.add(new CourseUnit(5, "4-5", "room", "1-17"));
		courseunits3.add(new CourseUnit(6, "14-17", "room", "1-17"));
		Course course3 = new Course(1, "测试", "teacher");
		course3.course_units = courseunits3;
		courses.add(course3);
		
		courseblocks = CourseHelper.getFullCourseBlocks(courses, 2);
	}
	
	public void testGetMaxTimeSlot(){
		int maxSlot = CourseHelper.getMaxTimeSlot(courseblocks);
		assertEquals(17, maxSlot);
	}

	public void testGetTimeSlot(){
		Date curDate = new Date(System.currentTimeMillis());
		App.mTimeSlotLength = 12;
		curDate.setHours(10);
		int result = CourseHelper.getTimeSlot(curDate);
		assertEquals(3, result);
		curDate.setHours(19);
		int result2 = CourseHelper.getTimeSlot(curDate);
		assertEquals(12, result2);
		curDate.setHours(22);
		int result3 = CourseHelper.getTimeSlot(curDate);
		assertEquals(12, result3);
	}
	
	public void testGetFirstDayOfWeek(){
		Date curDate = new Date(CourseHelper.getFirstDayOfWeek(Date.UTC(2013-1900, 8-1, 22, 0, 0, 0)));
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = sDateFormat.format(curDate); 
		assertEquals("2013-08-18", date);
	}
}

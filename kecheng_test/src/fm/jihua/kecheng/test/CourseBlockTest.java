package fm.jihua.kecheng.test;

import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import junit.framework.TestCase;

public class CourseBlockTest extends TestCase{
	
	//当只有CourseBlock的start_slot或者end_slot不同时，才会合并CourseBlock，所以创建CourseBlock的CourseUnit应该只有time_slots不同
	public void testMerge(){
		CourseBlock block = getNewCourseBlock("1-2");
		assertEquals(true, block.merge(getNewCourseBlock("3-4")));
		assertEquals("1-4", block.getSlotString());
		
		block = getNewCourseBlock("3-4");
		assertEquals(true, block.merge(getNewCourseBlock("1-2")));
		assertEquals("1-4", block.getSlotString());
		
		block = getNewCourseBlock("1-4");
		assertEquals(true, block.merge(getNewCourseBlock("2-3")));
		assertEquals("1-4", block.getSlotString());
		
		block = getNewCourseBlock("2-3");
		assertEquals(true, block.merge(getNewCourseBlock("1-4")));
		assertEquals("1-4", block.getSlotString());
		
		block = getNewCourseBlock("1-3");
		assertEquals(true, block.merge(getNewCourseBlock("2-5")));
		assertEquals("1-5", block.getSlotString());
		
		block = getNewCourseBlock("2-5");
		assertEquals(true, block.merge(getNewCourseBlock("1-3")));
		assertEquals("1-5", block.getSlotString());
		
		block = getNewCourseBlock("1-2");
		assertEquals(false, block.merge(getNewCourseBlock("4-5")));
		assertEquals("1-2", block.getSlotString());
		
		block = getNewCourseBlock("4-5");
		assertEquals(false, block.merge(getNewCourseBlock("1-2")));
		assertEquals("4-5", block.getSlotString());
		
		block = getNewCourseBlock("1-2");
		assertEquals(true, block.merge(getNewCourseBlock("3")));
		assertEquals("1-3", block.getSlotString());
		
		block = getNewCourseBlock("3");
		assertEquals(true, block.merge(getNewCourseBlock("1-2")));
		assertEquals("1-3", block.getSlotString());
		
		block = getNewCourseBlock("1");
		assertEquals(true, block.merge(getNewCourseBlock("2-3")));
		assertEquals("1-3", block.getSlotString());
		
		block = getNewCourseBlock("2-3");
		assertEquals(true, block.merge(getNewCourseBlock("1")));
		assertEquals("1-3", block.getSlotString());
		
		block = getNewCourseBlock("1-2");
		assertEquals(false, block.merge(getNewCourseBlock("4")));
		assertEquals("1-2", block.getSlotString());
		
		block = getNewCourseBlock("1-3");
		assertEquals(true, block.merge(getNewCourseBlock("2")));
		assertEquals("1-3", block.getSlotString());
		
		block = getNewCourseBlock("2");
		assertEquals(true, block.merge(getNewCourseBlock("1-3")));
		assertEquals("1-3", block.getSlotString());
		
		block = getNewCourseBlock("4");
		assertEquals(false, block.merge(getNewCourseBlock("1-2")));
		assertEquals("4", block.getSlotString());
		
		block = getNewCourseBlock("1");
		assertEquals(true, block.merge(getNewCourseBlock("1")));
		assertEquals("1", block.getSlotString());
		
		block = getNewCourseBlock("1-2");
		assertEquals(true, block.merge(getNewCourseBlock("1-2")));
		assertEquals("1-2", block.getSlotString());
		
		//courseName不同不合并
		block = getNewCourseBlock("数学", "teacher", 1, "1-2", "room", "1-17");
		assertEquals(false, block.merge(getNewCourseBlock("英语", "teacher", 1, "1-3", "room", "1-17")));
		assertEquals("1-2", block.getSlotString());
		
		// dayOfWeek不同不合并
		block = getNewCourseBlock("数学", "teacher", 1, "1-2", "room", "1-17");
		assertEquals(false, block.merge(getNewCourseBlock("数学", "teacher", 2, "1-3", "room", "1-17")));
		assertEquals("1-2", block.getSlotString());
		
		// room不同不合并
		block = getNewCourseBlock("数学", "teacher", 1, "1-2", "room", "1-17");
		assertEquals(false, block.merge(getNewCourseBlock("数学", "teacher", 1, "1-3", "rooooooom", "1-17")));
		assertEquals("1-2", block.getSlotString());
		
		// weeks不同不合并
		block = getNewCourseBlock("数学", "teacher", 1, "1-2", "room", "1-17");
		assertEquals(false, block.merge(getNewCourseBlock("数学", "teacher", 1, "1-3", "room", "1-18")));
		assertEquals("1-2", block.getSlotString());
	}
	
	public void testIsActive(){
		String weeks1 = "1-17";
		String weeks2 = "1-3,5-7,9,12-15";
		
		boolean result1 = CourseBlock.isActive(3, weeks1);
		boolean result2 = CourseBlock.isActive(18, weeks1);
		boolean result3 = CourseBlock.isActive(9, weeks2);
		boolean result4 = CourseBlock.isActive(6, weeks2);
		boolean result5 = CourseBlock.isActive(10, weeks2);
		
		assertEquals(true, result1);
		assertEquals(false, result2);
		assertEquals(true, result3);
		assertEquals(true, result4);
		assertEquals(false, result5);
	}
	
	
	
	/**
	 * 下面的为测试辅助方法
	 */
	
	//生成只有start_slot或者end_slot不同的CourseBlock
	public CourseBlock getNewCourseBlock(String timeslots){
		return getNewCourseBlock("数学", "teacher", 1, timeslots, "room", "1-17");
	}
	
	public CourseBlock getNewCourseBlock(String courseName, String teacher, int dayOfWeek, String timeslots, String room, String weeks){
		Course course = new Course(0, courseName, teacher);
		CourseUnit unit = new CourseUnit(dayOfWeek, timeslots, room, weeks);
		return new CourseBlock(course, unit, unit.time_slots);
	}
}

package fm.jihua.kecheng.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import fm.jihua.kecheng.rest.entities.CourseUnit;

public class CourseUnitTest extends TestCase{
	
	List<CourseUnit> courseunits = new ArrayList<CourseUnit>();
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		courseunits.add(new CourseUnit(1, "1-2", "room", "1-17"));
		courseunits.add(new CourseUnit(2, "1-2", "room", "1-17"));
	}
	
	public void testMergeToCourseUnits(){
		List<CourseUnit> units = new ArrayList<CourseUnit>();
		units.add(new CourseUnit(1, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-2", "room", "1-19"));   //list中第二个为需要merge的CourseUnit
		assertEquals(units, getNewCourseUnit("6-19").mergeToCourseUnits(new ArrayList<CourseUnit>(courseunits)));
		
		
		units.clear();
		units.add(new CourseUnit(1, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-2", "room", "1-17,19-24"));
		assertEquals(units, getNewCourseUnit("19-24").mergeToCourseUnits(new ArrayList<CourseUnit>(courseunits)));
		
		units.clear();
		units.add(new CourseUnit(1, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-2", "room", "1-24"));
		assertEquals(units, getNewCourseUnit("18-24").mergeToCourseUnits(new ArrayList<CourseUnit>(courseunits)));
		
		units.clear();
		units.add(new CourseUnit(1, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-2", "room", "1-17"));
		assertEquals(units, getNewCourseUnit("8").mergeToCourseUnits(new ArrayList<CourseUnit>(courseunits)));
		
		//dayOfWeek不同不合并
		units.clear();
		units.add(new CourseUnit(1, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-2", "room", "1-17"));
		units.add(new CourseUnit(3, "1-2", "room", "8"));
		assertEquals(units, getNewCourseUnit(3, "1-2", "room", "8").mergeToCourseUnits(new ArrayList<CourseUnit>(courseunits)));
		
		//room不同不合并
		units.clear();
		units.add(new CourseUnit(1, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-2", "xxx", "8"));
		assertEquals(units, getNewCourseUnit(2, "1-2", "xxx", "8").mergeToCourseUnits(new ArrayList<CourseUnit>(courseunits)));
		
		//timeslotes不同不合并
		units.clear();
		units.add(new CourseUnit(1, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-2", "room", "1-17"));
		units.add(new CourseUnit(2, "1-3", "room", "8"));
		assertEquals(units, getNewCourseUnit(2, "1-3", "room", "8").mergeToCourseUnits(new ArrayList<CourseUnit>(courseunits)));
	}
	
	public void testMergeString(){
		assertEquals("1-3,5,7-8", CourseUnit.mergeString("5", "1-3,5,7-8"));
		assertEquals("1-8", CourseUnit.mergeString("5", "1-8"));
		assertEquals("5,9-12", CourseUnit.mergeString("5", "9-12"));
		assertEquals("1-12", CourseUnit.mergeString("1-8", "9-12"));
		assertEquals("1-12", CourseUnit.mergeString("1-8", "6-12"));
		assertEquals("1-8", CourseUnit.mergeString("1-8", "1-8"));
		assertEquals("5", CourseUnit.mergeString("5", "5"));
		assertEquals("1-9", CourseUnit.mergeString("4-5,6-9", "1-3,5,7-8"));
	}
	
	public void testIsStringConflict(){
		assertEquals(true, CourseUnit.isStringConflict("5", "1-3,5,7-8"));
		assertEquals(true, CourseUnit.isStringConflict("1-3,5,7-8", "5"));
		assertEquals(false, CourseUnit.isStringConflict("4", "1-3,5,7-8"));
		assertEquals(false, CourseUnit.isStringConflict("1-3,5,7-8", "4"));
		assertEquals(true, CourseUnit.isStringConflict("5", "1-8"));
		assertEquals(true, CourseUnit.isStringConflict("1-8", "5"));
		assertEquals(false, CourseUnit.isStringConflict("5", "9-12"));
		assertEquals(false, CourseUnit.isStringConflict("9-12", "5"));
		assertEquals(false, CourseUnit.isStringConflict("1-8", "9-12"));
		assertEquals(false, CourseUnit.isStringConflict("9-12", "1-8"));
		assertEquals(true, CourseUnit.isStringConflict("5", "5"));
		assertEquals(true, CourseUnit.isStringConflict("5-6", "5-6"));
		assertEquals(true, CourseUnit.isStringConflict("2,5-6", "1,5-6"));
		assertEquals(true, CourseUnit.isStringConflict("1-4", "3-6"));
	}
	
	public void testGetWeekStringFromList(){
		assertEquals("3,5,7", CourseUnit.getWeekStringFromList(Arrays.asList(new Integer[]{3,5,7})));
		assertEquals("1-3,5,7-9", CourseUnit.getWeekStringFromList(Arrays.asList(new Integer[]{1,2,3,5,7,8,9})));
		assertEquals("1-8", CourseUnit.getWeekStringFromList(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8})));
		assertEquals("3", CourseUnit.getWeekStringFromList(Arrays.asList(new Integer[]{3})));
		assertEquals("1-3,5,7-8,10", CourseUnit.getWeekStringFromList(Arrays.asList(new Integer[]{1,2,3,5,7,8,10})));
	}
	
	public void testChangeStringToList(){
		assertEquals(Arrays.asList(new Integer[]{1,3,5,7}), CourseUnit.changeStringToList("1,3,5,7"));
		assertEquals(Arrays.asList(new Integer[]{1,2,3,5,7,8}), CourseUnit.changeStringToList("1-3,5,7-8,7-8"));
		assertEquals(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8}), CourseUnit.changeStringToList("1-8"));
		assertEquals(Arrays.asList(new Integer[]{1}), CourseUnit.changeStringToList("1"));
		assertEquals(Arrays.asList(new Integer[]{1}), CourseUnit.changeStringToList("1,1,1,1"));
	}
	
	public void testGetWeekStringWithOccurance(){
		assertEquals("1-7周(单周)", CourseUnit.getWeekStringWithOccurance("1,3,5,7"));
		assertEquals("2-8周(双周)", CourseUnit.getWeekStringWithOccurance("2,4,6,8"));
		assertEquals("1,2,4周", CourseUnit.getWeekStringWithOccurance("1,2,4"));
		assertEquals("1-8周", CourseUnit.getWeekStringWithOccurance("1-8"));
		assertEquals("第1周", CourseUnit.getWeekStringWithOccurance("1-1"));
		assertEquals("第1周", CourseUnit.getWeekStringWithOccurance("1"));
	}
	
	
	
	/**
	 * 下面的为测试辅助方法
	 */
	
	//生成只有weeks不同的CourseUnit
	public CourseUnit getNewCourseUnit(String weeks){
		return getNewCourseUnit(2, "1-2", "room", weeks);
	}
	
	public CourseUnit getNewCourseUnit(int dayOfWeek, String timeslots, String room, String weeks){
		return new CourseUnit(dayOfWeek, timeslots, room, weeks);
	}

}

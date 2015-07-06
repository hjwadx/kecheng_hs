package fm.jihua.kecheng.utils;

import java.util.ArrayList;
import java.util.List;

import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.Event;

public class EventHelper {
	
	public static List<CourseBlock> getCourseBlocksFromEventList(List<Event> events, boolean without_overdue){
    	List<CourseBlock> result = new ArrayList<CourseBlock>();
    	for (Event event : events) {
    		if(!(without_overdue && event.isOverdue()) || event.getActive()){
    			if(!event.isOutOfNormalTime()){
            		result.addAll(getCourseBlocksFromEvent(event));
    			}
    		}
		}
    	return result;
    }
	
	public static List<CourseBlock> getCourseBlocksFromEventListOnlyThisWeek(List<Event> events){
    	List<CourseBlock> result = new ArrayList<CourseBlock>();
    	for (Event event : events) {
    		if(event.getActive() && !event.isOutOfNormalTime()){
        		result.addAll(getCourseBlocksFromEvent(event));
    		}
		}
    	return result;
    }
	
	public static List<CourseBlock> getCourseBlocksFromEvent(Event event){
		List<CourseBlock> result = new ArrayList<CourseBlock>();
		result.add(new CourseBlock(event, true));
    	return result;
    }
	
	public static List<CourseBlock> changeToCourseBlocks(List<Event> events) {
		List<CourseBlock> result = new ArrayList<CourseBlock>();
    	for (Event event : events) {
    		CourseBlock block = new CourseBlock(event, false);
    		block.active = true;
        	result.add(block);
		}
    	return result;
	}
}

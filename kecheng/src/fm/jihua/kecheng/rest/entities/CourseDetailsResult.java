package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class CourseDetailsResult extends BaseResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8902696744578410897L;
	public User[] students;
	public Note[] notes;
	public float average_score;
	public int num_ratings;
	public LinkedHashMap<String, Integer> tags;
	
	public CourseDetailsResult () {
		
	}

}

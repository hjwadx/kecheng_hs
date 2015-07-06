package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class Examination implements Serializable {
	private static final long serialVersionUID = -8837049493699876962L;
	public int id;
	public String name;
	public long time;
	public String room;
	public Course course;
	public Integer courseId;
	
	public Examination() {

	}

	public Examination(int id, String name, long time, String room) {
		this.id = id;
		this.name = name;
		this.time = time;
		this.room = room;
	}
	
	public Integer courseId(){
		if (course != null) {
			return course.id;
		}else {
			return courseId;
		}
	}
	
	public boolean isExpired(){
		return time < System.currentTimeMillis();
	}
	
	public boolean isExpired(long now){
		return time < now;
	}
}

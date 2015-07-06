package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

/**
 * @author danny
 *	请注意begin_time和end_time是的单位是秒，以服务器为准
 */
public class Semester implements Serializable {
	
	private static final long serialVersionUID = -4099188260523902063L;
	
	public int id;
	public int order;
	public String name;
	/**
	 * 单位是秒，以服务器为准
	 */
	public long begin_time;
	
	
	/**
	 * 单位是秒，以服务器为准
	 */
	public long end_time;
	/**
	 * need to modify
	 */
	public boolean modified;

	
	public Semester() {
		// for gson usage
	}
	
	public Semester(int _id, int _order, long _begin_time, long _end_time, String name){
		this.id = _id;
		this.order = _order;
		this.begin_time = _begin_time;
		this.end_time = _end_time;
		this.name = name;
	}
	
//	public static Semester currentSemester = new Semester(3, 1, new Date(2013-1900, 2-1, 25).getTime()/1000, new Date(2013-1900, 7-1, 31).getTime()/1000, "2013年春季学期");
}

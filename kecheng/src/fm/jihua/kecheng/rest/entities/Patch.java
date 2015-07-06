package fm.jihua.kecheng.rest.entities;

import android.database.Cursor;

public class Patch {
	public int id;
	public int courseId;
	public String key;
	public String value;
	public String day_and_slots;
	public Patch(int id, int courseId, String key, String value, String day_and_slots) {
		super();
		this.id = id;
		this.courseId = courseId;
		this.key = key;
		this.value = value;
		this.day_and_slots = day_and_slots;
	}
	
	public Patch(Cursor cursor){
		this(cursor.getInt(0),cursor.getInt(1), cursor.getString(2),cursor.getString(3), cursor.getString(4));
	}
	
}

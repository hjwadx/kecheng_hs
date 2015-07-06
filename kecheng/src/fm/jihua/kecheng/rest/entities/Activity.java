package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;

public class Activity implements Serializable{
	private static final long serialVersionUID = 7548841010025060137L;
	
	public int id;
	public String activity_type;
	public User creator;
	public Object item;
	public String item_class;
	public String message;
	
	public Activity(){
		
	}

	public Activity(int id, String activity_type, User creator, Object item,
			String item_class, String message) {
		super();
		this.id = id;
		this.activity_type = activity_type;
		this.creator = creator;
		this.item = item;
		this.item_class = item_class;
		this.message = message;
	}
	
	
	
}

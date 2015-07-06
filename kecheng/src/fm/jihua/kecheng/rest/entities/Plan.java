package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;



public class Plan implements Serializable {

	private static final long serialVersionUID = 6098905102545841366L;
	
	public int id = 0;

	public String name;
	public int dayOfWeek;
	public int timeSlot;
	public int type;

	public Plan() {
		
	}

	public Plan(int id, String name, int type, int dayOfWeek, int timeSlot) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.dayOfWeek = dayOfWeek;
		this.timeSlot = timeSlot;
	}
	
}

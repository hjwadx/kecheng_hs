package fm.jihua.kecheng.rest.entities;

import java.util.HashMap;

public class Department {
	public int id;
	public String name;
	public Department() {
		// TODO Auto-generated constructor stub
	}
	
	public Department(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public HashMap<String, Object> toHashMap(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", name);
		return map;
	}
}

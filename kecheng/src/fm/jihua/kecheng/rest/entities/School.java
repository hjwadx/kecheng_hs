package fm.jihua.kecheng.rest.entities;

import java.util.HashMap;

public class School {
	public int id;
	public String name;
	public int county_id;
	public School() {
		// TODO Auto-generated constructor stub
	}
	
	public School(int id, String name, int county_id){
		this.id = id;
		this.name = name;
		this.county_id = county_id;
	}
	
	public HashMap<String, Object> toHashMap(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", name);
		map.put("county_id", county_id);
		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		School other = (School) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)) {
			return false;
		} else if (county_id != other.county_id){
			return false;
		}
		return true;
	}
}

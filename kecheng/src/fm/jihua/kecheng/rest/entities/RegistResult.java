package fm.jihua.kecheng.rest.entities;

import java.util.Map;

public class RegistResult {
	public User user;
	public boolean success;
	public String kecheng_token;
	public String error;
	public String notice;
	public int school_time_slot;
	public Semester[] semesters;
	public int active_semester_id;
	public Map<String, String> config_params;
	
	public RegistResult() {
		
	}
}

package fm.jihua.kecheng.rest.entities;

import java.util.Arrays;

public class ImportCoursesResult extends BaseResult {
	public String session_id;
	public String captcha;
	public Course[] courses;
	public boolean support = true;
	public String code;
	@Override
	public String toString() {
		return "ImportCoursesResult [session_id=" + session_id + ", captcha=" + captcha + ", courses=" + Arrays.toString(courses) + ", support=" + support + "]";
	}
	
	
}

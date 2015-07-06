package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;

import android.net.Uri;
import fm.jihua.kecheng.utils.Const;

public class User extends fm.jihua.chat.User implements Serializable {

	private static final long serialVersionUID = 2955638011832610698L;

	public String department;
	public String school;
	public String renren_token;
	public String renren_name;
	public String weibo_name;
	public String email;
	public String password;
	public String android_imei;
	public String tiny_avatar_url;
	public String origin_avatar_url;
	public String background;
	public int gezi_id;
	public int signup_from = 2;
	public int grade;
	public String city;
	/**
	 * 性别
	 * 女为0，男为1
	 */
	public int sex;
	public long renren_id;
	public String token;
	public Integer active_semester_id;
	
	public String birthday;
	public String astrology_sign;
	public String signature;
	public String pursuing_degree;
	public String career_goal;
	public String[] life_situations;
	public String relationship_status;
	public String dormitory;
	
	public String tencent_token;
	public String tencent_id;
	public String weibo_token;
	public String weibo_id;
	public String tencent_name;
	
	
	/**
	 * 是否显示生日
	 * 显示为1，不显示为3
	 */
	public int birthday_visibility;
//	public int age;

	public User() {
	}
	
	public User(int id, String name, String avatar, String school, String department, int grade, long renren_id, String bigAvatar, int sex, int gezi_id, 
			String background, String birthday, String astrology_sign, String signature, String pursuing_degree, String career_goal, String life_situationsString, String relationship_status, int birthday_visibility, String dormitory, String city){
		this.id = id;
		this.name = name;
		this.tiny_avatar_url = avatar;
		this.origin_avatar_url = (bigAvatar == null || bigAvatar.length() == 0) ? avatar : bigAvatar;
		this.school = school;
		this.department = department;
		this.grade = grade;
		this.sex = sex;
		this.renren_id = renren_id;
		this.gezi_id = gezi_id;
		this.background = background;
		
		this.birthday = birthday;
		this.birthday_visibility = birthday_visibility;
		this.astrology_sign = astrology_sign;
		this.signature = signature;
		this.pursuing_degree = pursuing_degree;
		this.career_goal = career_goal;
		this.relationship_status = relationship_status;
		this.life_situations = getLifeSituationsArray(life_situationsString);
		this.dormitory = dormitory;
		this.city = city;
	}
	
	public String[] getLifeSituationsArray(String str){
		if (str != null) {
			String []array = str.split(",");
			return array;
		}else {
			return new String[0];
		}
	}
	
	public String getStringFromArrayString (String[] array) {
		String string = "";
		if (array != null && array.length > 0) {
			for (String str:array) {				
				string += str + ",";
			}
		} else {
			string = null;
		}
		return string;
		
	}

	public String getJID(){
		return String.valueOf(this.id) + "@" + Const.CHAT_HOST;
	}
	
	public Object[] getObjects(){
		return new Object[]{this.id, this.name, this.tiny_avatar_url, this.school, this.department, this.grade, this.renren_id, this.origin_avatar_url, this.sex, this.gezi_id, this.background}; 
	}
	
	public Object[] getObjectsf(){
		return new Object[]{this.id, this.name, this.tiny_avatar_url, this.school, this.department, 
				this.grade, this.renren_id, this.origin_avatar_url, this.sex, this.gezi_id, this.background,
				this.birthday, this.astrology_sign, this.signature, this.pursuing_degree, this.career_goal, getStringFromArrayString(this.life_situations), this.relationship_status, this.birthday_visibility, this.dormitory, this.city}; 
	}
	
	public Object[] getObjectsExist(){
		return new Object[]{this.name, this.tiny_avatar_url, this.school, this.department, 
				this.grade, this.renren_id, this.origin_avatar_url, this.sex, this.gezi_id, this.background,
				this.birthday, this.astrology_sign, this.signature, this.pursuing_degree, this.career_goal, getStringFromArrayString(this.life_situations), this.relationship_status, this.birthday_visibility, this.dormitory, this.city, this.id}; 
	}
	
	public Uri getJabberUri(){
		return Uri.parse("xmpp:" + getJID());
	}
	
	public String getAvatarId(){
		return String.valueOf(id) + Const.AVATAR_CACHE_TINY_SUFFIX;
	}
	
	public static User fromJID(String jid){
		return fromId(Integer.valueOf(jid.split("@")[0]));
	}
	
	public static User fromJabberUri(Uri uri){
		return fromJID(uri.getSchemeSpecificPart());
	}
	
	public static User fromId(int _id){
		User user = new User();
		user.id = _id;
		return user;
	}

	public static int getYearFromGrade(String grade) {
		int index = 0;
		int year;
		String gradeArray[] = new String[] {"初一", "初二", "初三", "高一", "高二", "高三"};
		index = Arrays.asList(gradeArray).indexOf(grade);
		index = Math.max(0, index);
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR) - index;
		if(calendar.get(Calendar.MARCH) < 6){
			year-=1;
		}
		return year;
	}
	
	public static String getGradeFromYear(int year) {
		int index = 0;
		String gradeArray[] = new String[] {"初一", "初二", "初三", "高一", "高二", "高三"};
		Calendar calendar = Calendar.getInstance();
		index = calendar.get(Calendar.YEAR) - year;
		if(calendar.get(Calendar.MARCH) < 6){
			index-=1;
		}
		index = Math.max(0, index);
		index = Math.min(index, 5);
		return Arrays.asList(gradeArray).get(index);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			if(((User)o).id == this.id){
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}

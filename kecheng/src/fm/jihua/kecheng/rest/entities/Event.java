package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;

public class Event implements Serializable{

	private static final long serialVersionUID = 273878802745435090L;

	public int id = 0;

	public String name;
	public long start_time;
	public long end_time;
	public String city;
	public String province;
	public String location;
	public String organizer_name;
	public String content;
	public String poster_url;
	public String school;
	public String created_at;
	
	
	//new
	public String time_slots;
	
	public Event() {

	}
	
	public Event(int id, String name, long start_time, long end_time,
			String city, String province, String location,
			String organizer_name, String content, String poster_url,
			String time_slots, String school) {
		super();
		this.id = id;
		this.name = name;
		this.start_time = start_time;
		this.end_time = end_time;
		this.city = city;
		this.province = province;
		this.location = location;
		this.organizer_name = organizer_name;
		this.content = content;
		this.poster_url = poster_url;
		this.time_slots = time_slots;
		this.school = school;
	}
	
	
	
	public Event(int id, String name, long start_time, long end_time,
			String city, String province, String location,
			String organizer_name, String content, String poster_url,
			String created_at, String time_slots, String school) {
		super();
		this.id = id;
		this.name = name;
		this.start_time = start_time;
		this.end_time = end_time;
		this.city = city;
		this.province = province;
		this.location = location;
		this.organizer_name = organizer_name;
		this.content = content;
		this.poster_url = poster_url;
		this.created_at = created_at;
		this.time_slots = time_slots;
		this.school = school;
	}
	
	public String getTimeFromLong(long time){
		SimpleDateFormat dateFormat;
		String pattern="HH:mm";
		dateFormat=new SimpleDateFormat(pattern);
		return dateFormat.format(new Date(time));
	}
	
	public String getDateFromLong(long time){
		SimpleDateFormat dateFormat;
		String pattern="yyyy-MM-dd";
		dateFormat=new SimpleDateFormat(pattern);
		return dateFormat.format(new Date(time));
	}
	
	public int getDayOfWeek(){
		Date date = new Date(start_time * 1000);
		return date.getDay();
	}
	
	public boolean getActive(){
		Date date = new Date(start_time * 1000);
		return start_time * 1000 >= getMondayOfThisWeek() && start_time * 1000 < getMondayOfNextWeek();
	}
	
	public boolean isOverdue(){
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis() > end_time * 1000;
	}
	
	public static long getMondayOfThisWeek(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int dayOfWeek = CourseHelper.getWeekdayFromSystem(calendar.get(Calendar.DAY_OF_WEEK));
		calendar.add(Calendar.DATE, CourseHelper.getWeekdayFromSystem(Calendar.MONDAY)-dayOfWeek);
		return calendar.getTimeInMillis();
    }
	
	public static long getMondayOfNextWeek(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int dayOfWeek = CourseHelper.getWeekdayFromSystem(calendar.get(Calendar.DAY_OF_WEEK));
		calendar.add(Calendar.DATE, CourseHelper.getWeekdayFromSystem(Calendar.MONDAY)-dayOfWeek + 7);
		return calendar.getTimeInMillis();
    }
	
	
	public String getLocalString(){
		String local;
		local = CommonUtils.replaceNullString(this.city) + CommonUtils.replaceNullString(this.school) + CommonUtils.replaceNullString(this.location);
		return local;
	}
	
	public String getTimeString(){
		String timeString;
		String startDate = getDateFromLong(start_time * 1000);
		String endDate = getDateFromLong(end_time * 1000);
		if(startDate != null && startDate.equals(endDate)){
			timeString = startDate + " " + getDayOfWeekString() + " " + getTimeFromLong(start_time * 1000) + "-" + getTimeFromLong(end_time * 1000);
		} else {
			timeString = startDate + " " + getTimeFromLong(start_time * 1000) + "-" + endDate + " " +getTimeFromLong(end_time * 1000);
		}
		return timeString;
	}
	
	public String getDayOfWeekString(){
		Date date = new Date(start_time * 1000);
		return Const.WEEKS_XINGQI[CourseHelper.getWeekIndexFromDayOfWeek(date.getDay())];
	}


	@Override
	public boolean equals(Object o) {
		if (o instanceof Event) {
			Event b = (Event)o;
			return id == b.id && ObjectUtils.nullSafeEquals(name, b.name) && start_time == b.start_time && end_time == b.end_time && ObjectUtils.nullSafeEquals(city, b.city) 
					&& ObjectUtils.nullSafeEquals(province, b.province) && ObjectUtils.nullSafeEquals(location, b.location) && ObjectUtils.nullSafeEquals(organizer_name, b.organizer_name) 
					&& ObjectUtils.nullSafeEquals(content, b.content) && ObjectUtils.nullSafeEquals(poster_url, b.poster_url) && ObjectUtils.nullSafeEquals(time_slots, b.time_slots);
		}
		return false;
	}
	
	public boolean isOutOfNormalTime(){
		boolean result = false;
		List<List<String>> timeLists = App.getInstance().getTimeModeList();
		SimpleDateFormat dateFormat;
		String pattern="HH:mm";
		dateFormat=new SimpleDateFormat(pattern);
		List<String> slotsList = timeLists.get(0);
		Date date0 = new Date(start_time * 1000);
		Date date = null;
		try {
			date = dateFormat.parse(slotsList.get(0));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		date.setYear(date0.getYear());
		date.setMonth(date0.getMonth());
		date.setDate(date0.getDate());
		if(date.getTime() > end_time * 1000){
			result = true;
		}
		slotsList = timeLists.get(timeLists.size() - 1);
		try {
			date = dateFormat.parse(slotsList.get(1));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		date.setYear(date0.getYear());
		date.setMonth(date0.getMonth());
		date.setDate(date0.getDate());
		if(date.getTime() < start_time * 1000){
			result = true;
		}
		return result;
	}
	
	public String calculateTimeSlots(){
		String timeSlots = "";
		int start = 0;
		int end = 0;
		if(App.getInstance().getTimeModeList() == null || App.getInstance().getTimeModeList().size() == 0){
			return this.time_slots;
		}
		start = CourseHelper.calculateTimeSlot(start_time * 1000, false);
		end = CourseHelper.calculateTimeSlot(end_time * 1000, true);
		timeSlots = start + "-" +  end;
		return timeSlots;
	}
}

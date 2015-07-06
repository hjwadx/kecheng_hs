package fm.jihua.kecheng.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import fm.jihua.kecheng.AlarmTimeModeReceiver;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Event;

/**
 * @date 2013-7-9
 * @introduce 定时工具类
 */
public class AlarmManagerUtil {

	private static AlarmManagerUtil alarmManagerUtil;

	boolean isClassMute = false;
	boolean isBeforeClass = false;

	private AlarmManager am;

	public static final int TYPE_ClASS_BEFORE_CLASS = 100;
	public static final int TYPE_CLASS_MUTE_START = 200;
	public static final int TYPE_CLASS_MUTE_END = 300;
	public static final int TYPE_EVENT_BEFORE_CLASS = 400;
//	public static final int TYPE_EVENT_MUTE_START = 500;
	
	public static final String INTENT_CATEGORY = "intent_category";
	//class-->timeslot;event-->event_id
	public static final String INTENT_IMPORTANT_NUMBER = "intent_important_number";
	public static final String INTENT_IMPORTANT_ENTITY = "intent_important_entity";
	
	private int time_before;

	public static AlarmManagerUtil getInstance() {
		if (alarmManagerUtil == null) {
			alarmManagerUtil = new AlarmManagerUtil();
		}
		return alarmManagerUtil;
	}

	void initSettingOption() {
		Context context = App.getInstance();
		time_before = App.getInstance().getBeforeClassTime();
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
		isBeforeClass = setting.getBoolean(context.getString(R.string.time_mode_before_class), false);
		isClassMute = setting.getBoolean(context.getString(R.string.time_mode_class_mute), false);
	}
	
	public void resetAlarmByTimeMode() {
		initSettingOption();
		List<List<String>> listTimeFromJson = App.getInstance().getTimeModeList();
		for (int position = 0; position < listTimeFromJson.size(); position++) {
			List<String> list = listTimeFromJson.get(position);
			int timeSlot = position + 1;
			setAlarmByCategory(list, TYPE_ClASS_BEFORE_CLASS, timeSlot, isBeforeClass);
			setAlarmByCategory(list, TYPE_CLASS_MUTE_START, timeSlot, isClassMute);
			setAlarmByCategory(list, TYPE_CLASS_MUTE_END, timeSlot, isClassMute);
		}
	}
	
	public void resetAlarmByEvent(){
		List<Event> events = App.getInstance().getDBHelper().getEvents(App.getInstance().getUserDB());
		for (Event event : events) {
			startAlarmByEvent(event);
		}
	}
	
	public void startAlarmByEvent(Event event){
		initSettingOption();
		if(event.start_time != 0){
			if (isBeforeClass) {
				long alarmTimeByEvent = getAlarmTimeByEvent(event, TYPE_EVENT_BEFORE_CLASS);
				if (alarmTimeByEvent != -1) {
					scheduleAlarm(alarmTimeByEvent, TYPE_EVENT_BEFORE_CLASS, 0, false, event);
				}else{
					cancleAlarmByEvent(event);
				}
			}
//			if (isClassMute) {
//				long alarmTimeByEvent = getAlarmTimeByEvent(event, TYPE_EVENT_MUTE_START);
//				if (alarmTimeByEvent != -1) {
//					scheduleAlarm(getAlarmTimeByEvent(event, TYPE_EVENT_MUTE_START), TYPE_EVENT_MUTE_START, 0, false, event);
//				}else{
//					cancleAlarmByEvent(event);
//				}
//			}
		}
	}
	
	public void cancleAlarmByEvent(Event event){
		initSettingOption();
		cancel(TYPE_EVENT_BEFORE_CLASS, 0, event);
//		cancel(TYPE_EVENT_MUTE_START, 0, event);
	}

	private void setAlarmByCategory(List<String> itemTimeList, int category, int importantNumber, boolean open) {
		AppLogger.i(open+" "+importantNumber+" ~ "+category + "~ before:"+time_before + "  itemTimeList: "+itemTimeList.toString());
		if (open) {
			long time = getAlarmTimeByCourseList(itemTimeList, category);
			scheduleAlarm(time, category, importantNumber, true, null);
		} else {
			cancel(category, importantNumber, null);
		}
	}
	
	private PendingIntent getPendingIntent(int category, int importantNumber, Event event) {
		Context context = App.getInstance();
		Intent intent = new Intent(context, AlarmTimeModeReceiver.class);
		intent.putExtra(INTENT_CATEGORY, category);
		intent.putExtra(INTENT_IMPORTANT_NUMBER, importantNumber);
		intent.putExtra(INTENT_IMPORTANT_ENTITY, event);
		if (event != null) {
			importantNumber = event.id;
		}
		int requestCode = importantNumber + category;
		return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private void scheduleAlarm(long time, int category, int importantNumber, boolean isRepeat, Event event) {
		PendingIntent sender = getPendingIntent(category, importantNumber, event);
		if(isRepeat){
			am.setRepeating(AlarmManager.RTC_WAKEUP, time, 3600 * 24 * 1000, sender);
		}else{
			am.set(AlarmManager.RTC_WAKEUP, time, sender);
		}
	}

	private void cancel(int category, int importantNumber, Event event) {
		PendingIntent sender = getPendingIntent(category, importantNumber, event);
		am.cancel(sender);
	}
	
	@SuppressLint("SimpleDateFormat")
	private long getAlarmTimeByCourseList(List<String> itemTimeList, int category) {
		String timeString = "";
		int minuteDiff = 0;

		switch (category) {
		case TYPE_ClASS_BEFORE_CLASS:
			timeString = itemTimeList.get(0);
			minuteDiff = -time_before;
			break;
		case TYPE_CLASS_MUTE_START:
			timeString = itemTimeList.get(0);
			break;
		case TYPE_CLASS_MUTE_END:
			timeString = itemTimeList.get(1);
			break;
		default:
			break;
		}
		AppLogger.i("提醒时间  " + timeString);
		Date date = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		try {
			date = dateFormat.parse(timeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar old = Calendar.getInstance();
		old.setTime(date);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, old.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, old.get(Calendar.MINUTE) + minuteDiff);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
			calendar.add(Calendar.DATE, 1);
		}
		return calendar.getTimeInMillis();
	}
	
	private long getAlarmTimeByEvent(Event event, int category) {
		int minuteDiff = 0;

		if(category == TYPE_EVENT_BEFORE_CLASS){
			minuteDiff = -time_before;
		}
		
		long eventTime = event.start_time * 1000;
		
		eventTime = eventTime - minuteDiff * 60 * 1000;
		
		AppLogger.i("Event 提醒时间  " + eventTime + " category : " + category);
		if (eventTime < System.currentTimeMillis()) {
			return -1;
		}
		return eventTime;
	}
	
}

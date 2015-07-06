package fm.jihua.kecheng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.Examination;
import fm.jihua.kecheng.ui.activity.SplashActivity;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseBlockUtil;
import fm.jihua.kecheng_hs.R;

public class AlarmReceiver extends BroadcastReceiver {

	static int requestCode = Const.NOTIFICATION_REQUEST_ALARM;
	static int notifiCode = 0;
//	static int rollDate = 0;
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String notification_time = settings.getString(context.getString(R.string.notification_time), "");
		Calendar calendar = Calendar.getInstance();
		boolean tomorrow = false;
		if (notification_time.startsWith("-")) {
			calendar.add(Calendar.DATE, 1);
			tomorrow = true;
		}
		Log.d(Const.TAG, "AlarmReceiver receive");
//		List<CourseBlock> blocks = CourseBlockUtil.getInstance().getCourseBlocks(context, calendar.getTime());
//		//--todo 添加上活动的数量
//		List<Event> eventListByTime = App.getInstance().getDBHelper().getEventListByTime(App.getInstance().getUserDB(), calendar.getTimeInMillis());
//		if (blocks.size() > 0 || eventListByTime.size() > 0 || Const.IS_TEST_ENV) {
//			String day = tomorrow ? "明天" : "今天";
//			String message = day;
//			if(eventListByTime.size()>0){
//				message += ",有"+eventListByTime.size()+"个要参加的活动";
//			}
//			if(blocks.size() > 0){
//				message += ",有"+blocks.size()+"门要上的课";
//			}
//			sendNotification(context, message);
//		}
		List<Examination> examinations = getExaminations(context, calendar.getTime());
		if (examinations.size() > 0 || Const.IS_TEST_ENV) {
			String day = tomorrow ? "明天" : "今天";
			String message = day+"你有"+examinations.size()+"门考试";
			Notification notif = new Notification(App.getInstance().getIcon(), message, System.currentTimeMillis());
			notif.flags|=Notification.FLAG_AUTO_CANCEL;//|Notification.FLAG_INSISTENT;
			Intent intent = new Intent(context, SplashActivity.class);
			intent.putExtra("TO_EXAMINATION", true);
			PendingIntent contentIntent = PendingIntent.getActivity(context, Const.NOTIFICATION_REQUEST_EXAMINATION_ALARM, intent, PendingIntent.FLAG_ONE_SHOT);
			notif.setLatestEventInfo(context, "课程格子", message, contentIntent);
			sendNotification(context, notif);
		}
	}
	
	 
	private List<Examination> getExaminations(Context context, Date dt) {
		App app = (App) context.getApplicationContext();
		List<Examination> result = new ArrayList<Examination>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		List<Examination> examinations = app.getDBHelper().getExaminations(
				app.getUserDB());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		long begin = calendar.getTimeInMillis();
		long end = begin + 24 * 3600 * 1000;
		for (int i = 0; i < examinations.size(); i++) {
			if (examinations.get(i).time < end
					&& examinations.get(i).time >= begin) {
				result.add(examinations.get(i));
			}
		}
		return result;
	}

	public void sendNotification(Context context, Notification notif) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String ringtoneStr = settings.getString(
				App.NOTIFICATION_SOUND_COURSE_WARN_KEY,
				Settings.System.DEFAULT_NOTIFICATION_URI.toString());
		if (settings
				.getBoolean(context.getString(R.string.notification_sound_enable), true)){
			notif.sound = Uri.parse(ringtoneStr);
		}else {
			notif.defaults = 0;
		}
		if (settings
				.getBoolean(App.NOTIFICATION_VIBRATE_KEY, true))
			notif.defaults |= Notification.DEFAULT_VIBRATE;
		notif.ledARGB = 0xff0000ff; // Blue color
		notif.ledOnMS = 1000;
		notif.ledOffMS = 1000;
		notif.defaults |= Notification.DEFAULT_LIGHTS;
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notifiCode++, notif);
	}
	
	public void sendNotification(Context context, String message){
		Notification notif = new Notification(App.getInstance().getIcon(), message, System.currentTimeMillis());
		notif.flags|=Notification.FLAG_AUTO_CANCEL;//|Notification.FLAG_INSISTENT;
		Intent intent = new Intent(context, SplashActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
		notif.setLatestEventInfo(context, "课程格子", message, contentIntent);
		sendNotification(context, notif);
	}
	
	public static void schedule(Context context) {
		cancel(context);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String notification_time = settings.getString(context.getString(R.string.notification_time), "");
		boolean shouldNotificate = settings.getBoolean(context.getString(R.string.notification), false);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (shouldNotificate) {
			String timeText;
			if (notification_time.startsWith("-")) {
				timeText = notification_time.substring(1);
			}else {
				timeText = notification_time;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
			long time = 0;
			try {
				time = sdf.parse(timeText).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			time += (System.currentTimeMillis()+calendar.get(Calendar.ZONE_OFFSET ))/(24*3600*1000)*(24*3600*1000);
			if (time <= System.currentTimeMillis()) {
				time += 24*3600*1000;
			}
			Intent intent = new Intent(context, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
//			int alarmHour = Integer.parseInt(notification_time);
			Log.d(Const.TAG, "AlarmReceiver schedule at"+new Date(time).toLocaleString());
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, time, 3600*24*1000, sender);
//			alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30*1000, sender);
		}
	}
	
	public static void cancel(Context context){
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
		alarm.cancel(sender);
	}
}

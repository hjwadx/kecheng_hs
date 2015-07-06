package fm.jihua.kecheng;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;
import fm.jihua.kecheng_hs.R;

@SuppressLint("CommitPrefEdits")
public class WakeupReceiver extends BroadcastReceiver {

	static int requestCode = Const.NOTIFICATION_REQUEST_WAKEUP;
	static String TAG = "WakeupReceiver"; 
//	static int rollDate = 0;
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		long wakeupTime = settings.getLong(context.getString(R.string.wakeup_time), 0);
		String notification_time = settings.getString(context.getString(R.string.notification_time), "");
		if (notification_time.startsWith("-")) {
			wakeupTime += -24*3600*1000;
		}
		Log.d(TAG, "WakeupReceiver receive");
		if (System.currentTimeMillis() >= wakeupTime) {
			Log.d(TAG, "WakeupReceiver set vacation flag");
			Editor editor = settings.edit();
			editor.putBoolean(context.getString(R.string.vacation), false);
			App.commitEditor(editor);
		}else {
			schedule(context);
		}
	}
	
	public static long getNextWeekTime(Calendar calendar){
		calendar.add(Calendar.DATE, 7);
		return CourseHelper.getFirstDayOfWeek(calendar.getTimeInMillis());
    }
	
	public static void schedule(Context context) {
		cancel(context);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		long nextWakeTime = settings.getLong(context.getString(R.string.wakeup_time), 0);
		boolean isOnVacation = settings.getBoolean(context.getString(R.string.vacation), false);
		if (isOnVacation && nextWakeTime > new Date().getTime()) {
			Intent intent = new Intent(context, WakeupReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
			String notification_time = settings.getString(context.getString(R.string.notification_time), "");
			if (notification_time.startsWith("-")) {
				nextWakeTime -= +24*3600*1000;
			}
			// wakeup before next alarm
			Log.d(TAG, "WakeupReceiver schedule at " + new Date(nextWakeTime).toLocaleString());
			alarm.set(AlarmManager.RTC_WAKEUP, nextWakeTime, sender);
		}
	}
	
	public static void cancel(Context context){
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, WakeupReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
		alarm.cancel(sender);
	}
}

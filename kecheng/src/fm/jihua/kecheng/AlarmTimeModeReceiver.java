package fm.jihua.kecheng;

import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import fm.jihua.common.ui.helper.Hint;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.ui.activity.EventActivity;
import fm.jihua.kecheng.ui.activity.SplashActivity;
import fm.jihua.kecheng.utils.AlarmManagerUtil;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseBlockUtil;
import fm.jihua.kecheng_hs.R;

public class AlarmTimeModeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int type = intent.getIntExtra(AlarmManagerUtil.INTENT_CATEGORY, -1);
		int importantNumber = intent.getIntExtra(AlarmManagerUtil.INTENT_IMPORTANT_NUMBER, -1);
		String message = null;

		switch (type) {
		case AlarmManagerUtil.TYPE_ClASS_BEFORE_CLASS:
			CourseBlock courseBlock = getUseFulCourses(context, importantNumber);
			if (courseBlock != null && importantNumber == courseBlock.start_slot) {
				message = String.format(context.getResources().getString(R.string.time_mode_string_before_class), courseBlock.name, String.valueOf(App.getInstance().getBeforeClassTime()));
			}
			break;
		case AlarmManagerUtil.TYPE_CLASS_MUTE_START:
			if (getUseFulCourses(context, importantNumber) != null) {
				SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
				boolean isRemindMuteBeforeClass = setting.getBoolean(context.getString(R.string.remind_mute_before_class), false);
				if (isRemindMuteBeforeClass) {
					message = context.getResources().getString(R.string.time_mode_string_mute_start);
				}
				setSlientMode(true, context);
			}
			break;
		case AlarmManagerUtil.TYPE_CLASS_MUTE_END:
			if (getUseFulCourses(context, importantNumber) != null) {
				message = context.getResources().getString(R.string.time_mode_string_mute_end);
				setSlientMode(false, context);
			}
			break;
		case AlarmManagerUtil.TYPE_EVENT_BEFORE_CLASS:
			//--todo 活动 提前提醒
			// -- 点击可以跳转到活动详情页面
			Event event = (Event) intent.getSerializableExtra(AlarmManagerUtil.INTENT_IMPORTANT_ENTITY);
			if(event != null){
				message = String.format(context.getResources().getString(R.string.time_mode_string_before_class_event), event.name, String.valueOf(App.getInstance().getBeforeClassTime()));
			}
			break;
//		case AlarmManagerUtil.TYPE_EVENT_MUTE_START:
//			//--todo 活动开始提醒
//			// -- 点击可以跳转到活动详情页面
//			message = "活动 开始提醒";
//			break;
		}
		
		if(!TextUtils.isEmpty(message)){
			sendNotification(context, message, type, intent);
		}

	}

	private CourseBlock getUseFulCourses(Context context, int importantNumber) {
		List<CourseBlock> courseBlocks = CourseBlockUtil.getInstance().getCourseBlocks(context, Calendar.getInstance().getTime());
		if (!App.getInstance().isVacation() && courseBlocks.size() > 0) {
			for (CourseBlock courseBlock : courseBlocks) {
				if (courseBlock.active && importantNumber >= courseBlock.start_slot && importantNumber <= courseBlock.end_slot) {
					return courseBlock;
				}
			}
		}
		return null;
	}

	void setSlientMode(boolean isSlient, Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (isSlient) {
			App.getInstance().setPhoneRingerMode(audioManager.getRingerMode());
			int timeMuteCategory = App.getInstance().getTimeMuteCategory();
			// 无声
			if (timeMuteCategory == 0) {
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
			// 震动
			else if (timeMuteCategory == 1) {
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}
			Hint.showTipsShort(context, String.format(context.getString(R.string.turn_to_slient), context.getResources().getStringArray(R.array.time_mode_mute_category)[timeMuteCategory]));
		} else {
			audioManager.setRingerMode(App.getInstance().getPhoneRingerMode());
			Hint.showTipsShort(context, R.string.turn_2_normal);
		}
	}

	public void sendNotification(Context context, String message, int type, Intent intent) {
		Notification notif = new Notification();
		notif.icon = App.getInstance().getIcon();
		notif.tickerText = message;
		notif.when = System.currentTimeMillis();
		notif.flags |= Notification.FLAG_AUTO_CANCEL;// |Notification.FLAG_INSISTENT;
		Intent intentNofity = getIntentByType(context, type, intent);
		PendingIntent contentIntent = PendingIntent.getActivity(context, Const.NOTIFICATION_REQUEST_EXAMINATION_ALARM, intentNofity, PendingIntent.FLAG_ONE_SHOT);
		notif.setLatestEventInfo(context, "课程格子提醒", message, contentIntent);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String ringtoneStr = settings.getString(App.NOTIFICATION_SOUND_COURSE_WARN_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
		if (settings.getBoolean(context.getString(R.string.notification_sound_enable), true) && type != AlarmManagerUtil.TYPE_CLASS_MUTE_END) {
			notif.sound = Uri.parse(ringtoneStr);
		} else {
			notif.defaults = 0;
		}
		if (settings.getBoolean(App.NOTIFICATION_VIBRATE_KEY, true))
			notif.defaults |= Notification.DEFAULT_VIBRATE;
		notif.ledARGB = 0xff0000ff; // Blue color
		notif.ledOnMS = 1000;
		notif.ledOffMS = 1000;
		notif.defaults |= Notification.DEFAULT_LIGHTS;

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(123456, notif);
	}
	
	public Intent getIntentByType(Context context, int type, Intent intent){
		if(type == AlarmManagerUtil.TYPE_ClASS_BEFORE_CLASS || type == AlarmManagerUtil.TYPE_CLASS_MUTE_START || type == AlarmManagerUtil.TYPE_CLASS_MUTE_END){
			return new Intent(context, SplashActivity.class);
		}else if(type == AlarmManagerUtil.TYPE_EVENT_BEFORE_CLASS){
			// || type == AlarmManagerUtil.TYPE_EVENT_MUTE_START
			Event event = (Event) intent.getSerializableExtra(AlarmManagerUtil.INTENT_IMPORTANT_ENTITY);
			if(event != null){
				//--todo 跳转到详情页
				Intent intent2Event = new Intent(context,EventActivity.class);
				intent2Event.putExtra("EVENT", event);
				return intent2Event;
			}else{
				return new Intent(context, SplashActivity.class);
			}
		}
		return new Intent();
	}

}

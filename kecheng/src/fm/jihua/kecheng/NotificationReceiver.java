package fm.jihua.kecheng;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import fm.jihua.kecheng.rest.entities.PushNotification;
import fm.jihua.kecheng.rest.entities.Semester;
import fm.jihua.kecheng_hs.R;

public class NotificationReceiver extends BroadcastReceiver {
	private static final String TAG = "NotificationReceiver";
	public static final String SMEMSTER_NOTIFICATION = "fm.jihua.kecheng_hs.UPDATE_SEMESTER";
	int requestCode = 998980;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			String channel = intent.getExtras().getString("com.parse.Channel");
			JSONObject json = new JSONObject(intent.getExtras().getString(
					"com.parse.Data"));
			PushNotification pushNotification = new PushNotification(json.getInt("notification_id"), action, 
					json.getString("message"), json.getLong("created_at")*1000);
			handleNotification(context, pushNotification, json);
			Log.d(TAG, "received action " + action + " on channel " + channel
					+ " with extras:");
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
	
	private void handleNotification(Context context, PushNotification pushNotification, JSONObject json) throws JsonParseException, JSONException {
		if (pushNotification.action.equals(SMEMSTER_NOTIFICATION)) {
			handleSemester(context, pushNotification, json);
		}
	}
	
	private void handleSemester(Context context, PushNotification pushNotification, JSONObject json) throws JsonParseException, JSONException{
		Gson gson = new Gson();
		Semester semester = gson.fromJson(( json.getJSONObject("semester")).toString(), Semester.class);
		App app = (App)context.getApplicationContext();
		boolean newSemester = false;
		boolean emptyCourses = false;
		if (app.getDBHelper().existSemester(app.getUserDB(), semester) == null) {
			app.getDBHelper().saveSemester(app.getUserDB(), semester);
			newSemester = true;
		}else if(app.getDBHelper().getCourses(app.getUserDB()).size() == 0){
			emptyCourses = true;
		}
	}
	
	public void sendNotification(Context context, Notification notif) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String ringtoneStr = settings.getString(
				App.NOTIFICATION_SOUND_KEY,
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
		notificationManager.notify(requestCode, notif);
	}
	
//	public void sendNotification(Context context, PushNotification pushNotification){
//		Notification notif = new Notification(App.getInstance().getIcon(), pushNotification.message, System.currentTimeMillis());
//		notif.flags|=Notification.FLAG_AUTO_CANCEL;//|Notification.FLAG_INSISTENT;
//		Intent intent = new Intent(context, NotificationsActivity.class);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
//		notif.setLatestEventInfo(context, "课程格子", pushNotification.message, contentIntent);
//		sendNotification(context, notif);
//	}
}

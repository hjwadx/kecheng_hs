package fm.jihua.kecheng;

import java.util.Calendar;
import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.ui.activity.SplashActivity;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseBlockUtil;
import fm.jihua.kecheng.utils.CourseHelper;
import fm.jihua.kecheng_hs.R;

public class KechengAppWidgetProvider extends AppWidgetProvider {
	/** Called when the activity is first created. */
	
	private final String TAG = this.getClass().getSimpleName();
	Context mContext;
	public static final String UPDATE = "fm.jihua.kecheng.UPDATE";
	public static final String NEXT_ACTION = "fm.jihua.kecheng.NEXTACTION";
	public static final String PREV_ACTION = "fm.jihua.kecheng.PREVACTION";

    
	public void onReceive(Context context, Intent intent) {
		AppLogger.d(TAG, "onReceive");
		String action = intent.getAction();
		AppLogger.d(TAG, "the action is " + action);

		if (UPDATE.equals(action)) {
			update(context);
		} else if (NEXT_ACTION.equals(action) || PREV_ACTION.equals(action)) {
			CourseBlock course = (CourseBlock) intent.getExtras().get("course");
			List<CourseBlock> courses = CourseBlockUtil.getInstance().getCourseBlocks(context, Calendar.getInstance().getTime());
			int index = courses.indexOf(course);
			if (index >= 0) {
				if (NEXT_ACTION.equals(action)) {
					index = index != courses.size() - 1 ? index + 1 : index;
				} else if (PREV_ACTION.equals(action)) {
					index = index != 0 ? index - 1 : index;
				}
				update(context, courses.get(index));
			}
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		AppLogger.d(TAG, "onUpdate");
		mContext = context;
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			AppLogger.d(TAG, "this is [" + appWidgetId + "] onUpdate!");
		}
		update(context);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		AppLogger.d(TAG, "onDeleted");
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			AppLogger.d(TAG, "this is [" + appWidgetId + "] onDelete!");
		}
	}

    @Override
    public void onEnabled(Context context) {
		AppLogger.d(TAG, "onEnabled");
        // When the first widget is created, register for the TIMEZONE_CHANGED and TIME_CHANGED
        // broadcasts.  We don't want to be listening for these if nobody has our widget active.
        // This setting is sticky across reboots, but that doesn't matter, because this will
        // be called after boot if there is a widget instance for this provider.
//    	IntentFilter s_intentFilter = new IntentFilter();
//        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
//        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
//        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
//        context.registerReceiver(mReceiver, s_intentFilter);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(
//                new ComponentName("fm.jihua.kecheng", ".TimeBroadcastReceiver"),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
        MobclickAgent.onEvent(context, "action_widget_enable_1");
    }

    @Override
    public void onDisabled(Context context) {
        // When the first widget is created, stop listening for the TIMEZONE_CHANGED and
        // TIME_CHANGED broadcasts.
		AppLogger.d(TAG, "onDisabled");
//        context.unregisterReceiver(mReceiver);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(
//                new ComponentName("fm.jihua.kecheng", ".TimeBroadcastReceiver"),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
    }
    
    private void update(Context context){
		AppLogger.i(TAG, "update");
    	App app = (App)context.getApplicationContext();
		List<Course> courses = app.getDBHelper().getCourses(app.getUserDB());
		if (courses.size() > 0) {
			List<CourseBlock> blocks = CourseBlockUtil.getInstance().getCourseBlocks(context, Calendar.getInstance().getTime());
	    	if (blocks.size() > 0) {
	    		CourseBlock course = CourseHelper.getNextCourse(blocks);
	    		update(context, course);
			}else {
				update(context, null);
			}
		}else {
			RemoteViews appWidgetView = new RemoteViews(context.getPackageName(), R.layout.appwidget_1x4_provider); 
	    	Intent intent = new Intent(context, SplashActivity.class);
			intent.putExtra("widget", Const.WIDGET_1X4);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			appWidgetView.setOnClickPendingIntent(R.id.no_course_container, pendingIntent);
	    	AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, KechengAppWidgetProvider.class), appWidgetView);
		}
    }
    
    private void update(Context context, CourseBlock course){
    	RemoteViews appWidgetView = new RemoteViews(context.getPackageName(), R.layout.appwidget_1x4_provider); 
    	Intent intent = new Intent(context, SplashActivity.class);
		intent.putExtra("widget", Const.WIDGET_1X4);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    	if (course != null && !course.empty) {
    		appWidgetView.setViewVisibility(R.id.course_container, View.VISIBLE);
    		appWidgetView.setViewVisibility(R.id.no_course_container, View.GONE);
			List<CourseBlock> blocks = CourseBlockUtil.getInstance().getCourseBlocks(context, Calendar.getInstance().getTime());
			appWidgetView.setImageViewResource(R.id.get_prev_course, blocks.indexOf(course) == 0 ? R.drawable.widget_small_btn_previous_disabled : R.drawable.widget_small_btn_previous);
			appWidgetView.setImageViewResource(R.id.get_next_course, blocks.indexOf(course) == blocks.size()-1 ? R.drawable.widget_small_btn_next_disabled : R.drawable.widget_small_btn_next);
			refreshCourse(appWidgetView, course, R.id.num, R.id.name, R.id.room, R.id.teacher, R.id.course_container, pendingIntent);
            setActionListeners(context, appWidgetView, course);
		}else {
			appWidgetView.setViewVisibility(R.id.course_container, View.GONE);
			appWidgetView.setViewVisibility(R.id.no_course_container, View.VISIBLE);
			appWidgetView.setTextViewText(R.id.tip, "你今天没有课，好好放松吧！: D");
		} 
    	appWidgetView.setOnClickPendingIntent(R.id.no_course_container, pendingIntent);
    	try {
    		AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, KechengAppWidgetProvider.class), appWidgetView);
		} catch (Exception e) {
			AppLogger.printStackTrace(e);
		}
    }
    
    private void refreshCourse(RemoteViews view, CourseBlock course, int num_res, int name_res, int room_res, int teacher_res, int course_container_res, PendingIntent intent){
    	view.setTextViewText(num_res, course.getSlotString());
    	view.setTextViewText(name_res, course.name); 
    	view.setTextViewText(room_res, CommonUtils.isNullString(course.room) ? "地点未知" : course.room);
    	view.setTextViewText(teacher_res, CommonUtils.isNullString(course.teacher) ? "教师未知" : course.teacher);
        view.setOnClickPendingIntent(course_container_res, intent);
    }
    
    private void setActionListeners(Context context, RemoteViews appWidgetView, CourseBlock course){
    	Intent nextIntent = new Intent(context, KechengAppWidgetProvider.class);
        nextIntent.setAction(KechengAppWidgetProvider.NEXT_ACTION);
        nextIntent.putExtra("course", course);
        final PendingIntent refreshNextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Intent prevIntent = new Intent(context, KechengAppWidgetProvider.class);
        prevIntent.setAction(KechengAppWidgetProvider.PREV_ACTION);
        prevIntent.putExtra("course", course);
        final PendingIntent refreshPrevPendingIntent = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        appWidgetView.setOnClickPendingIntent(R.id.get_next_course, refreshNextPendingIntent);
        appWidgetView.setOnClickPendingIntent(R.id.get_prev_course, refreshPrevPendingIntent);
    }
}
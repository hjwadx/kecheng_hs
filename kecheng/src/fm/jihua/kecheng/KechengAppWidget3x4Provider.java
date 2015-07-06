package fm.jihua.kecheng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class KechengAppWidget3x4Provider extends AppWidgetProvider{
	
	private final String TAG = this.getClass().getSimpleName();
	Context mContext;
	
	public void onReceive( Context context, Intent intent ) {
		AppLogger.d(TAG, "onReceive");
        String action = intent.getAction();
		AppLogger.d(TAG, "the action is " + action);
        mContext = context;
        
        if (KechengAppWidgetProvider.UPDATE.equals(action)) {
        	update(context);
		}else if(KechengAppWidgetProvider.NEXT_ACTION.equals(action) || KechengAppWidgetProvider.PREV_ACTION.equals(action)){
			int displayDayOfWeek = intent.getIntExtra("displayDayOfWeek", 0);
			List<CourseBlock> courses = getCourseBlocksOfDay(context, action, displayDayOfWeek);
			update(context, courses, getDayOfWeekByAction(action, displayDayOfWeek));
		} 
        super.onReceive( context, intent );
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
        MobclickAgent.onEvent(context, "action_widget_enable_3");
    }
	
	@Override
    public void onDisabled(Context context) {
        // When the first widget is created, stop listening for the TIMEZONE_CHANGED and
        // TIME_CHANGED broadcasts.
        Log.d(Const.TAG, "onDisabled");
//        context.unregisterReceiver(mReceiver);
    }
	
	private void update(Context context){
		AppLogger.i(TAG, "update");
    	App app = (App)context.getApplicationContext();
		List<Course> courses = app.getDBHelper().getCourses(app.getUserDB());
		if (courses.size() > 0) {
			List<CourseBlock> blocks = CourseBlockUtil.getInstance().getCourseBlocks(context, Calendar.getInstance().getTime());
	    	if (blocks.size() > 0) {
	    		List<CourseBlock> courses1 = getNextThreeCourse(blocks);
	    		update(context, courses1, CourseHelper.getDayOfWeek());
			}else {
				update(context, null, CourseHelper.getDayOfWeek());
			}
		}else {
			RemoteViews appWidgetView = new RemoteViews(context.getPackageName(), R.layout.appwidget_3x4_provider); 
			appWidgetView.setViewVisibility(R.id.course_container, View.GONE);
			appWidgetView.setViewVisibility(R.id.course_container2, View.GONE);
			appWidgetView.setViewVisibility(R.id.course_container3, View.GONE);
	    	Intent intent = new Intent(context, SplashActivity.class);
			intent.putExtra("widget", Const.WIDGET_3X4);
			appWidgetView.setTextViewText(R.id.tip, "课表空空如也？想当学霸，从选课开始！");
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			appWidgetView.setOnClickPendingIntent(R.id.no_course_container, pendingIntent);
			appWidgetView.setViewVisibility(R.id.no_course_container, View.VISIBLE);
			setWeekAndDay(appWidgetView, CourseHelper.getDayOfWeek());
			try {
				AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, KechengAppWidget3x4Provider.class), appWidgetView);
			} catch (Exception e) {
				AppLogger.printStackTrace(e);
			}
		}
    }
	
	private void setWeekAndDay (RemoteViews appWidgetView, int dayToDisplay) {
		App app = (App)mContext.getApplicationContext();
		appWidgetView.setTextViewText(R.id.day_of_week, "第"+app.getCurrentWeek(true)+"周 "+getDay(dayToDisplay));
	}
	
	private String getDay (int dayToDisplay) {
        return Const.WEEKS_XINGQI[dayToDisplay - 1];		
	}
	
	private List<CourseBlock> getNextThreeCourse(List<CourseBlock> blocks){
    	List<CourseBlock> courses = new ArrayList<CourseBlock>();
    	CourseBlock course = null;
    	course = CourseHelper.getNextCourse(blocks);
		courses.add(course);
    	int local = blocks.indexOf(course);
    	while (local+1< blocks.size() && courses.size() < 3) {
    		courses.add(blocks.get(++local));
    	}
    	return courses;
    }
	
	private void update(Context context, List<CourseBlock> courses, int displayDayOfWeek) {
		RemoteViews appWidgetView = new RemoteViews(context.getPackageName(), R.layout.appwidget_3x4_provider);
		Intent intent = new Intent(context, SplashActivity.class);
		intent.putExtra("widget", Const.WIDGET_3X4);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		appWidgetView.setImageViewResource(R.id.get_prev_day, displayDayOfWeek == 1 ? R.drawable.widget_middlle_btn_previous_disabled : R.drawable.widget_middlle_btn_previous);
		appWidgetView.setImageViewResource(R.id.get_next_day, displayDayOfWeek == 7 ? R.drawable.widget_middlle_btn_next_disabled : R.drawable.widget_middlle_btn_next);
		
		setWeekAndDay(appWidgetView, displayDayOfWeek);
		CourseBlock course = null;
		if (courses != null && !courses.isEmpty()) {
			course = courses.get(0);
		}
		if (course != null && !course.empty) {
			appWidgetView.setViewVisibility(R.id.no_course_container, View.GONE);
			refreshCourse(appWidgetView, course, R.id.num, R.id.name, R.id.room, R.id.teacher, R.id.course_container, pendingIntent);
			//the second course
			if (courses.size() > 1) {
				course = courses.get(1);
				refreshCourse(appWidgetView, course, R.id.num2, R.id.name2, R.id.room2, R.id.teacher2, R.id.course_container2, pendingIntent);
			} else {
				appWidgetView.setViewVisibility(R.id.course_container2, View.INVISIBLE);
			}
			//the third course
			if (courses.size() > 2) {
				course = courses.get(2);
				refreshCourse(appWidgetView, course, R.id.num3, R.id.name3, R.id.room3, R.id.teacher3, R.id.course_container3, pendingIntent);
			} else {
				appWidgetView.setViewVisibility(R.id.course_container3, View.INVISIBLE);
			}
		} else {
			appWidgetView.setViewVisibility(R.id.course_container, View.GONE);
			appWidgetView.setViewVisibility(R.id.course_container2, View.GONE);
			appWidgetView.setViewVisibility(R.id.course_container3, View.GONE);
			appWidgetView.setViewVisibility(R.id.no_course_container, View.VISIBLE);
			appWidgetView.setTextViewText(R.id.tip, "你今天没有课，好好放松吧！: D");
		}
		setActionListeners(context, appWidgetView, displayDayOfWeek);
		appWidgetView.setOnClickPendingIntent(R.id.no_course_container, pendingIntent);
		AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, KechengAppWidget3x4Provider.class), appWidgetView);
	}
	
	private void refreshCourse(RemoteViews view, CourseBlock course, int num_res, int name_res, int room_res, int teacher_res, int course_container_res, PendingIntent intent){
		view.setViewVisibility(course_container_res, View.VISIBLE);
    	view.setTextViewText(num_res, course.getSlotString());
    	view.setTextViewText(name_res, course.name); 
    	view.setTextViewText(room_res, CommonUtils.isNullString(course.room) ? "地点未知" : course.room);
    	view.setTextViewText(teacher_res, CommonUtils.isNullString(course.teacher) ? "教师未知" : course.teacher);
        view.setOnClickPendingIntent(course_container_res, intent);
    }
	
	private List<CourseBlock> getCourseBlocksOfDay(Context context, String action, int displayDayOfWeek){
    	int dayOfWeek = getDayOfWeekByAction(action, displayDayOfWeek);
    	return CourseBlockUtil.getInstance().getCourseBlocksOfDayOfWeek(context, CourseHelper.getSystemWeekdayFromWeekday(dayOfWeek));
    }
	
	private int getDayOfWeekByAction(String action, int displayDayOfWeek){
		int dayOfWeek;
    	if (KechengAppWidgetProvider.NEXT_ACTION.equals(action)) {
    		dayOfWeek = displayDayOfWeek < 7 ? displayDayOfWeek+1 : 7;
    	} else {
    		dayOfWeek = displayDayOfWeek > 1 ? displayDayOfWeek-1 : 1;
    	}
    	return dayOfWeek;
	}
	 
	private void setActionListeners(Context context, RemoteViews appWidgetView, int displayDayOfWeek){
	    Intent nextIntent = new Intent(context, KechengAppWidget3x4Provider.class);
	    nextIntent.setAction(KechengAppWidgetProvider.NEXT_ACTION);
	    nextIntent.putExtra("displayDayOfWeek", displayDayOfWeek);
	    final PendingIntent refreshNextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
	    Intent prevIntent = new Intent(context, KechengAppWidget3x4Provider.class);
	    prevIntent.setAction(KechengAppWidgetProvider.PREV_ACTION);
	    prevIntent.putExtra("displayDayOfWeek", displayDayOfWeek);
	    final PendingIntent refreshPrevPendingIntent = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
	    appWidgetView.setOnClickPendingIntent(R.id.get_next_day, refreshNextPendingIntent);
		appWidgetView.setOnClickPendingIntent(R.id.get_prev_day, refreshPrevPendingIntent);
	}

}

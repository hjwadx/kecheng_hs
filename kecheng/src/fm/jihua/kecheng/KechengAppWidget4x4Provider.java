package fm.jihua.kecheng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.ui.activity.SplashActivity;
import fm.jihua.kecheng.ui.widget.WidgetWeekView;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;
import fm.jihua.kecheng.utils.EventHelper;
import fm.jihua.kecheng.utils.ImageHlp;
import fm.jihua.kecheng_hs.R;

public class KechengAppWidget4x4Provider extends AppWidgetProvider {
	
	private final String TAG = this.getClass().getSimpleName();

	Context mContext;
	int timeSlot;
	WidgetWeekView weekView;

	public void onReceive(Context context, Intent intent) {
		AppLogger.d(TAG, "onReceive");
		String action = intent.getAction();
		AppLogger.d(TAG, "the action is " + action);

		mContext = context;

		if (KechengAppWidgetProvider.UPDATE.equals(action)) {
			update(context);
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
	public void onDisabled(Context context) {
		// When the first widget is created, stop listening for the
		// TIMEZONE_CHANGED and
		// TIME_CHANGED broadcasts.
		AppLogger.d(TAG, "onDisabled");
		// context.unregisterReceiver(mReceiver);
	}

	@Override
	public void onEnabled(Context context) {
		AppLogger.d(TAG, "onEnabled");
		MobclickAgent.onEvent(context, "action_widget_enable_4");
	}

	private void update(Context context) {
		AppLogger.d(TAG, "update");
		RemoteViews appWidgetView = new RemoteViews(context.getPackageName(), R.layout.appwidget_4x4_provider);
		Intent intent = new Intent(context, SplashActivity.class);
		intent.putExtra("widget", Const.WIDGET_4X4);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		App app = (App) context.getApplicationContext();
		List<Course> courses = app.getDBHelper().getCourses(app.getUserDB());
		List<Event> events = App.getInstance().getDBHelper().getEvents(App.getInstance().getUserDB());
		if (getDataForWeekView(courses, events).size() > 0) {
			weekView = new WidgetWeekView(context);
			initWeekView(weekView, courses, events);
			String fileName = String.valueOf(System.currentTimeMillis());
			FileOutputStream fOut = null;
			try {
				fOut = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ImageHlp.combine(weekView).compress(Bitmap.CompressFormat.PNG, 100, fOut);
			try {
				fOut.flush();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (null != app.getWidgetFileUrl()) {
				File file = new File(app.getWidgetFileUrl());
				file.delete();
			}
			app.setWidgetFileUrl(context.getFilesDir() + "/" + fileName);
			appWidgetView.setImageViewUri(R.id.timetableimage, Uri.parse(context.getFilesDir() + "/" + fileName));
			appWidgetView.setOnClickPendingIntent(R.id.timetableimage, pendingIntent);
			appWidgetView.setViewVisibility(R.id.timetableimage, View.VISIBLE);
			appWidgetView.setViewVisibility(R.id.no_course_container, View.GONE);
		} else {
			appWidgetView.setOnClickPendingIntent(R.id.no_course_container, pendingIntent);
			appWidgetView.setViewVisibility(R.id.timetableimage, View.GONE);
			appWidgetView.setViewVisibility(R.id.no_course_container, View.VISIBLE);
		}
		try {
			AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, KechengAppWidget4x4Provider.class), appWidgetView);
		} catch (Exception e) {
			AppLogger.printStackTrace(e);
		}
	}

	public void initWeekView(WidgetWeekView weekView, List<Course> courses, List<Event> events) {
		timeSlot = CourseHelper.getMaxTimeSlot(getDataForWeekView(courses, events));
		timeSlot = Math.max(timeSlot, App.getInstance().getSlotLength());
		timeSlot = Math.min(timeSlot, 16);
		weekView.setTimeSlot(timeSlot);
		weekView.setData(getDataForWeekView(courses, events));
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int width = Compatibility.getWidth(windowManager.getDefaultDisplay());
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
		weekView.measure(widthMeasureSpec, heightMeasureSpec);
		weekView.layout(0, 0, width, (600 * width / 480));
	}

	List<CourseBlock> getDataForWeekView(List<Course> coruses, List<Event> events) {
		App app = (App) mContext.getApplicationContext();
		final int week = app.getCurrentWeek(false);
		List<CourseBlock> allBlocks = CourseHelper.getFullCourseBlocks(coruses,
				week);
		// this.addSimulationData();
		allBlocks = CourseHelper.merge(allBlocks, false);
		
		// 增加活动的courseblock
		List<CourseBlock> eventBlocks = EventHelper.getCourseBlocksFromEventListOnlyThisWeek(events);
		if (eventBlocks.size() > 0) {
			allBlocks.addAll(eventBlocks);
		}
		
		Collections.sort(allBlocks, new Comparator<CourseBlock>() {

			@Override
			public int compare(CourseBlock lhs, CourseBlock rhs) {
				if (lhs.active == rhs.active) {
					if (!lhs.active) {
//						if (lhs.start_week <= week && lhs.end_week >= week) {
//							return 1;
//						} else if (rhs.start_week <= week && rhs.end_week >= week) {
//							return -1;
//						} else {
//							if (lhs.start_week > week && rhs.start_week > week) {
//								return rhs.start_week - lhs.start_week;
//							} else {
//								return lhs.start_week - rhs.start_week;
//							}
//						}
						return 0;
					} else {
						if(lhs.event != null && rhs.event == null){
							return 1;
						} else {
							return 0;
						}
					}
				} else if (lhs.active) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		return allBlocks;
	}
}

package fm.jihua.kecheng.ui.widget;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.ui.widget.weekview.WeekSpiritPaster;
import fm.jihua.kecheng.ui.widget.weekview.WeekSpiritStyle;
import fm.jihua.kecheng.ui.widget.weekview.WeekViewParams;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.CourseHelper;

public class WeekView extends FrameLayout {

//	HashMap<String, Integer> colorMap;

	public boolean isMyselfView;
	private boolean readyDataToDraw = false;

	public Activity activity;

	public WeekSpiritStyle spiritStyle;
	public WeekSpiritPaster spiritPaster;
	
	private WeekViewParams weekViewParams;
	
	private GraduationView graduationView;

	Point pt_touchdown;

	public static final int TYPE_TITLE_PASTER_EDIT = 202;
	public static final int TYPE_TITLE_NORMAL = 203;

	public WeekView(Context context) {
		super(context);
	}

	public WeekView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setWillNotDraw(false);
		init(context);
		initListeners();
	}

	private void init(Context context) {
		activity = (Activity) context;
		weekViewParams = new WeekViewParams(getScreenWidth(), 0);
		spiritStyle = new WeekSpiritStyle();
		spiritPaster = new WeekSpiritPaster();
	}

	private void initListeners() {
		setOnLongClickListener(onLongClickListener);
	}

	public void initSpecialView(CustomScrollView scrollView) {
		graduationView = new GraduationView(getContext());
		post(new Runnable() {
			
			@Override
			public void run() {
				((ViewGroup) activity.findViewById(R.id.main_parent)).addView(graduationView);
			}
		});
		spiritStyle.initSpecialView(scrollView, graduationView);
	}

	public void setTimeSlot(int timeSlot) {
		weekViewParams.setTimeSlot(timeSlot);
		spiritStyle.resetWeekViewParams(weekViewParams);
		spiritPaster.resetWeekViewParams(weekViewParams);
	}

	public void setData(List<CourseBlock> courses, List<UserSticker> pasters, String styleName,int productId, boolean isMyself) {

		readyDataToDraw = true;
		this.isMyselfView = isMyself;

		resetParamsTimeSlot(courses);

		spiritStyle.resetData(this, styleName, productId, weekViewParams, courses);
		spiritPaster.resetData(spiritStyle.getPointCourses().keySet(), pasters, weekViewParams, this);

		invalidate();
	}
	
	private void resetParamsTimeSlot(List<CourseBlock> courses){
		if (weekViewParams.timeSlot == 0) {
			int timeSlot = CourseHelper.getMaxTimeSlot(courses);
			timeSlot = Math.max(timeSlot, 12);
			weekViewParams.setTimeSlot(timeSlot);
		}
	}

	public void invalidateGraduationViewForPaste(boolean showShadow) {
		spiritStyle.invalidateGraduationViewForPaste(showShadow);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (getHeight() != 0 && readyDataToDraw) {
			spiritStyle.draw(canvas);
			spiritPaster.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (getWidth() == 0 || getHeight() == 0) {
			return false;
		}

		if (!readyDataToDraw)
			return false;

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pt_touchdown = new Point((int) ev.getX(), (int) ev.getY());
			break;
		case MotionEvent.ACTION_UP:
			if (spiritPaster.onTouchUpPasting(pt_touchdown)) {
				AppLogger.i("ACTION_UP pasting spiritPaster");
			} else {
				if (ev.getEventTime() - ev.getDownTime() < ViewConfiguration.getLongPressTimeout()) {
					if (spiritStyle.onTouchUp(pt_touchdown)) {
						return true;
					} else if(spiritPaster.onTouchUp(pt_touchdown)){
						AppLogger.i("ACTION_UP spiritPaster");
					}
				}
			}

			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	OnLongClickListener onLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// 是否长按了贴纸
			if (isMyselfView && !spiritPaster.isChossingPaster()) {
				if(spiritPaster.onLongClick(pt_touchdown)){
					AppLogger.i("onLongClick spiritPaster");
				}
			}
			return false;
		}
	};

	public WeekViewParams getWeekViewParams() {
		return weekViewParams;
	}


	public static interface OnModifyTitleStatusListener {
		public void pasterStatusChanged(int statusType);
	}

	public OnModifyTitleStatusListener modifyTitleStatusListener;

	public void setOnModifyTitleStatusListener(OnModifyTitleStatusListener modifyTitleStatusListener) {
		this.modifyTitleStatusListener = modifyTitleStatusListener;
	}

	public OnCourseClickListener childClickListener;

	public void setOnChildClickListener(OnCourseClickListener clickListener) {
		childClickListener = clickListener;
	}

	public interface OnCourseClickListener {
		void onMultiCourseClick(List<CourseBlock> courseBlocks);

		void onCourseClick(CourseBlock courseBlock);

		void onPasterClick(UserSticker paster);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		switch (heightSpecMode) {
		case MeasureSpec.UNSPECIFIED:
			setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
			break;
		default:
			int height = (int) (weekViewParams.blockHeight * weekViewParams.timeSlot) + weekViewParams.offsetTop;
			int width = (int) ((0 + weekViewParams.dayCount) * weekViewParams.blockWidth) + weekViewParams.offsetLeft;
			this.setMeasuredDimension(width, height);
			break;
		}
	}

	public int getScreenWidth() {
		return Compatibility.getWidth(activity.getWindowManager().getDefaultDisplay());
	}

	public int getScreenHeight() {
		return Compatibility.getHeight(activity.getWindowManager().getDefaultDisplay());
	}
}

package fm.jihua.kecheng.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class WeekScrollView extends ScrollView {
	
	WeekView weekView;

	public WeekScrollView(Context context) {
		super(context);
		this.setFillViewport(true);
		initView();
	}

	public WeekScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		int count = getChildCount();
		if (weekView == null){
			for(int i=0; i<count; i++){
				View v = getChildAt(i);
				if (v instanceof WeekView) {
					weekView = (WeekView)v;
				}
			}
		}
	}
	
	void initView(){
		weekView = new WeekView(getContext());
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
 		weekView.setLayoutParams(lp);
 		addView(weekView);
	}
	
	public WeekView getWeekView(){
		return weekView;
	}

	boolean isLastEventInWeekView;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
//		return super.dispatchTouchEvent(ev);
		if (getVisibility() == VISIBLE && weekView != null) {
			MotionEvent evForWeekView = MotionEvent.obtain(ev);
			
			evForWeekView.setLocation(ev.getX(), getAjustedY(ev.getY()));
//			Log.d("WeekView", String.valueOf(evForWeekView.getY()));
//			Log.d(Const.TAG, String.valueOf(result));
			
			boolean inWeekViewRect = evForWeekView.getY() > getTop();

			if (inWeekViewRect) {
				boolean result =  weekView.onTouchEvent(evForWeekView);
				if (result) {
					isLastEventInWeekView = true;
					return true;
				}
			}
//			Log.d("WeekView", String.valueOf(ev.getAction()));
//			if (isLastEventInWeekView) {
//				ev.setAction(MotionEvent.ACTION_DOWN);
//				isLastEventInWeekView = false;
//			}
			try {
				return super.dispatchTouchEvent(ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	float getAjustedY(float y){
//		if (y-getStatusBarHeight() > getTitleHeight()) {
//			return y+scrollView.getScrollY()-getStatusBarHeight()-getTitleHeight();
//		}else {
//			return y-getStatusBarHeight()-getTitleHeight();
//		}
		return y+getScrollY();
	}
}

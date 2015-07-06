package fm.jihua.kecheng.ui.activity.register;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.viewpagerindicator.CirclePageIndicator;

import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;

public class TutorialActivity extends BaseActivity {

	boolean showExitNotice;
	DataAdapter mDataAdapter;
	TutorialFragmentAdapter mAdapter;
	ViewPager mPager;
	CirclePageIndicator mIndicator;
	RelativeLayout parent;
	GestureDetector gestureDetector;
	int screen_width;
	int screen_height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial);
		findViews();
		initUI();
	}

	void findViews(){
		mPager = (ViewPager) findViewById(R.id.pager);
		parent = (RelativeLayout) findViewById(R.id.parent);
	}
	
	void initUI() {

		mAdapter = new TutorialFragmentAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);

		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

		gestureDetector = new GestureDetector(this, new DefaultGestureDetector());
		Display defaultDisplay = getWindow().getWindowManager().getDefaultDisplay();
		screen_width = Compatibility.getWidth(defaultDisplay);
		screen_height = Compatibility.getHeight(defaultDisplay);
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (mPager.getCurrentItem() == TutorialFragmentAdapter.ICONS.length - 1) {
			switch (ev.getAction()) {
			// initialise
			case MotionEvent.ACTION_UP:
				if (distance > screen_width / 2) {
					finishActivity();
				}else {
					if (key_left_scroll_to_finish)
						setByDistance(0, true);
					else {
						setByDistance(0, false);
					}
					distance = 0;
				}
				key_left_scroll_to_finish = false;
				key_right_scroll_viewpager = false;
				break;
			}

			gestureDetector.onTouchEvent(ev);
			if (key_left_scroll_to_finish) {
				return true;
			}

		}
		
		//fix muti-point
		try {
			super.dispatchTouchEvent(ev);
		} catch (Exception e) {
		}
		return false;
	}

	int distance;
	int currentDistance;
	boolean key_left_scroll_to_finish = false;
	boolean key_right_scroll_viewpager = false;

	class DefaultGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			distance += distanceX;
			// left scroll
			if (distance > 0 && !key_right_scroll_viewpager) {
				key_left_scroll_to_finish = true;
				setByDistance(distance, false);
			}else if(distance < 0 && !key_left_scroll_to_finish){
				key_right_scroll_viewpager=true;
			}
			return false;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			
			if(velocityX < -1000){
				finishActivity();
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
	
	void finishActivity(){
		setResult(RESULT_OK);
		finish();
	}

	
	void setByDistance(int distance_all, boolean amin) {
		if (amin) {
			if (currentDistance == 0)
				currentDistance = distance;
			parent.post(aminToInitialise);
		} else {
			parent.layout(-distance_all, 0, screen_width - distance_all, screen_height);
			TutorialFragment fragment = (TutorialFragment) mAdapter.instantiateItem(mPager, TutorialFragmentAdapter.ICONS.length - 1);
			Compatibility.setAlpha(fragment.imageView, getColor(distance_all));
		}
	}

	Runnable aminToInitialise = new Runnable() {

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public void run() {
			parent.layout(-currentDistance, 0, screen_width - currentDistance, screen_height);
			TutorialFragment fragment = (TutorialFragment) mAdapter.instantiateItem(mPager, TutorialFragmentAdapter.ICONS.length - 1);
			Compatibility.setAlpha(fragment.imageView, getColor(currentDistance));
			currentDistance -= 30;
			if (currentDistance <= 0) {
				currentDistance = 0;
				parent.layout(-currentDistance, 0, screen_width - currentDistance, screen_height);
				Compatibility.setAlpha(fragment.imageView, 255);
			} else {
				parent.postDelayed(aminToInitialise, 1);
			}
		}
	};
	
	int getColor(int distance_all){
		int color = (int) ((1 - (Math.abs((float)distance_all / (screen_width)))) * 255);
		color += 40;
		if(color>255)
			color = 255;
		return color;
	}

	/* 消息处理 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			App app = (App) getApplication();
			if (showExitNotice) {
				app.exit();
			} else {
				Hint.showTipsShort(this, R.string.double_click_to_exit);
				showExitNotice = true;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

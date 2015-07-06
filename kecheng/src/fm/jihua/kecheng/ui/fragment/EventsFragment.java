package fm.jihua.kecheng.ui.fragment;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ViewFlipper;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.animation.ViewSwitcher;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.EventsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.profile.MyProfile;
import fm.jihua.kecheng.ui.view.BaseViewGroup;
import fm.jihua.kecheng.ui.view.EventListView;
import fm.jihua.kecheng_hs.R;

public class EventsFragment extends BaseFragment{
	
	private BaseViewGroup currentView;
	BaseViewGroup allView;
	BaseViewGroup myView;
	List<Event> myEvents ;

	ViewFlipper rootView;
	
	private DataAdapter mDataAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MobclickAgent.onEvent(parent, "enter_activity_view");
		mDataAdapter = new DataAdapter(parent, new MyDataCallback());
		mDataAdapter.getJoinedEvents();
		rootView = (ViewFlipper) inflater.inflate(R.layout.main, container, false);
		
		allView = new EventListView(this, EventListView.ALL_EVENT);
		currentView = allView;
		rootView.addView(currentView);
		myEvents = App.getInstance().getDBHelper().getEvents(App.getInstance().getUserDB());
		return rootView;
	}
	
	
	public void initTitlebar(){
		if (currentView != null) {
			currentView.onResume();
		}
		MenuActivity activity =(MenuActivity) getActivity();
		activity.getKechengActionBar().getRightButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchView();
			}
		});
	}
	
	@Override
	public void onShow() {
		super.onShow();
		registerBroad();
		refresh();
	}
	
	public void refresh(){
		List<Event> events = App.getInstance().getDBHelper().getEvents(App.getInstance().getUserDB());
		if (!ObjectUtils.nullSafeEquals(events, myEvents)) {
			myEvents = events;
			if(allView != null){
				allView.refreshUI();
			}
			if(myView != null){
				myView.refreshUI();
			}
		}
	}

	private void reload() {
		if(allView != null){
			((EventListView)allView).refresh();
		}
	}
	
	void switchView() {
		BaseViewGroup otherView = currentView == myView ? allView : myView;
		if (otherView == null) {
			if (currentView == myView) {
				otherView = allView = new EventListView(this, EventListView.ALL_EVENT);
			} else {
				otherView = myView = new EventListView(this, EventListView.MY_EVENT);
			}
			rootView.addView(otherView);
		}
		Animator animator = ObjectAnimator.ofFloat(currentView, "rotationY", 0, 90).setDuration(ViewSwitcher.DURATION);
		animator.setInterpolator(new AccelerateInterpolator(3f));
		currentView.setDrawingCacheEnabled(true);
		otherView.setDrawingCacheEnabled(true);
		animator.addListener(startAnimatorListener);
		animator.start();
		animator = ObjectAnimator.ofFloat(otherView, "rotationY", -90, 0).setDuration(ViewSwitcher.DURATION).setStartDelay(ViewSwitcher.DURATION);
		animator.setInterpolator(new DecelerateInterpolator(3f));
		animator.addListener(endAnimatorListener);
		animator.start();
		currentView = otherView;
		currentView.initTitleBar();
//		otherView.setData(null);
		findViewById(R.id.action).setEnabled(false);
	}
	
	AnimatorListener startAnimatorListener = new AnimatorListener() {

		@Override
		public void onAnimationStart(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}

		@Override
		public void onAnimationEnd(Animator animation) {
			ObjectAnimator objectAnimator = (ObjectAnimator) animation;
			((View) objectAnimator.getTarget()).setAnimation(null);
			rootView.showNext();
			findViewById(R.id.action).setEnabled(true);
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}
	};

	AnimatorListener endAnimatorListener = new AnimatorListener() {

		@Override
		public void onAnimationStart(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}

		@Override
		public void onAnimationEnd(Animator animation) {
			ObjectAnimator objectAnimator = (ObjectAnimator) animation;
			((View) objectAnimator.getTarget()).setAnimation(null);
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}
	};
	
	private class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_JOINED_EVENT:
				EventsResult result = (EventsResult) msg.obj;
				if(result != null && result.success){
					refresh();
				}
				break;
			default:
				break;
			}
		}
	}
	
	private void registerBroad() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(MyProfile.BRODCAST_SCHOOL_CHANGED);
		((MenuActivity) getActivity()).registerReceiver(mThemeReceiver, filter);
	}
	
	private void unRegisterBroad(){
		((MenuActivity) getActivity()).unregisterReceiver(mThemeReceiver);
	}
	
	private BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (MyProfile.BRODCAST_SCHOOL_CHANGED.equals(action)) {
				reload();
			}
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterBroad();
	}

}

package fm.jihua.kecheng.ui.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.mall.MallFragment;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialManager;
import fm.jihua.kecheng.ui.view.MainWeekView;
import fm.jihua.kecheng.ui.widget.WeekView;
import fm.jihua.kecheng.ui.widget.WeekView.OnModifyTitleStatusListener;

public class MainFragment extends BaseFragment {

	private List<Course> mCourseList;
	private List<UserSticker> mSavedStickerList;
	private List<Event> mEventList;
	private int lastTimeSlot;
	private int lastWeek;
	private boolean lastVacation;
	public MainWeekView mainWeekView;

	@SuppressWarnings("serial")
	Map<String, String> updateNotificationMap = new HashMap<String, String>() {
		{
			// put("1.40",
			// "新版本添加了“获取账号”功能，在“设置-我的账号”里可得到自己独一无二的格子号，设置密码就再也不用担心课表找不回来咯！同时新版本支持更多方式添加好友，可查看同院系的好友信息。");
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainWeekView = new MainWeekView(this);
		return mainWeekView;
	}
	
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mainWeekView.weekView.setOnModifyTitleStatusListener(modifyTitleStatusListener);
	}

	void showUpdateNotification(String message) {
		new AlertDialog.Builder(parent).setTitle("更新提示").setMessage(message).setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				dialoginterface.dismiss();
			}
		}).show();
	}

	@Override
	public void initTitlebar() {
		parent.setTitle(App.getInstance().getCurrentWeekName());
		parent.findViewById(R.id.action).setVisibility(View.GONE);
	}
	
	@Override
	public void onShow() {
		MobclickAgent.onEvent(parent, "enter_mycourse_view");
		super.onShow();
		if (mainWeekView != null) {
			mainWeekView.onResume();
		}
		registerBroad();
		final MenuActivity activity = (MenuActivity) getActivity();
		activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		refresh();
	}

	public void hideBottomMenu(){
		findViewById(R.id.menu_bar_container).setVisibility(View.GONE);
	}
	
	public void showBottomMenu(){
		findViewById(R.id.menu_bar_container).setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		mainWeekView.onActivityResult(requestCode, resultCode, data);
	}
	
	public void refresh(){
		List<Course> courses = App.getInstance().getDBHelper().getCourses(App.getInstance().getUserDB());
		List<UserSticker> savedStickerList = App.getInstance().getSavedStickerList();
		List<Event> events = App.getInstance().getDBHelper().getEvents(App.getInstance().getUserDB());
		
		if (!ObjectUtils.nullSafeEquals(events, mEventList) || !ObjectUtils.nullSafeEquals(savedStickerList, mSavedStickerList) ||!ObjectUtils.nullSafeEquals(courses, mCourseList) || lastWeek != App.getInstance().getCurrentWeek(false) || lastVacation != App.getInstance().isVacation()) {
			mCourseList = courses;
			this.mSavedStickerList = savedStickerList;
			mEventList = events;
			lastWeek = App.getInstance().getCurrentWeek(false);
			lastVacation = App.getInstance().isVacation();
			mainWeekView.setData(mCourseList, mEventList);
		}
		if (App.mTimeSlotLength != lastTimeSlot) {
			lastTimeSlot = App.mTimeSlotLength;
			mainWeekView.resetTimeSlotInfo();
		}
		if (mainWeekView.skinView != null) {
			mainWeekView.skinView.refresh();
		}
	}
	
	public boolean onBackPressed() {
		TutorialManager tutorialManager = mainWeekView.getTutorialUtils();
		if (tutorialManager != null && !tutorialManager.isFinished()) {
			tutorialManager.finishTutorials();
			return true;
		}
		
		if(mainWeekView.getMenuBar().getVisibility() == View.VISIBLE){
			mainWeekView.toggleMenu(null);
			return true;
		}
		return false;
	};
	
	OnModifyTitleStatusListener modifyTitleStatusListener = new OnModifyTitleStatusListener() {
		
		@Override
		public void pasterStatusChanged(int statusType) {

			final MenuActivity activity = (MenuActivity) getActivity();
			switch (statusType) {
			case WeekView.TYPE_TITLE_NORMAL:
				activity.getKechengActionBar().getRightButton().setVisibility(View.GONE);
				activity.getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_menu);
				// 还原按钮事件
				activity.getKechengActionBar().getLeftButton().setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						activity.getSlidingMenu().showMenu();
					}
				});
				activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				activity.lockKeyInput(false);
				activity.showTitleHintPoint(true);
				showBottomMenu();
				break;
			case WeekView.TYPE_TITLE_PASTER_EDIT:
				activity.getKechengActionBar().getRightButton().setVisibility(View.VISIBLE);
				activity.getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
				activity.getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
				activity.getKechengActionBar().getLeftButton().setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mainWeekView.weekView.spiritPaster.cancelPaste();
					}
				});
				activity.getKechengActionBar().getRightButton().setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mainWeekView.weekView.spiritPaster.confirmPaste();
					}
				});
				activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				hideBottomMenu();
				activity.lockKeyInput(true);
				activity.showTitleHintPoint(false);
				break;

			default:
				break;
			}
		}
	};
	
	private void registerBroad() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(MallFragment.BRODCAST_THEME_CHANGED);
		filter.addAction(MallFragment.BRODCAST_CURRENT_THEME_CHANGED);
		((MenuActivity) getActivity()).registerReceiver(mThemeReceiver, filter);
	}
	
	private void unRegisterBroad(){
		((MenuActivity) getActivity()).unregisterReceiver(mThemeReceiver);
	}
	
	private BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (MallFragment.BRODCAST_THEME_CHANGED.equals(action)) {
				mainWeekView.skinView.refresh();
			} else if (MallFragment.BRODCAST_CURRENT_THEME_CHANGED.equals(action)){
				mainWeekView.skinView.setCurrentWeekStyle(App.getInstance().getCurrentStyleName());
			}
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unRegisterBroad();
	}

	
	@Override
	public void onPause() {
		super.onPause();
		//如果贴纸ing，要取消状态
		if(mainWeekView.weekView.spiritPaster.isChossingPaster()){
			mainWeekView.weekView.spiritPaster.cancelPaste();
		}
	}
	
}
package fm.jihua.kecheng.ui.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.weibo.sdk.android.sso.SsoHandler;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.ui.helper.WheelUtil;
import fm.jihua.common.utils.CommonUtils;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.KechengAppWidgetProvider;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.AccountResult;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.Semester;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.register.RegisterActivity;
import fm.jihua.kecheng.ui.activity.setting.AboutActivity;
import fm.jihua.kecheng.ui.activity.setting.EditAccountActivity;
import fm.jihua.kecheng.ui.activity.setting.RecommendActivity;
import fm.jihua.kecheng.ui.activity.setting.SemestersActivity;
import fm.jihua.kecheng.ui.activity.setting.TextViewerActivity;
import fm.jihua.kecheng.ui.activity.setting.TimeModeActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.AlarmManagerUtil;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.TencentAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

@SuppressLint("CommitPrefEdits")
public class SettingsFragment extends BaseFragment{

	TextView bindRenren;
	TextView bindRenrenDone;
	TextView bindWeibo;
	TextView bindWeiboDone;
	TextView bindTencent;
	TextView bindTencentDone;
	TextView timeSlotLength;
	TextView semesterName;
//	TextView semesterBeginDate;
//	TextView semesterEndDate;
	ToggleButton toggleNotification;
	ToggleButton toggleVacation;
	ToggleButton toggleBeforeClass;
	ToggleButton toggleClassMute;
	ToggleButton toggleRemindMuteBeforeClass;
	TextView notificationTime;
	TextView week;
	TextView wakeupTimeView;
	TextView background;
	TextView highlightPassword;
	TextView highlightUpdate;
	
//	LinearLayout layout_time_mode;
	LinearLayout layout_time_mode_time;
	LinearLayout layout_mute_class_category;
	LinearLayout layout_remind_mute_before_class;
	
	TextView tv_time_mode;
	TextView tv_time_mode_time;
	TextView tv_time_mode_mute_category;

	View timerPickerView;
	Builder timerPickerDialog;
	SimpleDateFormat dateFormat;
	Semester semester;
	App app;
	SharedPreferences mSettings;
	RenrenAuthHelper renrenAuthHelper;
	WeiboAuthHelper weiboAuthHelper;
	TencentAuthHelper tencentAuthHelper;
	DataAdapter mDataAdapter;
	int lastClick;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MobclickAgent.onEvent(parent, "enter_setting_view");
		View view = inflater.inflate(R.layout.setting, container, false);
		app = App.getInstance();
		mSettings = PreferenceManager.getDefaultSharedPreferences(parent);
		renrenAuthHelper = new RenrenAuthHelper(parent);
		weiboAuthHelper = new WeiboAuthHelper(parent);
		tencentAuthHelper = new TencentAuthHelper(parent);
		mDataAdapter = new DataAdapter(parent, new MyDataCallback());
		String pattern="yyyy-MM-dd";
		dateFormat=new SimpleDateFormat(pattern);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViews();
		setListeners();
		if (Const.IS_DEBUG_ENABLE) {
			findViewById(R.id.logout_panel).setVisibility(View.VISIBLE);
		}
//		initViews();
		initTitlebar();
		mDataAdapter.getAccountInfo();
	}

	@Override
	public void onResume() {
		super.onResume();
		initViews();
	}

	void initViews(){
//		semester = app.getDBHelper().getSemester(app.getUserDB());
		semester = app.getDBHelper().getActiveSemester(app.getUserDB());
		User user = app.getMyself();
		refreshAuthInfo();
		bindRenren.postDelayed(new Runnable() {

			@Override
			public void run() {
				refreshAuthInfo();
			}
		}, 500);
		if (semester != null) {
			semesterName.setText(semester.name);
//			semesterBeginDate.setText(dateFormat.format(new Date(semester.begin_time*1000)));
//			semesterEndDate.setText(dateFormat.format(new Date(semester.end_time*1000)));
		}else {
			semester = new Semester();
		}
		week.setText("第"+app.getCurrentWeek(false)+"周");
		toggleVacation(app.isVacation());
		setWakeupTime();
		timeSlotLength.setText(String.valueOf(app.getSlotLength())+"节");
		String notificationTimeValue = mSettings.getString(getString(R.string.notification_time), "-4");
		String[] notificationTimeArray = getResources().getStringArray(R.array.notification_time);
		String[] notificationTimeValueArray = getResources().getStringArray(R.array.notification_time_values);
		int index = CommonUtils.find(notificationTimeValueArray, notificationTimeValue);
		String string = mSettings.getString(getString(R.string.notification_time), null);
		if(string == null){
			string = "未设置";
		} else {
			if (string.startsWith("-")) {
				string = "前一天 "+string.substring(1);
			} else {
				string = "当天 "+string;
			}
		}
		notificationTime.setText(string);
		boolean shouldNotificate = mSettings.getBoolean(getString(R.string.notification), false);
		toggleNotification(shouldNotificate);
		if (ObjectUtils.containsElement(Const.NO_RECOMMEND_CHANNEL, Const.getChannelName(parent))) {
			((View)findViewById(R.id.recommend_parent).getParent()).setVisibility(View.GONE);
		}
		List<String> highlightSettings = app.getUnHandledParams(Const.CONFIG_PARAM_HIGHLIGHT_SETTING);
		if (highlightSettings.contains(Const.CONFIG_PARAM_HIGHLIGHT_SETTING_PASSWORD)) {
			highlightPassword.setVisibility(View.VISIBLE);
		}else {
			highlightPassword.setVisibility(View.GONE);
		}

		if(app.getNewestVersion().compareToIgnoreCase(Const.getAppVersionName(parent)) > 0){
			highlightUpdate.setVisibility(View.VISIBLE);
		}else {
			highlightUpdate.setVisibility(View.GONE);
		}

		((View)findViewById(R.id.check_new_version_parent)).setOnClickListener(new ClickListener());
		if (ObjectUtils.containsElement(Const.NO_UPDATE_CHANNEL, Const.getChannelName(parent))) {
			findViewById(R.id.check_new_version_layout_parent).setVisibility(View.GONE);
		}
		
		initTimeModeLayout();
		checkForNewVersion();
	}

	void checkForNewVersion() {
		// UIUtil.block(parent);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				if (updateStatus == 2 && updateInfo != null) {
					if (updateInfo.hasUpdate) {
						updateStatus = 0;
					} else {
						updateStatus = 1;
					}
				}
				switch (updateStatus) {
				case 0: // has update
					((View) findViewById(R.id.check_new_version_layout_parent)).setVisibility(View.VISIBLE);
					((View) findViewById(R.id.check_new_version_parent)).setTag(updateInfo);
					// UmengUpdateAgent.showUpdateDialog(parent, updateInfo);
					break;
				// case 1: // has no update
				// Toast.makeText(parent, "你的课程格子已是最新版本",
				// Toast.LENGTH_SHORT).show();
				// break;
				// case 2: // none wifi
				// Toast.makeText(parent, "没有wifi连接， 只在wifi下更新",
				// Toast.LENGTH_SHORT).show();
				// break;
				// case 3: // time out
				// Toast.makeText(parent, "网络连接超时", Toast.LENGTH_SHORT).show();
				// break;
				}
				// UIUtil.unblock(parent);
			}
		});
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.update(parent);
	}

	void setWakeupTime(){
		try {
			long wakeupTime = app.getDefaultPreferences().getLong(getString(R.string.wakeup_time), 0);
			if (wakeupTime == 0) {
				wakeupTime = semester.begin_time * 1000;
			}
			if (wakeupTime < System.currentTimeMillis()) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, 7);
//				app.setWakeupTime(calendar.getTimeInMillis());
				wakeupTimeView.setText(dateFormat.format(new Date(calendar.getTimeInMillis())));
			}else {
				wakeupTimeView.setText(dateFormat.format(new Date(wakeupTime)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void refreshAuthInfo(){
		if (renrenAuthHelper.isAuthed()) {
			bindRenren.setVisibility(View.GONE);
			bindRenrenDone.setVisibility(View.VISIBLE);
		}else {
			bindRenren.setVisibility(View.VISIBLE);
			bindRenrenDone.setVisibility(View.GONE);
		}
		if (weiboAuthHelper.isAuthed()) {
			bindWeibo.setVisibility(View.GONE);
			bindWeiboDone.setVisibility(View.VISIBLE);
		}else{
			bindWeibo.setVisibility(View.VISIBLE);
			bindWeiboDone.setVisibility(View.GONE);
		}
		if (tencentAuthHelper.isAuthed()) {
			bindTencent.setVisibility(View.GONE);
			bindTencentDone.setVisibility(View.VISIBLE);
		} else {
			bindTencent.setVisibility(View.VISIBLE);
			bindTencentDone.setVisibility(View.GONE);
		}
	}

	void toggleVacation(boolean enable) {
		toggleVacation.setChecked(enable);
		findViewById(R.id.vacation_on).setVisibility(enable ? View.VISIBLE : View.GONE);
//		findViewById(R.id.vacation_off).setVisibility(enable ? View.GONE : View.VISIBLE);
		week.setText(enable ? "放假中" : "第" + app.getCurrentWeek(false) + "周");
		((View) week.getParent()).setOnClickListener(enable ? null : l);
		Compatibility.setBackground((View) week.getParent(),
				enable ? new ColorDrawable(getResources().getColor(R.color.divider_bg)) : getResources().getDrawable(R.color.white_item_selector_linearlayout_bg));
	}

	void toggleNotification(boolean enable){
		toggleNotification.setChecked(enable);
		if(enable){
			((View)notificationTime.getParent()).setVisibility(View.VISIBLE);
		}else{
			((View)notificationTime.getParent()).setVisibility(View.GONE);
		}
	}

	public void initTitlebar(){
		parent.setTitle(R.string.act_setting_title);
		MenuActivity menuActivity = (MenuActivity) getActivity();
		// menuActivity.getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		menuActivity.getKechengActionBar().getRightButton().setVisibility(View.GONE);
	}

	void findViews(){
		bindRenren = (TextView) findViewById(R.id.bind_renren);
		bindRenrenDone = (TextView) findViewById(R.id.bind_renren_done);
		bindWeibo = (TextView) findViewById(R.id.bind_weibo);
		bindWeiboDone = (TextView) findViewById(R.id.bind_weibo_done);
		bindTencent = (TextView) findViewById(R.id.bind_tencent);
		bindTencentDone = (TextView) findViewById(R.id.bind_tencent_done);
		timeSlotLength = (TextView) findViewById(R.id.time_slot_length);
		semesterName = (TextView) findViewById(R.id.semester_name);
//		semesterBeginDate = (TextView) findViewById(R.id.semester_start_date);
//		semesterEndDate = (TextView) findViewById(R.id.semester_end_date);
		toggleNotification = (ToggleButton) findViewById(R.id.toggle_notification);
		toggleVacation = (ToggleButton) findViewById(R.id.toggle_vacation);
		notificationTime = (TextView) findViewById(R.id.notification_time);
		week = (TextView)findViewById(R.id.week);
		wakeupTimeView = (TextView)findViewById(R.id.wake_up_time);
		background = (TextView)findViewById(R.id.background);
//		feedback = (TextView) findViewById(R.id.feedback);
//		recommend = (TextView)findViewById(R.id.recommend);
//		about = (TextView) findViewById(R.id.about);
//		help = (TextView) findViewById(R.id.help);
		highlightPassword = (TextView)findViewById(R.id.highlight_password);
		highlightUpdate = (TextView)findViewById(R.id.highlight_update);
		
		tv_time_mode=(TextView) findViewById(R.id.time_mode);
		tv_time_mode_time=(TextView) findViewById(R.id.time_mode_before_class_time);
		tv_time_mode_mute_category=(TextView) findViewById(R.id.time_mode_class_mute_category);
		layout_time_mode_time=(LinearLayout) findViewById(R.id.time_mode_before_class_time_parent);
		layout_mute_class_category=(LinearLayout) findViewById(R.id.time_mode_class_mute_category_parent);
		layout_remind_mute_before_class = (LinearLayout) findViewById(R.id.remind_mute_before_class_category_parent);
		
		toggleBeforeClass = (ToggleButton) findViewById(R.id.toggle_before_class);
		toggleClassMute = (ToggleButton) findViewById(R.id.toggle_class_mute);
		toggleRemindMuteBeforeClass = (ToggleButton)findViewById(R.id.toggle_remind_mute_beforeclass);
	}

	ClickListener l = new ClickListener();
	
	void setListeners(){
		
		((View)bindRenren.getParent()).setOnClickListener(l);
		((View)bindWeibo.getParent()).setOnClickListener(l);
		((View)bindTencent.getParent()).setOnClickListener(l);
		((View)timeSlotLength.getParent()).setOnClickListener(l);
		((View)semesterName.getParent()).setOnClickListener(l);
//		((View)semesterBeginDate.getParent()).setOnClickListener(l);
//		((View)semesterEndDate.getParent()).setOnClickListener(l);
		((View)notificationTime.getParent()).setOnClickListener(l);
		((View)week.getParent()).setOnClickListener(l);
		((View)wakeupTimeView.getParent()).setOnClickListener(l);
		((View)background.getParent()).setOnClickListener(l);
		((View)tv_time_mode.getParent()).setOnClickListener(l);
		((View)tv_time_mode_time.getParent()).setOnClickListener(l);
		((View)tv_time_mode_mute_category.getParent()).setOnClickListener(l);
		findViewById(R.id.edit_account_parent).setOnClickListener(l);
		findViewById(R.id.recommend_parent).setOnClickListener(l);
		findViewById(R.id.feedback_parent).setOnClickListener(l);
		findViewById(R.id.help_parent).setOnClickListener(l);
		findViewById(R.id.about_parent).setOnClickListener(l);
		findViewById(R.id.logout_parent).setOnClickListener(l);
		findViewById(R.id.clear_cache_parent).setOnClickListener(l);
		toggleNotification.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = mSettings.edit();
				editor.putBoolean(getString(R.string.notification), isChecked);
				App.commitEditor(editor);
				toggleNotification(isChecked);
			}
		});
		toggleVacation.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = mSettings.edit();
				editor.putBoolean(getString(R.string.vacation), isChecked);
				if (isChecked) {
					checkHaveToastVacation();
					setWakeupTime();
					try {
						long time = dateFormat.parse(wakeupTimeView.getText().toString()).getTime();
						app.setWakeupTime(time);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					app.setCurrentWeek(app.getCurrentWeek(true));
				}
				App.commitEditor(editor);
				toggleVacation(isChecked);
			}
		});
		
		toggleBeforeClass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				Editor editor = mSettings.edit();
				editor.putBoolean(getString(R.string.time_mode_before_class), isChecked);
				App.commitEditor(editor);
				AlarmManagerUtil.getInstance().resetAlarmByTimeMode();
				if (isChecked) {
					MobclickAgent.onEvent(parent, "event_open_before_alert_succeed");
					showFirstDialod4TimeMode("提前提醒");
				}
				layout_time_mode_time.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		});
		toggleClassMute.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				Editor editor = mSettings.edit();
				editor.putBoolean(getString(R.string.time_mode_class_mute), isChecked);
				App.commitEditor(editor);
				AlarmManagerUtil.getInstance().resetAlarmByTimeMode();
				if (isChecked) {
					MobclickAgent.onEvent(parent, "event_open_course_silent");
					showFirstDialod4TimeMode("上课静音");
				}
				layout_mute_class_category.setVisibility(isChecked ? View.VISIBLE : View.GONE);
				layout_remind_mute_before_class.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		});
		toggleRemindMuteBeforeClass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = mSettings.edit();
				editor.putBoolean(getString(R.string.remind_mute_before_class), isChecked);
				App.commitEditor(editor);
				AlarmManagerUtil.getInstance().resetAlarmByTimeMode();
			 }
		});
	}

	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			lastClick = v.getId();
			try {
				switch (v.getId()) {
				case R.id.check_new_version_parent:
					// checkForNewVersion();
					UpdateResponse updateInfo = (UpdateResponse) ((View)findViewById(R.id.check_new_version_parent)).getTag();
					UmengUpdateAgent.showUpdateDialog(parent, updateInfo);
					break;
				case R.id.bind_renren_parent:
					if (!renrenAuthHelper.isAuthed()) {
						renrenAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}else {
						showDeleteDialog(renrenAuthHelper);
					}
					break;
				case R.id.bind_tencent_parent:
					if (!tencentAuthHelper.isAuthed()) {
						tencentAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}else {
						showDeleteDialog(tencentAuthHelper);
					}
					break;
				case R.id.bind_weibo_parent:
					if (!weiboAuthHelper.isAuthed()) {
						weiboAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}else {
						showDeleteDialog(weiboAuthHelper);
					}
					break;
				case R.id.edit_account_parent:
					List<String> highlightSettings = app.getUnHandledParams(Const.CONFIG_PARAM_HIGHLIGHT_SETTING);
					if (highlightSettings.contains(Const.CONFIG_PARAM_HIGHLIGHT_SETTING_PASSWORD)) {
						App app = App.getInstance();
						app.handleConfigParam(Const.CONFIG_PARAM_HIGHLIGHT_SETTING,Const.CONFIG_PARAM_HIGHLIGHT_SETTING_PASSWORD);
					}
					startActivity(new Intent(parent, EditAccountActivity.class));
					break;
				case R.id.time_slot_length_parent:
					showTimeSlotLengthDialog();
					break;
				case R.id.semester_name_parent:
					startSemestersActivity();
					break;
				case R.id.wake_up_time_parent:
					showTimePicker(wakeupTimeView);
					break;
//			case R.id.semester_end_date_parent:
//				showTimePicker(semesterEndDate);
//				break;
				case R.id.week_parent:
					showWeekSelectorDialog();
					break;
				case R.id.notification_time_parent:
					showNotificationTimePicker(notificationTime);
//				showNotificationTimeDialog();
					break;
				case R.id.feedback_parent:
					UMFeedbackService.setGoBackButtonVisible();
					UMFeedbackService.openUmengFeedbackSDK(parent);
					break;
				case R.id.recommend_parent:
					startActivity(new Intent(parent, RecommendActivity.class));
					break;
				case R.id.about_parent:
					startActivity(new Intent(parent, AboutActivity.class));
					break;
				case R.id.help_parent:
					Intent intent = new Intent(parent, TextViewerActivity.class);
					intent.putExtra(Const.INTENT_TEXT_CONTENT, getResources().getString(R.string.help_content));
					intent.putExtra(Const.INTENT_TITLE_CONTENT, "用户帮助");
					startActivity(intent);
					break;
				case R.id.time_mode_parent:
					startActivity(new Intent(parent,TimeModeActivity.class));
					break;
				case R.id.time_mode_before_class_time_parent:
					showTimeModeDialog();
					break;
				case R.id.time_mode_class_mute_category_parent:
					showTimeModeMuteCategoryDialog();
					break;
				case R.id.clear_cache_parent:
					App.getInstance().clearCache();
					Hint.showTipsLong(parent, "缓存清除成功");
					break;
				case R.id.logout_parent:
					MobclickAgent.onEvent(parent, "action_logoff");
					showLogoutDialog();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				StringBuffer buffer=new StringBuffer();
				buffer.append("SettingsFragment_onClick Message="+
						e.getMessage());
				StackTraceElement[] stackTrace = e.getStackTrace();
				for (StackTraceElement stackTraceElement : stackTrace) {
					buffer.append("\n"+stackTraceElement.toString());
				}
				MobclickAgent.reportError(getActivity(),buffer.toString());

				e.printStackTrace();
			}
		}
	}
	
	private void showLogoutDialog(){
		new AlertDialog.Builder(parent)
		.setTitle("提示")
		.setMessage("是否注销当前格子号？")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						app.logOut();
						Intent intent = new Intent(parent, RegisterActivity.class);
//						intent.putExtra("SHOW_LOGIN_VIEW", true);
						parent.startActivity(intent);
						parent.finish();
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						// 按钮事件
						dialoginterface.dismiss();
					}
				}).show();
	}
	
	void showNotificationTimePicker(final TextView editText){
		try {
			String string = mSettings.getString(getString(R.string.notification_time), null);
			final View timerPickerView = WheelUtil.getCourseRemindDatePicker(parent, string);
			DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == -1) {
						String timeString = WheelUtil.getCourseRemindDateAndTime(timerPickerView);
				    	Editor editor = mSettings.edit();
				    	editor.putString(getString(R.string.notification_time), timeString);
				    	App.commitEditor(editor);
						if(timeString == null){
							timeString = "未设置";
						} else {
							if (timeString.startsWith("-")) {
								timeString = "前一天 "+timeString.substring(1);
							} else {
								timeString = "当天 "+timeString;
							}
						}
						editText.setText(timeString);
						dialog.dismiss();
					}
					dialog.dismiss();
				}
			};
			new AlertDialog.Builder(parent).setTitle("选择提醒时间").setView(timerPickerView)
			.setPositiveButton("确定", clickListener)
			.setNegativeButton("取消", clickListener).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showDeleteDialog(final AuthHelper helper){
		new AlertDialog.Builder(parent)
		.setTitle("提示")
		.setMessage("是否解除绑定？解除绑定后你将不能和朋友分享你的课程信息到"+ helper.getTypeName() + "。")
		.setPositiveButton("解除绑定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						helper.unBind();
						refreshAuthInfo();
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						// 按钮事件
						dialoginterface.dismiss();
					}
				}).show();
	}

	void showTimePicker(final TextView editText){
		try {
			timerPickerView = parent.getLayoutInflater().inflate(R.layout.date_picker, null);
			String value = editText.getText().toString().trim();
			Date dt = (value == null || value.length() == 0) ? new Date() : dateFormat.parse(value);
			initTimePicker(timerPickerView, dt);
			DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == -1) {
						String timeString = getTimeString();
						editText.setText(timeString);
						try {
							long time = dateFormat.parse(timeString).getTime();
							if (editText == wakeupTimeView) {
								if (time > System.currentTimeMillis()) {
									App app = App.getInstance();
									app.setWakeupTime(time);
								}else {
									Hint.showTipsShort(parent, "假期结束时间不得早于当前时间");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					dialog.dismiss();
				}
			};
			new AlertDialog.Builder(parent).setTitle("选择时间").setView(timerPickerView)
			.setPositiveButton("确定", clickListener)
			.setNegativeButton("取消", clickListener).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startSemestersActivity(){
		startActivity(new Intent(parent, SemestersActivity.class));
//		final EditText editText = new EditText(this);
//		editText.setText(semesterName.getText());
//		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == -1) {
//					semesterName.setText(editText.getText());
//					semester.name = editText.getText().toString();
//					saveSemester();
//				}
//				dialog.dismiss();
//			}
//		};
//		new AlertDialog.Builder(this).setTitle("请输入学期名称").setView(editText)
//		.setPositiveButton("确定", clickListener)
//		.setNegativeButton("取消", clickListener).show();
	}
	
	void initTimeModeLayout() {
		tv_time_mode_time.setText(String.format(getString(R.string.time_mode_string_textview), String.valueOf(App.getInstance().getBeforeClassTime())));
		tv_time_mode_mute_category.setText(getResources().getStringArray(R.array.time_mode_mute_category)[App.getInstance().getTimeMuteCategory()]);
		toggleBeforeClass.setChecked(mSettings.getBoolean(getString(R.string.time_mode_before_class), false));
		toggleClassMute.setChecked(mSettings.getBoolean(getString(R.string.time_mode_class_mute), false));
		toggleRemindMuteBeforeClass.setChecked(mSettings.getBoolean(getString(R.string.remind_mute_before_class), false));
	}
	
	void showTimeSlotLengthDialog(){
		final String[] choices=getResources().getStringArray(R.array.time_slot_length);
		final String[] values = getResources().getStringArray(R.array.time_slot_length_value);
		DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	timeSlotLength.setText(choices[which]);
		    	app.setSlotLength(Integer.parseInt(values[which]));
		    	getActivity().sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
		    	dialog.dismiss();
		    }
		};
        //包含多个选项的对话框
		String value = String.valueOf(app.getSlotLength());
		int truelyIndex = CommonUtils.find(values, value);
		int index = CommonUtils.find(values, value) == -1 ? 0 : truelyIndex;
		AlertDialog dialog = new AlertDialog.Builder(parent)
	            .setTitle(R.string.choice_slot_length).setSingleChoiceItems(choices, index, onselect).create();
		dialog.show();
	}

	void showWeekSelectorDialog(){

		try {
			final WheelView wheel = new WheelView(parent);
//			String value = week.getText().toString().trim();
			List<String> weeks = new ArrayList<String>();
			for (int i = 0; i < Const.MAX_WEEK; i++) {
				weeks.add("第" + (i+1) +"周");
			}
			final App app = App.getInstance();
			int currentWeek = app.getCurrentWeek(false);
			ArrayWheelAdapter<String> weeksAdapter =
		            new ArrayWheelAdapter<String>(parent, weeks.toArray(new String[weeks.size()]));
			wheel.setViewAdapter(weeksAdapter);
			wheel.setCurrentItem(currentWeek-1);
			DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == -1) {
						int currentWeek = wheel.getCurrentItem()+1;
						week.setText("第"+currentWeek+"周");
						if(app.getCurrentWeek(false) != currentWeek){
							parent.sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
							app.setCurrentWeek(currentWeek);
							mDataAdapter.updateSemesterBeginDate(app.getBeginDate());
						}
					}
					dialog.dismiss();
				}
			};
			new AlertDialog.Builder(parent).setTitle("请选择当前周数").setView(wheel)
			.setPositiveButton("确定", clickListener)
			.setNegativeButton("取消", clickListener).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkHaveToastVacation(){
		boolean toastVacation = mSettings.getBoolean("TOASTVACATION", false);
		if(!toastVacation){
			new AlertDialog.Builder(getActivity()).setTitle("格子提醒").setMessage("开启放假模式后，将不再有上课提醒和周数更新哦").setNegativeButton("我知道了", null).create().show();
			mSettings.edit().putBoolean("TOASTVACATION", true).commit();
		}
	}

	void showNotificationTimeDialog(){
		final String[] choices=getResources().getStringArray(R.array.notification_time);
		final String[] values=getResources().getStringArray(R.array.notification_time_values);
		DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	String value = values[which];
		    	notificationTime.setText(choices[which]);
		    	Editor editor = mSettings.edit();
		    	editor.putString(getString(R.string.notification_time), value);
		    	App.commitEditor(editor);
		    	dialog.dismiss();
		    }
		};
        //包含多个选项的对话框
		String value = mSettings.getString(getString(R.string.notification_time), "-4");
		int index = CommonUtils.find(values, value);
		AlertDialog dialog = new AlertDialog.Builder(parent)
	            .setTitle("请选择提醒时间").setSingleChoiceItems(choices, index, onselect).create();
		dialog.show();
	}
	
	void showTimeModeDialog(){
		final String[] choices=getResources().getStringArray(R.array.time_mode_class_before);
//		final String[] values = getResources().getStringArray(R.array.time_slot_length_value);
		DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
				String string = choices[which];
				tv_time_mode_time.setText(String.format(getString(R.string.time_mode_string_textview), string));
				app.setBeforeClassTime(Integer.parseInt(string));
				AlarmManagerUtil.getInstance().resetAlarmByTimeMode();
				dialog.dismiss();
		    }
		};
		int time = App.getInstance().getBeforeClassTime();
		int index = CommonUtils.find(choices, String.valueOf(time));
		AlertDialog dialog = new AlertDialog.Builder(parent)
	            .setTitle(R.string.time_mode_befroe_class_time).setSingleChoiceItems(choices, index, onselect).create();
		dialog.show();
	}
	
	void showTimeModeMuteCategoryDialog(){
		final String[] choices=getResources().getStringArray(R.array.time_mode_mute_category);
//		final String[] values = getResources().getStringArray(R.array.time_slot_length_value);
		DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
				tv_time_mode_mute_category.setText(choices[which]);
				app.setTimeMuteCategory(which);
				dialog.dismiss();
		    }
		};
		int category = App.getInstance().getTimeMuteCategory();
		AlertDialog dialog = new AlertDialog.Builder(parent)
	            .setTitle(R.string.time_mode_mute_category).setSingleChoiceItems(choices, category, onselect).create();
		dialog.show();
	}

	void saveSemester(){
		semester.modified = true;
		if (semester.end_time > semester.begin_time ) {
			app.getDBHelper().saveSemester(app.getUserDB(), semester);
		}else {
			Hint.showTipsLong(parent, "学期时间设置错误");
		}
	}

	private void initTimePicker(View view, Date dt){

		final WheelView month = (WheelView) view.findViewById(R.id.month_picker);
        final WheelView year = (WheelView) view.findViewById(R.id.year_picker);
        final WheelView day = (WheelView) view.findViewById(R.id.day_picker);

        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(year, month, day);
            }
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = new String[] {"一月", "二月", "三月", "四月", "五月", "六月",
        		"七月", "八月", "九月", "十月", "十一月", "十二月"};
        month.setViewAdapter(new DateArrayAdapter(parent, months, curMonth));
        month.setCurrentItem(curMonth);
        month.addChangingListener(listener);

        // year
        int curYear = calendar.get(Calendar.YEAR);
        year.setViewAdapter(new DateNumericAdapter(parent, curYear - 1, curYear + 1, 1));
        year.setCurrentItem(1);
        year.addChangingListener(listener);

        //day
        updateDays(year, month, day);
        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
	}

	String getTimeString(){
		if (timerPickerView != null) {
			final WheelView month = (WheelView) timerPickerView.findViewById(R.id.month_picker);
	        final WheelView year = (WheelView) timerPickerView.findViewById(R.id.year_picker);
	        final WheelView day = (WheelView) timerPickerView.findViewById(R.id.day_picker);
	        Calendar calendar = Calendar.getInstance();
	        Date dt = new Date(calendar.get(Calendar.YEAR)-1+year.getCurrentItem()-1900, month.getCurrentItem(), day.getCurrentItem()+1);
	        return dateFormat.format(dt);
		}
		return "";
	}

	void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(parent, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

	/**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
//            setTextSize(16);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
//                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

	/**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
//            setTextSize(16);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
//                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK){
			return;
		}
		App app = App.getInstance();
		try {
			User user = app.getMyself();
			switch (requestCode) {
			case SsoHandler.DEFAULT_AUTH_ACTIVITY_CODE:
				UIUtil.block(parent);
				weiboAuthHelper.onActivityResult(requestCode, resultCode, data);
				break;
			default:
				if(requestCode != Const.WEB_VIEWER_WITH_RESULT && requestCode != SsoHandler.DEFAULT_AUTH_ACTIVITY_CODE){
					tencentAuthHelper.onActivityResult(requestCode, resultCode, data);
				}
				break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(parent);
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_ACCOUNT_INFO:{
				AccountResult result = (AccountResult)msg.obj;
				if (result != null && result.success) {
					try {
						((TextView)findViewById(R.id.edit_account)).setText(result.gezi_id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
				break;
			case DataAdapter.MESSAGE_UPDATE_SEMESTER_BEGIN_DATE:
				BaseResult result = (BaseResult)msg.obj;
				if (result != null && result.success) {
					Hint.showTipsLong(parent, "上传成功");
				}
				break;
			default:
				break;
			}
		}
	}

	private class MySNSCallback implements SNSCallback {

		private int mScope = 0;

		public MySNSCallback(int scope) {
            mScope = scope;
        }

		@Override
		public void onComplete(AuthHelper authHelper, Object data) {
			switch (mScope) {
			case SNSCallback.AUTH:
				authHelper.bind(new MySNSCallback(SNSCallback.BIND));
				break;
			case SNSCallback.BIND:
				int helperCategory = authHelper.getType();
				if (helperCategory == Const.RENREN) {
					bindRenren.setVisibility(View.GONE);
					bindRenrenDone.setVisibility(View.VISIBLE);
				} else if (helperCategory == Const.WEIBO) {
					bindWeibo.setVisibility(View.GONE);
					bindWeiboDone.setVisibility(View.VISIBLE);
					weiboAuthHelper
							.update(parent.getResources().getString(R.string.first_bind_sns) + " http://app.sina.com.cn/appdetail.php?appID=118442", new MySNSCallback(SNSCallback.UPLOAD));
				} else if (helperCategory == Const.QQ) {
					bindTencent.setVisibility(View.GONE);
					bindTencentDone.setVisibility(View.VISIBLE);
					tencentAuthHelper.update(parent.getResources().getString(R.string.first_bind_sns), "", "http://hs.kechenggezi.com/", "http://img.kechenggezi.com/app/icon_114.png", "", "", new MySNSCallback(SNSCallback.UPLOAD));
				}
				break;
			case SNSCallback.UPLOAD:
				if (authHelper.getType() == Const.WEIBO) {
					MobclickAgent.onEvent(parent, "event_share_weibo_succeed", "bind");
				} else if (authHelper.getType() == Const.QQ) {
					MobclickAgent.onEvent(parent, "event_share_qq_succeed", "bind");
				}
				break;
			}
		}

		@Override
		public void onError(AuthHelper authHelper) {
			UIUtil.unblock(parent);
		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}
	
	private void showFirstDialod4TimeMode(String key) {
		if (App.getInstance().checkIsFirstStatus(Const.FIRST_SETTING_TIMEMODE_DIALOG) && App.getInstance().checkIsFirstStatus(Const.FIRST_ENTER2TIMEMODE)) {
			String string = String.format(getString(R.string.message_timemode), key);
			new AlertDialog.Builder(getActivity()).setTitle("格子提醒").setMessage(string).setNegativeButton("我知道了", null).create().show();
			App.getInstance().cancleFirstStatus(Const.FIRST_SETTING_TIMEMODE_DIALOG);
		}
	}
}
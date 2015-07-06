package fm.jihua.kecheng.ui.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.Hint;
import fm.jihua.common.ui.helper.PhotoPicker;
import fm.jihua.common.utils.CameraUtil;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.OfflineData;
import fm.jihua.kecheng.rest.entities.sticker.Sticker;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.rest.entities.weekstyle.Theme;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.EventActivity;
import fm.jihua.kecheng.ui.activity.course.AddCourseActivity;
import fm.jihua.kecheng.ui.activity.course.CourseActivity;
import fm.jihua.kecheng.ui.activity.course.ImportCoursesActivity;
import fm.jihua.kecheng.ui.activity.home.ChooseCoursesActivity;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.sticker.ChooseStickerActivity;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialManager;
import fm.jihua.kecheng.ui.activity.tutorial.WeekViewTutorialUtils;
import fm.jihua.kecheng.ui.adapter.ChooseStickerPagerAdapter;
import fm.jihua.kecheng.ui.fragment.MainFragment;
import fm.jihua.kecheng.ui.helper.BackgroundEditor;
import fm.jihua.kecheng.ui.helper.DialogUtils;
import fm.jihua.kecheng.ui.widget.CustomScrollView;
import fm.jihua.kecheng.ui.widget.ThemeView;
import fm.jihua.kecheng.ui.widget.ThemeView.OnSkinViewClickListener;
import fm.jihua.kecheng.ui.widget.WeekView;
import fm.jihua.kecheng.ui.widget.WeekView.OnCourseClickListener;
import fm.jihua.kecheng.utils.AnimationUtils;
import fm.jihua.kecheng.utils.AnimationUtils.OnAnimationEndListener;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;
import fm.jihua.kecheng.utils.EventHelper;
import fm.jihua.kecheng.utils.SemiCircleDrawable;
import fm.jihua.kecheng.utils.SemiCircleDrawable.Direction;
import fm.jihua.kecheng_hs.R;

public class MainWeekView extends BaseViewGroup implements OnCourseClickListener{
	public WeekView weekView;
	final List<Course> mCourses = new ArrayList<Course>();
	final List<Event> mEvents = new ArrayList<Event>();
	MenuActivity parent;
	CustomScrollView scrollView;
	RelativeLayout weekViewParent;
	public ThemeView skinView;
	BackgroundEditor bgEditor;

	public long lastModified;
	MainFragment fragment;
	WeekViewTutorialUtils tutorialUtils;
	RelativeLayout menuBarLayout;


	public MainWeekView(Fragment context) {
		super(context);

		fragment = (MainFragment) context;
		parent = (MenuActivity) getContext();
		initTitleBar();
		findViews();
		initMenuView();
		initViews();
	}

	@Override
	public void initTitleBar() {
		App app = (App) getContext().getApplicationContext();
		parent.setTitle(app.getCurrentWeekName());
		parent.getKechengActionBar().getRightButton().setVisibility(View.GONE);
		parent.getKechengActionBar().getLeftButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (menuBarLayout.getVisibility() == View.VISIBLE) {
					toggleMenu(false, null);
				}
				parent.getSlidingMenu().showMenu();
			}
		});
		app.setDefaultCourseView("week");
	}
	
	public WeekViewTutorialUtils getTutorialUtils(){
		return tutorialUtils;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void findViews() {
		// scrollView.setFlingEnabled(false);
		inflate(getContext(), R.layout.week_view, this);
		
		weekView = (WeekView) findViewById(R.id.weekview);
		scrollView = (CustomScrollView) findViewById(R.id.weekview_parent);
		weekView.initSpecialView(scrollView);
		skinView = (ThemeView) findViewById(R.id.skinview);
		weekViewParent = (RelativeLayout) findViewById(R.id.main_parent);
		menuBarLayout = (RelativeLayout) findViewById(R.id.menu_bar);
		
		if (Compatibility.isCompatible(11)) {
			menuBarLayout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	void initMenuView() {
		SemiCircleDrawable sCircleDrawable = new SemiCircleDrawable();
		sCircleDrawable.setAlpha(200);
		Compatibility.setBackground(menuBarLayout, sCircleDrawable);
		SemiCircleDrawable sCircleDrawable1 = new SemiCircleDrawable(getResources().getColor(R.color.textcolor_66), Direction.TOP);
		sCircleDrawable1.setAlpha(200);
		Compatibility.setBackground(findViewById(R.id.toggle_menu_bg), sCircleDrawable1);
		Compatibility.setAlpha((ImageView) findViewById(R.id.toggle_menu), 200);
		Compatibility.setAlpha((ImageView) findViewById(R.id.dot_add_course), 200);
		Compatibility.setAlpha((ImageView) findViewById(R.id.dot_theme), 200);
		Compatibility.setAlpha((ImageView) findViewById(R.id.dot_share_course), 200);
		Compatibility.setAlpha((ImageView) findViewById(R.id.dot_paster), 200);
	}


	void initViews() {
		setListeners();
		skinView.setOnSkinViewClickListener(new OnSkinViewClickListener() {

			@Override
			public void onClick(Theme weekStyleBean, int position) {
				if (position == 0 && (ObjectUtils.nullSafeEquals(App.getInstance().getCurrentStyleName(), weekStyleBean.name) || !BackgroundEditor.CUSTOM_BACKGROUND.exists())) {
					MobclickAgent.onEvent(getContext(), "action_custom_bg");
					bgEditor = new BackgroundEditor(fragment, scrollView.getWidth(), scrollView.getHeight());
					bgEditor.editPhotoForCustom(MainWeekView.this);
				}else {
					weekView.spiritStyle.setWeekStyleBean(weekStyleBean);
					saveTheme(weekStyleBean);
				}
			}
		});
	}
	
	DataAdapter dataAdapter;
	
	void saveTheme(Theme weekStyleBean) {
		final App app = (App) fragment.getActivity().getApplication();
		app.setCurrentStyleName(weekStyleBean.name);
		dataAdapter = new DataAdapter(parent, new DataCallback() {

			@Override
			public void callback(Message msg) {
				if (msg.what == DataAdapter.MESSAGE_CHANGED_THEME) {
					BaseResult baseResult = (BaseResult) msg.obj;
					//如果没有上传成功，下次进入再提交一次
					if(baseResult == null || !baseResult.success){
						AppLogger.i("theme upload is failed");
					}
				}
			}
		});
		dataAdapter.changeTheme(weekStyleBean.name);
	}
	
	public void setData(List<Course> courses, List<Event> events) {
		mCourses.clear();
		mCourses.addAll(courses);
		mEvents.clear();
		mEvents.addAll(events);
		showWeekView();
	}

	private void showWeekView() {
		Log.d(this.getClass().getSimpleName(), "showWeekView");

		findViewById(R.id.add_course_tip).setVisibility(mCourses.size() == 0 ? View.VISIBLE : View.GONE);
		
		resetTimeSlotInfo();
		
		List<CourseBlock> courseBlocks = getDataForWeekView(mCourses, mEvents);
		List<UserSticker> pasters = getUserStickerList();
		// addTestData(courseBlocks, pasters);
		weekView.setData(courseBlocks, pasters, App.getInstance().getCurrentStyleName(), 0, true);
//		skinView.setCurrentWeekStyle(App.getInstance().getCurrentStyleName());
		//show tutorial
		if (mCourses.size() > 0) {
			//hack
			MenuActivity menuActivity = (MenuActivity)parent;
			menuActivity.getSlidingMenu().setOnOpenListener(new OnOpenListener() {
				
				@Override
				public void onOpen() {
					if (tutorialUtils != null) {
						tutorialUtils.stopTutorials();
					}
				}
			});
			if (tutorialUtils == null) {
				if (!menuActivity.getSlidingMenu().isMenuShowing()) {
					tutorialUtils = new WeekViewTutorialUtils(getContext(), weekView.spiritStyle);
				}
			}
		}
	}
	
	public void resetTimeSlotInfo(){
		int timeSlot = App.mTimeSlotLength > 8 ? App.mTimeSlotLength : 8;
		weekView.setTimeSlot(timeSlot);
	}
	
	void addTestData(List<CourseBlock> courseBlocks, List<UserSticker> pasters){
		//Test conflict courses
		 CourseBlock courseBlock = (CourseBlock) courseBlocks.get(1).clone();
		 courseBlock.name = "tfsadfasdf";
		 courseBlock.id = 0;
		 courseBlocks.add(1,courseBlock);
		
		//Test 课在最右边
//		CourseBlock lastCourseBlock = null;
//		for (CourseBlock courseBlock : courseBlocks) {
//			//最右
////			if (courseBlock.day_of_week == 6) {
//			if (courseBlock.start_slot == 12) {
//				lastCourseBlock = courseBlock;
//				courseBlock.active = false;
//				break;
//			}
//		}
		
//		if (lastCourseBlock != null) {
//			courseBlocks.clear();
//			courseBlocks.add(lastCourseBlock);
//		}

		//Test paster
//		Paster paster = AssertgetInfoTuotuo().get(1);
//		paster.setPoint(4, 0);
//		pasters.add(paster);
	}

	@Override
	public void refreshUI() {
	}

	void setListeners() {
		findViewById(R.id.add_course_tip).setOnClickListener(listener);
		scrollView.setOnClickListener(listener);
		weekView.setOnClickListener(listener);
		weekView.setOnChildClickListener(this);
		findViewById(R.id.toggle_menu).setOnClickListener(listener);
		findViewById(R.id.add_course).setOnClickListener(listener);
		findViewById(R.id.theme).setOnClickListener(listener);
		findViewById(R.id.share_course).setOnClickListener(listener);
		findViewById(R.id.paster).setOnClickListener(listener);
		findViewById(R.id.dot_add_course).setOnClickListener(listener);
		findViewById(R.id.dot_theme).setOnClickListener(listener);
		findViewById(R.id.dot_share_course).setOnClickListener(listener);
		findViewById(R.id.dot_paster).setOnClickListener(listener);
		findViewById(R.id.menu_bar_container).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (AnimationUtils.animating.get() && ObjectUtils.nullSafeEquals(menuBarLayout.getTag(), GOING_SHOW)) {
					return true;
				}
				if (menuBarLayout.getVisibility() == View.VISIBLE) {
					toggleMenu(null);
					return true;
				}
				if (skinView.getVisibility() == View.VISIBLE) {
					skinView.startAnimation(AnimationUtils.getInstance().createExitFromBottomAnim());
					skinView.setVisibility(View.GONE);
				}
				return false;
			}
		});
		final View toggleMenu = findViewById(R.id.toggle_menu);
		menuBarLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				toggleMenu(null);
				int centerX = menuBarLayout.getWidth() / 2;
				int centerY = menuBarLayout.getHeight();
				int length = (int) Math.sqrt(Math.pow((event.getX() - centerX), 2) + Math.pow((event.getY() - centerY), 2));
				if (menuBarLayout.getVisibility() == View.VISIBLE && length < menuBarLayout.getHeight()) {
					return true;
				}
				return false;
			}
		});
		toggleMenu.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int centerX = toggleMenu.getWidth() / 2;
				int centerY = toggleMenu.getHeight();
				int length = (int) Math.sqrt(Math.pow((event.getX() - centerX), 2) + Math.pow((event.getY() - centerY), 2));
				if (length > toggleMenu.getHeight()) {
					return true;
				}
				return false;
			}
		});
	}

	private final boolean GOING_HIDE = true;
	private final Boolean GOING_SHOW = null;
	
	public void toggleMenu(OnAnimationEndListener endListener) {
		toggleMenu(true, endListener);
	}

	void toggleMenu(boolean animate, OnAnimationEndListener endListener) {
		if (menuBarLayout.getVisibility() == View.VISIBLE) {
			((ImageView) findViewById(R.id.toggle_menu)).setImageResource(R.drawable.float_menu_icon);
			AnimationUtils.startAnimationRotate(((ImageView) findViewById(R.id.toggle_menu)), 400, 0, null, 45);
			if (animate) {
				AnimationUtils.startAnimationOUT((RelativeLayout) menuBarLayout, 400, 0, endListener);
				menuBarLayout.setTag(GOING_HIDE);
			}else {
				menuBarLayout.setVisibility(View.GONE);
			}
		} else {
			((ImageView) findViewById(R.id.toggle_menu)).setImageBitmap(getMenuIcon());
			AnimationUtils.startAnimationRotate(((ImageView) findViewById(R.id.toggle_menu)), 400, 0, null, -45);
			if (animate) {
				AnimationUtils.startAnimationIN((RelativeLayout) menuBarLayout, 400, endListener);
				menuBarLayout.setTag(GOING_SHOW);
			}else {
				menuBarLayout.setVisibility(View.VISIBLE);
			}
		}
	}
	
	Bitmap getMenuIcon() {
		Resources res = this.getContext().getResources();
		Bitmap img = BitmapFactory.decodeResource(res, R.drawable.float_menu_icon);
		Matrix matrix = new Matrix();
		matrix.postRotate(45); /* 翻转45度 */
		int width = img.getWidth();
		int height = img.getHeight();
		return Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
	}
	
	public RelativeLayout getMenuBar(){
		return this.menuBarLayout;
	}

	OnTouchListener returnTrueOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}
	};
	
	private void startAddCourseActivity(){
		Intent intent = new Intent();
		App app = (App) parent.getApplication();
		boolean enableImport = app.isSupportImport();
		if(enableImport && !app.isKnowImportCourse()){
			intent.setClass(getContext(), ImportCoursesActivity.class);
		} else {
			intent.setClass(getContext(), AddCourseActivity.class);
		}
		((Activity) getContext()).startActivity(intent);
	}


	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_course_tip: 
				startAddCourseActivity();
				break;
			case R.id.weekview:
			case R.id.weekview_parent:
				// if (shareView.getVisibility() == View.VISIBLE) {
				// shareView.setVisibility(View.GONE);
				// } else if(findViewById(R.id.custom_bg_panel).getVisibility()
				// == View.VISIBLE){
				// findViewById(R.id.custom_bg_panel).setVisibility(View.GONE);
				// }else {
				// if(mCourses.size() > 0){
				// shareView.setVisibility(View.VISIBLE);
				// }
				// }
				break;
			case R.id.toggle_menu: {
				if (AnimationUtils.animating.get()) {
					break;
				}
				toggleMenu(null);
				break;
			}
			case R.id.share_course:
			case R.id.dot_share_course: {
				MobclickAgent.onEvent(getContext(), "action_share_course");
				toggleMenu(true, null);
				App app = (App) parent.getApplication();
				app.getDBHelper().getActiveSemester(app.getUserDB());
				//更改weekViewParent的宽高，在获得图片之后通过getTag设置回来
				weekViewParent.setTag(weekViewParent.getLayoutParams());
//				weekViewParent.setLayoutParams();
				DialogUtils.showShareWeekDialog(parent, weekViewParent, app.getDBHelper().getActiveSemester(app.getUserDB()).name, new LinearLayout.LayoutParams(weekView.getWeekViewParams().weekViewWidth, weekView.getWeekViewParams().weekViewHeight));
				break;
			}
			case R.id.dot_add_course:
			case R.id.add_course: {
				MobclickAgent.onEvent(getContext(), "action_add_course");
				toggleMenu(false, null);
				startAddCourseActivity();
				break;
			}
			case R.id.theme:
			case R.id.dot_theme: {
				MobclickAgent.onEvent(getContext(), "action_skin");
				toggleMenu(new OnAnimationEndListener() {
					@Override
					public void animEnd() {
						skinView.startAnimation(AnimationUtils.getInstance().createEnterFromBottomAnim());
						skinView.setVisibility(View.VISIBLE);
					}
				});
				break;
			}
			case R.id.dot_paster:
			case R.id.paster: {
				// weekView.activatePaster();
				toggleMenu(false, null);
				Intent intent = new Intent(getContext(), ChooseStickerActivity.class);
				intent.putExtra(BaseActivity.INTENT_THEME, R.style.XTheme_Transparent_Popup);
				((MenuActivity) getContext()).startActivityForResult(intent, ChooseStickerActivity.PASTER_REQUESTCODE);
				break;
			}
			default:
				break;
			}
		}
	};

	List<CourseBlock> getDataForWeekView(List<Course> coruses, List<Event> events) {
		App app = (App) getContext().getApplicationContext();
		final int week = app.getCurrentWeek(false);
		List<CourseBlock> allBlocks = CourseHelper.getFullCourseBlocks(coruses, week);
		// this.addSimulationData();
		allBlocks = CourseHelper.merge(allBlocks, false);
		
		//增加活动的courseblock
		List<CourseBlock> eventBlocks = EventHelper.getCourseBlocksFromEventList(events, true);
		if(eventBlocks.size() > 0){
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
	
	List<UserSticker> getUserStickerList(){
		//----在此处同步贴纸----
		App app = (App) getContext().getApplicationContext();
		List<UserSticker> userStickers = app.getSavedStickerList();
		List<OfflineData<UserSticker>> offlineDatas = app.getDBHelper().getOfflineData(app.getUserDB(), UserSticker.class);
		for (OfflineData<UserSticker> offlineData : offlineDatas) {
			switch (offlineData.operation) {
			case ADD:
				userStickers.add((UserSticker) offlineData.getObject());
				break;
			case MODIFY:{
				UserSticker paster = offlineData.getObject();
				UserSticker oldPaster = (UserSticker) CommonUtils.findById(userStickers, paster.id);
				userStickers.remove(oldPaster);
				userStickers.add(paster);
				break;
			}
			case DELETE:{
				UserSticker paster = offlineData.getObject();
				UserSticker oldPaster = (UserSticker) CommonUtils.findById(userStickers, paster.id);
				userStickers.remove(oldPaster);
			}
			default:
				break;
			}
		}
		return userStickers;
	}

	int getStatusBarHeight() {
		Rect rectgle = new Rect();
		Window window = parent.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		return rectgle.top;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		// Ignore failed requests
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case PhotoPicker.PHOTO_PICKED_WITH_DATA: {
			// We tell the activity to put the result in MY_AVATAR_URI
			// if (BackgroundEditor.CUSTOM_BACKGROUND.exists()) {
			// updateCustomBg();
			// }else if (data != null && data.getData() != null) {
			// doCropPhoto(data.getData(), Const.PHOTO_CROPED_WITH_DATA);
			// }
			// break;
			String path;
			if (BackgroundEditor.CUSTOM_BACKGROUND.exists() && lastModified != BackgroundEditor.CUSTOM_BACKGROUND.lastModified()) {
				path = BackgroundEditor.CUSTOM_BACKGROUND.getAbsolutePath();
			} else {
				path = CameraUtil.resolvePhotoFromIntent(parent, data, BackgroundEditor.CUSTOM_BACKGROUND.getAbsolutePath());
			}
			if (path != null) {
				doCropPhoto(Uri.fromFile(new File(path)), Const.PHOTO_CROPED_WITH_DATA);
			} else {
				Hint.showTipsLong(parent, "图片选取失败");
			}
			break;
		}
		case Const.PHOTO_CROPED_WITH_DATA:
			boolean exist;
			if (BackgroundEditor.CUSTOM_BACKGROUND.exists() && lastModified != BackgroundEditor.CUSTOM_BACKGROUND.lastModified()) {
				exist = true;
			} else {
				String path = CameraUtil.resolvePhotoFromIntent(parent, data, BackgroundEditor.CUSTOM_BACKGROUND.getAbsolutePath());
				exist = path != null;
			}
			if (exist) {
				weekView.spiritStyle.setWeekStyleBean(skinView.getCurrentWeekStyleBean());
				MobclickAgent.onEvent(getContext(), "event_custom_bg_succeed");
			}
			break;

		case PhotoPicker.CAMERA_WITH_DATA:
			doCropPhoto(Uri.fromFile(bgEditor.mPhotoFile), Const.PHOTO_CROPED_WITH_DATA);
			break;
		case ChooseCoursesActivity.CHOOSE_COURSES:
			CourseBlock course = (CourseBlock) data.getSerializableExtra("COURSE");
			boolean isFinishedChooseCourseActivity = data.getBooleanExtra(TutorialManager.TUTORIAL_FINISHED, false);
			if (isFinishedChooseCourseActivity) {
				tutorialUtils.gotoNextStep();
			}else if(course != null) {
				onCourseClick(course);
			}
			break;
		case ChooseStickerActivity.PASTER_REQUESTCODE:
			AppLogger.d("PASTER_REQUESTCODE");
			MobclickAgent.onEvent(getContext(), "enter_put_course_sticker_view");
			weekView.post(new Runnable() {
				@Override
				public void run() {
					//Sticker --> UserSticker
					Sticker sticker = (Sticker) (data.getSerializableExtra(ChooseStickerActivity.INTENT_PASTER));
					int position = data.getIntExtra(ChooseStickerPagerAdapter.FRAGMENT_POSITION, 0);
					UserSticker userSticker = new UserSticker(sticker);
					weekView.spiritPaster.showChoosingPasterMode(userSticker);
					weekView.spiritPaster.willReturnToStickerChoose(true, position);
				}
			});
			break;
		default:
			Log.w(Const.TAG, "onActivityResult : invalid request code");

		}
	}

	protected void doCropPhoto(Uri uri, int responseId) {
		try {
			// Launch gallery to crop the photo
			final Intent intent = BackgroundEditor.getCropImageIntent(uri);
			fragment.startActivityForResult(intent, responseId);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(parent, "出错了，没找到照片裁剪程序", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onMultiCourseClick(List<CourseBlock> courseBlocks) {
		ArrayList<CourseBlock> courses = new ArrayList<CourseBlock>(courseBlocks);
		Intent intent = new Intent();
		intent.putExtra("COURES", courses);
		intent.setClass(getContext(), ChooseCoursesActivity.class);
		fragment.startActivityForResult(intent, ChooseCoursesActivity.CHOOSE_COURSES);
	}

	@Override
	public void onCourseClick(CourseBlock course) {
		if (!course.empty) {
			if (course != null) {
				if(course.event != null){
					Intent intent = new Intent(parent, EventActivity.class);
					intent.putExtra("EVENT", course.event);
					((Activity) getContext()).startActivityForResult(intent,
							Const.WEB_VIEWER_WITH_RESULT);
				} else {
					Intent intent = new Intent();
					intent.putExtra(Const.BUNDLE_KEY_COURSE, course);
					intent.setClass(getContext(), CourseActivity.class);
					((Activity) getContext()).startActivityForResult(intent,
							Const.WEB_VIEWER_WITH_RESULT);
				}
			}
		}
	}

	@Override
	public void onPasterClick(UserSticker paster) {
		if (!isShownDeleteStickerHint()) {
			hint2DeleteDialog();
			deleteStickerHintShown();
		}
	}
	
	private final String PREFERENCE_IS_SHOWN_DELETE_STICKER_HINT = "PREFERENCE_IS_SHOWN_DELETE_STICKER_HINT";
	
	private boolean isShownDeleteStickerHint(){
		SharedPreferences share = App.getInstance().getDefaultPreferences();
		return share.getBoolean(PREFERENCE_IS_SHOWN_DELETE_STICKER_HINT, false);
	}
	
	@SuppressLint("CommitPrefEdits")
	private void deleteStickerHintShown(){
		SharedPreferences share = App.getInstance().getDefaultPreferences();
		Editor editor = share.edit();
		editor.putBoolean(PREFERENCE_IS_SHOWN_DELETE_STICKER_HINT, true);
		App.commitEditor(editor);
	}
	
	private void hint2DeleteDialog() {
		AlertDialog dialog = new AlertDialog.Builder(parent).setTitle("格子提醒").setMessage("长按贴纸可以重新贴或者删除哦").setPositiveButton("知道了", null).create();
		dialog.show();
	}

	@Override
	public void setData(Object data) {
		// TODO 暂时用不上
		
	}
}

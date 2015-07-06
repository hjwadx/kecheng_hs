package fm.jihua.kecheng.ui.activity.home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.AddCoursesResult;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.CoursesResult;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UserDetailsResult;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.EventActivity;
import fm.jihua.kecheng.ui.activity.course.CourseActivity;
import fm.jihua.kecheng.ui.activity.profile.ProfileActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CustomScrollView;
import fm.jihua.kecheng.ui.widget.WeekView;
import fm.jihua.kecheng.ui.widget.WeekView.OnCourseClickListener;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.CourseHelper;
import fm.jihua.kecheng.utils.EventHelper;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.TencentAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

public class WeekActivity extends BaseActivity implements OnCourseClickListener {
	WeekView other_weekView;
	CustomScrollView scrollView;
	View addView;
	TextView tv_copy_times;
	User user;
	DataAdapter dataAdapter;
	
	private final int INVALID_USER_ID = -1;
	int user_id = INVALID_USER_ID;

	RenrenAuthHelper renrenAuthHelper;
	WeiboAuthHelper weiboAuthHelper;
	TencentAuthHelper tencentAuthHelper;

	int renrenShareState;
	int weiboShareState;
	int tencentShareState;

	final int NO_NEED_UPLOAD = 0;
	final int READY_FOR_UPLOAD = 1;
	final int UPLOAD_SUCCESS = 2;
	final int UPLOAD_FAILED = 3;
	
	// new
	LinearLayout weekview_corner_layout;
	
	CoursesResult coursesResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.others_week_view);
		
		dataAdapter = new DataAdapter(this, new MyDataCallback());
		
		user = (User)getIntent().getSerializableExtra("USER");
		coursesResult = (CoursesResult) getIntent().getSerializableExtra(Const.STR_COURSESRESULT);
		
		if(user == null){
			user_id = getIntent().getIntExtra("USER_ID", INVALID_USER_ID);
			if (user_id != INVALID_USER_ID) {
				UIUtil.block(this);
				dataAdapter.getUser(user_id);
			}
		}else{
			initTitlebar();
			init();
		}
	}
	
	private void initTitlebar(){
		getKechengActionBar().getRightButton().setVisibility(View.GONE);
		setTitle((TextUtils.isEmpty(user.name) ? "Ta" : user.name) + "的课表");
		getKechengActionBar().getLeftButton().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	void init(){
		findViewById(R.id.add).setVisibility(View.GONE);
		
		other_weekView =(WeekView) findViewById(R.id.others_weekview);
		scrollView = (CustomScrollView)findViewById(R.id.others_weekview_parent);
		other_weekView.initSpecialView(scrollView);
		addView = findViewById(R.id.add);
		tv_copy_times = (TextView) findViewById(R.id.add_copy_times);
		
		setListeners();
		
		if (coursesResult == null) {
			UIUtil.block(this);
			dataAdapter.getCourses(user.id);
		}else {
			showWeekView();
			if (getListCourses().size() != 0) {
				addView.setVisibility(View.VISIBLE);
				tv_copy_times.setText(String.valueOf(coursesResult.calendar == null ? 0 : coursesResult.calendar.copy));
			}
		}

		renrenAuthHelper = new RenrenAuthHelper(this);
		tencentAuthHelper = new TencentAuthHelper(this);
		weiboAuthHelper = new WeiboAuthHelper(this);
	}

	void setListeners(){
		findViewById(R.id.add).setOnClickListener(listener);
		other_weekView.setOnChildClickListener(this);
		other_weekView.setOnClickListener(listener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (scrollView != null) {
			Drawable drawable = scrollView.getBackground();
			if (drawable instanceof BitmapDrawable) {
				scrollView.setBackgroundResource(0);
				if (drawable != null && ((BitmapDrawable) drawable).getBitmap() != null)
					((BitmapDrawable) drawable).getBitmap().recycle();
			}
		}
	}

	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add:{
				Intent intent=new Intent(WeekActivity.this, CourseListActivity.class);
				intent.putExtra(CourseListActivity.INTENT_KEY_COURSE_LIST, (Serializable)getListCourses());
				startActivityForResult(intent, CourseListActivity.REQUESTCODE_2_COURSE_LIST);
				// DialogUtils.showShareWeekDialog(WeekActivity.this, weekView);
				break;
			}
			case R.id.others_weekview:
				if (addView.getVisibility() == View.VISIBLE) {
					addView.setVisibility(View.GONE);
				}else {
					if (coursesResult != null && coursesResult.courses != null && coursesResult.courses.length != 0) {
						addView.setVisibility(View.VISIBLE);
					}
				}
				break;
			}
		}
	};

	private void showWeekView(){
		Log.d(this.getClass().getSimpleName(), "showWeekView");
		List<CourseBlock> allBlocks = CourseHelper.changeToCourseBlocks(getListCourses());
		List<UserSticker> asList = Arrays.asList(coursesResult.stickers);
		List<CourseBlock> eventBlocks = EventHelper.changeToCourseBlocks(Arrays.asList(coursesResult.events));
		if(eventBlocks.size() > 0){
			allBlocks.addAll(eventBlocks);
		}
		other_weekView.setData(allBlocks, asList, coursesResult.theme_name, coursesResult.theme_product_id, false);
	}
	
	private List<Course> getListCourses(){
		if(coursesResult != null && coursesResult.courses!=null){
			return Arrays.asList(coursesResult.courses);
		}else{
			return new ArrayList<Course>();
		}
	}

	private class MyDataCallback implements DataCallback {
		@Override
		public void callback(Message msg) {
			UIUtil.unblock(WeekActivity.this);
			Log.i(Const.TAG, "DataCallback Message:" + msg.what);
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_COURSES:{
				
				coursesResult = (CoursesResult) msg.obj;
				if (coursesResult != null) {
					if (coursesResult.courses == null) {
						coursesResult.courses = new Course[0];
					}
					if (coursesResult.events == null) {
						coursesResult.events = new Event[0];
					}
					if (coursesResult.stickers == null) {
					    coursesResult.stickers = new UserSticker[0];
					}
					showWeekView();
					if (!coursesResult.finished && coursesResult.courses.length == 0) {
						UIUtil.block(WeekActivity.this);
					}
					if(coursesResult.courses.length != 0){
						addView.setVisibility(View.VISIBLE);
						tv_copy_times.setText(String.valueOf(coursesResult.calendar.copy));
					}
				}
			}
				break;
			case DataAdapter.MESSAGE_ADD_FRIEND_COURSE_BY_ID:{
				AddCoursesResult result = (AddCoursesResult)msg.obj;
				if (result != null && result.success == true) {
					int numAdded = result.num_courses_added;
					String message = numAdded > 0 ? "有"+numAdded+"门课程添加成功" : "没有课程添加成功";
					if (numAdded > 0) {
						share(numAdded);
						tv_copy_times.setText(String.valueOf(result.calendar.copy));
					}
					Hint.showTipsShort(WeekActivity.this, message);
				}else {
					UIUtil.unblock(WeekActivity.this);
					Hint.showTipsShort(WeekActivity.this, "添加好友课表失败");
				}
			}
				break;
				
			case DataAdapter.MESSAGE_GET_USER:
				UserDetailsResult result = (UserDetailsResult) msg.obj;
				if (result == null || result.finished) {
					UIUtil.unblock(WeekActivity.this);
				}
				if (result != null) {
					user = result.user;
					if (user != null) {
						initTitlebar();
						init();
					}
				}else {
					Hint.showTipsLong(WeekActivity.this, "数据有误，请连网更新");
					finish();
				}
				break;
			default:
				break;
			}
		}
    }

	void share(int count) {
		String content = String.format(getResources().getString(R.string.share_after_copy_courses_succeed), user.name);
//		String file = getFileName();
		if (renrenAuthHelper.isAuthed()) {
			renrenShareState = READY_FOR_UPLOAD;
			String renrenContent = content + " http://t.cn/zRJEpPW";
			renrenAuthHelper.update(renrenContent, new MySNSCallback(SNSCallback.UPLOAD));
		}
		if (weiboAuthHelper.isAuthed()) {
			weiboShareState = READY_FOR_UPLOAD;
			String weiboContent = content + " http://t.cn/zRJEpPW";
			weiboAuthHelper.update(weiboContent, new MySNSCallback(SNSCallback.UPLOAD));
		}
		if (tencentAuthHelper.isAuthed()) {
			MobclickAgent.onEvent(WeekActivity.this, "action_share__send", "tencent selected");
			tencentShareState = READY_FOR_UPLOAD;
			String tencentContent = content + " http://t.cn/zRJEpPW";
			tencentAuthHelper.update("课程格子", tencentContent, "http://kechenggezi.com/", "http://img.kechenggezi.com/app/splash_screen.png", "", "", new MySNSCallback(SNSCallback.UPLOAD));
		}
	}

//	private String getFileName() {
//		File cacheDir = ImageCache.getDiskCacheDir(this, "share/add_course_share.jpg");
//		if (cacheDir.exists()) {
//			return cacheDir.getAbsolutePath();
//		}
//		if (!cacheDir.getParentFile().exists()) {
//			if (!cacheDir.getParentFile().mkdirs()) {
//				Log.e("WeekActivity","创建目标文件所在目录失败！");
//			}
//		}
//		File shareImageFile = new File(cacheDir.getAbsolutePath());
//		FileOutputStream fOut = null;
//		try {
//			fOut = new FileOutputStream(shareImageFile);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		ImageHlp.getImageFromAssetsFile(getResources(), "add_course_share.jpg").compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//		try {
//			fOut.flush();
//			fOut.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return shareImageFile.getAbsolutePath();
//	}

	void checkForFinish() {
		if (renrenShareState != READY_FOR_UPLOAD
				&& weiboShareState != READY_FOR_UPLOAD && tencentShareState != READY_FOR_UPLOAD) {
			UIUtil.unblock(WeekActivity.this);
		}
	}

    private class MySNSCallback implements SNSCallback{

		private int mScope = 0;

		public MySNSCallback(int scope) {
            mScope = scope;
        }

		@Override
		public void onComplete(final AuthHelper authHelper, Object data) {
			switch (mScope){
			case SNSCallback.UPLOAD:
				int helperCategory = authHelper.getType();
				((App) getApplication()).setShared(true);
				if (helperCategory == Const.RENREN) {
					MobclickAgent.onEvent(WeekActivity.this, "event_share_renren_succeed", "copy");
					renrenShareState = UPLOAD_SUCCESS;
				} else if (helperCategory == Const.WEIBO) {
					MobclickAgent.onEvent(WeekActivity.this, "event_share_weibo_succeed", "copy");
					weiboShareState = UPLOAD_SUCCESS;
				} else if (helperCategory == Const.QQ) {
					MobclickAgent.onEvent(WeekActivity.this, "event_share_qq_succeed", "copy");
					tencentShareState = UPLOAD_SUCCESS;
				}
				checkForFinish();
				break;
			}

		}

		@Override
		public void onError(final AuthHelper authHelper) {
			int helperCategory = authHelper.getType();
			if (helperCategory == Const.RENREN) {
				renrenShareState = UPLOAD_FAILED;
			} else if (helperCategory == Const.WEIBO) {
				weiboShareState = UPLOAD_FAILED;
			} else if (helperCategory == Const.QQ) {
				tencentShareState = UPLOAD_FAILED;
			}
			checkForFinish();
		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case CourseListActivity.REQUESTCODE_2_COURSE_LIST:
			UIUtil.block(WeekActivity.this);
			dataAdapter.addFriendCoursesById(user.id, data.getStringExtra(CourseListActivity.INTENT_KEY_COURSE_LIST));
			break;
		case ChooseCoursesActivity.CHOOSE_COURSES:
			CourseBlock course = (CourseBlock) data.getSerializableExtra("COURSE");
			onCourseClick(course);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onMultiCourseClick(List<CourseBlock> courseBlocks) {
		ArrayList<CourseBlock> courses = new ArrayList<CourseBlock>(courseBlocks);
		Intent intent = new Intent();
		intent.putExtra("COURES", courses);
		intent.setClass(this, ChooseCoursesActivity.class);
		startActivityForResult(intent, ChooseCoursesActivity.CHOOSE_COURSES);
	}

	@Override
	public void onCourseClick(CourseBlock course) {
		if (!course.empty) {
			if (course != null) {
				if(course.event != null){
					Intent intent = new Intent(this, EventActivity.class);
					intent.putExtra("EVENT", course.event);
					((Activity) this).startActivityForResult(intent,
							Const.WEB_VIEWER_WITH_RESULT);
				} else {
					Intent intent = new Intent();
					intent.putExtra(Const.BUNDLE_KEY_COURSE, course);
					intent.setClass(this, CourseActivity.class);
					((Activity) this).startActivityForResult(intent,
							Const.WEB_VIEWER_WITH_RESULT);
				}
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		if (user_id != INVALID_USER_ID) {
			Intent intent = new Intent(WeekActivity.this, ProfileActivity.class);
			intent.putExtra("USER", user);
			intent.putExtra("FROM", "WEEKACTIVITY");
			startActivity(intent);
			finish();
		} else
			super.onBackPressed();
	}

	@Override
	public void onPasterClick(UserSticker paster) {
		
	}
}

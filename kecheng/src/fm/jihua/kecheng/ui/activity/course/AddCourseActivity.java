package fm.jihua.kecheng.ui.activity.course;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.KechengAppWidgetProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseResult;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import fm.jihua.kecheng.rest.entities.OfflineData;
import fm.jihua.kecheng.rest.entities.Semester;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.adapter.LoadingAdapter;
import fm.jihua.kecheng.ui.adapter.OfflineCourseTipsWrapperAdapter;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.ShadowTextView;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng_hs.R;

public class AddCourseActivity extends BaseActivity {
	boolean firstLoad = true;
	boolean timeFilter = false;
	EditText searchBox;
	ListView searchListView;
	View footerView;
	TextView filterTextView;
	View iv_deleteFilter;
//	Button finishButton;
	int day_of_week;
	int time_slot;
	LoadingAdapter loaddingAdapter;
	boolean isFooterViewAdded;			//on added footerview after user change the text
	DataAdapter mDataAdapter;
	String school;

	List<Course> mCourseList;
	List<String> mShowCourseList = new ArrayList<String>();
	List<OfflineData<Course>> mOfflineDataList;
	OfflineData<Course> mOfflineData;
	OfflineCourseTipsWrapperAdapter offlineWrapperAdapter;
	App app;

	String currentSearchTerm;
	final String TAG = "AddCourseActivity";

	boolean saved;
	AddCourseAdapter adapter;
	final int intent_import_course = 1002;

	String[] courses = new String[]{"语文", "数学", "英语", "思想政治", "地理", "历史", "物理", "化学", "生物", "体育", "音乐", "美术", "信息技术", "心理健康", "班会", "活动"};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onEvent(this, "enter_add_course_view");
		setContentView(R.layout.add_course);
		app = (App) getApplication();
		mCourseList = app.getDBHelper().getCourses(app.getUserDB());
		mOfflineDataList = app.getDBHelper().getOfflineData(app.getUserDB(), Course.class);
		mDataAdapter = new DataAdapter(this, dataCallback);
		findViews();
		addListener();
		initList();
		initTitlebar();
		school = app.getMyself().school;
		((View)searchBox).setVisibility(View.GONE);
//		searchBox.setText("");
	}

	private void findViews(){
		searchBox = (EditText) findViewById(R.id.add_course_search);
		searchListView = (ListView) findViewById(R.id.add_course_courses);
		footerView = getLayoutInflater().inflate(R.layout.search_footer_view, searchListView, false);
		filterTextView = (TextView) findViewById(R.id.filter);
		iv_deleteFilter = findViewById(R.id.delete_filter);
		searchBox.clearFocus();
	}

	private void addListener(){
//		searchBox.addTextChangedListener(new MyTextWatcher());
//		searchBox.setOnClickListener(new MyOnClickListener());
		searchListView.setOnItemClickListener(new MyOnItemClickListener());
		footerView.findViewById(R.id.new_course_btn).setOnClickListener(new MyOnClickListener());
		iv_deleteFilter.setOnClickListener(new MyOnClickListener());
	}

	void initTitlebar(){
		setTitle(R.string.act_add_course_title);
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		getKechengActionBar().setRightText("复制同学");
		getKechengActionBar().getRightTextButton().setOnClickListener(new MyOnClickListener());
	}

	private void initList(){
//		finishButton.setText("完成");
		searchListView.addFooterView(footerView);
//		searchListView.removeFooterView(footerView);
		day_of_week = getIntent().getIntExtra("day_of_week", -1);
		time_slot = getIntent().getIntExtra("time_slot", -1);
//		if (day_of_week != -1) {
//			timeFilter = true;
//			String[] weekdays = getResources().getStringArray(R.array.week_days);
//			filterTextView.setText(weekdays[day_of_week-1] + "第" + time_slot + "节");
//			((View)filterTextView.getParent()).setVisibility(View.VISIBLE);
//			search("", day_of_week, time_slot);
//		}else {
//			search("");
//		}
		mShowCourseList.addAll(Arrays.asList(courses));
		refreshUI(mShowCourseList);
	}

	@Override
	public void finish() {
		if (saved) {
			setResult(RESULT_OK);
		}
		super.finish();
	}

	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
//			case R.id.delete_filter:
//				timeFilter = false;
//				((View)filterTextView.getParent()).setVisibility(View.GONE);
//				search(searchBox.getText().toString().trim());
//				break;
			case R.id.new_course_btn:{
				Intent intent = new Intent();
				intent.setClass(AddCourseActivity.this, NewCourseActivity.class);
				Course course = new Course();
				course.name = searchBox.getText().toString().trim();
				if (timeFilter) {
					course.course_units = new ArrayList<CourseUnit>();
					CourseUnit courseUnit = new CourseUnit(day_of_week, "" + time_slot, null, null);
					course.course_units.add(courseUnit);
				}
				intent.putExtra("COURSE", course);
				startActivityForResult(intent,
						Const.WEB_VIEWER_WITH_RESULT);
			}
				break;
			case R.id.add_course_search:
				break;
			case R.id.remove:{
				Object object = v.getTag();
				if (object != null && object instanceof OfflineData) {
					MobclickAgent.onEvent(AddCourseActivity.this, "action_cancel_offline_course");
					OfflineData offlineData = (OfflineData) object;
					app.getDBHelper().deleteOfflineData(app.getUserDB(), offlineData.id);
					mOfflineDataList.remove(offlineData);
					offlineWrapperAdapter.setOfflineDataList(mOfflineDataList);
					offlineWrapperAdapter.notifyDataSetChanged();
				}
				break;
			}
			case R.id.upload:{
				Object object = v.getTag();
				if (object != null && object instanceof OfflineData) {
					MobclickAgent.onEvent(AddCourseActivity.this, "action_update_offline_course");
					OfflineData<Course> offlineData = (OfflineData<Course>) object;
					Course course = offlineData.getObject();
					mOfflineData = offlineData;
					List<CourseUnit> courseUnits = course.course_units;
					UIUtil.block(AddCourseActivity.this);
					mDataAdapter.createCourse(course.getSuccinctCourse(), courseUnits);
				}
				break;
			}
			case R.id.action_textview:
				Intent intent = new Intent(AddCourseActivity.this, ImportCoursesActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
				break;
			}
		}
	}

	class MyOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			Object object = listView.getAdapter().getItem(position);
			String courseName = object.toString();
			Course course = getCourseByName(courseName);
			OfflineData<Course> offline = getOfflineCourseByName(courseName);
			if(offline != null){
				Intent intent = new Intent();
				intent.setClass(AddCourseActivity.this, NewCourseActivity.class);
				intent.putExtra("OFFLINE", offline);
				startActivityForResult(intent, Const.WEB_VIEWER_WITH_RESULT);
			} else if(course == null){
				Intent intent = new Intent();
				intent.setClass(AddCourseActivity.this, NewCourseActivity.class);
				Course new_course = new Course();
				new_course.name = courseName;
				intent.putExtra("COURSE", new_course);
				startActivityForResult(intent, Const.WEB_VIEWER_WITH_RESULT);
			} else {
				MobclickAgent.onEvent(AddCourseActivity.this, "action_edit_course");
				Intent intent = new Intent(AddCourseActivity.this, NewCourseActivity.class);
				intent.putExtra("COURSE", course);
				intent.putExtra("IS_EDIT", true);
				startActivityForResult(intent, CourseActivity.REQUESTCODE_EDIT_COURSE);
				overridePendingTransition(R.anim.slide_bottom_in, 0);
			}
		}
	}
	
	public OfflineData<Course> getOfflineCourseByName(String name) {
		if(name == null){
			return null;
		}
		OfflineData<Course> offline = null;
		for(OfflineData<Course> offlineData : mOfflineDataList){
			if(name.equals(offlineData.getObject().name)){
				offline = offlineData;
				break;
			}
		}
		return offline;
	}
	
	public Course getCourseByName(String name) {
		return App.getInstance().getDBHelper().getCourseByName(App.getInstance().getUserDB(), name);
	}

	private void refreshUI(List<String> courses) {
		UIUtil.unblock(AddCourseActivity.this);
		for(Course course : mCourseList){
			if(!mShowCourseList.contains(course.name)){
				mShowCourseList.add(course.name);
			}
		}
		adapter = new AddCourseAdapter(courses);
		offlineWrapperAdapter = new OfflineCourseTipsWrapperAdapter(adapter);
		if ("".equals(currentSearchTerm)) {
			Semester semester = app.getDBHelper().getActiveSemester(app.getUserDB());
			offlineWrapperAdapter.showTips("以下是同院系同学在上的课("+semester.name+")");
		}
		adapter.setOnActionListener(new MyOnClickListener());
		offlineWrapperAdapter.setOnActionListener(new MyOnClickListener());
		if (mOfflineDataList != null && mOfflineDataList.size() > 0){
			offlineWrapperAdapter.setOfflineDataList(mOfflineDataList);
		}
		searchListView.setAdapter(offlineWrapperAdapter);
	}

	private void showCourseResult(String hint, Course course){
		try {
			Toast toast = Toast.makeText(AddCourseActivity.this, hint, Toast.LENGTH_SHORT);
			toast.show();
			UIUtil.unblock(AddCourseActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addCourse(final Course course) {
		if (mCourseList.contains(course)) {
			String hint = "课程《" + course.name + "》已在你的课表中。";
			showCourseResult(hint, course);
			return;
		}
		UIUtil.block(AddCourseActivity.this);
		DataAdapter dataAdapter = new DataAdapter(this, new DataCallback() {

			@Override
			public void callback(Message msg) {
				UIUtil.unblock(AddCourseActivity.this);
				CourseResult courseResult = (CourseResult)msg.obj;
				if(courseResult != null){
					if(courseResult.success){
//						app.getDBHelper().saveCourse(app.getUserDB(), courseResult.course);
						sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
						mCourseList.add(courseResult.course);
						HeaderViewListAdapter headerListAdapter = (HeaderViewListAdapter) searchListView.getAdapter();
						if (headerListAdapter.getWrappedAdapter() instanceof OfflineCourseTipsWrapperAdapter) {
							((OfflineCourseTipsWrapperAdapter)headerListAdapter.getWrappedAdapter()).notifyDataSetChanged();
						}
//						((SearchAdapter)headerListAdapter.getWrappedAdapter()).setExistingCourses(mCourseList);
						showCourseResult("课程《"+courseResult.course.name+"》添加成功", courseResult.course);
//						CommonUtils.playSound(AddCourseActivity.this, R.raw.add_course);
						saved = true;
					}else {
						Course course = courseResult.conflict_course;
						if (course == null && courseResult.add_course_result.equals("duplicate")) {
							course = courseResult.course;
						}
						showCourseResult(courseResult.notice, course);
					}
				}else {
					Hint.showTipsLong(AddCourseActivity.this, "添加课程失败了，是不是网络链接不正常？");
				}
			}
		});
		dataAdapter.addCourse(course.id);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK){
			return;
		}
		try {
			switch (requestCode) {
			case Const.WEB_VIEWER_WITH_RESULT:
//				data.getBooleanExtra("result", false)
				saved = data.getBooleanExtra("SAVED", false);
				if (saved) {
					mCourseList = app.getDBHelper().getCourses(app.getUserDB());
					for(Course course : mCourseList){
						if(!mShowCourseList.contains(course.name)){
							mShowCourseList.add(course.name);
						}
					}
					if (adapter != null) {
//						adapter.setExistingCourses(mCourseList);
						adapter.notifyDataSetChanged();
					}
				}
				if (data.getBooleanExtra("OFFLINE", false)) {
					searchBox.setText("");
					mOfflineDataList = app.getDBHelper().getOfflineData(app.getUserDB(), Course.class);
					offlineWrapperAdapter.setOfflineDataList(mOfflineDataList);
					offlineWrapperAdapter.notifyDataSetChanged();
					searchListView.setSelection(0);
				}
				if (data.getBooleanExtra("TO_MAIN", false)) {
					saved = true;
					finish();
				}
				break;
			case CourseActivity.REQUESTCODE_EDIT_COURSE:
				if (data.getSerializableExtra("COURSE") != null) {
					mCourseList = app.getDBHelper().getCourses(app.getUserDB());
					mShowCourseList.clear();
					mShowCourseList.addAll(Arrays.asList(courses));
					for(Course course : mCourseList){
						if(!mShowCourseList.contains(course.name)){
							mShowCourseList.add(course.name);
						}
					}
					if (adapter != null) {
//						adapter.setExistingCourses(mCourseList);
						adapter.notifyDataSetChanged();
					}
				}
				break;
			case intent_import_course:
				setResult(RESULT_OK);
				finish();
				break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Main onActivityResult Exception:" + e.getMessage());
		}
	}

	DataCallback dataCallback = new DataCallback() {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(AddCourseActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_DELETE_COURSE:{
				BaseResult result = (BaseResult)msg.obj;
				if(result != null && result.success == true){
					mCourseList = app.getDBHelper().getCourses(app.getUserDB());
					if (adapter != null) {
//						adapter.setExistingCourses(mCourseList);
						adapter.notifyDataSetChanged();
						sendBroadcast(new Intent(KechengAppWidgetProvider.UPDATE));
						Hint.showTipsShort(getBaseContext(), "课程删除成功");
					}
					saved = true;
				}else {
					Hint.showTipsShort(getBaseContext(), "课程删除失败");
				}
				break;
			}
			case DataAdapter.MESSAGE_CREATE_COURSE:{
				CourseResult result = (CourseResult)msg.obj;
				createCourseCallback(result);
				break;
			}
			default:
				break;
			}
		}
	};

	void createCourseCallback(CourseResult result){
		UIUtil.unblock(this);
		if (result != null) {
			if (result.success) {
				saved = true;
				mCourseList.add(result.course);
				if (adapter != null) {
//					adapter.setExistingCourses(mCourseList);
					adapter.notifyDataSetChanged();
				}
				app.getDBHelper().deleteOfflineData(app.getUserDB(), mOfflineData.id);
				mOfflineDataList.remove(mOfflineData);
				offlineWrapperAdapter.setOfflineDataList(mOfflineDataList);
				offlineWrapperAdapter.notifyDataSetChanged();
				sendBroadcast(new Intent(
						KechengAppWidgetProvider.UPDATE));
			} else {
				Toast toast = Toast.makeText(AddCourseActivity.this, result.notice, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, -100);
				toast.show();
			}
		} else {
			Hint.showTipsLong(AddCourseActivity.this, "创建课程失败，请确保网络连接正常");
		}
	}
	


//	private Course isConflict(Course newCourse){
//		for (Course course : mCourseList) {
//			if (newCourse.isConflict(course)) {
//				return course;
//			}
//		}
//		return null;
//	}

//	private void showCurriculum(){
//		int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
//		int height = (int) (width/1.5);
//    	if(curriculumView == null){
//    		curriculumView = getLayoutInflater().inflate(R.layout.curriculum, null);
//    	}
//    	if (curriculumPopup == null) {
//    		curriculumPopup = new PopupWindow(curriculumView, width, height);
//    		curriculumView.findViewById(R.id.curriculum).setOnClickListener(new MyOnClickListener());
//		}
//    	Curriculum curriculum = (Curriculum) curriculumView.findViewById(R.id.curriculum);
//    	curriculum.setBasicColor(Color.GRAY);
//		curriculum.setData(mCourseList, width, height);
//		curriculumPopup.setAnimationStyle(android.R.style.Animation_Dialog);
//    	curriculumPopup.showAtLocation(mainView, Gravity.BOTTOM, 0, 0);
//	}


//	class MyTextWatcher implements TextWatcher {
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//		}
//
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count,
//				int after) {
//
//		}
//
//		@Override
//		public void afterTextChanged(Editable s) {
//			MobclickAgent.onEvent(AddCourseActivity.this, "action_add_course_search");
//			isFooterViewAdded = true;
//			if (s.toString().length() == 0) {
//				newCourseButton.setText("手动创建课程");
//			}else {
//				newCourseButton.setText("手动创建："+s.toString());
//			}
//			if (searchListView.getFooterViewsCount() == 1) {
//				Log.i(TAG, "remove footer view");
////				try {
////					searchListView.removeFooterView(footerView);
////		        } catch (ClassCastException e) {
////		        	e.printStackTrace();
////		        }
//			}
//
////			if (timeFilter) {
////				search(s.toString(), day_of_week, time_slot);
////			}else {
////				search(s.toString());
////			}
//		}
//	}

//	private void search(final String term) {
//		search(term, Const.INVALID_INT_VALUE, Const.INVALID_INT_VALUE);
//	}
//
//	private void search(final String term, final int day_of_week, final int time_slot) {
//		currentSearchTerm = term;
//		String text = term.length() == 0 ? "正在搜索同院系同学在上的课" : "正在搜索课程："+term;
//		loaddingAdapter = new LoadingAdapter(text);
//		if (mOfflineDataList != null && mOfflineDataList.size() > 0 && term.length() == 0) {
//			offlineWrapperAdapter = new OfflineCourseTipsWrapperAdapter(loaddingAdapter, mOfflineDataList);
//			offlineWrapperAdapter.setOnActionListener(new MyOnClickListener());
//			searchListView.setAdapter(offlineWrapperAdapter);
//		} else {
//			searchListView.setAdapter(loaddingAdapter);
//		}
//		DataAdapter dataAdapter = new DataAdapter(this, new DataCallback() {
//
//			@Override
//			public void callback(Message msg) {
//				if (term.equals(currentSearchTerm)) {
//					SearchResult result = (SearchResult) msg.obj;
//					if (result != null) {
//						if (result.courses == null || result.courses.length == 0) {
//							if (searchBox.getText() == null || "".equals(searchBox.getText().toString())) {
//								loaddingAdapter.setEmptyMessage("没有热门课？快来抢先创建课程！");
//							} else {
//								loaddingAdapter.setEmptyMessage("没有找到这门课哦，试试手动创建");
//							}
//							loaddingAdapter.showEmptyText();
//						}else{
//							refreshUI(Arrays.asList(result.courses));
//						}
//					}else {
//						loaddingAdapter.setEmptyMessage("搜索课程失败，试试手动创建课程吧！");
//						loaddingAdapter.showEmptyText();
//					}
//				}
//			}
//		});
//		dataAdapter.request(RestService.get().search(term, day_of_week, time_slot));
//	}
}

package fm.jihua.kecheng.ui.activity.course;


import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.chat.utils.BeemConnectivity;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.KechengAppWidgetProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseResult;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import fm.jihua.kecheng.rest.entities.OfflineData;
import fm.jihua.kecheng.rest.entities.OfflineData.Operation;
import fm.jihua.kecheng.rest.entities.UpdateCourseResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.view.CourseTimeWheelView;
import fm.jihua.kecheng.ui.view.EditCourseTimeTable;
import fm.jihua.kecheng.ui.widget.SimpleScrollView;
import fm.jihua.kecheng.ui.widget.SimpleScrollView.ScrollChangedListener;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng_hs.R;

public class NewCourseActivity extends BaseActivity {
	
	ViewGroup courseForm;
	ViewGroup courseTimeParent;
	SimpleScrollView formViewContainer;
	View mainView;
	EditText courseNameEditText;
	EditText teacherEditText;
//	EditText startWeekEditText;
//	EditText endWeekEditText;
	View currentTimeView;
	List<Course> mCourseList;
	
	CourseTimeWheelView wheelView;
	EditCourseTimeTable courseTimeController;
	
	int inputMethodHeight;
	int smallContentHeight;
	int fullContentHeight;
	
	final String TAG = "NewCourseActivity";
	boolean saved;
	boolean toMain;
	boolean offline;
	
	Course courseForEdit;
	OfflineData<Course> offlineCourse;
	Course c;
	boolean isEdit;
	DataAdapter mDataAdapter;

	boolean ignoreConflict;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_course);
		findViews();
		addListeners();
		initViews();
		App app = (App) getApplication();
		mCourseList = app.getDBHelper().getCourses(app.getUserDB());
		initTitlebar();
		mDataAdapter = new DataAdapter(this, myDataCallback);
	}
	
	private void findViews(){
		mainView = findViewById(R.id.new_course_main);
		courseForm = (LinearLayout) findViewById(R.id.add_course_form);
		courseNameEditText = (EditText) findViewById(R.id.course_name);
		teacherEditText = (EditText) findViewById(R.id.teacher);
		formViewContainer = (SimpleScrollView) findViewById(R.id.add_course_form_container);
		wheelView = (CourseTimeWheelView)findViewById(R.id.wheel_view);
		courseTimeController = (EditCourseTimeTable)findViewById(R.id.course_time_parent);
	}
	
	private void initViews(){
		courseForEdit = (Course) getIntent().getSerializableExtra("COURSE");
		isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
		offlineCourse = (OfflineData<Course>) getIntent().getSerializableExtra("OFFLINE");
		if(offlineCourse != null){
			courseForEdit = offlineCourse.getObject();
		}
		
		if (courseForEdit != null) {
			courseNameEditText.setText(courseForEdit.name);
			if (isEdit) {
				teacherEditText.setText(courseForEdit.teacher);
			}
			if (courseForEdit.course_units != null && courseForEdit.course_units.size() > 0) {
				courseTimeController.setData(courseForEdit);
			}
		} 
		if(!isEdit && offlineCourse == null){
			courseTimeController.setData(null);
		}
	}
	
	void initTitlebar(){
		setTitle(isEdit ? "修改课程" : "创建课程");
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
	}
	
	private void addListeners(){
		MyOnClickListener clickListener = new MyOnClickListener();
		MyOnFocusChangeListener focusChangeListener = new MyOnFocusChangeListener();
		courseNameEditText.setOnClickListener(clickListener);
		teacherEditText.setOnClickListener(clickListener);
//		courseNameEditText.setOnFocusChangeListener(focusChangeListener);
//		teacherEditText.setOnFocusChangeListener(focusChangeListener);
		getKechengActionBar().getRightButton().setOnClickListener(clickListener);
		formViewContainer.setOnScrollChangedListener(new ScrollChangedListener() {
			@Override
			public void scrollChanged() {
//				wheelView.setVisibility(View.GONE);
			}
		});
	}
	
	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra("SAVED", saved);
		intent.putExtra("TO_MAIN", toMain);
		intent.putExtra("OFFLINE", offline);
		setResult(RESULT_OK, intent);
		super.finish();
	}
	
	private void dismissDialog(){
		wheelView.setVisibility(View.GONE);
	}
	
	boolean hasEdited(){
		String name = courseNameEditText.getText().toString().trim();
		String teacher = teacherEditText.getText().toString().trim();
		if (courseForEdit.name != null) {
			courseForEdit.name = courseForEdit.name.trim();
		}
		if ((!CommonUtils.notStrictEquals(teacher, courseForEdit.teacher.trim())) ||
				!ObjectUtils.nullSafeEquals(name, courseForEdit.name) || 
				!ObjectUtils.nullSafeEquals(courseForEdit.course_units, courseTimeController.getData())) {
			return true;
		}
		return false;
	}
	
	class MyOnFocusChangeListener implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v instanceof EditText && hasFocus) {
				Log.i(TAG, "after EditText focus");
				dismissDialog();
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (wheelView.getVisibility() == View.VISIBLE) {
				dismissDialog();
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.course_name:
			case R.id.teacher:
				dismissDialog();
				break;
			case R.id.action:
				updateCourse();
				break;
			default:
				break;
			}
		}
	}
	
	void updateCourse(){
		List<CourseUnit> courseUnits = courseTimeController.getData();
		if (courseNameEditText.getText().toString().trim().length() == 0) {
			Hint.showTipsShort(NewCourseActivity.this, "课程名称不能为空");
			return;
		}
		if (courseUnits.size() == 0) {
			Hint.showTipsShort(NewCourseActivity.this, "上课时间不能为空");
			return;
		}
//		for(CourseUnit unit: courseUnits){
//			if(CommonUtils.isNullString(unit.weeks)){
//				Hint.showTipsShort(NewCourseActivity.this, "上课周数不能为空");
//				return;
//			}
//		}
		if(courseTimeController.isHasTimeNull()){
			Hint.showTipsShort(NewCourseActivity.this, "上课时间不能为空");
			return;
		}
		if(!isEdit){
			for(Course course : mCourseList){
				if(course.name.equals(courseNameEditText.getText().toString().trim())){
					Hint.showTipsShort(NewCourseActivity.this, "已存在同名课程");
					return;
				}
			}
		}
		if(!ignoreConflict){
			for(CourseUnit unit_new: courseUnits){
				for(Course course : mCourseList){
					if(isEdit && courseForEdit.id == course.id){
						continue;
					}
					for(CourseUnit unit_old: course.course_units){
						if(unit_new.day_of_week == unit_old.day_of_week){
							if(CourseUnit.isStringConflict(unit_new.time_slots, unit_old.time_slots)){
								showConflictConfirmDialog(course, unit_old);
								return;
							}
						}
					}
				}
			}
		}
		Point pt = CourseUnit.getConflictUnitsPoint(courseUnits);
		if (pt != null) {
			Hint.showTipsShort(NewCourseActivity.this, "上课时间" + pt.x + "和上课时间" + pt.y + "重叠，是不是输错了？");
		}else {
			courseUnits = courseTimeController.getDataMerged();
			c = isEdit ? new Course(courseForEdit.id, courseNameEditText.getText().toString().trim(), teacherEditText.getText().toString().trim()) : new Course(0, courseNameEditText.getText().toString().trim(), teacherEditText.getText().toString().trim());
			if (isEdit) {
				if (hasEdited()) {
					c.name = courseNameEditText.getText().toString().trim();
					c.teacher = teacherEditText.getText().toString().trim();
					c.course_units = null;
					UIUtil.block(NewCourseActivity.this);
					mDataAdapter.updateCourse(c, courseUnits);
				} else {
					finish();
					// Hint.showTipsShort(NewCourseActivity.this,
					// "你还没有修改任何东西，请修改后再提交");
				}
			} else {
				MobclickAgent.onEvent(NewCourseActivity.this, "action_create_course");
				c.course_units = null;
				UIUtil.block(NewCourseActivity.this);
				mDataAdapter.createCourse(c, courseUnits);
			}
		}
	}
	
	private void showConflictConfirmDialog(Course course, CourseUnit unit){
		new AlertDialog.Builder(NewCourseActivity.this)
		.setTitle("和" + Const.SERVER_WEEKS[unit.day_of_week] + "的" + course.name + "课时间有冲突！")
		.setMessage("是否继续创建该课程")
		.setPositiveButton("进行修改",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
					}
				})
		.setNegativeButton("继续创建",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						ignoreConflict = true;
						updateCourse();
					}
				}).show();
	}
	
	private void showConfirmDialog(){
		new AlertDialog.Builder(NewCourseActivity.this)
		.setTitle("添加成功")
		.setMessage("是否继续添加课程")
		.setCancelable(false)
		.setPositiveButton("继续添加",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						finish();
					}
				})
		.setNegativeButton("返回主页面",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						toMain = true;
						finish();
					}
				}).show();
	}
	
	private void showOfflineDialog(){
		new AlertDialog.Builder(NewCourseActivity.this)
		.setTitle("创建失败")
		.setMessage("oops，你的网络暂时不给力哦。你创建的课程已被保存，可稍后手动上传。")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						finish();
					}
				}).show();
	}
	
	void createCourseCallback(CourseResult result){
		UIUtil.unblock(this);
		if (result != null) {
			if (result.success) {
				if(offlineCourse != null){
					App.getInstance().getDBHelper().deleteOfflineData(App.getInstance().getUserDB(), offlineCourse.id);
					offline = true;
				}
				saved = true;
				mCourseList.add(result.course);
				showConfirmDialog();
				MobclickAgent.onEvent(NewCourseActivity.this, "event_create_course_succeed");
				sendBroadcast(new Intent(
						KechengAppWidgetProvider.UPDATE));
			} else {
				MobclickAgent.onEvent(NewCourseActivity.this, "event_create_course_failed", result.add_course_result);
				Toast toast = Toast.makeText(NewCourseActivity.this, result.notice, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, -100);
				toast.show();
			}
		} else {
			MobclickAgent.onEvent(NewCourseActivity.this, "event_create_course_failed", "network"+BeemConnectivity.isConnected(NewCourseActivity.this));
			if(offlineCourse != null){
				App.getInstance().getDBHelper().deleteOfflineData(App.getInstance().getUserDB(), offlineCourse.id);
			}
			offline = true;
			saveCourseOffline();
			showOfflineDialog();
		}
	}
	
	void saveCourseOffline() {
		MobclickAgent.onEvent(NewCourseActivity.this, "event_create_course_to_offline");
		OfflineData offlineData;
		Gson gson = new Gson();
		List<CourseUnit> courseUnits = courseTimeController.getData();
		c.course_units = courseUnits;
		offlineData = new OfflineData<Course>(Course.class, gson.toJson(c), Operation.ADD, App.getInstance().getActiveSemesterId());
		App app = (App)getApplication();
		app.getDBHelper().saveOfflineData(app.getUserDB(), offlineData);
	}
	
	void updateCourseCallback(UpdateCourseResult result){
		UIUtil.unblock(this);
		if (result != null) {
			if (result.success) {
				sendBroadcast(new Intent(
						KechengAppWidgetProvider.UPDATE));
				Intent intent = new Intent();
				intent.putExtra("COURSE", result.updated_course);
				setResult(RESULT_OK, intent);
				super.finish();
			} else {
				Hint.showTipsLong(NewCourseActivity.this, result.error);
			}
		} else {
			Hint.showTipsLong(NewCourseActivity.this, "创建课程失败，请确保网络连接正常");
		}
	}
	
	DataCallback myDataCallback = new DataCallback() {
		
		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_CREATE_COURSE:{
				CourseResult result = (CourseResult)msg.obj;
				createCourseCallback(result);
				break;
			}
			case DataAdapter.MESSAGE_UPDATE_COURSE:{
				UpdateCourseResult result = (UpdateCourseResult)msg.obj;
				updateCourseCallback(result);
				break;
			}
			default:
				break;
			}
		}
	};
}

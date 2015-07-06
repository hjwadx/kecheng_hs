package fm.jihua.kecheng.ui.activity.plugin;

import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.WheelUtil;
import fm.jihua.common.utils.TimeHelper;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.Examination;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.export.InputOrListActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;

public class AddExaminationActivity extends BaseActivity {

	EditText nameEditText;
	EditText roomEditText;
	TextView selectCourseTextView;
	TextView dateTextView;
	Examination examination;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_examination);
		initTitlebar();
		initViews();
	}

	void initViews() {
		examination = (Examination) getIntent().getSerializableExtra(
				"EXAMINATION");
		nameEditText = (EditText) findViewById(R.id.name);
		roomEditText = (EditText) findViewById(R.id.room);
		dateTextView = (TextView) findViewById(R.id.date);
		selectCourseTextView = (TextView)findViewById(R.id.select_course);
		findViewById(R.id.select_course).setOnClickListener(clickListener);
		dateTextView.setOnClickListener(clickListener);
		if (examination != null) {
			setTitle("修改考试");
			setName(examination.course, examination.name);
			roomEditText.setText(examination.room);
			setTime(examination.time);
		} else {
			MobclickAgent.onEvent(this, "action_add_exam_view");
			setTitle("添加考试");
			examination = new Examination();
		}
	}
	
	private void setName(Course course, String name){
		if (course == null) {
			selectCourseTextView.setText("其它课程");
			((View)nameEditText.getParent().getParent()).setVisibility(View.VISIBLE);
		}else {
			selectCourseTextView.setText(course.name);
			((View)nameEditText.getParent().getParent()).setVisibility(View.GONE);
		}
		nameEditText.setText(name);
	}

	private void setTime(long time) {
		dateTextView.setText(TimeHelper.getTime(time, true));
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action:
			case R.id.action_textview:{
				String name = nameEditText.getText().toString();
				String room = roomEditText.getText().toString();
				if ((name == null || "".equals(name)) && examination.course == null) {
					Hint.showTipsShort(AddExaminationActivity.this, "名字不能为空");
					return;
				}
				if (examination.time == 0) {
					Hint.showTipsShort(AddExaminationActivity.this, "请选择考试时间");
					return;
				}else if (examination.time < System.currentTimeMillis()) {
					Hint.showTipsShort(AddExaminationActivity.this, "考试时间已经过了哦");
					return;
				}
				MobclickAgent.onEvent(AddExaminationActivity.this, "event_add_exam_succeed");
				examination.name = name;
				examination.room = room;
				App app = (App) getApplication();
				app.getDBHelper().saveExamination(app.getUserDB(), examination, true);
				setResult(RESULT_OK);
				finish();
				break;
			}
			case R.id.select_course:
				showCoursePicker();
				break;
			case R.id.date:{
				TextView tv = (TextView)v;
				showTimePicker(tv);
			}
				break;
			default:
				break;
			}
		}
	};
	
	void showTimePicker(final TextView editText){
		try {
			Date dt = examination.time == 0 ? new Date() : new Date(examination.time);
			final View timerPickerView = WheelUtil.getDayAndTimePicker(this, dt);
			DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == -1) {
						examination.time = WheelUtil.getDateAndTime(timerPickerView);
						String timeString = TimeHelper.getTime(examination.time, true);
						editText.setText(timeString);
					}
					dialog.dismiss();
				}	
			};
			new AlertDialog.Builder(AddExaminationActivity.this).setTitle("选择考试时间").setView(timerPickerView)
			.setPositiveButton("确定", clickListener)
			.setNegativeButton("取消", clickListener).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showCoursePicker() {
		Intent intent = new Intent(this, InputOrListActivity.class);
		intent.putExtra(InputOrListActivity.INTENT_KEY, InputOrListActivity.CATEGORY_COURSEPICKER);
		startActivityForResult(intent, Const.INTENT_DEFAULT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		if (data != null) {
			Course course = (Course) data.getSerializableExtra("COURSE");
			examination.course = course;
			if (course != null) {
				setName(examination.course, examination.course.name);
			}else {
				setName(examination.course, "");
			}
		}
	}

	void initTitlebar() {
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		getKechengActionBar().getRightButton().setOnClickListener(clickListener);
	}
}

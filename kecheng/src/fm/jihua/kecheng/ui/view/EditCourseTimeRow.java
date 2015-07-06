package fm.jihua.kecheng.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fm.jihua.common.ui.helper.Hint;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import fm.jihua.kecheng.ui.view.EditCourseTimeWeekTable.onDismissListener;
import fm.jihua.kecheng.utils.CommonUtils;

public class EditCourseTimeRow extends LinearLayout {
	
	ImageView ivDelete;
	Button btnDelete;
	View selectedState;
	TextView courseTimeView;
	CourseBlock courseBlock;
	CourseTimeWheelView wheelView;
	EditText roomEditText;
	TextView tv_weeks;
	TextView tv_add_text;
	
	EditCourseTimeTable parent;
	boolean rotated;
	
	public EditCourseTimeRow(Context context, ViewGroup parent) {
		super(context);
		this.parent = (EditCourseTimeTable) parent;
		initViews();
	}

	public EditCourseTimeRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}
	
	void initViews(){
		inflate(getContext(), R.layout.course_time, this);
		ivDelete = (ImageView)findViewById(R.id.iv_delete);
		btnDelete = (Button)findViewById(R.id.delete_course_time);
		selectedState = findViewById(R.id.selected);
		courseTimeView = (TextView)findViewById(R.id.course_time_string);
		tv_weeks =(TextView) findViewById(R.id.weeks_textview);
		tv_add_text = (TextView) findViewById(R.id.course_time_add_text);
		
		ivDelete.setOnClickListener(clickListener);
		btnDelete.setOnClickListener(clickListener);
		findViewById(R.id.course_time_container).setOnClickListener(clickListener);
		findViewById(R.id.weeks).setOnClickListener(clickListener);
		View rootView = this.parent == null ? this.getRootView() : this.parent.getRootView();
		wheelView = (CourseTimeWheelView)rootView.findViewById(R.id.wheel_view);
		wheelView.setHost(this);
		roomEditText = (EditText)findViewById(R.id.room);
		roomEditText.setOnClickListener(clickListener);
//		roomEditText.setOnFocusChangeListener(new MyOnFocusChangeListener());
	}
	
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_delete:
				if (rotated) {
					setSelected();
				}else {
					btnDelete.setVisibility(View.VISIBLE);
					rotate(ivDelete, 0, -90);
					rotated = true;
				}
				break;
			case R.id.delete_course_time:
				parent.deleteChildViews(EditCourseTimeRow.this);
				break;
			case R.id.course_time_container:
				setSelected();
				break;
			case R.id.room:
//				wheelView.setVisibility(View.GONE);
				break;
			// add weeks
			case R.id.weeks:
//				wheelView.setVisibility(View.GONE);
				showWeekDialog(v);
				break;
			default:
				break;
			}
			
		}
	};
	
	Dialog dialog;

	void showWeekDialog(View view) {
		if (dialog == null)
			dialog = new Dialog(getContext(), R.style.MyDialog);
		EditCourseTimeWeekTable editCourseTimeWeekTable = new EditCourseTimeWeekTable(getContext(), view);
		editCourseTimeWeekTable.setOnDismissListener(new onDismissListener() {

			@Override
			public void dismiss() {
				dialog.dismiss();
			}
		});
		dialog.setContentView(editCourseTimeWeekTable);
		dialog.show();
	}
	
	public void hideDeleteBtn(){
		ivDelete.setVisibility(View.INVISIBLE);
	}
	
	class MyOnFocusChangeListener implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v instanceof EditText && hasFocus) {
//				wheelView.setVisibility(View.GONE);
			}
		}
	}
	
	void rotate(View view, int fromDegrees, int toDegrees){
		RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(150);
		animation.setFillAfter(true);
		view.startAnimation(animation);
	}
	
	public void setSelected(){
		Activity parent = (Activity)getContext();
		if (parent.getCurrentFocus() != null && parent.getCurrentFocus() instanceof EditText) {
			parent.getCurrentFocus().clearFocus();
			((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(parent.getCurrentFocus().getWindowToken(), 0);
		}
		if (wheelView.getVisibility() == View.GONE) {
			wheelView.setVisibility(View.VISIBLE);
		}
		wheelView.setHost(this);
		courseBlock.weeks = (String) tv_weeks.getTag();
		courseBlock.room = roomEditText.getText().toString();
		wheelView.setData(courseBlock);
		ViewGroup parentView = ((ViewGroup)getParent());
		for(int i=0; i<parentView.getChildCount(); i++){
			if (parentView.getChildAt(i) instanceof EditCourseTimeRow) {
				EditCourseTimeRow row = (EditCourseTimeRow) parentView.getChildAt(i);
				row.unSelect();
			}
		}
		if (btnDelete.getVisibility() == View.VISIBLE) {
			btnDelete.setVisibility(View.GONE);
		}
		if (rotated) {
			rotate(ivDelete, -90, 0);
			rotated = false;
		}
		selectedState.setVisibility(View.VISIBLE);
	}
	
	public void unSelect(){
		selectedState.setVisibility(View.GONE);
	}
	
	public void setData(CourseBlock courseBlock, int index) {
		this.courseBlock = (CourseBlock) courseBlock.clone();
		if (!CommonUtils.isNullString(courseBlock.weeks)) {
			tv_weeks.setText(CourseUnit.getWeekStringWithOccurance(courseBlock.weeks));
		}
		tv_weeks.setTag(courseBlock.weeks);
		if(courseBlock.start_slot != 0 && courseBlock.end_slot != 0){
			courseTimeView.setText(courseBlock.toString());
		}
		roomEditText.setText(courseBlock.room);
		if (index != -1)
			tv_add_text.setText("上课时间" + String.valueOf(index + 1));
	}
	
	public void resetData(int index){
		tv_add_text.setText("上课时间" + String.valueOf(index + 1));
	}
	
	public CourseBlock getData(){
		courseBlock.room = roomEditText.getText().toString().trim();
		courseBlock.weeks = (String) tv_weeks.getTag();
		return (CourseBlock) courseBlock.clone();
	}
	
	public CourseUnit getDataUnit(){
		courseBlock.room = roomEditText.getText().toString().trim();
		String slotString = courseBlock.start_slot == courseBlock.end_slot? "" + courseBlock.start_slot : courseBlock.start_slot + "-" + courseBlock.end_slot;
		return new CourseUnit(courseBlock.day_of_week, slotString , courseBlock.room, (String)tv_weeks.getTag());
	}
	
	public boolean isHasTimeNull(){
		if(courseBlock.start_slot == 0 || courseBlock.end_slot == 0){
			return true;
		}
		return false;
	}
	
	public boolean checkContentIsValid() {
		if (TextUtils.isEmpty(tv_weeks.getText().toString())) {
			Hint.showTipsShort(getContext(), "请先填写好上课周数");
			return false;
//		} else if (TextUtils.isEmpty(roomEditText.getText().toString())) {
//			Hint.showTipsShort(getContext(), "请先填写好上课地点");
//			return false;
		} else {
			return true;
		}
	}
}

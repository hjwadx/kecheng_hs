package fm.jihua.kecheng.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.CourseUnit;
import fm.jihua.kecheng.utils.CourseHelper;

public class EditCourseTimeTable extends LinearLayout {
	
	View newCourseTimeView;
	CourseTimeWheelView wheelView;

	public EditCourseTimeTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public EditCourseTimeTable(Context context) {
		super(context);
		initView();
	}
	
	void initView(){
		inflate(getContext(), R.layout.add_course_time_button, this);
		newCourseTimeView = findViewById(R.id.add_course_time_layout);
		newCourseTimeView.setOnClickListener(clickListener);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		wheelView = (CourseTimeWheelView) getRootView().findViewById(R.id.wheel_view);
//		if (getChildCount() == 1) {
//			addCourseTime(null, false);
//		}
	}

	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_course_time_layout:
				if (listEditRow.size() == 0 || listEditRow.get(listEditRow.size() - 1).checkContentIsValid()) {
					addCourseTime(null, true);
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void removeView(View view) {
		super.removeView(view);
		refreshView();
	}

	@Override
	public void removeViewAt(int index) {
		super.removeViewAt(index);
		refreshView();
	}
	
	void refreshView(){
		if (getChildCount() == 1) {
//			wheelView.setVisibility(View.GONE);
		}else if(getChildCount() > 1) {
//			EditCourseTimeRow row = (EditCourseTimeRow) getChildAt(0);
//			row.findViewById(R.id.course_time_container).setBackgroundResource(R.drawable.input_top_whole);
		}
	}
	
	public List<CourseUnit> getData(){
		int count = getChildCount();
		List<CourseUnit> courseUnits = new ArrayList<CourseUnit>();
		
		for (int i = 0; i < count-1; i++) {
			EditCourseTimeRow view = (EditCourseTimeRow) getChildAt(i);
			CourseUnit courseUnit = view.getDataUnit();
			courseUnits.add(courseUnit);
		}
		return courseUnits;
	}
	
	public List<CourseUnit> getDataMerged(){
		int count = getChildCount();
		List<CourseUnit> courseUnits = new ArrayList<CourseUnit>();
		
		for (int i = 0; i < count-1; i++) {
			EditCourseTimeRow view = (EditCourseTimeRow) getChildAt(i);
			CourseUnit courseUnit = view.getDataUnit();
			courseUnits = courseUnit.mergeToCourseUnits(courseUnits);
		}
		return courseUnits;
	}
	
	//暂时没用上
//	public Point getConflictUnit(){
//		int count = getChildCount();
//		List<CourseUnit> courseUnits = new ArrayList<CourseUnit>();
//		for (int i = 0; i < count-1; i++) {
//			EditCourseTimeRow view = (EditCourseTimeRow) getChildAt(i);
//			CourseUnit courseUnit = view.getDataUnit();
//			CourseUnit conflictUnit = courseUnit.getConflictUnits(courseUnits);
//			if(conflictUnit != null){
//				return new Point(courseUnits.indexOf(conflictUnit), i);
//			}
//			courseUnits.add(courseUnit);
//		}
//		return null;
//	}
	
	public boolean isHasTimeNull(){
		int count = getChildCount();
		
		for (int i = 0; i < count-1; i++) {
			EditCourseTimeRow view = (EditCourseTimeRow) getChildAt(i);
			if(view.isHasTimeNull()){
				return true;
			}
		}
		return false;
	}
	
	List<EditCourseTimeRow> listEditRow = new ArrayList<EditCourseTimeRow>();
	
	public void setData(Course course){
		if(course == null){
			addCourseTime(null, false);
			return;
		}
		for (int i = 0; i < getChildCount()-2; i++) {
			removeViewAt(i);
		}
		List<CourseBlock> blocks = CourseHelper.getCourseBlocksFromCourse(course, false);
		int size = blocks.size();
		for (int index = 0; index < size; index++) {
			EditCourseTimeRow row = new EditCourseTimeRow(getContext(), this);
			row.setData(blocks.get(index), index);
			listEditRow.add(row);
			addView(row, index);
		}
	}
	
	private void addCourseTime(CourseBlock block, boolean shouldScroll){
		Activity parent = (Activity)getContext();
//		if (parent.getCurrentFocus() != null && parent.getCurrentFocus() instanceof EditText) {
//			parent.getCurrentFocus().clearFocus();
//			((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(parent.getCurrentFocus().getWindowToken(), 0);
//		}
		if (block == null) {
			block = new CourseBlock();
			if (getChildCount() > 1 && getChildAt(getChildCount() - 2) != null && getChildAt(getChildCount() - 2) instanceof EditCourseTimeRow) {
				block.weeks = ((EditCourseTimeRow)getChildAt(getChildCount() - 2)).getData().weeks;
//				block = ((EditCourseTimeRow)getChildAt(getChildCount() - 2)).getData();
//				block.day_of_week = CourseHelper.getIndexFromDayOfWeek(block.day_of_week + 1);
			}else {
				block.weeks = "1-17";
			}
		}
		final EditCourseTimeRow editCourseTimeRow = new EditCourseTimeRow(getContext(), this);
		int index = indexOfChild(newCourseTimeView);
		editCourseTimeRow.setData(block, index);
		if(listEditRow.size() == 0){
			editCourseTimeRow.hideDeleteBtn();
		}
		listEditRow.add(editCourseTimeRow);
		addView(editCourseTimeRow, index);
		if (shouldScroll) {
			post(new Runnable() {            
			    @Override
			    public void run() {
			    	setFocus(editCourseTimeRow);
//			    	editCourseTimeRow.setSelected();
			    	wheelView.post(new Runnable() {
						
						@Override
						public void run() {
							((ScrollView)getRootView().findViewById(R.id.add_course_form_container)).fullScroll(ScrollView.FOCUS_DOWN);
						}
					});
			    }
			});
		}
	}
	
	public void deleteChildViews(EditCourseTimeRow courseTimeRowDelete) {
		listEditRow.remove(courseTimeRowDelete);
		removeView(courseTimeRowDelete);
		int size = listEditRow.size();
		for (int index = 0; index < size; index++) {
			EditCourseTimeRow courseTimeRow = listEditRow.get(index);
			courseTimeRow.resetData(index);
		}
	}
	
	private void setFocus(View view){
		view.setFocusable(true); 
    	view.requestFocus();
    	view.setFocusableInTouchMode(true);
	}
}

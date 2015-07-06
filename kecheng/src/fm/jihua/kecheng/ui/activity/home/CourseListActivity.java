package fm.jihua.kecheng.ui.activity.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import fm.jihua.common.ui.helper.Hint;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.adapter.SearchAdapter;
import fm.jihua.kecheng_hs.R;

public class CourseListActivity extends BaseActivity {

	ListView listView;
	private List<Boolean> initBooleanList;
	private SearchAdapter searchAdapter;
	private List<Course> mCourses;
	
	public static final String INTENT_KEY_COURSE_LIST = "course_list";
	public static final String INTENT_KEY_FROM = "courses_from";
	public static final int REQUESTCODE_2_COURSE_LIST = 100;
	
//	int from;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_course_copy_other);
//		from = getIntent().getIntExtra(INTENT_KEY_FROM, 0);
		initTitlebar();
		findViews();
		initViews();
	}

	void initTitlebar() {
		setTitle(R.string.title_import_course);
		View actionButton = getKechengActionBar().getActionButton();
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		actionButton.setVisibility(View.VISIBLE);
//		actionButton.setText(R.string.import_action);
		actionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<Course> listCourse = getListCourse();
				if (listCourse.size() == 0) {
					Hint.showTipsShort(CourseListActivity.this, R.string.choice_import_course);
				} else {
					Intent intent = getIntent();
					intent.putExtra(INTENT_KEY_COURSE_LIST, getIdString(listCourse));
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}

	void findViews() {
		listView = (ListView) findViewById(R.id.add_course_courses);
	}

	public void CopyCourseClick(View view) {
		switch (view.getId()) {
		case R.id.add_course_copy_choice_all:
			int size1 = initBooleanList.size();
			for (int i = 0; i < size1; i++) {
				initBooleanList.set(i, true);
			}
			searchAdapter.setBooleanList(initBooleanList);
			break;
		case R.id.add_course_copy_choice_none:
			int size2 = initBooleanList.size();
			for (int i = 0; i < size2; i++) {
				initBooleanList.set(i, false);
			}
			searchAdapter.setBooleanList(initBooleanList);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	void initViews() {
		Intent intent = getIntent();
		mCourses = (List<Course>) intent.getSerializableExtra(INTENT_KEY_COURSE_LIST);
		initBooleanList = initBooleanList(mCourses);
		searchAdapter = new SearchAdapter(mCourses, initBooleanList, true, true);
		listView.setAdapter(searchAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_search_item);
				initBooleanList.set(position, !checkBox.isChecked());
				searchAdapter.setBooleanList(initBooleanList);
			}
		});
	}

	List<Boolean> initBooleanList(List<Course> courses) {
		ArrayList<Boolean> listBoo = new ArrayList<Boolean>();
		int size = courses.size();
		for (int i = 0; i < size; i++) {
			listBoo.add(true);
		}
		return listBoo;
	}

	List<Course> getListCourse() {
		List<Course> courses = new ArrayList<Course>();
		int size = initBooleanList.size();
		for (int i = 0; i < size; i++) {
			if (initBooleanList.get(i)) {
				courses.add(mCourses.get(i));
			}
		}
		return courses;
	}
	
	String getIdString(List<Course> courses){
		StringBuffer stringBuffer=new StringBuffer();
		for (Course course : courses) {
			if(stringBuffer.length() == 0){
				stringBuffer.append(String.valueOf(course.id));
			}else{
				stringBuffer.append(","+String.valueOf(course.id));
			}
		}
		return stringBuffer.toString();
	}
}

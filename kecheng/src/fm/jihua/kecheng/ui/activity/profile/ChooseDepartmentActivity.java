package fm.jihua.kecheng.ui.activity.profile;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Department;
import fm.jihua.kecheng.rest.entities.DepartmentsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.utils.SchoolDBHelper;

public class ChooseDepartmentActivity extends BaseActivity{
	boolean firstLoad = true;
	EditText searchBox;
	ListView searchListView;
	String selectedDepartment;
	String otherDepartment;
	DataAdapter mDataAdapter;
	List<Department> mDepartments = new ArrayList<Department>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_edit_list);
		setTitle(R.string.choose_department);
		otherDepartment = getString(R.string.other_department);
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		mDataAdapter.searchAllDepartments(getIntent().getIntExtra("SCHOOL_ID", 0));
		findViews();
		addListener();
		initList();
	}

	private void findViews(){
		searchBox = (EditText) findViewById(R.id.add_course_search);
		searchBox.setHint(R.string.input_department_name);
		searchListView = (ListView) findViewById(R.id.add_course_courses);
	}

	private void addListener(){
		searchBox.addTextChangedListener(new MyTextWatcher());
		searchListView.setOnItemClickListener(new MyOnItemClickListener());
	}

	private void initList(){
		search(getIntent().getIntExtra("SCHOOL_ID", 0), "");
	}

	class MyOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			Object object = listView.getAdapter().getItem(position);
			if (object != null && object instanceof HashMap) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> department = (HashMap<String, Object>) object;
				selectedDepartment = (String) department.get("name");
				// if (selectedDepartment.equals(otherDepartment)) {
				// showEnterDepartmentDialog();
				// }else {
				next();
				// }
				// overridePendingTransition(android.R.anim.slide_in_left,
				// android.R.anim.slide_out_right);
				// Course course = (Course) object;
				// addCourse(course.id);
			}
		}
	}

	private void next(){
		if (getIntent().getIntExtra("YEAR", 0) != 0) {
			Intent data = new Intent();
			data.putExtra("DEPARTMENT", selectedDepartment);
			setResult(RESULT_OK, data);
			finish();
		}else {
			Intent intent = new Intent(this, ChooseListActivity.class);
			intent.putExtra(ConstProfile.INTENT_TO_CHOOSELIST_TYPE_KEY, ConstProfile.CHOOSELIST_TYPE_YEAR);
			intent.putExtra(ConstProfile.INTENT_TO_CHOOSELIST_TITLE_KEY, getString(R.string.name_year));
			startActivityForResult(intent, 0);
		}
	}

	class MyTextWatcher implements TextWatcher {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			search(getIntent().getIntExtra("SCHOOL_ID", 0), s.toString());
		}
	}

	private void search(int schoolId, String term) {
		// UIUtil.block(AddCourseActivity.this);
		App app = (App) getApplication();
		SchoolDBHelper dbHelper = app.getSchoolDBHelper();
		List<Department> departments = dbHelper.getDepartments(schoolId, term);
		List<HashMap<String, Object>> departsMap = new ArrayList<HashMap<String, Object>>();
		for (Department department : departments) {
			departsMap.add(department.toHashMap());
		}
		if (mDepartments.size() > 0) {
			for (Department department : mDepartments) {
				if(department.name.contains(term)) {
					departsMap.add(department.toHashMap());
				}
			}
		}
		Department depart = new Department(0, otherDepartment);
		departsMap.add(depart.toHashMap());
		refreshUI(departsMap);
	}


	private void refreshUI(List<HashMap<String, Object>> schools) {
		SimpleAdapter adapter = new SimpleAdapter(this, schools, R.layout.simple_list_item, new String[]{"name"}, new int[]{R.id.title});
		searchListView.setAdapter(adapter);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK){
			return;
		}
		try {
			data.putExtra("DEPARTMENT", selectedDepartment);
			setResult(RESULT_OK, data);
			finish();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_SEARCH_ALL_DEPARTMENTS:
				DepartmentsResult result = (DepartmentsResult) msg.obj;
				if (result != null && result.success){
					App app = (App) getApplication();
					SchoolDBHelper dbHelper = app.getSchoolDBHelper();
					List<Department> departments = dbHelper.getDepartments(getIntent().getIntExtra("SCHOOL_ID", 0), "");
					List<HashMap<String, Object>> departsMap = new ArrayList<HashMap<String, Object>>();
					for (Department department : departments) {
						departsMap.add(department.toHashMap());
					}
					for (Department department : result.departments) {
						if (departsMap.contains(department.toHashMap())){
							continue;
						}
						mDepartments.add(department);
					}
					search(getIntent().getIntExtra("SCHOOL_ID", 0), searchBox.getText().toString());
				}
				break;
			default:
				break;
			}
		}

	}
}

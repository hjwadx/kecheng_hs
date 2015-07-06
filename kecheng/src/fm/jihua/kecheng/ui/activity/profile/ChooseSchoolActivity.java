package fm.jihua.kecheng.ui.activity.profile;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
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

import com.umeng.analytics.MobclickAgent;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.School;
import fm.jihua.kecheng.rest.entities.SchoolsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.register.ChooseClassesActivity;
import fm.jihua.kecheng.ui.activity.register.RegisterFragment;
import fm.jihua.kecheng.ui.adapter.BottomTipsWrapperAdapter;
import fm.jihua.kecheng.utils.SchoolDBHelper;

public class ChooseSchoolActivity extends BaseActivity {
	boolean firstLoad = true;
	EditText searchBox;
	ListView searchListView;
//	TextView headerView;
	String selectedSchool;
	int schoolId;
	int county_id;
	BottomTipsWrapperAdapter mBottomTipsWrapperAdapter;
	String otherSchool;
	int cityId;
//	DataAdapter mDataAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent().getBooleanExtra("FORM_REGISTER_VIEW", false)){
			MobclickAgent.onEvent(this, "enter_choose_school_view");
		}
		setContentView(R.layout.profile_edit_list);
		actionbar.setTitle(R.string.choose_school);
		otherSchool = getString(R.string.other_school);
		cityId = getIntent().getIntExtra(RegisterFragment.INTENT_KEY_CITY_ID, 0);
//		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		actionbar.setShowBackBtn(true);
		findViews();
		addListener();
		initList();
	}
	
	private void findViews(){
		searchBox = (EditText) findViewById(R.id.add_course_search);
		searchBox.clearFocus();
		searchBox.setHint(R.string.input_school_name);
		searchListView = (ListView) findViewById(R.id.add_course_courses);
		findViewById(R.id.hint_parnet).setVisibility(View.VISIBLE);
//		headerView =new TextView(this);
//		headerView.setText("test");
	}
	
	private void addListener(){
		searchBox.addTextChangedListener(new MyTextWatcher());
		searchListView.setOnItemClickListener(new MyOnItemClickListener());
	}
	
	private void initList(){
		search("");
	}
	
	class MyOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			Object object = listView.getAdapter().getItem(position);
			if(object != null && object instanceof HashMap){
				@SuppressWarnings("unchecked")
				HashMap<String, Object> school = (HashMap<String, Object>)object;
				schoolId = (Integer) school.get("id");
				selectedSchool = (String) school.get("name");
				county_id = (Integer) school.get("county_id");
//				startChooseDepartmentActivity();
				intent2EditName();
			}
		}
	}
	
	void startChooseDepartmentActivity(){
		Intent intent = new Intent(ChooseSchoolActivity.this, ChooseDepartmentActivity.class);
		intent.putExtra("SCHOOL_ID", schoolId);
		intent.putExtra("YEAR", getIntent().getIntExtra("YEAR", 0));	
//		startActivityForResult(intent, 0);
	}
	
	void intent2EditName(){
		Intent intent = new Intent(this, ChooseClassesActivity.class);
		intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, getIntent().getStringExtra(RegisterFragment.INTENT_KEY_CLASSES));
		intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, getIntent().getStringExtra(RegisterFragment.INTENT_KEY_GRADE));
		startActivityForResult(intent, ConstProfile.REQUESTCODE_PROFILE_NAME);
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
			search(s.toString());
		}
	}
	
	private void search(final String term) {
		boolean withTip = false;
		App app = (App)getApplication();
		SchoolDBHelper dbHelper = app.getSchoolDBHelper();
		List<School> schools = dbHelper.getSchools(cityId, term);
		if (term.length() > 1 || schools.size() < 5) {
//			mDataAdapter.searchSchools(term, 1, 50);
//			withTip = true;
		}
		List<HashMap<String, Object>> schoolsMap = new ArrayList<HashMap<String,Object>>();
		for (School school : schools) {
			schoolsMap.add(school.toHashMap());
		}
		if (schoolsMap.size() < SchoolDBHelper.MAX_SCHOOL_LENGTH) {
			School school = new School(0, otherSchool, 0);
			schoolsMap.add(school.toHashMap());
		}
		refreshUI(schoolsMap, withTip);
	}
	
	
	private void refreshUI(List<HashMap<String, Object>> schools, boolean withTip) {
		SimpleAdapter adapter = new SimpleAdapter(this, schools, R.layout.simple_list_item, new String[]{"name"}, new int[]{R.id.title});
		mBottomTipsWrapperAdapter = new BottomTipsWrapperAdapter(adapter);
	    mBottomTipsWrapperAdapter.showTips(withTip);
		searchListView.setAdapter(mBottomTipsWrapperAdapter);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK){
			return;
		}
		try {
			data.putExtra(RegisterFragment.INTENT_KEY_SCHOOL_ID, schoolId);
			data.putExtra(RegisterFragment.INTENT_KEY_SCHOOL, selectedSchool);
			data.putExtra(RegisterFragment.INTENT_KEY_COUNTY, county_id);
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
			case DataAdapter.MESSAGE_SEARCH_SCHOOLS:
				SchoolsResult result = (SchoolsResult) msg.obj;
				if (result != null && result.success){
					if(result.term.equals(searchBox.getText().toString())){
						App app = (App)getApplication();
						SchoolDBHelper dbHelper = app.getSchoolDBHelper();
						List<School> schools = dbHelper.getSchools(cityId, searchBox.getText().toString());
						List<HashMap<String, Object>> schoolsMap = new ArrayList<HashMap<String,Object>>();
						for (School school : schools) {
							schoolsMap.add(school.toHashMap());
						}
						for (School school : result.schools) {
							if (schoolsMap.contains(school.toHashMap())){
								continue;
							}
							schoolsMap.add(school.toHashMap());
						}
						if (schoolsMap.size() < SchoolDBHelper.MAX_SCHOOL_LENGTH) {
							School school = new School(0, otherSchool, 0);
							schoolsMap.add(school.toHashMap());
						}
						refreshUI(schoolsMap, false);
					}			
				}
				mBottomTipsWrapperAdapter.showTips(false);
				mBottomTipsWrapperAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			
		}
		
	}
}

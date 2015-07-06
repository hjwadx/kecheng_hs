package fm.jihua.kecheng.ui.activity.register;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper.SchoolInfo;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;

public class ChooseRenrenSchoolActivity extends BaseActivity {
	boolean firstLoad = true;
	EditText searchBox;
	ListView searchListView;
	String selectedSchool;
	int schoolId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.add_course);
			setTitle("选择学校");
			findViews();
			addListener();
			initList();
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}
	
	private void findViews(){
		searchBox = (EditText) findViewById(R.id.add_course_search);
		((View)searchBox.getParent()).setVisibility(View.GONE);
		searchListView = (ListView) findViewById(R.id.add_course_courses);
	}
	
	private void addListener(){
		searchListView.setOnItemClickListener(new MyOnItemClickListener());
	}
	
	private void initList(){
		Intent intent = getIntent();
		
		List<SchoolInfo> schoolInfos = (List<SchoolInfo>) intent.getSerializableExtra("SCHOOL_INFOS");
		List<HashMap<String, Object>> schools = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < schoolInfos.size(); i++) {
			SchoolInfo schoolInfo = schoolInfos.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", schoolInfo.school);
			map.put("department", CommonUtils.replaceNullString(schoolInfo.department));
			map.put("year", String.valueOf(schoolInfo.year));
			schools.add(map);
		}
		refreshUI(schools);
	}
	
	class MyOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			Object object = listView.getAdapter().getItem(position);
			if(object != null && object instanceof HashMap){
				@SuppressWarnings("unchecked")
				HashMap<String, Object> school = (HashMap<String, Object>)object;
				Intent intent = new Intent();
				intent.putExtra("SCHOOL", (String)school.get("name"));
				intent.putExtra("DEPARTMENT", (String)school.get("department"));
				intent.putExtra("YEAR", Integer.valueOf((String) school.get("year")));
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
	
	private void refreshUI(List<HashMap<String, Object>> schools) {
		SimpleAdapter adapter = new SimpleAdapter(this, schools, R.layout.school_info, 
				new String[]{"name", "department", "year"}, 
				new int[]{R.id.school, R.id.department, R.id.year});
		searchListView.setAdapter(adapter);
	}
}

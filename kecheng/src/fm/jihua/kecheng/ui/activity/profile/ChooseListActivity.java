package fm.jihua.kecheng.ui.activity.profile;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.BaseActivity;

public class ChooseListActivity extends BaseActivity {
	
	ListView searchListView;
	
	boolean firstLoad = true;
	String selectedDepartment;
	int type;
	String title="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_edit_list);
		getTypeByIntent();
		setTitle(title);
		findViews();
		addListener();
		initList();
	}
	
	void getTypeByIntent(){
		type = getIntent().getIntExtra(ConstProfile.INTENT_TO_CHOOSELIST_TYPE_KEY, 0);
		title = getIntent().getStringExtra(ConstProfile.INTENT_TO_CHOOSELIST_TITLE_KEY);
	}
	
	private void findViews(){
		EditText searchBox = (EditText) findViewById(R.id.add_course_search);
		((View)searchBox.getParent()).setVisibility(View.GONE);
		searchListView = (ListView) findViewById(R.id.add_course_courses);
	}
	
	private void addListener(){
		searchListView.setOnItemClickListener(new MyOnItemClickListener());
	}
	
	private void initList(){
		setData();
	}
	
	class MyOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			Object object = listView.getAdapter().getItem(position);
			if(object != null && object instanceof HashMap){
				@SuppressWarnings("unchecked")
				HashMap<String, Object> year = (HashMap<String, Object>)object;
				Intent intent = new Intent();
				switch (type) {
				case ConstProfile.CHOOSELIST_TYPE_YEAR:
					intent.putExtra("YEAR", Integer.valueOf((String) year.get("name")));
					break;
				case ConstProfile.CHOOSELIST_TYPE_FIELD:
					intent.putExtra("FIELD", (String) year.get("name"));
					break;
				case ConstProfile.CHOOSELIST_TYPE_EDUCATION:
					intent.putExtra("EDUCATION", (String) year.get("name"));
					break;
				default:
					break;
				}
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
	
	
	private void setData() {
		
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		switch (type) {
		case ConstProfile.CHOOSELIST_TYPE_YEAR:
			Date dt = new Date();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(dt);
			
			for (int i=0; i<6; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", String.valueOf(calendar.get(Calendar.YEAR)-i));
				list.add(map);
			}
			break;
		case ConstProfile.CHOOSELIST_TYPE_FIELD:
			for (int i=0; i < ConstProfile.career_goal.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", ConstProfile.career_goal[i]);
				list.add(map);
			}
			break;
		case ConstProfile.CHOOSELIST_TYPE_EDUCATION:
			for (int i = 0; i < ConstProfile.education.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("name", ConstProfile.education[i]);
				list.add(map);
			}
			break;

		default:
			break;
		}
		refreshUI(list);
	}
	
	
	private void refreshUI(List<HashMap<String, Object>> schools) {
		SimpleAdapter adapter = new SimpleAdapter(this, schools, R.layout.simple_list_item, new String[]{"name"}, new int[]{R.id.title});
		searchListView.setAdapter(adapter);
	}
	
	
}

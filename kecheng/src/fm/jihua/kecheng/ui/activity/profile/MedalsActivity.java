package fm.jihua.kecheng.ui.activity.profile;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Medal;
import fm.jihua.kecheng.rest.entities.MedalsResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.adapter.MedalAdpter;

public class MedalsActivity extends BaseActivity{
	
	boolean firstLoad = true;
	EditText searchBox;
	ListView searchListView;
	List<Medal> ownMedals;
	MedalAdpter medalAdpter;

	User user;

	DataAdapter dataAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_course);
		setTitle("在读");
		user = (User) getIntent().getSerializableExtra("USER");
		findViews();
		// addListener();
		initList();
	}
	
	private void findViews(){
		searchBox = (EditText) findViewById(R.id.add_course_search);
		((View)searchBox.getParent()).setVisibility(View.GONE);
		searchListView = (ListView) findViewById(R.id.add_course_courses);
	}
	
	@SuppressWarnings("unchecked")
	private void initList(){
		ownMedals = (List<Medal>) getIntent().getSerializableExtra("MEDALS");
		dataAdapter = new DataAdapter(this, new MyDataCallback());
		if (ownMedals == null) {
//			dataAdapter.getUserMedals(user.id);
		}else {
			setData();
		}
	}

	private void setData() {
		// TODO Auto-generated method stub
		medalAdpter = new MedalAdpter(this, ownMedals);
		searchListView.setAdapter(medalAdpter);
	}
	
	private class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_USER_MEDALS:{
				UIUtil.unblock(MedalsActivity.this);
				MedalsResult result = (MedalsResult) msg.obj;
				if (result != null) {
					if (result.medals != null) {
						ownMedals = Arrays.asList(result.medals);
						setData();
					}
//					if (!result.finished && result.courses.length == 0) {
//						isCourseNull = true;
//						UIUtil.block(WeekActivity.this);
//					}
//					if(result.courses.length != 0){
//						isCourseNull = false;
//						addView.setVisibility(View.VISIBLE);
//					}
				}
			}
				break;
			default:
				break;
			}
			
		}
		
	}

}

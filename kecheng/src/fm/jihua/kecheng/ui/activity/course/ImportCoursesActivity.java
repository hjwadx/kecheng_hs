package fm.jihua.kecheng.ui.activity.course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.data.adapters.AddFriendsByClassmates;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.home.WeekActivity;
import fm.jihua.kecheng.ui.adapter.UserAdapter;
import fm.jihua.kecheng.ui.view.BiasHintView;
import fm.jihua.kecheng_hs.R;

public class ImportCoursesActivity extends BaseActivity{
	DataAdapter mDataAdapter;
	
	BiasHintView emptyView;
	
	ListView classmateListView;
	
	DataAdapter mAdapter;
	int page = 1;
	int perpage = 1000;
	List<User> mUsers = new ArrayList<User>();
	UserAdapter adpter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onEvent(this, "enter_import_course_view");
		setContentView(R.layout.act_import_courses);
		mAdapter = new DataAdapter(this, new MyDataCallback());
		if(App.getInstance().isSupportImport()){
			App.getInstance().setKnowImportCourse(true);
		}
		findView();
		initTitlebar();
		init();
		initFriends();
	}
	
	void initFriends(){
		mAdapter.getClassmates(page,perpage, AddFriendsByClassmates.CLASSMATES_SAME_CLASS);
	}
	
	private void findView() {
		emptyView = (BiasHintView) findViewById(R.id.empty);
		classmateListView = (ListView) findViewById(R.id.user_list);
	}

	void init(){
		emptyView.setText("你还没有同班同学，快去邀请同学加入课程格子吧！！！");
//		refreshViews(mUsers);
	    adpter = new UserAdapter(this, mUsers);
	    classmateListView.setDivider(null);
	    classmateListView.setAdapter(adpter);
	    classmateListView.setOnItemClickListener(new MyOnItemClickListener());
	}
	
	OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action_textview:
//				App.getInstance().setKnowImportCourse(true);
				finish();
				Intent intent = new Intent(ImportCoursesActivity.this, AddCourseActivity.class);
				startActivity(intent);
				break;
			case R.id.menu:
//				App.getInstance().setKnowImportCourse(true);
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	void initTitlebar(){
		setTitle("添加课程");
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		getKechengActionBar().setRightText("手动添加");
		getKechengActionBar().getRightTextButton().setOnClickListener(listener);
		getKechengActionBar().getLeftButton().setOnClickListener(listener);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		default:
			break;
		}
	}
	
	class MyDataCallback implements DataCallback{
		@Override
		public void callback(android.os.Message msg) {
			UIUtil.unblock(ImportCoursesActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_CLASSMATES:
				if (msg.obj != null) {
					UsersResult result = (UsersResult) msg.obj;
					if (result.success) {
						ArrayList<User> friends = new ArrayList<User>();
						friends.addAll(Arrays.asList(result.users));
						if (!result.finished && friends.size() == 0) {
							UIUtil.block(ImportCoursesActivity.this);
						} else {
							mUsers.clear();
							mUsers.addAll(friends);
							refreshViews(mUsers);
						}
					}
				}
				break;

			default:
				break;
			}
		}
	}
	
	class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			User user = (User) listView.getAdapter().getItem(position);
			Intent intent = new Intent(ImportCoursesActivity.this, WeekActivity.class);
			intent.putExtra("USER", user);
			startActivity(intent);
//			finish();
		}
	}
	
	void refreshViews(List<User> users){
		if (users == null || users.size() == 0) {
			emptyView.setVisibility(View.VISIBLE);
		    findViewById(R.id.top_hint_parent).setVisibility(View.GONE);
		    classmateListView.setVisibility(View.GONE);
		}else {
			emptyView.setVisibility(View.GONE);
		    findViewById(R.id.top_hint_parent).setVisibility(View.VISIBLE);
		    classmateListView.setVisibility(View.VISIBLE);
		    adpter.notifyDataSetChanged();
		}
	}
}

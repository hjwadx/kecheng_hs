package fm.jihua.kecheng.ui.activity.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.view.AddFriendsListView;
import fm.jihua.kecheng.ui.view.AddFriendsListView.ResultCallback;
import fm.jihua.kecheng.ui.view.BiasHintView;
import fm.jihua.kecheng.utils.Const;

public class AddFriendsBySNSActivity extends BaseActivity {
	
	final List<User> users = new ArrayList<User>();
	int category;
	AddFriendsListView listView;
	BiasHintView emptyView;
	@SuppressWarnings("serial")
	Map<Integer, String> hash = new HashMap<Integer, String>(){{
		put(Const.RENREN, "人人网好友");
		put(Const.WEIBO, "新浪微博好友");
		put(Const.CLASSMATES, "同班的同学");
		put(Const.FOLLOWER, "加我为好友的同学");
		put(Const.CLASSMATES_GRADE, "同年级的同学");
		put(Const.CLASSMATES_SCHOOL, "同校的同学");
	}};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		category = getIntent().getIntExtra("category", Const.RENREN);
		setContentView(R.layout.add_friends_list);
		initTitlebar();
		init();
	}
	
	void initTitlebar(){
		String title = getIntent().getStringExtra("title");
		title = title == null ? hash.get(category) : title;
		setTitle(title);
	}
	
	void init(){
		emptyView = (BiasHintView) findViewById(R.id.empty);
		UIUtil.block(this);
		listView = ((AddFriendsListView)findViewById(R.id.user_list));
		listView.init(category);
		listView.showUsers(null);
		listView.setResultCallback(new MyResultCallback());
		switch (category) {
		case Const.RENREN:
			emptyView.setText(R.string.empty_search_by_renren);
			break;
		case Const.WEIBO:
			emptyView.setText(R.string.empty_search_by_sina_weibo);		
			break;
		case Const.CLASSMATES_SCHOOL:{
			App app = App.getInstance();
			User user = app.getMyself();
			emptyView.setText(String.format(getResources().getString(R.string.empty_search_by_department), user.school));
			break;
		}
		case Const.CLASSMATES_GRADE:{
			App app = App.getInstance();
			User user = app.getMyself();
			emptyView.setText(String.format(getResources().getString(R.string.empty_search_by_department), user.school + User.getGradeFromYear(user.grade)));
			break;
		}
		case Const.CLASSMATES:
			App app = App.getInstance();
			User user = app.getMyself();
			emptyView.setText(String.format(getResources().getString(R.string.empty_search_by_department), User.getGradeFromYear(user.grade) + user.department + "班"));
			break;
		case Const.FOLLOWER:
			emptyView.setText(R.string.empty_search_by_follow);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
	

	
	class MyResultCallback implements ResultCallback{

		@Override
		public void onComplete() {
			listView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		}

		@Override
		public void onEmpty() {
			listView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}
}

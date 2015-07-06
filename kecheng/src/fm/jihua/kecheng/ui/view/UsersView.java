package fm.jihua.kecheng.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.commonsware.cwac.endless.LoadMoreAdapter;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.data.providers.AddStudentsProvider;
import fm.jihua.kecheng.data.providers.AddStudentsProvider.GetStudentsCallback;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.profile.ProfileActivity;
import fm.jihua.kecheng.ui.adapter.UserAdapter;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;

public class UsersView extends LinearLayout {
	
	ListView userListView;
	final List<User> users = new ArrayList<User>();
	LoadMoreUsersAdapter adapter;
	AddStudentsProvider dataProvider;
	Activity parent;
	int page = 1;
	Object param;
	String emptyDataMessage = "还没有人选这门课程";
	int courseId;
	
	public UsersView(Context context) {
		super(context);
		this.courseId = 0;
		initViews();
	}
	
	public UsersView(Context context, Object param) {
		super(context);
		this.courseId = 0;
		this.param = param;
		initViews();
	}

	public UsersView(Context context, int courseId) {
		super(context);
		this.courseId = courseId;
		initViews();
	}

	public UsersView(Context context, AttributeSet attrs, int courseId) {
		super(context, attrs);
		this.courseId = courseId;
		initViews();
	}
	
	public void init(int category){
		parent = (Activity)getContext();
		dataProvider = AddStudentsProvider.createByCategory(parent, category, courseId);
		dataProvider.setGetStudentsCallback(new MyGetStudentsCallback());
		if (category == Const.FRIENDS) {
			adapter.stopAppending();
		}
	}
		
	
	@TargetApi(16)
	void initViews(){
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		inflate(getContext(), R.layout.user_list, this);
		userListView = (ListView) findViewById(R.id.user_list);
		adapter = new LoadMoreUsersAdapter(getContext(), users);
		userListView.setAdapter(adapter);
		userListView.setOnItemClickListener(new MyOnItemClickListener());
		userListView.setDivider(null);
	}
	
	public void setData(List<User> users){
		setData(users, 0);
		if (users.size() < 30) {
			adapter.stopAppending();	
		}
	}

	public void setData(List<User> users, int position){
		this.users.clear();
		this.users.addAll(users);
		userListView.setSelection(position);
		adapter.notifyDataSetChanged();
		if (users.size() < 30) {
			adapter.stopAppending();	
		}
	}
	
	class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			User student = (User) listView.getAdapter().getItem(position);
			Intent intent = new Intent(getContext(), ProfileActivity.class);
			intent.putExtra("USER", student);
			getContext().startActivity(intent);
//			finish();
		}
		
	}
	
	class MyGetStudentsCallback implements GetStudentsCallback{

		@Override
		public void onError(final String notice) {
			((Activity)getContext()).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					UIUtil.unblock(parent);
					Hint.showTipsShort(parent, notice);
					adapter.onDataReady();
//					parent.finish();
				}
			});
		}

		@Override
		public void onComplete(final List<User> friends) {
			((Activity) getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					UIUtil.unblock((Activity) getContext());
					if (friends != null) {
						boolean anyMore = dataProvider
								.isAnyMore(friends.size());
						if (page == 1) {
							adapter.restartAppending();
							if (friends.size() == 0 && emptyDataMessage != null) {
								Hint.showTipsShort(getContext(),
										emptyDataMessage);
							}
						}
						if (!anyMore) {
							adapter.stopAppending();
						}
						// users.clear();
						users.addAll(friends);
						adapter.onDataReady();
					}
				}
			});
			
		}
	}
	
	class LoadMoreUsersAdapter extends LoadMoreAdapter {
		UserAdapter adapter;
	    LoadMoreUsersAdapter(Context context, List<User> users) {
	    	super(context, new UserAdapter(context, users), R.layout.pending_large, 
	    			R.layout.load_more_large);
	      adapter = (UserAdapter) getWrappedAdapter();
	      setRunInBackground(false);
	    }
	    
	    @Override
	    protected boolean cacheInBackground() throws Exception {
    		dataProvider.getUsers(param, ++page, dataProvider.getDefaultDataLimit());
    		return true;
	    }
	    
	    @Override
	    protected void appendCachedData() {
	    }
	  }
}

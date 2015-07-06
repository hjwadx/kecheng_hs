package fm.jihua.kecheng.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.commonsware.cwac.endless.LoadMoreAdapter;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.data.providers.AddFriendsProvider;
import fm.jihua.kecheng.data.providers.AddFriendsProvider.GetUsersCallback;
import fm.jihua.kecheng.interfaces.SimpleUser;
import fm.jihua.kecheng.rest.entities.SNSUsersResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.profile.ProfileActivity;
import fm.jihua.kecheng.ui.adapter.InviteFriendAdapter;
import fm.jihua.kecheng.ui.adapter.SNSFriendsAdapter;
import fm.jihua.kecheng.ui.adapter.UserAdapter;
import fm.jihua.kecheng.utils.Const;

public class AddFriendsListView extends ListView {
	Activity parent;
	final List<User> users = new ArrayList<User>();
	AddFriendsProvider dataProvider;
	LoadMoreAdapter adapter;
	int category;
	Object param;
	Object currentSearchParam;
	int page = 1;
	String emptyDataMessage;
	SNSUsersResult snsResult;
	final List<User> friends = new ArrayList<User>();
	final List<User> not_friends = new ArrayList<User>();
	final List<SimpleUser> sns_friends = new ArrayList<SimpleUser>();
	
	protected ResultCallback callback;
	
	public interface ResultCallback{
		public void onComplete();
		public void onEmpty();
	}
	
	public void setResultCallback(ResultCallback callback){
		this.callback = callback;
	}

	public AddFriendsListView(Context context) {
		super(context);
	}

	public AddFriendsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(int category){
		this.category = category;
		parent = (Activity)getContext();
		dataProvider = AddFriendsProvider.createByCategory(parent, category);
		if (category == Const.FOLLOWER) {
			adapter = new LoadMoreUsersByFollowersAdapter(getContext(), users);
		} else {
			adapter = new LoadMoreUsersAdapter(parent, users, category);
		}
		if (category == Const.WEIBO || category == Const.RENREN) {
			adapter = new LoadMoreUsersBySNSAdapter(parent,  friends,  not_friends,  sns_friends, category);
		}
		dataProvider.setGetUsersCallback(new MyGetUsersCallback());
		setSelector(android.R.color.transparent);
		setAdapter(adapter);
		setDivider(null);
		setBackgroundResource(R.color.app_background);
		setOnItemClickListener(new MyOnItemClickListener());
	}

	public void showUsers(Object param){
		showUsers(param, false);
	}

	public void setEmptyDataMessage(String message){
		this.emptyDataMessage = message;
	}

	public void showUsers(Object param, boolean loading){
		page = 1;
		users.clear();
		adapter.restartAppending();
		if (loading) {
			adapter.setLoadingAtFirst();
			adapter.stopAppending();
		}
		adapter.notifyDataSetChanged();
		showUsers(param, page, dataProvider.getDefaultDataLimit());
	}

	private void showUsers(Object param, int page, int limit){
		this.param = param;
		this.page = page;
		dataProvider.getUsers(param, page, limit);
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

	class MyGetUsersCallback implements GetUsersCallback{

		@Override
		public void onComplete(final User[] friends) {
			((Activity)getContext()).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					UIUtil.unblock((Activity)getContext());
					if (friends != null) {
						boolean anyMore = dataProvider.isAnyMore(friends.length);
						if (page == 1) {
							adapter.restartAppending();
							if (friends.length == 0 && callback != null) {
								callback.onEmpty();
//								Hint.showTipsShort(getContext(), emptyDataMessage);
							} else if(callback != null){
								callback.onComplete();
							}
						}
						if(!anyMore){
							adapter.stopAppending();
						}
//						users.clear();
						users.addAll(Arrays.asList(friends));
						adapter.onDataReady();
					}
				}
			});
		}

		@Override
		public void onError(final String notice) {
			((Activity)getContext()).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					UIUtil.unblock(parent);
					if(callback != null){
						callback.onEmpty();
					}
//					Hint.showTipsShort(parent, notice);
					adapter.onDataReady();
//					parent.finish();
				}
			});
		}

		@Override
		public void onSNSComplete(final SNSUsersResult result) {
			((Activity)getContext()).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (page == 1) {
						snsResult = result;
						friends.addAll(Arrays.asList(snsResult.friends));
						not_friends.addAll(Arrays.asList(snsResult.not_friends));
						sns_friends.addAll(snsResult.sns_friends);
					}
//					friends.clear();
//					not_friends.clear();
//					sns_friends.clear();
//					int remain = 10*page;
//					if (Arrays.asList(snsResult.not_friends).size() <= remain) {
//						not_friends.addAll(Arrays.asList(snsResult.not_friends));
//						remain -= Arrays.asList(snsResult.not_friends).size();
//					} else {
//						not_friends.addAll(Arrays.asList(snsResult.not_friends).subList(0, remain));
//						remain = 0;
//					}
//					if (remain > 0) {
//						if (snsResult.sns_friends.size() <= remain) {
//							sns_friends.addAll(snsResult.sns_friends);
//							remain -= snsResult.sns_friends.size();
//						} else {
//							sns_friends.addAll(snsResult.sns_friends.subList(0, remain));
//							remain = 0;
//						}
//					}
//					if (remain > 0) {
//						if (Arrays.asList(snsResult.friends).size() <= remain) {
//							friends.addAll(Arrays.asList(snsResult.friends));
//							remain -= Arrays.asList(snsResult.friends).size();
//						} else {
//							friends.addAll(Arrays.asList(snsResult.friends).subList(0, remain));
//							remain = 0;
//						}
//					}
					post(new Runnable() {
						
						@Override
						public void run() {
							if(page != 1){
								SNSFriendsAdapter snsAdapter = ((LoadMoreUsersBySNSAdapter) adapter).snsAdapter;
								snsAdapter.setCount();
							}
							if (snsResult.sns_friends.size() + Arrays.asList(snsResult.not_friends).size() + Arrays.asList(snsResult.friends).size() < Const.DATA_COUNT_PER_REQUEST*page){
								adapter.stopAppending();
							} else {
								adapter.restartAppending();
							}
							UIUtil.unblock((Activity)getContext());
							adapter.onDataReady();
						}
					});
				}
			});
		}
	}

	class LoadMoreUsersAdapter extends LoadMoreAdapter {
		InviteFriendAdapter adapter;
	    LoadMoreUsersAdapter(Activity context, List<User> users, int category) {
	    	super(context, new InviteFriendAdapter(context, users, category), R.layout.pending_large,
	    			R.layout.load_more_large);
	      adapter = (InviteFriendAdapter) getWrappedAdapter();
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

	class LoadMoreUsersByFollowersAdapter extends LoadMoreAdapter {
		UserAdapter adapter;
		LoadMoreUsersByFollowersAdapter(Context context, List<User> users) {
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

	class LoadMoreUsersBySNSAdapter extends LoadMoreAdapter {
		public SNSFriendsAdapter snsAdapter;
		LoadMoreUsersBySNSAdapter(Activity context,  List<User> friends,  List<User> not_friends, List<SimpleUser> sns_friends,int category) {
	    	super(context, new SNSFriendsAdapter(context, friends,  not_friends, sns_friends, category), R.layout.pending_large,
	    			R.layout.load_more_large);
	    	snsAdapter = (SNSFriendsAdapter) getWrappedAdapter();
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

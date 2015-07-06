package fm.jihua.kecheng.data.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Message;
import fm.jihua.kecheng.data.providers.AddFriendsProvider;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.interfaces.SimpleUser;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.SNSUsersResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

public class AddFriendsBySNS extends AddFriendsProvider {
	
	AuthHelper authHelper;
	DataAdapter mDataAdapter;
	int limit;
	boolean snsAnyMore = true;
	List<SimpleUser> friends;

	public AddFriendsBySNS(Activity parent, int category){
		super(parent, category);
		init();
	}
	
	@Override
	public void getUsers(Object param, int page, int limit) {
		this.limit = limit; 
		if (page > 1){
			SNSUsersResult result1 = new SNSUsersResult();
			callback.onSNSComplete(result1);
			return;
		}
		authHelper.getAllFriends(new MySNSCallback(SNSCallback.GET_ALL_FRIENDS_INFO));
	}
	
	public int getDefaultDataLimit(){
		return category == Const.WEIBO ? Const.WEIBO_COUNT_PER_REQUEST : Const.RENREN_COUNT_PER_REQUEST;
	}
	
	public boolean isAnyMore(int count){
		return snsAnyMore && count > 0;
	}
	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
		authHelper = category == Const.RENREN ? new RenrenAuthHelper(mActivity) : new WeiboAuthHelper(mActivity);
	}

	class MyDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_GRAPH:
				SNSUsersResult result = (SNSUsersResult)msg.obj;
				if (result != null && result.success) {
//					callback.onComplete(result.not_friends);
					List<String> idsList = Arrays.asList(result.invite_third_part_ids);
					List<SimpleUser> delList = new ArrayList<SimpleUser>();
					for (SimpleUser user : friends){
						if(!idsList.contains(user.id)){
							delList.add(user);
						}
					}
					friends.removeAll(delList);
					result.sns_friends = friends;
					callback.onSNSComplete(result);
				}else {
					callback.onError("获取好友信息失败");
				}
				break;
			default:
				break;
			}
		}
	}
	
    private class MySNSCallback implements SNSCallback{
		
		private int mScope = 0;
		
		public MySNSCallback(int scope) {
            mScope = scope;
        }

		@Override
		public void onComplete(AuthHelper authHelper, Object data) {
			switch (mScope){
			case SNSCallback.GET_ALL_FRIENDS_INFO:
				friends = (List<SimpleUser>)data;
				snsAnyMore = friends.size() >= limit;
				List<String> renrenIds = new ArrayList<String>();
				for (SimpleUser simpleUser : friends) {
					renrenIds.add(String.valueOf(simpleUser.id));
				}
				if (renrenIds.size() > 0) {
					mDataAdapter.getGraph(renrenIds, category, 1, limit);
				} else {
					callback.onComplete(new User[] {});
				}
				break;
			}
		}

		@Override
		public void onError(AuthHelper authHelper) {
			switch (mScope){
			case SNSCallback.GET_ALL_FRIENDS_INFO:
				callback.onError("获取好友信息失败");
				break;
			}
			
		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}
}

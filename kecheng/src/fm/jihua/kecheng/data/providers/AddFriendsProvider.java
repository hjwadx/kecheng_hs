package fm.jihua.kecheng.data.providers;

import android.app.Activity;
import fm.jihua.kecheng.data.adapters.AddFriendsByClassmates;
import fm.jihua.kecheng.data.adapters.AddFriendsByFollowers;
import fm.jihua.kecheng.data.adapters.AddFriendsBySNS;
import fm.jihua.kecheng.data.adapters.AddFriendsBySearch;
import fm.jihua.kecheng.data.adapters.AddFriendsBySearchGeziId;
import fm.jihua.kecheng.rest.entities.SNSUsersResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.utils.Const;

public abstract class AddFriendsProvider {
	protected Activity mActivity;
	protected int category;
	protected GetUsersCallback callback;

	public AddFriendsProvider(Activity parent, int category){
		mActivity = parent;
		this.category = category;
	}
	
	public interface GetUsersCallback{
		public void onComplete(User[] friends);
		public void onSNSComplete(SNSUsersResult result);
		public void onError(String notice);
	}
	
	public void setGetUsersCallback(GetUsersCallback callback){
		this.callback = callback;
	}
	
	public int getDefaultDataLimit(){
		return Const.DATA_COUNT_PER_REQUEST;
	}
	
	public boolean isAnyMore(int count){
		return count >= getDefaultDataLimit();
	}
	
	public void getUsers(Object param){
		getUsers(param, 1, getDefaultDataLimit());
	}
	public abstract void getUsers(Object param, int offset, int limit);
	
	public static AddFriendsProvider createByCategory(Activity parent, int category){
		switch (category) {
		case Const.SEARCH:
			return new AddFriendsBySearch(parent, category);
		case Const.SEARCH_BY_GEZI_ID:
			return new AddFriendsBySearchGeziId(parent, category);
		case Const.RENREN:
		case Const.WEIBO:
			return new AddFriendsBySNS(parent, category);
		case Const.CLASSMATES_SCHOOL:
		case Const.CLASSMATES_GRADE:
		case Const.CLASSMATES:
			return new AddFriendsByClassmates(parent, category);
		case Const.FOLLOWER:
			return new AddFriendsByFollowers(parent, category);
		default:
			break;
		}
		return null;
	}
}

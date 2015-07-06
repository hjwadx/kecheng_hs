package fm.jihua.kecheng.data.adapters;


import android.app.Activity;
import android.os.Message;
import fm.jihua.kecheng.data.providers.AddFriendsProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.service.DataAdapter;

public class AddFriendsByFollowers extends AddFriendsProvider{

	DataAdapter mDataAdapter;

	public AddFriendsByFollowers(Activity parent, int category) {
		super(parent, category);
		init();
	}

	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
	}

	@Override
	public void getUsers(Object param, int offset, int limit) {
		// TODO Auto-generated method stub
		mDataAdapter.getFollowers(offset, limit);	
	}
	
	class MyDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_FOLLOWERS:
				if (msg.obj != null) {
					UsersResult result = (UsersResult) msg.obj;
					if (result.success) {
						callback.onComplete(result.users);
					} else {
						callback.onError("获取好友信息失败");
					}
				}
				
				break;
			default:
				break;
			}
		}
	}

}

package fm.jihua.kecheng.data.adapters;

import android.app.Activity;
import android.os.Message;
import fm.jihua.kecheng.data.providers.AddFriendsProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UserResult;
import fm.jihua.kecheng.rest.service.DataAdapter;

public class AddFriendsBySearchGeziId extends AddFriendsProvider {
	
	DataAdapter mDataAdapter;

	public AddFriendsBySearchGeziId(Activity parent, int category){
		super(parent, category);
		init();
	}
	
	@Override
	public void getUsers(Object param, int page, int limit) {
		mDataAdapter.searchUserByGeziId(param.toString());
	}
	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
	}

	class MyDataCallback implements DataCallback{
		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_SEARCH_USER_BY_GEZI_ID:
				UserResult result = (UserResult)msg.obj;
				if (result != null) {
					if (result.success) {
						User[] users = new User[1];
						users[0] = result.user;
						callback.onComplete(users);
					}else {
						callback.onError("查无此人，请检查你输入的名字/格子号。");
					}
				}else {
					callback.onError("查找用户失败");
				}
				break;
			default:
				break;
			}
		}
	}
}

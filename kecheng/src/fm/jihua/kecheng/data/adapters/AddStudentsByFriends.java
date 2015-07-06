package fm.jihua.kecheng.data.adapters;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.os.Message;
import fm.jihua.kecheng.data.providers.AddStudentsProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.service.DataAdapter;

public class AddStudentsByFriends extends AddStudentsProvider{
	
	DataAdapter mDataAdapter;

	public AddStudentsByFriends(Activity parent, int category) {
		super(parent, category);
		init();
	}
	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
	}

	@Override
	public void getUsers(Object param, int offset, int limit) {
		mDataAdapter.getFriends(offset, limit, true);	
	}
	
	class MyDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_FRIENDS:
				if (msg.obj != null) {
					UsersResult result = (UsersResult) msg.obj;
					if (result.success) {
						ArrayList<User> friends = new ArrayList<User>();
						friends.addAll(Arrays.asList(result.users));
						callback.onComplete(friends);
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

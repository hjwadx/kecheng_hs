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

public class AddStudentsByFreeTimeStudents extends AddStudentsProvider{
	
	DataAdapter mDataAdapter;

	public AddStudentsByFreeTimeStudents(Activity parent, int category) {
		super(parent, category);
		init();
	}
	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
	}

	@Override
	public void getUsers(Object param, int offset, int limit) {
		int[] a = new int[2]; 
		a = (int[])param;
		mDataAdapter.getFreeTimeFriends(a[1], a[1], offset, limit);
	}
	
	class MyDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_FREE_TIME_FRIENDS:
				UsersResult result = (UsersResult)msg.obj;
				if (result != null && result.success) {
					if (result.users != null) {
						ArrayList<User> users = new ArrayList<User>(); 
						users.addAll(Arrays.asList(result.users));
						callback.onComplete(users);
					}		
				}else {
					callback.onError("获取空闲好友信息失败");
				}
				break;
			default:
				break;
			}
		}
	}

}

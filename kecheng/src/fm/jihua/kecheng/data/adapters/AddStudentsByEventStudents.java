package fm.jihua.kecheng.data.adapters;

import java.util.Arrays;

import android.app.Activity;
import android.os.Message;
import fm.jihua.kecheng.data.providers.AddStudentsProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.service.DataAdapter;

public class AddStudentsByEventStudents extends AddStudentsProvider{
	DataAdapter mDataAdapter;
	int eventId;

	public AddStudentsByEventStudents(Activity parent, int category, int eventId) {
		super(parent, category);
		this.eventId = eventId;
		init();
	}
	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
	}

	@Override
	public void getUsers(Object param, int offset, int limit) {
		mDataAdapter.getEventUsers(eventId, offset, limit);
	}
	
	class MyDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_EVENT_USERS:
				@SuppressWarnings("unchecked")
				UsersResult result = (UsersResult) msg.obj;
				if (result == null || !result.success) {
					callback.onError("获取好友信息失败");
				} else {
					callback.onComplete(Arrays.asList(result.users));
				}
				break;
			default:
				break;
			}
		}
	}

}

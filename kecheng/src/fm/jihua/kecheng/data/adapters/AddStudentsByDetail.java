package fm.jihua.kecheng.data.adapters;

import java.util.List;

import android.app.Activity;
import android.os.Message;
import fm.jihua.kecheng.data.providers.AddStudentsProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;

public class AddStudentsByDetail extends AddStudentsProvider{
	DataAdapter mDataAdapter;
	int courseId;

	public AddStudentsByDetail(Activity parent, int category, int courseId) {
		super(parent, category);
		this.courseId = courseId;
		init();
	}
	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
	}

	@Override
	public void getUsers(Object param, int offset, int limit) {
		mDataAdapter.getStudents(courseId, offset, limit);
	}
	
	class MyDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_STUDENTS:
				@SuppressWarnings("unchecked")
				List<User> users = (List<User>)msg.obj;
				if (users != null) {
					callback.onComplete(users);
				}else {
					callback.onError("获取好友信息失败");
				}
				break;
			default:
				break;
			}
		}
	}

}

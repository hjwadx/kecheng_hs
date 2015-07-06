package fm.jihua.kecheng.data.adapters;

import android.app.Activity;
import android.os.Message;
import fm.jihua.kecheng.data.providers.AddFriendsProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.utils.Const;

public class AddFriendsByClassmates extends AddFriendsProvider {
	
	DataAdapter mDataAdapter;
	public static final int CLASSMATES_SAME_CLASS = 0;
	public static final int CLASSMATES_SAME_SCHOOL = 1;
	public static final int CLASSMATES_SAME_GRADE = 2;
	int type;

	public AddFriendsByClassmates(Activity parent, int category){
		super(parent, category);
		init();
	}
	
	public void getUsers(Object param, int offset, int limit){
		mDataAdapter.getClassmates(offset, limit, type);
	}
	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
		switch (category) {
		case Const.CLASSMATES_SCHOOL:
			type = CLASSMATES_SAME_SCHOOL;
			break;
		case Const.CLASSMATES_GRADE:
			type = CLASSMATES_SAME_GRADE;
			break;
		case Const.CLASSMATES:
			type = CLASSMATES_SAME_CLASS;
			break;
		default:
			break;
		}
	}

	class MyDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_CLASSMATES:
				UsersResult result = (UsersResult)msg.obj;
				if (result != null && result.success) {
					callback.onComplete(result.users);
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

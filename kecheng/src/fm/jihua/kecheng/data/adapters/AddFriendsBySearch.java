package fm.jihua.kecheng.data.adapters;

import android.app.Activity;
import android.os.Message;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.data.providers.AddFriendsProvider;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.entities.WrappedResult;
import fm.jihua.kecheng.rest.service.DataAdapter;

public class AddFriendsBySearch extends AddFriendsProvider {
	
	DataAdapter mDataAdapter;
	Object currentParam;

	public AddFriendsBySearch(Activity parent, int category){
		super(parent, category);
		init();
	}
	
	@Override
	public void getUsers(Object param, int page, int limit) {
		currentParam = param;
		mDataAdapter.searchUsers(param.toString(), page, limit);
	}
	
	void init(){
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
	}
	
	public boolean isAnyMore(int count){
		return false;
	}

	class MyDataCallback implements DataCallback{
		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_SEARCH_USERS:
				WrappedResult wrappedResult = (WrappedResult)msg.obj;
				if (wrappedResult != null) {
					UsersResult result = (UsersResult)wrappedResult.result;
					if (ObjectUtils.nullSafeEquals(currentParam, wrappedResult.param)) {
						if (result != null && result.success) {
							callback.onComplete(result.users);
						}else {
							callback.onError("查找用户失败");
						}
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

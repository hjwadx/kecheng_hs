package fm.jihua.kecheng.data.providers;

import java.util.List;

import android.app.Activity;
import fm.jihua.kecheng.data.adapters.AddStudentsByDetail;
import fm.jihua.kecheng.data.adapters.AddStudentsByEventStudents;
import fm.jihua.kecheng.data.adapters.AddStudentsByFreeTimeStudents;
import fm.jihua.kecheng.data.adapters.AddStudentsByFriends;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.utils.Const;

public abstract class AddStudentsProvider {
	
	protected Activity mActivity;
	protected int category;
	protected GetStudentsCallback callback;

	public AddStudentsProvider(Activity parent, int category){
		mActivity = parent;
		this.category = category;
	}
	
	public interface GetStudentsCallback{
		public void onComplete(List<User> friends);
		public void onError(String notice);
	}
	
	public void setGetStudentsCallback(GetStudentsCallback callback){
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
	
	public static AddStudentsProvider createByCategory(Activity parent, int category,int courseId){
		switch (category) {
		case Const.CLASSMATES:
			return new AddStudentsByDetail(parent, category, courseId);
		case Const.FRIENDS:
			return new AddStudentsByFriends(parent, category);
		case Const.FREETIME_FRIENDS:
			return new AddStudentsByFreeTimeStudents(parent, category);
		case Const.EVENT_FRIENDS:
			return new AddStudentsByEventStudents(parent, category, courseId);
		default:
			break;
		}
		return null;
	}

}

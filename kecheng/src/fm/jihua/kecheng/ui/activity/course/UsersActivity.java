package fm.jihua.kecheng.ui.activity.course;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.view.NoFriendsView;
import fm.jihua.kecheng.ui.view.UsersView;
import fm.jihua.kecheng.utils.Const;

public class UsersActivity extends BaseActivity {
	
	ListView userListView;
	UsersView usersView;
	public static final String UPDATE_USERS_ACTION = "fm.jihua.kecheng_hs.UPDATE_USERS";
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			List<User> users = (List<User>) getIntent().getSerializableExtra("students");
			if (users.size() == 0) {
				setContentView(new NoFriendsView(this));
//				startActivityForResult(new Intent(this, NoFriendsActivity.class), Const.WEB_VIEWER_WITH_RESULT);
			}else {
				switch (getIntent().getIntExtra("getFriendsBy", 0)) {
				case Const.FREETIME_FRIENDS:
					int []a = new int[2];
					a[0] = getIntent().getIntExtra("TIME_SLOT", 0);
					a[1] = getIntent().getIntExtra("DAY_OF_WEEK", 0);
					usersView = new UsersView(this,a);
					usersView.init(Const.FREETIME_FRIENDS);
					break;
				case Const.FRIENDS:
					usersView = new UsersView(this, ((Course)getIntent().getSerializableExtra("course")).id);
					usersView.init(Const.CLASSMATES);
					break;
				case Const.EVENT_FRIENDS:
					usersView = new UsersView(this, ((Event)getIntent().getSerializableExtra("event")).id);
					usersView.init(Const.EVENT_FRIENDS);
					break;
				default:
					break;
				}
				usersView.setData(users, getIntent().getIntExtra("position", 0));
				setContentView(usersView);
			}
			initTitlebar();
		}catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, "Entry onCreate Exception:" + e.getMessage());
		}
	}
	
	void initTitlebar(){
		String title = getIntent().getStringExtra("title");
		title = title == null ? "同学列表" : title;
		setTitle(title);
	}
	
	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
}

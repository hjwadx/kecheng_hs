package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.view.NoFriendsView;
import fm.jihua.kecheng.ui.view.UsersView;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.Const;

@SuppressLint("CommitPrefEdits")
public class FriendsFragment extends BaseFragment {

	DataAdapter mAdapter;
	NoFriendsView noFriendsView;
	UsersView usersView;
	boolean firstEnter = true;
	int page = 1;
	List<User> mUsers;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		usersView = new UsersView(getActivity());
		usersView.init(Const.FRIENDS);
		noFriendsView = new NoFriendsView(getActivity());
		mAdapter = new DataAdapter(getActivity(), new MyDataCallback());
		initTitlebar();
		AppLogger.d("on createView");
		return usersView;
	}

	void initFriends(){
//		mAdapter.getFriends(page, Const.DATA_COUNT_PER_REQUEST);
		mAdapter.getFriends(false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initFriends();
		AppLogger.d("on view created");
	}
	
	@Override
	public void onShow(){
		super.onShow();
		MobclickAgent.onEvent(getActivity(), "enter_friends_view");
		refreshFriends();
	}
	
	void refreshFriends(){
		List<User> users = App.getInstance().getDBHelper().getFriends(App.getInstance().getUserDB());
		if (!ObjectUtils.nullSafeEquals(users, mUsers)) {
			mUsers = users;
			refreshViews(mUsers);
		}
	}
	
	void refreshViews(List<User> users){
		View view = null;
		if (users == null || users.size() == 0) {
			if(noFriendsView == null){
				noFriendsView = new NoFriendsView(getActivity());
			}
			view = noFriendsView;
			if (firstEnter) {
				noFriendsView.startInviteActivity();
				firstEnter = false;
			}
		}else {
			if(usersView == null){
				usersView = new UsersView(getActivity());
				usersView.init(Const.FRIENDS);
			}
			usersView.setData(users);
			view = usersView;
		}
		setContentView(view);
	}
	
	public void initTitlebar(){
		MenuActivity activity = (MenuActivity) getActivity();
		activity.setTitle("我的好友");
		activity.getKechengActionBar().getRightButton().setVisibility(View.VISIBLE);
		activity.getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_addfriend);
		activity.getKechengActionBar().getRightButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				noFriendsView.startInviteActivity();
			}
		});
	}
	
	boolean isFirstLoad(){
		App app = (App)getApplication();
		return app.getDefaultPreferences().getBoolean("first_load_friends", true);
	}
	
	void setFirstLoad(){
		App app = (App)getApplication();
		Editor editor = app.getDefaultPreferences().edit();
		editor.putBoolean("first_load_friends", false);
		App.commitEditor(editor);
	}
	
	class MyDataCallback implements DataCallback{
		@Override
		public void callback(android.os.Message msg) {
			UIUtil.unblock(parent);
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_FRIENDS:
				if (msg.obj != null) {
					UsersResult result = (UsersResult) msg.obj;
					if (result.success) {
						ArrayList<User> friends = new ArrayList<User>();
						friends.addAll(Arrays.asList(result.users));
						if (!result.finished && friends.size() == 0
								&& isFirstLoad()) {
							UIUtil.block(parent);
							setFirstLoad();
						} else {
							mUsers = friends;
							App.getInstance().getDBHelper().saveFriends(App.getInstance().getUserDB(), mUsers);
							refreshViews(mUsers);
						}
					}
				}
				break;

			default:
				break;
			}
		}
	}
}

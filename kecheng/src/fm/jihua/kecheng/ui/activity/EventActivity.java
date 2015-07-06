package fm.jihua.kecheng.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.TextView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.course.UsersActivity;
import fm.jihua.kecheng.ui.activity.profile.ProfileActivity;
import fm.jihua.kecheng.ui.helper.DialogUtils;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.AlarmManagerUtil;
import fm.jihua.kecheng.utils.Const;

public class EventActivity extends BaseActivity {
	
	private DataAdapter mDataAdapter;
	Event event;
	boolean changed;
	final int PAGE = 1;
	List<User> mUsers = new ArrayList<User>();
	List<CachedImageView> avatarsList = new ArrayList<CachedImageView>() ;
	CachedImageView poster;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event);
		MobclickAgent.onEvent(this, "action_detail_activity");
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		event = (Event) getIntent().getSerializableExtra("EVENT");
		mDataAdapter.getEventUsers(event.id, PAGE, Const.DATA_COUNT_PER_REQUEST);
		initTitlebar();
		findViews();
		initView();
	}

	void findViews() {
		avatarsList.add((CachedImageView)findViewById(R.id.participant0));
		avatarsList.add((CachedImageView)findViewById(R.id.participant1));
		avatarsList.add((CachedImageView)findViewById(R.id.participant2));
		avatarsList.add((CachedImageView)findViewById(R.id.participant3));
		poster = (CachedImageView) findViewById(R.id.poster);
	}

	void initView() {
		((ViewStub)findViewById(R.id.view_stub_add_event)).inflate();
		initRootButton();
		((TextView)findViewById(R.id.name)).setText(event.name);
		((TextView)findViewById(R.id.date)).setText("时间：" + event.getTimeString());
		((TextView)findViewById(R.id.locale)).setText("地点：" + event.getLocalString());
		((TextView)findViewById(R.id.initiator)).setText("发起人：" + event.organizer_name);
		((TextView)findViewById(R.id.introduction)).setText(event.content);
		poster.setImageURI(Uri.parse(event.poster_url));
	}
	
	void initRootButton() {
		if(App.getInstance().getDBHelper().getEvent(App.getInstance().getUserDB(), event.id) == null){
			findViewById(R.id.btn_add_event_parent).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_delete_event_parent).setVisibility(View.GONE);
			findViewById(R.id.btn_overdue_event_parent).setVisibility(View.GONE);
		} else {
			findViewById(R.id.btn_add_event_parent).setVisibility(View.GONE);
			if(event.isOverdue()){
				findViewById(R.id.btn_overdue_event_parent).setVisibility(View.VISIBLE);
				findViewById(R.id.btn_delete_event_parent).setVisibility(View.GONE);
			} else{
				findViewById(R.id.btn_delete_event_parent).setVisibility(View.VISIBLE);
				findViewById(R.id.btn_overdue_event_parent).setVisibility(View.GONE);
			}
		}
	}

	void initTitlebar() {
		setTitle("活动详情");
		getKechengActionBar().getActionButton().setVisibility(View.VISIBLE);
		getKechengActionBar().setRightImage(R.drawable.activity_btn_icon_share);
		getKechengActionBar().getRightButton().setOnClickListener(titleRightOnClickListener);
	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.participant_more:
			if (mUsers != null && mUsers.size() > 0) {
				Intent intent = new Intent();
				intent.putExtra("students", new ArrayList<User>(mUsers));
				intent.putExtra("event", event);
				intent.putExtra("getFriendsBy", Const.EVENT_FRIENDS);
				intent.putExtra("title", "共同参加");
				intent.setClass(EventActivity.this, UsersActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.participant0:
		case R.id.participant1:
		case R.id.participant2:
		case R.id.participant3:
			if(mUsers != null && avatarsList.indexOf(view) < mUsers.size()){
				startProfileActivity(mUsers.get(avatarsList.indexOf(view)));
			}
			break;
		case R.id.btn_delete_event:
			UIUtil.block(this);
			mDataAdapter.deleteEvent(event.id);
			break;
		case R.id.btn_add_event:
			UIUtil.block(this);
			MobclickAgent.onEvent(this, "action_detail_activity");
			mDataAdapter.addEvent(event.id);
			checkFirstJoinEvent();
			break;
		}
    }
	
	private void checkFirstJoinEvent(){
		if(App.getInstance().checkIsFirstStatus(Const.FIRST_EVENTDIALOG) && App.getInstance().checkIsFirstStatus(Const.FIRST_ENTER2TIMEMODE)){
			String string = String.format(getString(R.string.message_timemode), "活动");
			new AlertDialog.Builder(this).setTitle("格子提醒").setMessage(string).setNegativeButton("我知道了", null).create().show();
			App.getInstance().cancleFirstStatus(Const.FIRST_EVENTDIALOG);
		}
	}
	
	OnClickListener titleRightOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			String string = String.format(getString(R.string.sharetext_event), event.name);
			//null --> no image
			if(TextUtils.isEmpty(event.poster_url)){
				DialogUtils.showShareAppDialog(EventActivity.this, getString(R.string.share_title_normal), string, "activity");
			}else{
				View view = DialogUtils.getViewByImageViewResource(poster, EventActivity.this);
				DialogUtils.showShareExaminationsDialogWithOutImageTitle(EventActivity.this, view, string, "activity");
			}
		}
	};
	
	private void startProfileActivity(User user) {
		Intent intent = new Intent(EventActivity.this, ProfileActivity.class);
		intent.putExtra("USER", user);
		startActivity(intent);
	}
	
	@Override
	public void finish() {
		Log.d(Const.TAG, "exit event");
		if (changed) {
			setResult(RESULT_OK);
		}
		super.finish();
	}
	
	private void refreshStudents(){
		for (int i = 0; i < avatarsList.size();i++) {
    		avatarsList.get(i).setFadeIn(false);
			if (i < mUsers.size()) {
				avatarsList.get(i).setImageURI(Uri.parse(mUsers.get(i).tiny_avatar_url));
			} else {
				avatarsList.get(i).setImageBitmap(null);
			}
		}
	}
	
	
	private class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(EventActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_ADD_EVENT: {
				BaseResult result = (BaseResult) msg.obj;
				if (result == null || !result.success) {
					Hint.showTipsShort(EventActivity.this, "参加活动失败");
				} else {
					mUsers.add(App.getInstance().getMyself());
					refreshStudents();
					AlarmManagerUtil.getInstance().startAlarmByEvent(event);
					changed = true;
					initRootButton();
				}
			}
				break;
			case DataAdapter.MESSAGE_DELETE_EVENT: {
				BaseResult result = (BaseResult) msg.obj;
				if (result == null || !result.success) {
					Hint.showTipsShort(EventActivity.this, "取消参加活动失败");
				} else {
					User myself = App.getInstance().getMyself();
					for(User user : mUsers){
						if(user.id == myself.id){
							mUsers.remove(user);
							break;
						}
					}
					refreshStudents();
					AlarmManagerUtil.getInstance().cancleAlarmByEvent(event);
					changed = true;
					initRootButton();
				}
			}
				break;
			case DataAdapter.MESSAGE_GET_EVENT_USERS: {
				UsersResult result = (UsersResult) msg.obj;
				if (result == null || !result.success) {
					if(result == null || result.finished){
						Hint.showTipsShort(EventActivity.this, "获取参与同学失败");
					}
				} else {
					if(result.users != null){
						mUsers.clear();
						mUsers.addAll(Arrays.asList(result.users));
						refreshStudents();
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

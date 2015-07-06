package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.chat.service.Message;
import fm.jihua.chat.utils.DatabaseHelper;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.ContactItem;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.message.ChatActivity;
import fm.jihua.kecheng.ui.view.BiasHintView;
import fm.jihua.kecheng.ui.view.ContactsView;
import fm.jihua.kecheng.utils.Const;

public class ActivitiesFragment extends BaseFragment {

	List<ContactItem> mChatContactItems;
	ContactsView contactsView;
	ListView listView;
	BiasHintView emptyView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contactsView = new ContactsView(parent);
		return contactsView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = (ListView) view.findViewById(R.id.user_list);
		emptyView = (BiasHintView) view.findViewById(R.id.empty_with_image);
		emptyView.setText(R.string.empty_messages_list);
		listView.setOnItemLongClickListener(longClickListener);
		initViews();
		if (parent.getIntent() != null) {
			Intent intent = parent.getIntent();
			if (intent.getBooleanExtra(Const.BUNDLE_KEY_TO_CHAT, false)) {
				intent.getExtras().remove(Const.BUNDLE_KEY_TO_CHAT);
				intent.setClass(parent, ChatActivity.class);
				startActivity(intent);
			}
		}
	}
	
	@Override
	public void onShow() {
		super.onShow();
		MobclickAgent.onEvent(parent, "enter_message_view");
		initViews();
	}
	
	public void refresh(){
		initViews();
	}

	void initViews(){
		App app = (App) getApplication();
		DatabaseHelper dbHelper = app.getChatDBHelper();
		List<Message> messagesList = dbHelper.getThreads(dbHelper.getWritableDatabase(), app.getMyUserId(), true, 100, 0);
		mChatContactItems = changeMessageToContactItem(messagesList);
		refreshViews(mChatContactItems);
//		mAdapter = new DataAdapter(this, new MyDataCallback());
//		mAdapter.getFriends();
	}
	
	OnItemLongClickListener longClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> listView, View view,
				int position, long itemId) {
			if (position >= 0) {
				ContactItem item = mChatContactItems.get(position);
				showDeleteDialog(item);
				return true;
			}
			return false;
		}
	};
	
	private void showDeleteDialog(final ContactItem item){
		new AlertDialog.Builder(parent)
		.setTitle("提示")
		.setMessage("是否删除" + item.name + "的聊天记录")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						App app = (App)getApplication();
						app.getChatDBHelper().deleteThread(app.getChatDBHelper().getWritableDatabase(), item.getJID());
						initViews();
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						// 按钮事件
						dialoginterface.dismiss();
					}
				}).show();
	}
	
	public void initTitlebar(){
		parent.setTitle(R.string.act_message_center_title);
		MenuActivity activity =(MenuActivity) getActivity();
		activity.getKechengActionBar().getRightButton().setVisibility(View.GONE);
//		((Button)findViewById(R.id.action)).setText("");
//		((Button)findViewById(R.id.action)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_invitation, 0, 0, 0);
	}
	
	void refreshViews(List<ContactItem> users){
		if(contactsView == null){
			contactsView = new ContactsView(parent);
		}		
		contactsView.setData(users);
		if (users == null || users.size() < 1) {
			emptyView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			emptyView.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
		setContentView(contactsView);
	}
	
	List<ContactItem> changeMessageToContactItem(List<Message> messageList){
		App app = (App)getApplication();
		List<ContactItem> items = new ArrayList<ContactItem>();
		for (Message message : messageList) {
			String jid = message.getThread();
			User user = User.fromJID(jid);
			User existUser = app.getDBHelper().getUser(app.getUserDB(), user.id);
			if (existUser != null) {
				user = existUser;
			}
			int unreadCount = app.getChatDBHelper().getUnreadCount(app.getChatDBHelper().getWritableDatabase(), jid);
			items.add(new ContactItem(message, unreadCount, user));
		}
		return items;
	}
	
	List<ContactItem> mergeWithFriends(List<ContactItem> items, List<User> friends){
		for (User user : friends) {
			if (!items.contains(user)) {
				items.add(new ContactItem(user));
			}
		}
		return items;
	}
	
	class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			ContactItem item = (ContactItem) listView.getAdapter().getItem(position);
			Intent intent = new Intent(parent, ChatActivity.class);
			intent.setData(item.getJabberUri());
			startActivity(intent);
		}
	}
}

package fm.jihua.kecheng.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import fm.jihua.common.utils.CommonUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.ContactItem;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UserDetailsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.message.ChatActivity;
import fm.jihua.kecheng.ui.adapter.ContactAdapter;

public class ContactsView extends LinearLayout {
	
	ListView userListView;
	DataAdapter dataAdapter;
	ContactAdapter contactAdapter;
	final List<ContactItem> contactItems = new ArrayList<ContactItem>();

	public ContactsView(Context context) {
		super(context);
		initViews();
	}

	public ContactsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}
	
	void initViews(){
		inflate(getContext(), R.layout.user_list, this);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		userListView = (ListView) findViewById(R.id.user_list);
		dataAdapter = new DataAdapter((Activity)getContext(), dataCallback);
		contactAdapter = new ContactAdapter(contactItems, getContext());
		userListView.setAdapter(contactAdapter);
		userListView.setOnItemClickListener(new MyOnItemClickListener());
		userListView.setDivider(null);
	}

	public void setData(List<ContactItem> users){
		contactItems.clear();
		contactItems.addAll(users);
		for (ContactItem contactItem : contactItems) {
			if (TextUtils.isEmpty(contactItem.name)) {
				dataAdapter.getUser(contactItem.id);
			}
		}
		contactAdapter.notifyDataSetChanged();
	}
	
	class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			App app = (App) getContext().getApplicationContext();
			User student = (User) listView.getAdapter().getItem(position);
			app.getChatDBHelper().read(app.getChatDBHelper().getWritableDatabase(), student.getJID());
			Intent intent = new Intent(getContext(), ChatActivity.class);
			intent.setData(student.getJabberUri());
			getContext().startActivity(intent);
//			finish();
		}
	}
	
	DataCallback dataCallback = new DataCallback() {
		
		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_USER:
				UserDetailsResult result = (UserDetailsResult)msg.obj;
				if (result != null && result.user != null) {
					User user = result.user;
					App.getInstance().getDBHelper().saveUser(App.getInstance().getUserDB(), user);
					int index = CommonUtils.indexOfById(contactItems, user.id);
					if (index != -1) {
						ContactItem oldItem = contactItems.get(index);
						ContactItem item = new ContactItem(user);
						item.lastChatTime = oldItem.lastChatTime;
						item.lastMessage = oldItem.lastMessage;
						item.unreadCount = oldItem.unreadCount;
						item.isMessage = true;
						contactItems.remove(index);
						contactItems.add(index, item);
						contactAdapter.notifyDataSetChanged();
					}
				}
				break;
			default:
				break;
			}
		}
	};
}

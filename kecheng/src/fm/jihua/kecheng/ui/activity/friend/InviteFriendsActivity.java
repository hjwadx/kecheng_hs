package fm.jihua.kecheng.ui.activity.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

public class InviteFriendsActivity extends BaseActivity {

	RenrenAuthHelper renrenAuthHelper;
	AuthHelper weiboAuthHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_course);
		initTitlebar();
		initViews();
		init();
	}

	void initTitlebar() {
		setTitle("邀请好友");
	}

	void initViews() {
		int[] icons = new int[] { R.drawable.icon_addresslist,
				R.drawable.icon_renren, R.drawable.icon_weibo };
		String[] titles = new String[] { "通讯录", "人人网", "新浪微博" };
		String[] descs = new String[] { "从通讯录中邀请好友", "邀请人人好友一同使用课程格子",
				"邀请粉丝一同使用课程格子" };
		if (ObjectUtils.containsElement(Const.NO_SMS_CHANNEL, Const.getChannelName(this))) {
			icons = new int[] {R.drawable.icon_renren, R.drawable.icon_weibo };

			titles = new String[] {"人人网", "新浪微博" };
			descs = new String[] {"邀请人人好友一同使用课程格子", "邀请粉丝一同使用课程格子" };
		}
		ListView listView = (ListView) findViewById(R.id.add_course_courses);
		List<HashMap<String, Object>> array = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < titles.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("icon", icons[i]);
			map.put("title", titles[i]);
			map.put("desc", descs[i]);
			array.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, array,
				R.layout.invite_friends,
				new String[] { "icon", "title", "desc" }, new int[] {
						R.id.icon, R.id.title, R.id.desc });
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new MyOnItemClickListener());
		((View) findViewById(R.id.add_course_search).getParent())
				.setVisibility(View.GONE);
	}

	void init() {
		renrenAuthHelper = new RenrenAuthHelper(this);
		weiboAuthHelper = new WeiboAuthHelper(this);
	}

	void startPickPhone() {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, 1);
		} catch (SecurityException e) {
			e.printStackTrace();
			Hint.showTipsShort(this, "您当前的版本不支持发送短信。");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case 1:
			if (resultCode == RESULT_OK) {
				try {
					Uri contactData = data.getData();

					Cursor c = managedQuery(contactData, null, null, null, null);
					c.moveToFirst();
					ArrayList<String> phoneList = new ArrayList<String>();
//					while (!c.isAfterLast()) {
						String phoneNum = this.getContactPhone(c);
						phoneList.add(phoneNum);
//					}
					Intent intent = new Intent(InviteFriendsActivity.this,
							InviteActivity.class);
					intent.putExtra("type", 0);
					intent.putExtra("phone_list", phoneList);
					startActivity(intent);

				} catch (Exception exception) {
					exception.printStackTrace();
					Hint.showTipsLong(this, "您的当前版本不支持通过发送短信，如需通过短信邀请好友，请从应用汇上下载最新版本");
				}
			}
			break;
		}

	}

	// 获取联系人电话
	private String getContactPhone(Cursor cursor) {

		int phoneColumn = cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		int phoneNum = cursor.getInt(phoneColumn);
		String phoneResult = "";
		if (phoneNum > 0) {
			// 获得联系人的ID号
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
			// 获得联系人的电话号码的cursor;
			Cursor phones = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			// int phoneCount = phones.getCount();
			// allPhoneNum = new ArrayList<String>(phoneCount);
			if (phones.moveToFirst()) {
				// 遍历所有的电话号码
				for (; !phones.isAfterLast(); phones.moveToNext()) {
					int index = phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					int typeindex = phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
					int phone_type = phones.getInt(typeindex);
					String phoneNumber = phones.getString(index);
					switch (phone_type) {
					case 2:
						phoneResult = phoneNumber;
						break;
					}
					// allPhoneNum.add(phoneNumber);
				}
				if (!phones.isClosed()) {
					phones.close();
				}
			}
		}
		return phoneResult;
	}

	class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> listView, View arg1,
				int position, long arg3) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> data = (HashMap<String, Object>) listView.getAdapter().getItem(position);
			int icon = (Integer) data.get("icon");
			switch (icon) {
			case R.drawable.icon_addresslist: {
				startPickPhone();
			}
				break;
			case R.drawable.icon_renren:
				renrenAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
				break;
			case R.drawable.icon_weibo:
				weiboAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
				break;
			default:
				break;
			}
		}
	}

    private class MySNSCallback implements SNSCallback{

		private int mScope = 0;

		public MySNSCallback(int scope) {
            mScope = scope;
        }

		@Override
		public void onComplete(final AuthHelper authHelper, Object data) {
			switch (mScope){
			case SNSCallback.AUTH:
				authHelper.bind(new MySNSCallback(SNSCallback.BIND));
				break;
			case SNSCallback.BIND:
				Intent intent = new Intent(InviteFriendsActivity.this,
						AddFriendsBySNSActivity.class);
				intent.putExtra("category", authHelper.getType());
				startActivity(intent);
				break;
			}
		}

		@Override
		public void onError(AuthHelper authHelper) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}
}

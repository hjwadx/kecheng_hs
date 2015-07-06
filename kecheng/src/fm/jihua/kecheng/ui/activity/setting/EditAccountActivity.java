package fm.jihua.kecheng.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.AccountResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;

public class EditAccountActivity extends BaseActivity {
	
	DataAdapter mDataAdapter;
	AccountResult account;
	
	public static final int REQUESTCODE_EMAIL = 101;
	public static final int REQUESTCODE_PASSWORD = 102;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onEvent(this, "enter_account_view");
		setContentView(R.layout.edit_account);
		initTitlebar();
		initViews();
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		App app = (App)getApplication();
		if (app.getAccountInfo() == null) {
			UIUtil.block(this);
		}
		mDataAdapter.getAccountInfo();
	}

	void initTitlebar() {
		setTitle("我的账号");
	}

	void initViews() {
		findViewById(R.id.email_parent).setOnClickListener(clickListener);
		findViewById(R.id.password_parent).setOnClickListener(clickListener);
	}

	void init(AccountResult account) {
		this.account = account;
		((TextView)findViewById(R.id.email)).setText(account.email != null && account.email.length() > 0 ? "已设置" : "未设置");
		((TextView)findViewById(R.id.gezi_id)).setText(account.gezi_id);
		if (account.has_password) {
			((TextView)findViewById(R.id.password)).setText("修改密码");
		}else {
			((TextView)findViewById(R.id.password)).setText("设置密码");
		}
	}
	
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.email_parent:{
				Intent intent = new Intent(EditAccountActivity.this, EditEmailActivity.class);
				if (account != null) {
					intent.putExtra("email", account.email);
				}
				startActivityForResult(intent, REQUESTCODE_EMAIL);
			}
				break;
			case R.id.password_parent:{
				Intent intent = new Intent(EditAccountActivity.this, EditPasswordActivity.class);
				if (account != null) {
					intent.putExtra("has_password", account.has_password);
					if(!account.has_password){
						MobclickAgent.onEvent(EditAccountActivity.this, "action_set_password");
					}
				}
				startActivityForResult(intent, REQUESTCODE_PASSWORD);
			}
				break;

			default:
				break;
			}
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		mDataAdapter.getAccountInfo();
	}
	
	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(EditAccountActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_ACCOUNT_INFO:
				AccountResult result = (AccountResult)msg.obj;
				if (result != null && result.success) {
					init(result);
				}
				break;
			default:
				break;
			}
		}
	}
}

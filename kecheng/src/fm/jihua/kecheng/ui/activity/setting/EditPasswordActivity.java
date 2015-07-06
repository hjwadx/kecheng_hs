package fm.jihua.kecheng.ui.activity.setting;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.AccountResult;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;

public class EditPasswordActivity extends BaseActivity {
	
	DataAdapter mDataAdapter;
	EditText oldPasswordTextView;
	EditText passwordTextView;
	EditText passwordConfirmTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_password);
		initTitlebar();
		initViews();
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
	}

	void initTitlebar() {
		setTitle("设置密码");
		getKechengActionBar().setRightText("保存");
		getKechengActionBar().getRightTextButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (passwordTextView.getText().toString().equals(passwordConfirmTextView.getText().toString())) {
					UIUtil.block(EditPasswordActivity.this);
					mDataAdapter.changePassword(oldPasswordTextView.getText().toString().trim(), passwordTextView.getText().toString().trim());
				}else {
					Hint.showTipsShort(EditPasswordActivity.this, R.string.prompt_different_password_when_amend);
				}
			}
		});
	}

	void initViews() {
		oldPasswordTextView = (EditText)findViewById(R.id.old_password);
		passwordTextView = (EditText)findViewById(R.id.password);
		passwordConfirmTextView = (EditText)findViewById(R.id.password_confirmation);
		if (!getIntent().getBooleanExtra("has_password", true)) {
			((View)oldPasswordTextView.getParent()).setVisibility(View.GONE);
			((View)passwordTextView.getParent()).setBackgroundResource(R.drawable.input_top);
		}
	}
	
	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(EditPasswordActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_CHANGE_PASSWORD:
				BaseResult result = (BaseResult)msg.obj;
				if (result != null ) {
					if (result.success) {
						if (!getIntent().getBooleanExtra("has_password", true)) {
							MobclickAgent.onEvent(EditPasswordActivity.this, "event_set_password_succeed");
						}
						App app = (App)getApplication();
						AccountResult account = app.getAccountInfo();
						account.has_password = true;
						app.setAccountInfo(account);
						Hint.showTipsShort(EditPasswordActivity.this, "密码设置成功");
						setResult(RESULT_OK);
						finish();
					}else {
						Hint.showTipsShort(EditPasswordActivity.this, result.notice);
					}
				}else{
					Hint.showTipsShort(EditPasswordActivity.this, "密码设置失败");
				}
					
				break;
			default:
				break;
			}
		}
	}
}

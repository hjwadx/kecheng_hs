package fm.jihua.kecheng.ui.activity.setting;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.AccountResult;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;

public class EditEmailActivity extends BaseActivity {
	
	DataAdapter mDataAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_email);
		initTitlebar();
		initViews();
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
	}

	void initTitlebar() {
		setTitle("修改邮件地址");
		getKechengActionBar().setRightText("保存");
		getKechengActionBar().getRightTextButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIUtil.block(EditEmailActivity.this);
				Map<String, Object> map = new HashMap<String, Object>();
				TextView emailTextView = (TextView) findViewById(R.id.email);
				map.put("email", emailTextView.getText().toString().trim());
				mDataAdapter.updateUser(map, null, null, null, 0);
			}
		});
	}

	void initViews() {
		((TextView)findViewById(R.id.email)).setText(getIntent().getStringExtra("email"));
	}
	
	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(EditEmailActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_UPDATE_USER:
				RegistResult result = (RegistResult)msg.obj;
				if (result != null && result.success) {
					App app = (App)getApplication();
					AccountResult account = app.getAccountInfo();
					if (account != null) {
						try {
							TextView emailTextView = (TextView) findViewById(R.id.email);
							account.email = emailTextView.getText().toString().trim();
							app.setAccountInfo(account);
						} catch (Exception e) {
						}
					}
					Hint.showTipsShort(EditEmailActivity.this, "邮箱修改成功");
					setResult(RESULT_OK);
					finish();
				}else {
					Hint.showTipsShort(EditEmailActivity.this, "邮箱修改失败");
				}
				break;
			default:
				break;
			}
		}
	}
}

package fm.jihua.kecheng.ui.activity.friend;

import java.util.List;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.receiver.SMSSentReceiver;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

public class InviteActivity extends BaseActivity {

	EditText contentEditText;
	TextView remainCountTextView;
	int type = 0;//0for短信， 1for人人， 2for微博
	String[] titles = new String[]{"通过短信邀请", "通过人人网邀请", "通过微博邀请"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite);
		type = getIntent().getIntExtra("type", 1);
		initTitlebar();
		initViews();
	}

	void initTitlebar(){
		setTitle(titles[type]);
		getKechengActionBar().setRightText("发送");
		getKechengActionBar().getRightTextButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = contentEditText.getText().toString().trim();
				switch (type) {
				case 0:
					List<String> phones = getIntent().getStringArrayListExtra("phone_list");
					for(String phone:phones){
						sendMessage(phone, message);
					}
					finish();
					break;
				case 1:
					RenrenAuthHelper renrenAuthHelper = new RenrenAuthHelper(InviteActivity.this);
					renrenAuthHelper.update(message, new MySNSCallback(SNSCallback.UPLOAD));
					break;
				case 2:
					WeiboAuthHelper weiboAuthHelper = new WeiboAuthHelper(InviteActivity.this);
					weiboAuthHelper.update(message, new MySNSCallback(SNSCallback.UPLOAD));
					break;

				default:
					break;
				}
			}
		});
	}

	void sendMessage(String phoneNum, String message){
		SmsManager sms = SmsManager.getDefault();
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
		        new Intent(SMSSentReceiver.SENT), 0);
		// 如果短信没有超过限制长度，则返回一个长度的List。
		List<String> texts = sms.divideMessage(message);
		try {
			for (String text : texts) {
				sms.sendTextMessage(phoneNum, null, text, sentPI, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Hint.showTipsShort(this, "短信发送失败");
		}
	}

	void initViews(){
		contentEditText = (EditText)findViewById(R.id.invite_message);
		if (type == 0) {
			contentEditText.setText("我在用课程格子，一个很好用的中学生课程表。推荐给你试试：http://t.cn/zRJEpPW ");
		}
		remainCountTextView = (TextView)findViewById(R.id.remain_count);
		remainCountTextView.setText("还可以输入"
				+ (140 - contentEditText.getText().length()) + "个字");
		contentEditText.setSelection(contentEditText.length());
	}

    private class MySNSCallback implements SNSCallback{

		private int mScope = 0;

		public MySNSCallback(int scope) {
            mScope = scope;
        }

		@Override
		public void onComplete(final AuthHelper authHelper, Object data) {
			switch (mScope){
			case SNSCallback.UPLOAD:
				if (authHelper.getType() == Const.RENREN) {
					MobclickAgent.onEvent(InviteActivity.this, "action_invite_friends", "renren");
					Hint.showTipsShort(InviteActivity.this, "人人邀请成功");
				} else {
					MobclickAgent.onEvent(InviteActivity.this, "action_invite_friends", "weibo");
					Hint.showTipsShort(InviteActivity.this, "微博邀请成功");
				}
				finish();
				break;
			}
		}

		@Override
		public void onError(final AuthHelper authHelper) {
			switch (mScope){
			case SNSCallback.UPLOAD:
				if (authHelper.getType() == Const.RENREN) {
					Hint.showTipsShort(InviteActivity.this, "人人邀请失败");
				} else {
					Hint.showTipsShort(InviteActivity.this, "微博邀请失败");
				}
				break;
			}
		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}
}

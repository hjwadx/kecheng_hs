package fm.jihua.kecheng.ui.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.weibo.sdk.android.sso.SsoHandler;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.SharePic;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

public class SNSInviteActivity extends BaseActivity{

	EditText contentEditText;
	TextView remainCountTextView;

	RenrenAuthHelper renrenAuthHelper;
	WeiboAuthHelper weiboAuthHelper;
//	TencentAuthHelper tencentAuthHelper;
//	TencentAuthHelper tencentWeiboAuthHelper;

	final int NO_NEED_UPLOAD = 0;
	final int READY_FOR_UPLOAD = 1;
	final int UPLOAD_SUCCESS = 2;
	final int UPLOAD_FAILED = 3;

	int shareState;

	private boolean isOnlyText;
	int type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snsinvite);
		type = getIntent().getIntExtra("TYPE", 2);
		setTitle(type == Const.WEIBO?"微博邀请":"人人邀请");
		findViews();
		setListeners();
		initTitlebar();
		init();
	}

	void findViews() {
		contentEditText = (EditText) findViewById(R.id.content);
		remainCountTextView = (TextView) findViewById(R.id.remain_count);
	}

	void setListeners() {
		contentEditText.addTextChangedListener(textWatcher);
	}


	void initTitlebar() {
		getKechengActionBar().getMenuButton().setVisibility(View.VISIBLE);
		getKechengActionBar().getMenuButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		getKechengActionBar().getActionButton().setVisibility(View.VISIBLE);
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		getKechengActionBar().getActionButton().setOnClickListener(listener);
	}

	void init() {

		final SharePic sharePic = (SharePic) findViewById(R.id.share_pic);
		isOnlyText = getIntent().getBooleanExtra("ONLYTEXT", false);
		if (isOnlyText) {
			sharePic.setVisibility(View.GONE);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {

					final Bitmap pic = ImageHlp.decodeFile(App.getShareImageFilename());
					if (pic != null) {
						sharePic.setImage(pic);
					}
				}
			}).start();
		}

		String content = getIntent().getStringExtra("CONTENT");
		contentEditText.setText(content);
		remainCountTextView.setText("还可以输入" + (140 - contentEditText.getText().length()) + "个字");
		contentEditText.setSelection(contentEditText.length());

		renrenAuthHelper = new RenrenAuthHelper(this);
		weiboAuthHelper = new WeiboAuthHelper(this);
	}



	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			remainCountTextView.setText("还可以输入" + (140 - s.length()) + "个字");
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};


	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			UIUtil.block(SNSInviteActivity.this);
			String content = contentEditText.getText().toString().trim();
			switch (v.getId()) {
			case R.id.action:
				switch(type){
				case Const.WEIBO:
					shareState = READY_FOR_UPLOAD;
					String weiboContent = content;
					if (isOnlyText) {
						weiboAuthHelper.update(weiboContent, new MySNSCallback(SNSCallback.UPLOAD));
					} else
						weiboAuthHelper.upload(App.getShareImageFilename(), weiboContent, new MySNSCallback(SNSCallback.UPLOAD));
					break;
				case Const.RENREN:
					shareState = READY_FOR_UPLOAD;
					String renrenContent = content;
					if (isOnlyText) {
						renrenAuthHelper.update(renrenContent, new MySNSCallback(SNSCallback.UPLOAD));
					} else
						renrenAuthHelper.upload(App.getShareImageFilename(), renrenContent, new MySNSCallback(SNSCallback.UPLOAD));
					break;
				default:
					break;
				}
			}
		}
	};

	void checkForFinish() {

		if (shareState != READY_FOR_UPLOAD) {
			UIUtil.unblock(SNSInviteActivity.this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		try {
			UIUtil.block(this);
			switch (requestCode) {
			case SsoHandler.DEFAULT_AUTH_ACTIVITY_CODE:
				weiboAuthHelper.onActivityResult(requestCode, resultCode, data);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			case SNSCallback.UPLOAD:
				int helperCategory = authHelper.getType();
				((App) getApplication()).setShared(true);
				if (helperCategory == Const.RENREN) {
					shareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(SNSInviteActivity.this, "人人发布邀请成功");
				} else if (helperCategory == Const.WEIBO) {
					shareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(SNSInviteActivity.this, "新浪微博发布邀请成功");
				}
				checkForFinish();
				finish();
				break;
			}
		}

		@Override
		public void onError(final AuthHelper authHelper) {
			switch (mScope){
			case SNSCallback.UPLOAD:
				int helperCategory = authHelper.getType();
				if (helperCategory == Const.RENREN) {
					shareState = UPLOAD_FAILED;
					Hint.showTipsShort(SNSInviteActivity.this, "人人发布邀请失败");
				} else if (helperCategory == Const.WEIBO) {
					shareState = UPLOAD_FAILED;
					Hint.showTipsShort(SNSInviteActivity.this, "新浪微博发布邀请失败");
				}
				checkForFinish();
				break;
			}


		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}
}

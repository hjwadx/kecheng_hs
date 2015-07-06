package fm.jihua.kecheng.ui.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
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
import fm.jihua.kecheng.utils.TencentAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

public class ShareActivity extends BaseActivity {

	EditText contentEditText;
	CheckBox renrenCheckBox;
	CheckBox weiboCheckBox;
	CheckBox tencentCheckBox;
	CheckBox tencentWeiboCheckBox;
	TextView remainCountTextView;
	SharePic sharePic;

	RenrenAuthHelper renrenAuthHelper;
	WeiboAuthHelper weiboAuthHelper;
	TencentAuthHelper tencentAuthHelper;
	TencentAuthHelper tencentWeiboAuthHelper;

	final int NO_NEED_UPLOAD = 0;
	final int READY_FOR_UPLOAD = 1;
	final int UPLOAD_SUCCESS = 2;
	final int UPLOAD_FAILED = 3;

	int renrenShareState;
	int weiboShareState;
	int tencentShareState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
		findViews();
		setListeners();
		initTitlebar();
		init();
		initSharePic();
	}

	void init() {

		String content = getIntent().getStringExtra("CONTENT");
		contentEditText.setText(content);
		remainCountTextView.setText("还可以输入" + (140 - contentEditText.getText().length()) + "个字");
		contentEditText.setSelection(contentEditText.length());

		renrenAuthHelper = new RenrenAuthHelper(this);
		tencentAuthHelper = new TencentAuthHelper(this);
		tencentWeiboAuthHelper = new TencentAuthHelper(this);
		weiboAuthHelper = new WeiboAuthHelper(this);

		renrenCheckBox.setChecked(renrenAuthHelper.isAuthed());
		weiboCheckBox.setChecked(weiboAuthHelper.isAuthed());
		tencentCheckBox.setChecked(tencentAuthHelper.isAuthed());
		tencentWeiboCheckBox.setChecked(tencentWeiboAuthHelper.isAuthed());
	}
	
	void initSharePic(){
		
		isOnlyText = getIntent().getBooleanExtra("ONLYTEXT", false);
		if (isOnlyText) {
			sharePic.setVisibility(View.GONE);
			RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) contentEditText.getLayoutParams();
			int dimension = (int) getResources().getDimension(R.dimen.home_menu_left_padding);
			layoutParams.setMargins(dimension, dimension, dimension, dimension);
			contentEditText.setLayoutParams(layoutParams);
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
		
		sharePic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showImageInfoDialog();
			}
		});
	}
	
	private void showImageInfoDialog(){
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		ImageView imageView = new ImageView(ShareActivity.this);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageView.setImageBitmap(sharePic.getBitmap());
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
        dialog.setContentView(imageView);
        dialog.show();
	}

	void findViews() {
		contentEditText = (EditText) findViewById(R.id.content);
		renrenCheckBox = (CheckBox) findViewById(R.id.renren);
		weiboCheckBox = (CheckBox) findViewById(R.id.weibo);
		tencentCheckBox = (CheckBox) findViewById(R.id.tencent);
		tencentWeiboCheckBox = (CheckBox) findViewById(R.id.tencentWeibo);
		remainCountTextView = (TextView) findViewById(R.id.remain_count);
		sharePic = (SharePic) findViewById(R.id.share_pic);
	}

	void setListeners() {
		contentEditText.addTextChangedListener(textWatcher);
		renrenCheckBox.setOnCheckedChangeListener(checkedChangedListener);
		weiboCheckBox.setOnCheckedChangeListener(checkedChangedListener);
		tencentCheckBox.setOnCheckedChangeListener(checkedChangedListener);
		tencentWeiboCheckBox.setOnCheckedChangeListener(checkedChangedListener);
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

	OnCheckedChangeListener checkedChangedListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.renren:
				if (isChecked) {
					if (!renrenAuthHelper.isAuthed()) {
						renrenCheckBox.setChecked(false);
						renrenAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}
				}
				break;
			case R.id.weibo:
				if (isChecked) {
					if (!weiboAuthHelper.isAuthed()) {
						weiboCheckBox.setChecked(false);
						weiboAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}
				}
				break;
			case R.id.tencent:
				if (isChecked) {
					if (!tencentAuthHelper.isAuthed()) {
						tencentCheckBox.setChecked(false);
						tencentAuthHelper.setCurrentType(Const.QQ);
						tencentAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}
				}
				break;
			case R.id.tencentWeibo:
				if (isChecked) {
					if (!tencentWeiboAuthHelper.isAuthed()) {
						tencentWeiboCheckBox.setChecked(false);
						tencentWeiboAuthHelper.setCurrentType(Const.QQWeibo);
						tencentWeiboAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
					}
				}
				break;
			default:
				break;
			}
		}
	};

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
			switch (v.getId()) {
			case R.id.action:
				if (renrenCheckBox.isChecked() || weiboCheckBox.isChecked() || tencentCheckBox.isChecked() || tencentWeiboCheckBox.isChecked()) {
					UIUtil.block(ShareActivity.this);
				} else {
					Hint.showTipsShort(getApplicationContext(), "请选择人人或者新浪微博或者腾讯进行分享");
				}

				String content = contentEditText.getText().toString().trim();
				if (renrenCheckBox.isChecked()) {
					renrenShareState = READY_FOR_UPLOAD;
					String renrenContent = content;
					if (renrenContent.contains("@课程格子")) {
						renrenContent = renrenContent.replace("@课程格子", "@课程格子(699177210) ") + "http://t.cn/zRJEpPW ";
					} else {
						renrenContent = renrenContent + "#课程格子#http://t.cn/zRJEpPW ";
					}
					if (isOnlyText) {
						renrenAuthHelper.update(renrenContent, new MySNSCallback(SNSCallback.UPLOAD));
					} else
						renrenAuthHelper.upload(App.getShareImageFilename(), renrenContent, new MySNSCallback(SNSCallback.UPLOAD));
				}
				if (weiboCheckBox.isChecked()) {
					weiboShareState = READY_FOR_UPLOAD;
					String weiboContent;
					if (content.contains("@课程格子")) {
						weiboContent = content + "http://t.cn/zRJEpPW ";
					} else {
						weiboContent = content + "#课程格子#http://t.cn/zRJEpPW ";
					}
					if (isOnlyText) {
						weiboAuthHelper.update(weiboContent, new MySNSCallback(SNSCallback.UPLOAD));
					} else
						weiboAuthHelper.upload(App.getShareImageFilename(), weiboContent, new MySNSCallback(SNSCallback.UPLOAD));
				}
				if (tencentCheckBox.isChecked()) {
					tencentShareState = READY_FOR_UPLOAD;
					String tencentContent;
					if (content.contains("@课程格子")) {
						tencentContent = content + "http://t.cn/zRJEpPW ";
					} else {
						tencentContent = content + "#课程格子#http://t.cn/zRJEpPW ";
					}
					tencentAuthHelper.setCurrentType(Const.QQ);
					if (isOnlyText) {
						tencentAuthHelper.update(tencentContent, new MySNSCallback(SNSCallback.UPLOAD));
					} else
						tencentAuthHelper.upload(App.getShareImageFilename(), tencentContent, new MySNSCallback(SNSCallback.UPLOAD));
				}
				if (tencentWeiboCheckBox.isChecked()) {
					tencentShareState = READY_FOR_UPLOAD;
					String tencentContent;
					if (content.contains("@课程格子")) {
						tencentContent = content + "http://t.cn/zRJEpPW ";
					} else {
						tencentContent = content + "#课程格子#http://t.cn/zRJEpPW ";
					}
					tencentWeiboAuthHelper.setCurrentType(Const.QQWeibo);
					if (isOnlyText) {
						tencentWeiboAuthHelper.update(tencentContent, new MySNSCallback(SNSCallback.UPLOAD));
					} else
						tencentWeiboAuthHelper.upload(App.getShareImageFilename(), tencentContent, new MySNSCallback(SNSCallback.UPLOAD));
				}
				break;
			default:
				break;
			}
		}
	};

	private boolean isOnlyText;

	void checkForFinish() {

		if (renrenShareState != READY_FOR_UPLOAD && weiboShareState != READY_FOR_UPLOAD && tencentShareState != READY_FOR_UPLOAD) {
			UIUtil.unblock(ShareActivity.this);
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
				if(requestCode != SsoHandler.DEFAULT_AUTH_ACTIVITY_CODE){
					tencentAuthHelper.onActivityResult(requestCode, resultCode, data);
				}
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
			int helperCategory = authHelper.getType();
			switch (mScope) {
			case SNSCallback.AUTH:
				authHelper.bind(new MySNSCallback(SNSCallback.BIND));
				break;
			case SNSCallback.BIND:
				if (helperCategory == Const.RENREN) {
					Hint.showTipsShort(ShareActivity.this, "人人绑定成功");
					renrenCheckBox.setChecked(true);
				} else if (helperCategory == Const.WEIBO) {
					Hint.showTipsShort(ShareActivity.this, "新浪微博绑定成功");
					weiboCheckBox.setChecked(true);
				} else if (helperCategory == Const.QQ) {
					Hint.showTipsShort(ShareActivity.this, "腾讯QQ绑定成功");
					tencentCheckBox.setChecked(true);
				} else if (helperCategory == Const.QQWeibo) {
					Hint.showTipsShort(ShareActivity.this, "腾讯微博绑定成功");
					tencentWeiboCheckBox.setChecked(true);
				}
				break;
			case SNSCallback.UPLOAD:
				((App) getApplication()).setShared(true);
				if (helperCategory == Const.RENREN) {
					MobclickAgent.onEvent(ShareActivity.this, "event_share_renren_succeed", getIntent().getStringExtra("UMENG_PARAMETER"));
					renrenShareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(ShareActivity.this, "人人分享成功");
				} else if (helperCategory == Const.WEIBO) {
					MobclickAgent.onEvent(ShareActivity.this, "event_share_weibo_succeed", getIntent().getStringExtra("UMENG_PARAMETER"));
					weiboShareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(ShareActivity.this, "新浪微博分享成功");
				} else if (helperCategory == Const.QQ) {
					MobclickAgent.onEvent(ShareActivity.this, "event_share_qq_succeed", getIntent().getStringExtra("UMENG_PARAMETER"));
					tencentShareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(ShareActivity.this, "QQ空间分享成功");
				} else if (helperCategory == Const.QQWeibo) {
					MobclickAgent.onEvent(ShareActivity.this, "event_share_qq_succeed", getIntent().getStringExtra("UMENG_PARAMETER"));
					tencentShareState = UPLOAD_SUCCESS;
					Hint.showTipsShort(ShareActivity.this, "腾讯微博分享成功");
				}
				checkForFinish();
				break;
			}
		}

		@Override
		public void onError(final AuthHelper authHelper) {
			int helperCategory = authHelper.getType();
			switch (mScope) {
			case SNSCallback.AUTH:
			case SNSCallback.BIND:
				if (helperCategory == Const.RENREN) {
					renrenCheckBox.setChecked(false);
				} else if (helperCategory == Const.WEIBO) {
					weiboCheckBox.setChecked(false);
				} else if (helperCategory == Const.QQ) {
					tencentCheckBox.setChecked(false);
				} else if (helperCategory == Const.QQWeibo) {
					tencentCheckBox.setChecked(false);
				}
				break;
			case SNSCallback.UPLOAD:
				if (helperCategory == Const.RENREN) {
					renrenShareState = UPLOAD_FAILED;
					Hint.showTipsShort(ShareActivity.this, "人人分享失败");
				} else if (helperCategory == Const.WEIBO) {
					weiboShareState = UPLOAD_FAILED;
					Hint.showTipsShort(ShareActivity.this, "新浪微博分享失败");
				} else if (helperCategory == Const.QQ) {
					tencentShareState = UPLOAD_FAILED;
					Hint.showTipsShort(ShareActivity.this, "QQ空间分享失败");
				} else if (helperCategory == Const.QQWeibo) {
					tencentShareState = UPLOAD_FAILED;
					Hint.showTipsShort(ShareActivity.this, "腾讯微博分享失败");
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

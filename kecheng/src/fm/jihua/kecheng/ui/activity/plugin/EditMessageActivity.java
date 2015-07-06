package fm.jihua.kecheng.ui.activity.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.SecretPostComment;
import fm.jihua.kecheng.rest.entities.SecretPostCommentResult;
import fm.jihua.kecheng.rest.entities.SecretPostResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.Hint;

public class EditMessageActivity extends BaseActivity implements DataCallback {

	// private Note mNote;

	DataAdapter dataAdapter;
	TextView remainCountTextView;
	EditText contentEditText;

	ImageView img_bottom_btn;
	CheckBox checkbox_bottom;

//	EmojiGridContainer mEmojiGridContainer;

	// secret_post_comment
	SecretPost post;
	SecretPostComment comment;

	int currentType = 0;
	public static final String INTENT_STR = "editmessage";
	public static final int TYPE_SECRET_POST_NEW = 1;
	public static final int TYPE_SECRET_POST_COMMENT = 2;
	public static final int TYPE_SECRET_POST_COMMENT_REPLY = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_message);
		initIntent();
		findViews();
		initViews();
		setListeners();
		initTitlebar();
		dataAdapter = new DataAdapter(this, this);
	}

	void initIntent() {
		currentType = getIntent().getIntExtra(INTENT_STR, 0);

		post = (SecretPost) getIntent().getSerializableExtra("SECRET_POST");
		if(currentType == TYPE_SECRET_POST_COMMENT_REPLY){
			comment = (SecretPostComment) getIntent().getSerializableExtra("SECRET_POST_COMMEN");
		}
	}

	void findViews() {
		contentEditText = (EditText) findViewById(R.id.chat_input);
		remainCountTextView = (TextView) findViewById(R.id.remain_count);
		img_bottom_btn = (ImageView) findViewById(R.id.edit_message_bottom_face);
//		mEmojiGridContainer = (EmojiGridContainer) findViewById(R.id.emoji_grid);
		checkbox_bottom = (CheckBox) findViewById(R.id.edit_message_bottom_checkbox);
	}

	void initViews() {

		contentEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				mEmojiGridContainer.setVisibility(View.GONE);
				img_bottom_btn.setBackgroundResource(R.drawable.bottombar_btn_face);
			}
		});

		contentEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
//					mEmojiGridContainer.setVisibility(View.GONE);
				}
			}
		});

		contentEditText.requestFocus();

		switch (currentType) {
		case TYPE_SECRET_POST_NEW:
			MobclickAgent.onEvent(this, "enter_secret_post_create_view");
			checkbox_bottom.setVisibility(View.GONE);
			setTitle("投稿");
			contentEditText.setHint(R.string.secret_post_new_hint);
//			mEmojiGridContainer.initModule(EmojiGridContainer.MODULE_FACE, EmojiGridContainer.MODULE_PASTER);
			RelativeLayout relativeLayout = (RelativeLayout) img_bottom_btn.getParent();
			relativeLayout.setVisibility(View.GONE);
			break;
		case TYPE_SECRET_POST_COMMENT:
			MobclickAgent.onEvent(this, "enter_secret_post_comment_view");
			checkbox_bottom.setVisibility(View.VISIBLE);
			setTitle("添加评论");
			contentEditText.setHint(R.string.secret_post_comment_hint);
//			mEmojiGridContainer.initModule(EmojiGridContainer.MODULE_FACE, EmojiGridContainer.MODULE_PASTER);
			break;
		case TYPE_SECRET_POST_COMMENT_REPLY:
			MobclickAgent.onEvent(this, "enter_secret_post_comment_view");
			checkbox_bottom.setVisibility(View.VISIBLE);
			setTitle("回复" + comment.floor_num + "楼");
			contentEditText.setHint(R.string.secret_post_comment_hint);
//			mEmojiGridContainer.initModule(EmojiGridContainer.MODULE_FACE, EmojiGridContainer.MODULE_PASTER);
			break;
		default:
			break;
		}

	}

	void setListeners() {
		contentEditText.addTextChangedListener(textWatcher);
		getKechengActionBar().getRightButton().setOnClickListener(clickListener);
		img_bottom_btn.setOnClickListener(clickListener);
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

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action:
			case R.id.action_textview: {
				String content = contentEditText.getText().toString().trim();
				if (content != null && content.length() > 0) {
					UIUtil.block(EditMessageActivity.this);
					send_message(content);
				}
			}
			case R.id.edit_message_bottom_face: {
//				if (mEmojiGridContainer.getVisibility() == View.GONE) {
//					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(contentEditText.getWindowToken(), 0);
//					img_bottom_btn.setBackgroundResource(R.drawable.bottombar_btn_keyboard);
//					mEmojiGridContainer.postDelayed(new Runnable() {
//
//						@Override
//						public void run() {
//							mEmojiGridContainer.setVisibility(View.VISIBLE);
//						}
//					}, 100);
//				} else {
//					mEmojiGridContainer.setVisibility(View.GONE);
//					contentEditText.requestFocus();
//					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(contentEditText, 0);
//					img_bottom_btn.setBackgroundResource(R.drawable.bottombar_btn_face);
//				}
			}
			default:
				break;
			}
		}
	};

	void send_message(String content) {
		switch (currentType) {
		case TYPE_SECRET_POST_NEW:
			SecretPost post = new SecretPost();
			post.content = content;
			dataAdapter.createSecretPost(post);
			break;
		case TYPE_SECRET_POST_COMMENT:
			SecretPostComment comment = new SecretPostComment();
			comment.content = content;
			comment.anonymous = checkbox_bottom.isChecked();
			dataAdapter.createSecretPostComment(this.post.id, comment, 0);
			break;
		case TYPE_SECRET_POST_COMMENT_REPLY:
			//这里应该带上comment的id
			SecretPostComment reply = new SecretPostComment();
			reply.content = content;
			reply.anonymous = checkbox_bottom.isChecked();
			dataAdapter.createSecretPostComment(this.post.id, reply, this.comment.id);
			break;
		default:
			break;
		}
	}

	void initTitlebar() {
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
	}

	@Override
	public void callback(Message msg) {
		UIUtil.unblock(this);
		switch (msg.what) {
		case DataAdapter.MESSAGE_CREATE_SECRET_POST:
			SecretPostResult result = (SecretPostResult) msg.obj;
			if (result != null && result.success) {
				MobclickAgent.onEvent(this, "event_secret_post_create_succeed");
				Intent intent = new Intent();
				intent.putExtra("SECRET_POST", result.secret_post);
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Hint.showTipsShort(EditMessageActivity.this, "添加消息失败");
			}
			break;
		case DataAdapter.MESSAGE_CREATE_SECRET_POST_COMMENT:
			SecretPostCommentResult secretPostCommentResult = (SecretPostCommentResult) msg.obj;
			if (secretPostCommentResult != null && secretPostCommentResult.success) {
				MobclickAgent.onEvent(this, "event_secret_post_comment_succeed");
				Intent intent = new Intent();
				intent.putExtra("SECRET_POST_COMMENT", secretPostCommentResult.comment);
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Hint.showTipsShort(EditMessageActivity.this, "添加消息失败");
			}
			break;
		default:
			break;
		}
	}

//	@Override
//	public void onBackPressed() {
//		if (mEmojiGridContainer.getVisibility() == View.VISIBLE) {
//			mEmojiGridContainer.setVisibility(View.GONE);
//			img_bottom_btn.setBackgroundResource(R.drawable.bottombar_btn_face);
//		} else
//			super.onBackPressed();
//	}

}

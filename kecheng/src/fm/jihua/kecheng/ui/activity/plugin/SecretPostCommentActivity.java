package fm.jihua.kecheng.ui.activity.plugin;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import fm.jihua.common.ui.widget.PullDownView;
import fm.jihua.common.ui.widget.PullDownView.UpdateHandle;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.SecretPostComment;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.view.SecretPostCommentsListView;
import fm.jihua.kecheng.utils.Const;

public class SecretPostCommentActivity extends BaseActivity{

	private PullDownView pullDownView;
	private SecretPostCommentsListView listView;
	SecretPost post;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secret_post_comment);
		post = (SecretPost) getIntent().getSerializableExtra("SECRET_POST");
		setTitle("评论列表");
		initTitlebar();
		findViews();
		init();
	}

	void findViews() {
		pullDownView = (PullDownView) findViewById(R.id.listbase);
		listView = ((SecretPostCommentsListView)findViewById(R.id.list));
	}

	void init(){
		listView.init(post);
		listView.setDivider(null);
		pullDownView.setUpdateHandle(new UpdateHandle() {

			@Override
			public void onUpdate() {
				listView.getData(false);
			}
		});
		pullDownView.update();
		listView.getData(true);
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action:
			case R.id.action_textview:
				Intent intent = new Intent(SecretPostCommentActivity.this, EditMessageActivity.class);
				intent.putExtra(EditMessageActivity.INTENT_STR, EditMessageActivity.TYPE_SECRET_POST_COMMENT);
				intent.putExtra("SECRET_POST", post);
				startActivityForResult(intent, Const.INTENT_DEFAULT);
//				Intent intent = new Intent(SecretPostCommentActivity.this, NewSecretPostCommentActivity.class);
//				intent.putExtra("SECRET_POST", post);
//				startActivityForResult(intent, Const.INTENT_DEFAULT);
				break;
			default:
				break;
			}
		}
	};


	void initTitlebar(){
		getKechengActionBar().getLeftButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("SECRET_POST", post);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		getKechengActionBar().setRightText("评论");
		getKechengActionBar().getRightTextButton().setOnClickListener(listener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		SecretPostComment comment = (SecretPostComment) data.getSerializableExtra("SECRET_POST_COMMENT");
		post.comments_count += 1;
		List<SecretPostComment> posts = new ArrayList<SecretPostComment>();
		posts.add(comment);
		listView.insertData(posts);
	}

	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		intent.putExtra("SECRET_POST", post);
		setResult(RESULT_OK, intent);
		finish();
	}
}

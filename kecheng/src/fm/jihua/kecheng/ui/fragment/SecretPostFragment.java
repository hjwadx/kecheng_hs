package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.widget.PullDownView;
import fm.jihua.common.ui.widget.PullDownView.UpdateHandle;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.plugin.EditMessageActivity;
import fm.jihua.kecheng.ui.view.SecretPostsListView;
import fm.jihua.kecheng.utils.Const;

public class SecretPostFragment extends BaseFragment {

	private PullDownView pullDownView;
	private SecretPostsListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MobclickAgent.onEvent(parent, "enter_secret_post_view");
		View view = inflater.inflate(R.layout.secret_post, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		App app = (App) getApplication();
		String title = "树洞";
		if (!TextUtils.isEmpty(app.getMyself().school)) {
			title = app.getMyself().school + title;
		}
		getActivity().setTitle(title);
		initTitlebar();
		findViews();
		init();
	}

	void findViews() {
		pullDownView = (PullDownView) findViewById(R.id.listbase);
		listView = ((SecretPostsListView) findViewById(R.id.posts));
		listView.setFragment(this);
	}

	void init() {
		listView.init();
		listView.setDivider(null);
		pullDownView.setUpdateHandle(new UpdateHandle() {

			@Override
			public void onUpdate() {
				listView.getData(false);
			}
		});
		pullDownView.update();
		listView.getData(true);
		App app = (App) getApplication();
		app.setKnonwnSecretPost(true);
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action:
			case R.id.action_textview:
				Intent intent = new Intent(parent, EditMessageActivity.class);
				intent.putExtra(EditMessageActivity.INTENT_STR, EditMessageActivity.TYPE_SECRET_POST_NEW);
				startActivityForResult(intent, Const.INTENT_DEFAULT);
				break;
			default:
				break;
			}
		}
	};

	public void initTitlebar() {
		BaseActivity activity = (BaseActivity)getActivity();
		activity.getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		activity.getKechengActionBar().setRightText("投稿");
		activity.getKechengActionBar().getRightTextButton().setOnClickListener(listener);
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != getActivity().RESULT_OK) {
			return;
		}
		if (requestCode == Const.INTENT_DEFAULT) {
			SecretPost post = (SecretPost) data.getSerializableExtra("SECRET_POST");
			List<SecretPost> posts = new ArrayList<SecretPost>();
			posts.add(post);
			listView.insertData(posts);
		} else if (requestCode == Const.INTENT_FOR_COMMENT) {
			SecretPost post = (SecretPost) data.getSerializableExtra("SECRET_POST");
			int position = data.getIntExtra("POSITION", -1);
			if (position != -1)
				listView.modifyData(position, post);
		}
	}

}

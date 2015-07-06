package fm.jihua.kecheng.ui.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.weibo.sdk.android.sso.SsoHandler;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.WeiboAuthHelper;

public class AddFriendsActivity extends BaseActivity {

	RenrenAuthHelper renrenAuthHelper;
	WeiboAuthHelper weiboAuthHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friends_main);
		initTitlebar();
		initViews();
		init();
	}

	void initTitlebar() {
		setTitle(R.string.act_add_friends_title);
	}

	void initViews() {
		findViewById(R.id.search).setOnClickListener(clickListener);
		findViewById(R.id.renren).setOnClickListener(clickListener);
		findViewById(R.id.weibo).setOnClickListener(clickListener);
		findViewById(R.id.search_by_gezi_id).setOnClickListener(clickListener);
//		findViewById(R.id.t_weibo).setOnClickListener(clickListener);
		findViewById(R.id.same_school).setOnClickListener(clickListener);
		findViewById(R.id.same_class).setOnClickListener(clickListener);
		findViewById(R.id.same_grade).setOnClickListener(clickListener);
//		findViewById(R.id.school).setOnClickListener(clickListener);
//		findViewById(R.id.feature).setOnClickListener(clickListener);
		findViewById(R.id.follower).setOnClickListener(clickListener);
	}

	void init() {
		renrenAuthHelper = new RenrenAuthHelper(this);
		weiboAuthHelper = new WeiboAuthHelper(this);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.search:
				startFriendsActivity(Const.SEARCH);
				break;
			case R.id.search_by_gezi_id:
				startFriendsActivity(Const.SEARCH_BY_GEZI_ID);
				break;
			case R.id.renren:
				renrenAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
				break;
			case R.id.weibo:
				weiboAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
				break;
			case R.id.same_school:
				startFriendsActivity(Const.CLASSMATES_SCHOOL);
				break;
			case R.id.same_grade:
				startFriendsActivity(Const.CLASSMATES_GRADE);
				break;
			case R.id.same_class:
				startFriendsActivity(Const.CLASSMATES);
				break;
			case R.id.follower:
				startFriendsActivity(Const.FOLLOWER);
			default:
				break;
			}
		}
	};

	void startFriendsActivity(int category){
		Intent intent = new Intent(AddFriendsActivity.this,
				(category ==  Const.SEARCH || category == Const.SEARCH_BY_GEZI_ID) ? AddFriendsBySearchActivity.class : AddFriendsBySNSActivity.class);
		intent.putExtra("category", category);
		startActivity(intent);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK){
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
//					tencentAuthHelper.onActivityResult(requestCode, resultCode, data);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			UIUtil.unblock(this);
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
				startFriendsActivity(authHelper.getType());
				break;
			}
		}

		@Override
		public void onError(AuthHelper authHelper) {
			switch (mScope){
			case SNSCallback.AUTH:
				break;
			case SNSCallback.BIND:
				break;
			}
		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}
}

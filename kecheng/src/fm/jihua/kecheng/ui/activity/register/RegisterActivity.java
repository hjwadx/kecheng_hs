package fm.jihua.kecheng.ui.activity.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;
import com.weibo.sdk.android.sso.SsoHandler;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.AuthHelper.CommonUser;
import fm.jihua.kecheng.interfaces.AuthHelper.SchoolInfo;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseMenuActivity;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.fragment.BaseFragment;
import fm.jihua.kecheng.ui.fragment.RegisterMenuFragment;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.RenrenAuthHelper;
import fm.jihua.kecheng.utils.TencentAuthHelper;
import fm.jihua.kecheng.utils.UserStatusUtils;
import fm.jihua.kecheng.utils.WeiboAuthHelper;
import fm.jihua.kecheng.utils.UserStatusUtils.UserStatus;

public class RegisterActivity extends BaseMenuActivity implements OnClickListener {

	private DataAdapter mAdapter;

	View loginView;
	View registerView;
	SlidingMenu menu;

	AuthHelper currentAuthHelper;
	RegisterFragment registerFragment;
	LoginFragment loginFragment;
	BaseFragment currentFragment;
	boolean isFirstEnterLoginView = true;

	public static final Intent SERVICE_INTENT = new Intent();
	static {
		SERVICE_INTENT.setComponent(new ComponentName("fm.jihua.kecheng_hs",
				"fm.jihua.kecheng.BeemService"));
	}

	// private IXmppFacade mXmppFacade;
	// private final ServiceConnection mServConn = new LoginServiceConnection();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onEvent(RegisterActivity.this, "enter_regist_view");
		MobclickAgent.updateOnlineConfig(this);
		mAdapter = new DataAdapter(this, new MyDataCallback());
		init();
	}

	void init() {
		setContentView(R.layout.content_frame);
		hideTitleBar();
		getKechengActionBar().setLefttButtonGone();
		initMenu();
//		setListeners();
		registerFragment = new RegisterFragment();
		loginFragment = new LoginFragment();
		switchContentFragment();
		App app = App.getInstance();
		UMFeedbackService.setFeedBackListener(feedbackListener);
		if (app.getToken() != null && app.getMyUserId() != 0) {
			login();
		}
	}

	void initMenu() {
		setBehindContentView(R.layout.menu_frame);
		menu = getSlidingMenu();
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.menu_shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.post(new Runnable() {
			
			@Override
			public void run() {
				menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			}
		});
//		setSlidingActionBarEnabled(true);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new RegisterMenuFragment()).commit();
	}

	@Override
	public void onBackPressed() {
		showMenuOrQuit();
	}
	
	void showMenuOrQuit(){
		SlidingMenu menu = getSlidingMenu();
		if (!menu.isMenuShowing()) {
			toggle();
		} else {
			showQuitDialog();
		}
	}
	
	private void showQuitDialog(){
		new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("是否退出课程格子？")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						finish();
						App app = (App)getApplication();
						app.exit();
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						// 按钮事件
						dialoginterface.dismiss();
					}
				}).show();
	}
	
	/* 消息处理 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(!isKeyLocked){
			if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				onBackPressed();
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
				toggle();
				return true;
			}else {
				return super.onKeyUp(keyCode, event);
			}
		}else{
			return true;
		}
		
	}

//	void setListeners() {
//		actionbar.getLeftTextButton().setOnClickListener(this);
//	}

	public void login() {
		startService(SERVICE_INTENT);
		startMainActivity();
	}

	private void startMainActivity() {
		// App app = (App)getApplication();

		// Intent intent = new Intent(RenrenAuthActivity.this,
		// app.getDefaultCourseView() != R.layout.main_layout ?
		// WeekViewActivity.class : MainActivity.class);
		startActivity(new Intent(this, MenuActivity.class));
		finish();
	}

	public void switchContentFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (getSupportFragmentManager().findFragmentByTag(
				RegisterFragment.class.getName()) != null) {
			transaction.replace(R.id.content_frame, loginFragment,
							LoginFragment.class.getName()).commit();
			currentFragment = loginFragment;
			if(isFirstEnterLoginView){
				MobclickAgent.onEvent(RegisterActivity.this, "enter_login_view");
				isFirstEnterLoginView = false;
			}
			hideTitleBar();
		} else {
			transaction.replace(R.id.content_frame, registerFragment,
							RegisterFragment.class.getName()).commit();
			currentFragment = registerFragment;
			if(registerFragment.isInfoFilled()){
				showTitleBar();
			} else {
				hideTitleBar();
			}
		}
		currentFragment.onShow();
		getSupportFragmentManager().executePendingTransactions();
	}

	void showTips(final String tips) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Hint.showTipsShort(RegisterActivity.this, tips);
			}
		});
	}

	public void processUniversityStudent(SchoolInfo[] schoolInfos) {
		if (schoolInfos != null && schoolInfos.length > 1) {
			Intent intent = new Intent(RegisterActivity.this,
					ChooseRenrenSchoolActivity.class);
			ArrayList<SchoolInfo> schoolInfos1 = new ArrayList<SchoolInfo>(Arrays.asList(schoolInfos));
			intent.putExtra("SCHOOL_INFOS", schoolInfos1);
			registerFragment.startActivityForResult(intent,
					RegisterFragment.CITY);
		} else {
			if (schoolInfos != null && schoolInfos.length > 0) {
//				registerFragment.updateSchoolInfo(schoolInfos[0]);
			}
			registerFragment.changeToConfirmView();
		}
	}

	// private void removeListeners(){
	// if (findViewById(R.id.school) != null) {
	// ((TextView) findViewById(R.id.school)).setOnClickListener(null);
	// ((TextView) findViewById(R.id.department)).setOnClickListener(null);
	// ((TextView) findViewById(R.id.year)).setOnClickListener(null);
	// }
	// }

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		try {
			switch (requestCode) {
			case SsoHandler.DEFAULT_AUTH_ACTIVITY_CODE:
				UIUtil.block(this);
				((WeiboAuthHelper) currentAuthHelper).onActivityResult(
						requestCode, resultCode, data);
				break;
			default:
				if (requestCode != SsoHandler.DEFAULT_AUTH_ACTIVITY_CODE
						&& currentAuthHelper instanceof TencentAuthHelper) {
					((TencentAuthHelper) currentAuthHelper).onActivityResult(
							requestCode, resultCode, data);
				}
				super.onActivityResult(requestCode, resultCode, data);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.renren_button:
			MobclickAgent.onEvent(RegisterActivity.this, "action_login_renren");
			RenrenAuthHelper renrenAuthHelper = new RenrenAuthHelper(this);
			currentAuthHelper = renrenAuthHelper;
			renrenAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
			break;
		case R.id.weibo_button:
			MobclickAgent.onEvent(RegisterActivity.this, "action_login_weibo");
			WeiboAuthHelper weiboAuthHelper = new WeiboAuthHelper(this);
			currentAuthHelper = weiboAuthHelper;
			weiboAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
			break;
		case R.id.tencent_button:
			MobclickAgent.onEvent(RegisterActivity.this, "action_login_qq");
			TencentAuthHelper tencentAuthHelper = new TencentAuthHelper(this);
			currentAuthHelper = tencentAuthHelper;
			tencentAuthHelper.auth(new MySNSCallback(SNSCallback.AUTH));
			break;
		case R.id.menu_textview:
		case R.id.menu:
		case R.id.change_view_text:{
			switchContentFragment();
		}
			break;
		default:
			currentFragment.onClick(v);
			break;
		}
	}

	private class MySNSCallback implements SNSCallback{

		private int mScope = 0;

		public MySNSCallback(int scope) {
            mScope = scope;
        }

		@Override
		public void onComplete(final AuthHelper authHelper, final Object data) {
			switch (mScope){
			case SNSCallback.AUTH:
				UIUtil.block(RegisterActivity.this);
				currentAuthHelper = authHelper;
				mAdapter.loginByOauth(authHelper.getThirdPartId(),
						authHelper.getThirdPartToken(), authHelper.getType());
				break;
			case SNSCallback.GET_USER_INFO:
				registerFragment.setUser((CommonUser) data);
				registerFragment.setType(authHelper.getType());
				if (currentFragment instanceof LoginFragment) {
					switch(currentAuthHelper.getType()){
					case Const.RENREN:
						MobclickAgent.onEvent(RegisterActivity.this, "event_renren_login_to_regist");
						break;
					case Const.QQ:
						MobclickAgent.onEvent(RegisterActivity.this, "event_qq_login_to_regist");
						break;
					case Const.WEIBO:
						MobclickAgent.onEvent(RegisterActivity.this, "event_weibo_login_to_regist");
						break;
					default:
						break;
					}
					switchContentFragment();
				}
				processUniversityStudent(((CommonUser) data).schoolInfos);
				break;
			}
		}

		@Override
		public void onError(AuthHelper authHelper) {
			switch (mScope){
			case SNSCallback.GET_USER_INFO:
				Hint.showTipsLong(RegisterActivity.this, "获取个人信息失败");
				break;
			}
		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}

	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(RegisterActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_LOGIN_BY_OAUTH:
				if (msg.obj == null) {
					Hint.showTipsLong(RegisterActivity.this, "登录出错了，请检查网络配置");
				} else {
					RegistResult result = (RegistResult) msg.obj;
					if (result.success) {
						switch(currentAuthHelper.getType()){
						case Const.RENREN:
							MobclickAgent.onEvent(RegisterActivity.this, "event_login_renren_succeed");
							break;
						case Const.QQ:
							MobclickAgent.onEvent(RegisterActivity.this, "event_login_qq_succeed");
							break;
						case Const.WEIBO:
							MobclickAgent.onEvent(RegisterActivity.this, "event_login_weibo_succeed");
							break;
						default:
							break;
						}
						UserStatusUtils.get().setNewUser(UserStatus.RELOGIN_USER);
						login();
					} else {
						currentAuthHelper.getUserInfo(new MySNSCallback(SNSCallback.GET_USER_INFO));
					}
				}
				break;
			default:
				break;
			}
		}
	}

	FeedBackListener feedbackListener = new FeedBackListener() {
		@Override
		public void onSubmitFB(Activity activity) {
			App app = App.getInstance();
			User user = app.getMyself();
			if (user != null) {
				Map<String, String> contactMap = new HashMap<String, String>();
				contactMap.put("user_id", String.valueOf(app.getMyUserId()));
				contactMap.put("name", user.name);
				UMFeedbackService.setContactMap(contactMap);
				Map<String, String> remarkMap = new HashMap<String, String>();
				remarkMap.put("school", user.school);
				remarkMap.put("department", user.department);
				UMFeedbackService.setRemarkMap(remarkMap);
			}
		}

		@Override
		public void onResetFB(Activity activity,
				Map<String, String> contactMap, Map<String, String> remarkMap) {
		}
	};

//	@Override
//	public void onComplete(AuthHelper helper, CommonUser userInfo) {
//		if (currentFragment instanceof LoginFragment) {
//			switchContentFragment();
//		}
//		processUniversityStudent(userInfo.schoolInfos);
//	}
//
//	@Override
//	public void onError(AuthHelper helper) {
//
//	}
}
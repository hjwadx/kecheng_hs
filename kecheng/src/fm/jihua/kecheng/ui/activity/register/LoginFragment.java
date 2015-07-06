package fm.jihua.kecheng.ui.activity.register;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseMenuActivity;
import fm.jihua.kecheng.ui.fragment.BaseFragment;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.UserStatusUtils;
import fm.jihua.kecheng.utils.UserStatusUtils.UserStatus;

public class LoginFragment extends BaseFragment implements DataCallback {
	EditText emailEditText;
	EditText passwordEditText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.login, container, false);
		findViews(v);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((RegisterActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		initTitleBar();
	}

	void findViews(View v){
		emailEditText = (EditText)v.findViewById(R.id.email);
		passwordEditText = (EditText)v.findViewById(R.id.password);
	}
	
	void initTitleBar(){
//		getActivity().setTitle("登录");
		((BaseMenuActivity)parent).getKechengActionBar().setRightButtonGone();
//		((BaseMenuActivity)parent).getKechengActionBar().setLeftText("注册");
//		findViewById(R.id.action).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Animation shake = AnimationUtils.loadAnimation(parent.getApplicationContext(), R.anim.shake);
		if (TextUtils.isEmpty(emailEditText.getText().toString().trim())) {
			Hint.showTipsShort(parent, "账号不能为空");
			emailEditText.startAnimation(shake);
		}else if(TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {
			Hint.showTipsShort(parent, "密码不能为空");
			passwordEditText.startAnimation(shake);
		} else {
			login(v);
		}
	}
	
	void login(View v){
		MobclickAgent.onEvent(getActivity(), "action_login");
		if (TextUtils.isEmpty(emailEditText.getText().toString()) || TextUtils.isEmpty(passwordEditText.getText().toString())) {
			Hint.showTipsShort(parent, R.string.register_no_account_hint);
			return;
		}
		UIUtil.block(getActivity());
		new DataAdapter(parent, this).login(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
	}

	@Override
	public void callback(Message msg) {
		RegistResult result = (RegistResult) msg.obj;
		if (result != null) {
			if (result.success) {
				MobclickAgent.onEvent(getActivity(), "event_login_succeed");
				UserStatusUtils.get().setNewUser(UserStatus.RELOGIN_USER);
				((RegisterActivity)getActivity()).login();
			}else {
				Hint.showTipsLong(getActivity(), "用户名或密码错误，请再检查一下");
			}
		}else {
			Hint.showTipsLong(getActivity(), "登录失败，是不是网络不给力？");
		}
		UIUtil.unblock(getActivity());
	}
}
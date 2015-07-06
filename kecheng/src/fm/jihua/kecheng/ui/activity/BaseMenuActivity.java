package fm.jihua.kecheng.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.BuildConfig;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.view.KechengActionbar;
import fm.jihua.kecheng.utils.ViewServer;

public class BaseMenuActivity extends fm.jihua.common.ui.BaseMenuActivity implements OnClickListener{
	protected KechengActionbar actionbar;
	protected boolean isKeyLocked;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (BuildConfig.DEBUG) {
			ViewServer.get(this).addWindow(this);
		}
		buildCustomActionBarTitle();
		initTitleBar();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BuildConfig.DEBUG) {
			ViewServer.get(this).removeWindow(this);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (BuildConfig.DEBUG) {
			ViewServer.get(this).setFocusedWindow(this);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		App.getInstance().getHttpQueue().cancelAll(getTag());
	}
	
	public String getTag(){
		return this.getClass().getName();
	}
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		actionbar.setTitle(title);
	}
	
	public void lockKeyInput(boolean isKeyLocked){
		this.isKeyLocked = isKeyLocked;
	}
	
	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);
		actionbar.setTitle(titleId);
	}
	
	public KechengActionbar getKechengActionBar(){
		return actionbar;
	}
	
	public void hideTitleBar(){
		getSupportActionBar().hide();
	}
	
	public void showTitleBar(){
		getSupportActionBar().show();
	}
	
	private void buildCustomActionBarTitle() {
        actionbar = new KechengActionbar(this);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        getSupportActionBar().setCustomView(actionbar, layoutParams);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }
	
	private void initTitleBar(){
		actionbar.getMenuButton().setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu:
			getSlidingMenu().showMenu();
			break;
		default:
			break;
		}
	}
}

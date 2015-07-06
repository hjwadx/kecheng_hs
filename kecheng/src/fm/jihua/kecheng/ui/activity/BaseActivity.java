package fm.jihua.kecheng.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.BuildConfig;
import fm.jihua.kecheng.ui.view.KechengActionbar;
import fm.jihua.kecheng.utils.ViewServer;

public class BaseActivity extends fm.jihua.common.ui.BaseActivity {
	
	protected KechengActionbar actionbar;
	public final static String INTENT_THEME = "theme";
	protected boolean isKeyLocked;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int theme = getIntent().getIntExtra(INTENT_THEME, 0);
		if (theme != 0) {
			setTheme(theme);
		}
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (getSupportActionBar() != null) {
			buildCustomActionBarTitle();
		}
		if (BuildConfig.DEBUG) {
			ViewServer.get(this).addWindow(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BuildConfig.DEBUG) {
			ViewServer.get(this).removeWindow(this);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		App.getInstance().getHttpQueue().cancelAll(getTag());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (BuildConfig.DEBUG) {
			ViewServer.get(this).setFocusedWindow(this);
		}
	}
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		if (actionbar != null) {
			actionbar.setTitle(title);
		}
	}
	
	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);
		if (actionbar != null) {
			actionbar.setTitle(titleId);
		}
	}
	
	public KechengActionbar getKechengActionBar(){
		return actionbar;
	}
	
	public String getTag(){
		return this.getClass().getName();
	}
	
	public void lockKeyInput(boolean isKeyLocked){
		this.isKeyLocked = isKeyLocked;
	}
	
	@Override
	public void onBackPressed() {
		if (!isKeyLocked) {
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (!isKeyLocked) {
			return super.onKeyUp(keyCode, event);
		}
		return true;
	}
	
	public Activity getThis(){
		return this;
	}
	
	private void buildCustomActionBarTitle() {
        actionbar = new KechengActionbar(this);
        actionbar.showBackBtn();
        actionbar.getActionButton().setVisibility(View.GONE);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        getSupportActionBar().setCustomView(actionbar, layoutParams);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        setTitle(getTitle());
    }
		
	public void startActivity(Activity activity, Class<?> cls){
		Intent intent = new Intent(activity, cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); 
		activity.startActivity(intent);
//		activity.finish();
	}
}

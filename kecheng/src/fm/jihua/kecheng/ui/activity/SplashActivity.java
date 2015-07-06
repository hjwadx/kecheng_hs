package fm.jihua.kecheng.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.register.RegisterActivity;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.FileUtils;

public class SplashActivity extends Activity {
	View rootView;
	long minShowSplashTime = 250;
	long startTime;
	public static final String IS_SEND_SD_INFO = "IS_SEND_SD_INFO";
	
	String getTag(){
		return this.getClass().getSimpleName();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startTime = System.currentTimeMillis();
		rootView = getLayoutInflater().inflate(R.layout.act_splash, null);
		setContentView(rootView);
		enterActivity();
		AppLogger.d(getTag(), "onCreate");
	}
	
//	@Override
//	protected void onPostCreate(Bundle savedInstanceState) {
//		super.onPostCreate(savedInstanceState);
//		AppLogger.d(getTag(), "onPostCreate");
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				AppLogger.d(getTag(), "before initAtFirst");
//				long interval = System.currentTimeMillis() - startTime;
//				long delayed = interval > minShowSplashTime ? 100 : minShowSplashTime - interval;
//				try {
//					Thread.sleep(delayed);
//				} catch (InterruptedException e) {
//					AppLogger.printStackTrace(e);
//				}
//				App.getInstance().initAtFirst();
//				AppLogger.d(getTag(), "after initAtFirst");
//				startTime = System.currentTimeMillis();
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						long interval = System.currentTimeMillis() - startTime;
//						long delayed = interval > minShowSplashTime ? 100 : minShowSplashTime - interval;
//						AppLogger.d(getTag(), "delayed = "+delayed);
//						boolean result = rootView.postDelayed(new Runnable() {
//							
//							@Override
//							public void run() {
//								AppLogger.d(getTag(), "before enterActivity");
//								enterActivity();
//								AppLogger.d(getTag(), "after enterActivity");
//							}
//						}, delayed);
//						AppLogger.d(getTag(), "post delayed = "+result);
//					}
//				});
//			}
//		}).start();
//	}
	
	private void enterActivity(){
//		App.mDisplayWidth = Compatibility.getWidth(getWindow().getWindowManager().getDefaultDisplay());
//		App.mDisplayHeight = Compatibility.getHeight(getWindow().getWindowManager().getDefaultDisplay());
		if (TextUtils.isEmpty(App.getInstance().getValue(IS_SEND_SD_INFO))) {
			MobclickAgent.onEvent(SplashActivity.this, "global_device_is_sd_exists", String.valueOf(FileUtils.getInstance().isSDAvailable()));
			App.getInstance().putValue(IS_SEND_SD_INFO, "true");
		}
		if (getIntent() != null) {
			Intent intent = getIntent();
			if (intent.getBooleanExtra("TO_CHAT", false)) {
				MobclickAgent.onEvent(SplashActivity.this, "action_notification_enter_message_view");
				intent.setClass(SplashActivity.this, MenuActivity.class);
				startActivity(intent);
			}else if(intent.getBooleanExtra("TO_EXAMINATION", false)){
				intent.setClass(SplashActivity.this, MenuActivity.class);
				startActivity(intent);
			}else {
				startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
			}
			int widgetType = intent.getIntExtra("widget", 0);
			switch (widgetType) {
			case Const.WIDGET_1X4:
				MobclickAgent.onEvent(SplashActivity.this, "enter_app_through_widget",
						"widget_1*4");
				break;
			case Const.WIDGET_3X4:
				MobclickAgent.onEvent(SplashActivity.this, "enter_app_through_widget",
						"widget_3*4");
				break;
			case Const.WIDGET_4X4:
				MobclickAgent.onEvent(SplashActivity.this, "enter_app_through_widget",
						"widget_4*4");
				break;
			default:
			}
		}else {
			startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
		}
		finish();
	}
}

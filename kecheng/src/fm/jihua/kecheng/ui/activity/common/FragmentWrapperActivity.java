package fm.jihua.kecheng.ui.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.utils.AppLogger;

public class FragmentWrapperActivity extends BaseActivity {
	
	public static final String INTENT_CLASS_NAME = "class_name";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
		try {
			String className = getIntent().getStringExtra(INTENT_CLASS_NAME);
			Class<?> cls = Class.forName(className);    // 获取Class类的对象的方法之二
			Fragment fragment = (Fragment)cls.newInstance();
			
			if (getIntent() != null) {
				fragment.setArguments(getIntent().getExtras());
			}

			getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
		} catch (Exception e) {
			AppLogger.printStackTrace(e);
		}
	}
	
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if(getSupportFragmentManager().findFragmentById(R.id.content_frame) != null){
				getSupportFragmentManager().findFragmentById(R.id.content_frame).onActivityResult(requestCode, resultCode, data);
			}
		}
	}
}

package fm.jihua.kecheng.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.utils.Const;

public class AboutActivity extends BaseActivity {
	public static final int UPDATE_LOG = 1000;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setTitle("关于课程格子");
		initTitlebar();
		init();
	}
	
	void init(){
		((TextView)findViewById(R.id.version)).setText(Const.getAppVersionName(this));
		((View)findViewById(R.id.about_parent)).setOnClickListener(listener);
	}
	
	OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.about_parent:
				Intent intent = new Intent(AboutActivity.this, TextViewerActivity.class);
				intent.putExtra(Const.INTENT_TEXT_CONTENT, getResources().getString(R.string.update_log));
				intent.putExtra(Const.INTENT_TITLE_CONTENT, "更新日志");
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};
	
	void initTitlebar(){
		getKechengActionBar().getRightButton().setVisibility(View.GONE);
	}
	
//	void showConfirmDialog(final String message){
//		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == -1) {
//					
//				}
//				dialog.dismiss();
//			}
//		};
//		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")    
//        	.setMessage(message).setPositiveButton("确定", clickListener)
//        	.setNegativeButton("取消", clickListener).create();  
//		dialog.show();  
//	}
}

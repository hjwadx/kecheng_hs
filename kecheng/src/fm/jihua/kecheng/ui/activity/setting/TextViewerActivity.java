package fm.jihua.kecheng.ui.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.utils.Const;

public class TextViewerActivity extends BaseActivity{
	
	String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.help);
			content = getIntent().getStringExtra(Const.INTENT_TEXT_CONTENT);
			((TextView)findViewById(R.id.content)).setText(content);
			initTitlebar();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	void initTitlebar(){
		setTitle(getIntent().getStringExtra(Const.INTENT_TITLE_CONTENT));
		getKechengActionBar().getRightButton().setVisibility(View.GONE);
	}

}

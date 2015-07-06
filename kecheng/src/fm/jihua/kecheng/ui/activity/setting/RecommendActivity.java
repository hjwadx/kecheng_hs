package fm.jihua.kecheng.ui.activity.setting;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.umeng.newxp.controller.ExchangeDataService;
import com.umeng.newxp.view.ExchangeViewManager;

import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.BaseActivity;

public class RecommendActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend);
		ListView l1 = (ListView) findViewById(R.id.list_1);
		ViewGroup vg1 = (ViewGroup) findViewById(R.id.father1);
		ExchangeDataService exchangeDataService1 = new ExchangeDataService();
//		exchangeDataService1.setKeywords("game");
//		exchangeDataService1.autofill = 1;
		new ExchangeViewManager(this, exchangeDataService1).addView(vg1, l1);
		initTitlebar();
	}
	
	void initTitlebar(){
		setTitle("推荐应用");
		getKechengActionBar().getRightButton().setVisibility(View.GONE);
	}
}

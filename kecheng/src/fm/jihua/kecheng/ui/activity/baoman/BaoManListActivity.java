package fm.jihua.kecheng.ui.activity.baoman;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.tinymission.rss.FeedActivity;
import com.tinymission.rss.Item;

import fm.jihua.kecheng.ui.activity.common.FragmentWrapperActivity;
import fm.jihua.kecheng.ui.view.KechengActionbar;
import fm.jihua.kecheng_hs.R;

public class BaoManListActivity extends FeedActivity {
	
	protected KechengActionbar actionbar;

	RadioGroup radioGroup;
	final String HOT_URL = "http://baozoumanhua.com/groups/19/digest.xml";
	final String NEW_URL = "http://baozoumanhua.com/groups/1/rss";
	String url = HOT_URL;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getSupportActionBar() != null) {
			buildCustomActionBarTitle();
			
		}
		initView();
	}
	
	private void initView() {
		((RadioButton)(findViewById(R.id.radiobtn_hot))).setChecked(true);
		findViewById(R.id.select_button_parent).setVisibility(View.VISIBLE);
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
	}

	@Override
	public String getFeedUrl() {
		return url;
	}
	
	protected void onFeedItemClick(Item item) {
		Intent detailIntent = new Intent(this, FragmentWrapperActivity.class);
        detailIntent.putExtra(ArticleDetailFragment.KEY, item);
        detailIntent.putExtra(FragmentWrapperActivity.INTENT_CLASS_NAME, ArticleDetailFragment.class.getName());
        startActivity(detailIntent);
	}

	private void buildCustomActionBarTitle() {
        actionbar = new KechengActionbar(this);
        actionbar.showBackBtn();
        actionbar.getActionButton().setVisibility(View.GONE);
        actionbar.setTitle("暴走漫画");
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        getSupportActionBar().setCustomView(actionbar, layoutParams);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }
	
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.radiobtn_hot:
				url = HOT_URL;
				refresh();
				break;
			case R.id.radiobtn_new:
				url = NEW_URL;
				refresh();
				break;

			default:
				break;
			}
		}
	};
}

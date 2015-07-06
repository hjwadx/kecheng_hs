package fm.jihua.kecheng.ui.activity.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Leaderboard;
import fm.jihua.kecheng.rest.entities.Ranking;
import fm.jihua.kecheng.rest.entities.RankingsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.course.CourseActivity;
import fm.jihua.kecheng.ui.adapter.RankingAdapter;
import fm.jihua.kecheng.ui.helper.DialogUtils;
import fm.jihua.kecheng.utils.Const;

public class RankingsActivity extends BaseActivity{
	
	private DataAdapter mDataAdapter;
	RankingAdapter adapter;
	ListView listView;
	Leaderboard leaderboard;
	final List<Ranking> rankings = new ArrayList<Ranking>();
//	private final String cachePrefix = "Leaderboard";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		leaderboard = (Leaderboard) getIntent().getSerializableExtra("LEADERBOARD");
		setContentView(R.layout.rankings);
		initTitlebar();
		initViews();
		setListeners();
	}
	
	private void initViews() {
		adapter = new RankingAdapter(rankings);
		listView = (ListView) findViewById(R.id.list1);
		listView.setAdapter(adapter);
		listView.setDivider(null);
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		mDataAdapter.getRankings(leaderboard.id);
		UIUtil.block(this);
	}
	
	private void refershListView(){
		adapter.notifyDataSetChanged();
		getKechengActionBar().getRightTextButton().setVisibility(View.VISIBLE);
	}
	
	public int getListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			int widthSpec = MeasureSpec.makeMeasureSpec(listView.getMeasuredWidth()-(listView.getPaddingLeft()+listView.getPaddingRight()), MeasureSpec.AT_MOST);
			int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			listItem.measure(widthSpec, heightSpec);
			totalHeight += listItem.getMeasuredHeight();
		}

		return totalHeight;
	}
	
	private void setListeners(){
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				try {
					Ranking ranking = rankings.get(position);
					Intent intent = new Intent();
					intent.putExtra(Const.BUNDLE_KEY_COURSE, ranking.course);
					intent.setClass(RankingsActivity.this, CourseActivity.class);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	void initTitlebar() {
		setTitle(leaderboard.title);
		getKechengActionBar().setRightText("分享");
		getKechengActionBar().getRightTextButton().setVisibility(View.GONE);
		getKechengActionBar().getRightTextButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String title = "";
				String school = "";
				if (leaderboard.scope == 1) {
					title = leaderboard.title.replace("全校", "");
					if (rankings.size() > 0) {
						school = rankings.get(0).course.school_name;
					}
				}else if(leaderboard.scope == 10) {
					title = leaderboard.title.replace("全国", "");
					school = "全国";
				}
				title += "排行榜";

				ListView view = (ListView) getLayoutInflater().inflate(R.layout.ranking_listview, null);
				view.setDivider(null);
				view.setAdapter(new RankingAdapter(rankings));
				int height = getListViewHeightBasedOnChildren(view);
				int widthMeasureSpec = MeasureSpec.makeMeasureSpec(Compatibility.getWidth(getThis().getWindowManager().getDefaultDisplay()), MeasureSpec.EXACTLY);
				int heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
				view.measure(widthMeasureSpec, heightMeasureSpec);
				view.layout(0, 0, view.getMeasuredWidth(), height);
				
				DialogUtils.showShareRankingDialog(RankingsActivity.this, view, title, school, leaderboard.scope);
			}
		});
	}
	
	private class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_LEADERBOARD:
				RankingsResult result = (RankingsResult) msg.obj;
				if(result !=null && result.success == true){
					if (result.rankings.length > 0) {
						rankings.clear(); 
						rankings.addAll(Arrays.asList(result.rankings));
						refershListView();
					}else {
						findViewById(R.id.empty).setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
					}
				}
			    UIUtil.unblock(RankingsActivity.this);
				break;
			default:
				break;
			}
		}
	}
}

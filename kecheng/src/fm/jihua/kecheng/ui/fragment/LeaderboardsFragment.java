package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Leaderboard;
import fm.jihua.kecheng.rest.entities.LeaderboardsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.plugin.RankingsActivity;
import fm.jihua.kecheng.ui.widget.AutoTableLayout;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class LeaderboardsFragment extends BaseFragment{
	
	AutoTableLayout schoolLeaderboards, globalLeaderboards;
	private DataAdapter mDataAdapter;
	private LayoutInflater inflater;
	private final String cacheName = "Leaderboards";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;
		try {
			view = inflater.inflate(R.layout.leaderboards, container, false);
			initTitlebar();
		}catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, e.getMessage(), e);
		}
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initViews();
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initTitlebar();
	}
	
	private void initViews() {
		schoolLeaderboards = (AutoTableLayout) findViewById(R.id.schoolleaderboards);
		globalLeaderboards = (AutoTableLayout) findViewById(R.id.globalleaderboards);
		mDataAdapter = new DataAdapter(parent, new MyDataCallback());
		mDataAdapter.getLeaderboards();
		UIUtil.block(parent);
	}


	public void initTitlebar() {
		getActivity().setTitle("排行榜");
		findViewById(R.id.action).setVisibility(View.GONE);
	}
	
	private class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_LEADERBOARDS:
			//得到数据，更新view
				LeaderboardsResult result = (LeaderboardsResult) msg.obj;
				if(result !=null && result.success == true && result.leaderboards.length > 0){
					List<Leaderboard> leaderboards = new ArrayList<Leaderboard>(); 
					leaderboards.addAll(Arrays.asList(result.leaderboards));
				    refreshView(leaderboards);
				}
			    UIUtil.unblock(parent);
				break;
			default:
				break;
			}
		}
	}
	
	private void refreshView(List<Leaderboard> leaderboards) {
		try {
			inflater = LayoutInflater.from(parent);
			CachedImageView cacheImageView;
			TextView txetView;
			ViewGroup linearLayout;
			globalLeaderboards.removeAllViews();
			schoolLeaderboards.removeAllViews();
			final List<String> highlightRankings = ((App)getApplication()).getUnHandledParams(Const.CONFIG_PARAM_HIGHLIGHT_RANKING);
			for(final Leaderboard leaderboard:leaderboards){
				linearLayout = (ViewGroup) inflater.inflate(R.layout.leaderboard_item,null);
				if(leaderboard.scope == 10){
					globalLeaderboards.addView(linearLayout);
				} else {
					schoolLeaderboards.addView(linearLayout);
				}
				if (highlightRankings.contains(leaderboard.title)) {
					linearLayout.findViewById(R.id.highlight).setVisibility(View.VISIBLE);
				}
				linearLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (highlightRankings.contains(leaderboard.title)) {
							App app = (App) getApplication();
							app.handleConfigParam(Const.CONFIG_PARAM_HIGHLIGHT_RANKING,leaderboard.title);
						}
						Intent intent = new Intent(parent,RankingsActivity.class);
						intent.putExtra("LEADERBOARD", leaderboard);
						startActivity(intent);		
					}
				});
				cacheImageView = (CachedImageView) linearLayout.findViewById(R.id.leaderborader_image);
				cacheImageView.setLoadingBitmap(null);
				cacheImageView.setFadeIn(false);
				cacheImageView.setImageURI(Uri.parse(leaderboard.icon_url));
				cacheImageView.setBackgroundColor(Color.TRANSPARENT);
				txetView = (TextView) linearLayout.findViewById(R.id.leaderboard_name);	
				txetView.setText(leaderboard.title);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}

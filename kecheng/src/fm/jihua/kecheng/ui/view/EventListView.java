package fm.jihua.kecheng.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.commonsware.cwac.endless.LoadMoreAdapter;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.rest.entities.EventsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseMenuActivity;
import fm.jihua.kecheng.ui.activity.EventActivity;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.adapter.EventAdapter;
import fm.jihua.kecheng.ui.fragment.MenuFragment;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;

public class EventListView extends BaseViewGroup{
	LoadMoreAdapter adapter;
	ListView listView;
	BiasHintView emptyView;
	Fragment fragment;
	private DataAdapter mDataAdapter;
	List<Event> mEvents = new ArrayList<Event>();
	int mType;
	int page = 1;
	public static final int ALL_EVENT = 1;
	public static final int MY_EVENT = 2;
	
	int per_page = Const.DATA_COUNT_PER_REQUEST;

	public EventListView(Fragment fragment, int type) {
		super(fragment);
		this.fragment = fragment;
		mType = type;
		mDataAdapter = new DataAdapter(parent, new MyDataCallback());
		initViews();
	}
	
	void initViews(){
		parent.getLayoutInflater().inflate(R.layout.events_list, this);
        initTitleBar();
        findViews();
        setListeners();
        refresh();
	}
	
	void setListeners() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long id) {
				Intent intent = new Intent(parent, EventActivity.class);
				intent.putExtra("EVENT", mEvents.get(position));
				fragment.startActivity(intent);
			}
		});
	}
	
	void findViews() {
		listView = (ListView) findViewById(R.id.events_list);
		emptyView = (BiasHintView) findViewById(R.id.empty);
	}

	@Override
	public void setData(Object data) {
		// TODO Auto-generated method stub
		EventAdapter adapter = (EventAdapter) listView.getAdapter();
		adapter.setData(mEvents);
	}

	@Override
	public void refreshUI() {
		if(mType == ALL_EVENT){
			adapter.notifyDataSetChanged();
		} else {
			refresh();
		}
	}

	@Override
	public void initTitleBar() {
		parent.setTitle(mType == ALL_EVENT ? "全部活动" : "我的活动");
		((BaseMenuActivity)parent).getKechengActionBar().getRightButton().setVisibility(View.VISIBLE);
    	((BaseMenuActivity)parent).getKechengActionBar().setRightImage(mType == ALL_EVENT ? R.drawable.activity_icon_myacttivity : R.drawable.menubar_btn_icon_all);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 暂时没用到
	}
	
	
	public void refresh() {
		mEvents.clear();
		page = 1;
		if(mType == ALL_EVENT){
			UIUtil.block((Activity)getContext());
			mDataAdapter.getEvents(page, per_page);
			emptyView.setText(R.string.empty_all_events_list);
		} else {
			mEvents = App.getInstance().getDBHelper().getEvents(App.getInstance().getUserDB());
			emptyView.setText(R.string.empty_my_events_list);
			refreshEmpty();
		}
		adapter = new LoadMoreEventAdapter(parent, mEvents, mType);
		listView.setDivider(null);
		listView.setAdapter(adapter);
		if(mType == MY_EVENT){
			getMore();
		}
	}
	
	void refreshEmpty(){
		if(mEvents.size() > 0){
			listView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		} else {
			listView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}
	
	
	class LoadMoreEventAdapter extends LoadMoreAdapter {
		EventAdapter adapter;

		LoadMoreEventAdapter(Activity context, List<Event> events, int category) {
			super(context, new EventAdapter(context, events, category), R.layout.pending_large, R.layout.load_more_large);
			adapter = (EventAdapter) getWrappedAdapter();
			setRunInBackground(false);
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if(mType == EventListView.ALL_EVENT){
				mDataAdapter.getEvents(++page, per_page);
			} else {
				++page;
				getMore();
			}
			return true;
		}

		@Override
		protected void appendCachedData() {
		}
	}
	
	void getMore(){
		parent.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				post(new Runnable() {
					@Override
					public void run() {
						if(page != 1){
							EventAdapter snsAdapter = ((LoadMoreEventAdapter) adapter).adapter;
							snsAdapter.setCount();
						}
						if (mEvents.size() <= per_page*page){
							adapter.stopAppending();
						} else {
							adapter.restartAppending();
						}
						adapter.onDataReady();
					}
				});
			}
		});
	}
	
	private class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_EVENT: {
				UIUtil.unblock((Activity)getContext());
				EventsResult result = (EventsResult) msg.obj;
				if (result == null || !result.success) {
					if(result == null || result.finished){
						Hint.showTipsShort(parent, "获取活动失败");
					} else {
						UIUtil.block((Activity)getContext());
					}
				} else {
//					if(result.finished && result.events != null && result.events.length > 0){
//						Hint.showTipsShort(parent, "获取活动成功");
//					}
					if(page == 1){
						mEvents.clear();
						mEvents.addAll(Arrays.asList(result.events));
						refreshEmpty();
						adapter.notifyDataSetChanged();
						if (result.events.length > 0 && result.events[0] != null) {
							App.getInstance().saveEventLastLocalId(result.events[0].id);
						}
						App.getInstance().putBooleanSharedValue(MenuActivity.HIGHLIGHT_KEY_EVENT, false);
						getContext().sendBroadcast(new Intent(MenuFragment.INTENT_ACTION_REFRESH));
					} else {
						mEvents.addAll(Arrays.asList(result.events));
						adapter.notifyDataSetChanged();
					}
					if(result.events != null && result.events.length >= per_page){
						adapter.restartAppending();
					} else {
						adapter.stopAppending();
					}
					adapter.onDataReady();
				}
			}
				break;
			default:
				break;
			}
		}
	}

}

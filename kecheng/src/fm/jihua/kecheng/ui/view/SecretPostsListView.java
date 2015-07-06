package fm.jihua.kecheng.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.commonsware.cwac.endless.LoadMoreAdapter;

import fm.jihua.common.ui.widget.PullDownView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.SecretPostsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.plugin.SecretPostCommentActivity;
import fm.jihua.kecheng.ui.adapter.SecretPostAdapter;
import fm.jihua.kecheng.ui.fragment.SecretPostFragment;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;

public class SecretPostsListView extends ListView implements DataCallback {
	Activity parent;
	final List<SecretPost> secretPosts = new ArrayList<SecretPost>();
	LoadMoreAdapter adapter;
	int page = 1;
	String emptyDataMessage = "还没有人对树洞说话哦，你有什么秘密对树洞说吗？";
	private DataAdapter dataAdapter;
	private final int limit = Const.DATA_COUNT_PER_REQUEST;
	
	SecretPostFragment postFragment;
	
	public SecretPostsListView(Context context) {
		super(context);
	}

	public SecretPostsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setFragment(SecretPostFragment postFragment){
		this.postFragment = postFragment;
	}
	
	public void init(){
		parent = (Activity)getContext();
		adapter = new LoadMoreUsersAdapter(parent, secretPosts);
		dataAdapter = new DataAdapter(parent, this);
		setAdapter(adapter);
		setBackgroundResource(R.color.app_background);
		setOnItemClickListener(new MyOnItemClickListener());
	}
	
	public void getData(boolean cache){
		page = 1;
//		secretPosts.clear();
//		adapter.restartAppending();
		adapter.stopAppending();
		adapter.notifyDataSetChanged();
		dataAdapter.getSecretPosts(cache, page, limit);
	}
	
	public void setEmptyDataMessage(String message){
		this.emptyDataMessage = message;
	}
	
	public void insertData(List<SecretPost> posts){
		secretPosts.addAll(0, posts);
		adapter.notifyDataSetChanged();
		saveSecretPostArray(secretPosts);
	}
	
	public void modifyData(int position, SecretPost secretPost){
		secretPosts.set(position, secretPost);
		adapter.notifyDataSetChanged();
		saveSecretPostArray(secretPosts);
	}
	
	void saveSecretPostArray(List<SecretPost> secretPosts){
		
		//update create_at date
		if(secretPosts!=null &&secretPosts.size()>0){
			SecretPost secretPost = secretPosts.get(0);
			App.getInstance().setPostLastReadId(secretPost.id);
		}
		
		SecretPostsResult secretPostsResult = new SecretPostsResult();
		secretPostsResult.success = true;
		secretPostsResult.secret_posts = secretPosts.toArray(new SecretPost[secretPosts.size()]);
	}
	
	class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> listView, View arg1, int position,
				long arg3) {
			SecretPost post = (SecretPost) listView.getAdapter().getItem(position);
			Intent intent = new Intent(getContext(), SecretPostCommentActivity.class);
			intent.putExtra("POSITION", position);
			intent.putExtra("SECRET_POST", post);
			postFragment.startActivityForResult(intent, Const.INTENT_FOR_COMMENT);
//			finish();
		}
	}
	
	class LoadMoreUsersAdapter extends LoadMoreAdapter {
		SecretPostAdapter adapter;
	    LoadMoreUsersAdapter(Activity context, List<SecretPost> data) {
	    	super(context, new SecretPostAdapter(context, data), R.layout.pending_large, 
	    			R.layout.load_more_large);
	      adapter = (SecretPostAdapter) getWrappedAdapter();
	      setRunInBackground(false);
	    }
	    
	    @Override
	    protected boolean cacheInBackground() throws Exception {
    		dataAdapter.getSecretPosts(false, ++page, limit);
    		return true;
	    }
	    
	    @Override
	    protected void appendCachedData() {
	    }
	  }
	
	@Override
	public void callback(Message msg) {
		switch (msg.what) {
		case DataAdapter.MESSAGE_GET_SECRET_POSTS:
			SecretPostsResult result = (SecretPostsResult) msg.obj;
			if (result != null && result.success) {
				boolean anyMore = result.secret_posts.length >= limit;
				if (page == 1) {
					adapter.restartAppending();
					secretPosts.clear();
					if (result.secret_posts.length == 0 && emptyDataMessage != null) {
						Hint.showTipsShort(getContext(), emptyDataMessage);
					}
				}
				if(!anyMore){
					adapter.stopAppending();
					if (page != 1) {
						Hint.showTipsShort(getContext(), "没有更多消息了");
					}
				}
//				users.clear();
				secretPosts.addAll(Arrays.asList(result.secret_posts));
			}else if(result == null || result.finished){
				Hint.showTipsShort(parent, "获取数据失败");
			}
			if (page == 1) {
				((PullDownView)parent.findViewById(R.id.listbase)).endUpdate("");
			}
			if(secretPosts!=null &&secretPosts.size()>0){
				SecretPost secretPost = secretPosts.get(0);
				App.getInstance().putBooleanSharedValue(MenuActivity.HIGHLIGHT_KEY_SECRET_POST, false);
				App.getInstance().setPostLastReadId(secretPost.id);
			}
			adapter.onDataReady();
			break;

		default:
			break;
		}
	}
}

package fm.jihua.kecheng.ui.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.commonsware.cwac.endless.LoadMoreAdapter;

import fm.jihua.common.ui.widget.PullDownView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.SecretPostComment;
import fm.jihua.kecheng.rest.entities.SecretPostCommentsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.plugin.EditMessageActivity;
import fm.jihua.kecheng.ui.adapter.SecretPostCommentAdapter;
import fm.jihua.kecheng.ui.adapter.SecretPostCommentAdapter.OnReplyClickCallbackListener;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;

public class SecretPostCommentsListView extends ListView implements DataCallback {
	Activity parent;
	final List<SecretPostComment> secretPostComments = new ArrayList<SecretPostComment>();
	LoadMoreAdapter adapter;
	int page = 1;
	SecretPost post;
	String emptyDataMessage;
	private DataAdapter dataAdapter;
	private final int limit = Const.DATA_COUNT_PER_REQUEST;
	
	public SecretPostCommentsListView(Context context) {
		super(context);
	}

	public SecretPostCommentsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void init(SecretPost post){
		this.post = post;
		parent = (Activity)getContext();
		adapter = new LoadMoreUsersAdapter(parent, secretPostComments);
		dataAdapter = new DataAdapter(parent, this);
		setAdapter(adapter);
//		setOnItemClickListener(new MyOnItemClickListener());
	}
	
	public void getData(boolean cache){
		page = 1;
//		secretPosts.clear();
//		adapter.restartAppending();
		if (secretPostComments.size() == 0) {
			secretPostComments.add(null);
		}
		adapter.stopAppending();
		adapter.notifyDataSetChanged();
		dataAdapter.getSecretPostComments(post.id, page, limit);
	}
	
	public void setEmptyDataMessage(String message){
		this.emptyDataMessage = message;
	}
	
	public void insertData(List<SecretPostComment> posts){
		secretPostComments.addAll(1, posts);
		adapter.notifyDataSetChanged();
		
		//控制长度，手动去掉“空”提醒
		if (secretPostComments != null && secretPostComments.size() == 2) {
			adapter.stopAppending();
		}
	}
	
//	class MyOnItemClickListener implements OnItemClickListener{
//
//		@Override
//		public void onItemClick(AdapterView<?> listView, View arg1, int position,
//				long arg3) {
//			SecretPost post = (SecretPost) listView.getAdapter().getItem(position);
//			Intent intent = new Intent(getContext(), SecretPostCommentActivity.class);
//			intent.putExtra("USER", post);
//			getContext().startActivity(intent);
////			finish();
//		}
//	}
	
	int loadMoreResource = R.layout.load_more_large;
	
	class LoadMoreUsersAdapter extends LoadMoreAdapter {
		SecretPostCommentAdapter adapter;
	    LoadMoreUsersAdapter(Activity context, List<SecretPostComment> data) {
	    	super(context, new SecretPostCommentAdapter(context, data, post), R.layout.pending_large, 
	    			loadMoreResource);
	      adapter = (SecretPostCommentAdapter) getWrappedAdapter();
			adapter.setOnReplyClickCallbackListener(new OnReplyClickCallbackListener() {

				@Override
				public void onClick(SecretPostComment comment) {
					Intent intent = new Intent(parent,
							EditMessageActivity.class);
					intent.putExtra(EditMessageActivity.INTENT_STR,
							EditMessageActivity.TYPE_SECRET_POST_COMMENT_REPLY);
					intent.putExtra("SECRET_POST", post);
					intent.putExtra("SECRET_POST_COMMEN", comment);
					parent.startActivityForResult(intent, Const.INTENT_DEFAULT);
				}

			});
	      setRunInBackground(false);
	    }
	    
	    @Override
	    protected boolean cacheInBackground() throws Exception {
    		dataAdapter.getSecretPostComments(post.id, ++page, limit);
    		return true;
	    }
	    
	    @Override
	    protected void appendCachedData() {
	    }
	    
	    //方法重写，分条件去掉点击事件
		@Override
		protected View getLoadMoreView(ViewGroup parent) {
			// TODO Auto-generated method stub
			if(loadMoreResource == R.layout.load_more_no_more)
				return ((LayoutInflater)this.getContext().getSystemService("layout_inflater")).inflate(loadMoreResource, parent, false);
			else
				return super.getLoadMoreView(parent);
		}
	  }
	
	@Override
	public void callback(Message msg) {
		switch (msg.what) {
		case DataAdapter.MESSAGE_GET_SECRET_POST_COMMENTS:
			SecretPostCommentsResult result = (SecretPostCommentsResult) msg.obj;
			boolean key_empty = false;
			if (result != null && result.success) {
				boolean anyMore = result.comments.length >= limit;
				post.comments_count = result.secret_post.comments_count;
				if (page == 1) {
					adapter.restartAppending();
					secretPostComments.clear();
					secretPostComments.add(null);
					if (result.comments.length == 0) {
						key_empty = true;
						if (emptyDataMessage != null)
							Hint.showTipsShort(getContext(), emptyDataMessage);
					}
				}
				if(!anyMore){
					//通过反射来改变父类中的res，更换布局文件
					if (!key_empty){
						adapter.stopAppending();
					}
					else{
						Class<?> superclass = adapter.getClass().getSuperclass();
						loadMoreResource = R.layout.load_more_no_more;
						try {
							Field field = superclass.getDeclaredField("loadMoreResource");
							field.setAccessible(true);
							field.setInt(adapter, loadMoreResource);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (page != 1) {
						Hint.showTipsShort(getContext(), "没有更多评论了");
					}
				}
//				users.clear();
				secretPostComments.addAll(Arrays.asList(result.comments));
			}else if(result == null || result.finished){
				Hint.showTipsShort(parent, "获取数据失败");
			}
			if (page == 1) {
				((PullDownView)parent.findViewById(R.id.listbase)).endUpdate("");
			}
			adapter.onDataReady();
			post(new Runnable() {
				
				@Override
				public void run() {
					setListViewHeightBasedOnChildren();
				}
			});
			break;

		default:
			break;
		}
	}
	
	public void setListViewHeightBasedOnChildren() {
		ListView listView = this;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		int count = listAdapter.getCount();
		for (int i = 0; i < count; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			int widthSpec = MeasureSpec.makeMeasureSpec(listView.getMeasuredWidth()-(listView.getPaddingLeft()+listView.getPaddingRight()), MeasureSpec.AT_MOST);
			int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			listItem.measure(widthSpec, heightSpec);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.height += ImageHlp.changeToSystemUnitFromDP(parent, 5);// if
																	// without
																	// this
																	// statement,the
																	// listview
																	// will be a
																	// little
																	// short
		listView.setLayoutParams(params);
	}
}

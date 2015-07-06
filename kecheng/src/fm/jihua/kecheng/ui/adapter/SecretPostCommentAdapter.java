package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.common.utils.TimeHelper;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.SecretPostComment;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.widget.CachedImageView;

public class SecretPostCommentAdapter extends BaseAdapter {

	final List<SecretPostComment> comments;
	final SecretPost post;
	DataAdapter mDataAdapter;
	Activity mActivity;
	
	final int POST = 0;
	final int COMMENT = 1;

	public SecretPostCommentAdapter(Activity parent, List<SecretPostComment> posts, SecretPost post) {
		this.mActivity = parent;
		this.comments = posts;
		this.post = post;
	}

	@Override
	public int getCount() {
		return this.comments.size();
	}

	@Override
	public Object getItem(int position) {
		return this.comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position > 0 ? COMMENT : POST;
	}
	

	@Override
	public View getView(int position, View convertView,
			final ViewGroup parent) {
		try {
			int type = getItemViewType(position);
			if (convertView == null) {
				convertView = initView(parent, type);
			}
			BaseViewHolder viewHolder = (BaseViewHolder) convertView.getTag();
			if (type == POST) {
				viewHolder.setView(parent.getContext(), post);
			}else {
				final SecretPostComment post = comments.get(position);
				viewHolder.setView(parent.getContext(), post);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
	
	View initView(ViewGroup parent, int type){
		View convertView = null;
		if (type == POST) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.secret_post_comment_post_item, parent, false);
			PostViewHolder holder = new PostViewHolder();
			holder.avatar = (CachedImageView) convertView.findViewById(R.id.avatar);
			holder.avatar.setFadeIn(false);
			holder.avatar.setCorner(true);
			holder.name = (TextView) convertView.findViewById(R.id.name);
//			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.comments = (TextView) convertView.findViewById(R.id.comments);
			convertView.setTag(holder);
		}else {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.secret_post_comment_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.avatar = (CachedImageView) convertView.findViewById(R.id.avatar);
			holder.avatar.setFadeIn(false);
			holder.avatar.setCorner(true);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.floor = (TextView) convertView.findViewById(R.id.floor);
			holder.reply = convertView.findViewById(R.id.reply);
			convertView.setTag(holder);
		}
		return convertView;
	}
	
	static abstract class BaseViewHolder{
		abstract void setView(Context context, Object data);
	}
	
	static class ViewHolder extends BaseViewHolder{
		CachedImageView avatar;
		TextView name;
		TextView content;
		TextView time;
		TextView floor;
		View reply;
		@Override
		void setView(Context context, Object data) {
			final SecretPostComment comment = (SecretPostComment) data;
			if (comment != null) {
				avatar.setImageURI(Uri.parse(comment.avatar));
				name.setText(comment.name);
				String contentString = comment.reply == null ? comment.content : "回复" + comment.reply.floor_num + "楼:" + comment.content;
				content.setText(contentString);
				time.setText(TimeHelper.getEarlyTime(comment.created_at*1000));
				floor.setText(comment.floor_num + "楼");
				reply.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (onReplyClickCallbackListener != null) {
							onReplyClickCallbackListener.onClick(comment);
						}
					}
				});
			}
		}
	}
	
	static class PostViewHolder extends BaseViewHolder{
		CachedImageView avatar;
		TextView name;
		TextView content;
//		TextView time;
		TextView comments;
		@Override
		void setView(Context context, Object data) {
			SecretPost post = (SecretPost) data;
			if (post != null) {
				avatar.setImageURI(Uri.parse(post.avatar));
				name.setText(post.name);
				content.setText(post.content);
//				time.setText(TimeHelper.getEarlyTime(post.created_at*1000));
				comments.setText(String.valueOf(post.comments_count)+"条评论");
			}
		}
	}
	
	public interface OnReplyClickCallbackListener {
		public void onClick(SecretPostComment comment);
	}

	private static OnReplyClickCallbackListener onReplyClickCallbackListener;

	public void setOnReplyClickCallbackListener(
			OnReplyClickCallbackListener onReplyClickCallbackListener) {
		this.onReplyClickCallbackListener = onReplyClickCallbackListener;
	}
}

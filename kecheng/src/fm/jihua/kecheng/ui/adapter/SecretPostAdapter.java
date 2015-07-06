package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.common.utils.TimeHelper;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.widget.CachedImageView;

public class SecretPostAdapter extends BaseAdapter {

	final List<SecretPost> posts;
	DataAdapter mDataAdapter;
	Activity mActivity;

	public SecretPostAdapter(Activity parent, List<SecretPost> posts) {
		this.mActivity = parent;
		this.posts = posts;
	}

	@Override
	public int getCount() {
		return this.posts.size();
	}

	@Override
	public Object getItem(int position) {
		return this.posts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView,
			final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.secret_post_item, parent, false);
			holder = new ViewHolder();
			holder.avatar = (CachedImageView) convertView.findViewById(R.id.avatar);
			holder.avatar.setFadeIn(false);
			holder.avatar.setCorner(true);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.comments = (TextView) convertView.findViewById(R.id.comments);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			final SecretPost post = posts.get(position);
			holder.avatar.setImageURI(Uri.parse(post.avatar));
			holder.name.setText(post.name);
			holder.content.setText(post.content);
			holder.time.setText(TimeHelper.getEarlyTime(post.created_at*1000));
			holder.comments.setText(String.valueOf(post.comments_count) + "条评论");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
	
	static class ViewHolder{
		CachedImageView avatar;
		TextView name;
		TextView content;
		TextView time;
		TextView comments;
	}
}

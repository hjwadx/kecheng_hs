package fm.jihua.kecheng.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Avatar;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class ImageAdapter extends BaseAdapter{

	ArrayList<Avatar> avatars;
	Context context;
	
	public ImageAdapter(Context context, ArrayList<Avatar> avatars) {
		this.context = context;
		this.avatars = avatars;
	}

	@Override
	public int getCount() {
		return avatars.size();
	}

	@Override
	public Object getItem(int position) {
		return avatars.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Avatar avatar = (Avatar) getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_item, parent, false);
			holder = new ViewHolder();
			holder.ivPhoto = (CachedImageView) convertView.findViewById(R.id.ivPhoto);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		try{
			holder.ivPhoto.setImageURI(Uri.parse(avatar.origin_avatar_url));
			holder.ivPhoto.setFadeIn(false);
		}catch(Exception e){
			Log.e(Const.TAG, this.getClass().getName() + " getView Exception:"+e.getMessage());
		}
		return convertView;
	}	
	
	static class ViewHolder{
		CachedImageView ivPhoto;
	}
}

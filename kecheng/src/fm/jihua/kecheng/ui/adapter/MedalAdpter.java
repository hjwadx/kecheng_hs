package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Medal;
import fm.jihua.kecheng.ui.widget.CachedImageView;

public class MedalAdpter extends BaseAdapter{
	
	List<Medal> mMedals;
	Context context;
	
	public MedalAdpter(Context context, List<Medal> medals) {
		this.context = context;
		setData(medals);
	}
	
	public void setData(List<Medal> medals) {
		this.mMedals = medals;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mMedals.size();
	}

	@Override
	public Object getItem(int position) {
		return mMedals.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.medal_item, parent, false);
			holder = new ViewHolder();
			holder.medal = (CachedImageView) convertView.findViewById(R.id.medal);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.description = (TextView) convertView.findViewById(R.id.description);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			Medal medal = mMedals.get(position);
			holder.medal.setImageURI(Uri.parse(medal.url));

			holder.name.setText(medal.name);
			holder.description.setText(String.valueOf(medal.description));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
	
	
	static class ViewHolder{
		CachedImageView medal;
		TextView name;
		TextView description;
	}

}

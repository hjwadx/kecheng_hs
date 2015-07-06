package fm.jihua.kecheng.ui.adapter;

import java.util.HashMap;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;

public class SimplestAdapter extends BaseAdapter {
	
	List<HashMap<String, Object>> mData;
	
	public SimplestAdapter(List<HashMap<String, Object>> data){
		this.mData = data;
	}

	@Override
	public int getCount() {
		return this.mData.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.user_item, parent, false);
			holder = new ViewHolder();
			holder.title = ((TextView)convertView.findViewById(R.id.title));
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>)getItem(position);
			holder.title.setText((String)map.get("name"));
		} catch (Exception e) {	
			e.printStackTrace();
		}
		
		return convertView;
	}

	static class ViewHolder{
		TextView title;
	}
}

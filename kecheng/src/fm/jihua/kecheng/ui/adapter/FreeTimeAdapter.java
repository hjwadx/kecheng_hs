package fm.jihua.kecheng.ui.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.Const;

public class FreeTimeAdapter extends BaseAdapter {

	List<? extends Map<String, ?>> mItems;
	Context mContext;
	
	public FreeTimeAdapter(Context context, List<? extends Map<String, ?>> items){
		this.mContext = context;
		this.mItems = items;
	}

	@Override
	public int getCount() {
		return this.mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_image_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			Map<String, ?> item = mItems.get(position); 
			viewHolder.icon.setImageResource((Integer) item.get("icon"));
				
			viewHolder.title.setText((String) item.get("title"));
			if (item.containsKey("delete")) {
				viewHolder.title.setTextColor(convertView.getResources().getColor(R.color.red));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, e.getMessage(), e);
		}
        
		return convertView;
	}
	
	static class ViewHolder{
		ImageView icon;
		TextView title;
	}

}

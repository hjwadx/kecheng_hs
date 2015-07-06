package fm.jihua.kecheng.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;

public class LoadingAdapter extends BaseAdapter {
	
	boolean showEmptyText = false;
	String message;
	String message_empty = "这门课尚未被创建，试试手动添加";
	
	public LoadingAdapter(String text){
		message = text;
	}
	
	public void showEmptyText() {
		this.showEmptyText = true;
		this.notifyDataSetChanged();
	}
	
	public void setEmptyMessage(String message) {
		this.message_empty = message;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_header_view, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.progressBar1 = convertView.findViewById(R.id.progressBar1);
			viewHolder.tv_update = ((TextView)convertView.findViewById(R.id.tv_update));
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (showEmptyText) {
			viewHolder.progressBar1.setVisibility(View.GONE);
			viewHolder.tv_update.setText(message_empty);
		}else {
			viewHolder.progressBar1.setVisibility(View.VISIBLE);
			viewHolder.tv_update.setText(message);
		}
		return convertView;
	}

	static class ViewHolder{
		View progressBar1;
		TextView tv_update;
	}
}

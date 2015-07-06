package fm.jihua.kecheng.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.commonsware.cwac.adapter.AdapterWrapper;

import fm.jihua.kecheng_hs.R;

public class BottomTipsWrapperAdapter extends AdapterWrapper{
	
	boolean showTips;
	String tips = "搜索中...";
	ListAdapter wrapped;

	public BottomTipsWrapperAdapter(ListAdapter wrapped) {
		super(wrapped);
		this.wrapped = wrapped;
	}
	
	public void showTips(boolean show){
		showTips = show;
	}
	
	public void showTips(String tips){
		showTips = true;
		this.tips = tips;
	}
	
	@Override
	public int getCount() {
		if (showTips) {
			return super.getCount() + 1;
		}
		return super.getCount();
	}
	
	@Override
	public Object getItem(int position) {
		if (showTips) {
			if (position < wrapped.getCount()) {
				return super.getItem(position);
			}
		}else {
			return super.getItem(position);
		}
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public int getItemViewType(int position) {
		if (showTips) {
			if (position < wrapped.getCount()) {
				return super.getItemViewType(position);
			}
		} else {
			return super.getItemViewType(position);
		}
		return IGNORE_ITEM_VIEW_TYPE;
	}

	public int getViewTypeCount() {
		if (showTips) {
			return (super.getViewTypeCount() + 1);
		}
		return (super.getViewTypeCount());
	}
	  
	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		if (showTips) {
			ViewHolder viewHolder;
			if (position == wrapped.getCount()) {		
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.search_header_view, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.progressBar1 = convertView.findViewById(R.id.progressBar1);
				viewHolder.tv_update = ((TextView)convertView.findViewById(R.id.tv_update));
				viewHolder.tv_update.setText(tips);
				return convertView;
			} else {
				return super.getView(position , convertView, parent);
			}
		}
		return super.getView(position, convertView, parent);
	}
	
	static class ViewHolder{
		View progressBar1;
		TextView tv_update;
	}

}

package fm.jihua.kecheng.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.commonsware.cwac.adapter.AdapterWrapper;

import fm.jihua.kecheng_hs.R;

public class TipsWrapperAdapter extends AdapterWrapper {
	
	boolean showTips;
	String tips;
	
	public TipsWrapperAdapter(ListAdapter wrapped){
		super(wrapped);
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
			if (position > 0) {
				return super.getItem(position-1);
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
			if (position > 0) {
				return super.getItemViewType(position-1);
			}
		}else {
			return super.getItemViewType(position);
		}
		return IGNORE_ITEM_VIEW_TYPE;
	  }

	  public int getViewTypeCount() {
		  if (showTips) {
			  return(super.getViewTypeCount() + 1);
		}
		  return(super.getViewTypeCount());
	  }

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (showTips) {
			if (position == 0) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_course_tip, parent, false);
				((TextView)convertView.findViewById(R.id.tips)).setText(tips);
				return convertView;
			}else {
				return super.getView(position-1, convertView, parent);
			}
		}
		return super.getView(position, convertView, parent);
	}
}

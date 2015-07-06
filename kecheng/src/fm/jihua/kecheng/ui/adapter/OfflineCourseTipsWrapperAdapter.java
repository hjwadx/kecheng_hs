package fm.jihua.kecheng.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.commonsware.cwac.adapter.AdapterWrapper;

import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.OfflineData;

public class OfflineCourseTipsWrapperAdapter extends AdapterWrapper{
	
	List<OfflineData<Course>> mOfflineDataList;
	boolean showTips;
	String tips;
	OnClickListener childClickListener;

	public OfflineCourseTipsWrapperAdapter(ListAdapter wrapped) {
		super(wrapped);
		this.mOfflineDataList = new ArrayList<OfflineData<Course>>();
	}
	
	public OfflineCourseTipsWrapperAdapter(ListAdapter wrapped,List<OfflineData<Course>> offlineDataList) {
		super(wrapped);
		this.mOfflineDataList = offlineDataList;
	}
	
	public void setOfflineDataList(List<OfflineData<Course>> offlineDataList) {
		this.mOfflineDataList = offlineDataList;
	}
	
	
	
	@Override
	public boolean isEnabled(int position) {
		if (position < mOfflineDataList.size() + getTipsCount()) {
			return false;
		}
		return super.isEnabled(position);
	}

	public void setOnActionListener(OnClickListener childClickListener){
		this.childClickListener = childClickListener;
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
		return super.getCount() + getTipsCount() + mOfflineDataList.size();
	}
	
	@Override
	public Object getItem(int position) {
		if (position >= mOfflineDataList.size() + getTipsCount()){
			return super.getItem(position-getTipsCount()-mOfflineDataList.size());
		}
		if (position == mOfflineDataList.size()) {
			return null;
		} else {
			return mOfflineDataList.get(position);
		}
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public int getItemViewType(int position) {
		if (position >= mOfflineDataList.size() + getTipsCount()){
			return super.getItemViewType(position-getTipsCount()-mOfflineDataList.size());
		}
		return IGNORE_ITEM_VIEW_TYPE;
	}
	
	public int getViewTypeCount() {
		return (super.getViewTypeCount() + 1);
	}
	
	private int getTipsCount(){
		return showTips ? 1 : 0;
	}
	
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (position >= mOfflineDataList.size() + getTipsCount()){
			return super.getView(position-getTipsCount()-mOfflineDataList.size(), convertView, parent);
		}else if (position < mOfflineDataList.size()){
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.offline_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.title = (TextView)convertView.findViewById(R.id.title);
				viewHolder.upload = ((Button)convertView.findViewById(R.id.upload));
				viewHolder.remove = ((Button)convertView.findViewById(R.id.remove));
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			try {
				OfflineData<Course> offlineData = (OfflineData<Course>)getItem(position);
				Course course = offlineData.getObject();			
				viewHolder.title.setText("失败："+ course.name);
				viewHolder.upload.setTag(offlineData);
				viewHolder.upload.setOnClickListener(this.childClickListener);
				viewHolder.remove.setTag(offlineData);
				viewHolder.remove.setOnClickListener(this.childClickListener);
			} catch (Exception e) {
				e.printStackTrace();
			}			
			return convertView;
		} else {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_course_tip, parent, false);
			((TextView) convertView.findViewById(R.id.tips)).setText(tips);
			return convertView;
		}
	}
	
	static class ViewHolder{
		TextView title;
		Button upload;
		Button remove;
	}
}

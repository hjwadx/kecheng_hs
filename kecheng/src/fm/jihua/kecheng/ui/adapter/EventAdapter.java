package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Event;
import fm.jihua.kecheng.ui.view.EventListView;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class EventAdapter extends BaseAdapter{
	
	List<Event> mEvent;
	Context context;
	int mType;
	int page = 1;
	
	public EventAdapter(Context context, List<Event> events, int type) {
		this.context = context;
		this.mType = type;
		setData(events);
	}
	
	public void setCount() {
		++page;
	}
	
	public void setData(List<Event> events) {
		this.mEvent = events;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(mType == EventListView.ALL_EVENT){
			return mEvent.size();
		} else {
			return Const.DATA_COUNT_PER_REQUEST*page <= mEvent.size()? Const.DATA_COUNT_PER_REQUEST*page : mEvent.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return mEvent.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.event_item, parent, false);
			holder = new ViewHolder();
			holder.poster = (CachedImageView) convertView.findViewById(R.id.poster);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.sign = (ImageView)convertView.findViewById(R.id.sign);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.locale = (TextView) convertView.findViewById(R.id.locale);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			Event event = mEvent.get(position);
			holder.poster.setImageURI(Uri.parse(event.poster_url));
			if(mType == EventListView.ALL_EVENT){
				holder.sign.setImageResource(R.drawable.activity_icon_participate);
				if(App.getInstance().getDBHelper().getEvent(App.getInstance().getUserDB(), event.id) == null){
					holder.sign.setVisibility(View.GONE);
				} else {
					holder.sign.setVisibility(View.VISIBLE);
				}
			} else {
				holder.sign.setImageResource(R.drawable.activity_icon_end);
				if(event.isOverdue()){
					holder.sign.setVisibility(View.VISIBLE);
				} else {
					holder.sign.setVisibility(View.GONE);
				}
			}
			holder.name.setText(event.name);
			holder.date.setText("时间：" + event.getTimeString());
			holder.locale.setText("地点：" + event.getLocalString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}
	
	
	static class ViewHolder{
		CachedImageView poster;
		TextView name;
		ImageView sign;
		TextView date;
		TextView locale;
	}

}

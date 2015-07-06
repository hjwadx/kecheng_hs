package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;

/**
 * @date 2013-7-9
 * @introduce 时间模式adapter
 */
public class TimeModeAdapter extends BaseAdapter {

	int count;
	Context context;
	LayoutInflater inflater;
	List<List<String>> listString;

	// 下午开始时间
	// int startTime = 14;

	public TimeModeAdapter(List<List<String>> listString, Context context) {
		super();
		this.listString = listString;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public void setListString(List<List<String>> listString) {
		this.listString = listString;
	}

	@Override
	public int getCount() {
		return listString.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = inflater.inflate(R.layout.itemview_time_mode, null);
			viewHolder = new ViewHolder();
			viewHolder.initViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tv_class.setText("第 " + (position + 1) + " 节");
		List<String> list = listString.get(position);
		viewHolder.tv_time.setText(list.get(0)+" ~ "+list.get(1));
		return view;
	}

	class ViewHolder {
		TextView tv_class;
		TextView tv_time;

		public void initViewHolder(View view) {
			tv_class = (TextView) view.findViewById(R.id.itemview_timemode_class);
			tv_time = (TextView) view.findViewById(R.id.itemview_timemode_time);
		}
	}
}

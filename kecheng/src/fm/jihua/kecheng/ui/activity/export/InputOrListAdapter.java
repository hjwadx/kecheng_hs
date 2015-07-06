package fm.jihua.kecheng.ui.activity.export;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;

/**
 * @date 2013-7-16
 * @introduce
 */
public class InputOrListAdapter extends BaseAdapter {

	Context context;
	List<String> listString;
	LayoutInflater inflater;

	public InputOrListAdapter(Context context, List<String> listString) {
		super();
		this.context = context;
		this.listString = listString;
		inflater = LayoutInflater.from(context);
	}

	public InputOrListAdapter(Context context, String[] arrString) {
		super();
		this.context = context;
		inflater = LayoutInflater.from(context);
		listString = Arrays.asList(arrString);
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
		// TODO Auto-generated method stub
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = inflater.inflate(R.layout.layout_only_text_with_arrow, null);
			viewHolder = new ViewHolder();
			viewHolder.initViews(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tv_title.setText(listString.get(position));
		return view;
	}

	class ViewHolder {
		TextView tv_title;

		public void initViews(View view) {
			tv_title = (TextView) view.findViewById(R.id.layout_textview);
		}
	}

}

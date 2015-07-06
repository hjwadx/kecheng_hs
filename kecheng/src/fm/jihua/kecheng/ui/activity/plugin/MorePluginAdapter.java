package fm.jihua.kecheng.ui.activity.plugin;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.PluginBean;

/**
 * @date 2013-7-15
 * @introduce 更多插件 适配器
 */
public class MorePluginAdapter extends BaseAdapter {

	Context context;
	List<PluginBean> list;
	LayoutInflater inflater;

	public MorePluginAdapter(Context context, List<PluginBean> list) {
		super();
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
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
		View view = inflater.inflate(R.layout.layout_itemview_more_plugin, null);
		return view;
	}
}

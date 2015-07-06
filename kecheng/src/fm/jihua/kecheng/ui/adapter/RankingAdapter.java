package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Ranking;

public class RankingAdapter extends BaseAdapter{
	
	final List<Ranking> rankings;

	public RankingAdapter(List<Ranking> rankings) {
		this.rankings = rankings;
	}

	@Override
	public int getCount() {
		return rankings.size();
	}

	@Override
	public Object getItem(int position) {
		return rankings.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.rank = (TextView)convertView.findViewById(R.id.rank);
			viewHolder.name = (TextView)convertView.findViewById(R.id.name);
			viewHolder.description = (TextView)convertView.findViewById(R.id.description);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Ranking ranking = (Ranking)getItem(position);
		
		viewHolder.rank.setText(String.valueOf(ranking.rank));
		viewHolder.name.setText(ranking.course.name);
		viewHolder.description.setText(ranking.desc);
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView rank;
		TextView name;
		TextView description;
	}
}

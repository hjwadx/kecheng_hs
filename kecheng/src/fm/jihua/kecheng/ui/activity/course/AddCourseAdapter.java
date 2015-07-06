package fm.jihua.kecheng.ui.activity.course;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng_hs.R;

public class AddCourseAdapter extends BaseAdapter {
	
	List<String> mCourses;
	OnClickListener childClickListener;
	boolean readonly;
	boolean showCheckBox;
	List<Boolean> initBooleanList = new ArrayList<Boolean>();
	
	public AddCourseAdapter(List<String> courses){
		this.mCourses = courses;
	}
	
	
	public void setBooleanList(List<Boolean> initBooleanList){
		this.initBooleanList = initBooleanList;
		notifyDataSetChanged();
	}
	
	public void setOnActionListener(OnClickListener childClickListener){
		this.childClickListener = childClickListener;
	}

	@Override
	public int getCount() {
		int count = this.mCourses.size();
		return count;
	}

	@Override
	public Object getItem(int position) {
		return this.mCourses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.addcourse_course_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView)convertView.findViewById(R.id.title);
			viewHolder.describe = (TextView)convertView.findViewById(R.id.describe);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			String name = (String) getItem(position);
			viewHolder.title.setText(name);
			Course course = getCourseByName(name);
			String describe = "";
			if(course != null){
				describe = course.getUnitString(false);
			}
			viewHolder.describe.setText(describe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return convertView;
	}

	static class ViewHolder{
		TextView title;
		TextView describe;
	}
	
	public Course getCourseByName(String name) {
		return App.getInstance().getDBHelper().getCourseByName(App.getInstance().getUserDB(), name);
	}
}

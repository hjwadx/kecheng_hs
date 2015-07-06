package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.renderscript.Program.TextureType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.Course;

public class SearchAdapter extends BaseAdapter {
	
	List<Course> mCourses;
	List<Course> mExistingCourses;
	OnClickListener childClickListener;
	boolean readonly;
	boolean showCheckBox;
	List<Boolean> initBooleanList;
	
	public SearchAdapter(List<Course> existingCourses, List<Course> courses){
		this.mExistingCourses = existingCourses;
		this.mCourses = courses;
	}
	
	public SearchAdapter(List<Course> courses, boolean readonly){
		this.mCourses = courses;
		this.readonly = readonly;
	}
	
	public SearchAdapter(List<Course> courses, List<Boolean> initBooleanList, boolean readonly, boolean showCheckBox) {
		this.mCourses = courses;
		this.readonly = readonly;
		this.showCheckBox = showCheckBox;
		this.initBooleanList = initBooleanList;
	}
	
	public void setBooleanList(List<Boolean> initBooleanList){
		this.initBooleanList = initBooleanList;
		notifyDataSetChanged();
	}
	
	public void setExistingCourses(List<Course> existingCourses) {
		this.mExistingCourses = existingCourses;
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
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView)convertView.findViewById(R.id.title);
			viewHolder.students = (TextView)convertView.findViewById(R.id.students);
			viewHolder.teacher = (TextView)convertView.findViewById(R.id.teacher);
			viewHolder.course_times = (TextView)convertView.findViewById(R.id.course_times);
//			viewHolder.add_or_remove = ((Button)convertView.findViewById(R.id.btn_add_or_remove));
			viewHolder.checkBox = ((CheckBox)convertView.findViewById(R.id.checkbox_search_item));
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			Course course = (Course)getItem(position);
			
			viewHolder.title.setText(course.name);
			viewHolder.teacher.setText(TextUtils.isEmpty(course.teacher) ? "未知" : course.teacher);
			viewHolder.students.setText("" + course.students_count + "人参加");
			viewHolder.course_times.setText(course.getUnitString(true));
//			if (readonly) {
//				viewHolder.add_or_remove.setVisibility(View.GONE);
//			}else {
//				viewHolder.add_or_remove.setVisibility(View.VISIBLE);
//				if (mExistingCourses != null && mExistingCourses.contains(course)) {
//					viewHolder.add_or_remove.setBackgroundResource(R.drawable.button_red_with_padding);
//					viewHolder.add_or_remove.setText("删除");
//				}else {
//					viewHolder.add_or_remove.setBackgroundResource(R.drawable.button_blue_with_padding);
//					viewHolder.add_or_remove.setText("添加");
//				}
//				viewHolder.add_or_remove.setTag(course);
//				viewHolder.add_or_remove.setOnClickListener(this.childClickListener);
//			}
			if(showCheckBox){
				viewHolder.checkBox.setVisibility(View.VISIBLE);
				if(initBooleanList.get(position)){
					viewHolder.checkBox.setChecked(true);
				}else{
					viewHolder.checkBox.setChecked(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return convertView;
	}

	static class ViewHolder{
		TextView title;
		TextView teacher;
		TextView students;
		TextView course_times;
		Button add_or_remove;
		CheckBox checkBox;
	}
}

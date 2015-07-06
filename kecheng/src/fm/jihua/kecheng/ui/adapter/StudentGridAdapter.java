package fm.jihua.kecheng.ui.adapter;


import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class StudentGridAdapter extends BaseAdapter {
	private List<User> mStudents; 
	
	//private final int maxShown = 6;
	public StudentGridAdapter(Context context, List<User> students){
		this.mStudents = students;
	}
	
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		User student = mStudents.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_students_grid_item, parent, false);
			holder = new ViewHolder();
			holder.ivPhoto = (CachedImageView) convertView.findViewById(R.id.ivPhoto);
			convertView.setTag(holder);
			if (student.sex == Const.FEMALE) {
				holder.ivPhoto.setImageResource(R.drawable.avatar_default_female);
			}else {
				holder.ivPhoto.setImageResource(R.drawable.avatar_default_male);
			}
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		try{
			holder.ivPhoto.setImageURI(Uri.parse(student.tiny_avatar_url));
		}catch(Exception e){
			Log.e(Const.TAG, this.getClass().getName() + " getView Exception:"+e.getMessage());
		}
		return convertView;
    }

    public final int getCount() {
        return mStudents.size();
    }

    public final Object getItem(int position) {
    	return mStudents.get(position);
    }

    public final long getItemId(int position) {
    	return position;
    }
    
    static class ViewHolder{
		CachedImageView ivPhoto;
	}
}

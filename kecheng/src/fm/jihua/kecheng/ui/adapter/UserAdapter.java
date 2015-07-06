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
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.Const;

public class UserAdapter extends BaseAdapter {

	List<User> mUsers;
	Context context;

	public UserAdapter(Context context, List<User> users) {
		this.context = context;
		setData(users);
	}

	public void setData(List<User> users) {
		// this.mCourseTimes = new ArrayList<CourseTime>();
		// for (CourseTime courseTime : courseTimes) {
		// if (courseTime != null) {
		// this.mCourseTimes.add(courseTime);
		// }
		// }
		// this.mFullCourseTimes = courseTimes;
		// this.mFullCourseTimes = courseTimes;
		this.mUsers = users;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.mUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView,
			final ViewGroup parent) {
		ViewHolder holder;
		User student = mUsers.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.user_item, parent, false);
			holder = new ViewHolder();
			holder.avatar = (CachedImageView) convertView.findViewById(R.id.avatar);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.sex = (ImageView)convertView.findViewById(R.id.sex);
			holder.grade_and_major = (TextView) convertView.findViewById(R.id.grade_and_major);
			holder.school = (TextView) convertView.findViewById(R.id.school);
			if (student.sex == Const.FEMALE) {
				holder.avatar.setImageResource(R.drawable.avatar_default_female);
			}else {
				holder.avatar.setImageResource(R.drawable.avatar_default_male);
			}
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
//			holder.avatar.setImageURI(null);
		}
		try {
//			InputStream in;
//			in = new java.net.URL(student.phone_avatar_url).openStream();
//			Bitmap bmp = BitmapFactory.decodeStream(in);
			holder.avatar.setCorner(true);
			holder.avatar.setImageURI(Uri.parse(student.origin_avatar_url));
			// ((TextView)convertView.findViewById(R.id.num)).setText(String.valueOf(mFullCourseTimes.indexOf(courseTime)+1));

			holder.name.setText(student.name);
//			((TextView) convertView.findViewById(R.id.name)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.female, 0);
			holder.sex.setImageResource(student.sex == Const.FEMALE ? R.drawable.friendlist_gender_female : R.drawable.friendlist_gender_male);
			// "讲师:"+ 教室:
			holder.grade_and_major.setText(String.valueOf(student.grade)+"级 " + User.getGradeFromYear(student.grade) + student.department + "班");
			holder.school.setText(student.school);
		} catch (Exception e) {
			AppLogger.printStackTrace(e);
		}

		return convertView;
	}
	
	static class ViewHolder{
		CachedImageView avatar;
		TextView name;
		ImageView sex;
		TextView school;
		TextView grade_and_major;
	}
}

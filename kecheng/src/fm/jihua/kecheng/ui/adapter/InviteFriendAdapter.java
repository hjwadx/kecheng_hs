package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.friend.InviteActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class InviteFriendAdapter extends BaseAdapter {

	final List<User> mUsers;
	int category;
	DataAdapter mDataAdapter;
	Activity mActivity;

	public InviteFriendAdapter(Activity parent, List<User> users, int category) {
		this.mActivity = parent;
		mUsers = users;
		this.category = category;
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
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
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.search_user_item, parent, false);
			holder = new ViewHolder();
			holder.avatar = (CachedImageView) convertView.findViewById(R.id.avatar);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.sex = (ImageView)convertView.findViewById(R.id.sex);
			holder.grade_and_major = (TextView) convertView.findViewById(R.id.grade_and_major);
			holder.school = (TextView) convertView.findViewById(R.id.school);
			holder.addFriend = convertView.findViewById(R.id.add_friend);
			holder.removeFriend = convertView.findViewById(R.id.remove_friend);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
			holder.avatar.setImageURI(null);
		}
		try {
			final User friend = mUsers.get(position);
//			InputStream in;
//			in = new java.net.URL(student.phone_avatar_url).openStream();
//			Bitmap bmp = BitmapFactory.decodeStream(in);
			holder.avatar.setCorner(true);
			holder.avatar.setImageURI(Uri.parse(friend.origin_avatar_url));
			// ((TextView)convertView.findViewById(R.id.num)).setText(String.valueOf(mFullCourseTimes.indexOf(courseTime)+1));

			holder.name.setText(friend.name);
//			((TextView) convertView.findViewById(R.id.name)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.female, 0);
			holder.sex.setImageResource(friend.sex == Const.FEMALE ? R.drawable.friendlist_gender_female : R.drawable.friendlist_gender_male);
			switch (category) {
			case Const.RENREN:
				if (friend.renren_name != null && friend.renren_name.length() > 0) {
					holder.school.setText("人人:"+friend.renren_name);
				}
				break;
			case Const.WEIBO:
				holder.school.setText("微博:"+friend.weibo_name);
				break;
			default:
				holder.grade_and_major.setText(String.valueOf(friend.grade)+"级 " + User.getGradeFromYear(friend.grade) + friend.department + "班");
				holder.school.setText(friend.school);
				break;
			}
			App app = (App)mActivity.getApplication();
			if (app.getMyUserId() == friend.id) {
				holder.addFriend.setVisibility(View.GONE);
				holder.removeFriend.setVisibility(View.GONE);
			}else {
				final boolean isFriend = app.getDBHelper().isFriend(app.getUserDB(), friend.id);
				holder.addFriend.setVisibility(isFriend ? View.GONE : View.VISIBLE);
				holder.removeFriend.setVisibility(isFriend ? View.VISIBLE : View.GONE);
			}

			holder.addFriend.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UIUtil.block(mActivity);
					sendUmengMessage("action_add_friends");
					mDataAdapter.addFriend(friend);
				}
			});
			
			holder.removeFriend.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UIUtil.block(mActivity);
					sendUmengMessage("action_del_friends");
					mDataAdapter.removeFriend(friend.id);
				}
			});
//			// "讲师:"+ 教室:
//			holder.grade.setText(String.valueOf(friend.grade));
//			holder.major.setText(friend.department);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}
	
	void sendUmengMessage(String message){
		switch (category) {
		case Const.SEARCH:
			MobclickAgent.onEvent(mActivity, message, "name");
			break;
		case Const.SEARCH_BY_GEZI_ID:
			MobclickAgent.onEvent(mActivity, message, "id");
			break;
		case Const.CLASSMATES:
			MobclickAgent.onEvent(mActivity, message, "mates");
			break;
		default:
			break;
		}
	}
	
	private class MyDataCallback implements DataCallback {
		@Override
		public void callback(Message msg) {
			Log.i(Const.TAG, "DataCallback Message:" + msg.what);
			UIUtil.unblock(mActivity);
			switch (msg.what) {
			case DataAdapter.MESSAGE_ADD_FRIEND:{
				BaseResult result = (BaseResult)msg.obj;
				if ( result != null && result.success ) {
					Hint.showTipsShort(mActivity, "添加好友成功");
					notifyDataSetChanged();
				}else {
					Hint.showTipsShort(mActivity, "添加好友失败");
				}
			}
				break;
			case DataAdapter.MESSAGE_REMOVE_FRIEND:{
				BaseResult result = (BaseResult)msg.obj;
				if ( result != null && result.success ) {
					Hint.showTipsShort(mActivity, "删除好友成功");
					notifyDataSetChanged();
				}else {
					Hint.showTipsShort(mActivity, "删除好友失败");
				}
			}
				break;
			default:
				break;
			}
		}
    }
	
	static class ViewHolder{
		CachedImageView avatar;
		TextView name;
		ImageView sex;
		TextView school;
		TextView grade_and_major;
		View addFriend;
		View removeFriend;
	}
}

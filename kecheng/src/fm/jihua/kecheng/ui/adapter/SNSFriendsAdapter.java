package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.interfaces.SimpleUser;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.friend.SNSInviteActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class SNSFriendsAdapter  extends BaseAdapter{

	int category;
	DataAdapter mDataAdapter;
	Activity mActivity;
	public List<User> friends;
	public List<User> not_friends;
	public List<SimpleUser> sns_friends;
	int page;

	public SNSFriendsAdapter(Activity parent, List<User> friends,  List<User> not_friends, List<SimpleUser> sns_friends,int category) {
		this.mActivity = parent;
		this.friends = friends;
		this.not_friends = not_friends;
		this.sns_friends = sns_friends;
		this.category = category;
		page = 1;
		mDataAdapter = new DataAdapter(mActivity, new MyDataCallback());
	}

	public void setCount() {
		++page;
	}

	@Override
	public int getCount() {
//		return friends.size() + not_friends.size() + sns_friends.size();
		return Const.DATA_COUNT_PER_REQUEST*page <= (friends.size() + not_friends.size() + sns_friends.size()) ? Const.DATA_COUNT_PER_REQUEST*page : friends.size() + not_friends.size() + sns_friends.size();
	}

	@Override
	public boolean isEnabled(int position) {
		if (position < not_friends.size() || position >= sns_friends.size() + not_friends.size()) {
			return super.isEnabled(position);
		}
		return false;
	}

	@Override
	public Object getItem(int position) {
		if (position < not_friends.size()){
			return not_friends.get(position);
		} else if (position < sns_friends.size() + not_friends.size()){
			return sns_friends.get(position - not_friends.size());
		} else if (position < sns_friends.size() + not_friends.size() + friends.size()){
			return friends.get(position - not_friends.size() - sns_friends.size());
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.sns_user_item, parent, false);
			holder = new ViewHolder();
			holder.avatar = (CachedImageView) convertView.findViewById(R.id.avatar);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.otherName = (TextView) convertView.findViewById(R.id.other_name);
			holder.addFriend = convertView.findViewById(R.id.add_friend);
			holder.removeFriend = convertView.findViewById(R.id.remove_friend);
			holder.inviteFriend= convertView.findViewById(R.id.invite_friend);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
			holder.avatar.setImageURI(null);
		}
		final User user;
		final SimpleUser user1;
		App app = (App)mActivity.getApplication();
		holder.avatar.setCorner(true);
		if (position < not_friends.size()){
			final User friend = not_friends.get(position);
			user = friend;
			user1 = new SimpleUser();
			holder.avatar.setImageURI(Uri.parse(friend.origin_avatar_url));
			holder.name.setText(friend.name);
			holder.otherName.setVisibility(View.VISIBLE);
			if (category == Const.WEIBO && friend.weibo_name != null){
				holder.otherName.setText("微博:"+friend.weibo_name);
			} else if(category == Const.RENREN && friend.renren_name != null){
				holder.otherName.setText("人人:"+friend.renren_name);
			} else {
				holder.otherName.setVisibility(View.GONE);
			}
			final boolean isFriend = app.getDBHelper().isFriend(app.getUserDB(), friend.id);
			holder.addFriend.setVisibility(isFriend ? View.GONE : View.VISIBLE);
			holder.removeFriend.setVisibility(isFriend ? View.VISIBLE : View.GONE);
			holder.inviteFriend.setVisibility(View.GONE);
		} else if (position < sns_friends.size() + not_friends.size()){
			final SimpleUser friend = sns_friends.get(position - not_friends.size());
			user1 = friend;
			user = new User();
			holder.avatar.setImageURI(Uri.parse(friend.avatar));
			holder.name.setText(friend.name);
			holder.otherName.setVisibility(View.GONE);
			holder.inviteFriend.setVisibility(View.VISIBLE);
			holder.addFriend.setVisibility(View.GONE);
			holder.removeFriend.setVisibility(View.GONE);
//		} else if (position < sns_friends.size() + not_friends.size() + friends.size()){
		} else {
			final User friend = friends.get(position - not_friends.size() - sns_friends.size());
			user = friend;
			user1 = new SimpleUser();
			holder.avatar.setImageURI(Uri.parse(friend.origin_avatar_url));
			holder.otherName.setVisibility(View.VISIBLE);
			if (category == Const.WEIBO && friend.weibo_name != null){
				holder.otherName.setText("微博:"+friend.weibo_name);
			} else if(category == Const.RENREN && friend.renren_name != null){
				holder.otherName.setText("人人:"+friend.renren_name);
			} else {
				holder.otherName.setVisibility(View.GONE);
			}
			holder.name.setText(friend.name);
			final boolean isFriend = app.getDBHelper().isFriend(app.getUserDB(), friend.id);
			holder.addFriend.setVisibility(isFriend ? View.GONE : View.VISIBLE);
			holder.removeFriend.setVisibility(isFriend ? View.VISIBLE : View.GONE);
			holder.inviteFriend.setVisibility(View.GONE);
		}

		holder.addFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIUtil.block(mActivity);
				sendUmengMessage("action_add_friends");
				mDataAdapter.addFriend(user);
			}
		});

		holder.removeFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIUtil.block(mActivity);
				sendUmengMessage("action_del_friends");
				mDataAdapter.removeFriend(user.id);
			}
		});
		holder.inviteFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, SNSInviteActivity.class);
				intent.putExtra("ONLYTEXT", true);
				String string = mActivity.getResources().getString(R.string.share_sns_invite);
				if (category == Const.WEIBO) {
					MobclickAgent.onEvent(mActivity, "action_invite_friends", "weibo");
					intent.putExtra("CONTENT", "@" + user1.name + " " + string + " http://app.sina.com.cn/appdetail.php?appID=118442") ;
					intent.putExtra("TYPE", Const.WEIBO);
				} else {
					MobclickAgent.onEvent(mActivity, "action_invite_friends", "renren");
					intent.putExtra("CONTENT", "@" + user1.name + "(" + user1.id + ") " + " " + string + " http://hs.kechenggezi.com/" );
					intent.putExtra("TYPE", Const.RENREN);
				}
				(mActivity).startActivity(intent);
			}
		});
		return convertView;
	}
	
	void sendUmengMessage(String message){
		switch (category) {
		case Const.WEIBO:
			MobclickAgent.onEvent(mActivity, message, "weibo");
			break;
		case Const.RENREN:
			MobclickAgent.onEvent(mActivity, message, "renren");
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
		TextView otherName;
		View addFriend;
		View removeFriend;
		View inviteFriend;
	}

}

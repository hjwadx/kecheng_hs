package fm.jihua.kecheng.ui.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.common.utils.TimeHelper;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.ContactItem;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class ContactAdapter extends BaseAdapter {

	final List<ContactItem> mItems;
	Context context;
	
	public ContactAdapter(List<ContactItem> items, Context context){
		this.mItems = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		return this.mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		ContactItem contactItem = mItems.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
			holder = new ViewHolder();
			holder.avatar = (CachedImageView) convertView.findViewById(R.id.avatar);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView)convertView.findViewById(R.id.time);
			holder.unread_count = (TextView) convertView.findViewById(R.id.unread_count);
			holder.imageview = (ImageView) convertView.findViewById(R.id.imageview);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			if (contactItem.name != null) {
				holder.avatar.setImageURI(Uri.parse(contactItem.tiny_avatar_url));
				holder.name.setText(contactItem.name);
			}else {
				holder.name.setText("课程格子");
				if (contactItem.sex == Const.FEMALE) {
					holder.avatar.setImageResource(R.drawable.avatar_default_female);
				}else {
					holder.avatar.setImageResource(R.drawable.avatar_default_male);
				}
			}
			if (contactItem.isMessage) {
				if (contactItem.lastChatTime == 0) {
					holder.time.setText("");
				}else {
					holder.time.setText(TimeHelper.getEarlyTime(contactItem.lastChatTime));
				}
				if (contactItem.lastMessage.contains("[") && contactItem.lastMessage.contains("]") && Arrays.asList(Const.CHENGZI_MMS).contains(contactItem.lastMessage.replace("[", "").replace("]", ""))) {
//					holder.message.setVisibility(View.GONE);
//					holder.imageview.setVisibility(View.VISIBLE);
//					String name = contactItem.lastMessage.replace("[", "").replace("]","");
//					int i = Arrays.asList(Const.CHENGZI_MMS).indexOf(name);
//					InputStream is;
//					Drawable drawable = null;
//					try {
//						is = context.getResources().getAssets().open(Const.CHENGZI_MMS_FILES[i] + ".png");
//						drawable = Drawable.createFromResourceStream(context.getResources(), null, is, "srcName");
//						// drawable = Drawable.createFromStream(is, "srcName");
//						is.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					holder.imageview.setImageDrawable(drawable);
					holder.message.setText("[贴纸]");
				} else {
//					holder.message.setVisibility(View.VISIBLE);
//					holder.imageview.setVisibility(View.GONE);
					holder.message.setText(contactItem.lastMessage);
				}
				if (contactItem.unreadCount == 0) {
					holder.unread_count.setVisibility(View.GONE);
				}else {
					holder.unread_count.setVisibility(View.VISIBLE);
					holder.unread_count.setText(String.valueOf(contactItem.unreadCount));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, e.getMessage(), e);
		}
        
		return convertView;
	}
	
	static class ViewHolder{
		CachedImageView avatar;
		TextView name;
		TextView time;
		TextView message;
		TextView unread_count;
		ImageView imageview;
	}

}

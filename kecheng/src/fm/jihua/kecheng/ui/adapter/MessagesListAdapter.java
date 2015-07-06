package fm.jihua.kecheng.ui.adapter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.chat.service.MessageText;
import fm.jihua.common.utils.TimeHelper;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.sticker.Sticker;
import fm.jihua.kecheng.rest.service.RestService;
import fm.jihua.kecheng.ui.activity.common.WebViewActivity;
import fm.jihua.kecheng.ui.activity.message.MessageTextUtils;
import fm.jihua.kecheng.ui.activity.plugin.SecretPostCommentActivity;
import fm.jihua.kecheng.ui.activity.profile.ProfileActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.view.GifMovieView;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.AppLogger;

/**
 * {@inheritDoc}.
 */
public class MessagesListAdapter extends BaseAdapter {

	private final List<MessageText> mListMessages;
	private User mTalkUser;
	private User mMyself;

	final int MYSELF = 0;
	final int OTHER = 1;
	final int SYSTEM_MSG = 2;
	Context context;
	Map<String, Sticker> allPasterBeanForChatCode;

	/**
	 * Constructor.
	 */
	public MessagesListAdapter(List<MessageText> messages, Context context) {
		this.mListMessages = messages;
		this.context = context;
		allPasterBeanForChatCode = Sticker.getPasterMap2KeyChatCode();
	}

	public void setData(User talkUser, User myself) {
		this.mMyself = myself;
		this.mTalkUser = talkUser;
	}

	/**
	 * Returns the number of messages contained in the messages list.
	 * 
	 * @return The number of messages contained in the messages list.
	 */
	@Override
	public int getCount() {
		return mListMessages.size();
	}

	/**
	 * Return an item from the messages list that is positioned at the position
	 * passed by parameter.
	 * 
	 * @param position
	 *            The position of the requested item.
	 * @return The item from the messages list at the requested position.
	 */
	@Override
	public Object getItem(int position) {
		return mListMessages.get(position);
	}

	/**
	 * Return the id of an item from the messages list that is positioned at the
	 * position passed by parameter.
	 * 
	 * @param position
	 *            The position of the requested item.
	 * @return The id of an item from the messages list at the requested
	 *         position.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		try {
			MessageText msg = mListMessages.get(position);
			if(msg.getExtraType() != null && msg.getExtraType() != ""){
				return SYSTEM_MSG;
			}
			String id = msg.getBareJid().split("@")[0];
			if ("me".equalsIgnoreCase(id)
					|| "me".equalsIgnoreCase(msg.getName())
					|| String.valueOf(mMyself.id).equals(id)) {
				return MYSELF;
			} else {
				return OTHER;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return OTHER;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	public View getSpecialView(int position, View convertView, final ViewGroup parent) {
		final ViewHolderSystemMsg viewHolder;
		final MessageText msg = mListMessages.get(position);
		if(convertView == null) {
			viewHolder = new ViewHolderSystemMsg();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.system_msg, parent, false);
			int type = getItemViewType(position);
			viewHolder.type = type;
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.time_layout = (RelativeLayout) convertView
					.findViewById(R.id.time_layout);
			viewHolder.avatar2 = (CachedImageView)convertView.findViewById(R.id.avatar2);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.content = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(viewHolder);
		}  else {
			viewHolder = (ViewHolderSystemMsg) convertView.getTag();
		}
		try {
			viewHolder.avatar2.setCorner(true);
			if (computingTime(position)) {
				viewHolder.time_layout.setVisibility(View.VISIBLE);
				String date = TimeHelper.getEarlyTimeWithOutMinute(msg
						.getTimestamp().getTime(), false);
				viewHolder.time.setText(date);
			} else {
				viewHolder.time.setText("");
				viewHolder.time_layout.setVisibility(View.GONE);
			}
			if("inline_browser".equals(msg.getExtraType()) || "secret_post".equals(msg.getExtraType())){
				viewHolder.avatar2.setVisibility(View.GONE);
				if(msg.getSubject() != null && !"".equals(msg.getSubject())){
					viewHolder.name.setText(msg.getSubject());
					viewHolder.name.setVisibility(View.VISIBLE);
				} else {
					viewHolder.name.setVisibility(View.GONE);
				}
			} else if ("add_friend".equals(msg.getExtraType())){
				JSONObject jsonObject = new JSONObject(msg.getExtraJson());
				viewHolder.name.setText(jsonObject.getString("name").toString());
				viewHolder.avatar2.setImageURI(Uri.parse(jsonObject.getString("tiny_avatar_url")));
				viewHolder.name.setVisibility(View.VISIBLE);
				viewHolder.avatar2.setVisibility(View.VISIBLE);
			}
			viewHolder.content.setText(msg.getMessage());
			convertView.findViewById(R.id.tableRow2).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					specialAction(msg.getExtraType(), msg.getExtraJson());
				}
			});
		} catch (JSONException e) {
			AppLogger.printStackTrace(e);
		}
		return convertView;
	}
	
	public void specialAction(String type, String json){
		Gson gson = new Gson();
		if("inline_browser".equals(type)){
			MobclickAgent.onEvent(context, "action_message_link", json);
			Intent intent = new Intent(context, WebViewActivity.class);
			intent.putExtra("URL", json);
			context.startActivity(intent);
		} else if ("add_friend".equals(type)){
			User user = gson.fromJson(json, User.class);
			Intent intent = new Intent(context, ProfileActivity.class);
			intent.putExtra("USER", user);
			context.startActivity(intent);
		} else if ("secret_post".equals(type)){
			SecretPost post = gson.fromJson(json, SecretPost.class);
			Intent intent = new Intent(context, SecretPostCommentActivity.class);
			intent.putExtra("SECRET_POST", post);
			context.startActivity(intent);
		}
	}

	/**
	 * Return the view of an item from the messages list.
	 * 
	 * @param position
	 *            The position of the requested item.
	 * @param convertView
	 *            The old view to reuse if possible.
	 * @param parent
	 *            The parent that this view will eventually be attached to.
	 * @return A View corresponding to the data at the specified position.
	 */
	public View getView(int position, View convertView, final ViewGroup parent) {
		if(getItemViewType(position) == SYSTEM_MSG) {
			return getSpecialView(position, convertView, parent);
		} 
		MessageText msg = mListMessages.get(position);
		final ViewHolder viewHolder;
		if (convertView == null) {
			int type = getItemViewType(position);
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					type == MYSELF ? R.layout.chat_right_msg
							: R.layout.chat_left_msg, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.type = type;
			viewHolder.text = (TextView) convertView.findViewById(R.id.text);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.time_layout = (RelativeLayout) convertView
					.findViewById(R.id.time_layout);
			viewHolder.avatar = (CachedImageView) convertView
					.findViewById(R.id.avatar);
			viewHolder.gif = (GifMovieView) convertView.findViewById(R.id.gif1);
			viewHolder.imageview = (CachedImageView) convertView.findViewById(R.id.imageview);
			viewHolder.msg_textcontainer = convertView.findViewById(R.id.msg_textcontainer);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			final User user = viewHolder.type == MYSELF ? mMyself : mTalkUser;
			viewHolder.avatar.setCorner(true);
			viewHolder.avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(user.name != null && user.name.length() > 0){
						Intent intent = new Intent(parent.getContext(), ProfileActivity.class);
						intent.putExtra("USER", user);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						parent.getContext().startActivity(intent);
					}
				}
			});
			String text = msg.getMessage();
			String chatCode = MessageTextUtils.getChatCodeFromMessage(msg.getMessage());
			if (chatCode != null) {
				viewHolder.msg_textcontainer.setVisibility(View.GONE);
				viewHolder.imageview.setVisibility(View.VISIBLE);
				if(allPasterBeanForChatCode.containsKey(chatCode)){
					Sticker paster = allPasterBeanForChatCode.get(chatCode);
					Drawable drawable = paster.getDrawable(context);
					viewHolder.imageview.setImageDrawable(drawable);
				}else{
					viewHolder.imageview.setImageURI(Uri.parse(RestService.get().getDownloadStickerUrlFromChatCode(chatCode)));
				} 
			} else {
				text = MessageTextUtils.unescape(text);
				viewHolder.msg_textcontainer.setVisibility(View.VISIBLE);
				viewHolder.imageview.setVisibility(View.GONE);
			}
			viewHolder.text.setText(text);
			viewHolder.text.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {

					showLongClickDialog(((TextView) v).getText().toString());
					return true;
				}
			});
			viewHolder.text.setAutoLinkMask(Linkify.ALL);

			if (computingTime(position)) {
				viewHolder.time_layout.setVisibility(View.VISIBLE);
				String date = TimeHelper.getEarlyTimeWithOutMinute(msg
						.getTimestamp().getTime(), false);
				viewHolder.time.setText(date);
			} else {
				viewHolder.time.setText("");
				viewHolder.time_layout.setVisibility(View.GONE);
			}

			if (user.tiny_avatar_url != null) {
				viewHolder.avatar.setImageURI(Uri.parse(user.tiny_avatar_url));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	static class ViewHolder {
		int type;
		CachedImageView avatar;
		View msg_textcontainer;
		TextView text;
		TextView time;
		RelativeLayout time_layout;
		GifMovieView gif;
		CachedImageView imageview;
	}
	
	static class ViewHolderSystemMsg {
		int type;
		TextView time;
		RelativeLayout time_layout;
		CachedImageView avatar2;
		TextView name;
		TextView content;
	}

	long timeStandrd = -1;
	long timeDistance = 1000 * 60 * 5;// 间隔5分钟
	// Map<Integer, Integer> map;

	boolean computingTime(int position) {
		if (mListMessages != null && mListMessages.size() > 0) {
			if (timeStandrd == -1) {
				timeStandrd = mListMessages.get(mListMessages.size() - 1)
						.getTimestamp().getTime();
				// map = new HashMap<Integer, Integer>();
			}

			Date timestamp = mListMessages.get(position).getTimestamp();
			if (timestamp == null || timestamp.getTime() == 0)
				return false;
			long distance = timestamp.getTime() - timeStandrd;
			int disNumber = (int) (distance / timeDistance);

			if (position != 0 && mListMessages.get(position - 1) != null) {
				Date timestampB = mListMessages.get(position - 1)
						.getTimestamp();
				long distanceB = timestampB.getTime() - timeStandrd;
				int disNumberB = (int) (distanceB / timeDistance);
				if (disNumber == disNumberB)
					return false;
			}
			return true;
		}
		return false;
	}

	private void showLongClickDialog(final String str) {
		Builder builder = new Builder(context);
		builder.setTitle(context.getString(R.string.chat_dialog_title));
		builder.setItems(R.array.chatLongClickItem,
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:// 复制
							try {
								ClipboardManager clipboard = (ClipboardManager) context
										.getSystemService(Context.CLIPBOARD_SERVICE);
								clipboard.setText(str);
								Hint.showTipsLong(context, "内容被复制到剪贴板");
							} catch (Exception e) {
								// no clip board, do nothing
							}
							break;
						}
					}
				});
		builder.create().show();
	}
}
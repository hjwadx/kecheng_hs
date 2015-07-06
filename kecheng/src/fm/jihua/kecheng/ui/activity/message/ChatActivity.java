package fm.jihua.kecheng.ui.activity.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.util.StringUtils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import fm.jihua.chat.service.Contact;
import fm.jihua.chat.service.Message;
import fm.jihua.chat.service.MessageText;
import fm.jihua.chat.service.aidl.IChat;
import fm.jihua.chat.service.aidl.IChatManager;
import fm.jihua.chat.service.aidl.IChatManagerListener;
import fm.jihua.chat.service.aidl.IMessageListener;
import fm.jihua.chat.service.aidl.IXmppFacade;
import fm.jihua.chat.utils.BeemBroadcastReceiver;
import fm.jihua.chat.utils.BeemConnectivity;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.ui.widget.PullDownView;
import fm.jihua.common.ui.widget.PullDownView.UpdateHandle;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UserDetailsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.mall.MallFragment;
import fm.jihua.kecheng.ui.activity.profile.ProfileActivity;
import fm.jihua.kecheng.ui.adapter.MessagesListAdapter;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.EmojiGridContainer;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.Const;

public class ChatActivity extends BaseActivity implements TextView.OnEditorActionListener {

	public static final String INTENT_FROM_PROFILE = "FROM_PROFILE";
	private static final Intent SERVICE_INTENT = new Intent();
	static {
		SERVICE_INTENT.setComponent(new ComponentName("fm.jihua.kecheng_hs", "fm.jihua.kecheng.BeemService"));
	}
	private Handler mHandler = new Handler();

	private Contact mContact;
	private DataAdapter mDataAdapter;

	private TextView mContactNameTextView;
	private TextView mContactStatusMsgTextView;
	private TextView mContactChatState;
	// private TextView mContactOtrState;
	// private ImageView mContactStatusIcon;
	// private LayerDrawable mAvatarStatusDrawable;
	private ListView mMessagesListView;
	private PullDownView pullDownView;
	private EditText mInputField;
	private Button mSendButton;
	private Button mEmojiButton;
	private EmojiGridContainer mEmojiGridContainer;
	// private final Map<Integer, Bitmap> mStatusIconsMap = new HashMap<Integer,
	// Bitmap>();

	private final List<MessageText> mListMessages = new ArrayList<MessageText>();
	private final List<Message> mHistoryMessages = new ArrayList<Message>();
	private final List<Message> mCurrentMessages = new ArrayList<Message>();

	private IChat mChat;
	private IChatManager mChatManager;
	private final IMessageListener mMessageListener = new OnMessageListener();
	private final IChatManagerListener mChatManagerListener = new ChatManagerListener();
	private MessagesListAdapter mMessagesListAdapter = new MessagesListAdapter(mListMessages, this);

	private final ServiceConnection mConn = new BeemServiceConnection();
	private final BeemBroadcastReceiver mBroadcastReceiver = new ConnectionBroadcastReceiver();
	private IXmppFacade mXmppFacade;
//	private String mCurrentAvatarId;
	private boolean mBinded;
	private boolean mCompact;
	private User mTalkUser;
	private User mMyself;
//	private Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		}
		mCompact = true;
		setContentView(R.layout.chat_compact);
		findViews();
		initViews();
		initTitlebar();
		init();
	}

	void findViews() {
		mMessagesListView = (ListView) findViewById(R.id.chat_messages);
		pullDownView = (PullDownView) findViewById(R.id.listbase);
		mInputField = (EditText) findViewById(R.id.chat_input);
		mSendButton = (Button) findViewById(R.id.chat_send_message);
		mEmojiButton = (Button) findViewById(R.id.btn_emoji);
		mEmojiGridContainer = (EmojiGridContainer) findViewById(R.id.emoji_grid);
	}

	void init() {
		User user = User.fromJabberUri(getIntent().getData());
		App app = (App) getApplicationContext();
		app.getChatDBHelper().read(app.getChatDBHelper().getWritableDatabase(), user.getJID());
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		mTalkUser = user;
		mMyself = app.getMyself();
		mDataAdapter.getUser(user.id);
		mMessagesListAdapter.setData(mTalkUser, mMyself);
		if(ObjectUtils.containsElement(Const.SYSTEM_ACCOUNTS, String.valueOf(mTalkUser.id))){
//		if(mTalkUser.id == 1752625 || mTalkUser.id == 1752675 || mTalkUser.id == 1756063){
			findViewById(R.id.input_area).setVisibility(View.GONE);
			getKechengActionBar().getActionTextButton().setVisibility(View.GONE);
			getKechengActionBar().getActionButton().setVisibility(View.GONE);
		}
	}

	void initViews() {
		mMessagesListView.setAdapter(mMessagesListAdapter);
		mMessagesListView.setDivider(null);
		mMessagesListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				mEmojiGridContainer.setVisibility(View.GONE);
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mInputField.getWindowToken(), 0);
				mEmojiButton.setBackgroundResource(R.drawable.emoji_button);
				return false;
			}
		});
		pullDownView.setUpdateHandle(new UpdateHandle() {

			@Override
			public void onUpdate() {
				new GetDataTask().execute();
			}
		});

		mInputField.setOnEditorActionListener(this);
		mInputField.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEmojiGridContainer.setVisibility(View.GONE);
				mEmojiButton.setBackgroundResource(R.drawable.emoji_button);
				showBottom();
			}
		});

		mInputField.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mEmojiGridContainer.setVisibility(View.GONE);
				}
			}
		});
		mInputField.requestFocus();

		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});

		// 按钮图片切换
		mEmojiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mEmojiGridContainer.getVisibility() == View.GONE) {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mInputField.getWindowToken(), 0);
					mEmojiButton.setBackgroundResource(R.drawable.emoji_button_keybroad);
					mEmojiGridContainer.postDelayed(new Runnable() {

						@Override
						public void run() {
							mEmojiGridContainer.setVisibility(View.VISIBLE);
						}
					}, 100);
				} else {
					mEmojiGridContainer.setVisibility(View.GONE);
					mInputField.requestFocus();
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mInputField, 0);
					mEmojiButton.setBackgroundResource(R.drawable.emoji_button);
				}
				showBottom();
			}
		});
	}

	void initTitlebar() {
		setTitle("课程格子");
		
		if (!getIntent().getBooleanExtra(INTENT_FROM_PROFILE, false)) {
			// getKechengActionBar().getActionTextButton().setVisibility(View.VISIBLE);
			// getKechengActionBar().setRightText("详细资料");
			// getKechengActionBar().getActionTextButton().setOnClickListener(new
			// OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// startProfileActivity(mTalkUser);
			// }
			// });
			getKechengActionBar().getMenuButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			getKechengActionBar().setShowBackBtn(true);

		}
	}

	void startProfileActivity(User user) {
		if (user == null) {
			Hint.showTipsLong(this, "该用户信息有误，请连网更新.");
		} else {
			Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
			intent.putExtra("USER", user);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mContact = new Contact(getIntent().getData());
		if (!mBinded) {
			bindService(SERVICE_INTENT, mConn, BIND_AUTO_CREATE);
			mBinded = true;
		}
		mEmojiGridContainer.onResume();
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected void onDestroy() {
		mMessagesListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mMessagesListView.clearFocus();
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		this.registerReceiver(mBroadcastReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED));
		this.registerReceiver(mBroadcastReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CREATED));
		registerBroad();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unRegisterBroad();
		this.unregisterReceiver(mBroadcastReceiver);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (mChat != null) {
				mChat.setOpen(false);
				mChat.removeMessageListener(mMessageListener);
			}

			if (mChatManager != null)
				mChatManager.removeChatCreationListener(mChatManagerListener);
		} catch (RemoteException e) {
			Log.e(Const.TAG, e.getMessage());
		}
		if (mBinded) {
			unbindService(mConn);
			mBinded = false;
		}
		mXmppFacade = null;
		mChat = null;
		mChatManager = null;
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v == mInputField && actionId == EditorInfo.IME_ACTION_SEND) {
			sendMessage();
			return true;
		}
		return false;
	}

	boolean isBottom = false;

	/**
	 * Send an XMPP message.
	 */
	private void sendMessage() {
		String inputContent = mInputField.getText().toString();
		if (!TextUtils.isEmpty(inputContent)) {
			Message msgToSend = new Message(mContact.getJIDWithRes(), Message.MSG_TYPE_CHAT);
			msgToSend.setFrom(mMyself.getJID());
			if (mInputField.getTag() != null && "image".equals(mInputField.getTag())) {
				mInputField.setTag(null);
			}else {
				inputContent = MessageTextUtils.escape(inputContent);
			}
			msgToSend.setBody(inputContent);

			try {
				if (mChat == null) {
					mChat = mChatManager.createChat(mContact, mMessageListener);
					mChat.setOpen(true);
				}
				mChat.sendMessage(msgToSend);
//				User user = User.fromJID(mContact.getJID());
				// mDataAdapter.sendMessage(user.id, user.name != null ?
				// user.name + ":" + inputContent : inputContent);
				mHistoryMessages.add(msgToSend);
				isBottom = true;
				playRegisteredTranscript();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// final String self = getString(R.string.chat_self);
			mInputField.setText(null);
		}
	}

	/**
	 * Change the displayed chat.
	 * 
	 * @param contact
	 *            the targeted contact of the new chat
	 * @throws RemoteException
	 *             If a Binder remote-invocation error occurred.
	 */
	private void changeCurrentChat(Contact contact) throws RemoteException {
		Log.i(Const.TAG, "changeCurrentChat");
		if (mChat != null) {
			mChat.setOpen(false);
			mChat.removeMessageListener(mMessageListener);
		}
		mChat = mChatManager.getChat(contact);
		if (mChat != null) {
			mChat.setOpen(true);
			mChat.addMessageListener(mMessageListener);
			mChatManager.deleteChatNotification(mChat);
		} else {
			mChat = mChatManager.createChat(mContact, mMessageListener);
			mChat.setOpen(true);
		}
		mContact = contact;// mRoster.getContact(contact.getJID());
		String res = contact.getSelectedRes();
		if (mContact == null)
			mContact = contact;
		if (!"".equals(res)) {
			mContact.setSelectedRes(res);
		}
		updateContactInformations();
		playRegisteredTranscript();
	}

	/**
	 * Get all messages from the current chat and refresh the activity with
	 * them.
	 * 
	 * @throws RemoteException
	 *             If a Binder remote-invocation error occurred.
	 */
	private void playRegisteredTranscript() throws RemoteException {
		mListMessages.clear();
		List<Message> messages = new LinkedList<Message>();
		messages.addAll(mHistoryMessages);
		if (mChat != null) {
			mCurrentMessages.clear();
			for (Message message : messages) {
				if (!mHistoryMessages.contains(message)) {
					mCurrentMessages.add(message);
				}
			}
		}
		messages.addAll(mCurrentMessages);
		List<MessageText> msgList = convertMessagesList(messages);
		mListMessages.addAll(msgList);
		mMessagesListAdapter.notifyDataSetChanged();
		if (isFirstTime) {
			mMessagesListView.setSelection(mListMessages.size() - 1);
		} else {
			if (messages != null) {
				if (isBottom)
					mMessagesListView.setSelection(mListMessages.size() - 1);
				else
					mMessagesListView.setSelection(messagesRead.size());
				isBottom = false;
			}
		}
		isFirstTime = false;
	}

	List<Message> messagesRead;

	public void requestHistoryMessage(int count) {
		if (mXmppFacade != null) {
			int offset = mHistoryMessages.size() + mCurrentMessages.size();
			try {
				messagesRead = mXmppFacade.requestHistoryMessage(mTalkUser.getJID(), mMyself.getJID(), offset, count);
				Collections.reverse(messagesRead);
				mHistoryMessages.addAll(0, messagesRead);
				playRegisteredTranscript();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Convert a list of Message coming from the service to a list of
	 * MessageText that can be displayed in UI.
	 * 
	 * @param chatMessages
	 *            the list of Message
	 * @return a list of message that can be displayed.
	 */
	private List<MessageText> convertMessagesList(List<Message> chatMessages) {
		List<MessageText> result = new ArrayList<MessageText>(chatMessages.size());
		String remoteName = mContact.getName();
		String localName = getString(R.string.chat_self);
		MessageText lastMessage = null;

		for (Message m : chatMessages) {
			String name = remoteName;
			String fromBareJid = StringUtils.parseBareAddress(m.getFrom());
			if (m.getType() == Message.MSG_TYPE_ERROR) {
				lastMessage = null;
				result.add(new MessageText(fromBareJid, name, m.getBody(), true, m.getTimestamp()));
			} else if (m.getType() == Message.MSG_TYPE_INFO) {
				lastMessage = new MessageText("", "", m.getBody(), false);
				result.add(lastMessage);

			} else if (m.getType() == Message.MSG_TYPE_CHAT) {
				if (fromBareJid == null) { // nofrom or from == yours
					name = localName;
					fromBareJid = "";
				}
				if (m.getBody() != null) {
					lastMessage = new MessageText(fromBareJid, name, m.getBody(), false, m.getTimestamp(), m.getExtraType(), m.getExtraJson(), m.getSubject());
					result.add(lastMessage);
				}
			}
		}
		return result;
	}

	/**
	 * Update the contact informations.
	 */
	private void updateContactInformations() {
		// Check for a contact name update
		String name = mContact.getName();
		String res = mContact.getSelectedRes();
		if (!"".equals(res))
			name += "(" + res + ")";
		if (!mCompact) {
			if (!(mContactNameTextView.getText().toString().equals(name)))
				mContactNameTextView.setText(name);
			// Check for a contact status message update
			if (!(mContactStatusMsgTextView.getText().toString().equals(mContact.getMsgState()))) {
				mContactStatusMsgTextView.setText(mContact.getMsgState());
				Linkify.addLinks(mContactStatusMsgTextView, Linkify.WEB_URLS);
			}
		}
	}

	private void initChatManager() {
		try {
			mChatManager = mXmppFacade.getChatManager();
			if (mChatManager != null) {
				mChatManager.addChatCreationListener(mChatManagerListener);
				changeCurrentChat(mContact);
			}
			UIUtil.unblock(this);
		} catch (RemoteException e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}

	// 第一次请求数据，这个标志位决定了ListView的Selection
	boolean isFirstTime = true;

	/**
	 * {@inheritDoc}.
	 */
	private final class BeemServiceConnection implements ServiceConnection {

		/**
		 * Constructor.
		 */
		public BeemServiceConnection() {
		}

		/**
		 * {@inheritDoc}.
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppFacade = IXmppFacade.Stub.asInterface(service);
			if (mHistoryMessages.size() == 0) {
				requestHistoryMessage(10);
			}
			App app = (App) getApplication();
			if (app.isConnected()) {
				initChatManager();
			} else if (app.isConnecting()) {
				UIUtil.block(ChatActivity.this);
			} else {
				try {
					if (!BeemConnectivity.isConnected(ChatActivity.this)) {
						Hint.showTipsShort(ChatActivity.this, "连接服务器失败，请检查你的网络设置");
						mSendButton.setEnabled(false);
					} else {
						UIUtil.block(ChatActivity.this, "正在连接聊天服务器");
						new Thread() {
							@Override
							public void run() {
								try {
									Log.d(Const.TAG, "connectSync");
									if (!mXmppFacade.connectSync()) {
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												UIUtil.unblock(ChatActivity.this);
												Hint.showTipsShort(ChatActivity.this, "连接服务器失败，请稍候再试");
											}
										});
									}
								} catch (Exception e) {
									e.printStackTrace();
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											UIUtil.unblock(ChatActivity.this);
											Hint.showTipsShort(ChatActivity.this, "连接服务器失败，请稍候再试");
										}
									});
								}
							}
						}.start();
					}
				} catch (Exception e) {
					e.printStackTrace();
					UIUtil.unblock(ChatActivity.this);
					Hint.showTipsShort(ChatActivity.this, "连接服务器失败，请检查你的网络设置");
				}
			}
		}

		/**
		 * {@inheritDoc}.
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mXmppFacade = null;
			try {
				mChatManager.removeChatCreationListener(mChatManagerListener);
			} catch (RemoteException e) {
				Log.e(Const.TAG, e.getMessage());
			}
		}
	}

	/**
	 * {@inheritDoc}.
	 */
	private class OnMessageListener extends IMessageListener.Stub {

		/**
		 * Constructor.
		 */
		public OnMessageListener() {
		}

		/**
		 * {@inheritDoc}.
		 */
		@Override
		public void processMessage(IChat chat, final Message msg) throws RemoteException {
			final String fromBareJid = StringUtils.parseBareAddress(msg.getFrom());
			if (mContact.getJID().equals(fromBareJid)) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (msg.getType() == Message.MSG_TYPE_ERROR) {
							mHistoryMessages.add(msg);
							try {
								isBottom = true;
								playRegisteredTranscript();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						} else if (msg.getBody() != null) {
							mHistoryMessages.add(msg);
							try {
								isBottom = true;
								playRegisteredTranscript();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
							Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
							vibrator.vibrate(500);
							App app = (App) getApplication();
							app.getChatDBHelper().read(app.getChatDBHelper().getWritableDatabase(), mContact.getJID());
						}
					}
				});
			}
		}

		/**
		 * {@inheritDoc}.
		 */
		@Override
		public void stateChanged(IChat chat) throws RemoteException {
			final String state = chat.getState();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					String text = null;
					if ("active".equals(state)) {
						text = getString(R.string.chat_state_active);
					} else if ("composing".equals(state)) {
						text = getString(R.string.chat_state_composing);
					} else if ("gone".equals(state)) {
						text = getString(R.string.chat_state_gone);
					} else if ("inactive".equals(state)) {
						text = getString(R.string.chat_state_inactive);
					} else if ("paused".equals(state)) {
						text = getString(R.string.chat_state_active);
					}
					if (!mCompact)
						mContactChatState.setText(text);
				}
			});

		}

		@Override
		public void otrStateChanged(final String otrState) throws RemoteException {
		}
	}

	/**
	 * This class is in charge of getting the new chat in the activity if
	 * someone talk to you.
	 */
	private class ChatManagerListener extends IChatManagerListener.Stub {

		/**
		 * Constructor.
		 */
		public ChatManagerListener() {
		}

		@Override
		public void chatCreated(IChat chat, boolean locally) {
			if (locally)
				return;
			try {
				String contactJid = mContact.getJIDWithRes();
				String chatJid = chat.getParticipant().getJIDWithRes();
				if (chatJid.equals(contactJid)) {
					// This should not be happened but to be sure
					if (mChat != null) {
						mChat.setOpen(false);
						mChat.removeMessageListener(mMessageListener);
					}
					mChat = chat;
					mChat.setOpen(true);
					mChat.addMessageListener(mMessageListener);
					mChatManager.deleteChatNotification(mChat);
				}
			} catch (RemoteException ex) {
				Log.e(Const.TAG, "A remote exception occurs during the creation of a chat", ex);
			}
		}

		@Override
		public void messageArrived(Message msg) throws RemoteException {

		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mMessagesListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
			requestHistoryMessage(20);
			pullDownView.endUpdate("");
			super.onPostExecute(result);
		}
	}

	private class MyDataCallback implements DataCallback {

		@Override
		public void callback(android.os.Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_USER: {
				UserDetailsResult result = (UserDetailsResult) msg.obj;
				if (result != null) {
					mTalkUser = result.user;
					if (mTalkUser != null) {
						setTitle(mTalkUser.name);
						mMessagesListAdapter.setData(mTalkUser, mMyself);
						mMessagesListAdapter.notifyDataSetChanged();
					}else{
						AppLogger.i("MyDataCallback user is null");
					}
				}
			}
				break;
			default:
				break;
			}
		}
	}

	private class ConnectionBroadcastReceiver extends BeemBroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String intentAction = intent.getAction();
			if (intentAction.equals(BEEM_CONNECTION_CREATED)) {
				if (mXmppFacade != null) {
					mSendButton.setEnabled(true);
					initChatManager();
				}
			}
			super.onReceive(context, intent);
		}

	}

	@Override
	public void onBackPressed() {
		if (mEmojiGridContainer.getVisibility() == View.VISIBLE) {
			mEmojiGridContainer.setVisibility(View.GONE);
			mEmojiButton.setBackgroundResource(R.drawable.emoji_button);
		} else
			super.onBackPressed();
	}

	void showBottom() {
		// 给个time
		mMessagesListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mMessagesListView.setSelection(mListMessages.size() - 1);
			}
		}, 250);
	}
	
	private void registerBroad() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(MallFragment.BRODCAST_STICKER_CHANGED);
		registerReceiver(mBatInfoReceiver, filter);
	}
	
	private void unRegisterBroad(){
		unregisterReceiver(mBatInfoReceiver);
	}
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (MallFragment.BRODCAST_STICKER_CHANGED.equals(action)) {
				mEmojiGridContainer.reLoadProduct();
			}
		}
	};

}

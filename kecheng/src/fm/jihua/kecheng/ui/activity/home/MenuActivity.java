package fm.jihua.kecheng.ui.activity.home;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import fm.jihua.chat.service.Message;
import fm.jihua.chat.service.aidl.IChat;
import fm.jihua.chat.service.aidl.IChatManager;
import fm.jihua.chat.service.aidl.IChatManagerListener;
import fm.jihua.chat.service.aidl.IXmppFacade;
import fm.jihua.chat.utils.BeemBroadcastReceiver;
import fm.jihua.chat.utils.BeemConnectivity;
import fm.jihua.chat.utils.DatabaseHelper;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.CommonUtils;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.common.utils.Utility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.data.adapters.AddFriendsByClassmates;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.ClassTimeResult;
import fm.jihua.kecheng.rest.entities.CoursesResult;
import fm.jihua.kecheng.rest.entities.EventsResult;
import fm.jihua.kecheng.rest.entities.OfflineData;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UsersResult;
import fm.jihua.kecheng.rest.entities.OfflineData.Operation;
import fm.jihua.kecheng.rest.entities.SecretPost;
import fm.jihua.kecheng.rest.entities.SecretPostsResult;
import fm.jihua.kecheng.rest.entities.mall.MyProductsResult;
import fm.jihua.kecheng.rest.entities.sticker.PasteResult;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseMenuActivity;
import fm.jihua.kecheng.ui.activity.SplashActivity;
import fm.jihua.kecheng.ui.activity.course.AddCourseActivity;
import fm.jihua.kecheng.ui.activity.course.ImportCoursesActivity;
import fm.jihua.kecheng.ui.activity.profile.MyProfileFragment;
import fm.jihua.kecheng.ui.fragment.ActivitiesFragment;
import fm.jihua.kecheng.ui.fragment.BaseFragment;
import fm.jihua.kecheng.ui.fragment.EventsFragment;
import fm.jihua.kecheng.ui.fragment.ExaminationsFragment;
import fm.jihua.kecheng.ui.fragment.FriendsFragment;
import fm.jihua.kecheng.ui.fragment.MainFragment;
import fm.jihua.kecheng.ui.fragment.MenuFragment;
import fm.jihua.kecheng.ui.fragment.SettingsFragment;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.UserStatusUtils;
import fm.jihua.kecheng_hs.R;

@SuppressLint("CommitPrefEdits")
public class MenuActivity extends BaseMenuActivity{
	boolean mInited;
	SparseArray<Fragment> fragments = new SparseArray<Fragment>();
	@SuppressWarnings("rawtypes")
	SparseArray<Class> fragmentClasses = new SparseArray<Class>();
	MenuFragment menuFragment;
	DataAdapter dataAdapter;
	int currentFragmentId;
	
	public final static String HIGHLIGHT_KEY_SECRET_POST = "HIGHLIGHT_KEY_SECRET_POST"; 
	public final static String HIGHLIGHT_KEY_EVENT = "HIGHLIGHT_KEY_EVENT"; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    dataAdapter = new DataAdapter(this, new MyDataCallback());
	    
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		setContentView(R.layout.content_frame);
		
//		initFragments(savedInstanceState);
		initFragmentClasss();
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			mFrag = new MenuFragment();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
			currentFragmentId = MenuFragment.MENU_MY_COURSES;
		} else {
			mFrag = (BaseFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
			currentFragmentId = savedInstanceState.getInt("selected_menu_id");
		}
		menuFragment = (MenuFragment) mFrag;
		configSlidingMenu(true);
	    init();
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		toActivity();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selected_menu_id", currentFragmentId);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	private void toActivity(){
		if (getIntent().getBooleanExtra("TO_CHAT", false)) {
	    	setMainContent(MenuFragment.MENU_MESSAGES);
		}else if (getIntent().getBooleanExtra("TO_EXAMINATION", false)) {
	    	setMainContent(MenuFragment.MENU_EXAMINATIONS);
		}else {
			setMainContent(currentFragmentId);
			final App app = (App)getApplication();
			if (UserStatusUtils.get().isNewUser()) {
				findViewById(R.id.content_frame).getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					
					@Override
					public void onGlobalLayout() {
						boolean enableImport = app.isSupportImport();
						if(enableImport && !app.isKnowImportCourse()){
							startActivityForResult(new Intent(MenuActivity.this, ImportCoursesActivity.class), Const.INTENT_ADD_COURSE);
						} else {
							startActivityForResult(new Intent(MenuActivity.this, AddCourseActivity.class), Const.INTENT_ADD_COURSE);
						}
						if (Compatibility.isCompatible(16)) {
							findViewById(R.id.content_frame).getViewTreeObserver().removeOnGlobalLayoutListener(this);
						}else {
							findViewById(R.id.content_frame).getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}
					}
				});
			}
		}
	}
	
	private void configSlidingMenu(boolean phone) {
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.menu_shadow);
        if (phone)
            slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        else
            slidingMenu.setBehindOffset(Utility.getScreenWidth());

        slidingMenu.setFadeDegree(0.35f);
//        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindScrollScale(0.0f);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        slidingMenu.setOnPageScrollListener(new SlidingMenu.OnPageScrollListener() {
//            @Override
//            public void onPageScroll() {
//                LongClickableLinkMovementMethod.getInstance().setLongClickable(false);
//                (getFriendsTimeLineFragment()).clearActionMode();
//            }
//        });
    }
	
	private void initFragmentClasss() {
		fragmentClasses.clear();
		fragmentClasses.append(MenuFragment.MENU_MY_COURSES, MainFragment.class);
		fragmentClasses.append(MenuFragment.MENU_MY_PROFILE, MyProfileFragment.class);
		fragmentClasses.append(MenuFragment.MENU_SETTING, SettingsFragment.class);
		fragmentClasses.append(MenuFragment.MENU_FRIENDS, FriendsFragment.class);
		fragmentClasses.append(MenuFragment.MENU_EXAMINATIONS, ExaminationsFragment.class);
		fragmentClasses.append(MenuFragment.MENU_MESSAGES, ActivitiesFragment.class);
		fragmentClasses.append(MenuFragment.MENU_ACTIVITY, EventsFragment.class);
		fragmentClasses.append(MenuFragment.MENU_SETTING, SettingsFragment.class);
//		fragmentClasses.append(MenuFragment.MENU_LEADERBPARDS, LeaderboardsFragment.class);
	}
	
	@Override
	public void onBackPressed() {
		BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag(fragmentClasses.get(currentFragmentId)));
		boolean pressed = false;
		if (currentFragment != null) {
			pressed = currentFragment.onBackPressed();
		}
		if (!isKeyLocked && !pressed) {
			showMenuOrQuit();
		}
	}
	
	void showMenuOrQuit(){
		if (!getSlidingMenu().isMenuShowing()) {
			toggle();
		} else {
			showQuitDialog();
		}
	}
	
	/* 消息处理 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(!isKeyLocked){
			if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				onBackPressed();
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
				if(currentFragmentId == MenuFragment.MENU_MY_COURSES && !getSlidingMenu().isMenuShowing()){
					MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag(fragmentClasses.get(currentFragmentId)));
					mainFragment.mainWeekView.toggleMenu(null);
					return true;
				}
				return false;
			}else {
				return super.onKeyUp(keyCode, event);
			}
		}else{
			return true;
		}
		
	}
	
	public void showTitleHintPoint(boolean isShow){
		if(isShow){
			menuFragment.refreshStatus();
		}else{
			menuFragment.dismissTitleRedPoint();
		}
	}

	private void showQuitDialog(){
		new AlertDialog.Builder(MenuActivity.this)
		.setTitle("提示")
		.setMessage("是否退出课程格子？")
		.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						finish();
						App app = (App)getApplication();
						app.exit();
					}
				})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						// 按钮事件
						dialoginterface.dismiss();
					}
				}).show();
	}
	
	protected void onResume() {
	    super.onResume();
	    if (!mBinded) {
	    	this.registerReceiver(mBroadcastReceiver, new IntentFilter(
					BeemBroadcastReceiver.BEEM_CONNECTION_CREATED));
			bindService(SERVICE_INTENT, mConn, BIND_AUTO_CREATE);
			mBinded = true;
		}
	    FragmentManager manager = getSupportFragmentManager();
	    BaseFragment currentFragment = null;
	    currentFragment = (BaseFragment)manager.findFragmentByTag(getFragmentTag(fragmentClasses.get(currentFragmentId)));
	    if(currentFragment != null){
	    	currentFragment.onShow();
	    }
	}
	
	protected void onPause() {
	    super.onPause();
	    try {
			if (mChatManager != null)
				mChatManager.removeChatCreationListener(mChatManagerListener);
		} catch (RemoteException e) {
			Log.e(Const.TAG, e.getMessage());
		}
		if (mBinded) {
			unbindService(mConn);
			this.unregisterReceiver(mBroadcastReceiver);
			mBinded = false;
		}
		mXmppFacade = null;
		mChatManager = null;
	}
	
    private void init() {
    	if (!mInited) {
    		MobclickAgent.updateOnlineConfig(this);
    		checkForNewVersion();
            MobclickAgent.onError(this);
            UMFeedbackService.setGoBackButtonVisible();
            UMFeedbackService.enableNewReplyNotification(this, NotificationType.AlertDialog);
            MobclickAgent.getConfigParams(this, "force_follow_weibo");
            MobclickAgent.getConfigParams(this, "auto_follow_weibo");
            if (!((App)getApplication()).isShowAddCourseTutorial()) {
				// findViewById(R.id.iv_tutorial).setVisibility(View.VISIBLE);
				((App)getApplication()).setShowAddCourseTutorial(true);
			}
            if (!((App)getApplication()).isCreatedShortcut() && !CommonUtils.isShortcutAdded(this)) {
				if (!"Xiaomi".equalsIgnoreCase(android.os.Build.BRAND)) {
					CommonUtils.createShortcut(this, SplashActivity.class,
							R.drawable.icon);
					((App) getApplication()).setCreatedShortcut(true);
				}
			}
    		if (!hasAddedWelcomeMessage()) {
    			addWelcomeMessage();
    		}
    		if (UserStatusUtils.get().isReloginUser()) {
				UIUtil.block(this);
			}
    		dataAdapter.getConfigParams();
    		dataAdapter.getClassmates(1, 1, AddFriendsByClassmates.CLASSMATES_SAME_CLASS);
    		dataAdapter.getSemester();
    		dataAdapter.getTags(false);
    		dataAdapter.getCourses();
    		dataAdapter.getSecretPosts(false, 1, Const.DATA_COUNT_PER_REQUEST);
    		dataAdapter.getRemoteMineProducts();
    		dataAdapter.getEvents(1, 1);
    		syncClassTime();
    		uploadStickerOfflineData();
            mInited = true;
		}
	}
    
    private void uploadStickerOfflineData(){
    	final App app = (App)getApplication();
    	List<OfflineData<UserSticker>> offlineDatas = app.getDBHelper().getOfflineData(app.getUserDB(), UserSticker.class);
    	for (final OfflineData<UserSticker> offlineData : offlineDatas) {
			if (offlineData.operation != Operation.ADD || offlineData.semesterId == app.getActiveSemesterId()) {
				UserSticker paster = (UserSticker) offlineData.getObject();
				DataAdapter dataAdapter = getDataAdapter(offlineData.id, paster);
				switch (offlineData.operation) {
				case ADD:
					dataAdapter.pasterSticker(paster);
					break;
				case MODIFY:
					dataAdapter.modifySticker(paster);
					break;
				case DELETE:
					dataAdapter.removeSticker(paster);
				default:
					break;
				}
			}
		}
    }
    
    private DataAdapter getDataAdapter(final int offlineDataId, final UserSticker paster){
		
		return new DataAdapter((Activity) this, new DataCallback() {

			@Override
			public void callback(android.os.Message msg) {
				List<UserSticker> userStickerList = App.getInstance().getSavedStickerList();
				switch (msg.what) {
				case DataAdapter.MESSAGE_STICKER_PASTE: {
					PasteResult pasterResult = (PasteResult) msg.obj;
					if (pasterResult != null) {
						AppLogger.i("paste_sticker  "+pasterResult.toString());
						if (pasterResult.success) {
							paster.id = pasterResult.new_id;
							userStickerList.add(paster);
							App.getInstance().saveStickerList(userStickerList);
							refreshMainFragment();
						}else{
							AppLogger.i("paste_sticker_is_failed");
						}
						App.getInstance().getDBHelper().deleteOfflineData(App.getInstance().getUserDB(), offlineDataId);
					}
					break;
				}
				case DataAdapter.MESSAGE_STICKER_MODIFY: {
					BaseResult pasterResult = (BaseResult) msg.obj;
					if (pasterResult != null) {
						if (!pasterResult.success) {
							AppLogger.i("modity_sticker_is_failed");
						}else {
							UserSticker oldPaster = (UserSticker) CommonUtils.findById(userStickerList, paster.id);
							if (oldPaster != null && userStickerList.remove(oldPaster)) {
								userStickerList.add(paster);
								App.getInstance().saveStickerList(userStickerList);
								refreshMainFragment();
							}
						}
						App.getInstance().getDBHelper().deleteOfflineData(App.getInstance().getUserDB(), offlineDataId);
					}
					break;
				}
				case DataAdapter.MESSAGE_STICKER_REMOVE:
					BaseResult baseResult = (BaseResult) msg.obj;
					if (baseResult != null) {
						App.getInstance().getDBHelper().deleteOfflineData(App.getInstance().getUserDB(), offlineDataId);
						if (baseResult.success) {
							UserSticker oldPaster = (UserSticker) CommonUtils.findById(userStickerList, paster.id);
							if (oldPaster != null && userStickerList.remove(oldPaster)) {
								App.getInstance().saveStickerList(userStickerList);
								refreshMainFragment();
							}
						}
					}
					break;
				default:
					break;
				}
			}
		});
	}
    
    void checkForNewVersion(){
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
	        @Override
	        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
	        	if (updateStatus == 2 && updateInfo != null) {
	        		if(updateInfo.hasUpdate){
		        		updateStatus = 0;
		        	}else {
						updateStatus = 1;
					}
				}
	            switch (updateStatus) {
	            case 0: // has update
	            	if (updateInfo != null) {
						App app = (App)getApplication();
						app.setNewestVersion(updateInfo.version);
						if (BeemConnectivity.isWifi(MenuActivity.this) || (System.currentTimeMillis() - app.getLastTimeUpdateNotify() > Const.UPDATE_NOTIFY_INTERVAL)) {
							app.setLastTimeUpdateNotify(System.currentTimeMillis());
							UmengUpdateAgent.showUpdateDialog(MenuActivity.this, updateInfo);
						}
					}
	                break;
	            }
	        }
		});
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.update(this);
	}
    
    //时间模式初始设置
	private void syncClassTime() {
		if(App.getInstance().getTimeModeList().size() == 0){
			dataAdapter.getClassTime();
		}else{
			//同步一下本地数据
			dataAdapter.saveClassTime(App.getInstance().getTimeModeString());
		}
	}
    
    class MyDataCallback implements DataCallback{

		@Override
		public void callback(android.os.Message msg) {
			try {
				App app = (App) getApplication();
				switch (msg.what) {
				case DataAdapter.MESSAGE_GET_CLASSMATES:
					if (msg.obj != null) {
						UsersResult result = (UsersResult) msg.obj;
						if (result.success) {
							ArrayList<User> friends = new ArrayList<User>();
							friends.addAll(Arrays.asList(result.users));
							if (result.finished && friends.size() != 0) {
								Map<String, String> map = new HashMap<String, String>();
								map.put(Const.CONFIG_PARAM_ENABLE_IMPORT, "true");
								App.getInstance().saveConfigParams(map);
							}
						}
					}
					break;
				case DataAdapter.MESSAGE_GET_CONFIG_PARAMS:
					if (!isFinishing()) {
						menuFragment.refreshStatus();
					}
					break;
				case DataAdapter.MESSAGE_GET_COURSES: {
					UIUtil.unblock(MenuActivity.this);
					CoursesResult result = (CoursesResult) msg.obj;
					if (result == null) {
						Hint.showTipsShort(MenuActivity.this, "获取课程失败，请检查网络是否正常");
					} else {
						if (!result.finished && UserStatusUtils.get().isReloginUser()) {
							UIUtil.block(MenuActivity.this);
						}
						if (!TextUtils.isEmpty(result.theme_name))
							app.setCurrentStyleName(result.theme_name);
						if(currentFragmentId == MenuFragment.MENU_MY_COURSES){
							MainFragment fragment = ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame));
							if (fragment != null) {
								fragment.refresh();
							}
						}
						AppLogger.i(result.toString());
					}
				}
					break;
				case DataAdapter.MESSAGE_GET_SECRET_POSTS: {

					SecretPostsResult result = (SecretPostsResult) msg.obj;
					if (result != null && result.success) {
						List<SecretPost> postLists = Arrays.asList(result.secret_posts);
						if (postLists.size() > 0) {
							SecretPost secretPost = postLists.get(0);
							int lastPostId = App.getInstance().getPostLastReadId();
							if (lastPostId == -1 || secretPost.id != lastPostId) {
								App.getInstance().putBooleanSharedValue(HIGHLIGHT_KEY_SECRET_POST, true);
							}
						}
					}
					menuFragment.refreshStatus();
				}
					break;
				case DataAdapter.MESSAGE_GET_MY_PRODUCTS: {
					MyProductsResult result = (MyProductsResult) msg.obj;
					if (result != null && result.success) {
						refreshMainFragment();
					}
				}
					break;
				case DataAdapter.MESSAGE_GET_USER_CLASSTIME: {
					ClassTimeResult result = (ClassTimeResult) msg.obj;
					String resultString;
					if (result != null && result.success) {
						resultString = result.time_json;
					} else {
						resultString = Const.CLASSTIME_DEFAULT;
					}
					App.getInstance().saveTimeModeString(resultString);
				}
					break;
				case DataAdapter.MESSAGE_GET_EVENT: {
					EventsResult result = (EventsResult) msg.obj;
					if (result != null && result.success) {
						if (result.events.length == 1 && result.events[0] != null) {
							int eventLastLocalId = App.getInstance().getEventLastLocalId();
							if (eventLastLocalId == -1 || result.events[0].id != eventLastLocalId) {
								App.getInstance().putBooleanSharedValue(HIGHLIGHT_KEY_EVENT, true);
							}
						}
					}
					menuFragment.refreshStatus();
					break;
				}
				
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
    }
    
    public void refreshMainFragment(){
    	if(currentFragmentId == MenuFragment.MENU_MY_COURSES){
    		MainFragment fragment = ((MainFragment)getSupportFragmentManager().findFragmentById(R.id.content_frame));
    		if (fragment != null) {
    			fragment.refresh();
    		}
		}
    }
	
	boolean hasAddedWelcomeMessage(){
		App app = (App)getApplication();
		return app.getDefaultPreferences().getBoolean("has_added_welcome_message", false);
	}
	
	void addWelcomeMessage(){
		App app = (App)getApplication();
		DatabaseHelper dbHelper = app.getChatDBHelper();
		Message message = new Message(0, "23@"+Const.CHAT_HOST, app.getMyself().getJID(), "欢迎来到课程格子", 0, Message.UNREAD);
		dbHelper.addMessage(dbHelper.getWritableDatabase(), message);
		Editor editor = app.getDefaultPreferences().edit();
		editor.putBoolean("has_added_welcome_message", true);
		App.commitEditor(editor);
	}
   
	private String getFragmentTag(@SuppressWarnings("rawtypes") Class fragment){
		return fragment.getName();
	}
	
    public synchronized void setMainContent(int menuId){
    	FragmentManager manager = getSupportFragmentManager();
    	Fragment currentFragment = null;
    	boolean firstTimeShowContent = false;
    	if (currentFragmentId != 0) {
    		currentFragment = manager.findFragmentByTag(getFragmentTag(fragmentClasses.get(currentFragmentId)));
		}else {
			firstTimeShowContent = true;
		}
    	if ((currentFragmentId == menuId && currentFragment != null) || fragmentClasses.get(menuId) == null) {
    		getSlidingMenu().showContent();
			return;
		}
    	@SuppressWarnings("unchecked")
		final BaseFragment targetFragment = (BaseFragment)getFragment(fragmentClasses.get(menuId));
    	String tag = getFragmentTag(targetFragment.getClass());
    	currentFragmentId = menuId;
    	if (targetFragment != null && (currentFragment == null || currentFragment.getTag() != tag)) {
    		FragmentTransaction act = manager.beginTransaction();
//    		act.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    		if (currentFragment != null) {
    			act.hide(currentFragment);
//    			act.detach(currentFragment);
			}
    		boolean shouldAdd  = false;
    		if (targetFragment != null) {
				if (targetFragment.isHidden()) {
					act.show(targetFragment);
					targetFragment.initTitlebar();
					getSlidingMenu().setOnClosedListener(new OnClosedListener() {
						
						@Override
						public void onClosed() {
							targetFragment.onShow();
							getSlidingMenu().setOnClosedListener(null);
						}
					});
//					act.attach(fragment);
				}else if (!targetFragment.isAdded()) {
					shouldAdd = true;
					act.add(R.id.content_frame, targetFragment, tag);
					getSlidingMenu().setOnClosedListener(null);
				}
			}
    		if (!act.isEmpty()) {
                act.commit();
        		manager.executePendingTransactions();
        		Handler h = new Handler();
        		if (shouldAdd && !firstTimeShowContent) {
        			h.postDelayed(new Runnable() {
	    				public void run() {
	    					getSlidingMenu().showContent();
	    				}
	    			}, 100);
				}else {
					h.postDelayed(new Runnable() {
	    				public void run() {
	    					getSlidingMenu().showContent();
	    				}
	    			}, 48);
				}
            }
//    		menuFragment.setCurrentMenuId(menuId);
		}
    }
	
	private static final Intent SERVICE_INTENT = new Intent();
	static {
		SERVICE_INTENT.setComponent(new ComponentName("fm.jihua.kecheng_hs",
				"fm.jihua.kecheng.BeemService"));
	}
	private IChatManager mChatManager;
	private final IChatManagerListener mChatManagerListener = new ChatManagerListener();

	private final ServiceConnection mConn = new BeemServiceConnection();
	private final BeemBroadcastReceiver mBroadcastReceiver = new ConnectionBroadcastReceiver();
	private IXmppFacade mXmppFacade;
	private boolean mBinded;
	private void initChatManager() {
		try {
			mChatManager = mXmppFacade.getChatManager();
			if (mChatManager != null) {
				mChatManager.addChatCreationListener(mChatManagerListener);
			}
		} catch (RemoteException e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}
	
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
			App app = (App)getApplication();
			if (app.isConnected()) {
				initChatManager();
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

	private class ChatManagerListener extends IChatManagerListener.Stub {

		/**
		 * Constructor.
		 */
		public ChatManagerListener() {
		}

		@Override
		public void chatCreated(IChat chat, boolean locally) {

		}

		@Override
		public void messageArrived(Message msg) throws RemoteException {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					menuFragment.refreshStatus();
					if (menuFragment.getCurrentMenuId() == MenuFragment.MENU_MESSAGES && getSupportFragmentManager().findFragmentById(R.id.content_frame) instanceof ActivitiesFragment) {
						ActivitiesFragment fragment = (ActivitiesFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
						if (fragment != null) {
							fragment.refresh();
						}
					}
				}
			});
		}
	}
	
	private class ConnectionBroadcastReceiver extends BeemBroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			String intentAction = intent.getAction();
			if (intentAction.equals(BEEM_CONNECTION_CREATED)) {
				if (mXmppFacade != null) {
					initChatManager();
				}
			}
			super.onReceive(context, intent);
		}
	}
	
	public <T> T getFragment(Class<T> fragmentClass) {
        @SuppressWarnings("unchecked")
		T fragment = (T) getSupportFragmentManager().findFragmentByTag(
        		getFragmentTag(fragmentClass));
        if (fragment == null) {
            try {
				fragment = fragmentClass.newInstance();
			} catch (InstantiationException e) {
				AppLogger.printStackTrace(e);
			} catch (IllegalAccessException e) {
				AppLogger.printStackTrace(e);
			}
            ((Fragment)fragment).setArguments(new Bundle());
        }
        return fragment;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.avatar:
		case R.id.small_avatar0:
		case R.id.small_avatar1:
		case R.id.small_avatar2:
		case R.id.small_avatar3:
		case R.id.small_avatar4:
	    	if (currentFragmentId != 0) {
	    		BaseFragment currentFragment = null;
	    		FragmentManager manager = getSupportFragmentManager();
	    		currentFragment = (BaseFragment) manager.findFragmentByTag(getFragmentTag(fragmentClasses.get(currentFragmentId)));
	    		currentFragment.onClick(v);
			}
			break;
		default:
			super.onClick(v);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			FragmentManager manager = getSupportFragmentManager();
    		Fragment currentFragment = manager.findFragmentByTag(getFragmentTag(fragmentClasses.get(currentFragmentId)));
    		currentFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

}

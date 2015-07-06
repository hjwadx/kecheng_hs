package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.PluginBean;
import fm.jihua.kecheng.rest.entities.SimpleMenuItem;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.baoman.BaoManListActivity;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.mall.MallFragment;
import fm.jihua.kecheng.ui.activity.register.RegisterActivity;
import fm.jihua.kecheng.ui.adapter.MenuAdapter;
import fm.jihua.kecheng.ui.helper.DialogUtils;
import fm.jihua.kecheng.ui.widget.PluginMenuView;
import fm.jihua.kecheng.utils.Const;

public class MenuFragment extends BaseFragment implements OnItemClickListener, OnClickListener{
	
	public List<SimpleMenuItem> mMenuList;
	public Map<Object, Integer> mActivityMap;
	
	public static final int MENU_MY_PROFILE = 1;
	public static final int MENU_MESSAGES = 2;
	public static final int MENU_LOG_OUT = 3;
	public static final int MENU_QUIT = 4;
	public static final int MENU_SETTING = 5;
	public static final int MENU_MY_COURSES = 6;
	public static final int MENU_SECRET_POSTS = 7;
	public static final int MENU_FRIENDS = 8;
	public static final int MENU_EXAMINATIONS = 9;
	public static final int MENU_ACTIVITY = 10;
//	public static final int MENU_BAOMAN = 11;
	

	public static final int MENU_EMPTY = 30;
	
	public static final int MENU_TUTORIAL = 100;
	
	Integer mSelectedMenuItemId;
	MenuActivity parent;
	App app;
	private ListView mainMenuList; 
	private ListView secondaryMenuList;
	MenuAdapter menuAdapter;
	MenuAdapter menuSecondaryAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu_panel, container, false);
		mainMenuList = (ListView) v.findViewById(R.id.main_list);
		secondaryMenuList = (ListView) v.findViewById(R.id.secondary_list);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parent = (MenuActivity) getActivity();
//		if (prevActivity != null) {
//			menus = new SimpleMenuItem[prevActivity.mMenuList.size()];
//			menus = prevActivity.mMenuList.toArray(menus);
//			mSelectedMenuItem = prevActivity.getMenuItem();
//		}
//		if (menus == null) {
//			SimpleMenuItem menuItem = new SimpleMenuItem(5, "退出", false);
//			menus[0] = menuItem;
//		}
		if (savedInstanceState == null) {
			mSelectedMenuItemId = MENU_MY_COURSES;
		}else {
			mSelectedMenuItemId = savedInstanceState.getInt("selected_menu_id");
		}
		app = (App)getActivity().getApplication();
		menuAdapter = new MenuAdapter(getMainMenus());
		mainMenuList.setAdapter(menuAdapter);
		mainMenuList.setOnItemClickListener(this);
		menuSecondaryAdapter = new MenuAdapter(getSecondaryMenus());
		secondaryMenuList.setAdapter(menuSecondaryAdapter);
		secondaryMenuList.setOnItemClickListener(this);
		initPlugins();
		findViewById(R.id.baoman).setOnClickListener(this);
		parent.getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
//                LongClickableLinkMovementMethod.getInstance().setLongClickable(true);
//            	if (adapter.getSelected() != mSelectedMenuItemId) {
//            		onOptionsItemSelected( adapter.getSelected());
//				}
            }
        });
		registeRefreshBroadcast();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selected_menu_id", mSelectedMenuItemId);
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onEvent(parent, "enter_pathmenu_view");
		refreshStatus();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisteRefreshBroadcast();
	}
	
	//添加模块的时候，不要忘记修改此处的位置
//	int positionEvent = 0;
	int positionMessage = 1;
	int positionSetting = 2;
	
	int positionSecretPost = 0;
	
//	private void initPosition() {
//		positionSecretPost = 0;
//		if (App.getInstance().isSupportEvent()) {
//			positionEvent = 0;
//			positionMessage = 2;
//			positionSetting = 3;
//		} else {
//			positionMessage = 1;
//			positionSetting = 2;
//		}
//	}

	
	public void refreshStatus() {
//		initPosition();
		dismissTitleRedPoint();

		// message
		List<SimpleMenuItem> mainMenus2 = getSecondaryMenus();
		int count = app.getChatDBHelper().getUnreadCount(app.getChatDBHelper().getWritableDatabase());
		SimpleMenuItem simpleMenuItemMessage = mainMenus2.get(positionMessage);
		if (count > 0) {
			simpleMenuItemMessage.setHighlightText(String.valueOf(count));
			showTitlebarHint(true);
		}else{
			simpleMenuItemMessage.setHighlightText("");
		}
		menuSecondaryAdapter.setData(mainMenus2);
		menuSecondaryAdapter.notifyDataSetChanged();
		
		// setting
		List<String> highlightSettings = app.getUnHandledParams(Const.CONFIG_PARAM_HIGHLIGHT_SETTING);
		int settingCount = highlightSettings.size();
		settingCount = app.getNewestVersion().compareToIgnoreCase(Const.getAppVersionName(parent)) > 0 ? settingCount + 1 : settingCount;
		List<SimpleMenuItem> mainMenus = getMainMenus();
		// SimpleMenuItem simpleMenuItem = mainMenus.get(0);
		
		SimpleMenuItem simpleMenuItemSetting = mainMenus2.get(positionSetting);
		if (settingCount > 0) {
			simpleMenuItemSetting.setHighlightText(String.valueOf(settingCount));
			// simpleMenuItem.showSettingIconHint(true);
			showTitlebarHint(true);
		}else{
			simpleMenuItemSetting.setHighlightText("");
			// simpleMenuItem.showSettingIconHint(false);
		}
		menuAdapter.setData(mainMenus);
		menuAdapter.notifyDataSetChanged();
		
		//secret_post
		if (App.getInstance().getBooleanSharedValue(MenuActivity.HIGHLIGHT_KEY_SECRET_POST)) {
			LinearLayout layout_child = (LinearLayout) menuView.getChildAt(positionSecretPost);
			ImageView image = (ImageView) layout_child.findViewById(R.id.menu_redpoint);
			image.setVisibility(View.VISIBLE);
			showTitlebarHint(true);
		}
	}
	
	public void dismissTitleRedPoint(){
		showTitlebarHint(false);
	}
	
	void showTitlebarHint(boolean isShow) {
		MenuActivity activity = (MenuActivity) getActivity();
		if (isShow) {
			activity.getKechengActionBar().showRedPoint();
		} else {
			activity.getKechengActionBar().hideRedPoint();
		}
	}
	
	List<SimpleMenuItem> getMainMenus(){
		List<SimpleMenuItem> menuList = new ArrayList<SimpleMenuItem>();
		if(menuList.size() == 0){
			SimpleMenuItem menuItem;
			menuItem = new SimpleMenuItem(MENU_MY_COURSES, "我的课表", "", R.drawable.menu_icon_timetable);
			// menuItem.showSettingIcon();
			menuList.add(menuItem);
			menuItem = new SimpleMenuItem(MENU_MY_PROFILE, "我的资料", "", R.drawable.menu_icon_profile);
			menuList.add(menuItem);
		}
		return menuList;
	}
	
	List<SimpleMenuItem> getSecondaryMenus(){
		List<SimpleMenuItem> menuList = new ArrayList<SimpleMenuItem>();
		if(menuList.size() == 0){
			SimpleMenuItem menuItem;
			if(App.getInstance().isSupportEvent()){
				menuItem = new SimpleMenuItem(MENU_ACTIVITY, "活动", "", R.drawable.menu_icon_activity);menuList.add(menuItem);
			}
//			menuItem = new SimpleMenuItem(MENU_BAOMAN, "暴走漫画", "", R.drawable.menu_icon_friendlist);menuList.add(menuItem);
			menuItem = new SimpleMenuItem(MENU_FRIENDS, "好友", "", R.drawable.menu_icon_friendlist);menuList.add(menuItem);
			menuItem = new SimpleMenuItem(MENU_MESSAGES, "消息", "", R.drawable.menu_icon_messagelist);menuList.add(menuItem);
			menuItem = new SimpleMenuItem(MENU_SETTING, "设置", "", R.drawable.menu_icon_setting);menuList.add(menuItem);
		}
		return menuList;
	}
	
	List<PluginBean> getPluginMenus(){
		List<PluginBean> plugins = new ArrayList<PluginBean>();
		PluginBean plugin;
		plugin = new PluginBean(SecretPostFragment.class, R.drawable.menu_icon_widget_treehole, "格子树洞");
		plugins.add(plugin);
		plugin = new PluginBean(ExaminationsFragment.class, R.drawable.menu_icon_widget_exam, "考试倒计时");
		plugins.add(plugin);
		plugin = new PluginBean(MallFragment.class, R.drawable.menu_icon_widget_market, "贴图商店");
		plugins.add(plugin);
//		plugin = new PluginBean("fm.jihua.kecheng.exam", R.drawable.menu_icon_widget_exam,R.drawable.menu_icon_widget_note, "考试哈啦啦");plugins.add(plugin);
		return plugins;
	}

	PluginMenuView menuView;
	
	void initPlugins(){
		List<PluginBean> plugins = getPluginMenus();
		menuView = (PluginMenuView) findViewById(R.id.plugin_list);
		menuView.setData(plugins);
	}
	
	@Override
	public void startActivity(Intent intent) {
		intent.putExtra(BaseActivity.INTENT_THEME, R.style.XTheme_Transparent_Popup);
		super.startActivity(intent);
	}
	
	public int getCurrentMenuId(){
		if (mSelectedMenuItemId != null) {
			return mSelectedMenuItemId;
		}
		return 0;
	}
	
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position,
			long id) {
		SimpleMenuItem menuItem = (SimpleMenuItem) l.getAdapter().getItem(position);
		onOptionsItemSelected(menuItem.getItemId());
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.baoman:
//			DialogUtils.showShareAppDialog(parent, parent.getResources().getString(R.string.share_title_normal), parent.getResources().getString(R.string.share_sns_invite), "app");
			startActivity(new Intent(parent, BaoManListActivity.class));
			break;

		default:
			break;
		}
	}

	
	public boolean onOptionsItemSelected(int itemId) {
		App app = (App)parent.getApplication();
		switch (itemId){
		case MENU_MY_COURSES:
		case MENU_MY_PROFILE:
		case MENU_ACTIVITY:
		case MENU_FRIENDS:
		case MENU_MESSAGES:
		case MENU_SETTING:
			mSelectedMenuItemId = itemId;
			parent.setMainContent(itemId);
			break;
//		case MENU_BAOMAN:
//			startActivity(new Intent(parent, BaoManListActivity.class));
//			break;
		case MENU_LOG_OUT:
			app.logOut();
			Intent intent = new Intent(parent, RegisterActivity.class);
//				intent.putExtra("SHOW_LOGIN_VIEW", true);
			parent.startActivity(intent);
			parent.finish();
			return true;
		case MENU_QUIT:
			parent.finish();
			app.exit();
			return true;
		default:
			return false;
		}
		return true;
	}
	
	//添加一个用来通知刷新的广播
	
	private RefreshStatusRecevier refreshStatusRecevier;
	private void registeRefreshBroadcast(){
		
		refreshStatusRecevier = new RefreshStatusRecevier();
		IntentFilter filter = new IntentFilter();
		filter.addAction(INTENT_ACTION_REFRESH);
		getActivity().registerReceiver(refreshStatusRecevier, filter );
	}
	
	private void unregisteRefreshBroadcast(){
		getActivity().unregisterReceiver(refreshStatusRecevier);
	}
	
	public static final String INTENT_ACTION_REFRESH = "INTENT_ACTION_REFRESH";
	class RefreshStatusRecevier extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(INTENT_ACTION_REFRESH)){
				refreshStatus();
			}
			
		}
		
	}
}

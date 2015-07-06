package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.umeng.fb.UMFeedbackService;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.SimpleMenuItem;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.adapter.MenuAdapter;
import fm.jihua.kecheng.utils.Const;

public class RegisterMenuFragment extends ListFragment{
	
	public List<SimpleMenuItem> mMenuList;
	public Map<Object, Integer> mActivityMap;
	
	public static final int MENU_FEEDBACK = 3;
	public static final int MENU_QUIT = 5;
	
	public MenuAdapter adapter;
	public List<SimpleMenuItem> menuItems;
	DataAdapter dataAdapter;
	App app;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		menuItems = getMenus();
//		if (prevActivity != null) {
//			menus = new SimpleMenuItem[prevActivity.mMenuList.size()];
//			menus = prevActivity.mMenuList.toArray(menus);
//			mSelectedMenuItem = prevActivity.getMenuItem();
//		}
//		if (menus == null) {
//			SimpleMenuItem menuItem = new SimpleMenuItem(5, "退出", false);
//			menus[0] = menuItem;
//		}
//		adapter = new SimpleAdapter(getActivity(), menuItems, R.layout.menu_list_item, new String[]{"name"}, new int[]{R.id.name});
		adapter = new MenuAdapter(menuItems);
		setListAdapter(adapter);
		getListView().setCacheColorHint(0);
		getListView().setSelector(android.R.color.transparent);
		getListView().setDrawSelectorOnTop(false);
	}
	
	public void onResume() {
		super.onResume();
	};
	
	List<SimpleMenuItem> getMenus(){
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		SimpleMenuItem menuItem;
//		Map<String, Object> menu = new HashMap<String, Object>();
//		menuItem = new SimpleMenuItem(MENU_FEEDBACK, "反馈");
//		menu.put("name", menuItem.title);
//		menu.put("data", menuItem);
//		list.add(menu);
//		
//		menuItem = new SimpleMenuItem(MENU_QUIT, "退出", "", R.drawable.menu_icon_logout);
//		menu = new HashMap<String, Object>();
//		menu.put("name", menuItem.title);
//		menu.put("data", menuItem);
//		list.add(menu);
//		return list;
		
		List<SimpleMenuItem> menuList = new ArrayList<SimpleMenuItem>();
		SimpleMenuItem menuItem;
		menuItem = new SimpleMenuItem(MENU_FEEDBACK, "    反馈", "", 0);menuList.add(menuItem);
		menuItem = new SimpleMenuItem(MENU_QUIT, "    退出", "", 0);menuList.add(menuItem);
		return menuList;
	}
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		SimpleMenuItem menuItem = (SimpleMenuItem) l.getAdapter().getItem(position);
		onOptionsItemSelected(menuItem.getItemId());
	}

	
	public boolean onOptionsItemSelected(int itemId) {
		App app = (App) App.getInstance();
		try {
			switch (itemId){
			case MENU_FEEDBACK:
				UMFeedbackService.openUmengFeedbackSDK(getActivity());
				return true;
			case MENU_QUIT:
				app.exit();
				return true;
			default:
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG,
					"Main onOptionsItemSelected Exception" + e.getMessage());
		}
		return false;
	}
}

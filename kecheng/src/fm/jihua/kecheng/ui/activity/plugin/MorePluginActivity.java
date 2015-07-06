package fm.jihua.kecheng.ui.activity.plugin;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.PluginBean;
import fm.jihua.kecheng.ui.activity.BaseActivity;

/**
 * @date 2013-7-15
 * @introduce 更多插件页面
 */
public class MorePluginActivity extends BaseActivity {

	GridView gridView;
	List<PluginBean> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_more_plugin);
		setTitle();
		findViews();
		initViews();

	}

	void setTitle() {
		setTitle("更多插件");
	}

	void findViews() {
		gridView = (GridView) findViewById(R.id.grid_view);
	}

	void initViews() {

		list = new ArrayList<PluginBean>();
		list.add(new PluginBean("fm.jihua.kecheng.exam", R.drawable.menu_icon_widget_exam, R.drawable.menu_icon_widget_note, "考试哈啦啦"));
		list.add(new PluginBean("fm.jihua.kecheng.exam11", R.drawable.menu_icon_widget_exam, R.drawable.menu_icon_widget_note, "考试哈啦啦111"));
		list.add(new PluginBean("fm.jihua.kecheng.exam22", R.drawable.menu_icon_widget_exam, R.drawable.menu_icon_widget_note, "考试哈啦啦22"));
		list.add(new PluginBean("fm.jihua.kecheng.exam33", R.drawable.menu_icon_widget_exam, R.drawable.menu_icon_widget_note, "考试哈啦啦33"));
		list.add(new PluginBean("fm.jihua.kecheng.exam444", R.drawable.menu_icon_widget_exam, R.drawable.menu_icon_widget_note, "考试哈啦啦4"));
		
		checkPackageIsInstall();
		
		gridView.setAdapter(new MorePluginAdapter(this, list));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PluginBean pluginBean = list.get(position);
				if (pluginBean.installed) {
					Intent intent = getPackageManager().getLaunchIntentForPackage(pluginBean.pakageName);
					startActivity(intent);
				} else {
					Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id="+pluginBean.pakageName));
                    startActivity(intent);
				}
			}
		});
	}
	
	void checkPackageIsInstall(){
		PackageManager pm = getPackageManager();
		List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo pkg : pkgs) {
			// 包名
			String packageName = pkg.packageName;
			if(TextUtils.isEmpty(packageName) || !packageName.contains("fm.jihua.kecheng")){
				continue;
			}
			for (PluginBean pluginBean : list) {
				if (packageName.equals(pluginBean.pakageName)) {
					pluginBean.installed = true;
					break;
				}
			}
		}
	}
	
//	void initPlugins(){
//		List<PluginBean> plugins = getPluginMenus();
//		ViewGroup container = (ViewGroup) findViewById(R.id.plugin_list);
//		for (int i = 0; i < plugins.size(); i++) {
//			final PluginBean plugin = plugins.get(i);
//			switch (plugin.category) {
//			case PluginBean.CATEGORY_LOCAL:
//				((ImageView)container.getChildAt(i).findViewWithTag("icon")).setImageResource(plugin.icon);
//				((TextView)container.getChildAt(i).findViewWithTag("text")).setText(plugin.text);
//				container.getChildAt(i).setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						if (plugin.activityClass != null) {
//							if (plugin.activityClass.isAssignableFrom(Activity.class)) {
//								startActivity(new Intent(parent, plugin.activityClass));
//							}else {
//								Intent intent = new Intent(parent, FragmentWrapperActivity.class);
//								intent.putExtra(FragmentWrapperActivity.INTENT_CLASS_NAME, plugin.activityClass.getName());
//								startActivity(intent);
//							}
//						}else {
//							Hint.showTipsShort(parent, "此功能暂不支持");
//						}
//					}
//				});
//				break;
//			case PluginBean.CATEGORY_WEB:
//				final PluginBean pluginBean = checkPackageIsInstall(plugin);
//				if(pluginBean.installed){
//					((ImageView)container.getChildAt(i).findViewWithTag("icon")).setImageResource(plugin.icon);
//				}else{
//					((ImageView)container.getChildAt(i).findViewWithTag("icon")).setImageResource(plugin.icon_uninstall);
//				}
//				((TextView)container.getChildAt(i).findViewWithTag("text")).setText(plugin.text);
//				container.getChildAt(i).setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						if (pluginBean.installed) {
//							Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(pluginBean.pakageName);
//							startActivity(intent);
//						} else {
//							Hint.showTipsShort(parent, "还没有下载这个插件");
//						}
//					}
//				});
//				break;
//
//			default:
//				break;
//			}
//			
//		}
//		container.findViewById(R.id.menu_plugin_more).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), MorePluginActivity.class);
//				startActivity(intent);
//			}
//		});
//	}

}

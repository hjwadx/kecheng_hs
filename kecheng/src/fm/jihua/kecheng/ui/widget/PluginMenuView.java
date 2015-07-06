package fm.jihua.kecheng.ui.widget;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.PluginBean;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.common.FragmentWrapperActivity;
import fm.jihua.kecheng.ui.activity.plugin.MorePluginActivity;

/**
 * @date 2013-7-15
 * @introduce MenuFragment 中的 插件view
 */
public class PluginMenuView extends LinearLayout {

	List<PluginBean> plugins;

	public PluginMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PluginMenuView(Context context) {
		super(context);
	}

	public void setData(List<PluginBean> plugins) {
		this.plugins = plugins;
		initLayout();
		initViews();
	}

	void initLayout() {
		setOrientation(HORIZONTAL);
	}

	void initViews() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		for (final PluginBean pluginBean : plugins) {
			final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_itemview_more_plugin, this, false);

			TextView textView = (TextView) layout.findViewById(R.id.plugin_text);
			ImageView img = (ImageView) layout.findViewById(R.id.plugin_image);

			textView.setText(pluginBean.text);
			img.setImageResource(pluginBean.icon);
			switch (pluginBean.category) {
			case PluginBean.CATEGORY_LOCAL:
				layout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (pluginBean.activityClass != null) {
							if (pluginBean.activityClass.isAssignableFrom(Activity.class)) {
								getContext().startActivity(new Intent(getContext(), pluginBean.activityClass));
							}else {
								Intent intent = new Intent(getContext(), FragmentWrapperActivity.class);
								intent.putExtra(BaseActivity.INTENT_THEME, R.style.XTheme_Transparent_Popup);
								intent.putExtra(FragmentWrapperActivity.INTENT_CLASS_NAME, pluginBean.activityClass.getName());
								getContext().startActivity(intent);
							}
							((Activity)getContext()).overridePendingTransition(R.anim.slide_bottom_in, 0);
							ImageView image = (ImageView) layout.findViewById(R.id.menu_redpoint);
							if(image.getVisibility() == View.VISIBLE){
								image.setVisibility(View.GONE);
							}
						}else {
							Intent intent=new Intent(getContext(), MorePluginActivity.class);
							getContext().startActivity(intent);
						}
					}
				});
				break;
			case PluginBean.CATEGORY_WEB:
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
				break;

			default:
				break;
			}
			addView(layout);
		}
	}
	

	PluginBean checkPackageIsInstall(PluginBean plugin){
		PackageManager pm = getContext().getPackageManager();
		List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for (PackageInfo pkg : pkgs) {
			// 包名
			String packageName = pkg.packageName;
			if(packageName.equals(plugin.pakageName)){
				plugin.installed = true;
				break;
			}
		}
		return plugin;
	}

}

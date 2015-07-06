package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.SimpleMenuItem;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.common.FragmentWrapperActivity;
import fm.jihua.kecheng.ui.fragment.SettingsFragment;

public class MenuAdapter extends BaseAdapter {
    
	List<SimpleMenuItem> mMenus;
	int selectedItemId;
	
	public MenuAdapter(List<SimpleMenuItem> menus){
		this.mMenus = menus;
	}

	@Override
	public int getCount() {
		return this.mMenus.size();
	}
	
	public void setData(List<SimpleMenuItem> menus){
		this.mMenus = menus;
	}
	
	public List<SimpleMenuItem> getData(){
		return mMenus;
	}

	@Override
	public Object getItem(int position) {
		return this.mMenus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mMenus.get(position).getItemId();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
			
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.text = ((TextView)convertView.findViewById(R.id.text));
			viewHolder.icon = (ImageView)convertView.findViewById(R.id.icon);
			viewHolder.settingGroup = convertView.findViewById(R.id.setting_group);
			viewHolder.hint = (TextView)convertView.findViewById(R.id.hint);
			viewHolder.settingHint = (ImageView)convertView.findViewById(R.id.menu_redpoint);
//			viewHolder.hint = (ImageView)convertView.findViewById(R.id.hint);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		SimpleMenuItem menu = (SimpleMenuItem)getItem(position);
		
		viewHolder.text.setText(menu.getTitle());
		
		if (menu.getIcon() != 0) {
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.icon.setImageResource(menu.getIcon());
		} else {
			viewHolder.icon.setVisibility(View.GONE);
		}
		if (menu.isShowSettingIcon()) {
			final Context context = convertView.getContext();
			viewHolder.settingGroup.setVisibility(View.VISIBLE);
			viewHolder.settingGroup.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, FragmentWrapperActivity.class);
					intent.putExtra(BaseActivity.INTENT_THEME, R.style.XTheme_Transparent_Popup);
					intent.putExtra(FragmentWrapperActivity.INTENT_CLASS_NAME, SettingsFragment.class.getName());
					context.startActivity(intent);
					((Activity)context).overridePendingTransition(R.anim.slide_bottom_in, 0);
				}
			});
		}else {
			viewHolder.settingGroup.setVisibility(View.GONE);
		}
		
		if(menu.getSettingIconHint()){
			viewHolder.settingHint.setVisibility(View.VISIBLE);
		}else{
			viewHolder.settingHint.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(menu.getHighlightText())) {
			viewHolder.hint.setText(menu.getHighlightText());
			viewHolder.hint.setVisibility(View.VISIBLE);
		}else {
			viewHolder.hint.setVisibility(View.GONE);
		}
		return convertView;
	}

	static class ViewHolder{
		TextView text;
		TextView hint;
		ImageView icon;
		View settingGroup;
		ImageView settingHint;
	}
}

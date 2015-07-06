package fm.jihua.kecheng.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.ThemeProduct;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;
import fm.jihua.kecheng.rest.entities.weekstyle.Theme;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.common.FragmentWrapperActivity;
import fm.jihua.kecheng.ui.activity.mall.MallFragment;
import fm.jihua.kecheng.ui.activity.sticker.ChooseStickerActivity;

/**
 * @date 2013-7-24
 * @introduce
 */
public class ThemeView extends LinearLayout {

	public ThemeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	public ThemeView(Context context) {
		super(context);
	}

	List<Theme> skinStyleList;
	Theme currentStyle;

	public LinearLayout skinsLayout;
	public TextView tv_title;
	public HorizontalScrollView scrollView;

	private void initViews() {

		skinStyleList = new ArrayList<Theme>();
		skinStyleList.addAll(ThemeProduct.getMyLocalItems());

		LayoutInflater.from(getContext()).inflate(R.layout.layout_choose_skin, this);
		skinsLayout = (LinearLayout) findViewById(R.id.chooseskin_layout);
		scrollView = (HorizontalScrollView) findViewById(R.id.chooseskin_layout_parent);
		tv_title=(TextView) findViewById(R.id.chooseskin_title);
		currentStyle = getLastSkin();
		initLayout();
	}

	Theme getLastSkin() {
		App app = (App) (((Activity) getContext()).getApplication());
		Theme current = Theme.getThemeByName(app.getCurrentStyleName());
		if (current == null) {
			current = Theme.getDefaultTheme();
		}
		return current;
	}
	
	int initSkinItemsView(View selectedView){
		int selectedIndex = 0;
		int childCount = skinsLayout.getChildCount();
		for (int childIndex = 0; childIndex < childCount; childIndex++) {
			SkinItemView view = (SkinItemView) skinsLayout.getChildAt(childIndex);
			if (view == selectedView) {
				if (!view.isChecked()) {
					currentStyle = view.getSkinBean();
					view.setChecked(true);
					view.performClick();
				}
				selectedIndex = childIndex;
			} else {
				view.setChecked(false);
			}
		}
		return selectedIndex;
	}

	private void initLayout() {
		String currentStyleName = App.getInstance().getCurrentStyleName();
		
		for (int position = 0; position < skinStyleList.size(); position++) {
			final SkinItemView skinItemView = new SkinItemView(getContext());
			final Theme weekStyleBean = skinStyleList.get(position);
			skinItemView.setSkinBean(weekStyleBean);
			skinItemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int selectedIndex = initSkinItemsView(v);
					if (onSkinViewClickListener != null){
						onSkinViewClickListener.onClick(currentStyle, selectedIndex);
					}
				}
			});
			if (position == 0)
				skinItemView.setType(SkinItemView.CATEGORY_CUSTOM);
			//根据当前主题的名称选中
			skinItemView.setChecked(weekStyleBean.name.equals(currentStyleName));
			skinsLayout.addView(skinItemView);
		}
		addMallBtn();
	}
	
	void addMallBtn(){
		
		final SkinItemView skinItemView = new SkinItemView(getContext());
		skinItemView.setImage(R.drawable.skin_add_skin_btn);
		skinItemView.setText("更多");
		skinItemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), FragmentWrapperActivity.class);
				intent.putExtra(BaseActivity.INTENT_THEME, R.style.XTheme_Transparent_Popup);
				intent.putExtra(FragmentWrapperActivity.INTENT_CLASS_NAME, MallFragment.class.getName());
				intent.putExtra(MallFragment.KEY_TAB_INDEX, MallFragment.PAGETHEME);
				getContext().startActivity(intent);
			}
		});
		skinsLayout.addView(skinItemView);
	}
	
	public void setCurrentWeekStyle(String name){
		int childCount = skinsLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			SkinItemView skinItemView = (SkinItemView) skinsLayout.getChildAt(i);
			if(skinItemView.getSkinBean().name.equals(name)){
				initSkinItemsView(skinItemView);
				return ;
			}
		}
	}

	public Theme getCurrentWeekStyleBean() {
		return currentStyle;
	}

	public List<Theme> getSkinStyleList() {
		return skinStyleList;
	}

	public interface OnSkinViewClickListener {
		public void onClick(Theme weekStyleBean, int position);
	}

	private OnSkinViewClickListener onSkinViewClickListener;

	public void setOnSkinViewClickListener(OnSkinViewClickListener onSkinViewClickListener) {
		this.onSkinViewClickListener = onSkinViewClickListener;
	}
	
	public void refresh(){
		skinStyleList.clear();
		skinStyleList.addAll(ThemeProduct.getMyLocalItems());
		currentStyle = getLastSkin();
		skinsLayout.removeAllViews();
		initLayout();
	}

}

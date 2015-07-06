package fm.jihua.kecheng.ui.activity.mall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.mall.ProductsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.common.FragmentWrapperActivity;
import fm.jihua.kecheng.ui.fragment.BaseFragment;

/**
 * @date 2013-8-29
 * @introduce
 */
public class MallFragment extends BaseFragment {

	ViewPager viewPager;
	BaseActivity activity;
	RadioGroup radioGroup;
	DataAdapter dataAdapter;
	List<Product> pasters = new ArrayList<Product>();
	List<Product> skins = new ArrayList<Product>();
	public final static String BRODCAST_STICKER_CHANGED = "sticker_changed";
	public final static String BRODCAST_THEME_CHANGED = "theme_changed";
	public final static String BRODCAST_CURRENT_THEME_CHANGED = "theme_current_changed";
	
	public static final String KEY_TAB_INDEX = "tab_id";
	
	public final static int PAGESTICKER = 0;
	public final static int PAGETHEME = 1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.act_mall, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initTitleBar();
		initViews(view);
		initListeners();
		dataAdapter = new DataAdapter(getActivity(), dataCallback);
		((RadioButton)(getView().findViewById(R.id.radiobtn_paste))).setChecked(true);
		dataAdapter.getAllProducts();
	}

	private void initTitleBar() {
		activity = (BaseActivity) getActivity();
		activity.getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		activity.getKechengActionBar().setRightImage(R.drawable.menubar_btn_setting);
		activity.getKechengActionBar().getRightButton().setOnClickListener(rightTitleBarClickListener);
		activity.setTitle("格子商城");
	}

	private void initViews(View view) {
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
		radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
		viewPager.setAdapter(new MallFragmentAdapter(activity.getSupportFragmentManager()));

	}

	private void initListeners() {
		radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
		viewPager.setOnPageChangeListener(onPageChangeListener);
	}
	
	private int getTabIndex(){
		Bundle arguments = getArguments();
		return arguments.getInt(KEY_TAB_INDEX, -1);
	}

	OnClickListener rightTitleBarClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent=new Intent(getActivity(), MallMineActivity.class);
			startActivityForResult(intent, MallMineActivity.EDIT_MAIN_PRODUCTS);
		}
	};
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.radiobtn_paste:
				viewPager.setCurrentItem(MallConst.POSITION_PASTE, true);
				break;
			case R.id.radiobtn_skin:
				viewPager.setCurrentItem(MallConst.POSITION_SKIN, true);
				break;

			default:
				break;
			}
		}
	};

	OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			switch (position) {
			case MallConst.POSITION_PASTE:
				((RadioButton)(getView().findViewById(R.id.radiobtn_paste))).setChecked(true);
				break;
			case MallConst.POSITION_SKIN:
				((RadioButton)(getView().findViewById(R.id.radiobtn_skin))).setChecked(true);
				break;

			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	
	DataCallback dataCallback = new DataCallback() {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_ALL_PRODUCTS:
				ProductsResult result = (ProductsResult) msg.obj;
				if(result != null && !result.finished){
					if (!result.success) {
						UIUtil.block(getActivity());
					}
				} else {
					UIUtil.unblock(getActivity());
				}
				if(result != null && result.success){
					pasters.clear();
					skins.clear();
					MallStickerSetFragment pasteFragment = (MallStickerSetFragment) ((MallFragmentAdapter)viewPager.getAdapter()).instantiateItem(viewPager, 0);
					pasters.addAll(Arrays.asList(result.stickers));
					pasteFragment.setData(pasters, result.banner, result.more_sticker);
					MallThemeFragment skinFragment = (MallThemeFragment) ((MallFragmentAdapter)viewPager.getAdapter()).instantiateItem(viewPager, 1);
					skins.addAll(Arrays.asList(result.themes));
					skinFragment.setData(skins, result.more_themes);
					int tabIndex = getTabIndex();
					if(tabIndex != -1){
						viewPager.setCurrentItem(tabIndex);
					}
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case MallMineActivity.EDIT_MAIN_PRODUCTS:
			if(data.getBooleanExtra(MallMineActivity.STICKERS_CHANGED, false)){
				MallStickerSetFragment pasteFragment = (MallStickerSetFragment) ((MallFragmentAdapter)viewPager.getAdapter()).instantiateItem(viewPager, 0);
				pasteFragment.refresh();
			}
            if(data.getBooleanExtra(MallMineActivity.THEME_CHANGED, false)){
            	MallThemeFragment skinFragment = (MallThemeFragment) ((MallFragmentAdapter)viewPager.getAdapter()).instantiateItem(viewPager, 1);
            	skinFragment.onRefresh();
			}
			break;
		default:
			break;
		}
	}
}

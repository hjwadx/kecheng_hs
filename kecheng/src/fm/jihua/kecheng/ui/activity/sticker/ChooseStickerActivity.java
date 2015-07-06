package fm.jihua.kecheng.ui.activity.sticker;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.StickerSetProduct;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.common.FragmentWrapperActivity;
import fm.jihua.kecheng.ui.activity.mall.MallFragment;
import fm.jihua.kecheng.ui.adapter.ChooseStickerPagerAdapter;
import fm.jihua.kecheng.ui.widget.StickerBottomItem;

/**
 * @date 2013-7-17
 * @introduce 贴纸选择页面
 */
public class ChooseStickerActivity extends BaseActivity {

	ViewPager viewPager;
	LinearLayout bottom_layout;
	List<StickerSet> stickerSets;
	// Paster[] pasters;
	// PasterChoiceAdapter pasterChoiceAdapter;
	ChooseStickerPagerAdapter choicePagerAdapter;
	FrameLayout	hintLayout;

	public static final int PASTER_REQUESTCODE = 211;
	public static final String INTENT_KEY = "pasterchoice";
	public static final String INTENT_PASTER = "paster";
//	boolean autoEnterMall = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onEvent(this, "enter_course_sticker_view");
		setContentView(R.layout.act_paster_choice);
		initTitlebar();
		findViews();
		initViews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		stickerSets.clear();
		stickerSets.addAll(StickerSetProduct.getMyCourseItems());
		choicePagerAdapter.notifyDataSetChanged();
		initViewGroup();
//		if (bottom_layout.getChildCount() == 1 && autoEnterMall) {
//			bottom_layout.getChildAt(0).performClick();
//			autoEnterMall = false;
//		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerBroad();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unRegisterBroad();
	}

	void initTitlebar() {
		setTitle("贴纸");
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
	}

	void findViews() {
		viewPager = (ViewPager) findViewById(R.id.paster_viewpager);
		bottom_layout = (LinearLayout) findViewById(R.id.paster_bottom_layout);
		hintLayout = (FrameLayout) findViewById(R.id.hintlayout);
	}

	void initViews() {

//		listPasterInfo = AssetsFileUtils.getInstance().getPasterInfoFromAssets(this, false);
		stickerSets = new ArrayList<StickerSet>();
		choicePagerAdapter = new ChooseStickerPagerAdapter(getSupportFragmentManager(), stickerSets);
		viewPager.setAdapter(choicePagerAdapter);
		viewPager.setOnPageChangeListener(changeListener);
		initViewGroup();
		
	}

	void initViewGroup() {
		
		bottom_layout.removeAllViews();
		
		for (int i = 0; i < stickerSets.size(); i++) {
			final StickerBottomItem bottomItem = new StickerBottomItem(this, stickerSets.get(i));
//			final StickerSet stickerSet = stickerSets.get(i);
			bottomItem.setPosition(i);
			bottomItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setCheckPosition(v);
					viewPager.setCurrentItem(bottomItem.getPosition(), true);
//					if (!stickersInfo.exists()) {
//						Hint.showTipsShort(getThis(), "所选贴纸还未下载，请先下载之后再使用。");
//						Intent intent = new Intent(getThis(), MyStickerSetProductsActivity.class);
//						startActivity(intent);
//					}
				}
			});
			bottom_layout.addView(bottomItem);
		}
		
		addMallBtn();

		if (bottom_layout.getChildCount() > 1) {
			int position = getIntent().getIntExtra(ChooseStickerPagerAdapter.FRAGMENT_POSITION, 0);
			((StickerBottomItem) bottom_layout.getChildAt(position)).performClick();
			hintLayout.setVisibility(View.GONE);
		} else {
			hintLayout.setVisibility(View.VISIBLE);
		}
	}
	
	void addMallBtn(){
		final StickerBottomItem bottomItem = new StickerBottomItem(this, (StickerSet) null);
		bottomItem.setImages(R.drawable.keyboard_add_sticker_btn, R.drawable.keyboard_add_sticker_btn);
		bottomItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ChooseStickerActivity.this, FragmentWrapperActivity.class);
				intent.putExtra(BaseActivity.INTENT_THEME, R.style.XTheme_Transparent_Popup);
				intent.putExtra(FragmentWrapperActivity.INTENT_CLASS_NAME, MallFragment.class.getName());
				startActivity(intent);
			}
		});
		bottom_layout.addView(bottomItem);
	}

	void setCheckPosition(View view) {
		int childCount = bottom_layout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			StickerBottomItem childView = (StickerBottomItem) bottom_layout.getChildAt(i);
			if (bottom_layout.getChildAt(i) == view) {
				childView.setChecked(true);
			} else {
				childView.setChecked(false);
			}
		}
	}

	void setCheckPosition(int position) {
		setCheckPosition(bottom_layout.getChildAt(position));
	}

	OnPageChangeListener changeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			setCheckPosition(position);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

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
				stickerSets.clear();
				stickerSets.addAll(StickerSetProduct.getMyCourseItems());
			}
		}
	};
}

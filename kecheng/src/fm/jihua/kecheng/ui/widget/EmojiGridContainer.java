package fm.jihua.kecheng.ui.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.StickerSetProduct;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.common.FragmentWrapperActivity;
import fm.jihua.kecheng.ui.activity.mall.MallFragment;
import fm.jihua.kecheng.ui.fragment.FaceEmoji;
import fm.jihua.kecheng.ui.fragment.ChatStickerSet;
import fm.jihua.kecheng.ui.fragment.FaceYanWenzi;

public class EmojiGridContainer extends RelativeLayout {

	Context context;
	LinearLayout bottom_layout;
	FragmentTransaction transaction;
	
	List<StickerSet> stickerSets;
	
	public EmojiGridContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		findViews();
	}

	public EmojiGridContainer(Context context) {
		super(context);
		this.context = context;
		findViews();
	}
	
	void findViews(){
		inflate(getContext(), R.layout.emoji_grid_container, this);
		bottom_layout=(LinearLayout) findViewById(R.id.paster_bottom_layout);
		
		initViews();
	}
	
	void initViews() {
//		listPasterInfo = AssetsFileUtils.getInstance().getPasterInfoFromAssets(getContext(), false);
		stickerSets = StickerSetProduct.getMyChatItems();
		initViewGroup();
	}
	
	public void onResume(){
		stickerSets.clear();
		stickerSets.addAll(StickerSetProduct.getMyChatItems());
		initViewGroup();
	}
	
	public void reLoadProduct(){
		stickerSets.clear();
		stickerSets.addAll(StickerSetProduct.getMyChatItems());
	}
	
	void initViewGroup() {
		
		bottom_layout.removeAllViews();
		
		bottom_layout.setBackgroundColor(0xFFD0D0D0);
		int size = stickerSets.size() + getCustomFaceSize();
		for (int i = 0; i < size; i++) {
			StickerSet product = null;
			StickerBottomItem bottomItem;
			if(i == 0){
				bottomItem = new StickerBottomItem(getContext(), null);
				bottomItem.setImages(R.drawable.paster_toolbar_icon_emoji, R.drawable.paster_toolbar_icon_emoji_selected);
			}else if(i == 1){
				bottomItem = new StickerBottomItem(getContext(), null);
				bottomItem.setImages(R.drawable.paster_toolbar_icon_yanwenzi, R.drawable.paster_toolbar_icon_yanwenzi_selected);
			}else{
				product = stickerSets.get(i - getCustomFaceSize());
				bottomItem = new StickerBottomItem(getContext(), product);
			}
			
//			final StickerSet finalProduct = product;
			final int position = i;
			
			bottomItem.setBackground(0x00FFFFFF, getResources().getColor (R.color.divider_bg));
			bottomItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					StickerBottomItem bottomItem = (StickerBottomItem) v;
//					if (finalProduct != null && !finalProduct.exists()) {
//						Hint.showTipsShort(context, "所选贴纸还未下载，请先下载之后再使用。");
//						Intent intent = new Intent(context, MyStickerSetProductsActivity.class);
//						context.startActivity(intent);
//					} else 
					if (!bottomItem.isChecked()) {
						setCheckPosition(v);
						initFragments(position);
					}
				}
			});
			bottom_layout.addView(bottomItem);
		}
		
		addMallBtn();

		if (bottom_layout.getChildCount() > 0) {
			((StickerBottomItem) bottom_layout.getChildAt(0)).performClick();
		}
	}
	
	void addMallBtn(){
		final StickerBottomItem bottomItem = new StickerBottomItem(context,(StickerSet)null);
		bottomItem.setImages(R.drawable.keyboard_add_sticker_btn, R.drawable.keyboard_add_sticker_btn);
		bottomItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, FragmentWrapperActivity.class);
				intent.putExtra(BaseActivity.INTENT_THEME, R.style.XTheme_Transparent_Popup);
				intent.putExtra(FragmentWrapperActivity.INTENT_CLASS_NAME, MallFragment.class.getName());
				context.startActivity(intent);
			}
		});
		bottom_layout.addView(bottomItem);
	}
	
	void initFragments(int position){
		Fragment fragment;
		if(position == 0){
			fragment = new FaceEmoji();
		}else if(position == 1){
			fragment = new FaceYanWenzi();
		}else{
			fragment = new ChatStickerSet();
			Bundle bundle = new Bundle();
			bundle.putSerializable(StickerSet.class.getName(), stickerSets.get(position - getCustomFaceSize()));
			fragment.setArguments(bundle);
		}
		transaction = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.main_fragment_container, fragment);
		transaction.commitAllowingStateLoss();
	}
	
	private int getCustomFaceSize() {
		return getEmojiSize() + getYanwenziSize();
	}
	
	private int getEmojiSize(){
		return 1;
	}
	
	private int getYanwenziSize(){
		return 1;
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
}

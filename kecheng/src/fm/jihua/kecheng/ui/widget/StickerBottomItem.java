package fm.jihua.kecheng.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;

/**
 * @date 2013-7-31
 * @introduce 贴纸底部选择子布局
 */
public class StickerBottomItem extends LinearLayout {

	public StickerBottomItem(Context context, StickerSet stickerSet) {
		super(context);
		this.stickerSet = stickerSet;
		initViews();
		if (stickerSet != null) {
			setChecked(false);
		}
	}

	CachedImageView img_base;
	View view_divider;
	LinearLayout layoutParent;

	int imagename_normal;
	int imagename_selected;
	StickerSet stickerSet;

	private boolean isChecked;
	private int backColorNormal;
	private int backColorSelected;

	void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_pasterbottom_itemview, this);

		img_base = (CachedImageView) findViewById(R.id.pasterbottom_itemview_image);
		view_divider = findViewById(R.id.pasterbottom_itemview_divider);
		layoutParent = (LinearLayout) findViewById(R.id.pasterbottom_layout);
	}

	private int position;
	public void setPosition(int position){
		this.position = position;
	}
	
	public int getPosition(){
		return this.position;
	}

//	public StickerSet getProduct() {
//		return this.stickerSet;
//	}

	public void setImages(int imagename_normal, int imagename_selected) {
		this.imagename_normal = imagename_normal;
		this.imagename_selected = imagename_selected;
		setChecked(false);
	}

	public void hideDivider() {
		view_divider.setVisibility(View.GONE);
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		if (isChecked) {
			if (imagename_selected != 0) {
				img_base.setImageResource(imagename_selected);
			} else {
				// if (stickerSet.exists()) {
				img_base.setImageDrawable(stickerSet.getSelectedIconDrawable(getContext()));
				// }else {
				// img_base.setImageURI(Uri.parse(stickerSet.press_icon_url));
				// }
			}
			if (backColorSelected != 0) {
				layoutParent.setBackgroundColor(backColorSelected);
			}
		} else {
			if (imagename_normal != 0) {
				img_base.setImageResource(imagename_normal);
			} else {
				// if (stickerSet.exists()) {
				img_base.setImageDrawable(stickerSet.getNormalIconDrawable(getContext()));
				// }else {
				// img_base.setImageURI(Uri.parse(stickerSet.icon_url));
				// }
			}

			if (backColorNormal != 0) {
				layoutParent.setBackgroundColor(backColorNormal);
			}
		}
	}
	
	public void setBackground(int colorNormal, int colorSelected) {
		this.backColorNormal = colorNormal;
		this.backColorSelected = colorSelected;
	}

}

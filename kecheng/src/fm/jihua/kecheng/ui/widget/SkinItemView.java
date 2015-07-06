package fm.jihua.kecheng.ui.widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.weekstyle.Theme;
import fm.jihua.kecheng.utils.Const;

/**
 * @date 2013-7-25
 * @introduce
 */
public class SkinItemView extends FrameLayout {

	public SkinItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SkinItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SkinItemView(Context context) {
		super(context);
		initView();
	}

	ImageView imgCover;
	ImageView imgBase;
	TextView textView;
	Theme theme;

	int category = CATEGORY_NORMAL;
	public static final int CATEGORY_CUSTOM = 1;
	public static final int CATEGORY_NORMAL = 2;

	void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.week_skin_item, this);

		imgCover = (ImageView) findViewById(R.id.skinitem_cover);
		imgBase = (ImageView) findViewById(R.id.skinitem_image_base);
		textView = (TextView) findViewById(R.id.skinitem_text);
	}

	public void setType(int category) {
		this.category = category;

		if (CATEGORY_CUSTOM == category) {
			imgCover.setImageResource(R.drawable.skin_icon_custom_selected);
			imgBase.setImageResource(R.drawable.skin_img_custom);
		}
	}

	public void setSkinBean(Theme theme) {
		this.theme = theme;
		setText(theme.name);
		if(theme.category == Theme.CATEGORY_FILE){
			imgBase.setImageDrawable(theme.getIconDrawable(getContext()));
		}
	}
	
	public Theme getSkinBean(){
		return this.theme;
	}

	public void setImage(int img_background, int img_cover) {
		imgCover.setImageResource(img_cover);
		imgBase.setImageResource(img_background);
	}
	
	public void setImage(int img_background) {
		this.setImage(img_background, R.drawable.skin_icon_selected);
	}

	public void setText(String text) {
		textView.setText(text);
	}

	public boolean isChecked() {
		return imgCover.getVisibility() == View.VISIBLE;
	}

	public void setChecked(boolean isChecked) {
		int visibility = isChecked ? View.VISIBLE : View.GONE;
		imgCover.setVisibility(visibility);
	}

}

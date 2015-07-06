package fm.jihua.kecheng.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng_hs.R;

public class ShadowTextView extends LinearLayout {

	public ShadowTextView(Context context) {
		super(context);
		
		init();
	}

	public ShadowTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageTextViewSetting);

		// float width =
		// a.getDimension(R.styleable.ImageTextViewSetting_drawableWidth, 0);
		// float height =
		// a.getDimension(R.styleable.ImageTextViewSetting_drawableHeight, 0);
		Drawable drawableBackground = a.getDrawable(R.styleable.ImageTextViewSetting_background_color);
		// int drawableLeft_res =
		// a.getInt(R.styleable.ImageTextViewSetting_drawableLeftImage, -1);
		String text = a.getString(R.styleable.ImageTextViewSetting_text);
		
		int shadowParentId = a.getResourceId(R.styleable.ImageTextViewSetting_shadowParentId, -1);
		
		if(shadowParentId != -1){
			layout.setId(shadowParentId);
		}

		a.recycle();

		if (null != drawableBackground)
			Compatibility.setBackground(layout, drawableBackground);

		// if (width != 0 && height != 0) {
		//
		// Drawable[] drawables = textView.getCompoundDrawables();
		// if (drawables.length > 0) {
		// for (Drawable drawable : drawables) {
		// if (drawable != null) {
		// drawable.setBounds(0, 0, (int) width, (int) height);
		// }
		// }
		// }
		// textView.setCompoundDrawables(drawables[0], drawables[1],
		// drawables[2], drawables[3]);
		// }

		if (!TextUtils.isEmpty(text)) {
			textView.setText(text);
		}
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.layout_btn_with_shadow, this);
		findViews();
	}

	LinearLayout layout;
	TextView textView;
	ImageView image;

	void findViews() {
		layout = (LinearLayout) findViewById(R.id.share);
		textView = (TextView) findViewById(R.id.shadow_btn);
		image = (ImageView) findViewById(R.id.shadow_image);
	}

	public void setBackgroundColor(int color) {
		layout.setBackgroundColor(color);
	}
	
	public void setBackgroundResource(int res) {
		layout.setBackgroundResource(res);
	}

	public void setText(String text) {
		textView.setText(text);
	}

	public void setImageIcon(int res) {
		if (res != -1) {
			Drawable drawable = getResources().getDrawable(res);
			setDrawableLeft(drawable);
		} else {
			image.setImageDrawable(null);
			image.setVisibility(View.GONE);
		}
	}

	public void setDrawableLeft(Drawable drawable) {
		if (drawable != null) {
			image.setVisibility(View.VISIBLE);
			image.setImageDrawable(drawable);
		} else {
			image.setImageDrawable(null);
			image.setVisibility(View.GONE);
		}
	}
}

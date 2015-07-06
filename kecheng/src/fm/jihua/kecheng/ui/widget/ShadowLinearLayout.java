package fm.jihua.kecheng.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.AppLogger;

public class ShadowLinearLayout extends LinearLayout {
	Drawable mShadowDrawable;
	int mShadowLeft;
	int mShadowRight;
	int mShadowTop;
	int mShadowBottom;
	
	public ShadowLinearLayout(Context context) {
		super(context);
		init();
	}

	public ShadowLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	void init(){
		mShadowDrawable = getResources().getDrawable(R.drawable.bg_shadow);
		mShadowLeft = getResources().getDimensionPixelSize(R.dimen.shadow_left);
		mShadowRight = getResources().getDimensionPixelSize(R.dimen.shadow_right);
		mShadowTop = getResources().getDimensionPixelSize(R.dimen.shadow_top);
		mShadowBottom = getResources().getDimensionPixelSize(R.dimen.shadow_bottom);
		setWillNotDraw(false);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		drawShadow(canvas);
	}

	public void drawShadow(Canvas canvas) {
		if (mShadowDrawable == null) return;
		mShadowDrawable.setBounds(getLeft()-mShadowLeft, getTop()-mShadowTop, getRight() + mShadowRight, getBottom() + mShadowBottom);
		AppLogger.d(mShadowDrawable.getBounds().toShortString());
		mShadowDrawable.draw(canvas);
	}
}

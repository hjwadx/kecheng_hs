package fm.jihua.kecheng.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class SimpleScrollView extends ScrollView {
	
	ScrollChangedListener scrollChangedListener;
	
	public static interface ScrollChangedListener{
		public void scrollChanged();
	}

	public SimpleScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SimpleScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SimpleScrollView(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollChangedListener != null) {
			scrollChangedListener.scrollChanged();
		}
	}
	
	public void setOnScrollChangedListener(ScrollChangedListener listener){
		scrollChangedListener = listener;
	}
}

package fm.jihua.kecheng.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
/**
 * 用来监听键盘的显示\隐藏
 * @author dangtao
 *
 */
public class OnResizeLineaerLayout extends LinearLayout {

	public OnResizeLineaerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public OnResizeLineaerLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.i("tag", "newWidth: " + w + " newHeight: " + h + " oldWidth: "
				+ oldw + " oldHeight: " + oldh);
		if (reSizeListener != null) {
			reSizeListener.onResize(h, oldh);
		}
	}

	public interface onReSizeListener {
		public void onResize(int newHeight, int oldHeight);
	}

	private onReSizeListener reSizeListener;

	public void setOnResizeListener(onReSizeListener reSizeListener) {
		this.reSizeListener = reSizeListener;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		if (reSizeListener != null) {
			reSizeListener.onResize(0, 0);
		}
	}
}

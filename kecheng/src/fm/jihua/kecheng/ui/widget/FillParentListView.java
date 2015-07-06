package fm.jihua.kecheng.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FillParentListView extends ListView {

	// private boolean haveScrollbar = true;

	public FillParentListView(Context context) {
		super(context);
	}

	public FillParentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FillParentListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// public void setHaveScrollbar(boolean haveScrollbar) {
	// this.haveScrollbar = haveScrollbar;
	// }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}

package fm.jihua.kecheng.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseViewGroup extends ViewGroup {
	Activity parent;
	Fragment fragment;
	

	public BaseViewGroup(Fragment context) {
		super(context.getActivity());
		fragment = context;
		parent = (Activity) context.getActivity();
	}
	
	public void onResume(){
		initTitleBar();
	}

	public abstract void setData(Object data);

	public abstract void refreshUI();
	
	public abstract void initTitleBar();
	public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			child.measure(widthMeasureSpec, heightMeasureSpec);
		}
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				// Compute position of each child
				child.layout(left, top, right, bottom);
			}
		}
	}
}

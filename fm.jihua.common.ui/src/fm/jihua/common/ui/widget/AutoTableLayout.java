package fm.jihua.common.ui.widget;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import fm.jihua.ui.R;

public class AutoTableLayout extends LinearLayout {

	public AutoTableLayout(Context context) {
		super(context);
	}

	public AutoTableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// void initViews(){
	// inflate(getContext(), R.layout.user_list, this);
	// userListView = (ListView) findViewById(R.id.user_list);
	// BitmapDrawable bd = App.getBaseBackground(getResources());
	// userListView.setBackgroundDrawable(bd);
	// }

	public void setData(List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		removeAllViews();
		for (int i = 0; i < data.size(); i++) {
			View parent = inflate(getContext(), resource, null);
			if (data.size() == 1) {
				parent.setBackgroundResource(R.drawable.input_single);
			} else {
				if (i == 0) {
					parent.setBackgroundResource(R.drawable.input_top);
				} else if (i == data.size() - 1) {
					parent.setBackgroundResource(R.drawable.input_bottom);
				} else {
					parent.setBackgroundResource(R.drawable.input_middle);
				}
			}
			for (int j = 0; j < from.length; j++) {
				((TextView) parent.findViewById(to[j])).setText((String) data.get(i).get(from[j]));
			}
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			addView(parent, lp);
		}
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		resetChildViews();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			resetChildViews();
		}
	}

	private void resetChildViews() {
		LinkedList<View> linearLayoutViews = new LinkedList<View>();
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if (getChildAt(i) instanceof ViewGroup) {
				if (getChildAt(i).getVisibility() == View.VISIBLE) {
					linearLayoutViews.add(getChildAt(i));
				}
			}
		}
		count = linearLayoutViews.size();
		if (count == 1) {
			linearLayoutViews.get(0).setBackgroundResource(R.drawable.input_single);
		} else if (count > 1) {

			for (int i = 0; i < count; i++) {
				if (i == 0) {
					linearLayoutViews.get(i).setBackgroundResource(R.drawable.input_top);
				} else if (i == count - 1) {
					linearLayoutViews.get(i).setBackgroundResource(R.drawable.input_bottom);
				} else {
					linearLayoutViews.get(i).setBackgroundResource(R.drawable.input_middle);
				}
			}
		}
	}
}

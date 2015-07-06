package fm.jihua.kecheng.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;

/**
 * @date 2013-7-30
 * @introduce
 */
public class BiasHintView extends LinearLayout {

	public BiasHintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	public BiasHintView(Context context) {
		super(context);
		initViews();
	}

	TextView textView;

	void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.bias_hintview, this);
		textView = (TextView) findViewById(R.id.bias_hintview_text);
	}

	public void setText(String text) {
		textView.setText(text);
	}

	public void setText(int stringRes) {
		setText(getResources().getString(stringRes));
	}

}

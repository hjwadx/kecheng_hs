package fm.jihua.kecheng.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @date 2013-7-30
 * @introduce
 */
public class BiasTextView extends TextView {

	public BiasTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BiasTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public BiasTextView(Context context) {
		super(context);
		initView();
	}
	
	void initView(){
		
	}
	
	Paint paint;
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.rotate(-1);
		super.onDraw(canvas);
	}

}

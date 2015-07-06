package fm.jihua.kecheng.ui.widget;

import android.content.Context;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.widget.weekview.WeekSpiritStyle;

public class GraduationView extends RelativeLayout {

	ScrollView leftScrollView;
	HorizontalScrollView topScrollView;

	ImageView imageCorner;
	ImageView imageLeft;
	ImageView imageTop;

	public GraduationView(Context context, WeekSpiritStyle controller) {
		super(context);
		inflate(context, R.layout.week_graduation, this);
		init();
	}

	public GraduationView(Context context) {
		super(context);
		inflate(context, R.layout.week_graduation, this);
		init();
	}

	void init() {
		leftScrollView = (ScrollView) findViewById(R.id.left_title_parent);
		topScrollView = (HorizontalScrollView) findViewById(R.id.top_title_parent);
		leftScrollView.setEnabled(false);
		topScrollView.setEnabled(false);
		// TODO 如果换了time_slot 怎么办？
		imageCorner = (ImageView) findViewById(R.id.weekview_corner_layout);
		imageLeft = (ImageView) findViewById(R.id.left_title);
		imageTop = (ImageView) findViewById(R.id.top_title);
	}

	public void scrollTo(int dx, int dy) {
		leftScrollView.smoothScrollTo(0, dy);
		topScrollView.smoothScrollTo(dx, 0);
	}

	public void invalidateSizeView(boolean showShadow, WeekSpiritStyle controller) {
		imageCorner.setImageBitmap(controller.getTopLeftBitmap(showShadow));
		imageLeft.setImageBitmap(controller.getLeftViewBitmap(showShadow));
		imageTop.setImageBitmap(controller.getTopViewBitmap(showShadow));
	}
}
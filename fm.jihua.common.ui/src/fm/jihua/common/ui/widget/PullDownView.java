package fm.jihua.common.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
import fm.jihua.ui.R;

/**
 * @author Ecpalm
 */
public class PullDownView extends FrameLayout implements
		GestureDetector.OnGestureListener, Animation.AnimationListener {
	public static int MAX_LENGHT = 0;
	public static final int STATE_CLOSE = 1;
	public static final int STATE_OPEN = 2;
	public static final int STATE_OPEN_MAX = 4;
	public static final int STATE_OPEN_MAX_RELEASE = 5;
	public static final int STATE_OPEN_RELEASE = 3;
	public static final int STATE_UPDATE = 6;
	public static final int STATE_UPDATE_SCROLL = 7;
	private Animation animationDown;
	private Animation animationUp;
	private ImageView arrow;
	private String date;
	private GestureDetector detector = new GestureDetector(this);
	private Flinger flinger = new Flinger();
	private boolean isAutoScroller;
	private int pading;
	private ProgressBar progressBar;
	private int state = STATE_CLOSE;
	private TextView title;
	private FrameLayout updateContent;
	private UpdateHandle updateHandle;

	private Drawable arrow_up, arrow_down;

	public PullDownView(Context paramContext) {
		super(paramContext);
		init();
		addUpdateBar();
	}

	public PullDownView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
		addUpdateBar();
	}

	private void addUpdateBar() {
		arrow_up = getResources().getDrawable(R.drawable.arrow_up);
		arrow_down = getResources().getDrawable(R.drawable.arrow_down);

		this.animationUp = AnimationUtils.loadAnimation(getContext(),
				R.anim.rotate_up);
		this.animationUp.setAnimationListener(this);
		this.animationDown = AnimationUtils.loadAnimation(getContext(),
				R.anim.rotate_down);
		this.animationDown.setAnimationListener(this);
		View updateBarView = LayoutInflater.from(getContext()).inflate(
				R.layout.vw_update_bar, null);
		updateBarView.setVisibility(View.INVISIBLE);
		addView(updateBarView);
		this.arrow = new ImageView(getContext());
		this.arrow.setBackgroundColor(0x00000000);
		LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
		this.arrow.setScaleType(ImageView.ScaleType.FIT_CENTER);
		this.arrow.setLayoutParams(layoutParams);
		this.arrow.setImageDrawable(arrow_down);
		this.updateContent = ((FrameLayout) getChildAt(0).findViewById(
				R.id.iv_content));
		this.updateContent.addView(this.arrow);
		layoutParams = new FrameLayout.LayoutParams(-2, -2);
		layoutParams.gravity = 17;
		this.progressBar = new ProgressBar(getContext(), null,
				android.R.attr.progressBarStyleInverse);
		this.progressBar.setBackgroundColor(0x00000000);
		int i = getResources().getDimensionPixelSize(R.dimen.updatebar_padding);
		this.progressBar.setPadding(i, i, i, i);
		this.progressBar.setLayoutParams(layoutParams);
		this.updateContent.addView(this.progressBar);
		this.title = ((TextView) findViewById(R.id.tv_title));
	}

	private void init() {
		MAX_LENGHT = getResources().getDimensionPixelSize(
				R.dimen.updatebar_height);
		setDrawingCacheEnabled(false);
		setBackgroundDrawable(null);
		setClipChildren(false);
		this.detector.setIsLongpressEnabled(true);
	}

	private boolean move(float distance, boolean direction) {
		if (this.state == STATE_UPDATE) {
			if (distance < 0.0F)
				return true;
			if (direction)
				this.state = STATE_UPDATE_SCROLL;
		}
		if ((this.state != STATE_UPDATE_SCROLL) || (distance >= 0.0F)
				|| (-this.pading < MAX_LENGHT)) {
			this.pading = (int) (distance + this.pading);
			if (this.pading > 0)
				this.pading = 0;
			if (direction) {
				switch (this.state) {
				case STATE_CLOSE:
					if (this.pading >= 0)
						break;
					this.state = STATE_OPEN;
					this.progressBar.setVisibility(View.INVISIBLE);
					this.arrow.setVisibility(View.VISIBLE);
					break;
				case STATE_OPEN:
					if (Math.abs(this.pading) < MAX_LENGHT) {
						if (this.pading != 0)
							break;
						this.state = STATE_CLOSE;
						break;
					}
					this.state = STATE_OPEN_MAX;
					this.progressBar.setVisibility(View.INVISIBLE);
					this.arrow.setVisibility(View.VISIBLE);
					this.arrow.startAnimation(this.animationUp);
					break;
				case STATE_OPEN_RELEASE:
				case STATE_OPEN_MAX_RELEASE:
					if (!direction) {
						if (this.pading == 0)
							this.state = STATE_CLOSE;
					} else if (Math.abs(this.pading) < MAX_LENGHT) {
						if (Math.abs(this.pading) >= MAX_LENGHT) {
							if (this.pading == 0)
								this.state = STATE_CLOSE;
						} else {
							this.state = STATE_OPEN;
							this.progressBar.setVisibility(View.INVISIBLE);
							this.arrow.setVisibility(View.VISIBLE);
							this.arrow.startAnimation(this.animationDown);
						}
					} else {
						this.state = STATE_OPEN_MAX;
						this.progressBar.setVisibility(View.INVISIBLE);
						this.arrow.setVisibility(View.VISIBLE);
						this.arrow.startAnimation(this.animationUp);
					}
					invalidate();
					break;
				case STATE_OPEN_MAX:
					if (Math.abs(this.pading) >= MAX_LENGHT)
						break;
					this.state = STATE_OPEN;
					this.progressBar.setVisibility(View.INVISIBLE);
					this.arrow.setVisibility(View.VISIBLE);
					this.arrow.startAnimation(this.animationDown);
					break;
				case STATE_UPDATE:
					if (this.pading == 0)
						this.state = STATE_CLOSE;
					invalidate();
					break;
				}
			} else {
				if (this.state != STATE_OPEN_MAX_RELEASE) {
					if ((this.state != STATE_UPDATE) || (this.pading != 0)) {
						if ((this.state != STATE_OPEN_RELEASE)
								|| (this.pading != 0)) {
							if ((this.state == STATE_UPDATE_SCROLL)
									&& (this.pading == 0))
								this.state = STATE_CLOSE;
						} else
							this.state = STATE_CLOSE;
					} else
						this.state = STATE_CLOSE;
				} else {
					this.state = STATE_UPDATE;
					if (this.updateHandle != null)
						this.updateHandle.onUpdate();
				}
				invalidate();
			}
		}
		return true;
	}

	private boolean release() {
		boolean ret = false;
		if (this.pading < 0) {
			switch (this.state) {
			case STATE_OPEN:
			case STATE_OPEN_RELEASE:
				if (Math.abs(this.pading) < MAX_LENGHT)
					this.state = STATE_OPEN_RELEASE;
				scrollToClose();
				break;
			case STATE_OPEN_MAX:
			case STATE_OPEN_MAX_RELEASE:
				this.state = STATE_OPEN_MAX_RELEASE;
				scrollToUpdate();
			}
			ret = true;
		}
		return ret;
	}

	private void scrollToClose() {
		this.flinger.startUsingDistance(-this.pading, 300);
	}

	private void scrollToUpdate() {
		this.flinger.startUsingDistance(-this.pading - MAX_LENGHT, 300);
	}

	private void updateView() {
		View view1 = getChildAt(0);
		View view2 = getChildAt(1);

		if (this.date == null)
			this.date = "";
		int j;
		int i;
		switch (this.state) {
		case STATE_CLOSE:
			if (view1.getVisibility() != View.INVISIBLE)
				view1.setVisibility(View.INVISIBLE);
			view2.offsetTopAndBottom(-view2.getTop());
			break;
		case STATE_OPEN:
		case STATE_OPEN_RELEASE:
			j = view2.getTop();
			view2.offsetTopAndBottom(-this.pading - j);
			if (view1.getVisibility() != View.VISIBLE)
				view1.setVisibility(View.VISIBLE);
			i = view1.getTop();
			view1.offsetTopAndBottom(-MAX_LENGHT - this.pading - i);
			this.title.setText(getResources().getString(R.string.drop_down)
					+ "\n" + this.date);
			break;
		case STATE_OPEN_MAX:
		case STATE_OPEN_MAX_RELEASE:
			j = view2.getTop();
			view2.offsetTopAndBottom(-this.pading - j);
			if (view1.getVisibility() != View.VISIBLE)
				view1.setVisibility(View.VISIBLE);
			i = view1.getTop();
			view1.offsetTopAndBottom(-MAX_LENGHT - this.pading - i);
			this.title.setText(getResources()
					.getString(R.string.release_update) + "\n" + this.date);
			break;
		case STATE_UPDATE:
		case STATE_UPDATE_SCROLL:
			j = view2.getTop();
			view2.offsetTopAndBottom(-this.pading - j);
			i = view1.getTop();
			if (this.progressBar.getVisibility() != View.VISIBLE)
				this.progressBar.setVisibility(View.VISIBLE);
			if (this.arrow.getVisibility() != View.INVISIBLE)
				this.arrow.setVisibility(View.INVISIBLE);
			this.title.setText(getResources().getString(R.string.loading)
					+ "\n" + this.date);
			view1.offsetTopAndBottom(-MAX_LENGHT - this.pading - i);
			if (view1.getVisibility() == View.VISIBLE)
				break;
			view1.setVisibility(View.VISIBLE);
		}
		invalidate();
	}

	public boolean dispatchTouchEvent(MotionEvent e) {
		int i = MotionEvent.EDGE_TOP;
		boolean ret = false;
		if (!this.isAutoScroller) {
			boolean detectorTouchRet = this.detector.onTouchEvent(e);
			int j = e.getAction();
			if (j != i) {
				if (j == MotionEvent.ACTION_CANCEL)
					detectorTouchRet = release();
			} else
				detectorTouchRet = release();
			if ((this.state != STATE_UPDATE)
					&& (this.state != STATE_UPDATE_SCROLL)) {
				if (((!detectorTouchRet) && (this.state != STATE_OPEN)
						&& (this.state != STATE_OPEN_MAX)
						&& (this.state != STATE_OPEN_MAX_RELEASE) && (this.state != STATE_OPEN_RELEASE))
						|| (getChildAt(i).getTop() == 0)) {
					updateView();
					ret = super.dispatchTouchEvent(e);
				} else {
					e.setAction(MotionEvent.ACTION_CANCEL);
					super.dispatchTouchEvent(e);
					updateView();
				}
			} else {
				updateView();
				ret = super.dispatchTouchEvent(e);
			}
		}
		return ret;
	}

	/**
	 * 初始化标题字体颜色�?
	 * 
	 * @param color
	 *            0xffffffff
	 */
	public void initTitleColor(int color) {
		this.title.setTextColor(color);
	}

	public void endUpdate(String date) {
		this.date = date;
		if (this.pading == 0)
			this.state = STATE_CLOSE;
		else
			scrollToClose();
	}

	public void onAnimationEnd(Animation animation) {
		if ((this.state != STATE_OPEN) && (this.state != STATE_OPEN_RELEASE))
			this.arrow.setImageDrawable(arrow_up);
		else
			this.arrow.setImageDrawable(arrow_down);
	}

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		getChildAt(0).layout(0, -MAX_LENGHT - this.pading, getMeasuredWidth(),
				-this.pading);
		getChildAt(1).layout(0, -this.pading, getMeasuredWidth(),
				getMeasuredHeight() - this.pading);
	}

	public void onLongPress(MotionEvent e) {
	}

	// mode by liufei 6/14
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		boolean bool = false;
		float f = (float) (0.5D * distanceY);
		AdapterView<?> adapterView = null;
		if (keyAdapterView)
			adapterView = (AdapterView<?>) getChildAt(1);
		else {
			FrameLayout frameLayout = (FrameLayout) getChildAt(1);
			adapterView = (AdapterView<?>) frameLayout.getChildAt(0);
		}
		// 为了让ListView在为空的时�?也能刷新，下面一句先注释�?---dangtao 2012/11/6
		// if (adapterView.getCount() != 0) {
		int i;
		if (adapterView.getFirstVisiblePosition() != 0) {
			i = 0;
		} else {
			i = 1;
		}
		if ((i != 0) && (adapterView.getChildAt(0) != null)) {
			if (adapterView.getChildAt(0).getTop() != 0
					&& adapterView instanceof ListView) {
				i = 0;
			} else if (adapterView.getChildAt(0).getTop() < 0
					&& adapterView instanceof GridView) {
				i = 0;
			} else {
				i = 1;
			}
		}
		if (adapterView instanceof GridView) {
			if (((f < 0.0F) && (i != 0)) || (this.pading < 0))
				bool = move(f, true);
		}
		if (adapterView instanceof ListView) {
			if (((f < 0.0F) && (i != 0)) || (this.pading < 0)) {
				bool = move(f, true);
			}
		}
		// }
		return bool;
	}

	private boolean keyAdapterView = true;

	public void setIsAdapterView(boolean keyAdapterView) {
		this.keyAdapterView = keyAdapterView;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public void setUpdateDate(String date) {
		this.date = date;
	}

	public void setUpdateHandle(UpdateHandle updateHandle) {
		this.updateHandle = updateHandle;
	}

	public void update() {
		this.pading = (-MAX_LENGHT);
		this.state = STATE_UPDATE_SCROLL;
		postDelayed(new Runnable() {
			public void run() {
				PullDownView.this.updateView();
			}
		}, 10L);
	}

	public void updateWithoutOffset() {
		this.state = STATE_UPDATE_SCROLL;
		invalidate();
	}

	private class Flinger implements Runnable {
		private int lastFlingX;
		private Scroller scroller = new Scroller(PullDownView.this.getContext());

		public Flinger() {
		}

		private void startCommon() {
			PullDownView.this.removeCallbacks(this);
		}

		public void run() {
			Scroller scroller = this.scroller;
			boolean bool = scroller.computeScrollOffset();
			int i = scroller.getCurrX();
			int j = this.lastFlingX - i;
			PullDownView.this.move(j, false);
			PullDownView.this.updateView();
			if (!bool) {
				PullDownView.this.isAutoScroller = false;
				PullDownView.this.removeCallbacks(this);
			} else {
				this.lastFlingX = i;
				PullDownView.this.post(this);
			}
		}

		public void startUsingDistance(int dx, int duration) {
			if (dx == 0)
				dx--;
			startCommon();
			this.lastFlingX = 0;
			this.scroller.startScroll(0, 0, -dx, 0, duration);
			PullDownView.this.isAutoScroller = true;
			PullDownView.this.post(this);
		}
	}

	public static abstract interface UpdateHandle {
		public abstract void onUpdate();
	}

}

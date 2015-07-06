package fm.jihua.kecheng.ui.widget.weekview;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.AbsoluteLayout.LayoutParams;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.common.ShareActivity;
import fm.jihua.kecheng.ui.widget.WeekView;
import fm.jihua.kecheng.utils.AnimationUtils;
import fm.jihua.kecheng.utils.CommonUtils;

/**
 * @date 2013-7-25
 * @introduce WeekView的绘制
 */
public class WeekShakeEggController {

	WeekView weekView;

	public WeekShakeEggController(WeekView weekView) {
		super();
		this.weekView = weekView;
	}

	public View hideView;
	public int currentType = 0;
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_SHAKE = 1;
	public static final int TYPE_GONE = 2;
	public boolean isAnimationing = false;
	
	public static final int STATUS_ADD = 10;
	public static final int STATUS_REMOVE = 11;

	public void animateCourses() {
		if (!isAnimationing) {
			int childCount = 0;
			switch (currentType) {
			case TYPE_NORMAL:

				childCount = weekView.getChildCount();
				currentType = TYPE_SHAKE;
				for (int i = 0; i < childCount; i++) {
					final View childAt = weekView.getChildAt(i);
					childAt.setTag(R.id.tag_anim, TYPE_SHAKE);
					Animation createOutAnimation = AnimationUtils.getInstance().createSharkAnim();
					childAt.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (currentType == TYPE_SHAKE) {
//								weekView.isTouchChildView = true;
								if (event.getAction() == MotionEvent.ACTION_UP && !isAnimationing && ((Integer) childAt.getTag(R.id.tag_anim)) == TYPE_SHAKE) {

									childAt.clearAnimation();
									AnimationSet anim = AnimationUtils.getInstance().createBoomAnimation();
									childAt.setVisibility(View.GONE);
									childAt.startAnimation(anim);
									childAt.setTag(R.id.tag_anim, TYPE_GONE);
									CommonUtils.playSound(weekView.getContext(), R.raw.bomb);
									// 全部点完，也显示文字
									if (getVisiableViewCount() <= 0) {
										addSharkText();
										currentType = TYPE_GONE;
									}
								}
								return true;
							}
							return false;
						}
					});
					childAt.startAnimation(createOutAnimation);
					isAnimationing = true;
					childAt.postDelayed(new Runnable() {

						@Override
						public void run() {
							isAnimationing = false;
						}
					}, 1500);
				}

				break;
			case TYPE_SHAKE:

				childCount = weekView.getChildCount();
				for (int i = 0; i < childCount; i++) {
					final View childAt = weekView.getChildAt(i);
					int childType = (Integer) childAt.getTag(R.id.tag_anim);
					if (childType == TYPE_SHAKE) {
						childAt.clearAnimation();
						Animation anim = AnimationUtils.getInstance().createDismissAnim();
						anim.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								isAnimationing = true;
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								isAnimationing = false;
								if (currentType == TYPE_SHAKE) {
									addSharkText();
									currentType = TYPE_GONE;
								}
							}
						});
						childAt.startAnimation(anim);
						childAt.setVisibility(View.GONE);
					}
					childAt.setTag(R.id.tag_anim, TYPE_GONE);
				}
				break;
			case TYPE_GONE:
				if(changedListener!=null){
					changedListener.onStatusChanged(hideView, STATUS_REMOVE);
				}
				childCount = weekView.getChildCount();
				for (int i = 0; i < childCount; i++) {
					View childAt = weekView.getChildAt(i);
					childAt.setTag(R.id.tag_anim, TYPE_NORMAL);
					childAt.setVisibility(View.VISIBLE);
					AnimationSet createEnterAnimation = AnimationUtils.getInstance().createEnterAnimation();
					createEnterAnimation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							isAnimationing = true;
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							isAnimationing = false;
						}
					});
					childAt.startAnimation(createEnterAnimation);
				}
				currentType = TYPE_NORMAL;
				hideView = null;
				break;
			}

		}
	}

	public void turn2Normal() {
		int childCount = weekView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = weekView.getChildAt(i);
			childView.clearAnimation();
			childView.setVisibility(View.VISIBLE);
		}
		if (hideView != null) {
			hideView.setVisibility(View.GONE);
			hideView = null;
		}
		currentType = TYPE_NORMAL;
	}

	private int getVisiableViewCount() {
		int childCount = weekView.getChildCount();
		int count = 0;
		for (int i = 0; i < childCount; i++) {
			View childAt = weekView.getChildAt(i);
			if (((Integer) childAt.getTag(R.id.tag_anim)) == TYPE_SHAKE)
				count++;
		}
		return count;
	}

	private void addSharkText() {
		hideView = LayoutInflater.from(weekView.getContext()).inflate(R.layout.caidan_course_hidelayout, null);
		hideView.findViewById(R.id.button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent((Activity) weekView.getContext(), ShareActivity.class);
				intent.putExtra("ONLYTEXT", true);
				intent.putExtra("CONTENT", "我在课程格子发现了一个彩蛋——木有烦恼的新世界！你能找得到么？");
				((Activity) weekView.getContext()).startActivity(intent);
			}
		});
		hideView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0, 0));
		if(changedListener!=null){
			changedListener.onStatusChanged(hideView, STATUS_ADD);
		}
	}
	
	public interface OnEggShakeStatusChangedListener {
		public void onStatusChanged(View view,int status);
	}

	public void setOnEggShakeStatusChangedListener(OnEggShakeStatusChangedListener changedListener) {
		this.changedListener = changedListener;
	}

	OnEggShakeStatusChangedListener changedListener;
}

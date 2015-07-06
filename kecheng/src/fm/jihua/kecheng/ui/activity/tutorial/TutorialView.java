package fm.jihua.kecheng.ui.activity.tutorial;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.ImageHlp;

public class TutorialView extends FrameLayout {
	
	public enum TrigerEvent{
		CLICK,FLING_TO_RIGHT, FLING_TO_LEFT
	}
	
	public static class TutorialParams{
		public int id;
		public int noticeLayout;
		public int noticeImage;
		public int noticeHeight;
		public String category;
		public CharSequence noticeText;
		public Point noticePosition;
		public int animateImage;
		public int animateRotate;
		public Animation animation;
		public Point animationPosition;
		public List<Rect> coverRects;
		public Rect notCoverRect;
		public Rect clickRegion;
		public boolean sendEvent = true;
		public TrigerEvent trigerEvent;
		public Object tag;
		public boolean triggered;
		TrigerCallback trigerCallback;

		public TutorialParams(){
			//- means margin from bottom
			this.noticePosition = new Point(0, ImageHlp.changeToSystemUnitFromDP(App.getInstance(), -110));
//			this.noticeImage = R.drawable.tutorial_tuotuo_hello_left;
			this.noticeLayout = R.layout.view_tutorial_notice;
			this.trigerEvent = TrigerEvent.CLICK;
			this.noticeHeight = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 80);
		}
		
		public TutorialParams(String category, CharSequence noticeText, TrigerCallback trigerCallback){
			this();
			this.category = category;
			this.noticeText = noticeText;
			this.trigerCallback = trigerCallback;
		}
		
		public TutorialParams(String category, CharSequence noticeText, TrigerCallback trigerCallback, int animationImage, Animation animation, Point animationPosition){
			this(category, noticeText, trigerCallback);
			this.animation = animation;
			this.animateImage = animationImage;
			this.animationPosition = animationPosition;
		}
		
		public void applyOffset(int offsetX, int offsetY){
			if (animationPosition != null) {
				animationPosition = new Point(animationPosition);
				animationPosition.x += offsetX;
				animationPosition.y += offsetY;
			}
			if (notCoverRect != null) {
				notCoverRect = new Rect(notCoverRect);
				notCoverRect.offset(offsetX, offsetY);
			}
			if (clickRegion != null && clickRegion != notCoverRect) {
				clickRegion = new Rect(clickRegion);
				clickRegion.offset(offsetX, offsetY);
			}
			List<Rect> rects = new ArrayList<Rect>();
			if (coverRects != null) {
				for (Rect rect : coverRects) {
					Rect newRect = new Rect(rect);
					newRect.offset(offsetX, offsetY);
					rects.add(newRect);
				}
			}
			coverRects = rects;
		}
	}
	
	TutorialParams params;
	GestureDetector gestureDetector;
	View noticeView;
	ImageView animaImageView;
	Drawable drawableShadow;

	public TutorialView(Context context) {
		super(context);
		gestureDetector = new GestureDetector(getContext(), new DefaultGestureDetector());
		animaImageView = new ImageView(getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.TOP;
		animaImageView.setVisibility(View.VISIBLE);
		this.addView(animaImageView, lp);
		setWillNotDraw(false);
		drawableShadow = ImageHlp.getRepeatShadowBitmap(App.getInstance(), R.drawable.paster_edit_class_cover_repeat);
	}
	
	public void setTutorialParams(TutorialParams params){
		this.params = params;
		init();
	}
	
	public TutorialParams getTutorialParams(){
		return this.params;
	}
	
	void init(){
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View newNoticeView = layoutInflater.inflate(params.noticeLayout, this, false);
		ImageView iv = (ImageView)newNoticeView.findViewById(R.id.notice_image);
		iv.setImageResource(params.noticeImage);
		TextView tv = (TextView)newNoticeView.findViewById(R.id.notice_text);
		tv.setText(params.noticeText);
		if (noticeView == null) {
			this.addView(newNoticeView);
		}else {
			LayoutParams lp = (LayoutParams) noticeView.getLayoutParams();
			lp.gravity = Gravity.TOP;
			if (params.noticePosition.y < 0) {
				lp.topMargin = this.getHeight() + params.noticePosition.y - lp.height;
			}else {
				lp.topMargin = params.noticePosition.y;
			}
			if (noticeView != newNoticeView) {
				this.removeView(noticeView);
				this.addView(newNoticeView, lp);
			}
		}
		noticeView = newNoticeView;
		if (params.animateImage != 0) {
			LayoutParams lp = (LayoutParams) animaImageView.getLayoutParams();
			lp.topMargin = params.animationPosition.y;
			lp.leftMargin = params.animationPosition.x;
			animaImageView.setLayoutParams(lp);
			animaImageView.setImageResource(params.animateImage);
			if (params.animateRotate != 0) {
				Matrix matrix=new Matrix();
				animaImageView.setScaleType(ScaleType.MATRIX);   //required
				Drawable drawable = getResources().getDrawable(params.animateImage);
				int width = drawable.getIntrinsicWidth();
				int height = drawable.getIntrinsicHeight();
				matrix.postRotate((float) params.animateRotate, width/2, height/2);
				animaImageView.setImageMatrix(matrix);
			}else {
				animaImageView.setImageMatrix(null);
			}
			animaImageView.startAnimation(params.animation);
			animaImageView.setVisibility(View.VISIBLE);
		}else {
			animaImageView.setVisibility(View.GONE);
			animaImageView.setAnimation(null);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (params.coverRects != null) {
			for (Rect rect : params.coverRects) {
				drawableShadow.setBounds(rect);
				drawableShadow.draw(canvas);
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	public void setNoticeText(CharSequence text){
		TextView tv = (TextView)noticeView.findViewById(R.id.notice_text);
		tv.setText(text);
	}
	
	public void setNoticeVisibility(int visibility){
		if (noticeView != null && noticeView.getVisibility() != visibility) {
			noticeView.setVisibility(visibility);
		}
	}
	
	class DefaultGestureDetector extends SimpleOnGestureListener {
		
		@Override
		public boolean onDown(MotionEvent e) {
			super.onDown(e);
			if (params.trigerEvent == TrigerEvent.CLICK && !params.triggered) {
				if (params.clickRegion == null || params.clickRegion.contains((int)e.getX(), (int)e.getY())) {
					if (params.trigerCallback != null) {
						boolean sendEvent = params.sendEvent;
						boolean careEvent = params.clickRegion != null;
						params.triggered = true;
						post(new Runnable() {
							
							@Override
							public void run() {
								params.trigerCallback.onTriger(params);
							}
						});
						if (careEvent && sendEvent) {
							return false;
						}else {
							return true;
						}
					}
				}
			}else if (params.trigerEvent != TrigerEvent.CLICK ) {
				if (params.trigerCallback != null) {
					params.trigerCallback.onError(params);
				}
			}
			return true;
		}

		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			if (params.trigerEvent == TrigerEvent.FLING_TO_RIGHT && velocityX < 0 ||
//					(params.trigerEvent == TrigerEvent.FLING_TO_LEFT && velocityX > 0)) {
//				if (params.trigerCallback != null) {
//					params.trigerCallback.onTriger(params);
//				}
//			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (params.trigerEvent == TrigerEvent.FLING_TO_RIGHT ||
					(params.trigerEvent == TrigerEvent.FLING_TO_LEFT)) {
				if (params.trigerCallback != null) {
					params.trigerCallback.onTriger(params);
				}
			}
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
	
	public interface TrigerCallback{
		public void onTriger(TutorialParams params);
		
		public void onError(TutorialParams params);
	}
}

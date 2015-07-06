package fm.jihua.kecheng.ui.widget;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.ImageHlp;

public class SharePic extends View {
	
	Drawable bg;
	Bitmap pic;
	Paint mPaint = new Paint();
	private ArrayList<SoftReference<Bitmap>> mBitmapRefs = new ArrayList<SoftReference<Bitmap>>();  
	boolean isShow;

	public SharePic(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setWillNotDraw(false);
		init();
	}

	void init(){
		bg = getResources().getDrawable(R.drawable.share_pic_bg);
		mPaint.setAntiAlias(true);   
	}
	
	public void setImage(Bitmap bmp){
//		pic = ImageHlp.setBlur(bmp, 1);
		pic = ImageHlp.blurImageAmeliorate(bmp);
		bmp.recycle();
		mBitmapRefs.add(new SoftReference<Bitmap>(pic));
		post(new Runnable() {
			@Override
			public void run() {
				isShow = true;
				invalidate();
			}
		});
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (pic != null && !pic.isRecycled()) {
			pic.recycle();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		canvas.rotate(10);
		canvas.translate(63, 8);
		if (pic != null) {
			Rect src = new Rect(0, 0, pic.getWidth(), pic.getHeight());
			Rect dst = new Rect(0, 0, bg.getIntrinsicWidth() - 34, bg.getIntrinsicHeight() - 20);
			canvas.drawBitmap(pic, src, dst, mPaint);
		}
		canvas.translate(-6, -12);
		bg.setBounds(0, 0, bg.getIntrinsicWidth()-20, bg.getIntrinsicHeight());
		bg.draw(canvas);
		canvas.translate(-57, 4);
		canvas.rotate(-10);
	}
	
	int fromDP(int size){
		return ImageHlp.changeToSystemUnitFromDP(getContext(), size);
	}
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(bg.getIntrinsicWidth()+50, bg.getIntrinsicHeight()+fromDP(30));
    }
    
    public Bitmap getBitmap(){
    	return pic;
    }
    
    public boolean isShowing(){
    	return isShow;
    }
	
}

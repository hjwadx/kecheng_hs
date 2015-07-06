package fm.jihua.kecheng.ui.widget;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.ui.widget.weekview.WeekCanvasUtils;
import fm.jihua.kecheng.utils.CourseHelper;
import fm.jihua.kecheng.utils.ImageHlp;

@SuppressWarnings("deprecation")
public class WidgetWeekView extends FrameLayout {
	int[] colors = {Color.parseColor("#FA7886"), Color.parseColor("#34CED9"), Color.parseColor("#38D3A9"), Color.parseColor("#AF92D7"), Color.parseColor("#FCDC36"), Color.parseColor("#64BAFF"), Color.parseColor("#FFA941"), Color.parseColor("#A8D241"), Color.parseColor("#F895D7")};
	List<CourseBlock> courseBlocks = new ArrayList<CourseBlock>(); //only for refresh
	double blockWidth;
	double blockHeight;
	int offsetTop;
	int offsetLeft;
	int timeSlot;
	Context mContext;
	Paint courseTextPaint;
	private int coursePadding = 0;
	private int courseSpacing;
	private int courseHintLineHeight;
	
	String[] weekDays = {"一", "二", "三", "四", "五", "六", "日"};


	public static double titleHeightPercent = 48.0/64.0;
	public static double titleWidthPercent = 32.0/64.0;
	Drawable drawableWeekView;
//	Drawable drawableBackground;
	HashMap<String, Integer> colorMap;
	String TAG = this.getClass().getSimpleName();
	boolean DEBUG = false;
	
	public WidgetWeekView(Context context) {
		super(context);
		init(context);
	}
	
	public WidgetWeekView(Context context, AttributeSet attrs){
		super(context, attrs);
		this.setWillNotDraw(false);
		init(context);
	}
	
	void init(Context context){
		mContext = context;
		colorMap = new HashMap<String, Integer>();
		coursePadding = ImageHlp.changeToSystemUnitFromDP(mContext.getApplicationContext(), 3);
		courseSpacing = ImageHlp.changeToSystemUnitFromDP(mContext.getApplicationContext(), 2);
		drawableWeekView = getResources().getDrawable(R.drawable.widget_big_bg);
//		setBackgroundColor(R.color.black);
//		setBackground(0);
	}
	

	public void setData(List<CourseBlock> courses) {
		this.courseBlocks = courses;
		if (timeSlot == 0) {
			timeSlot = CourseHelper.getMaxTimeSlot(courses);
			timeSlot = Math.max(timeSlot, 12);
		}
	}
	
	public void setTimeSlot(int timeSlot){
		this.timeSlot = timeSlot;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);  
	    int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
	    switch (heightSpecMode) {
		case MeasureSpec.UNSPECIFIED:
			setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
	                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
			break;
		default:
			int height = timeSlot > 12 ? (int) ((titleHeightPercent + timeSlot)*heightSpecSize/(titleHeightPercent + 12)) : heightSpecSize;
			if (DEBUG) {
				Log.d(TAG, "onMeasure h="+height);
			}
			this.setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
			break;
		}
	}
	
	//because setBackgroundDrawable will auto change the bounds,so wo have to draw it myself
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE); 
		int width = Compatibility.getWidth(windowManager.getDefaultDisplay());
		blockWidth =  (this.getWidth() / ( titleWidthPercent + 7 ));
		blockHeight =  (this.getHeight() / ( titleHeightPercent + timeSlot ));
		offsetTop = (int) (titleHeightPercent*blockHeight);
		offsetLeft = (int) (titleWidthPercent*blockWidth);
		drawableWeekView.setBounds(0, 0, width + ImageHlp.changeToSystemUnitFromDP(mContext, 3), (600*width/480) + ImageHlp.changeToSystemUnitFromDP(mContext, 3));
		drawableWeekView.setAlpha(230);
		drawableWeekView.draw(canvas);
		if (getHeight() != 0) {
			drawLine(canvas);
			drawCourseBlocks(canvas, courseBlocks);
			drawDayView(canvas);
			drawTimeView(canvas);
		}
	}
	
	public void drawLine(Canvas canvas){
		Paint pb = new Paint();
		pb.setColor(getResources().getColor(R.color.divider_bg));
		pb.setStrokeWidth(0);
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE); 
		int width = Compatibility.getWidth(windowManager.getDefaultDisplay());
		for(int i = 0; i < timeSlot; i++) {
			int top = (int) (offsetTop+blockHeight*i);
	        canvas.drawLine(ImageHlp.changeToSystemUnitFromDP(mContext, 1), top, width, top, pb);
		}
	}
	
	public void drawCourseBlocks(Canvas canvas, List<CourseBlock> blocks){
		for(CourseBlock block : blocks){
			if (block.start_slot > 0 && block.active) {
				drawCourseBlock(canvas, block, false);
			}
		}
	}
	
	public Rect getCourseRect(CourseBlock courseBlock){
		int top = (int) (offsetTop + blockHeight * (courseBlock.start_slot - 1));
		int bottom = (int) (offsetTop + blockHeight * (courseBlock.end_slot));
		int left = (int) (offsetLeft + blockWidth * (courseBlock.getWeekIndex()));
		int right = (int) (offsetLeft + blockWidth * (courseBlock.getWeekIndex()+1));
		return new Rect(left, top, right, bottom);
	}
	
	public void drawCourseBlock(Canvas canvas, CourseBlock courseBlock, boolean isTucked) {
		Rect rect = getCourseRect(courseBlock);
		Paint paint;
		int color = 0;
		if (colorMap.containsKey(courseBlock.name)) {
			color = colorMap.get(courseBlock.name);
		}else {
			if(courseBlock.event == null){
				color = colors[colorMap.size()%colors.length];
			} else {
				color = mContext.getResources().getColor(R.color.default_event_color);
			}
			colorMap.put(courseBlock.name, color);
		}
		color = color & 0xCCFFFFFF;
	    paint = new Paint();
		paint.setColor(color);
		
		canvas.drawRect(rect, paint);
		drawCourseText(canvas, courseBlock);
//		int topLineRight = rect.right;
		
		//top bottom line
//		canvas.drawRect(rect.left, rect.top, topLineRight, rect.top + courseHintLineHeight, courseTopPaint);
//		canvas.drawRect(rect.left, rect.bottom - courseHintLineHeight, rect.right, rect.bottom, courseBottomPaint);
	}
	
	void drawDayView (Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.textcolor_80));
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, mContext.getApplicationContext().getResources().getDisplayMetrics());
		paint.setTextSize(size);
		paint.setAntiAlias(true);
		for(int i = 0; i < 7; i++) {
			int padding = 0;
			int top = (int) (offsetTop / 2 + size/2);
			int left = (int) (offsetLeft+blockWidth*(i + 0.5) - size/2);
			canvas.drawText(weekDays[i], left+padding, top+padding, paint);
		}
	}
	
	void drawTimeView (Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.textcolor_80));
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, mContext.getApplicationContext().getResources().getDisplayMetrics());
		paint.setTextSize(size);
		paint.setAntiAlias(true);
		for(int i = 0; i < timeSlot; i++) {
			int padding = 0;
			int top = (int) (offsetTop+blockHeight*(i + 0.5) + + size/2);
			int left = (int) (offsetLeft / 4 + size / 4);
			if(i > 8){
				left = (int) (offsetLeft / 4);
			}
			canvas.drawText(""+(i+1), left+padding, top+padding, paint);
		}
	}
	
	private void drawCourseText(Canvas canvas, CourseBlock courseBlock){
		courseTextPaint = new Paint();
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, mContext.getApplicationContext().getResources().getDisplayMetrics());
		courseTextPaint.setTextSize(size);
		courseTextPaint.setAntiAlias(true);
		
		int lineCount = 0;
		String content = courseBlock.name + (TextUtils.isEmpty(courseBlock.room) ? "" : " @" + courseBlock.room);
        char[] textCharArray = content.toCharArray();  
        // 已绘的宽度  
        float drawedWidth = 0;  
        float charWidth;  
        Rect rect = getCourseRect(courseBlock);
        int color = Color.WHITE;
		color = color & 0xCCFFFFFF;
        courseTextPaint.setColor(color);
        for (int i = 0; i < textCharArray.length; i++) {  
            charWidth = courseTextPaint.measureText(textCharArray, i, 1);  
              
            if(textCharArray[i]=='\n'){
                lineCount++;  
                drawedWidth = 0;  
                continue;  
            }  
            //coursePadding * 2 左右两边的边距
            if (drawedWidth + charWidth > blockWidth - coursePadding * 2) {  
                lineCount++;  
                drawedWidth = 0;  
            }
            if(coursePadding + rect.top + (lineCount + 2) * courseTextPaint.getTextSize() > rect.bottom - coursePadding){
            	break;
            }
			canvas.drawText(textCharArray, i, 1, coursePadding + rect.left + drawedWidth, coursePadding + rect.top + (lineCount + 1) * (courseTextPaint.getTextSize() + courseSpacing), courseTextPaint);
            drawedWidth += charWidth;  
        }  
	}
	
//	public void setBasicColor(int color){
//		colors = new int[]{color};
//	}
	
//	public void setBackground(Drawable drawable){
//		drawableBackground = drawable;
//	}
}

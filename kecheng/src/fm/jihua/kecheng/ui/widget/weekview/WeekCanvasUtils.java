package fm.jihua.kecheng.ui.widget.weekview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.utils.ImageHlp;

/**
 *	@date	2013-7-25
 *	@introduce	
 */
public class WeekCanvasUtils {

	private static WeekCanvasUtils canvasUtils;
	public static WeekCanvasUtils getInstance(){
		if(canvasUtils == null){
			canvasUtils = new WeekCanvasUtils();
		}
		return canvasUtils;
	}
	
	//为view添加阴影
	public void setShadowForView(View v) {
		Drawable d = v.getBackground();
		int w = v.getLayoutParams().width;
		int h = v.getLayoutParams().height;
		Bitmap bt = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas mCanvas = new Canvas(bt);
		d.setBounds(0, 0, w, h);
		d.draw(mCanvas);
		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setARGB(64, 255, 255, 255);
		mCanvas.drawLine(0, 0, w, 0, paint);
		paint.setARGB(39, 76, 76, 76);
		mCanvas.drawLine(0, h, w, h, paint);
		BitmapDrawable bd = new BitmapDrawable(v.getContext().getResources(), bt);
		Compatibility.setBackground(v, bd);
	}
	
	/**
	 * 为view设置一个折叠的角
	 * @param v
	 */
	public void setViewTucked(Context context,View v) {
		Drawable d = v.getBackground();
		int w = v.getLayoutParams().width;
		int h = v.getLayoutParams().height;
		int tuck = (int) ImageHlp.changeToSystemUnitFromDP(context, 15);
		Bitmap bt = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(w - tuck, 0);
		path.lineTo(w, tuck);
		path.lineTo(w, h);
		path.lineTo(0, h);
		path.lineTo(0, 0);
		Canvas mCanvas = new Canvas(bt);
		mCanvas.clipPath(path);
		d.setBounds(0, 0, w, h);
		d.draw(mCanvas);

		Path wPath = new Path();
		wPath.moveTo(w - tuck, 0);
		wPath.lineTo(w - tuck, tuck);
		wPath.lineTo(w, tuck);
		wPath.lineTo(w - tuck, 0);
		mCanvas.clipPath(wPath);
		mCanvas.drawARGB(64, 255, 255, 255);

		Canvas mCanvas1 = new Canvas(bt);
		Path bPath = new Path();
		bPath.moveTo(w - tuck, tuck);
		bPath.lineTo(w, tuck);
		bPath.lineTo(w, tuck * 2);
		bPath.lineTo(w - tuck, tuck);
		mCanvas1.clipPath(bPath);
		mCanvas1.drawARGB(39, 0, 0, 0);

		BitmapDrawable bd = new BitmapDrawable(v.getContext().getResources(), bt);
		Compatibility.setBackground(v, bd);
	}
	
	public Rect getHintViewRect(UserSticker paster, Point pt, WeekViewParams weekViewParams){
		int top = (int) (weekViewParams.blockHeight * pt.y + weekViewParams.offsetTop + (weekViewParams.blockHeight / 2 - weekViewParams.pasterHintViewSize / 2));
		int left = (int) (weekViewParams.blockWidth * pt.x + weekViewParams.offsetLeft + (weekViewParams.blockWidth / 2 - weekViewParams.pasterHintViewSize / 2));
		int width = (int) weekViewParams.pasterHintViewSize;
		int height = (int) weekViewParams.pasterHintViewSize;
		return new Rect(left, top, left+width, top+height);
	}
	
	public Rect getCourseRect(CourseBlock courseBlock, WeekViewParams weekViewParams) {
		return weekViewParams.getWeekViewRect(courseBlock.getWeekIndex(), courseBlock.start_slot - 1, courseBlock.getWeekIndex() + 1, courseBlock.end_slot);
	}

	public Rect getPasterRect(UserSticker userSticker, WeekViewParams weekViewParams) {
		return weekViewParams.getWeekViewRect(userSticker.getWeekIndexX(), userSticker.getTimeSlotY(), userSticker.getWeekIndexX() + userSticker.sticker.width, userSticker.getTimeSlotY() + userSticker.sticker.height);
	}

	public Rect getPasterRect(Point point, WeekViewParams weekViewParams) {
		return weekViewParams.getWeekViewRect(point.x, point.y, point.x + 1, point.y + 1);
	}

	public Rect getShadowRect(Point point, int width_number, int height_number, WeekViewParams weekViewParams) {
		return weekViewParams.getWeekViewRect(point.x, point.y, point.x + width_number, point.y + height_number);
	}
	
	public int getColorFromString(String colorString) {
		if (!TextUtils.isEmpty(colorString)) {
			String[] colors = colorString.split(",");
			for (int i = 0; i < colors.length; i++) {
				colors[i] = colors[i].trim();
			}
			if (colors == null || colors.length == 0 || colors.length == 1) {
				return 0;
			} else {
				try {
					if (colors.length == 3) {
						return Color.rgb(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
					} else if (colors.length == 4) {
						return Color.argb((int) (Double.parseDouble(colors[3]) * 255), Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
					} else {
						return 0;
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return 0;
				}
			}
		}
		return 0;
	}
	
	public int getSizeFromStyleFile(int fileSize) {
		return ImageHlp.changeToSystemUnitFromDP(App.getInstance(), fileSize / 2);
	}
	
	private Paint paint_background;
	
	public Paint getHintViewWhiteBackgroundPaint() {
		if (paint_background == null) {
			paint_background = new Paint();
			paint_background.setAntiAlias(true);
			paint_background.setColor(0xAFFFFFFF);
		}
		return paint_background;
	}
	
	private Paint paint_backCircle;

	public Paint getHintViewBackgroundPaint() {
		if (paint_backCircle == null) {
			paint_backCircle = new Paint();
			paint_backCircle.setAntiAlias(true);
			paint_backCircle.setColor(0xFF34ced9);
		}
		return paint_backCircle;
	}
	
	private Paint paint_text;
	public Paint getHintViewTextPaint(int textSize) {
		if (paint_text == null) {
			paint_text = new Paint();
			paint_text.setAntiAlias(true);
			paint_text.setColor(0xFFFFFFFF);
			paint_text.setTextSize(textSize);
		}
		return paint_text;
	}
	
	private Paint paint_cutoff_corss;
	public Paint getCutoffCorssPaint(int color,int stroke_width) {
		if (paint_cutoff_corss == null) {
			paint_cutoff_corss = new Paint();
			paint_cutoff_corss.setAntiAlias(true);
		}
		paint_cutoff_corss.setColor(color);
		paint_cutoff_corss.setStrokeWidth(stroke_width);
		return paint_cutoff_corss;
	}
	
	private Paint paint_cutoff_dashed;
	public Paint getCutoffDashedPaint(int color,int stroke_width,PathEffect effects) {
		if (paint_cutoff_dashed == null) {
			paint_cutoff_dashed = new Paint();
			paint_cutoff_dashed.setAntiAlias(true);
			paint_cutoff_dashed.setStyle(Paint.Style.STROKE);
		}
		paint_cutoff_dashed.setColor(color);
		paint_cutoff_dashed.setStrokeWidth(stroke_width);
		paint_cutoff_dashed.setPathEffect(effects);
		return paint_cutoff_dashed;
	}
	
	private Paint paintHintLine;
	private Paint paintText;
	private Paint paintBack;
	
	public Paint getSizePaintHintLine(int color,int stroke_width){
		if (paintHintLine == null) {
			paintHintLine = new Paint();
			paintHintLine.setAntiAlias(true);
		}
		paintHintLine.setColor(color);
		paintHintLine.setStrokeWidth(stroke_width);
		return paintHintLine;
	}
	
	public Paint getSizePaintText(int textSize){
		if (paintText == null) {
			paintText = new Paint();
			paintText.setAntiAlias(true);
		}
		paintText.setTextSize(textSize);
		return paintText;
	} 
	
	public Paint getSizePaintBack(){
		if (paintBack == null) {
			paintBack = new Paint();
			paintBack.setAntiAlias(true);
		}
		return paintBack;
	}
	
	private Paint paintTopLeftCorner; 
	public Paint getPaintTopLeftCorner(int color){
		if (paintTopLeftCorner == null) {
			paintTopLeftCorner = new Paint();
			paintTopLeftCorner.setAntiAlias(true);
		}
		paintTopLeftCorner.setColor(color);
		return paintTopLeftCorner;
	}
	
	private Paint paintCourseText;
	public Paint getPaintCourseText(){
		if (paintCourseText == null) {
			paintCourseText = new Paint();
			paintCourseText.setColor(Color.WHITE);
			float size = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_SP, 12, App.getInstance().getResources().getDisplayMetrics());
			paintCourseText.setTextSize(size);
			paintCourseText.setAntiAlias(true);
		}
		return paintCourseText;
	}
	
	private Paint paintCourseTopLine;
	public Paint getPaintCourseTopLine(){
		if (paintCourseTopLine == null) {
			paintCourseTopLine = new Paint();
			paintCourseTopLine.setColor(0x40FFFFFF);
		}
		return paintCourseTopLine;
	}
	
	private Paint paintCourseBottomLine;
	public Paint getPaintCourseBottomLine(){
		if (paintCourseBottomLine == null) {
			paintCourseBottomLine = new Paint();
			paintCourseBottomLine.setColor(0x254C4C4C);
		}
		return paintCourseBottomLine;
	}
}

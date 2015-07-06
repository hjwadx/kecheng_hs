package fm.jihua.kecheng.ui.view;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.ImageView;
import fm.jihua.common.utils.ImageHlp;

public class ColorBlock extends ImageView {
	private static String TAG = "ColorBlock";
	
	final static int DEFAULT_SIZE = 60;
	final static int BLOCK_SIZE = 9;
	private Paint[] paints;
	private int[] colors = new int[BLOCK_SIZE];
	Path path = new Path();
	RectF rectF = new RectF();
	Paint paint = new Paint();

	public ColorBlock(Context context) {
		super(context);
	}
	
	public ColorBlock(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public int[] getColors(){
		return this.colors;
	}

	/**设置色块颜色值。
	 * 必须调用此函数，否则初始化失败。
	 * 
	 * @param colors
	 */
	public void setColors(int[] colors){
		Random random = new Random();
		paints = new Paint[BLOCK_SIZE];
		for (int i = 0; i < BLOCK_SIZE; i++) {
			int color;
			if (i < colors.length) {
				color = colors[i];
			}else {
				color = 0xff000000+random.nextInt(0xffffff);
			}
			this.colors[i] = color;
			Paint paint = new Paint();
			paint.setAntiAlias(true);
	        paint.setColor(color);
	        paint.setAntiAlias(true);
//			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        paints[i] = paint;
		}
		Bitmap bmp = generateColorBlock();
		setImageBitmap(bmp);
		invalidate();
//		setImageResource(R.drawable.icon);
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
////		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);  
////	    int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
////	    int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);  
////	    int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
////	    switch (heightSpecMode) {
////		case MeasureSpec.UNSPECIFIED:
////		case MeasureSpec.AT_MOST:
//			setMeasuredDimension(ImageHlp.changeToSystemUnitFromDP(getContext(), DEFAULT_SIZE),
//					ImageHlp.changeToSystemUnitFromDP(getContext(), DEFAULT_SIZE));
////			break;
////		default:
////			this.setMeasuredDimension(widthSpecSize, heightSpecSize);
////			break;
////		}
//	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != 0 && h != 0 && colors != null) {
			Bitmap bmp = generateColorBlock();
			setImageBitmap(bmp);
			invalidate();
		}
	};
	
	Bitmap generateColorBlock(){
		float blockWidth = ImageHlp.changeToSystemUnitFromDP(getContext(), DEFAULT_SIZE)/3f;
		float blockHeight = ImageHlp.changeToSystemUnitFromDP(getContext(), DEFAULT_SIZE)/3f;
		Bitmap output = Bitmap.createBitmap(ImageHlp.changeToSystemUnitFromDP(getContext(), DEFAULT_SIZE),
				ImageHlp.changeToSystemUnitFromDP(getContext(), DEFAULT_SIZE), Config.ARGB_8888);
//		final Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		Canvas canvas = new Canvas(output);
		path.reset();
		canvas.clipPath(path); // makes the clip empty
		rectF.set(0, 0, getWidth(), getHeight());
		path.addRoundRect(rectF, 10, 10, Direction.CCW);
		canvas.clipPath(path, Region.Op.REPLACE);
		for (int i = 0; i < BLOCK_SIZE; i++) {
			int row = i/3;
			int column = i%3;
			canvas.drawRect(blockWidth*column, blockHeight*row, blockWidth*(column+1), blockHeight*(row+1), paints[i]);
		}
		return output;
	}
}

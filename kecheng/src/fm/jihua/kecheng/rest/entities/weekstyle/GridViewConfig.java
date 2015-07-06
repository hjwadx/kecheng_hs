package fm.jihua.kecheng.rest.entities.weekstyle;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import fm.jihua.kecheng.ui.widget.weekview.WeekCanvasUtils;
import fm.jihua.kecheng.ui.widget.weekview.WeekViewParams;

/**
 * @date 2013-7-26
 * @introduce
 */
public class GridViewConfig {

	public String separatorTagShape = "cross";
	public String separatorTagColor = "214,213,208";
	public Cross cross = new Cross();
	public Dashed dashed = new Dashed();

	@Override
	public String toString() {
		return "GridViewConfig [separatorTagShape=" + separatorTagShape + ", separatorTagColor=" + separatorTagColor + ", cross=" + cross + ", dashed=" + dashed + "]";
	}
	
	public void onDrawView(Canvas canvas, WeekViewParams weekViewParams) {
		// 十字
		if ("cross".equals(separatorTagShape)) {
			int half_long = WeekCanvasUtils.getInstance().getSizeFromStyleFile(cross.crossLineLength) / 2;
			Paint paint = WeekCanvasUtils.getInstance().getCutoffCorssPaint(WeekCanvasUtils.getInstance().getColorFromString(separatorTagColor),
					WeekCanvasUtils.getInstance().getSizeFromStyleFile(cross.crossLineWidth));
			for (int i = 1; i < weekViewParams.timeSlot; i++) {
				float currentBottom = (float) (weekViewParams.blockHeight * i) + weekViewParams.offsetTop;
				for (int j = 1; j < weekViewParams.dayCount; j++) {
					float paddingleft = (float) (weekViewParams.blockWidth * j) + weekViewParams.offsetLeft;
					canvas.drawLine(paddingleft - half_long, currentBottom, paddingleft + half_long, currentBottom, paint);
				}
			}

			for (int i = 1; i <= weekViewParams.dayCount; i++) {
				float currentLeft = (float) (weekViewParams.blockWidth * i) + weekViewParams.offsetLeft;
				for (int j = 1; j < weekViewParams.timeSlot; j++) {
					float paddingtop = (float) (weekViewParams.blockHeight * j) + weekViewParams.offsetTop;
					canvas.drawLine(currentLeft, paddingtop - half_long, currentLeft, paddingtop + half_long, paint);
				}
			}
		}
		// 虚线
		else if ("dashed".equals(separatorTagShape)) {
			PathEffect effects = new DashPathEffect(
					new float[] { WeekCanvasUtils.getInstance().getSizeFromStyleFile(dashed.lineLength), WeekCanvasUtils.getInstance().getSizeFromStyleFile(dashed.spaceLength) }, 1);
			Paint paint = WeekCanvasUtils.getInstance().getCutoffDashedPaint(WeekCanvasUtils.getInstance().getColorFromString(separatorTagColor),
					WeekCanvasUtils.getInstance().getSizeFromStyleFile(dashed.lineWidth), effects);
			for (int i = 1; i < weekViewParams.timeSlot; i++) {
				float currentBottom = (float) (weekViewParams.blockHeight * i) + weekViewParams.offsetTop;
				Path path = new Path();
				path.moveTo(weekViewParams.offsetLeft, currentBottom);
				path.lineTo((float) (weekViewParams.offsetLeft + weekViewParams.blockWidth * weekViewParams.dayCount), currentBottom);
				canvas.drawPath(path, paint);
			}

			for (int i = 1; i <= weekViewParams.dayCount; i++) {
				float currentLeft = (float) (weekViewParams.blockWidth * i) + weekViewParams.offsetLeft;
				Path path = new Path();
				path.moveTo(currentLeft, (float) weekViewParams.offsetTop);
				path.lineTo(currentLeft, (float) (weekViewParams.blockHeight * weekViewParams.timeSlot + weekViewParams.offsetTop));
				canvas.drawPath(path, paint);
			}
		}
	}
}

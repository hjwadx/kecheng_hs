package fm.jihua.kecheng.test.weekview;

import java.util.List;

import junit.framework.TestCase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.sticker.Sticker;
import fm.jihua.kecheng.ui.widget.weekview.WeekCanvasUtils;
import fm.jihua.kecheng.ui.widget.weekview.WeekViewParams;

/**
 * @date 2013-8-20
 * @introduce
 */
public class WeekViewParamsTest extends TestCase {
	WeekViewParams weekViewParams;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		weekViewParams = new WeekViewParams(485, 10);
	}

	public void testBlockHaveDeviation() {
		weekViewParams = new WeekViewParams(480, 10);
		Rect rect = weekViewParams.getWeekViewRect(4, 1, 5, 2);
		assertEquals(480, rect.right);

		weekViewParams = new WeekViewParams(320, 10);
		Rect rect2 = weekViewParams.getWeekViewRect(4, 1, 5, 2);
		assertEquals(320, rect2.right);

		weekViewParams = new WeekViewParams(800, 10);
		Rect rect3 = weekViewParams.getWeekViewRect(4, 1, 5, 2);
		assertEquals(800, rect3.right);

		weekViewParams = new WeekViewParams(240, 10);
		Rect rect4 = weekViewParams.getWeekViewRect(4, 1, 5, 2);
		assertEquals(240, rect4.right);

	}

	public void testBlockParams() {

		Point point = new Point(0, 0);
		Rect rect2 = WeekCanvasUtils.getInstance().getPasterRect(point, weekViewParams);
		int correctLeft = weekViewParams.offsetLeft;
		int correctTop = weekViewParams.offsetTop;
		int correctRight = (int) (weekViewParams.offsetLeft + weekViewParams.blockWidth);
		int correctBottom = (int) (weekViewParams.offsetLeft + weekViewParams.blockHeight);
		assertEquals(correctLeft, rect2.left);
		assertEquals(correctTop, rect2.top);
		assertEquals(correctRight, rect2.right);
		assertEquals(correctBottom, rect2.bottom);

		CourseBlock block = new CourseBlock();
		block.start_slot = 1;
		block.end_slot = 2;
		block.day_of_week = 2;
		Rect rect3 = WeekCanvasUtils.getInstance().getCourseRect(block, weekViewParams);
		int correctLeft1 = (int) (weekViewParams.offsetLeft + weekViewParams.blockWidth);
		int correctTop1 = weekViewParams.offsetTop;
		int correctRight1 = (int) (weekViewParams.offsetLeft + weekViewParams.blockWidth * 2);
		int correctBottom1 = (int) (weekViewParams.offsetTop + weekViewParams.blockHeight * 2);
		assertEquals(correctLeft1, rect3.left);
		assertEquals(correctTop1, rect3.top);
		assertEquals(correctRight1, rect3.right);
		assertEquals(correctBottom1, rect3.bottom);

		Sticker paster = new Sticker("tt13", 2, 2);
		paster.setPoint(2, 1);
		Rect rect4 = WeekCanvasUtils.getInstance().getPasterRect(paster, weekViewParams);
		assertEquals((int) (weekViewParams.offsetLeft + weekViewParams.blockWidth * 2), rect4.left);
	}

	public void testGetColorFromString() {
		assertEquals(0, WeekCanvasUtils.getInstance().getColorFromString(""));
		assertEquals(0, WeekCanvasUtils.getInstance().getColorFromString("255"));
		assertEquals(0, WeekCanvasUtils.getInstance().getColorFromString("255,255"));
		assertEquals(Color.rgb(122, 122, 122), WeekCanvasUtils.getInstance().getColorFromString("122,122,122"));
		assertEquals(Color.argb(0, 255, 255, 255), WeekCanvasUtils.getInstance().getColorFromString("255,255,255,0"));
		assertEquals(0, WeekCanvasUtils.getInstance().getColorFromString("255,255,255,255,255"));
		assertEquals(0, WeekCanvasUtils.getInstance().getColorFromString("dsadsadsadas.dwa,.dwadaw"));
	}

	public void testPasteGetList() {
		Sticker paster = new Sticker("tt13", 2, 2);
		paster.setPoint(2, 1);
		List<Point> pointList = paster.getPointList();
		assertEquals(true, pointList.contains(new Point(2, 1)));
		assertEquals(true, pointList.contains(new Point(2, 2)));
		assertEquals(true, pointList.contains(new Point(3, 1)));
		assertEquals(true, pointList.contains(new Point(3, 2)));
	}

}

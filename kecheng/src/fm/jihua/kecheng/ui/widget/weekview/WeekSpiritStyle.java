package fm.jihua.kecheng.ui.widget.weekview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;

import com.mozillaonline.providers.DownloadManager;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.weekstyle.CourseIndicatorConfig;
import fm.jihua.kecheng.rest.entities.weekstyle.DayIndicatorConfig;
import fm.jihua.kecheng.rest.entities.weekstyle.GridViewConfig;
import fm.jihua.kecheng.rest.entities.weekstyle.LeftTopCornerConfig;
import fm.jihua.kecheng.rest.entities.weekstyle.Theme;
import fm.jihua.kecheng.rest.service.RestService;
import fm.jihua.kecheng.ui.activity.mall.DownloadSpirit;
import fm.jihua.kecheng.ui.activity.mall.DownloadSpirit.OnDownloadListener;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CustomScrollView;
import fm.jihua.kecheng.ui.widget.GraduationView;
import fm.jihua.kecheng.ui.widget.WeekView;
import fm.jihua.kecheng.utils.FileUtils;
import fm.jihua.kecheng.utils.ImageHlp;

/**
 * @date 2013-7-25
 * @introduce WeekView的绘制
 */
public class WeekSpiritStyle {

	WeekView weekView;
	public Theme theme;
	GraduationView graduationView;
	CustomScrollView scrollView;
	WeekViewParams mWeekViewParams;
	String styleName;
	int productId;
	
	List<CourseBlock> courseBlocks;
	HashMap<Point, List<CourseBlock>> mapPointCourses = new HashMap<Point, List<CourseBlock>>();
	Map<String, Paint> coursePaintsMap = new HashMap<String, Paint>();
	Map<String, Integer> courseColorMap = new HashMap<String, Integer>();
	
	public void resetData(WeekView weekView, String styleName, int productId, WeekViewParams weekViewParams, List<CourseBlock> courseBlocks){
		this.weekView = weekView;
		this.courseBlocks = courseBlocks;
		this.mWeekViewParams = weekViewParams;
		this.styleName = styleName;
		this.productId = productId;
		resetInfo(false);
	}
	
	private void resetInfo(boolean refreshTheme){
		Theme weekStyleBeanByName = null;
		if(weekView.isMyselfView){
			weekStyleBeanByName = Theme.getThemeByName(styleName);
		}else{
			weekStyleBeanByName = Theme.getThemeByNameForAllFolder(productId);
		}
		if(weekStyleBeanByName == null){
			if (!FileUtils.getInstance().isSDAvailable()) {
				Hint.showTipsShort(weekView.getContext(), R.string.week_no_sd_card_notice);
			}
			weekStyleBeanByName = Theme.getDefaultTheme();
			if (!weekView.isMyselfView && !refreshTheme) {
				if (!TextUtils.isEmpty(styleName) && !Theme.isDefaultTheme(styleName)) {
					downloadThemeZip(styleName);
				}
			}
		}
		
		this.theme = weekStyleBeanByName;
		
		coursePaintsMap.clear();
		
		initPointCoursesMap(this.courseBlocks, mapPointCourses);
		
		//check stylename
		String oldStyleName = (String) weekView.getTag();
		String newStyleName = theme.name;
		if (TextUtils.isEmpty(oldStyleName) || !ObjectUtils.nullSafeEquals(oldStyleName, newStyleName)) {
			invalidateSizeAndBackViews();
		}
		weekView.setTag(newStyleName);
	}

	public void setWeekStyleBean(Theme weekStyleBean) {
		this.theme = weekStyleBean;
		if(this.theme == null){
			this.theme = Theme.getDefaultTheme();
		}
		coursePaintsMap.clear();
		courseColorMap.clear();
		invalidateSizeAndBackViews();
		weekView.invalidate();
	}
	
	public void draw(Canvas canvas) {
		drawCutOffRule(canvas);
		drawCourseBlocks(canvas, courseBlocks);
	}
	
	public Theme getWeekStyleBean(){
		return this.theme;
	}
	
	public void resetWeekViewParams(WeekViewParams weekViewParams){
		this.mWeekViewParams = weekViewParams;
		if(theme != null){
			invalidateSizeAndBackViews();
		}
	}
	
	public WeekViewParams getWeekViewParams(){
		return this.mWeekViewParams;
	}
	
	public void initSpecialView(CustomScrollView scrollView, GraduationView graduationView) {
		this.scrollView = scrollView;
		this.graduationView = graduationView;
		addScrollViewListener();
	}
	
	void addScrollViewListener(){
		if (this.scrollView != null && this.graduationView != null) {
			final OnScrollChangedListener listener = new OnScrollChangedListener() {

				@Override
				public void onScrollChanged() {
					graduationView.scrollTo(scrollView.getScrollX(), scrollView.getScrollY());
				}
			};
			scrollView.getViewTreeObserver().addOnScrollChangedListener(listener);
			//			scrollView.getViewTreeObserver().removeOnScrollChangedListener(listener);
			//For prevent addOnScrollChangedListener failed
			scrollView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					scrollView.getViewTreeObserver().addOnScrollChangedListener(listener);
					return false;
				}
			});
		}
	}
	
	public boolean onTouchUp(Point pt_touchdown){
		List<CourseBlock> courseBlocks = getCourseBlocksByActualXY(pt_touchdown);
		if (courseBlocks != null && courseBlocks.size() > 0) {
			if (courseBlocks.size() > 1) {
				weekView.childClickListener.onMultiCourseClick(courseBlocks);
			} else {
				weekView.childClickListener.onCourseClick(courseBlocks.get(0));
			}
			return true;
		} 
		return false;
	}
	
	public void invalidateSizeAndBackViews(){
		graduationView.invalidateSizeView(false, this);
		setBackground(scrollView);
	}
	
	public void invalidateGraduationViewForPaste(boolean showShadow){
		graduationView.invalidateSizeView(showShadow, this);
	}
	
	public CustomScrollView getScrollView(){
		return scrollView;
	}
	
	private int getScrollTop(){
		return scrollView == null ? 0 : scrollView.getScrollY();
	}
	
	private int getScrollLeft() {
		return scrollView == null ? 0 : scrollView.getScrollX();
	}
	
	public void drawCourseBlocks(Canvas canvas, List<CourseBlock> courseBlocks) {
		for (CourseBlock courseBlock : courseBlocks) {
			drawCourseBlock(canvas, courseBlock, isTopCourse(courseBlock, mapPointCourses));
		}
	}
	
	public void drawCourseBlock(Canvas canvas, CourseBlock courseBlock, boolean isTucked) {
		Rect rect = WeekCanvasUtils.getInstance().getCourseRect(courseBlock,mWeekViewParams);
		Rect clipBounds = canvas.getClipBounds();
		if (rect.left > clipBounds.right || rect.right < clipBounds.left || rect.top > clipBounds.bottom || rect.bottom < clipBounds.top) {
			return;
		}
		Paint paint;
		if (coursePaintsMap.containsKey(courseBlock.name)) {
			paint = coursePaintsMap.get(courseBlock.name);
		} else {
			int color;
			if (courseColorMap.containsKey(courseBlock.name)) {
				color = courseColorMap.get(courseBlock.name);
			}else {
				if(courseBlock.event == null){
					color = WeekCanvasUtils.getInstance().getColorFromString(theme.timeGridConfig.gridColors[coursePaintsMap.size() % theme.timeGridConfig.gridColors.length]);
				} else {
					color = WeekCanvasUtils.getInstance().getColorFromString(theme.timeGridConfig.eventColor);
				}
				courseColorMap.put(courseBlock.name, color);
			} 
			paint = new Paint();
			paint.setColor(color);
			// paint.setAntiAlias(true);
			// paint.setXfermode(new PorterDuffXfermode(Mode.ADD));
			coursePaintsMap.put(courseBlock.name, paint);
		}
		
		//如果不在当前学期，就设置透明
		int color = paint.getColor();
		if (!courseBlock.active) {
			//20% alpha
			color = color & 0x33FFFFFF;
		}else{
			color = color | 0xFF000000;
		}
		paint.setColor(color);
		courseBlock.backColor = color;
		
		int topLineRightPadding = rect.right;
		if (isTucked) {
			drawTucked(canvas, rect, paint);
			topLineRightPadding = rect.right - mWeekViewParams.tukedWidth;
		}else {
			canvas.drawRect(rect, paint);
		}
		
		if(courseBlock.event != null){
			int half_icon_w = ImageHlp.changeToSystemUnitFromDP(weekView.getContext(), 7);
			Rect rect2 = new Rect((rect.left + rect.right)/2 - half_icon_w, rect.top + mWeekViewParams.coursePadding, (rect.left + rect.right)/2 + half_icon_w, rect.top + half_icon_w * 2 + mWeekViewParams.coursePadding);
			Drawable drawable = App.getInstance().getResources().getDrawable(R.drawable.class_icon_activity);
			drawable.setBounds(rect2);
//			if(!courseBlock.active){
//				drawable.setAlpha(51);
//			} else {
//				drawable.setAlpha(255);
//			}
			drawable.draw(canvas);
		}
		
		drawCourseText(canvas, courseBlock);
		
		Paint paintCourseTopLine = WeekCanvasUtils.getInstance().getPaintCourseTopLine();
		Paint paintCourseBottomLine = WeekCanvasUtils.getInstance().getPaintCourseBottomLine();
		//top bottom line
		canvas.drawRect(rect.left, rect.top, topLineRightPadding, rect.top + mWeekViewParams.courseHintLineHeight, paintCourseTopLine);
		canvas.drawRect(rect.left, rect.bottom - mWeekViewParams.courseHintLineHeight, rect.right, rect.bottom, paintCourseBottomLine);
	}
	
	private void drawTucked(Canvas canvas, Rect rect, Paint bgPaint) {
		int tuck = mWeekViewParams.tukedWidth;
		Path path = new Path();
//		canvas.save();
		path.moveTo(rect.left, rect.top);
		path.lineTo(rect.right - tuck, rect.top);
		path.lineTo(rect.right, rect.top + tuck);
		path.lineTo(rect.right, rect.bottom);
		path.lineTo(rect.left, rect.bottom);
		path.lineTo(rect.left, rect.top);
		canvas.drawPath(path, bgPaint);

		Path wPath = new Path();
		wPath.moveTo(rect.right - tuck, rect.top);
		wPath.lineTo(rect.right - tuck, rect.top + tuck);
		wPath.lineTo(rect.right, rect.top + tuck);
		wPath.lineTo(rect.right - tuck, rect.top);
		Paint paint = new Paint();
		paint.setARGB(64, 255, 255, 255);
		canvas.drawPath(wPath, paint);

		Path bPath = new Path();
		bPath.moveTo(rect.right - tuck, rect.top + tuck);
		bPath.lineTo(rect.right, rect.top + tuck);
		bPath.lineTo(rect.right, rect.top + tuck * 2);
		bPath.lineTo(rect.right - tuck, rect.top + tuck);
		Paint paint2 = new Paint();
		paint2.setARGB(39, 0, 0, 0);
		canvas.drawPath(bPath, paint2);
//		canvas.restore();
	}
	
	private void drawCourseText(Canvas canvas, CourseBlock courseBlock){
		
		Paint paintCourseText = WeekCanvasUtils.getInstance().getPaintCourseText();
		
		int lineCount = 0;
		if(courseBlock.event != null){
			lineCount = 1;
		}
		String content = courseBlock.name + (TextUtils.isEmpty(courseBlock.room) ? "" : " @" + courseBlock.room);
        // 已绘的宽度  
        float drawedWidth = 0;  
        float charWidth;  
        Rect rect = WeekCanvasUtils.getInstance().getCourseRect(courseBlock,mWeekViewParams);
        int color = Color.WHITE;
        if (!courseBlock.active) {
//			color = color & 0x7FFFFFFF;
			content = "[非本周]"+content;
		}
        paintCourseText.setColor(color);
        char[] textCharArray = content.toCharArray();  
        for (int i = 0; i < textCharArray.length; i++) {  
            charWidth = paintCourseText.measureText(textCharArray, i, 1);  
              
            if(textCharArray[i]=='\n'){
                lineCount++;  
                drawedWidth = 0;  
                continue;  
            }
            //coursePadding * 2 左右两边的边距
            if (drawedWidth + charWidth > mWeekViewParams.blockWidth - mWeekViewParams.coursePadding * 2) {  
                lineCount++;  
                drawedWidth = 0;  
            }
            if(mWeekViewParams.coursePadding + rect.top + (lineCount + 2) * paintCourseText.getTextSize() > rect.bottom - mWeekViewParams.coursePadding){
            	break;
            }
			canvas.drawText(textCharArray, i, 1, mWeekViewParams.coursePadding + rect.left + drawedWidth, mWeekViewParams.coursePadding + rect.top + (lineCount + 1) * (paintCourseText.getTextSize() + mWeekViewParams.courseSpacing), paintCourseText);
            drawedWidth += charWidth;  
        }  
	}

	/**
	 * 绘制分割线
	 * 
	 * @param canvas
	 */
	public void drawCutOffRule(Canvas canvas) {
		GridViewConfig gridViewConfig = theme.gridViewConfig;
		gridViewConfig.onDrawView(canvas, mWeekViewParams);
	}

//	public int getWeekViewCornerColor() {
//		return weekViewParams.utilsData.getColorFromString(weekStyleBean.leftTopCornerConfig.color);
//	}
 
	public void setBackground(CustomScrollView scrollView) {
		Bitmap bitmap = null; 
		if (theme.category == Theme.CATEGORY_FILE) {
			if ("color".equals(theme.gridBackgroundConfig.backgroundType)) {
				scrollView.setBackgroundColor(WeekCanvasUtils.getInstance().getColorFromString(theme.gridBackgroundConfig.backgroundColor));
			} else {
				String fileName = FileUtils.getInstance().addPngSuffix(theme.getLocalStoreDir() + theme.gridBackgroundConfig.backgroundImage);
				
				Drawable drawable = ImageHlp.getImageFromFileShrinked(scrollView.getContext(), fileName);
				Compatibility.setBackground(scrollView, drawable);
			}
		} else if (theme.category == Theme.CATEGORY_CUSTOM ) {
			if(weekView.isMyselfView){
				bitmap = App.getInstance().getSpecialBgBitmap(scrollView.getWidth(), scrollView.getHeight());
				if (bitmap != null) {
					BitmapDrawable bitmapDrawable = new BitmapDrawable(scrollView.getResources(), bitmap);
					Compatibility.setBackground(scrollView, bitmapDrawable);
				} else {
					scrollView.setBackgroundColor(WeekCanvasUtils.getInstance().getColorFromString(theme.gridBackgroundConfig.backgroundColor));
				}
			}else{
				scrollView.setBackgroundColor(WeekCanvasUtils.getInstance().getColorFromString(theme.gridBackgroundConfig.backgroundColor));
			}
			
		}
	}

	public Bitmap getTopViewBitmap(boolean haveShadowForPaste) {
		DayIndicatorConfig dayIndicatorConfig = theme.dayIndicatorConfig;
		return dayIndicatorConfig.onDrawView(mWeekViewParams, haveShadowForPaste, weekView);
	}

	public Bitmap getLeftViewBitmap(boolean haveShadowForPaste) {
		CourseIndicatorConfig courseIndicatorConfig = theme.courseIndicatorConfig;
		return courseIndicatorConfig.onDrawView(mWeekViewParams, haveShadowForPaste);
	}
	
	public Bitmap getTopLeftBitmap(boolean haveShadowForPaste){
		LeftTopCornerConfig leftTopCornerConfig = theme.leftTopCornerConfig;
		return leftTopCornerConfig.onDrawView(mWeekViewParams, haveShadowForPaste);
	}
	
	public void initTuckedView(List<View> allViews, HashMap<Point, List<View>> mapPointViews) {
		for (View v : allViews) {
			WeekCanvasUtils.getInstance().setShadowForView(v);
			CourseBlock block = (CourseBlock) v.getTag();
			for (int j = block.start_slot; j <= block.end_slot; j++) {
				Point pt = new Point(block.getWeekIndex(), j - 1);
				List<View> linkedViews = mapPointViews.get(pt);
				if (linkedViews != null && linkedViews.size() > 1 && linkedViews.get(linkedViews.size() - 1).equals(v)) {
					WeekCanvasUtils.getInstance().setViewTucked(weekView.getContext(), v);
					break;
				}
			}
		}
	}

	public Map<Point, List<CourseBlock>> getPointCourses(){
		return this.mapPointCourses;
	}
	
	public List<CourseBlock> getCourseBlocksByActualXY(Point pt){
		Point point = mWeekViewParams.changeActualXYToSlotXY(pt);
		if (point != null) {
//			return mapPointCourses.get(point);
			return getOverlapCourseBlocks(mapPointCourses.get(point), point,mapPointCourses);
		}else
			return new ArrayList<CourseBlock>();
	}
	
	public List<Rect> getCoursesRectsInParentCoordinate(){
		List<Rect> rects = new LinkedList<Rect>();
		for (CourseBlock courseBlock : courseBlocks) {
			Rect rect = WeekCanvasUtils.getInstance().getCourseRect(courseBlock,mWeekViewParams);
			rect.left -= getScrollLeft();
			rect.top -= getScrollTop();
			rects.add(rect);
		}
		return rects;
	}
	
	public List<CourseBlock> getCoursesBlocks(){
		return courseBlocks;
	}
	
	public WeekView getWeekView(){
		return weekView;
	}
	
	private boolean isTopCourse(CourseBlock courseBlock,HashMap<Point, List<CourseBlock>> mapPointCourses){
		for (int j = courseBlock.start_slot; j <= courseBlock.end_slot; j++) {
			Point pt = new Point(courseBlock.getWeekIndex(), j - 1);
			List<CourseBlock> linkedViews = mapPointCourses.get(pt);
			if (linkedViews != null && linkedViews.size() > 1 && linkedViews.get(linkedViews.size() - 1).equals(courseBlock)) {
				return true;
			}
		}
		return false;
	}
	
	private void initPointCoursesMap(List<CourseBlock> courses, HashMap<Point, List<CourseBlock>> mapPointCourses) {
		mapPointCourses.clear();
		for (int i = 0; i < courses.size(); i++) {
			CourseBlock block = courses.get(i);
			if (block.start_slot > 0) {
				addCourseBlockToPointMap(block, mapPointCourses);
			}
		}
	}

	private void addCourseBlockToPointMap(CourseBlock block, HashMap<Point, List<CourseBlock>> mapPointCourses) {
		for (int indexY = block.start_slot; indexY <= block.end_slot; indexY++) {
			Point pt = new Point(block.getWeekIndex(), indexY - 1);
			List<CourseBlock> linkedBlocks = mapPointCourses.get(pt);
			if (linkedBlocks == null) {
				linkedBlocks = new LinkedList<CourseBlock>();
			}
			linkedBlocks.add(block);
			mapPointCourses.put(pt, linkedBlocks);
		}
	}
	
	private List<CourseBlock> getOverlapCourseBlocks(List<CourseBlock> blocks, Point pt, HashMap<Point, List<CourseBlock>> mapPointCourses){
		if(blocks == null){
			return null;
		}
		List<CourseBlock> courseBlocks = new ArrayList<CourseBlock>();
		CourseBlock block = blocks.get(blocks.size() - 1);
		for(int i = block.start_slot - 1; i < block.end_slot; i++){
			Point point = new Point(pt.x, i);
			for(CourseBlock block2 : mapPointCourses.get(point)){
				if(!courseBlocks.contains(block2)){
					courseBlocks.add(block2);
				}
			}
		}
		return courseBlocks;
	}
	
	private void downloadThemeZip(String styleName){
		UIUtil.block(weekView.activity, "主题载入中..");
		String styleUrl = RestService.get().getDownloadThemeUrlFromName(styleName);
		DownloadSpirit downloadSpirit = new DownloadSpirit(RestService.get().getAuthUrl(styleUrl), Product.PRODUCTS_FOLDER_PATH, productId);
		downloadSpirit.start(new OnDownloadListener() {

			@Override
			public void statusChanged(int statusType, int precent, int current, int total) {
				if(statusType == DownloadManager.STATUS_SUCCESSFUL || statusType == DownloadManager.STATUS_FAILED){
					UIUtil.unblock(weekView.activity);
				}
				if(statusType == DownloadSpirit.STATUS_COMPLETED){
					resetInfo(true);
				}
			}
			
		});
	}
	
}

package fm.jihua.kecheng.ui.activity.tutorial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.ui.activity.BaseMenuActivity;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialView.TrigerCallback;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialView.TrigerEvent;
import fm.jihua.kecheng.ui.activity.tutorial.TutorialView.TutorialParams;
import fm.jihua.kecheng.ui.view.KechengActionbar;
import fm.jihua.kecheng.ui.widget.CustomScrollView;
import fm.jihua.kecheng.ui.widget.WeekView;
import fm.jihua.kecheng.ui.widget.weekview.WeekCanvasUtils;
import fm.jihua.kecheng.ui.widget.weekview.WeekSpiritStyle;
import fm.jihua.kecheng.ui.widget.weekview.WeekViewParams;
import fm.jihua.kecheng.utils.AnimationUtils;
import fm.jihua.kecheng.utils.ImageHlp;

public class WeekViewTutorialUtils extends TutorialManager implements TrigerCallback {
	WeekSpiritStyle spiritStyle;
	WeekView weekView;
	CustomScrollView scrollView;
	View menuView;
	List<Rect> rects;
	
	public final static String NORMAL_TUTORIAL = "NORMAL_TUTORIAL";
	public final static String NOT_CURRENT_WEEK_TUTORIAL = "NOT_CURRENT_WEEK_TUTORIAL";
	public final static String CONFLICT_COURSES_TUTORIAL = "CONFLICT_COURSES_TUTORIAL";
	public final static String SHOW_MORE_TUTORIAL = "SHOW_MORE_TUTORIAL";
	
	public final static int MAX_CONFLICT_COURSES_TUTORIALS_STEPS = 4;
	
	private final int FINISH_SCROLL_STEP = 100;
//	private final int VIEW_COURSE = 105;
	
	private final int CLICK_ON_CONFLICT_COURSES = 110;
	private int tutorialViewHeight;
	private int titlebarHeight;
	private int sparkAnimationImageSize;
	private int arrowAnimationImageWidth;
	private int arrowAnimationImageHeight;
	
	class MyTutorialSet extends TutorialSet{

		MyTutorialSet(String tutorialSetName) {
			super(tutorialSetName);
		}
		
		@Override
		public void finishTutorial() {
			super.finishTutorial();
			finishCurrentStep(); //add one more
		}
	}
	
	private WeekViewTutorialUtils(Context context) {
		super(context);
//		clearTutorials();
	}
	
	public WeekViewTutorialUtils(final Context context, final WeekSpiritStyle spiritStyle) {
		this(context);
		this.spiritStyle = spiritStyle;
		scrollView = ((CustomScrollView)activity.findViewById(R.id.weekview_parent));
		weekView = spiritStyle.getWeekView();
		menuView = activity.findViewById(R.id.toggle_menu);
		sparkAnimationImageSize = context.getResources().getDrawable(R.drawable.tutorial_spark).getIntrinsicWidth();
		Drawable drawableArrow = context.getResources().getDrawable(R.drawable.tutorial_arrow);
		arrowAnimationImageWidth = drawableArrow.getIntrinsicWidth();
		arrowAnimationImageHeight = drawableArrow.getIntrinsicHeight();
		final KechengActionbar actionbar = ((BaseMenuActivity)activity).getKechengActionBar();
		actionbar.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if (rects == null) {
					rects = spiritStyle.getCoursesRectsInParentCoordinate();
					titlebarHeight = actionbar.getHeight();
					tutorialViewHeight = screenHeight - getStatusBarHeight();
					for (Rect rect : rects) {
						changeToNewRect(rect);
					}
					initTutorialsSets();
					startTutorial();
				}
			}
		});
	}
	
	void changeToNewRect(Rect rect){
		rect.top += titlebarHeight;
		rect.bottom += titlebarHeight;
	}
	
	@Override
	protected TutorialParams transformParams(TutorialParams params) {
		if (params.notCoverRect != null) {
			int dx = 0;
			int dy = 0;
			WeekViewParams weekViewParams = spiritStyle.getWeekViewParams();
			
			//计算是否需要滚动
			if (params.notCoverRect.right > screenWidth) {
				dx = weekView.getRight()-screenWidth;
				scrollView.scrollBy(dx, 0);
			}
			if (params.notCoverRect.bottom > tutorialViewHeight) {
				dy = weekView.getBottom() - tutorialViewHeight + titlebarHeight;
				int topMargin = params.notCoverRect.top - weekViewParams.offsetTop;
				dy = Math.min(dy, topMargin);
				scrollView.scrollBy(0, dy);
			}
			if (dx != 0 || dy != 0) {
				params.applyOffset(-dx, -dy);
			}
			
			if (params.coverRects != null) {
				for (Rect rect : params.coverRects) {
					Rect leftRect = new Rect(0, 0, weekViewParams.offsetLeft, tutorialViewHeight);
					Rect topRect = new Rect(0, 0, screenWidth, weekViewParams.offsetTop+titlebarHeight);
					if (intersects(rect, leftRect) && rect.right >= weekViewParams.offsetLeft) {
						rect.left = weekViewParams.offsetLeft;
					}
					if (intersects(rect, topRect) && rect.bottom >= weekViewParams.offsetTop+titlebarHeight) {
						rect.top = weekViewParams.offsetTop+titlebarHeight;
					}
//					rect.top = Math.max(rect.top, weekViewParams.offsetTop+titlebarHeight);
				}
			}
			
			int minMargin = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 20);
			Rect noticeRect;
			if (params.noticePosition.y < 0) {
				noticeRect = new Rect(0, tutorialViewHeight + params.noticePosition.y - params.noticeHeight, screenWidth, tutorialViewHeight + params.noticePosition.y);
			}else {
				noticeRect = new Rect(0, params.noticePosition.y, screenWidth, params.noticePosition.y+params.noticeHeight);
			}
			int belowMargin = params.notCoverRect.top - noticeRect.bottom;
//			int topMargin =  noticeRect.top - params.notCoverRect.bottom;
			//设置提示框的位置
			if (noticeRect.intersects(params.notCoverRect.left, params.notCoverRect.top, params.notCoverRect.right, params.notCoverRect.bottom)) {
				params.noticePosition.y = Math.max(params.notCoverRect.top - minMargin - params.noticeHeight, minMargin);
			}else if (belowMargin > 0 && belowMargin < minMargin) {
				params.noticePosition.y = noticeRect.top - minMargin;
			}
			
			int height = menuView.getHeight();
			Rect menuRect = new Rect(screenWidth/2-height, tutorialViewHeight-height, screenWidth/2+height, tutorialViewHeight);
			if (!SHOW_MORE_TUTORIAL.equals(params.category) && intersects(params.notCoverRect, menuRect)) {
				menuView.setVisibility(View.INVISIBLE);
			}else {
				menuView.setVisibility(View.VISIBLE);
			}
			
			if (params.animateImage == R.drawable.tutorial_arrow && params.animationPosition.x > screenWidth) {
				params.animationPosition.x = params.notCoverRect.left - arrowAnimationImageWidth;
				params.animateRotate = 180;
				Animation animation = AnimationUtils.getInstance().createTranslateAnim(-0.5f, 0, 0, 0);
				animation.setRepeatCount(1000);
				animation.setDuration(1000);
				params.animation = animation;
			}
			tutorialView.setTutorialParams(params);
		}
		return params;
	}
	
	void initTutorialsSets(){
		TutorialSet tutorialsSet = new TutorialSet(NORMAL_TUTORIAL); tutorialsSet.setData(getCommonTutorials(spiritStyle, rects));
		mapTutorialSets.put(tutorialsSet.tutorialSetName, tutorialsSet);
		CourseBlock noCurrentWeekCourse = getNotCurrentWeekCourse(spiritStyle.getCoursesBlocks());
		List<CourseBlock> conflictCourses = getConflictCourses();
//		if (noCurrentWeekCourse != null) {
//			tutorialsSet = new TutorialSet(NOT_CURRENT_WEEK_TUTORIAL); tutorialsSet.setData(getNotCurrentWeekTutorials(noCurrentWeekCourse));
//			mapTutorialSets.put(tutorialsSet.tutorialSetName, tutorialsSet);
//		}
		if (conflictCourses != null) {
			tutorialsSet = new MyTutorialSet(CONFLICT_COURSES_TUTORIAL); tutorialsSet.setData(getConflictCoursesTutorials(conflictCourses));
			mapTutorialSets.put(tutorialsSet.tutorialSetName, tutorialsSet);
		}
		tutorialsSet = new TutorialSet(SHOW_MORE_TUTORIAL); tutorialsSet.setData(getShowMoreTutorials());
		mapTutorialSets.put(tutorialsSet.tutorialSetName, tutorialsSet);
	}
	
	protected ViewGroup getParentView(){
		return (ViewGroup) activity.getWindow().getDecorView().findViewById(R.id.slidingmenumain);
	}
	
	private CourseBlock getNotCurrentWeekCourse(List<CourseBlock> courseBlocks){
		for (CourseBlock courseBlock : courseBlocks) {
			if (!courseBlock.active) {
				return courseBlock;
			}
		}
		return null;
	}
	
	private List<CourseBlock> getConflictCourses(){
		Map<Point, List<CourseBlock>> map = spiritStyle.getPointCourses();
		for (List<CourseBlock> courseBlocks : map.values()) {
			if (courseBlocks.size() > 1) {
				return courseBlocks;
			}
		}
		return null;
	}
	
	private List<TutorialParams> getCommonTutorials(WeekSpiritStyle controller, List<Rect> rects){
		List<TutorialParams> list = new LinkedList<TutorialParams>();
		TutorialParams tutorialParams = new TutorialParams(NORMAL_TUTORIAL, "同学你好！我是坨坨", this); list.add(tutorialParams);//1
		
		tutorialParams = new TutorialParams(NORMAL_TUTORIAL, "课表完成了！这是好的开始哦！", this); list.add(tutorialParams);//2
		
		tutorialParams = new TutorialParams(NORMAL_TUTORIAL, "下面，让坨坨告诉你一些使用技巧", this); list.add(tutorialParams);//3
		
//		tutorialParams = new TutorialParams(NORMAL_TUTORIAL, "最上方是当前周数，会自动更新", this, R.drawable.tutorial_spark, 
//				AnimationUtils.getInstance().createFadeAnim(), 
//				new Point(screenWidth/2-ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 50), ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 5))
//		);
//		tutorialParams.noticeLayout = R.layout.view_tutorial_notice_image_right;
//		list.add(tutorialParams);//4
//		
//		SpannableString text = new SpannableString("如果周数有误，可以在设置[image]中进行修改");
//		ImageSpan is = new ImageSpan(App.getInstance(), R.drawable.menu_icon_setting);
//		text.setSpan(is, 12, 12+7, 0);
//		tutorialParams = new TutorialParams(NORMAL_TUTORIAL, text, this, R.drawable.tutorial_spark, 
//				AnimationUtils.getInstance().createFadeAnim(), 
//				new Point(screenWidth/2-ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 50), ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 5))
//		);
//		tutorialParams.noticeLayout = R.layout.view_tutorial_notice_image_right;
//		list.add(tutorialParams);//5
		
		Point pt = new Point(screenWidth/2+ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 50), ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 200));
		Animation animation = AnimationUtils.getInstance().createTranslateAnim(0, -3, 0, 0);
		animation.setRepeatCount(1000);
		animation.setDuration(2000);
		tutorialParams = new TutorialParams(NORMAL_TUTORIAL, "向左滑动课表，可查看周末", this, R.drawable.tutorial_arrow, 
				animation, pt 
		);
		tutorialParams.noticeImage = R.drawable.tutorial_tuotuo_answer_left;
		tutorialParams.trigerEvent = TrigerEvent.FLING_TO_RIGHT;
		list.add(tutorialParams);//6
		
		tutorialParams = new TutorialParams(NORMAL_TUTORIAL, "是的，就是这样", this);
		tutorialParams.noticeImage = R.drawable.tutorial_tuotuo_answer_left;
		tutorialParams.id = FINISH_SCROLL_STEP;
		list.add(tutorialParams);//7
		
//		Rect firstCourseRect = null;
//		for (CourseBlock courseBlock : controller.getCoursesBlocks()) {
//			if (courseBlock.active) {
//				firstCourseRect = controller.getCourseRect(courseBlock);
//				changeToNewRect(firstCourseRect);
//				break;
//			}
//		}
//		if (firstCourseRect == null) {
//			firstCourseRect = rects.get(0);
//		}
//		pt = new Point(firstCourseRect.right + ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 20), firstCourseRect.centerY()-sparkAnimationImageSize/2);
//		animation = AnimationUtils.getInstance().createTranslateAnim(0, -0.5f, 0, 0);
//		animation.setRepeatCount(1000);
//		animation.setDuration(1000);
//		tutorialParams = new TutorialParams(NORMAL_TUTORIAL, "点击课程进入具体的一节课", this, R.drawable.tutorial_arrow, 
//				animation, pt 
//		);
//		tutorialParams.notCoverRect = firstCourseRect;
//		tutorialParams.noticeLayout = R.layout.view_tutorial_notice_image_right;
//		tutorialParams.noticeImage = R.drawable.tutorial_tuotuo_answer_right;
//		tutorialParams.clickRegion = firstCourseRect;
//		tutorialParams.id = VIEW_COURSE;
//		if (rects.size() > 1) {
//			tutorialParams.coverRects = getOtherRects(firstCourseRect);
//		}
//		list.add(tutorialParams);//8
		return list;
	}
	
	private List<TutorialParams> getNotCurrentWeekTutorials(CourseBlock noCurrentWeekCourse){
		List<TutorialParams> list = new LinkedList<TutorialParams>();
		String category = NOT_CURRENT_WEEK_TUTORIAL;
		Rect rect = WeekCanvasUtils.getInstance().getCourseRect(noCurrentWeekCourse, spiritStyle.getWeekViewParams());
		changeToNewRect(rect);
		Animation animation = AnimationUtils.getInstance().createFadeAnim();
		Point pt = new Point(rect.centerX()-sparkAnimationImageSize/2, rect.centerY()-sparkAnimationImageSize/2);
		TutorialParams tutorialParams = new TutorialParams(category, "为什么有些课颜色比较浅?", this, R.drawable.tutorial_spark, 
				animation, pt
		);
//		tutorialParams.unBlockRegion = rect;
		tutorialParams.notCoverRect = rect;
		tutorialParams.coverRects = getOtherRects(rect);
		tutorialParams.noticeImage = R.drawable.tutorial_tuotuo_ask_left;
		list.add(tutorialParams);//1
		
		tutorialParams = new TutorialParams(category, "浅色的课程是当前周不上的课程", this, R.drawable.tutorial_spark, animation, pt);
		tutorialParams.coverRects = getOtherRects(rect);
		tutorialParams.notCoverRect = rect;
		tutorialParams.noticeLayout = R.layout.view_tutorial_notice_image_right;
		list.add(tutorialParams);//2
		tutorialParams = new TutorialParams(category, "格子会自动判断每周要上的课，方便规划时间", this, R.drawable.tutorial_spark, animation, pt);
		tutorialParams.coverRects = getOtherRects(rect);
		tutorialParams.notCoverRect = rect;
		tutorialParams.noticeLayout = R.layout.view_tutorial_notice_image_right;
		list.add(tutorialParams);//3
		return list;
	}
	
	private boolean intersects(Rect rect1, Rect rect2){
		return rect1.intersects(rect2.left, rect2.top, rect2.right, rect2.bottom);
	}
	
	private List<Rect> getOtherRects(Rect rect){
		List<Rect> notCoverRects = new ArrayList<Rect>();
		notCoverRects.add(rect);
		return getOtherRects(notCoverRects);
	}
	
	private List<Rect> getOtherRects(List<Rect> notCoverRects){
		List<Rect> result = new ArrayList<Rect>();
		for (Rect rect : rects) {
			boolean intersect = false;
			for (Rect notCoverRect : notCoverRects) {
				if (intersects(rect, notCoverRect)) {
					intersect = true;
					break;
				}
			}
			if (!intersect) {
				result.add(rect);
			}
		}
		return result;
	}
	
	private List<TutorialParams> getConflictCoursesTutorials(List<CourseBlock> confictCourses){
		List<TutorialParams> list = new LinkedList<TutorialParams>();
		String category = CONFLICT_COURSES_TUTORIAL;
		List<Rect> notCoverRects = new ArrayList<Rect>();
		for (CourseBlock courseBlock : confictCourses) {
			Rect rect = WeekCanvasUtils.getInstance().getCourseRect(courseBlock, spiritStyle.getWeekViewParams());
			changeToNewRect(rect);
			notCoverRects.add(rect);
		}
		Animation animation = AnimationUtils.getInstance().createFadeAnim();
		Rect rect = notCoverRects.get(notCoverRects.size()-1);
		Point pt = new Point(rect.centerX()-sparkAnimationImageSize/2, rect.centerY()-sparkAnimationImageSize/2);
		TutorialParams tutorialParams = new TutorialParams(category, "注意到课程右上方的小折角了吗？", this, R.drawable.tutorial_spark, 
				animation, pt
		);
//		tutorialParams.unBlockRegion = rect;
		notCoverRects.add(rect);
		tutorialParams.noticeImage = R.drawable.tutorial_tuotuo_ask_left;
		tutorialParams.coverRects = getOtherRects(notCoverRects);
		tutorialParams.notCoverRect = rect;
		list.add(tutorialParams);//1
		
		tutorialParams = new TutorialParams(category, "折角说明这门课的下面还藏着别的东西哦", this, R.drawable.tutorial_spark, animation, pt);
		tutorialParams.coverRects = getOtherRects(notCoverRects);
		tutorialParams.noticeLayout = R.layout.view_tutorial_notice_image_right;
		tutorialParams.notCoverRect = rect;
		list.add(tutorialParams);//2
		
		pt = new Point(rect.right + ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 20), rect.centerY()-arrowAnimationImageWidth/2);
		animation = AnimationUtils.getInstance().createTranslateAnim(0, -0.5f, 0, 0);
		animation.setRepeatCount(1000);
		animation.setDuration(2000);
		tutorialParams = new TutorialParams(category, "试着点击这门课", this, R.drawable.tutorial_arrow, animation, pt);
		Rect unBlockRegion = new Rect();
		for (Rect rect2 : notCoverRects) {
			unBlockRegion.union(rect2);
		}
		tutorialParams.clickRegion = unBlockRegion;
		tutorialParams.coverRects = getOtherRects(notCoverRects);
		tutorialParams.notCoverRect = rect;
		tutorialParams.noticeLayout = R.layout.view_tutorial_notice_image_right;
//		tutorialParams.sendEvent = false;
		tutorialParams.id = CLICK_ON_CONFLICT_COURSES;
		list.add(tutorialParams);//3
		return list;
	}
	
	private List<TutorialParams> getShowMoreTutorials(){
		List<TutorialParams> list = new LinkedList<TutorialParams>();
		TutorialParams tutorialParams = new TutorialParams(SHOW_MORE_TUTORIAL, "太棒了，你已经熟悉了课程格子的基本用法", this); list.add(tutorialParams);//1
		if (tutorialViewHeight == screenHeight) {
			tutorialViewHeight -= ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 25);
		}
		
		Point pt = new Point(screenWidth/2-arrowAnimationImageWidth/2, (tutorialViewHeight - (arrowAnimationImageHeight + ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 40+20))));
		Animation animation = AnimationUtils.getInstance().createTranslateAnim(0, 0f, 0, 0.5f);
		animation.setRepeatCount(1000);
		animation.setRepeatMode(Animation.REVERSE);
		animation.setDuration(1000);
		tutorialParams = new TutorialParams(SHOW_MORE_TUTORIAL, "更多好玩功能藏在下面等你发现哟~~~", this, R.drawable.tutorial_arrow, 
				animation, pt  
		);
		tutorialParams.animateRotate = 270;
		tutorialParams.clickRegion = new Rect(screenWidth/2-menuView.getWidth()/2, 
				tutorialViewHeight - menuView.getHeight(), screenWidth/2+menuView.getWidth()/2, tutorialViewHeight);
		tutorialParams.notCoverRect = tutorialParams.clickRegion;
		list.add(tutorialParams);//2
		return list;
	}

	@Override
	public void onTriger(final TutorialParams params) {
		if (params.trigerEvent == TrigerEvent.FLING_TO_RIGHT) {
			((CustomScrollView)activity.findViewById(R.id.weekview_parent)).smoothScrollTo(weekView.getRight(), 0);
		}else if (scrollView.getScrollX() !=0 || scrollView.getScrollY() != 0) {
			if (params.id != CLICK_ON_CONFLICT_COURSES) {
				scrollView.scrollTo(0, 0);
			}
		}
		menuView.setVisibility(View.VISIBLE);
//		if (params.id == VIEW_COURSE) {
//			new Handler().postDelayed(new Runnable() {
//				
//				@Override
//				public void run() {
//					callParentTriger(params);
//				}
//			}, 1000);
//		} else 
		if (params.id == CLICK_ON_CONFLICT_COURSES) {
			mapTutorialSets.get(params.category).finishCurrentStep();
			if (isFinished()) {
				stopTutorials();
			}else {
				tutorialView.setNoticeVisibility(View.GONE);
			}
		}else {
			super.onTriger(params);
		}
	}
	
//	private void callParentTriger(TutorialParams params){
//		super.onTriger(params);
//	}
	
	@Override
	public void onError(TutorialParams params) {
		if (params.trigerEvent == TrigerEvent.FLING_TO_RIGHT) {
			tutorialView.setNoticeText("滑一下试试");
		}
	}
	
	@SuppressLint("CommitPrefEdits")
	public static void clearTutorials(){
		SharedPreferences sharedPreferences = App.getInstance().getDefaultPreferences();
		Editor editor = sharedPreferences.edit();
		editor.remove(NORMAL_TUTORIAL);
		editor.remove(NOT_CURRENT_WEEK_TUTORIAL);
		editor.remove(CONFLICT_COURSES_TUTORIAL);
		editor.remove(SHOW_MORE_TUTORIAL);
		App.commitEditor(editor);
	}
}

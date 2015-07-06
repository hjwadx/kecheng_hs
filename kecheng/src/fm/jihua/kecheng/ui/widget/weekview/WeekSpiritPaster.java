package fm.jihua.kecheng.ui.widget.weekview;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.OfflineData;
import fm.jihua.kecheng.rest.entities.OfflineData.Operation;
import fm.jihua.kecheng.rest.entities.sticker.PasteResult;
import fm.jihua.kecheng.rest.entities.sticker.UserSticker;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.rest.service.RestService;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.activity.sticker.ChooseStickerActivity;
import fm.jihua.kecheng.ui.adapter.ChooseStickerPagerAdapter;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.WeekView;
import fm.jihua.kecheng.utils.AnimationUtils;
import fm.jihua.kecheng.utils.AppLogger;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.ImageHlp;

/**
 * @date 2013-7-25
 * @introduce WeekView的绘制
 */
public class WeekSpiritPaster {

	Set<Point> courseBlockPoints;
	Set<Point> pointsPaster = new HashSet<Point>();
	Set<Point> pointsForValidPaste = new LinkedHashSet<Point>();
	Set<Point> pointsForWhiteBack = new LinkedHashSet<Point>();
	Set<UserSticker> existPasters;
	UserSticker pasterBefore;
	UserSticker paster2Move;
	WeekViewParams weekViewParams;
	Animation fadingAnimation;
	Bitmap bitmapSelectedPaster;
	Paint paintSelectedPaster;
	public final static int NEWPASTE = 1;
	public final static int NORMAL = 2;
	public final static int REPASTE = 3;
	private int state = NORMAL;
	Handler mHandler;
	Context context;
	WeekView weekView;
	
	
	public WeekSpiritPaster() {
		super();
		fadingAnimation = AnimationUtils.getInstance().createFadeAnim();
		paintSelectedPaster = new Paint();
		mHandler = new Handler();
	}

	public void resetData(Set<Point> notValidPoints, List<UserSticker> existPasters, WeekViewParams weekViewParams,WeekView weekView){
		this.courseBlockPoints = notValidPoints;
		this.existPasters = new HashSet<UserSticker>(existPasters);
		this.weekViewParams = weekViewParams;
		this.weekView = weekView;
		this.context = weekView.getContext();
		
		initExistPastePoints();
	}
	
	public boolean prepareToPaste(UserSticker paster){
		this.pasterBefore = paster;
		this.paster2Move = (UserSticker) pasterBefore.clone();
		if (existPasters.contains(paster)) {
			this.state = REPASTE;
			existPasters.remove(pasterBefore);
			weekView.invalidateGraduationViewForPaste(true);
			initExistPastePoints();
			initValidPasterPoints();
			return true;
		}else {
			initValidPasterPoints();
			if (pointsForValidPaste.size() > 0) {
				this.state = NEWPASTE;
				Point firstPaster = pointsForValidPaste.iterator().next();
				pasterBefore.setPoint(firstPaster);
				paster2Move.setPoint(firstPaster);
				weekView.invalidateGraduationViewForPaste(true);
				return true;
			}
		}
		return false;
	}
	
	public void draw(Canvas canvas){
		drawExistPasters(canvas);
		if (isChossingPaster()) {
			drawPasterHintView(canvas);
			drawPasterShadows(canvas);
			drawSelectedPaster(canvas);
		}
	}
	
	public boolean onTouchUpPasting(Point pt_touchdown){
		if (isChossingPaster()) {
			UserSticker paster = choosePositionByActualXY(pt_touchdown);
			if (paster != null) {
				changeSelectedPasterPosition(weekViewParams.changeActualXYToSlotXY(pt_touchdown));
			}
			return true;
		}
		return false;
	}
	
	public boolean onTouchUp(Point pt_touchdown){
		UserSticker paster = selectPasterByActualXY(pt_touchdown);
		if (paster != null) {
			weekView.childClickListener.onPasterClick(paster);
			return true;
		}
		return false;
	}
	
	public boolean onLongClick(Point pt_touchdown){
		UserSticker paster = selectPasterByActualXY(pt_touchdown);
		if (paster != null && !paster.isHidden()) {
			showDialogForPaster(paster);
			return true;
		}
		return false;
	}
	
	private void drawExistPasters(Canvas canvas){
		for (UserSticker paster : existPasters) {
			if (!ObjectUtils.nullSafeEquals(paster.getPoint(), paster2Move == null ? null : paster2Move.getPoint()) && !paster.isHidden()) {
				drawPaster(canvas, paster);
			}
		}
	}
	
	private void drawPasterHintView(Canvas canvas){
		
		for (Point pt : pointsForWhiteBack) {
			drawHintViewWhiteBack(canvas, pt);
		}
		
		for (Point pt : pointsForValidPaste) {
			if (!ObjectUtils.nullSafeEquals(pt, paster2Move.getPoint())) {
				drawHintView(canvas, paster2Move, pt);
			}
		}
	}
	
	private void drawPaster(Canvas canvas, final UserSticker paster) {
		drawPaster(canvas, paster, paster.getPoint());
	}
	
	private void drawPaster(Canvas canvas, final UserSticker userSticker, Point pt) {
		Rect rect = WeekCanvasUtils.getInstance().getPasterRect(userSticker, weekViewParams);
		Rect clipBounds = canvas.getClipBounds();
		if (rect.left > clipBounds.right || rect.right < clipBounds.left || rect.top > clipBounds.bottom || rect.bottom < clipBounds.top) {
			return;
		}
		Drawable drawable = userSticker.getDrawable(App.getInstance());
		if (drawable != null) {
			drawable.setBounds(rect);
			drawable.draw(canvas);
		}else{
			ImageContainer specialBitmap = App.getInstance().getImageLoader().getSpecialBitmap(RestService.get().getDownloadStickerUrlFromChatCode(userSticker.sticker.chat_code), new ImageListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
				}
				
				@Override
				public void onResponse(ImageContainer response, boolean isImmediate) {
					weekView.invalidate();
				}
			});
			if(specialBitmap != null){
				Bitmap bitmap = specialBitmap.getBitmap();
				if(bitmap != null){
					drawable = new BitmapDrawable(weekView.getContext().getResources(), bitmap);
					drawable.setBounds(rect);
					drawable.draw(canvas);
				}
			}
		}
	}
	
	private void drawHintViewWhiteBack(Canvas canvas, Point pt){
		canvas.drawRect(WeekCanvasUtils.getInstance().getPasterRect(pt, weekViewParams), WeekCanvasUtils.getInstance().getHintViewWhiteBackgroundPaint());
	}
	
	private void drawHintView(Canvas canvas, final UserSticker paster, Point pt){
		Rect rect = WeekCanvasUtils.getInstance().getHintViewRect(paster, pt, weekViewParams);
		Rect clipBounds = canvas.getClipBounds();
		if (rect.left > clipBounds.right || rect.right < clipBounds.left || rect.top > clipBounds.bottom || rect.bottom < clipBounds.top) {
			return;
		}
		int radius = (rect.right-rect.left)/2;
		canvas.drawCircle(rect.left+radius, rect.top+radius, radius, WeekCanvasUtils.getInstance().getHintViewBackgroundPaint());
		int textSize = ImageHlp.changeToSystemUnitFromDP(context, 13);
		canvas.drawText("贴", rect.left+radius - textSize/2, rect.top+ radius + textSize/3, WeekCanvasUtils.getInstance().getHintViewTextPaint(textSize));
	}
	
	private void drawPasterShadows(Canvas canvas){
		for (Point pt : courseBlockPoints) {
			drawPasterShadow(canvas, pt);
		}
		
		for (Point pt : pointsPaster) {
			if (!this.pasterBefore.getPointList().contains(pt)) {
				drawPasterShadow(canvas, pt);
			}
		}
	}
	
	private void drawPasterShadow(Canvas canvas, Point point) {
		drawPasterShadow(canvas, point, null);
	}
	
	private void drawPasterShadow(Canvas canvas, Point point, UserSticker userSticker) {
		int width_number = 1;
		int height_number = 1;
		if (userSticker != null) {
			width_number = userSticker.sticker.width;
			height_number = userSticker.sticker.height;
		}
		Rect shadowRect = WeekCanvasUtils.getInstance().getShadowRect(point, width_number, height_number, weekViewParams);
		weekViewParams.drawableShadow.setBounds(shadowRect);
		weekViewParams.drawableShadow.draw(canvas);
	}
	
	public void drawSelectedPaster(Canvas canvas){
		Transformation transformation = new Transformation();
		fadingAnimation.getTransformation(System.currentTimeMillis(), transformation);
		if (bitmapSelectedPaster == null) {
			bitmapSelectedPaster = createSelectedPasterBitmap();
		}
		float alpha = transformation.getAlpha();
		paintSelectedPaster.setAlpha((int) (alpha*255));
		Rect rect = WeekCanvasUtils.getInstance().getPasterRect(paster2Move, weekViewParams);
		AppLogger.d(rect.toShortString());
		canvas.drawBitmap(bitmapSelectedPaster, rect.left, rect.top, paintSelectedPaster);
	}
	
	void showDialogForPaster(final UserSticker paster) {
		if (paster.getDrawable(App.getInstance()) == null) {
			Hint.showTipsShort(App.getInstance(), "该套贴纸已被删除，无法重新张贴");
			AlertDialog.Builder builder = new AlertDialog.Builder(weekView.getContext());
			builder.setItems(new String[] { "删除" }, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						deletePaster(paster);
						weekView.invalidate();
						break;

					}
				}
			});
			builder.create().show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(weekView.getContext());
			builder.setItems(new String[] { "重新贴", "删除" }, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						showChoosingPasterMode(paster);
						break;
					case 1:
						deletePaster(paster);
						weekView.invalidate();
						break;

					}
				}
			});
			builder.create().show();
		}

	}
	
	public void showChoosingPasterMode(UserSticker paster) {
		if (prepareToPaste(paster)) {
			if (weekView.modifyTitleStatusListener != null) {
				weekView.modifyTitleStatusListener.pasterStatusChanged(WeekView.TYPE_TITLE_PASTER_EDIT);
			}
			showHighlightPasterView();
			int[] currentSelectedScrollInfo = getCurrentSelectedScrollInfo();
			if (currentSelectedScrollInfo != null)
				weekView.spiritStyle.getScrollView().scrollTo(currentSelectedScrollInfo[0] - weekView.getScreenWidth() / 2, currentSelectedScrollInfo[1] - weekView.getScreenHeight() / 2);
			weekView.invalidate();
		} else {
			Hint.showTipsShort(weekView.getContext(), R.string.paster_hint_string);
		}
	}
	
	private boolean isReturnToSticker = false;
	private int returnLocationPosition = 0;

	public void willReturnToStickerChoose(boolean isReturnToSticker, int returnLocationPosition) {
		this.isReturnToSticker = isReturnToSticker;
		if(isReturnToSticker){
			this.returnLocationPosition = returnLocationPosition;
		}else{
			this.returnLocationPosition = 0;
		}
	}
	
	public boolean isWillReturnToSticker(){
		return isReturnToSticker;
	}
	
	public boolean isChossingPaster(){
		return state == NEWPASTE || state == REPASTE;
	}
	
	public void resetWeekViewParams(WeekViewParams weekViewParams){
		this.weekViewParams = weekViewParams;
	}
	
	public UserSticker getSelectedPaster(){
		return paster2Move;
	}
	
//	public Set<Paster> getPasterSet(){
//		return existPasters;
//	}
	
	public void changeSelectedPasterPosition(Point point){
		paster2Move.setPoint(point);
		showHighlightPasterView();
	}
	
	public int[] getCurrentSelectedScrollInfo() {
		Rect pasterRect = WeekCanvasUtils.getInstance().getPasterRect(paster2Move, weekViewParams);
		return new int[] { pasterRect.left, pasterRect.top };
	}
	
	private void addPaster2List(UserSticker paster){
		existPasters.add(paster);
//		App.getInstance().saveStickerList(StickerSetProduct.getUseFulStickerList(existPasters));
		initExistPastePoints();
	}
	
	void initValidPasterPoints(){
		
		pointsForValidPaste.clear();
		pointsForWhiteBack.clear();
		for (int x = 0; x < weekViewParams.dayCount; x++) {
			for (int y = 0; y < weekViewParams.timeSlot; y++) {
				Point point = new Point(x, y);
				if (!courseBlockPoints.contains(point) && !pointsPaster.contains(point)) {
					if (checkIsValidHintView(point, paster2Move)) {
						pointsForValidPaste.add(point);
					}
					pointsForWhiteBack.add(point);
				}
			}
		}
	}
	
	boolean checkIsValidHintView(Point pt, UserSticker userSticker){
		boolean isValid = true;
		for (int i = 0; i < userSticker.sticker.width && isValid; i++) {
			for (int j = 0; j < userSticker.sticker.height && isValid; j++) {
				int currentX = pt.x + i;
				int currentY = pt.y + j;
				// 越界问题
				if (currentX > (weekViewParams.dayCount - 1) || currentY > (weekViewParams.timeSlot - 1)) {
					isValid = false;
				}else{
					Point pointNear = new Point(currentX, currentY);
					if (courseBlockPoints.contains(pointNear) || pointsPaster.contains(pointNear)) {
						isValid = false;
					}
				}
			}
		}
		return isValid;
	}
	
	void initExistPastePoints(){
		checkIsNeedHiddenByCourses();
		checkIsNeedHiddenByPasteAndInitPoint();
	}
	
	void checkIsNeedHiddenByCourses() {
		for (UserSticker paster : existPasters) {
			paster.setHiddenStatus(false);
			List<Point> pointList = paster.getPointList();
			for (Point point : pointList) {
				if (courseBlockPoints.contains(point)) {
					paster.setHiddenStatus(true);
					break;
				}
			}
		}
	}
	
	void checkIsNeedHiddenByPasteAndInitPoint(){
		pointsPaster.clear();
		for (UserSticker paster : existPasters) {
			if (!paster.isHidden()){
				List<Point> pointList = paster.getPointList();
				boolean needHidden = false;
				for (Point point : pointList) {
					if(pointsPaster.contains(point)){
						needHidden = true;
					}
				}
				if(needHidden){
					paster.setHiddenStatus(true);
				}else{
					pointsPaster.addAll(paster.getPointList());
				}
			}
		}
	}
	
	
	public void cancelPaste() {
		removeHighlightPasterView();
		if (state == REPASTE){
			addPaster2List(pasterBefore);
		}
		resetNew();
		weekView.invalidate();
		if (weekView.modifyTitleStatusListener != null) {
			weekView.modifyTitleStatusListener.pasterStatusChanged(WeekView.TYPE_TITLE_NORMAL);
		}
		willReturnToStickerChoose(false, 0);
	}

	public void confirmPaste() {
		MobclickAgent.onEvent(weekView.getContext(), "event_course_sticker_succeed", "sticker " + getSelectedPaster().sticker.product_id);
		removeHighlightPasterView();
		pasterBefore.setPoint(paster2Move.getPoint());
		addPaster2List(pasterBefore);
		
		Gson gson = new Gson();
		// ----在此处，如果paster是有id的，就修改，如果没有id，则调用添加接口
		if (pasterBefore.id == UserSticker.DEFAULT_ID) {
			OfflineData<UserSticker> offlineData = new OfflineData<UserSticker>(UserSticker.class, gson.toJson(pasterBefore), Operation.ADD, App.getInstance().getActiveSemesterId());
			int id = App.getInstance().getDBHelper().saveOfflineData(App.getInstance().getUserDB(), offlineData);
			getDataAdapter(id, pasterBefore).pasterSticker(pasterBefore);
		} else {
			OfflineData<UserSticker> offlineData = new OfflineData<UserSticker>(UserSticker.class, gson.toJson(pasterBefore), Operation.MODIFY, App.getInstance().getActiveSemesterId());
			int id = App.getInstance().getDBHelper().saveOfflineData(App.getInstance().getUserDB(), offlineData);
			getDataAdapter(id, pasterBefore).modifySticker(pasterBefore);
		}
		resetNew();
		weekView.invalidate();
		if (weekView.modifyTitleStatusListener != null) {
			weekView.modifyTitleStatusListener.pasterStatusChanged(WeekView.TYPE_TITLE_NORMAL);
		}
		
		//是否要跳转回到贴纸选择页面
		if(isWillReturnToSticker()){
			Intent intent = new Intent(weekView.getContext(), ChooseStickerActivity.class);
//			intent.putExtra(BaseActivity.INTENT_THEME, R.style.XTheme_Transparent_Popup);
			intent.putExtra(ChooseStickerPagerAdapter.FRAGMENT_POSITION, returnLocationPosition);
			((MenuActivity) weekView.getContext()).startActivityForResult(intent, ChooseStickerActivity.PASTER_REQUESTCODE);
			((MenuActivity) weekView.getContext()).overridePendingTransition(0, 0);
		}
		willReturnToStickerChoose(false, 0);
	}
	
	private void resetNew(){
		paster2Move = null;
		this.state = NORMAL;
		pointsForValidPaste.clear();
		weekView.invalidateGraduationViewForPaste(false);
	}
	
	public UserSticker choosePositionByActualXY(Point pt){
		Point point = weekViewParams.changeActualXYToSlotXY(pt);
		if (point != null && pointsForValidPaste.contains(point)) {
			UserSticker paster = (UserSticker)paster2Move.clone();
			paster.setPoint(point);
			return paster;
		}else {
			return null;
		}
	}
	
	public UserSticker selectPasterByActualXY(Point pt){
		UserSticker selected = null;
		Point point = weekViewParams.changeActualXYToSlotXY(pt);
		for (UserSticker paster : existPasters) {
			if (paster.getPointList().contains(point)) {
				selected = paster;
				break;
			}
		}
		return selected;
	}
	
	public Bitmap createSelectedPasterBitmap(){
		Rect rect = WeekCanvasUtils.getInstance().getPasterRect(paster2Move, weekViewParams);
		Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Drawable drawable = paster2Move.getDrawable(App.getInstance());
		drawable.setBounds(0, 0, rect.width(), rect.height());
		drawable.draw(canvas);
		return bitmap;
	}
	
	public void removeHighlightPasterView(){
		stopShowAnimation();
	}
	
	public void showHighlightPasterView(){
		startShowAnimation(new InvalidateCallback() {
			
			@Override
			public void invalidate(Rect rect) {
				weekView.invalidate(rect);
			}
		});
	}
	
	private final Runnable mRunnable1 = new Runnable() {
		public void run() {
			Rect rect = WeekCanvasUtils.getInstance().getPasterRect(paster2Move, weekViewParams);
			invalidateCallback.invalidate(rect);
			mHandler.postDelayed(mRunnable1, 33);
		}
	};
	
	InvalidateCallback invalidateCallback;
	public void startShowAnimation(InvalidateCallback callback){
		if (!fadingAnimation.isInitialized()) {
			this.invalidateCallback = callback;
			fadingAnimation.startNow();
			mHandler.post(mRunnable1);
		}
	}
	
	public void stopShowAnimation(){
		fadingAnimation.reset();
		mHandler.removeCallbacks(mRunnable1);
		bitmapSelectedPaster.recycle();
		bitmapSelectedPaster = null;
	}
	
	public interface InvalidateCallback{
		void invalidate(Rect rect);
	}
	
	UserSticker paster2Remove;
	
	public void removePaster(UserSticker paster2Remove){
		this.paster2Remove = paster2Remove;
		existPasters.remove(paster2Remove);
//		App.getInstance().saveStickerList(StickerSetProduct.getUseFulStickerList(existPasters));
		initExistPastePoints();
	}
	
	public void deletePaster(final UserSticker userSticker) {
		removePaster(userSticker);
		// 调用删除接口
		if (userSticker.id != UserSticker.DEFAULT_ID) {
			Gson gson = new Gson();
			OfflineData<UserSticker> offlineData = new OfflineData<UserSticker>(UserSticker.class, gson.toJson(userSticker), Operation.DELETE, App.getInstance().getActiveSemesterId());
			int id = App.getInstance().getDBHelper().saveOfflineData(App.getInstance().getUserDB(), offlineData);
			getDataAdapter(id, userSticker).removeSticker(userSticker);
		}
	}
	
	private DataAdapter getDataAdapter(final int offlineDataId, final UserSticker userSticker){
		
		return new DataAdapter((Activity) context, new DataCallback() {

			@Override
			public void callback(Message msg) {
				List<UserSticker> userStickers = App.getInstance().getSavedStickerList();
				switch (msg.what) {
				case DataAdapter.MESSAGE_STICKER_PASTE: {
					PasteResult pasterResult = (PasteResult) msg.obj;
					if (pasterResult != null) {
						AppLogger.i("paste_sticker  "+pasterResult.toString());
						if (pasterResult.success) {
							pasterBefore.id = pasterResult.new_id;
							userStickers.add(userSticker);
							App.getInstance().saveStickerList(userStickers);
						}else{
							AppLogger.i("paste_sticker_is_failed");
						}
						App.getInstance().getDBHelper().deleteOfflineData(App.getInstance().getUserDB(), offlineDataId);
					}
					break;
				}
				case DataAdapter.MESSAGE_STICKER_MODIFY: {
					BaseResult pasterResult = (BaseResult) msg.obj;
					if (pasterResult != null) {
						if (!pasterResult.success) {
							AppLogger.i("modity_sticker_is_failed");
						}else {
							UserSticker oldPaster = (UserSticker) CommonUtils.findById(userStickers, userSticker.id);
							if (oldPaster != null && userStickers.remove(oldPaster)) {
								userStickers.add(userSticker);
								App.getInstance().saveStickerList(userStickers);
							}
						}
						App.getInstance().getDBHelper().deleteOfflineData(App.getInstance().getUserDB(), offlineDataId);
					}
					break;
				}
				case DataAdapter.MESSAGE_STICKER_REMOVE:
					BaseResult baseResult = (BaseResult) msg.obj;
					if (baseResult != null) {
						App.getInstance().getDBHelper().deleteOfflineData(App.getInstance().getUserDB(), offlineDataId);
						if (baseResult.success) {
							UserSticker oldPaster = (UserSticker) CommonUtils.findById(userStickers, userSticker.id);
							if (oldPaster != null && userStickers.remove(oldPaster)) {
								App.getInstance().saveStickerList(userStickers);
							}
						}
					}
					break;
				default:
					break;
				}
			}
		});
	}
	
}

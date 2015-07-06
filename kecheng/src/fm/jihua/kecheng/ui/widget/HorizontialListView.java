/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package fm.jihua.kecheng.ui.widget;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;
import fm.jihua.kecheng.utils.Const;

/**
 * @author 黄祥旦
 *注意使用细节：
 *1、Child不要使用setLayoutParam进行设置，否则崩溃
 */
public class HorizontialListView extends AdapterView<ListAdapter> {
	//TODO 需要重写，因为没有实现Multi View
	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter mAdapter;
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;	//指的是当前屏幕左侧的实际位置
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mMaxChildX = Integer.MAX_VALUE;
	private int mMinX = Integer.MIN_VALUE;
	private int mDisplayOffset = 0;		//是指第一个出现的元素的偏移量
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View>[] mRemovedViewQueue;
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnLayoutCompletedListener mOnLayoutCompleted;
	private OnDoubleTapListener mOnDoubleTabed;
	private boolean mDataChanged = false;
	private boolean mIsUp = false;
	private boolean mRequestScrollTo = false;		//是否请求超出范围回弹
	private int mLastChildRight = 0;
	private boolean mIsScrolling = false;
	private int mWidth;
	private boolean mIsStartScrolling = false;
	

	public HorizontialListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mLastChildRight = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mMaxChildX = Integer.MAX_VALUE;
		if(mScroller == null)
			mScroller = new Scroller(getContext());
		if(mGesture == null)
			mGesture = new GestureDetector(getContext(), mOnGesture);
	}
	
	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}
	
	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
		mOnItemClicked = listener;
	}
	
	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized(HorizontialListView.this){
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
//			Log.i("horizontallistview", "onChanged");
		}

		@Override
		public void onInvalidated() {
			synchronized(HorizontialListView.this){
				mDataChanged = true;
			}
			reset();
			invalidate();
			requestLayout();
//			Log.i("horizontallistview", "onInvalidated");
		}
		
	};

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		//TODO: implement
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if(mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		setViewTypeCount(mAdapter.getViewTypeCount());
		reset();
	}
	
	void setViewTypeCount(int count){
		mRemovedViewQueue = new Queue[count];
		for (int i = 0; i < count; i++) {
			mRemovedViewQueue[i] = new LinkedList<View>();
        }
	}
	
	private synchronized void reset(){
		initView();
		removeAllViewsInLayout();
        requestLayout();
	}

	@Override
	public void setSelection(int position) {
		//TODO: implement
	}
	
	public void setOnLayoutCompletedListener(OnLayoutCompletedListener listener){
		this.mOnLayoutCompleted = listener;
	}
	
	public void setOnDoubleTapListener(OnDoubleTapListener listener){
		this.mOnDoubleTabed = listener;
	}
	
	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if(params == null) {
			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		addViewInLayout(child, viewPos, params, true);
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if(mAdapter == null){
			return;
		}
		
		int scrollOffset = 0;
		boolean scrolling = false;
		
		if(mDataChanged){
			int oldCurrentX = mCurrentX;
			initView();
			removeAllViewsInLayout();
			mNextX = oldCurrentX;
			mScroller.setFinalX(oldCurrentX);
		}

		if(mScroller.computeScrollOffset()){
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
//			Log.i("horizontallistview", "getCurrX, scrollx:"+scrollx);
		}
		
		if(mNextX <= mMinX){
			Log.i("horizontallistview", "mNextX:"+mNextX);
			mNextX = mMinX;
			if(!mRequestScrollTo){
				mScroller.forceFinished(true);
			}
//			Log.i("horizontallistview", "isfinished:"+mScroller.isFinished());
		}
		if(mNextX >= mMaxX) {
			Log.i("horizontallistview", "mNextX:"+mNextX);
			mNextX = mMaxX;
			if(!mRequestScrollTo){
				mScroller.forceFinished(true);
			}
//			Log.i("horizontallistview", "isfinished:"+mScroller.isFinished());
		}
		
//		Log.i("horizontallistview", "onLayout");
		
		int dx = mCurrentX - mNextX;

		scrollOffset = mDataChanged ? 0 : dx;
		mDataChanged = false;
		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);
		
		mCurrentX = mNextX;
		
		if(mNextX > mMaxChildX || mNextX < 0){
			//mNextX = mMaxChildX;
			if(mIsUp && mScroller.isFinished()){
				if(!mRequestScrollTo){
//					Log.i("horizontallistview", "post MyUIThread");
					mRequestScrollTo = true;
					scrolling = true;
					post(new MyUIThread( mNextX < 0 ? 0 : mMaxChildX));	
				}
			}
			
		}else{
			mRequestScrollTo = false;
//			Log.i("horizontallistview", "mRequestScrollTo:false");
		}
		
		if(!mScroller.isFinished()){
//			Log.i("horizontallistview", "last isFinished"+ mScroller.isFinished());
			scrolling = true;
			post(new Runnable(){
				@Override
				public void run() {
//					Log.i("horizontallistview", "Runnable requestLayout");
					requestLayout();
				}
			});
		}
		if(mOnLayoutCompleted != null){
			mOnLayoutCompleted.onLayoutCompleted(scrollOffset, mIsScrolling);
		}
		mIsScrolling = scrolling;
	}
	
	public int getCurScrollX(){
		return mScroller.getCurrX();
	}
	
	public int getWidth(View v){
		try{
			LayoutParams lp = v.getLayoutParams();
			if(lp instanceof MarginLayoutParams){
				MarginLayoutParams mlp = (MarginLayoutParams)lp;
				return v.getMeasuredWidth()+mlp.leftMargin+mlp.rightMargin;
			}
		}catch(Exception e){
			Log.e(Const.TAG, this.getClass().getName()+" getWidth Exception:"+e.getMessage());
		}
		return v.getMeasuredWidth();
	}
	
	public int getHeight(View v){
		try{
			LayoutParams lp = v.getLayoutParams();
			if(lp instanceof MarginLayoutParams){
				MarginLayoutParams mlp = (MarginLayoutParams)lp;
				return v.getMeasuredHeight()+mlp.topMargin+mlp.bottomMargin;
			}
		}catch(Exception e){
			Log.e(Const.TAG, this.getClass().getName()+" getHeight Exception:"+e.getMessage());
		}
		return v.getMeasuredHeight();
	}
	
	private void fillList(final int dx) {
		if(mMaxX <= this.getMeasuredWidth()/2){
			//如果滚动条的位置比全部内容的长度还长，那么就不显示所有内容。
			if(mNextX > mWidth){
				mDisplayOffset += dx;
				return;
			}else{
				
			}
		}
		int edge = 0;
		View child = getChildAt(getChildCount()-1);
//		Log.i("horizontallistview", "mLastChildRight:"+mLastChildRight);
		if(child != null) {
			edge = child.getRight();
//			Log.i("horizontallistview", "child.getRight():"+mLastChildRight);
		}else{
			edge = mLastChildRight;
		}
		fillListRight(edge, dx);
		
		edge = 0;
		
		child = getChildAt(0);
		if(child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);
		
		
	}
	
	private void fillListRight(int rightEdge, final int dx) {
		//int initIndex = mRightViewIndex;
//		Log.i("horizontallistview", "mCurrentX:"+mCurrentX);
//		Log.i("horizontallistview", "mRightViewIndex:"+mRightViewIndex);
//		Log.i("horizontallistview", "dx:"+dx);
		while(rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
			int viewType = mAdapter.getItemViewType(mRightViewIndex);
			View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue[viewType].poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += getWidth(child);
//			Log.i("horizontallistview", "rightEdge:"+rightEdge);
			if(mRightViewIndex == mAdapter.getCount()-1){
				mMaxX = mCurrentX + rightEdge - getWidth();
				if(mMaxX < 0){
					mMaxX = 0;
					mWidth = mCurrentX + rightEdge;
				}
				mMaxChildX = mMaxX;
				mMaxX += this.getMeasuredWidth()/2;
//				Log.i("horizontallistview", "mAdapter.getCount()-1:"+(mAdapter.getCount()-1));
//				Log.i("horizontallistview", "mMaxChildX:"+mMaxChildX);
//				Log.i("horizontallistview", "mMaxX:"+mMaxX);
			}
			
			mRightViewIndex++;
		}
	}
	
	private void fillListLeft(int leftEdge, final int dx) {
		while(leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			int viewType = mAdapter.getItemViewType(mLeftViewIndex);
			View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue[viewType].poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= getWidth(child);
			
			mLeftViewIndex--;
			mDisplayOffset -= getWidth(child);
		}
		mMinX = -this.getMeasuredWidth()/2;
	}
	
	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
//		int width = 0;
		while(child != null && child.getRight() + dx <= 0) {
//			width += getWidth(child);
			mDisplayOffset += getWidth(child);
			int viewType = mAdapter.getItemViewType(mLeftViewIndex+1);
			mRemovedViewQueue[viewType].offer(child);
			removeViewInLayout(child);
//			Log.i("horizontallistview", "removeNonVisibleItems lt");
			mLeftViewIndex++;
			child = getChildAt(0);
		}
//		if(mMaxX <= this.getMeasuredWidth()/2 && mNextX > width && width != 0){
//			mDisplayOffset = mNextX;
//		}
		//if(dx + mDisplayOffset < )
		
		child = getChildAt(getChildCount()-1); 
		while(child != null && child.getLeft() + dx >= getWidth()) {
			int viewType = mAdapter.getItemViewType(mRightViewIndex-1);
			mRemovedViewQueue[viewType].offer(child);
			removeViewInLayout(child);
//			Log.i("horizontallistview", "removeNonVisibleItems right");
			mRightViewIndex--;
			child = getChildAt(getChildCount()-1);
		}
	}
	
	private void positionItems(final int dx) {
		if(getChildCount() > 0){
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int childWidth = getWidth(child);
				int childHeight = getHeight(child);
				int top = (this.getMeasuredHeight() - childHeight)/2;
				child.layout(left, top, left + childWidth, top+childHeight);
				left += childWidth; 
				mLastChildRight = left;
			}
		}
	}
	
	public synchronized void scrollTo(int x) {
		synchronized(HorizontialListView.this){
			mScroller.startScroll(mNextX, 0, x - mNextX, 0, 500);
		}
		//computeScroll();
		requestLayout();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mIsStartScrolling = false;
		}
		if (ev.getAction() == MotionEvent.ACTION_CANCEL && mIsStartScrolling) {
			ev.setAction(MotionEvent.ACTION_UP);
		}
		if(ev.getAction() == MotionEvent.ACTION_UP){
			mIsUp = true;
			requestLayout();
		}else{
			mIsUp = false;
			mRequestScrollTo = false;
		}
		boolean handled = mGesture.onTouchEvent(ev);
		return handled;
	}
	
	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
		Log.d("action_index", "fling");
		synchronized(HorizontialListView.this){
			mScroller.fling(mNextX, 0, (int)-velocityX, 0, mMinX, mMaxX, 0, 0);
		}

//		Log.i("horizontallistview", "onFling requestLayout");
		requestLayout();
		
		return true;
	}
	
	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}
	
	private class MyUIThread implements Runnable{
		int mX;
		public MyUIThread(int x){
			this.mX = x;
		}
		
		@Override
		public void run() {
			scrollTo(mX);
		}
		
	}
	
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontialListView.this.onDown(e);
		}
		

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(velocityX>2000){
				velocityX = 2000;
			}else if(velocityX < -2000){
				velocityX = -2000;
			}
			mIsScrolling = true;
			return HorizontialListView.this.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			synchronized(HorizontialListView.this){
				//如果已到达最大区域，则将移动距离设为一半
				if(mNextX < mMaxChildX && mNextX + (int)distanceX > mMaxChildX ){
					mNextX = mMaxChildX + (mNextX + (int)distanceX - mMaxChildX)/2;
				}else if(mNextX > mMaxChildX){
					mNextX += (int)distanceX/2;
				}else if(mNextX > 0 && mNextX + (int)distanceX < 0 ){
					mNextX = 0 + (mNextX + (int)distanceX - 0)/2;
				}else if(mNextX < 0){
					mNextX += (int)distanceX/2;
				}else{
					mNextX += (int)distanceX;	
				}
				mIsScrolling = true;
			}
			mIsStartScrolling = true;
//			Log.i("horizontallistview", "onScroll requestLayout");
			requestLayout();
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Rect viewRect = new Rect();
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if(viewRect.contains((int)e.getX(), (int)e.getY())){
					if(mOnItemClicked != null){
						mOnItemClicked.onItemClick(HorizontialListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(HorizontialListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					break;
				}
				
			}
			return true;
		}


		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Rect viewRect = new Rect();
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set(left, top, right, bottom);
				if(viewRect.contains((int)e.getX(), (int)e.getY())){
					if(mOnDoubleTabed != null){
						mOnDoubleTabed.onDoubleTap(HorizontialListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					break;
				}
				
			}
			return true;
		}	
		
	};

	public interface OnLayoutCompletedListener{
		public void onLayoutCompleted(int offset, boolean isScrolling);
	}
	
	public interface OnDoubleTapListener{
		public void onDoubleTap(AdapterView<?> arg0, View arg1, int arg2, long arg3);
	}

	
}

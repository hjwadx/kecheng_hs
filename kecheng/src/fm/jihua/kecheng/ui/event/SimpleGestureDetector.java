package fm.jihua.kecheng.ui.event;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ListView;

public class SimpleGestureDetector extends SimpleOnGestureListener{ 
	
	private int REL_SWIPE_MIN_DISTANCE; 
    private int REL_SWIPE_MAX_OFF_PATH;
    private int REL_SWIPE_THRESHOLD_VELOCITY;
    
    
    public interface OnFlingListener{
		public void onRTLFling();
		public void onLTRFling();
		public void onItemClick(int pos);
		public void onLongPress(int pos);
	}

	Context mContext;
	ListView mListView;
	private OnFlingListener mFlingListener;
    
    public SimpleGestureDetector(Context context, ListView lv){
    	mContext = context;
    	mListView = lv;
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
	    REL_SWIPE_MIN_DISTANCE = (int)(120.0f * dm.densityDpi / 160.0f + 0.5); 
	    REL_SWIPE_MAX_OFF_PATH = (int)(250.0f * dm.densityDpi / 160.0f + 0.5);
	    REL_SWIPE_THRESHOLD_VELOCITY = (int)(200.0f * dm.densityDpi / 160.0f + 0.5);
    }
    
    public void setOnFlingListener (OnFlingListener listener) {
		this.mFlingListener = listener;
	}
		
    @Override 
    public boolean onSingleTapUp(MotionEvent e) {
        ListView lv = mListView;
        int pos = lv.pointToPosition((int)e.getX(), (int)e.getY());
        mFlingListener.onItemClick(pos);
        return false;
    }
    
    

    @Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
    	super.onLongPress(e);
    	ListView lv = mListView;
    	int pos = lv.pointToPosition((int)e.getX(), (int)e.getY());
    	mFlingListener.onLongPress(pos);
	}

	@Override 
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { 
		if (e1 == null || e2 == null) {
			return false;
		}
        if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH) 
            return false; 
        if(e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE && 
            Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) { 
        	mFlingListener.onRTLFling();
        }  else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE && 
            Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) { 
        	mFlingListener.onLTRFling(); 
        } 
        return false; 
    } 
} 
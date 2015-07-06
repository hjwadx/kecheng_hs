package fm.jihua.kecheng.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import fm.jihua.kecheng.ui.activity.BaseMenuActivity;

public class BaseFragment extends fm.jihua.common.ui.BaseFragment implements OnClickListener {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		onShow();
	}

	public void onShow(){
		if (getActivity() instanceof BaseMenuActivity) {
			((BaseMenuActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}
		initTitlebar();
	}
	
	public void initTitlebar(){
		
	}
	
	@Override
	public void onClick(View v) {
		
	}
	
	public boolean onBackPressed(){
		return false;
	}
}
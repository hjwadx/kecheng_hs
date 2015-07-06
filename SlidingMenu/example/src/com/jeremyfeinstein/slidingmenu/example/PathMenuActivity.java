package com.jeremyfeinstein.slidingmenu.example;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class PathMenuActivity extends SlidingFragmentActivity {

	protected ListFragment mFrag;
	private View mCustomView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
		final ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(false);
		bar.setDisplayShowHomeEnabled(true);
//		mCustomView = getLayoutInflater().inflate(R.layout.titlebar_main, null);
//        // Configure several action bar elements that will be toggled by display options.
//        bar.setCustomView(mCustomView,
//               new com.actionbarsherlock.app.ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
////        int newGravity = 0;
////        switch (lp.gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
////            case Gravity.START:
////                newGravity = Gravity.CENTER_HORIZONTAL;
////                break;
////            case Gravity.CENTER_HORIZONTAL:
////                newGravity = Gravity.END;
////                break;
////            case Gravity.END:
////                newGravity = Gravity.START;
////                break;
////        }
////        lp.gravity = lp.gravity & ~Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK | newGravity;
//        bar.setCustomView(mCustomView);
//        bar.setDisplayShowCustomEnabled(true);
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			mFrag = new SampleListFragment();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (ListFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//		mCustomView.findViewById(R.id.menu).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				toggle();
//				
//			}
//		});
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			toggle();
			return true;
		}
		return false;
	}
}

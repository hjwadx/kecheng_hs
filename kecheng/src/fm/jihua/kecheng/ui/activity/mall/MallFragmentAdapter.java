package fm.jihua.kecheng.ui.activity.mall;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 *	@date	2013-8-29
 *	@introduce	
 */
public class MallFragmentAdapter extends FragmentPagerAdapter {

	public MallFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return getFragmentByPosition(position);
	}

	@Override
	public int getCount() {
		return 2;
	}
	
	private Fragment getFragmentByPosition(int position){
		switch (position) {
		case MallConst.POSITION_PASTE:
			return new MallStickerSetFragment();
		case MallConst.POSITION_SKIN:
			return new MallThemeFragment();
		}
		return null;
	}

}

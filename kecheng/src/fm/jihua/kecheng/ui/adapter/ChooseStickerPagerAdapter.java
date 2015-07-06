package fm.jihua.kecheng.ui.adapter;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;
import fm.jihua.kecheng.ui.fragment.ChooseStickerFragment;

/**
 * @date 2013-8-29
 * @introduce
 */
public class ChooseStickerPagerAdapter extends FragmentStatePagerAdapter {

	private List<StickerSet> stickerSets;
	
	public static final String FRAGMENT_ARGUMENT = "INFO";
	public static final String FRAGMENT_POSITION = "position";

	public ChooseStickerPagerAdapter(FragmentManager fm, List<StickerSet> stickerSets) {
		super(fm);
		this.stickerSets = stickerSets;
	}

	@Override
	public Fragment getItem(int position) {
		ChooseStickerFragment pasteChoiceFragment = new ChooseStickerFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(FRAGMENT_ARGUMENT, stickerSets.get(position));
		bundle.putInt(FRAGMENT_POSITION, position);
		pasteChoiceFragment.setArguments(bundle);
		return pasteChoiceFragment;
	}

	@Override
	public int getCount() {
		return stickerSets.size();
	}
	
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}

}

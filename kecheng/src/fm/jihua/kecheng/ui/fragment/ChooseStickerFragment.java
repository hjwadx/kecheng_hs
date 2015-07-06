package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.StickerSetProduct;
import fm.jihua.kecheng.rest.entities.sticker.Sticker;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;
import fm.jihua.kecheng.ui.activity.mall.StickerSetProductActivity;
import fm.jihua.kecheng.ui.activity.sticker.ChooseStickerActivity;
import fm.jihua.kecheng.ui.activity.sticker.ChooseStickerAdapter;
import fm.jihua.kecheng.ui.adapter.ChooseStickerPagerAdapter;
import fm.jihua.kecheng.utils.CommonUtils;

/**
 * @date 2013-8-29
 * @introduce
 */
public class ChooseStickerFragment extends Fragment {

	StickerSet stickerSet;
	int locationPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_pastechoice_gridview, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle bundle = getArguments();
		stickerSet = (StickerSet) bundle.getSerializable(ChooseStickerPagerAdapter.FRAGMENT_ARGUMENT);
		locationPosition = bundle.getInt(ChooseStickerPagerAdapter.FRAGMENT_POSITION, 0);
		GridView gridView = (GridView) view.findViewById(R.id.paster_gridview);
		List<Sticker> stickers = new ArrayList<Sticker>();
		for (Sticker sticker : stickerSet.stickers) {
			if (sticker.isValidForCategory(Sticker.CATEOGRY_COURSE)) {
				stickers.add(sticker);
			}
		}
		ChooseStickerAdapter pasterChoiceAdapter = new ChooseStickerAdapter(getActivity(), stickers.toArray(new Sticker[stickers.size()]));
		gridView.setAdapter(pasterChoiceAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ChooseStickerActivity activity = (ChooseStickerActivity) getActivity();
				Intent intent = activity.getIntent();
				Sticker sticker = stickerSet.stickers[position];
				if (sticker.lock) {
					List<StickerSetProduct> products = StickerSetProduct.getMyLocalProducts();
					StickerSetProduct product = (StickerSetProduct) CommonUtils.findByParam(products, "id", sticker.product_id);
					if (product != null) {
						Intent newIntent = new Intent(getActivity(), StickerSetProductActivity.class);
						newIntent.putExtra(StickerSetProductActivity.INTENT_KEY, product);
						startActivity(newIntent);
					}
				}else {
					intent.putExtra(ChooseStickerActivity.INTENT_PASTER, sticker);
					intent.putExtra(ChooseStickerPagerAdapter.FRAGMENT_POSITION, locationPosition);
					activity.setResult(ChooseStickerActivity.RESULT_OK, intent);
					activity.finish();
				}
			}
		});
	}
}

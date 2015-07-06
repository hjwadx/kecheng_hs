package fm.jihua.kecheng.ui.activity.mall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;

import com.commonsware.cwac.endless.LoadMoreAdapter;

import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.Banner;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CachedImageView;

/**
 * @date 2013-8-29
 * @introduce
 */
public class MallStickerSetFragment extends Fragment {

	private ListView listView;
	List<Product> pasters = new ArrayList<Product>();
	LoadMoreAdapter adapter;
	StickerSetProductAdapter pasterAdapter;
	Banner banner;
	CachedImageView banner_imageView;
	public static final int STICKER_SET_PRODUCT = 1000;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_mall_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViews(view);
		initViews();
	}
	
	public void setData(List<Product> pasters, Banner banner, boolean more){
		this.pasters = pasters;
		this.banner = banner;
//		initViews();
		if(banner != null && banner.image_url != null){
			banner_imageView.setImageURI(Uri.parse(this.banner.image_url));
		}else {
			banner_imageView.setVisibility(View.GONE);
		}
		pasterAdapter.setData(pasters);
		if(more){
			adapter.restartAppending();
			adapter.onDataReady();
		} else {
			adapter.stopAppending();
			adapter.onDataReady();
		}
	}

	private void findViews(View view) {
		listView = (ListView) view.findViewById(R.id.mall_list);
	}

	private void initViews() {
		addBanner();
		adapter = new LoadMorePastersAdapter(getActivity(), pasters);
		listView.setDivider(null);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent =new Intent(getActivity(), StickerSetProductActivity.class);
				intent.putExtra("PRODUCT", pasters.get(position - 1));
				startActivityForResult(intent, STICKER_SET_PRODUCT);
			}
		});
	}
	
	void addBanner(){
		banner_imageView = new CachedImageView(getActivity());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		banner_imageView.setLayoutParams(layoutParams);
		banner_imageView.setScaleType(ScaleType.CENTER_CROP);
//		banner_imageView.setImageURI(Uri.parse(this.banner.image));
		banner_imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Hint.showTipsShort(getActivity(), "banner  :" + MallStickerSetFragment.this.banner.product_id);
				Product product = null;
				for(Product pro : pasters){
					if(pro.id == banner.product_id){
						product = pro;
						break;
					}
				}
				Intent intent =new Intent(getActivity(), StickerSetProductActivity.class);
				intent.putExtra("PRODUCT", product);
				startActivityForResult(intent, STICKER_SET_PRODUCT);
			}
		});
		listView.addHeaderView(banner_imageView);
	}
	
	class LoadMorePastersAdapter extends LoadMoreAdapter {

		LoadMorePastersAdapter(Activity context, List<Product> products) {
			super(context, new StickerSetProductAdapter(context, products), R.layout.pending_large, R.layout.load_more_large);
			pasterAdapter = (StickerSetProductAdapter) getWrappedAdapter();
			setRunInBackground(false);
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			getMore();
			return true;
		}

		@Override
		protected void appendCachedData() {
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case STICKER_SET_PRODUCT:
			Product product = (Product) data.getSerializableExtra("PRODUCT");
			for(Product paster : pasters){
				if(paster.id == product.id){
					paster.addStatus(Product.STATUS_PURCHASED);
					break;
				}
			}
			pasterAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
	
	public void refresh(){
		pasterAdapter.notifyDataSetChanged();
		adapter.onDataReady();
	}

	void getMore(){
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				listView.post(new Runnable() {
					@Override
					public void run() {
						adapter.stopAppending();
						adapter.onDataReady();
					}
				});
			}
		});
	}
}

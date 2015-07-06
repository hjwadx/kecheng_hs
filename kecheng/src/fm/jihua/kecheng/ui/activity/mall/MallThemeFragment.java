package fm.jihua.kecheng.ui.activity.mall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.commonsware.cwac.endless.LoadMoreAdapter;
import com.google.gson.Gson;
import com.mozillaonline.providers.DownloadManager;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.pay.AlipayTool;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.mall.MyProductsResult;
import fm.jihua.kecheng.rest.entities.mall.PaymentlResult;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.mall.ThemeProduct;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.rest.service.GsonRequest;
import fm.jihua.kecheng.ui.activity.mall.DownloadSpirit.OnDownloadListener;
import fm.jihua.kecheng.ui.activity.mall.PayUtils.CATEGORY;
import fm.jihua.kecheng.ui.activity.mall.PayUtils.RefreshListener;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.MallDownloadDialog;

/**
 * @date 2013-8-29
 * @introduce
 */
public class MallThemeFragment extends Fragment implements RefreshListener {

	LoadMoreAdapter adapter;
	ThemeProductAdapter skinAdapter;

	private ListView listView;
	List<Product> skins = new ArrayList<Product>();
	PayUtils payUtils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_mall_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViews(view);
		initViews();
		payUtils = new PayUtils(getActivity(), CATEGORY.THEME, this);
	}

	public void setData(List<Product> skins, boolean more) {
		this.skins = skins;
		skinAdapter.setData(skins);
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
		adapter = new LoadMoreSkinsAdapter(getActivity(), skins);
		listView.setDivider(null);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//下载或者选择
				Product product = skins.get(position);
				switch (product.getStatusLocal()) {
				case Product.FREE_AND_PAYMENT:
				case Product.PAY_AND_PAYMENT:
				case Product.PAY_AND_UNPAYMENT:
				case Product.FREE_AND_UNPAYMENT:
					payUtils.downloadOrPay(product);
					break;
				case Product.EXIST:
					if (!product.name.equals(App.getInstance()
							.getCurrentStyleName())) {
						App.getInstance().setCurrentStyleName(product.name);
						onRefresh();
						Hint.showTipsShort(getActivity(), product.name + "使用成功");
						sendCurrentThemeChangedBroadcast();
					}
					break;
				default:
					break;
				}
			}
		});
	}
	
	void sendCurrentThemeChangedBroadcast() {
		Intent intent = new Intent(MallFragment.BRODCAST_CURRENT_THEME_CHANGED);
		getActivity().sendBroadcast(intent);
	}

	class LoadMoreSkinsAdapter extends LoadMoreAdapter {

		LoadMoreSkinsAdapter(Activity context, List<Product> products) {
			super(context, new ThemeProductAdapter(context, products), R.layout.pending_large, R.layout.load_more_large);
			skinAdapter = (ThemeProductAdapter) getWrappedAdapter();
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

	void getMore() {
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
	
	@Override
	public void onRefresh() {
		skinAdapter.notifyDataSetChanged();
		adapter.onDataReady();
	}
}

package fm.jihua.kecheng.ui.activity.mall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.mozillaonline.providers.DownloadManager;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.pay.AlipayTool;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.mall.MyProductsResult;
import fm.jihua.kecheng.rest.entities.mall.PaymentlResult;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.mall.StickerSetProduct;
import fm.jihua.kecheng.rest.entities.mall.ThemeProduct;
import fm.jihua.kecheng.rest.entities.sticker.Sticker;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.rest.service.GsonRequest;
import fm.jihua.kecheng.rest.service.RestService;
import fm.jihua.kecheng.ui.activity.mall.DownloadSpirit.OnDownloadListener;
import fm.jihua.kecheng.ui.widget.MallDownloadDialog;
import fm.jihua.kecheng.utils.CommonUtils;

public class PayUtils {
	Product waitingToPayProduct;
	Product downloadProduct;
	DataAdapter dataAdapter;
	Activity activity;
	public final static int MAX_WAIT_ALIPAY_TIME = 10000;
	long startTime;
	CATEGORY category;
	RefreshListener refreshListener;
	
	public enum CATEGORY{
		STICKER, THEME
	}
	
	public PayUtils(Activity activity, CATEGORY category, RefreshListener refreshListener) {
		this.activity = activity;
		dataAdapter = new DataAdapter(activity, dataCallback);
		this.category = category;
		this.refreshListener = refreshListener;
	}
	
	public interface RefreshListener{
		void onRefresh();
	}
	
	DataCallback dataCallback = new DataCallback() {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_NOT_CARE:
				UIUtil.unblock(activity);
				PaymentlResult result = (PaymentlResult) msg.obj;
				if(result != null && result.success){
					if (result.result != null) {
						AlipayTool alipayTool = new AlipayTool(activity, new PayDataCallback());
						alipayTool.pay(downloadProduct, result.result.order_id, result.result.notify_url);
					}else {
						saveMyProducts();
						downloadProduct();
					}
				}
				break;
			default:
				break;
			}
		}
	};
	
	DataCallback afterPayCallback = new DataCallback() {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_NOT_CARE:
				PaymentlResult result = (PaymentlResult) msg.obj;
				if(result != null && result.success){
					if (result.result != null) {
						if (System.currentTimeMillis() - startTime < MAX_WAIT_ALIPAY_TIME) {
							new Handler().postDelayed(new Runnable() {
								
								@Override
								public void run() {
									if (waitingToPayProduct != null) {
										(new DataAdapter(activity, afterPayCallback)).request(RestService.get().payment(waitingToPayProduct.id, "alipay"));
									}
								}
							}, 1000);
						}
					}else {
						UIUtil.unblock(activity);
						waitingToPayProduct = null;
						saveMyProducts();
						downloadProduct();
					}
				}
				break;
			default:
				break;
			}
		}
	};
	
	class PayDataCallback implements DataCallback{

		@Override
		public void callback(Message msg) {
			boolean result = (Boolean) msg.obj;
			if (result) {
				startTime = System.currentTimeMillis();
				if (waitingToPayProduct != null) {
					UIUtil.block(activity);
					(new DataAdapter(activity, afterPayCallback)).request(RestService.get().payment(waitingToPayProduct.id, "alipay"));
				}
			}else {
				UIUtil.unblock(activity);
			}
		}
		
	}
	
	private void downloadProduct(){
		this.refreshListener.onRefresh();
		showDialog();
	}
	
	private void showDialog() {
		
		final DownloadSpirit downloadSpirit = downloadProduct.getDownloadSpirit();
		final Dialog dialogDown = new Dialog(activity, R.style.MyDialog);
		final MallDownloadDialog downloadDialog = new MallDownloadDialog(activity, MallDownloadDialog.TYPE_SKIN);
		
		downloadDialog.setData(downloadProduct);
		downloadDialog.setDialogOnClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadSpirit.cancle();
				dialogDown.cancel();
			}
		});
		dialogDown.setContentView(downloadDialog);
		dialogDown.setCancelable(false);
		dialogDown.show();

		
		downloadSpirit.start(new OnDownloadListener() {

			@Override
			public void statusChanged(int statusType, int precent, int current, int total) {
				downloadDialog.updateProgress(precent, current, total);
				if(statusType == DownloadSpirit.STATUS_COMPLETED || statusType == DownloadManager.STATUS_FAILED || statusType == DownloadSpirit.STATUS_UNZIP_FAILED){
					dialogDown.cancel();
				}
				if(statusType == DownloadSpirit.STATUS_COMPLETED){
					refreshListener.onRefresh();
					sendBroadcast();
				}
			}
		});
	}
	
	void saveMyProducts(){
		downloadProduct.addStatus(Product.STATUS_PURCHASED);
		MyProductsResult result1 = App.getInstance().getObjectValue(DataAdapter.MY_PRODUCTS_KEY, MyProductsResult.class);
		boolean exist = false;
		if (category == CATEGORY.STICKER) {
			List<StickerSetProduct> list = new ArrayList<StickerSetProduct>(Arrays.asList(result1.stickers));
			exist = CommonUtils.findById(list, downloadProduct.id) != null;
			if (!exist) {
				StickerSet stickerSet = ((StickerSetProduct)downloadProduct).getItem();
				if (((StickerSetProduct)downloadProduct).stickers == null && stickerSet != null) {
					((StickerSetProduct)downloadProduct).stickers = stickerSet.stickers;
				}
				list.add((StickerSetProduct) downloadProduct);
				result1.stickers = list.toArray(new StickerSetProduct[list.size()]);
			}
		}else if(category == CATEGORY.THEME){
			List<ThemeProduct> list = new ArrayList<ThemeProduct>(Arrays.asList(result1.themes));
			exist = CommonUtils.findById(list, downloadProduct.id) != null;
			if (!exist) {
				list.add((ThemeProduct) downloadProduct);
				result1.themes = list.toArray(new ThemeProduct[list.size()]);
			}
		}
		if (!exist) {
			Gson gson = GsonRequest.getDefaultGson();
			App.getInstance().putValue(DataAdapter.MY_PRODUCTS_KEY, gson.toJson(result1));
		}
	}
	
	void sendBroadcast(){
		String action = "";
		switch (category) {
		case STICKER:
			action = MallFragment.BRODCAST_STICKER_CHANGED;
			break;
		case THEME:
			action = MallFragment.BRODCAST_THEME_CHANGED;
			break;
		default:
			break;
		}
		Intent intent=new Intent(action);
	    activity.sendBroadcast(intent); 
	}
	
	public void downloadOrPay(Product product){
		switch (product.getStatusLocal()) {
		case Product.FREE_AND_PAYMENT:
		case Product.PAY_AND_PAYMENT:
			downloadProduct = product;
			showDialog();
			break;
		case Product.PAY_AND_UNPAYMENT:
		case Product.FREE_AND_UNPAYMENT:
			downloadProduct = product;
			UIUtil.block(activity);
			waitingToPayProduct = product;
			dataAdapter.request(RestService.get().payment(product.id, "alipay"));
			break;
		}
	}
}

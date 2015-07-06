package fm.jihua.kecheng.ui.activity.mall;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.mozillaonline.providers.DownloadManager;

import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.ProductManager;
import fm.jihua.kecheng.rest.entities.mall.StickerSetProduct;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.mall.DownloadSpirit.OnDownloadListener;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.MallDownloadDialog;
import fm.jihua.kecheng.ui.widget.ShadowTextView;

/**
 * @date 2013-8-29
 * @introduce
 */
public class MyStickerSetProductsActivity extends BaseActivity {

	private MallMineStickerSetAdapter adapter;
	DragSortListView listView;
	private ShadowTextView btn_returnall;
	private TextView textHint;
	private TextView textEmpty;
	View view;

	public static final int STATUS_NORMAL = 101;
	public static final int STATUS_EDIT = 102;
	
	List<StickerSetProduct> products;
	List<StickerSetProduct> productsCopy;
	
	boolean changed;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_mall_mine_paste);
		initTitleBar();
		findViews();
		initViews();
	}

	private void initTitleBar() {
		setTitle("我的贴纸");
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_edit);
		getKechengActionBar().getRightButton().setOnClickListener(rightBtnClickListener);
		getKechengActionBar().getLeftButton().setOnClickListener(leftBtnClickListener);
	}
	
	private void findViews(){
//		btn_returnall = (ShadowTextView) findViewById(R.id.btn_returnback);
		listView = (DragSortListView) findViewById(android.R.id.list);
		textHint=(TextView) findViewById(R.id.hint_text);
		textEmpty = (TextView) findViewById(R.id.empty_text);
	}
	
	private void initViews(){
		productsCopy = new ArrayList<StickerSetProduct>();
		products = StickerSetProduct.getMyProducts();
		adapter = new MallMineStickerSetAdapter(this,products);
		view = LayoutInflater.from(this).inflate(R.layout.root_button, null);
		btn_returnall = (ShadowTextView) view.findViewById(R.id.btn_returnback);
		btn_returnall.setText("下载全部贴纸");
		listView.addFooterView(view);

		listView.setAdapter(adapter);

		listView.setDropListener(onDrop);
		listView.setRemoveListener(remove);
		listView.setOnItemClickListener(itemClickListener);
		
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reloadList();
			}
		});
		btn_returnall.setDrawableLeft(null);

		if (products == null || products.size() == 0) {
			btn_returnall.setVisibility(View.GONE);
			textEmpty.setVisibility(View.VISIBLE);
			getKechengActionBar().getRightButton().setVisibility(View.GONE);
		} else {
			btn_returnall.setVisibility(View.VISIBLE);
			textEmpty.setVisibility(View.GONE);
		}
		
	}

	DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				DragSortListView list = listView;
				adapter.changeNewPosition(from, to);
				list.moveCheckState(from, to);
				Log.d("DSLV", "Selected item is " + list.getCheckedItemPosition());
			}
		}
	};

	DragSortListView.RemoveListener remove = new DragSortListView.RemoveListener() {

		@Override
		public void remove(int which) {
			products.remove(which);
			adapter.notifyDataSetChanged();
		}
	};

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(MyStickerSetProductsActivity.this, StickerSetProductActivity.class);
			intent.putExtra("PRODUCT", products.get(position));
			startActivity(intent);
		}
	};
	
	OnClickListener leftBtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			if (adapter.isEditStatus()) {
				view.setVisibility(View.VISIBLE);
				adapter.setStatus(STATUS_NORMAL);
				getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_edit);
				getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_back);
				listView.setOnItemClickListener(itemClickListener);
				
				btn_returnall.setVisibility(View.VISIBLE);
				textHint.setVisibility(View.GONE);
				resetPrevView();
			} else {
				finish();
			}
		}
	};
	
	OnClickListener rightBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			if (adapter.isEditStatus()) {
				getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_edit);
				getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_back);
				listView.setOnItemClickListener(itemClickListener);
				
				btn_returnall.setVisibility(View.VISIBLE);
				textHint.setVisibility(View.GONE);
				
				changePasteOrder();
				view.setVisibility(View.VISIBLE);
				adapter.setStatus(STATUS_NORMAL);
			} else {
				view.setVisibility(View.GONE);
				adapter.setStatus(STATUS_EDIT);
				getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
				getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
				listView.setOnItemClickListener(null);
				
				btn_returnall.setVisibility(View.GONE);
				textHint.setVisibility(View.VISIBLE);
				
				productsCopy.clear();
				productsCopy.addAll(products);
			}
		}
	};
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			getKechengActionBar().getLeftButton().performClick();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private void changePasteOrder() {
		List<String> order = new ArrayList<String>();
		for (StickerSetProduct product : products) {
			if(product.exists()){
				order.add(product.getOrderPropertyValue());
			}
		}
		if(productsCopy.size() > products.size()){
			for(StickerSetProduct product : productsCopy){
				if(!products.contains(product)){
					product.delete();
//					Hint.showTipsShort(MyStickerSetProductsActivity.this, "deleteProductFromSD = " + product.name);
				}
			}
		}
		new ProductManager(StickerSetProduct.class).setOrder(order);
		sendStickerChangedBroadcast();
		products.clear();
		products.addAll(StickerSetProduct.getMyProducts());
		adapter.notifyDataSetChanged();
		changed = true;
	}
	
	private void resetPrevView(){
		products.clear();
		products.addAll(productsCopy);
		for (StickerSetProduct info : products) {
			info.resetStatus();
		}
		adapter.notifyDataSetChanged();
	}
	
	List<StickerSetProduct> productNeedDownload = new ArrayList<StickerSetProduct>();
	private void reloadList(){
		productNeedDownload.clear();
		isCancled = false;
		for (StickerSetProduct product : products) {
			if (!product.exists()) {
				productNeedDownload.add(product);
			}
		}
		check2Download();
	}
	
	boolean isCancled = false;
	private void check2Download(){
		if(productNeedDownload.size() > 0 && !isCancled){
			downloadPaste(productNeedDownload.get(0));
		}else{
			Hint.showTipsShort(MyStickerSetProductsActivity.this, "没有需要下载的贴纸");
			adapter.notifyDataSetChanged();
		}
	}
	
	public void downloadPaste(final StickerSetProduct stickersInfoProduct){
		final DownloadSpirit downloadSpirit = stickersInfoProduct.getDownloadSpirit();
		final Dialog dialogDown = new Dialog(this, R.style.MyDialog);
		final MallDownloadDialog downloadDialog = new MallDownloadDialog(this, MallDownloadDialog.TYPE_SKIN);
		
		downloadDialog.setData(stickersInfoProduct);
		downloadDialog.setDialogOnClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				isCancled = true;
				downloadSpirit.cancle();
				dialogDown.cancel();
				changePasteOrder();
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
					if (productNeedDownload.size() > 0) {
						productNeedDownload.remove(stickersInfoProduct);
						if(productNeedDownload.size() > 0 && !isCancled){
							downloadPaste(productNeedDownload.get(0));
						}
					}else{
//						Hint.showTipsShort(MyStickerSetProductsActivity.this, "已恢复完成");
					}
					changePasteOrder();
				}
			}
		});
	}
	
	void sendStickerChangedBroadcast(){
		Intent intent=new Intent(MallFragment.BRODCAST_STICKER_CHANGED);
	    sendBroadcast(intent); 
	}
	
	@Override
	public void finish() {
		if(changed){
			Intent intent = new Intent();
			intent.putExtra(MallMineActivity.STICKERS_CHANGED, changed);
			setResult(RESULT_OK, intent);
		}
		super.finish();
	}
}

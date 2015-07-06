package fm.jihua.kecheng.ui.activity.mall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.mall.MyProductsResult;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.rest.service.RestService;
import fm.jihua.kecheng.ui.activity.BaseActivity;

/**
 * @date 2013-8-29
 * @introduce
 */
public class MallMineActivity extends BaseActivity {
	

	DataAdapter dataAdapter;
	List<Product> pasters = new ArrayList<Product>();
	List<Product> skins = new ArrayList<Product>();
	public final static int EDIT_MAIN_PRODUCTS = 100;
	public final static String STICKERS_CHANGED = "stickers_changed";
	public final static String THEME_CHANGED = "themes_changed";
	boolean stickers_changed;
	boolean themes_changed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_mall_mine);
		
		dataAdapter = new DataAdapter(this, dataCallback);
		dataAdapter.request(RestService.get().getMineProducts(), DataAdapter.MESSAGE_GET_MY_PRODUCTS);

		initTitleBar();
		initListener();
	}

	private void initTitleBar() {
		setTitle("购买管理");
	}

	private void initListener() {
		findViewById(R.id.mallmine_paste).setOnClickListener(onClickListener);
		findViewById(R.id.mallmine_skin).setOnClickListener(onClickListener);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.mallmine_paste:
				intent = new Intent(MallMineActivity.this, MyStickerSetProductsActivity.class);
				intent.putExtra("PRODUCTS", new ArrayList<Product>(pasters));
				startActivityForResult(intent, EDIT_MAIN_PRODUCTS);
				break;
			case R.id.mallmine_skin:
				intent = new Intent(MallMineActivity.this, MyThemeProductsActivity.class);
				intent.putExtra("PRODUCTS", new ArrayList<Product>(skins));
				startActivityForResult(intent, EDIT_MAIN_PRODUCTS);
				break;
			default:
				break;
			}
		}
	};
	
	DataCallback dataCallback = new DataCallback() {

		@Override
		public void callback(Message msg) {
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_MY_PRODUCTS:
				MyProductsResult result = (MyProductsResult) msg.obj;
				if (result != null && result.success) {
					pasters.addAll(Arrays.asList(result.stickers));
					skins.addAll(Arrays.asList(result.themes));
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void finish() {
		if(stickers_changed || themes_changed){
			Intent intent = new Intent();
			intent.putExtra(STICKERS_CHANGED, stickers_changed);
			intent.putExtra(THEME_CHANGED, themes_changed);
			setResult(RESULT_OK, intent);
		}
		super.finish();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case MallMineActivity.EDIT_MAIN_PRODUCTS:
			if(data.getBooleanExtra(STICKERS_CHANGED, false)){
				stickers_changed = true;
			}
            if(data.getBooleanExtra(THEME_CHANGED, false)){
            	themes_changed = true;
			}
			break;
		default:
			break;
		}
	}
}

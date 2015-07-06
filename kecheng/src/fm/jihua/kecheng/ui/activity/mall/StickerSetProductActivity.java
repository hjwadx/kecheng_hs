package fm.jihua.kecheng.ui.activity.mall;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.mall.PayUtils.CATEGORY;
import fm.jihua.kecheng.ui.activity.mall.PayUtils.RefreshListener;
import fm.jihua.kecheng.ui.activity.setting.TextViewerActivity;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.ui.widget.CachedImageView.GetBitmapCallbackListener;
import fm.jihua.kecheng.ui.widget.ShadowTextView;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;
import fm.jihua.kecheng_hs.R;

/**
 * @date 2013-8-29
 * @introduce
 */
public class StickerSetProductActivity extends BaseActivity implements RefreshListener{

	LinearLayout layoutShare;
	ShadowTextView download;
	CachedImageView banner;
	CachedImageView preview;
	CachedImageView avatar;
	Product product;
	
	public static final String INTENT_KEY = "PRODUCT";
	
	boolean downloadedOrPay;

	DataAdapter dataAdapter;
	PayUtils payUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_mall_pasteinfo);
		product = (Product) getIntent().getSerializableExtra(INTENT_KEY);
		payUtils = new PayUtils(this, CATEGORY.STICKER, this);
		initTitlebar();
		findViews();
		initViews();
	}

	
	private void initTitlebar(){
		if (product != null) {
			setTitle(product.name);
		}
	}

	private void findViews() {
		layoutShare = (LinearLayout) findViewById(R.id.share);
		download = (ShadowTextView) findViewById(R.id.download);
		banner = (CachedImageView) findViewById(R.id.banner);
		preview = (CachedImageView) findViewById(R.id.preview);
		avatar = (CachedImageView) findViewById(R.id.avatar);
	}
	
	
	private void initViews(){
//		banner.setImageURI(Uri.parse("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcS8jlmEv1a6P2X11DseWdLhfRyca7PI85NP5rNelnItlRs0YqOw"));
		if(product != null){
			WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
			int width = Compatibility.getWidth(windowManager.getDefaultDisplay());
			int height = (504 * width / 720);  
			banner.setScaleType(ScaleType.FIT_XY);
			banner.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
			((TextView)findViewById(R.id.name)).setText(product.name);
			if(product.getStatus(Product.STATUS_EVENT)){
				findViewById(R.id.price).setVisibility(View.GONE);
				findViewById(R.id.access).setVisibility(View.VISIBLE);
			} else {
				((TextView)findViewById(R.id.price)).setText(product.getPriceString());
			}
			((TextView)findViewById(R.id.sticker_presentation)).setText(product.introduction);
			banner.setImageURI(Uri.parse(product.banner_url));
			preview.setOnGetBitmapClickListener(new GetBitmapCallbackListener() {
				@Override
				public void onClick(int w, int h) {
					WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
					int width = Compatibility.getWidth(windowManager.getDefaultDisplay())  - ImageHlp.changeToSystemUnitFromDP(preview.getContext(), 26);
					int height = (h * width / w);  
					preview.setScaleType(ScaleType.FIT_XY);
					preview.setLayoutParams(new LinearLayout.LayoutParams(width, height));
				}
			});
			preview.setImageURI(Uri.parse(product.screen_shot_url));
			avatar.setCorner(true);
			avatar.setImageURI(Uri.parse(product.author_icon));
			((TextView)findViewById(R.id.author)).setText(product.author);
			((TextView)findViewById(R.id.author_presentation)).setText(product.about_author);
			initButton();
		}
		findViewById(R.id.share).setOnClickListener(onClickListener);
		download.setImageIcon(-1);
	}
	
	void initButton(){
		download.setText(getButtonText(product));
		if(product.exists()){
			download.setBackgroundColor(getResources().getColor(R.color.textcolor_b2));
		}
	}
	
	String getButtonText(Product product){
		String str = "";
		switch (product.getStatusLocal()) {
		case Product.EXIST:
			str = "已下载";
			break;
		case Product.PAY_AND_PAYMENT:
			str = "下载（已购买）";
			break;
		case Product.PAY_AND_UNPAYMENT:
			str = "购买";
			break;
		case Product.FREE_AND_PAYMENT:
		case Product.FREE_AND_UNPAYMENT:
			str = "下载";
			break;
		default:
			break;
		}
		return str;
	}
	
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(product != null){
				switch (product.getStatusLocal()) {
				case Product.FREE_AND_PAYMENT:
				case Product.PAY_AND_PAYMENT:
				case Product.FREE_AND_UNPAYMENT:
				case Product.PAY_AND_UNPAYMENT:
					payUtils.downloadOrPay(product);
					break;
				default:
					break;
				}
			}
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.access:
			Intent intent = new Intent(StickerSetProductActivity.this, TextViewerActivity.class);
			intent.putExtra(Const.INTENT_TEXT_CONTENT, product.get_way);
			intent.putExtra(Const.INTENT_TITLE_CONTENT, product.name + "获取方式");
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onRefresh() {
		downloadedOrPay = true;
		initButton();
	}


	@Override
	public void finish() {
		if(downloadedOrPay){
			Intent intent = new Intent();
			intent.putExtra("PRODUCT", product);
			setResult(RESULT_OK, intent);
		}
		super.finish();
	}
}

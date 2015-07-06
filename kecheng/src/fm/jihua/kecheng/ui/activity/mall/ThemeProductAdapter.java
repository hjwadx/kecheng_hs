package fm.jihua.kecheng.ui.activity.mall;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.ImageHlp;

/**
 * @date 2013-8-29
 * @introduce
 */
public class ThemeProductAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	
	List<Product> products;
	
	public ThemeProductAdapter(Context context, List<Product> products) {
		super();
		this.mContext = context;
		this.products = products;
		inflater = LayoutInflater.from(this.mContext);
	}
	
	public void setData(List<Product> products) {
		this.products = products;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return products.size();
	}

	@Override
	public Object getItem(int position) {
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = inflater.inflate(R.layout.mall_skin_item, null);
			viewHolder = new ViewHolder();
			viewHolder.init(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		Product product = products.get(position);
		if(product.name.equals(App.getInstance().getCurrentStyleName())){
			viewHolder.selected.setBackgroundResource(R.drawable.theme_selected_blue_bg);
		} else {
			viewHolder.selected.setBackgroundResource(android.R.color.transparent);
		}
		viewHolder.name.setText(product.name);
		WindowManager windowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
		int width = Compatibility.getWidth(windowManager.getDefaultDisplay()) - ImageHlp.changeToSystemUnitFromDP(mContext, 22);
		int height = (236 * width / 720);  
		viewHolder.banner.setScaleType(ScaleType.FIT_XY);
		viewHolder.banner.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
		viewHolder.banner.setCorner(true);
		viewHolder.banner.setCornerSize(ImageHlp.changeToSystemUnitFromDP(mContext, 8));
		viewHolder.banner.setImageURI(Uri.parse(product.banner_url));
//		if (product.exists()){
//			viewHolder.price.setVisibility(View.GONE);
//		} else {
			viewHolder.price.setVisibility(View.VISIBLE);
		    initPrice(product, viewHolder.price);
//		}
		return view;
	}
	
	void initPrice(Product product, TextView view){
		switch (product.getStatusLocal()) {
		case Product.EXIST:
			view.setBackgroundResource(android.R.color.transparent);
			if(product.name.equals(App.getInstance().getCurrentStyleName())){
				view.setText(" 已使用 ");
			} else {
				view.setText(" 已下载 ");
			}
			break;
		case Product.PAY_AND_PAYMENT:
			view.setBackgroundResource(R.drawable.button_purple_normal);
			view.setText(" 下载(已购买) ");
			break;
		case Product.PAY_AND_UNPAYMENT:
			view.setBackgroundResource(R.drawable.button_yellow_normal);
			view.setText("￥" + product.price);
			break;
		case Product.FREE_AND_PAYMENT:
		case Product.FREE_AND_UNPAYMENT:
			view.setBackgroundResource(R.drawable.button_blue_normal);
			view.setText(" 免费下载 ");
			break;
		default:
			break;
		}
	}
	
	class ViewHolder{
		CachedImageView banner;
		TextView name;
		TextView price;
		LinearLayout selected;

		public void init(View view) {
			banner = (CachedImageView) view.findViewById(R.id.banner);
			name = (TextView) view.findViewById(R.id.name);
			price = (TextView) view.findViewById(R.id.price);
			selected = (LinearLayout) view.findViewById(R.id.selected);

//			initListener();
		}

		public void initListener(){
			price.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showWeekDialog();
				}
			});
		}
	}

	private Dialog dialog;

	void showWeekDialog() {
		if (dialog == null)
			dialog = new Dialog(mContext, R.style.MyDialog);
//		MallDownloadDialog mallDownloadDialog = new MallDownloadDialog(mContext);
//		dialog.setContentView(R.layout.layout_download_dialog);
		dialog.show();
	}

}

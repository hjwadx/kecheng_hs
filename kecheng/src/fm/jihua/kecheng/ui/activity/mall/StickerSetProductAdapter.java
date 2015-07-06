package fm.jihua.kecheng.ui.activity.mall;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.ui.widget.CachedImageView;

public class StickerSetProductAdapter extends BaseAdapter{
	
	private Context context;
	private LayoutInflater inflater;
	
	List<Product> products;
	
	public StickerSetProductAdapter(Context context, List<Product> products) {
		super();
		this.context = context;
		this.products = products;
		inflater = LayoutInflater.from(this.context);
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
		if(convertView == null){
			view = inflater.inflate(R.layout.mall_paster_item, null);
			viewHolder = new ViewHolder();
			viewHolder.init(view);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		Product product = products.get(position);
		viewHolder.paster.setImageURI(Uri.parse(product.thumb_url));
		viewHolder.name.setText(product.name);
		viewHolder.price.setText(product.getPriceString());
		viewHolder.sign.setImageResource(getSignRes(product));
		return view;
	}
	
	int getSignRes(Product product){
		if(product.exists()){
			return R.drawable.shop_sticker_list_downloaded;
		} else if(product.getStatus(Product.STATUS_EVENT)){
			return R.drawable.shop_sticker_list_activity;
		} else if(product.getStatus(Product.STATUS_NEW)){
			return R.drawable.shop_sticker_list_new;
		} else {
			return android.R.color.transparent;
		}
	}
	
	class ViewHolder{
		CachedImageView paster;
		TextView name;
		TextView price;
		ImageView sign;
		
		public void init(View view){
			paster = (CachedImageView) view.findViewById(R.id.paster);
			name = (TextView) view.findViewById(R.id.name);
			price = (TextView) view.findViewById(R.id.price);
			sign = (ImageView) view.findViewById(R.id.sign);
		}
	}

}

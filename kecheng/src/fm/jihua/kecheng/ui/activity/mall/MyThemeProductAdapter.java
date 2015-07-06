package fm.jihua.kecheng.ui.activity.mall;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.mall.ThemeProduct;
import fm.jihua.kecheng.rest.entities.weekstyle.Theme;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.FileUtils;

public class MyThemeProductAdapter extends BaseAdapter{
	
	Context mContext;
	private int status = MyStickerSetProductsActivity.STATUS_NORMAL;
	private final Object mLock = new Object();
	List<ThemeProduct> products;

	public MyThemeProductAdapter(Context context, List<ThemeProduct> products) {
		super();
		this.mContext = context;
		this.products = products;
	}

	public void setStatus(int status) {
		this.status = status;
		notifyDataSetChanged();
	}

	public boolean isEditStatus() {
		return this.status == MyStickerSetProductsActivity.STATUS_EDIT;
	}

	@Override
	public int getCount() {
		return isEditStatus() ? getEditSize() : products.size();
	}
	
	int getEditSize(){
		int i = 0;
		for(Product product : products){
			if(product.exists()){
				i++;
			} else {
				break;
			}
		}
		return i;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void changeNewPosition(int from, int to) {
		synchronized (mLock) {
			ThemeProduct product = products.get(from);
			products.remove(product);
			products.add(to, product);
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final ViewHolder vh;
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.itemview_mall_paste, null);
			vh = new ViewHolder();
			vh.initViews(view);
			view.setTag(vh);
		} else {
			view = convertView;
			vh = (ViewHolder) view.getTag();
		}

		final ThemeProduct product = products.get(position);

		if (isEditStatus()) {
			vh.drag_handle.setVisibility(View.VISIBLE);
			vh.btn_download.setVisibility(View.GONE);
			vh.btn_delete.setVisibility(View.GONE);
			vh.img_delete.clearAnimation();
			if(product.name.equals(App.getInstance().getCurrentStyleName())){
				vh.img_delete.setVisibility(View.INVISIBLE);
			} else {
				vh.img_delete.setVisibility(View.VISIBLE);
			}
			if (product.isEditStatus) {
				vh.btn_delete.setVisibility(View.VISIBLE);
				vh.drag_handle.setVisibility(View.GONE);
				rotate(vh.img_delete, 0, -90, false);
			}
		} else {
			vh.img_delete.setVisibility(View.GONE);
			vh.drag_handle.setVisibility(View.GONE);
			vh.btn_delete.setVisibility(View.GONE);
			if (product.exists()) {
				vh.btn_download.setVisibility(View.GONE);
			} else {
				vh.btn_download.setVisibility(View.VISIBLE);
			}
		}

		vh.name.setText(product.name);
		Theme bean = product.getItem();
		if(bean != null){
			InputStream input;
			try {
				String name = FileUtils.getInstance().addPngSuffix(product.getLocalStoreDir() + bean.iconImage);
				input = new FileInputStream(new File(name));
				vh.avatar.setImageDrawable(new BitmapDrawable(mContext.getResources(), input));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			vh.avatar.setImageURI(Uri.parse(product.thumb_url));
		}

		vh.img_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (product.isEditStatus) {
					vh.btn_delete.setVisibility(View.GONE);
					vh.drag_handle.setVisibility(View.VISIBLE);
					rotate(vh.img_delete, -90, 0, true);
					product.isEditStatus = false;
				} else {
					vh.btn_delete.setVisibility(View.VISIBLE);
					vh.drag_handle.setVisibility(View.GONE);
					rotate(vh.img_delete, 0, -90, true);
					product.isEditStatus = true;
				}
			}
		});
		vh.btn_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MyThemeProductsActivity)mContext).downloadPaste(product);
			}
		});

		return view;
	}

	class ViewHolder {
		ImageView img_delete;
		CachedImageView avatar;
		TextView name;
		TextView describe;
		ImageView drag_handle;
		// ImageView image_tag;
		ImageView btn_delete;
		Button btn_download;

		public void initViews(View view) {
			img_delete = (ImageView) view.findViewById(R.id.img_delete);
			drag_handle = (ImageView) view.findViewById(R.id.drag_handle);
			avatar = (CachedImageView) view.findViewById(R.id.avatar);
			name = (TextView) view.findViewById(R.id.name);
			describe = (TextView) view.findViewById(R.id.describe);
			btn_delete = (ImageView) view.findViewById(R.id.click_remove);
			btn_download = (Button) view.findViewById(R.id.btn_download);
		}
	}

	void rotate(View view, int fromDegrees, int toDegrees, boolean anim) {
		RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(anim ? 150 : 0);
		animation.setFillAfter(true);
		view.startAnimation(animation);
	}
	

}

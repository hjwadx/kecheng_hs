package fm.jihua.kecheng.ui.activity.sticker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.sticker.Sticker;

/**
 * @date 2013-7-17
 * @introduce
 */
public class ChooseStickerAdapter extends BaseAdapter {

	Context context;
	Sticker[] stickers;
	LayoutInflater inflater;

	public ChooseStickerAdapter(Context context,Sticker[] pasters) {
		super();
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.stickers = pasters;
	}

//	public void setListPaster(Paster[] pasters) {
//		this.pasters = pasters;
//	}

	@Override
	public int getCount() {
		if (stickers == null)
			return 0;
		return stickers.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
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
			view = inflater.inflate(R.layout.layout_itemview_paster_choice, null);
			viewHolder = new ViewHolder();
			viewHolder.init(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		Sticker sticker = stickers[position];
		viewHolder.image.setImageDrawable(sticker.getDrawable(context));
		if (sticker.lock) {
			Compatibility.setAlpha(viewHolder.image, Sticker.LOCK_ALPHA);
		}else{
			Compatibility.setAlpha(viewHolder.image, 255);
		}
			

		viewHolder.tv.setText(sticker.width + "x" + sticker.height);

		return view;
	}

	class ViewHolder {
		TextView tv;
		ImageView image;

		void init(View view) {
			image = (ImageView) view.findViewById(R.id.itemview_image);
			tv = (TextView) view.findViewById(R.id.itemview_textview);
		}
	}

}

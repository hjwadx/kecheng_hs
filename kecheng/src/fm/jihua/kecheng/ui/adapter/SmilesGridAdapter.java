package fm.jihua.kecheng.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import fm.jihua.common.App;
import fm.jihua.common.ui.helper.SlideableGridData;
import fm.jihua.common.ui.helper.SlideableGridData.DataItem;
import fm.jihua.common.ui.helper.SlideableGridData.MenuDataOneScreen;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.ImageHlp;

public class SmilesGridAdapter extends BaseAdapter {
	
	private Context mContext;
	private MenuDataOneScreen mScreen; 
	int padding = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 5);
	int top_padding = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 11);
//	int height = ImageHlp.changeToSystemUnitFromDP(App.getInstance(), 50);
	
	public SmilesGridAdapter(Context context){
		this.mContext = context;
	}
	
	/**这里将数据赋予Adapter*/ 
    public void setScreenData(MenuDataOneScreen screenData) { 
        mScreen = screenData; 
    } 
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		DataItem dataItem = mScreen.mDataItems.get(position);
		
		if (convertView == null) {
			convertView = new ImageView(this.mContext);
		}
		ImageView iv = (ImageView) convertView;
//		iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, height));
		iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		iv.setPadding(padding, top_padding, padding, padding);
		iv.setTag(dataItem);
		
		switch (dataItem.category) {
		case SlideableGridData.CATEGORY_EMOJI:
			iv.setImageDrawable(dataItem.drawable);
			break;
		case SlideableGridData.CATEGORY_HIDDEN:
			iv.setImageDrawable(dataItem.drawable);
			iv.setVisibility(View.INVISIBLE);
			break;
		case SlideableGridData.CATEGORY_DELETE:
			iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selector_chat_keyboard_btn_delete));
			break;

		default:
			break;
		}
		// int width = ImageHlp.changeToSystemUnitFromDP(mContext, 30);
		// int height = ImageHlp.changeToSystemUnitFromDP(mContext, 30);
		// iv.setLayoutParams(new GridView.LayoutParams(width, height));
		// iv.setTag(index);
		// iv.setOnClickListener(new ImageClickListener());
		return iv;
	}

    public final int getCount() {
        return mScreen.mDataItems.size();
    }

    public final Object getItem(int position) {
    	return mScreen.mDataItems.get(position);
    }

    public final long getItemId(int position) {
    	return position;
    }
}

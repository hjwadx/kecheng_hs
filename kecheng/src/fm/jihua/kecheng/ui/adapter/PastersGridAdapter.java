package fm.jihua.kecheng.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import fm.jihua.common.ui.helper.SlideableGridData.MenuDataOneScreen;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;

public class PastersGridAdapter extends BaseAdapter {
	private Context mContext;
	private MenuDataOneScreen mScreen; 
	public PastersGridAdapter(Context context){
		this.mContext = context;
	}
	
	/**这里将数据赋予Adapter*/ 
    public void setScreenData(MenuDataOneScreen screenData) { 
        mScreen = screenData; 
    } 
	
	public View getView(int position, View convertView, ViewGroup parent) {
		try{
			if (convertView == null) {
				convertView = new ImageView(this.mContext);
			}
			ImageView iv = (ImageView) convertView;
			if(position < mScreen.mDataItems.size()){
		        iv.setImageDrawable(mScreen.mDataItems.get(position).drawable);
//		        iv.setScaleType(ImageView.ScaleType.CENTER);
		        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		        int padding = ImageHlp.changeToSystemUnitFromDP(mContext, 2);
		        iv.setPadding(padding, padding, padding, padding);
		        int width = ImageHlp.changeToSystemUnitFromDP(mContext, 66); 
				int height = ImageHlp.changeToSystemUnitFromDP(mContext, 66); 
		        iv.setLayoutParams(new GridView.LayoutParams(width, height));
		        //iv.setTag(index);
		        //iv.setOnClickListener(new ImageClickListener());
		        return iv;
			}
	        return null;
		}catch(Exception e){
			Log.e(Const.TAG, this.getClass().getName() + " getView Exception:"+e.getMessage());
		}
		return null;
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

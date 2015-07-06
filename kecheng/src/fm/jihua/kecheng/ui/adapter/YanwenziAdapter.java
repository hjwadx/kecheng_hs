package fm.jihua.kecheng.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;

public class YanwenziAdapter extends BaseAdapter {
	
	private Context mContext;
	private ArrayList<String> mScreen; 
	public YanwenziAdapter(Context context, ArrayList<String> mDataItems){
		this.mContext = context;
		this.mScreen = mDataItems;
	}

	@Override
	public int getCount() {
		return mScreen.size();
	}

	@Override
	public Object getItem(int position) {
		return mScreen.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try{
			if (convertView == null) {
				convertView = new TextView(this.mContext);
			}
			TextView iv = (TextView) convertView;
			if(position < mScreen.size()){
		        iv.setText(mScreen.get(position));
//		        iv.setScaleType(ImageView.ScaleType.CENTER);
//		        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		        iv.setBackgroundResource(R.drawable.keyboard_yanwenzi_btn);
		        iv.setGravity(Gravity.CENTER);
		        iv.setTextColor(mContext.getResources().getColor(R.color.gray_darker));
		        int padding = ImageHlp.changeToSystemUnitFromDP(mContext, 1);
		        iv.setPadding(padding, padding, padding, padding);
		        int width = ImageHlp.changeToSystemUnitFromDP(mContext, 100); 
				int height = ImageHlp.changeToSystemUnitFromDP(mContext, 40); 
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

}

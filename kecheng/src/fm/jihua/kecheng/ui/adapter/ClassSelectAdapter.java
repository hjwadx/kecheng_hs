package fm.jihua.kecheng.ui.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.CourseBlock;
import fm.jihua.kecheng.ui.widget.GalleryFlow;
import fm.jihua.kecheng.utils.ImageHlp;

public class ClassSelectAdapter extends BaseAdapter {
	
	private Context mContext;
	
	List<CourseBlock> mCourses;
	
	HashMap<String, Integer> colorMap;
	
	public static int[] defaultColors = new int[] { Color.parseColor("#A08FCE"), Color.parseColor("#6BC2EB"), Color.parseColor("#71A2E4"), Color.parseColor("#F38B81"), Color.parseColor("#F0C393"),
			Color.parseColor("#4FC5D0"), Color.parseColor("#B35D80"), Color.parseColor("#90C4FD"), Color.parseColor("#FAB0BF") };
	int[] colors = defaultColors;

	public ClassSelectAdapter(Context c, List<CourseBlock> courses) {
		mContext = c;
		mCourses = courses;
		colorMap = new HashMap<String, Integer>();
	}

	@Override
	public int getCount() {
		return mCourses.size();
	}

	@Override
	public Object getItem(int position) {
		return mCourses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		
		TextView textView;
		if (convertView == null) {
			textView = new TextView(mContext);
			textView.setTextColor(Color.WHITE);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			textView.setLayoutParams(new GalleryFlow.LayoutParams(ImageHlp.changeToSystemUnitFromDP(mContext, 100), ImageHlp.changeToSystemUnitFromDP(mContext, 160)));

			int padding = (int) mContext.getResources().getDimension(R.dimen.choosecourses_padding);
			textView.setPadding(padding, padding, padding, padding);
		} else {
			textView = (TextView) convertView;
			// viewHolder = (ViewHolder) convertView.getTag();
		}
		CourseBlock block = mCourses.get(position);
		String content = block.name + (TextUtils.isEmpty(block.room) ? "" : " @" + block.room);
		textView.setText(content);

		int color = 0;
		
		if(block.backColor == 0){
			if (colorMap.containsKey(block.name)) {
				color = colorMap.get(block.name);
			} else {
				color = colors[colorMap.size() % colors.length];
				colorMap.put(block.name, color);
			}
		}else{
			color = block.backColor;
		}
		textView.setBackgroundColor(color);
		// ((TextView)convertView).setTag(color);

		return textView;
	}
}

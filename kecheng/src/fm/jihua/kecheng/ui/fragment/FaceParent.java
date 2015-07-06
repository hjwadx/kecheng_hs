package fm.jihua.kecheng.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import fm.jihua.common.ui.helper.SlideableGridData;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.widget.ScrollLayout;

public abstract class FaceParent extends Fragment {
	
	View view;
	public OnItemClickListener itemClickListener;
	protected SlideableGridData mMenuData;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.face_container, null);
		((ScrollLayout) view.findViewById(R.id.scrollLayout)).setPageDot(R.drawable.keyboard_dot, R.drawable.keyboard_dot_selected);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMenuData = getMenuData();
		initGridView(view);
	}

	public abstract SlideableGridData getMenuData();

	public abstract void initGridView(View view);


}

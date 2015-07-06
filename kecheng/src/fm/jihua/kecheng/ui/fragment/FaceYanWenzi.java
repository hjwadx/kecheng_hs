package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import fm.jihua.common.ui.helper.SlideableGridData;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.adapter.YanwenziAdapter;
import fm.jihua.kecheng.utils.Const;

public class FaceYanWenzi extends FaceParent {

	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	int screenNum;

	@Override
	public SlideableGridData getMenuData() {
		screenNum = Const.YANWENZI.length / 9;
		int remain = Const.YANWENZI.length % 9;
		screenNum += remain == 0 ? 0 : 1;
		return null;
	}

	@Override
	public void initGridView(View view) {
		int pos = 0;
		for (int i = 0; i < screenNum; i++) {
			ArrayList<String> mDataItems = new ArrayList<String>();
			for (int j = 0; j < 9; j++) {
				if (pos <= Const.YANWENZI.length - 1) {
					mDataItems.add(Const.YANWENZI[pos]);
					pos++;
				}
			}
			View listParent = (View) View.inflate(view.getContext(), R.layout.gridsmiles, null);
			GridView listView = (GridView) listParent.findViewWithTag("gridview");
			listView.setTag(i);
			YanwenziAdapter adapter = new YanwenziAdapter(getActivity(), mDataItems);
			listView.setNumColumns(3);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new SmilesClickListener());
			((ViewGroup) view.findViewById(R.id.scrollLayout)).addView(listParent);
		}
	}

	private class SmilesClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			try {
				// ImageView view = (ImageView) v;
				EditText editSendText = (EditText) ((Activity) getActivity()).findViewById(R.id.chat_input);
				int start = editSendText.getSelectionStart();
				// Integer position = (Integer)v.getTag();
				editSendText.getText().insert(start, ((TextView) v).getText().toString());
			} catch (Exception e) {
				Log.e(Const.TAG, "SmilesGridAdapter onClick, Exception:" + e.getMessage());
			}
		}
	}

}

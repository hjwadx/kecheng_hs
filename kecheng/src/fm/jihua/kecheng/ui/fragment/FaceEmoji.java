package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import fm.jihua.common.ui.helper.SlideableGridData;
import fm.jihua.common.ui.helper.SlideableGridData.DataItem;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.adapter.SmilesGridAdapter;
import fm.jihua.kecheng.ui.helper.SmilesMgr;
import fm.jihua.kecheng.utils.Const;

public class FaceEmoji extends FaceParent {

	final int columnNumber = 7;
	final int rowNumber = 3;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public SlideableGridData getMenuData() {
		((ViewGroup) view.findViewById(R.id.scrollLayout)).removeAllViews();
		ArrayList<DataItem> dataItems = new ArrayList<DataItem>();
		for (int i = 0; i < Const.EMOJIS.length; i++) {
			String label = "" + i;
			DataItem item = new DataItem();
			item.dataName = label;
			item.drawable = SmilesMgr.getInstance().getDrawable(view.getContext(), Const.EMOJIS[i], 1.0f);
			dataItems.add(item);
		}
		SlideableGridData mMenuData = new SlideableGridData();
		mMenuData.setDeleteBtn(true);
		mMenuData.setNumber(rowNumber * columnNumber);
		mMenuData.setMenuItems(dataItems);
		return mMenuData;
	}

	@Override
	public void initGridView(View view) {
		for (int i = 0; i < mMenuData.getScreenNumber(); i++) {
			View listParent = (View) View.inflate(view.getContext(), R.layout.gridsmiles, null);
			GridView listView = (GridView) listParent.findViewWithTag("gridview");
			listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			listView.setTag(i);
			SmilesGridAdapter adapter = new SmilesGridAdapter(getActivity());
			adapter.setScreenData(mMenuData.getScreen(i));
			listView.setNumColumns(columnNumber);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new SmilesClickListener());
			((ViewGroup) view.findViewById(R.id.scrollLayout)).addView(listParent);
		}
	}

	private class SmilesClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			// try {
			DataItem dataItem = (DataItem) v.getTag();
			EditText editSendText = (EditText) ((Activity) getActivity()).findViewById(R.id.chat_input);
			switch (dataItem.category) {
			case SlideableGridData.CATEGORY_EMOJI:
				SmilesMgr.getInstance().insertExpression(editSendText, dataItem.emojiIndex);
				break;
			case SlideableGridData.CATEGORY_DELETE:
				editSendText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
				break;

			default:
				break;
			}
		}
	}
}

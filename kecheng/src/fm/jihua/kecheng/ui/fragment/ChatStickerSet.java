package fm.jihua.kecheng.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.SlideableGridData;
import fm.jihua.common.ui.helper.SlideableGridData.DataItem;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.mall.StickerSetProduct;
import fm.jihua.kecheng.rest.entities.sticker.Sticker;
import fm.jihua.kecheng.rest.entities.sticker.StickerSet;
import fm.jihua.kecheng.ui.activity.mall.StickerSetProductActivity;
import fm.jihua.kecheng.ui.adapter.PastersGridAdapter;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;

public class ChatStickerSet extends FaceParent {

	private List<Drawable> listDrawable = new ArrayList<Drawable>();
	String existed;

	StickerSet stickerSet;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public SlideableGridData getMenuData() {
		Bundle arguments = getArguments();
		stickerSet = (StickerSet) arguments.getSerializable(StickerSet.class.getName());
		if (stickerSet != null) {

			((ViewGroup) view.findViewById(R.id.scrollLayout)).removeAllViews();
			ArrayList<DataItem> dataItems = new ArrayList<DataItem>();
			Sticker[] stickers = stickerSet.stickers;
			if (stickers != null) {
				for (Sticker sticker : stickers) {
					if (sticker.isValidForCategory(Sticker.CATEGORY_CHAT)) {
						String label = sticker.chat_code;
						DataItem item = new DataItem();
						item.dataName = label;
						Drawable drawable = sticker.getDrawable(getActivity()); 
						if (sticker.lock) {
							drawable.setAlpha(Sticker.LOCK_ALPHA);
						}
						item.drawable = drawable;
						listDrawable.add(item.drawable);
						dataItems.add(item);
					}
				}
				SlideableGridData mMenuData = new SlideableGridData();
				mMenuData.setNumber(8);
				mMenuData.setMenuItems(dataItems);
				return mMenuData;
			}
		}
		return new SlideableGridData();
	}

	@Override
	public void initGridView(View view) {
		for (int i = 0; i < mMenuData.getScreenNumber(); i++) {
			// 使用xml的方式加载始终有问题，因此改用代码的方式
			// 经查证是由于设置的Columns太多，将NumColumns设小即可。
			// 后来给scrollLayout加上了margin ok了。
			View listParent = (View) View.inflate(view.getContext(), R.layout.gridsmiles, null);
			GridView listView = (GridView) listParent.findViewWithTag("gridview");
			listView.setTag(i);
			PastersGridAdapter adapter = new PastersGridAdapter(getActivity());
			adapter.setScreenData(mMenuData.getScreen(i));
			listView.setNumColumns(4);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new SmilesClickListener());
			((ViewGroup) view.findViewById(R.id.scrollLayout)).addView(listParent);
		}
		if(mMenuData.getScreenNumber() == 0){
			View listParent = (View) View.inflate(view.getContext(), R.layout.gridsmiles, null);
			GridView listView = (GridView) listParent.findViewWithTag("gridview");
			listView.setTag(0);
			listView.setNumColumns(4);
			listView.setOnItemClickListener(new SmilesClickListener());
			((ViewGroup) view.findViewById(R.id.scrollLayout)).addView(listParent);
		}
	}

	private class SmilesClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			try {
				int index = 8 * ((Integer) parent.getTag()) + position;
				// ImageView view = (ImageView) v;
				EditText editSendText = (EditText) ((Activity) getActivity()).findViewById(R.id.chat_input);
				if (editSendText.getText().toString().length() > 0) {
					existed = editSendText.getText().toString();
					editSendText.setText("");
				} else {
					existed = "";
				}
				if (!stickerSet.stickers[index].lock) {
					insertExpression(editSendText, index);
				}else {
					List<StickerSetProduct> products = StickerSetProduct.getMyLocalProducts();
					StickerSetProduct product = (StickerSetProduct) CommonUtils.findByParam(products, "id", stickerSet.stickers[index].product_id);
					if (product != null) {
						Intent newIntent = new Intent(getActivity(), StickerSetProductActivity.class);
						newIntent.putExtra(StickerSetProductActivity.INTENT_KEY, product);
						startActivity(newIntent);
					}
				}
			} catch (Exception e) {
				Log.e(Const.TAG, "SmilesGridAdapter onClick, Exception:" + e.getMessage());
			}
		}
	}

	public void insertExpression(EditText edit, int index) {
		int start = edit.getSelectionStart();
		MobclickAgent.onEvent(getActivity(), "event_message_sticker_succeed", String.valueOf(stickerSet.stickerSetName));
		String expression = "[" + stickerSet.stickers[index].chat_code + "]";
		Spannable ss = edit.getText().insert(start, expression);
		Drawable d = listDrawable.get(index);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		ImageSpan span = new ImageSpan(d, expression, ImageSpan.ALIGN_BOTTOM);
		ss.setSpan(span, start, start + expression.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		EditText editSendText = (EditText) ((Activity) getActivity()).findViewById(R.id.chat_input);
		editSendText.setTag("image");
		Button button = (Button) ((Activity) getActivity()).findViewById(R.id.chat_send_message);
		button.performClick();
		editSendText.setText(existed);
	}

}

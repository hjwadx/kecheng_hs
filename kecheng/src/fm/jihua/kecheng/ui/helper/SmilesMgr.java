package fm.jihua.kecheng.ui.helper;

import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;
import fm.jihua.common.utils.ImageCache;
import fm.jihua.common.utils.ValueBuilder;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;

/**
 * @author 黄祥旦
 * 表情管理
 */
public class SmilesMgr {
//	private String mStartTag = Const.SIMLES_START_TAG;
//	private String mEndTag = Const.SMILES_END_TAG;
	
	private static  SmilesMgr mInstance=new SmilesMgr();
	
	public static SmilesMgr getInstance(){
		return mInstance;
	}
	
	Map<String, Integer> emojiMap;
	
	public class SmilesItem{
		public String mTag;
		public int mDrawableID;
		public SmilesItem(String tag, int drawableID){
			this.mTag = tag;
			this.mDrawableID = drawableID;
		}
	}
	
//	public Drawable getDrawable(Context context, int position){
//		return getDrawableCache(context, position, 1);
//	}
	
	public Integer getDrawblePosition(Context context, String smiles){
		App app = (App) context.getApplicationContext();
		if (emojiMap == null) {
			emojiMap = app.getSchoolDBHelper().getAllUtf8Emoji();
		}
		return emojiMap.get(smiles);
	}
	
	public int getCount(){
		return Const.EMOJIS.length;
	}
	
	public  void insertExpression(EditText edit, int index) {
		//EditText edit = (EditText) findViewById(R.id.editSend);
		int start = edit.getSelectionStart();
		String expression = Const.EMOJIS[index];
		Log.i(Const.TAG, "insertExpression expression" + expression);
		Spannable ss = edit.getText().insert(start, expression);

		Integer position = getDrawblePosition(edit.getContext(), expression);
		if (position != null) {
			Drawable d = getDrawableCache(edit.getContext(), position, 1);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			ImageSpan span = new ImageSpan(d, expression,
					ImageSpan.ALIGN_BOTTOM);
			ss.setSpan(span, start, start + expression.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	public Drawable getDrawable(Context context, String smiles){
		return getDrawable(context, smiles, 1);
	}
	public Drawable getDrawable(Context context, String smiles, float scale){
		Integer id = getDrawblePosition(context, smiles);
		if(id != null){
			return getDrawableCache(context, id, scale);
		}
		return null;
	}
	
	private Drawable getDrawableCache(Context context, int index, final float scale){
		int row = index / 32;
    	int col = index % 32;
    	Drawable emojiDrawable = context.getResources().getDrawable(R.drawable.emojimap_36);
    	Bitmap bmpFull = ((BitmapDrawable)emojiDrawable).getBitmap();
    	int size = ImageHlp.changeToSystemUnitFromDP(context, 24);
    	final Bitmap bmpOrigin = Bitmap.createBitmap(bmpFull, col*size, row*size, size, size);
    	Bitmap emoji = ImageCache.getInstance().get(Const.AVATAR_CACHE_EMOJI_PREFIX+index, new ValueBuilder<Bitmap>() {
			@Override
			public Bitmap buildValue() {
				return Bitmap.createScaledBitmap(bmpOrigin, (int)(bmpOrigin.getWidth()*scale), (int)(bmpOrigin.getHeight()*scale), true);
			}
		}, true);
    	Drawable result = new BitmapDrawable(context.getResources(), emoji);
//    	Drawable result = new BitmapDrawable(context.getResources(), bmpOrigin);
    	bmpOrigin.recycle();
//    	bmp.recycle();
		result.setBounds(0, 0, (int)(result.getIntrinsicWidth()), (int)(result.getIntrinsicHeight()));
    	return result;
	}
}

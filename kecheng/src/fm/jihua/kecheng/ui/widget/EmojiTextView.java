package fm.jihua.kecheng.ui.widget;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.AttributeSet;
import android.widget.TextView;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.ui.helper.SmilesMgr;

public class EmojiTextView extends TextView {
	
	int lineLength = 32;
	int maxColLength = 15;
	
	public EmojiTextView(Context context) {
		super(context);
	}
	
	public EmojiTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		setEmojiText(text.toString());
	}

	public void setEmojiText(String text) {
		text = convertTag(text);
		CharSequence spanned = Html.fromHtml(text, emojiGetter, null);
		super.setText(spanned, BufferType.NORMAL);
	}
	
	private String convertTag(String text){
		text = text.replaceAll("<", "&lt;").replace("\n", "<br/>");
		App app = (App)getContext().getApplicationContext();
		Map<String, Integer> emoji = app.getSchoolDBHelper().getAllUtf8Emoji();
		for(String key : emoji.keySet()){
			text = text.replace(key, "<img src='"+key+"'/>");
		}
		return text;
	}
	
	private ImageGetter emojiGetter = new ImageGetter()
	{
        public Drawable getDrawable(String source){
        	return SmilesMgr.getInstance().getDrawable(getContext(), source);
        }
    };		
}
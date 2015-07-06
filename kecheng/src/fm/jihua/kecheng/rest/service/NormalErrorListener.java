package fm.jihua.kecheng.rest.service;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.ui.helper.Hint;

public class NormalErrorListener implements ErrorListener {
	Context mContext;
	String mMessage;
	
	public NormalErrorListener(Activity activity, String message) {
		this.mContext = activity;
		this.mMessage = message;
	}
	
	public NormalErrorListener(Context context, String message) {
		this.mContext = context;
		this.mMessage = message;
	}
	
	
	
	@Override
	public void onErrorResponse(VolleyError error) {
//		if (error.networkResponse != null) {
//			AppLogger.w(error.networkResponse.)
//		}
		Hint.showTipsShort(mContext, mMessage + "失败了，原因：\n"+ error.getLocalizedMessage());
		if (mContext instanceof Activity) {
			UIUtil.unblock((Activity) mContext);
		}
	}

}

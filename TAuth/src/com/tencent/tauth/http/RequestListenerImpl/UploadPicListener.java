package com.tencent.tauth.http.RequestListenerImpl;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tauth.bean.Pic;
import com.tencent.tauth.http.BaseRequestListener;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.CommonException;
import com.tencent.tauth.http.ParseResoneJson;
import com.tencent.tauth.http.TDebug;

public class UploadPicListener extends BaseRequestListener {
	private static final String TAG = "UploadPicListener";
	private Callback mCallback;

	public UploadPicListener(Callback callback) {
		this.mCallback = callback;
	}

	@Override
	public void onComplete(String response, Object state) {
		super.onComplete(response, state);
		try {
			JSONObject obj = ParseResoneJson.parseJson(response);
			
			int ret = 0;
			String msg = "";
			try {
				ret = obj.getInt("ret");
				msg = obj.getString("msg");
			} catch (JSONException e) {
				//is OK
			}
			
			if (ret == 0) {
				Pic pic = new Pic(	obj.getString("albumid"), 
									obj.getString("lloc"), 
									obj.getString("sloc"), 
									obj.getInt("width"), 
									obj.getInt("height"));
				mCallback.onSuccess(pic);
			} else {
				mCallback.onFail(ret, msg);
			}
		} catch (NumberFormatException e) {
			mCallback.onFail(Integer.MIN_VALUE, e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			mCallback.onFail(Integer.MIN_VALUE, e.getMessage());
			e.printStackTrace();
		} catch (CommonException e) {
			mCallback.onFail(Integer.MIN_VALUE, e.getMessage());
			e.printStackTrace();
		}
		TDebug.i(TAG, response);
	}
	
	@Override
	public void onFileNotFoundException(FileNotFoundException e, Object state) {
		mCallback.onFail(Integer.MIN_VALUE, "Resource not found:" + e.getMessage());
	}
	
	@Override
	public void onIOException(IOException e, Object state) {
		mCallback.onFail(Integer.MIN_VALUE, "Network Error:" + e.getMessage());
	}
}

package com.tencent.tauth.http.RequestListenerImpl;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.http.BaseRequestListener;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.CommonException;
import com.tencent.tauth.http.ParseResoneJson;
import com.tencent.tauth.http.TDebug;

public class OpenIDListener extends BaseRequestListener {
	private static final String TAG = "OpenIDListener";
	private Callback mCallback;

	public OpenIDListener(Callback callback) {
		mCallback = callback;
	}

	@Override
	public void onComplete(String response, Object state) {
		super.onComplete(response, state);
		try {
			JSONObject obj = ParseResoneJson.parseJson(response);
			
			int ret = 0;
			String msg = "";
			try {
				ret = obj.getInt("error");
				msg = obj.getString("error_description");
			} catch (JSONException e) {
				//is OK
			}
			
			if (ret == 0) {
				String openid = obj.getString("openid");
				String client_id = obj.getString("client_id");
				OpenId id = new OpenId(openid, client_id);
				mCallback.onSuccess(id);
			} else {
				mCallback.onFail(ret, msg);
			}
			
		} catch (CommonException e) {
			mCallback.onFail(Integer.MIN_VALUE, e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			mCallback.onFail(Integer.MIN_VALUE, e.getMessage());
		} catch (JSONException e) {
			mCallback.onFail(Integer.MIN_VALUE, e.getMessage());
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

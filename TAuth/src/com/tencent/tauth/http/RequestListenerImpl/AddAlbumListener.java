package com.tencent.tauth.http.RequestListenerImpl;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tauth.bean.Album;
import com.tencent.tauth.http.BaseRequestListener;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.CommonException;
import com.tencent.tauth.http.ParseResoneJson;
import com.tencent.tauth.http.TDebug;

public class AddAlbumListener extends BaseRequestListener {
	private static final String TAG = "AddAlbumListener";
	private Callback mCallback;

	public AddAlbumListener(Callback callback) {
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
				Album album = new Album(obj.getString("albumid"), 
										obj.getInt("classid"), 
										obj.getLong("createtime"),
										obj.getString("desc"), 
										obj.getString("name"), 
										0, 
										obj.getInt("priv"));
				mCallback.onSuccess(album);
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

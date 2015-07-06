package com.tencent.tauth.http.RequestListenerImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.tauth.bean.Album;
import com.tencent.tauth.http.BaseRequestListener;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.CommonException;
import com.tencent.tauth.http.ParseResoneJson;
import com.tencent.tauth.http.TDebug;

public class ListAlbumListener extends BaseRequestListener {
	private static final String TAG = "ListAlbumListener";
	private Callback mCallback;

	public ListAlbumListener(Callback callback) {
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
				int albumnum = obj.getInt("albumnum");
				JSONArray albumArray = obj.getJSONArray("album");
				ArrayList<Album> albums = new ArrayList<Album>();
				for (int i = 0; i < albumnum; i++) {
					JSONObject albumObj = albumArray.getJSONObject(i);
					Album album = new Album(albumObj.getString("albumid"), 
											albumObj.getInt("classid"), 
											albumObj.getLong("createtime"),
											albumObj.getString("desc"), 
											albumObj.getString("name"), 
											albumObj.getInt("picnum"), 
											albumObj.getInt("priv"));
					albums.add(album);
				}
				mCallback.onSuccess(albums);
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

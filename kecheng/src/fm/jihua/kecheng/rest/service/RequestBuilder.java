package fm.jihua.kecheng.rest.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.utils.AppLogger;

public class RequestBuilder {
	RequestQueue queue;
	Handler mHandler;
	Gson gson;
	
	public RequestBuilder(RequestQueue queue, Handler handler) {
		this.queue = queue;
		this.mHandler = handler;
		this.gson = GsonRequest.getDefaultGson();
	}
	
	private class ErrorListenerAdapter<T> implements ErrorListener{
		int category;
		boolean notFinished;
		Class<T> classOfT;
		
		public ErrorListenerAdapter(int category, boolean notFinished, Class<T> classOfT) {
			this.category = category;
			this.notFinished = notFinished;
			this.classOfT = classOfT;
		}
		
		@Override
		public void onErrorResponse(VolleyError error) {
			T t = null;
			if (notFinished && VolleyError.NO_CACHE_ERROR.equals(error.getMessage())) {
				try {
					t = classOfT.newInstance();
					if (t instanceof BaseResult) {
						((BaseResult)t).finished = false;
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			AppLogger.e(error.getMessage(), error);
			mHandler.sendMessage(createMessage(this.category, t));
		}
	}
	
	
	public <T>void buildAndAddRequest(RestEntity restEntity, Class<T> clazz, final int category){
		buildAndAddRequest(restEntity, clazz, category, false);
	}
	
	public <T>void buildAndAddRequest(RestEntity restEntity, Class<T> clazz, final int category, boolean forceUsingCache){ 
		buildAndAddRequest(restEntity, clazz, category, forceUsingCache, null, forceUsingCache);
	}
	
	public <T>void buildAndAddRequest(RestEntity restEntity, Class<T> clazz, final int category, boolean forceUsingCache, boolean notFinished){ 
		buildAndAddRequest(restEntity, clazz, category, forceUsingCache, null, notFinished);
	}
	
	public <T>void buildAndAddRequest(RestEntity restEntity, Class<T> clazz, final int category, Listener<T> listener){ 
		buildAndAddRequest(restEntity, clazz, category, false, listener);
	}
	
	public <T>void buildAndAddRequest(RestEntity restEntity, Class<T> clazz, final int category, boolean forceUsingCache, Listener<T> listener){
		buildAndAddRequest(restEntity, clazz, category, forceUsingCache, listener, forceUsingCache);
	}
	
	public <T>void buildAndAddRequest(RestEntity restEntity, Class<T> clazz, final int category, boolean forceUsingCache, Listener<T> listener, boolean notFinished){
		GsonRequest<T> request = buildRequest(restEntity, clazz, category, forceUsingCache, listener, notFinished);
		queue.add(request);
	}
	
	public <T>GsonRequest<T> buildRequest(RestEntity restEntity, Class<T> clazz, final int category, boolean forceUsingCache, Listener<T> listener, boolean notFinished){
		if (listener == null) {
			listener = buildRequestListener(clazz, category, notFinished);
		}
		GsonRequest<T> request = new GsonRequest<T>(restEntity, clazz, listener, new ErrorListenerAdapter<T>(category, notFinished, clazz));
		if (forceUsingCache) {
			request.setForceUsingCache(forceUsingCache);
		}
		request.setTag(DataAdapter.TAG);
		return request;
	}
	
	public <T>void buildAndAddListRequest(RestEntity restEntity, Class<T> clazz, final int category){
		buildAndAddListRequest(restEntity, clazz, category, false, false);
	}
	
	public <T>void buildAndAddListRequest(RestEntity restEntity, Class<T> clazz, final int category, boolean forceUsingCache, boolean notFinished){
		JSONArrayRequest request = new JSONArrayRequest(restEntity, buildListListener(category, clazz), new ErrorListenerAdapter<T>(category, notFinished, clazz));
		if (forceUsingCache) {
			request.setForceUsingCache(forceUsingCache);
		}
		request.setTag(DataAdapter.TAG);
		queue.add(request);
	}
	
	public <T>Listener<T> buildRequestListener(Class<T> classOfT, final int category){
		return buildRequestListener(classOfT, category, false);
	}
	
	public <T>Listener<T> buildRequestListener(Class<T> classOfT, final int category, final boolean notFinished){
		return new Listener<T>() {

			@Override
			public void onResponse(T response) {
				if (notFinished && response instanceof BaseResult) {
					((BaseResult)response).finished = false;
				}
				mHandler.sendMessage(createMessage(category, response));
			}
		};
	}
	
	public <T>Listener<JSONArray> buildListListener(final int category, final Class<T> clazz){
		return new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				List<T> result = new ArrayList<T>();
				for (int i = 0; i < response.length(); i++) {
					try {
						if (response.isNull(i)){
							result.add(null);
						}else {
							JSONObject obj = response.getJSONObject(i);
							T objOfT = gson.fromJson(obj.toString(),
									clazz);
							result.add(objOfT);
						}
					} catch (JSONException e) {
						AppLogger.printStackTrace(e);
						continue;
					}
				}
				mHandler.sendMessage(createMessage(category, result));
			}
		};
	}
	
	private Message createMessage(int what, Object obj) {
		return Message.obtain(null, what, obj);
	}
}

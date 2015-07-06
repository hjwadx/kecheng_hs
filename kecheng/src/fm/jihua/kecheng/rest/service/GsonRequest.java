package fm.jihua.kecheng.rest.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.rest.entities.mall.ProductAdapter;
import fm.jihua.kecheng.utils.AppLogger;


public class GsonRequest<T> extends JsonRequest<T> {
    private final Gson mGson;
    private final Class<T> mClazz;
    private final Map<String, String> mHeaders = new HashMap<String, String>();
    
    public GsonRequest(
            String url,
            JSONObject jsonRequest,
            Class<T> clazz,
            Listener<T> listener,
            ErrorListener errorListener) {
	this(Method.GET, url, jsonRequest, clazz, listener, errorListener, getDefaultGson());
	}


    public GsonRequest(int method,
                       String url,
                       JSONObject jsonRequest,
                       Class<T> clazz,
                       Listener<T> listener,
                       ErrorListener errorListener) {
        this(method, url, jsonRequest, clazz, listener, errorListener, getDefaultGson());
    }
    
    public GsonRequest(RestEntity entit,
            Class<T> clazz,
            Listener<T> listener,
            ErrorListener errorListener) {
    	this(entit.methodCode, entit.url, entit.requestData, clazz, listener, errorListener, getDefaultGson());
	}
    
    public static Gson getDefaultGson(){
    	GsonBuilder gsonBuilder = new GsonBuilder();
    	gsonBuilder.setDateFormat("yyyy-MM-dd").registerTypeAdapter(Product.class, new ProductAdapter());
    	return gsonBuilder.create();
    }
    
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
    	return mHeaders;
    }
    
    public void addHeader(String key, String value){
    	mHeaders.put(key, value);
    }

    public GsonRequest(int method,
                       String url,
                       JSONObject jsonRequest,
                       Class<T> clazz,
                       Listener<T> listener,
                       ErrorListener errorListener,
                       Gson gson) {
    	super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        this.mClazz = clazz;
        mGson = gson;
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            AppLogger.i("NetWork:  "+json);
            return Response.success(mGson.fromJson(json, mClazz),
                                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
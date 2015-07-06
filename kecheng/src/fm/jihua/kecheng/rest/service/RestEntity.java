package fm.jihua.kecheng.rest.service;

import java.io.Serializable;

import org.json.JSONObject;

import com.android.volley.Request.Method;

public class RestEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5911309472155706739L;
	public final static String GET = "get";
	public final static String POST = "post";
	public final static String PUT = "put";
	public final static String DELETE = "delete";
	
	public String url;
	public String method = "get";
	public JSONObject requestData;
	public int methodCode;
	@SuppressWarnings("rawtypes")
	public Class clientClass;
	
	public RestEntity(String url){
		this.url = RestService.get().buildRequestUrl(url);
		this.methodCode = getMethodCode(this.method);
	}
	
	public <T>RestEntity(String url, Class<T> classOfT){
		this(url);
		this.clientClass = classOfT;
	}
	
	public RestEntity(String method, String url){
		this(url);
		this.method = method;
		this.methodCode = getMethodCode(this.method);
	}
	
	public <T>RestEntity(String method, String url, Class<T> classOfT){
		this(method, url);
		this.clientClass = classOfT;
	}
	
	public <T>RestEntity(String method, String url, JSONObject data){
		this(method, url);
		this.requestData = data;
	}
	
	public <T>RestEntity(String method, String url, JSONObject data, Class<T> classOfT){
		this(method, url, classOfT);
		this.requestData = data;
	}
	
	public static int getMethodCode(String method){
		int code;
		if ("delete".equals(method)) {
			code = Method.DELETE;
		}else if ("post".equals(method)) {
			code = Method.POST;
		}else if ("put".equals(method)) {
			code = Method.PUT;
		}else {
			code = Method.GET;
		}
		return code;
	}
}

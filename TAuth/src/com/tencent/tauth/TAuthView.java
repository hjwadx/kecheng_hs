package com.tencent.tauth;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TAuthView extends Activity {
	
	private String mGraphURL ;
	private String mCallback = "auth://tauth.qq.com/";
	private WebView mWebView;
	private String mAuthResult;
	private String mAccessToken;
	private String mExpiresIn;
	private String mRet;
	private String mErrorDes;
	private ProgressDialog dialog;
	private TextView titleTxt;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
						dialog = null;
					}
					break;
	
				default://2.1一下版本中的bug:如果不新创建，会导致进度条不转
					if (dialog == null) {
						dialog = new ProgressDialog(TAuthView.this);
						dialog.setMessage("请求中,请稍候...");
					}
					if (!TAuthView.this.isFinishing() && !dialog.isShowing()) {
						dialog.show();
					}
					break;
			}
		}
	};
	
	public static final String AUTH_BROADCAST = "com.tencent.auth.BROWSER";
	public static final String CLIENT_ID = "client_id";
	public static final String SCOPE = "scope";
	public static final String CALLBACK = "callback";
	public static final String TARGET = "target";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String ERROR_RET = "error";
	public static final String ERROR_DES = "error_description";
	
	/**
	 * 处理打开登录页面的方式：浏览器 与 Webview两种方式
	 * 
	 * @author John.Meng<arzen1013@gmail> QQ:3440895
	 * @date 2011-9-5
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mGraphURL = TencentOpenHost.GRAPH_ACCESS_TOKEN_URL + "#" + System.currentTimeMillis();
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String client_id = bundle.getString(CLIENT_ID);
			String scope = bundle.getString(SCOPE);
			String callback = bundle.getString(CALLBACK);
			if (callback != null && !callback.equals("")) {
				mCallback = callback;
			}
			String url = String.format(mGraphURL, client_id, scope, mCallback, getIp(), getOS(), getMachine(), getVersion());
			String target = bundle.getString(TARGET);
			
			if (target.equals("_blank")) {
				//以浏览器方式打开登录页面
				Uri uri = Uri.parse(url);   
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				finish();
			}else {
				//以webview方式打开登录页面
				requestWindowFeature(Window.FEATURE_NO_TITLE);
		    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
		    			WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		    	
		    	LinearLayout root = new LinearLayout(this);
		    	root.setOrientation(LinearLayout.VERTICAL);
		    	root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
		    			ViewGroup.LayoutParams.FILL_PARENT));
		    	
		    	RelativeLayout titleBar = new RelativeLayout(this);
		    	RelativeLayout.LayoutParams titleBarParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
		    			ViewGroup.LayoutParams.WRAP_CONTENT);
		    	titleBar.setLayoutParams(titleBarParams);
		    	titleBar.setPadding(10, 0, 6, 0);
		    	
		    	ImageButton closeBtn =  new ImageButton(this);
		    	closeBtn.setId(0xff040000);
		    	closeBtn.setPadding(0, 0, 0, 0);
		    	closeBtn.setImageDrawable(TencentOpenRes.getCloseBtnSrc(getAssets()));
				closeBtn.setBackgroundDrawable(TencentOpenRes.getCloseBtnSrcBg(getAssets()));
		    	closeBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
				RelativeLayout.LayoutParams closeBtnParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				closeBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				closeBtnParams.addRule(RelativeLayout.CENTER_VERTICAL);
				titleBar.addView(closeBtn, closeBtnParams);
		    	
		    	titleTxt = new TextView(this);
		    	titleTxt.setTextColor(Color.WHITE);
		    	RelativeLayout.LayoutParams titleTxtParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		    	titleTxtParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		    	titleTxtParams.addRule(RelativeLayout.CENTER_VERTICAL);
		    	titleTxtParams.addRule(RelativeLayout.LEFT_OF, closeBtn.getId());
		    	titleTxtParams.setMargins(0, 0, 5, 0);
		    	titleTxt.setSingleLine();
		    	titleTxt.setEllipsize(TruncateAt.END);
		    	titleTxt.setText("QQ登录");
		    	titleBar.addView(titleTxt,titleTxtParams);
		    	
				titleBar.setBackgroundDrawable(TencentOpenRes.getTitleBg(getAssets()));
		    	
		    	root.addView(titleBar, new ViewGroup.LayoutParams(-1,-2));
				
				mWebView = new WebView(this);
				mWebView.setLayoutParams(new LinearLayout.LayoutParams(
		        		ViewGroup.LayoutParams.FILL_PARENT,
		        		ViewGroup.LayoutParams.FILL_PARENT));
		    	root.addView(mWebView);
				
				mWebView.setInitialScale(100);
				mWebView.setVerticalScrollBarEnabled(false);
				
				mWebView.setWebViewClient(new MyWebViewClient() );
				
				WebSettings setting = mWebView.getSettings();
				setting.setJavaScriptEnabled(true);
				setting.setBuiltInZoomControls(true);
				
				mWebView.setWebChromeClient(new MyWebChromeClient());
				
				setContentView(root);
				
				mWebView.loadUrl(url);
				
			}
			
		}else {
			//捕获浏览器方式打开的回调结果
			Uri uri = getIntent().getData();  
    	    String url = uri.toString();  
    	    parseResult(url);
    	    sendBroadcastResult();
    	    finish();
		}
		
	}

	/**
	 * 解析返回结果，提取出Access token
	 * @param	String	url
	 * @author John.Meng<arzen1013@gmail> QQ:3440895
	 * @date 2011-9-1
	 */
	private void parseResult(String url) {
		mAuthResult = url;
		String tmp = url;
		if (tmp.startsWith(mCallback + "?#")) {
			tmp = tmp.substring(tmp.indexOf('#') + 1);
		} else {
			tmp = tmp.substring(tmp.indexOf('?') + 1);
		}
		String[] arr = tmp.split("&");
		HashMap<String, String> res = new HashMap<String, String>();
		for (String item : arr) {
			String[] data = item.split("=");
			res.put(data[0], data[1]);
		}
		mAccessToken = res.get(ACCESS_TOKEN);
		mExpiresIn = res.get(EXPIRES_IN);
		mRet = res.get(ERROR_RET);
		mErrorDes = res.get(ERROR_DES);
		
	}
	
	private void returnResult() {
		sendBroadcastResult();
		finish();
	}
	
	/**
	 * 以广播的形式把返回结果及access token发送，以便调用者接收处理
	 * 
	 * @author John.Meng<arzen1013@gmail> QQ:3440895
	 * @date 2011-9-5
	 */
	private void sendBroadcastResult() {
		Intent intent = new Intent(AUTH_BROADCAST);
		intent.putExtra("raw", mAuthResult);
		intent.putExtra(ACCESS_TOKEN, mAccessToken);
		intent.putExtra(EXPIRES_IN, mExpiresIn);
		intent.putExtra(ERROR_RET, mRet);
		intent.putExtra(ERROR_DES, mErrorDes);
		sendBroadcast(intent);
		
	}
	
	private void setWinTitle(String title) {
		titleTxt.setText(title);
	}
	
    private class MyWebChromeClient extends WebChromeClient {
    	
    	@Override
    	public void onReceivedTitle(WebView view, String title) {
    		super.onReceivedTitle(view, title);
    		setWinTitle(title);
    	}
    }
	
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(mCallback + "?")) {
                // 拦截回调结果
        	    parseResult(url);
            	handler.sendEmptyMessage(0);
        	    returnResult();
                return true;
            }
        	handler.sendEmptyMessage(1);
            return false;
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	handler.sendEmptyMessage(1);
        }
        public void onPageFinished(WebView view, String url) {
        	handler.sendEmptyMessage(0);
        }
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
        	handler.sendEmptyMessage(0);
        	super.onReceivedError(view, errorCode,
                    description, failingUrl);
        }
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			handler.proceed();
		}
    }   
    
	private String getIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.d("TAG", ex.toString());
		}
		return "";
	}
    
    private String getOS() {
    	return android.os.Build.VERSION.RELEASE;
    }
    
    private String getMachine() {
    	
    	return android.os.Build.MODEL;
    }
    
    private String getVersion() {
    	
    	return android.os.Build.VERSION.SDK;
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
    }
    
}

package fm.jihua.kecheng.ui.activity.common;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.activity.BaseActivity;

public class WebViewActivity extends BaseActivity{
	
	WebView webView;
	Button back;
	Button forward;
	Button refresh;
//	GifMovieView loading;
	ProgressBar loading;
	public static final String url = "http://hs.kechenggezi.com/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser);
		findview();
		initTitlebar();
		init();
		setLintener();
	}
	
	private void setLintener() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new ClickListener());
		forward.setOnClickListener(new ClickListener());
		refresh.setOnClickListener(new ClickListener());
	}

	void init() {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		webView.setWebViewClient(new MyWebViewClient());
		webView.setWebChromeClient(new MyChromeClient()); 
		String target = getIntent().getStringExtra("URL");
		if(target == null || "".equals(target)){
			target =url;
		}
		webView.loadUrl(target);
	}

	void findview() {
		webView = (WebView) findViewById(R.id.webview);
		back = (Button) findViewById(R.id.go_back);
		forward = (Button) findViewById(R.id.go_forward);
		refresh = (Button) findViewById(R.id.refresh);
//		loading = (GifMovieView) findViewById(R.id.loading);
		loading = (ProgressBar) findViewById(R.id.loading);
	}

	void initTitlebar(){
		setTitle(R.string.act_web_view_title);
		actionbar.setShowBackBtn(true);
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {   
        	webView.goBack();   
            return true;   
        } 
        return super.onKeyDown(keyCode, event);   
    }
	
	private class MyWebViewClient extends WebViewClient {
		 @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
			 webView.loadUrl(url);//载入网页
             return true; 
	        }
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//	        	handler.sendEmptyMessage(1);
	        	loading.setVisibility(View.VISIBLE);
	        	refresh.setVisibility(View.GONE);
	        }
	        public void onPageFinished(WebView view, String url) {
//	        	handler.sendEmptyMessage(0);
	        	loading.setVisibility(View.GONE);
	        	refresh.setVisibility(View.VISIBLE);
	        	if(webView.canGoBack()){
	        		back.setBackgroundResource(R.drawable.web_go_back_btn);
	        	} else {
	        		back.setBackgroundResource(R.drawable.btn_back_pressed);
	        	}
	        	if(webView.canGoForward()){
	        		forward.setBackgroundResource(R.drawable.web_go_forward_btn);
	        	} else {
	        		forward.setBackgroundResource(R.drawable.btn_forward_pressed);
	        	}
	        }
//	        public void onReceivedError(WebView view, int errorCode,
//	                String description, String failingUrl) {
//	        	handler.sendEmptyMessage(0);
//	        	super.onReceivedError(view, errorCode,
//	                    description, failingUrl);
//	        }
//			@Override
//			public void onReceivedSslError(WebView view, SslErrorHandler handler,
//					SslError error) {
//				handler.proceed();
//			}
	}
	
	private class MyChromeClient extends WebChromeClient {
		@Override 

        public void onReceivedTitle(WebView view, String title) { 

            //设置当前activity的标题栏 
			setTitle(title); 
            super.onReceivedTitle(view, title); 

        } 
	}
	
	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.go_back:{
				if(webView.canGoBack()){
					webView.goBack(); 
				} else {
//					webView.reload();
				}
			}
				break;
			case R.id.go_forward:{
				if(webView.canGoForward()){
					webView.goForward(); 
				} else {
//					webView.reload();
				}
			}
				break;
			case R.id.refresh:{
				webView.reload();
			}
				break;
			default:
				break;
			}
		}
		
	}

}

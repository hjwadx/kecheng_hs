package fm.jihua.kecheng.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.helper.Hint;

public class TencentAuthHelper implements AuthHelper {

	private Activity mContext;
	private final String TAG = "TencentAuthHelper";
	SNSCallback mSNSCallback;
	private Boolean succeed = false;
	App app;
	DataAdapter mDataAdapter;


	//new
	private Tencent mTencent;

	// 用type来区分QQ空间和腾讯微博
	int currentType = Const.QQ;

	public TencentAuthHelper(Activity context) {
		this.mContext = context;
		app = App.getInstance();
		mDataAdapter = new DataAdapter(mContext, new MyDataCallback());
		final Context ctxContext = (Application)mContext.getApplicationContext();

		//new
		mTencent = Tencent.createInstance(Const.TENCENT_APP_ID, ctxContext);
		if (isAuthed()) {
			String expires_in = String.valueOf((app.getTencentExpires() - System.currentTimeMillis())/1000);
			mTencent.setAccessToken(app.getTencentToken(), expires_in);
			mTencent.setOpenId(app.getTencentOpenid());
		}
	}

	public String getThirdPartId() {
		return app.getTencentOpenid();
	}

	public String getThirdPartToken() {
		return app.getTencentToken();
	}

	/**
	 * 内部会对当前Activity进行block和unblock
	 * 返回之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	@Override
	public void auth(final SNSCallback callback) {
		if (!isAuthed() || !mTencent.isSessionValid()) {
			UIUtil.block(mContext);
            mTencent.login(mContext, Const.TENCENT_SCOPE, new BaseUiListener(callback));
		} else {
			callback.onComplete(this, null);
		}
	}

	public void setCurrentType(int currentType) {
		this.currentType = currentType;
	}

	@Override
	public void unBind() {
		app.logoutTencent();
		mTencent.logout(mContext);
	}

	@Override
	public boolean isAuthed() {
		return app.getTencentToken() != null && app.getTencentOpenid() != null;
	}

//	@Override
//	public void setSNSCallback(SNSCallback callback) {
//		this.mSNSCallback = callback;
//	}

	public Tencent getTencent() {
		return mTencent;
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void update(String status, final SNSCallback callback) {

		if (currentType == Const.QQ) {
			Bundle bundle = new Bundle();
			bundle.putString("title", status);
			bundle.putString("url", "http://hs.kechenggezi.com/");
			bundle.putString("site", "课程格子");
			bundle.putString("fromurl", "http://hs.kechenggezi.com/");
			addShare(bundle, callback);
		} else if (currentType == Const.QQWeibo) {
//			//腾讯微博发送一条不带图片的
			Bundle bundle= new Bundle();
			bundle.putString("content", status);
			mTencent.requestAsync("t/add_t", bundle, Constants.HTTP_POST,
					new BaseApiListener("add_t", callback), null);
		}
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void upload(String file, String status, final SNSCallback callback) {
		succeed = false;
		if (currentType == Const.QQ) {
			Bitmap pic = ImageHlp.decodeFile(file);
			byte[] buff = null;
			buff = ImageHlp.getBitmapByte(pic);

			Bundle bundle = null;
			bundle = new Bundle();
			bundle.putByteArray("picture", buff);// 必须.上传照片的文件名以及图片的内容（在发送请求时，图片内容以二进制数据流的形式发送，见下面的请求示例），注意照片名称不能超过30个字符。
			bundle.putString("photodesc", status);// 照片描述，注意照片描述不能超过200个字符。
			bundle.putString("title", "来至课程格子的分享.png");// 照片的命名，必须以.jpg, .gif,
														// .png, .jpeg,
														// .bmp此类后缀结尾。
			mTencent.requestAsync(Constants.GRAPH_UPLOAD_PIC, bundle,
                    Constants.HTTP_POST, new BaseApiListener("upload_pic", callback), null);
		} else if (currentType == Const.QQWeibo) {
			Bitmap pic = ImageHlp.decodeFile(file);
			byte[] buff = null;
			buff = ImageHlp.getBitmapByte(pic);
			Bundle bundle = new Bundle();
			bundle.putString("content", status);
			bundle.putByteArray("pic", buff);
			mTencent.requestAsync("t/add_pic_t", bundle, Constants.HTTP_POST,
					new BaseApiListener("add_pic_t", callback), null);
		}
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void update(String title, String message, String url,
			String imageUrl, String subtitle, String targetUrl,
			SNSCallback callback) {
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("url", url);
		bundle.putString("comment", message);
		bundle.putString("images", imageUrl);
		addShare(bundle, callback);

	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void getFriends(int page, int limit, final SNSCallback callback) {
		// TODO 暂时没用到

	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	public void getAllFriends(final SNSCallback callback) {
		// TODO 暂时没用到
	}

	@Override
	public int getType() {
		return currentType;
	}

	@Override
	public String getTypeName() {
		return currentType == Const.QQ ? "腾讯QQ" : "腾讯空间";
	}

	private void addShare(Bundle bundle, final SNSCallback callback) {
		//bundle.putString("title", "我下载了课程格子，一个很不错的手机课程表应用");// 必须。feeds的标题，最长36个中文字，超出部分会被截断。
		//bundle.putString("url", "http://kechenggezi.com/");// 必须。分享所在网页资源的链接，点击后跳转至第三方网页，请以http://开头。
		//bundle.putString("comment", ("QQ登陆SDK：测试comment" + new Date()));//用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
		//bundle.putString("summary", "QQ登陆SDK：测试summary");//所分享的网页资源的摘要内容，或者是网页的概要描述。 最长80个中文字，超出部分会被截断。
		//bundle.putString("images", "http://ww2.sinaimg.cn/square/71321dd5gw1duvaccv95hj.jpg");// 所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
		succeed = false;
		mTencent.requestAsync(Constants.GRAPH_ADD_SHARE, bundle,
                 Constants.HTTP_POST, new BaseApiListener("add_share", callback), null);
	}

	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(mContext);
			if (msg.what == DataAdapter.MESSAGE_UPDATE_OAUTH) {
				BaseResult result = (BaseResult) msg.obj;
				if (result != null) {
					if (result.success) {
						mSNSCallback.onComplete(TencentAuthHelper.this, null);
					} else {
						mSNSCallback.onError(TencentAuthHelper.this);
						if(mSNSCallback.getNeedAuthHelperProcessMessage()) {
							showTips(result.notice);
						}
					}
				} else {
					mSNSCallback.onError(TencentAuthHelper.this);

					String hintString = (currentType == Const.QQ ? "绑定QQ出错了" : "绑定腾讯微博出错了");
					if(mSNSCallback.getNeedAuthHelperProcessMessage()) {
						showTips(hintString);
					}
				}
			}
		}
	}


	private class BaseUiListener implements IUiListener {

		private SNSCallback mCallback;

		public BaseUiListener(SNSCallback callback) {
            this.mCallback = callback;
        }

		@Override
		public void onComplete(JSONObject response) {
			doComplete(response);
		}

		protected void doComplete(JSONObject values) {
			 try {
	            	String access_token = values.getString("access_token");
					String expires_in = values.getString("expires_in");
	                String openid = values.getString("openid");
	                app.setTencentToken(access_token);
	                app.setTencentOpenid(openid);
	                app.setTencentExpires(System.currentTimeMillis() + Long.parseLong(expires_in) * 1000);
//	                app.setTencentExpires(expires_in);
	                if(mCallback.getNeedAuthHelperProcessMessage()){
		                showTips("腾讯授权成功");
	                }
	                UIUtil.unblock(mContext);
					mCallback.onComplete(TencentAuthHelper.this, null);
				} catch (JSONException e) {
					if(mCallback.getNeedAuthHelperProcessMessage()){
		                showTips("腾讯授权失败");
	                }
					AppLogger.printStackTrace(e);
					UIUtil.unblock(mContext);
					mCallback.onError(TencentAuthHelper.this);
				}
		}

		@Override
		public void onError(UiError e) {
			Log.e("TencentAuthHelper", "code:" + e.errorCode + ", msg:"
					+ e.errorMessage + ", detail:" + e.errorDetail);
			if(mCallback.getNeedAuthHelperProcessMessage()){
                showTips("腾讯授权失败");
            }
			UIUtil.unblock(mContext);
			mCallback.onError(TencentAuthHelper.this);
		}

		@Override
		public void onCancel() {
			Log.e("TencentAuthHelper", "onCancel");
			if(mCallback.getNeedAuthHelperProcessMessage()){
                showTips("腾讯授权失败");
            }
			UIUtil.unblock(mContext);
			mCallback.onError(TencentAuthHelper.this);
		}
	}

	private class BaseApiListener implements IRequestListener {
        private String mScope = "all";
        private Boolean mNeedReAuth = false;
        private SNSCallback mCallback;


        public BaseApiListener(String scope, boolean needReAuth) {
            mScope = scope;
            mNeedReAuth = needReAuth;
        }

        public BaseApiListener(String scope, SNSCallback callback) {
            mScope = scope;
            this.mCallback = callback;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            Log.v(TAG, "IRequestListener.onComplete:" + response.toString());
            succeed = true;
            doComplete(response, state);
        }

        protected void doComplete(final JSONObject response, Object state) {
        	mContext.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					try {
		                int ret = response.getInt("ret");
		                if (ret == 100030) {
		                    if (mNeedReAuth) {
		                        Runnable r = new Runnable() {
		                            public void run() {
		            					// TODO 暂时没想明白怎么做
//		                                mTencent.reAuth(mContext, mScope, listener);
		                            }
		                        };
		                        mContext.runOnUiThread(r);
		                    }
		                }
		                if ("add_share".equals(mScope) || "upload_pic".equals(mScope) || "add_pic_t".equals(mScope) || "add_t".equals(mScope)) {
		                	Log.v(TAG, "同步发表动态到QQ空间或微博返回数据成功");
		                	mCallback.onComplete(TencentAuthHelper.this, null);
		                }else if ("get_simple_userinfo".equals(mScope)) {
		                	CommonUser user = new CommonUser();
							user.avatar = response.getString("figureurl_qq_1");
							user.largeAvatar = response.getString("figureurl_qq_2");
							user.name = response.getString("nickname");
							user.id = getThirdPartId();
							user.gender = response.getString("gender").equals("男") ? Const.MALE : Const.FEMALE;
							user.token = getThirdPartToken();
							if (mCallback != null) {
								mCallback.onComplete(TencentAuthHelper.this, user);
							}
						}
		                // azrael 2/1注释掉了, 这里为何要在api返回的时候设置token呢,
		                // 如果cgi返回的值没有token, 则会清空原来的token
		                // String token = response.getString("access_token");
		                // String expire = response.getString("expires_in");
		                // String openid = response.getString("openid");
		                // mTencent.setAccessToken(token, expire);
		                // mTencent.setOpenId(openid);
		            } catch (JSONException e) {
		                AppLogger.w(response.toString(), e);
		                if (mCallback != null) {
		                	mCallback.onError(TencentAuthHelper.this);
						}
		            }
				}
			});
            

        }

        @Override
        public void onIOException(final IOException e, Object state) {
            Log.v(TAG, "IRequestListener.onIOException:" + e.getMessage());
            if (!succeed) {
            	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
		            	mCallback.onError(TencentAuthHelper.this);
					}
				});
            }
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                Object state) {
            Log.v(TAG, "IRequestListener.onMalformedURLException:" + e.toString());
            if (!succeed) {
            	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
		            	mCallback.onError(TencentAuthHelper.this);
					}
				});
            }
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
            Log.v(TAG, "IRequestListener.onJSONException:" + e.getMessage());
            if (!succeed) {
            	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
		            	mCallback.onError(TencentAuthHelper.this);
					}
				});
            }
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException arg0,
                Object arg1) {
            Log.v(TAG, "IRequestListener.onConnectTimeoutException:" + arg0.getMessage());
            if (!succeed) {
            	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
		            	mCallback.onError(TencentAuthHelper.this);
					}
				});
            }
        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException arg0,
                Object arg1) {
            Log.v(TAG, "IRequestListener.SocketTimeoutException:" + arg0.getMessage());
            if (!succeed) {
            	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
		            	mCallback.onError(TencentAuthHelper.this);
					}
				});
            }
        }

        @Override
        public void onUnknowException(Exception arg0, Object arg1) {
            Log.v(TAG, "IRequestListener.onUnknowException:" + arg0.getMessage());
            if (!succeed) {
            	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
		            	mCallback.onError(TencentAuthHelper.this);
					}
				});
            }
        }

        @Override
        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
            Log.v(TAG, "IRequestListener.HttpStatusException:" + arg0.getMessage());
            if (!succeed) {
            	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
		            	mCallback.onError(TencentAuthHelper.this);
					}
				});
            }
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
            Log.v(TAG, "IRequestListener.onNetworkUnavailableException:" + arg0.getMessage());
            if (!succeed) {
            	mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
		            	mCallback.onError(TencentAuthHelper.this);
					}
				});
            }
        }
    }

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		/**
		 * 下面两个注释掉的代码，仅当sdk支持sso时有效，
		 */
		mTencent.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void getUserInfo(final SNSCallback callback) {
		mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
                Constants.HTTP_GET, new BaseApiListener("get_simple_userinfo", callback), null);
	}

	void showTips(final String tips){
		mContext.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Hint.showTipsShort(mContext, tips);
			}
		});
	}

	/**
	 * 内部会对当前Activity进行block和unblock
	 * 失败之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	@Override
	public void bind(SNSCallback callback) {
		mSNSCallback = callback;
		UIUtil.block(mContext);
		mDataAdapter.updateOauth(getType(), app.getTencentOpenid(), app.getTencentToken(), "", null);
	}
}

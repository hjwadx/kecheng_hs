package fm.jihua.kecheng.utils;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.api.FriendshipsAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.interfaces.SimpleUser;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.helper.Hint;

public class WeiboAuthHelper implements AuthHelper {

	Weibo weibo;
	long mBeginAuthTime;
	long mLastCompleteTime;
	String uid;
	int cursor = 0;

	DataAdapter mDataAdapter;

	Activity mContext;
	SNSCallback mSNSCallback;
	Oauth2AccessToken accessToken;
	SsoHandler ssoHandler;
	List<SimpleUser> friends = new ArrayList<SimpleUser>();

	public WeiboAuthHelper(Activity context) {
		this.mContext = context;
		weibo = Weibo.getInstance(Const.WEIBO_CONSUMER_KEY, "http://kechengbiao.me");
		App app = App.getInstance();
		mDataAdapter = new DataAdapter(mContext, new MyDataCallback());
		if (app.getWeiboToken() != null) {
			accessToken = new Oauth2AccessToken(app.getWeiboToken(), app.getWeiboExpires());
			if (!accessToken.isSessionValid()) {
				accessToken = null;
			}
		}
	}

	public String getThirdPartId(){
		App app = App.getInstance();
		return app.getWeiboId();
	}
	public String getThirdPartToken(){
		App app = App.getInstance();
		return app.getWeiboToken();
	}

//	public void setSNSCallback(SNSCallback callback){
//		mSNSCallback = callback;
//	}

	/**
	 * 内部会对当前Activity进行block和unblock
	 * 失败之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	public void auth(SNSCallback callback){
		if (!isAuthed()) {
			UIUtil.block(mContext);
			String autoFollow = MobclickAgent.getConfigParams(mContext, "auto_follow_weibo");
			if (autoFollow.equals("") || autoFollow.equals("0")) {
				authInner(null, callback);
			}else {
				WeiboParameters parameters = new WeiboParameters();
				parameters.add("scope", "follow_app_official_microblog");
				authInner(parameters, callback);
			}
		}else {
			callback.onComplete(WeiboAuthHelper.this, null);
		}
	}

	private void authInner(WeiboParameters parameters, SNSCallback callback){
		boolean hasSso = false;
		try {
            Class sso=Class.forName("com.weibo.sdk.android.sso.SsoHandler");
            hasSso = true;
        } catch (ClassNotFoundException e) {
//            AppLogger.printStackTrace(e);
            Log.i(Const.TAG, "com.weibo.sdk.android.sso.SsoHandler not found");
        }
		if (parameters == null) {
			parameters = new WeiboParameters();
		}
		if (hasSso) {
			ssoHandler =new SsoHandler(mContext,weibo);
			ssoHandler.authorize( new AuthDialogListener(callback), parameters);
		}else {
			weibo.authorize(mContext, parameters, new AuthDialogListener(callback));
		}
	}

	public void unBind(){
		App app = App.getInstance();
		app.logoutWeibo();
	}

	@Override
	public boolean isAuthed() {
		App app = App.getInstance();
		return accessToken != null && app.getWeiboId() != null;
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	public void update(String status, SNSCallback callback){
		StatusesAPI api = new StatusesAPI(accessToken);
		api.update(status, null, null, new MyRequestListener(SNSCallback.UPLOAD, callback));
    }

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	public void upload(String file, String status, SNSCallback callback){
		StatusesAPI api = new StatusesAPI(accessToken);
		api.upload(status, file, null, null, new MyRequestListener(SNSCallback.UPLOAD, callback));
    }

	public int getType(){
		return Const.WEIBO;
	}

	public String getTypeName(){
		return "新浪微博";
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	public void getFriends(int cursor, int limit, SNSCallback callback) {
		// TODO 暂时没有用到
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 * 失败之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	public void getAllFriends(SNSCallback callback) {
		FriendshipsAPI api = new FriendshipsAPI(accessToken);
		App app = App.getInstance();
		api.friends(Long.parseLong(app.getWeiboId()), Const.WEIBO_COUNT_PER_REQUEST, cursor, true, new MyRequestListener(SNSCallback.GET_ALL_FRIENDS_INFO, callback));
	}

	public void followGezi(){
		FriendshipsAPI api = new FriendshipsAPI(accessToken);
		api.create(Long.parseLong(Const.WEIBO_GEZI_ID), null, null);
	}


	class AuthDialogListener implements WeiboAuthListener {
		private SNSCallback mCallback;

		public AuthDialogListener(SNSCallback callback) {
			this.mCallback = callback;
		}

		@Override
		public void onComplete(Bundle values) {


			String token = values.getString("access_token");
			uid = values.getString("uid");
			App app = App.getInstance();
			app.setWeiboToken(token);
			app.setWeiboId(uid);
			String expires_in = values.getString("expires_in");
			app.setWeiboExpires(expires_in);
			accessToken = new Oauth2AccessToken(token, expires_in);
			accessToken.setExpiresIn(expires_in);
			String forceFollow = MobclickAgent.getConfigParams(mContext, "force_follow_weibo");
			if (forceFollow.equals("1")) {
				followGezi();
			}
			UIUtil.unblock(mContext);
			mCallback.onComplete(WeiboAuthHelper.this, null);
		}

		@Override
		public void onError(WeiboDialogError e) {
			UIUtil.unblock(mContext);
			mCallback.onError(WeiboAuthHelper.this);
			if (mCallback.getNeedAuthHelperProcessMessage()) {
				showTips("微博验证出错: " + e.getMessage());
			}
		}

		@Override
		public void onCancel() {
			UIUtil.unblock(mContext);
			mCallback.onError(WeiboAuthHelper.this);
		}

		@Override
		public void onWeiboException(WeiboException e) {
			UIUtil.unblock(mContext);
			mCallback.onError(WeiboAuthHelper.this);
			if (mCallback.getNeedAuthHelperProcessMessage()) {
				showTips("微博验证异常: " + e.getMessage());
			}
		}

	}

	class MyDataCallback implements DataCallback{
		@Override
		public void callback(Message msg) {
			UIUtil.unblock(mContext);
			if (msg.what == DataAdapter.MESSAGE_UPDATE_OAUTH) {
				BaseResult result = (BaseResult)msg.obj;
				if (result != null) {
					if (result.success) {
						mSNSCallback.onComplete(WeiboAuthHelper.this, null);
					}else {
						mSNSCallback.onError(WeiboAuthHelper.this);
						if(mSNSCallback.getNeedAuthHelperProcessMessage()){
							showTips(result.notice);
						}
					}
				}else {
					mSNSCallback.onError(WeiboAuthHelper.this);
					if(mSNSCallback.getNeedAuthHelperProcessMessage()){
						showTips("绑定微博出错了");
					}
				}
			}
		}
	}

	void showTips(final String tips){
		mContext.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Hint.showTipsShort(mContext, tips);
			}
		});
	}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /**
         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
         */
        if(ssoHandler!=null){
        	ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void getUserInfo(SNSCallback callback) {
		App app = App.getInstance();
		UsersAPI api = new UsersAPI(accessToken);
		api.show(Long.parseLong(app.getWeiboId()), new MyRequestListener(SNSCallback.GET_USER_INFO, callback));
	}

	/**
	 * 内部会对当前Activity进行block和unblock
	 * 失败之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	@Override
	public void bind(SNSCallback callback) {
		UIUtil.block(mContext);
		mSNSCallback = callback;
		getUserInfo(new MyCallback(SNSCallback.GET_USER_INFO));
	}

	private class MyRequestListener implements RequestListener{
		private SNSCallback mCallback;
		private int mScope = 0;

		public MyRequestListener(int scope ,SNSCallback callback) {
			mCallback = callback;
			mScope = scope;
		}

		@Override
		public void onComplete(final String response) {
			mContext.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					switch (mScope) {
					case SNSCallback.UPLOAD:
						mCallback.onComplete(WeiboAuthHelper.this, null);
						break;
					case SNSCallback.GET_ALL_FRIENDS_INFO:
						try {
							JSONObject json;
							json = new JSONObject(response);
							JSONArray array = json.getJSONArray("users");
							if (array != null) {
								for (int i = 0; i < array.length(); i++) {
									SimpleUser user = new SimpleUser();
									JSONObject jsonObject = new JSONObject(array.get(i).toString());
									user.id = jsonObject.getString("id");
									user.name = jsonObject.getString("screen_name");
									user.avatar = jsonObject.getString("avatar_large");
									// user.sex =
									// jsonObject.getString("gender").equals("m")?1:0;
									friends.add(user);
								}
							}
							if (Integer.parseInt(json.getString("next_cursor")) != 0) {
								cursor = Integer.parseInt(json.getString("next_cursor"));
								getAllFriends(mCallback);
							} else if (mCallback != null) {
								mCallback.onComplete(WeiboAuthHelper.this, friends);
							}
						} catch (Exception e) {
							AppLogger.printStackTrace(e);
							mCallback.onError(WeiboAuthHelper.this);
							if (mCallback.getNeedAuthHelperProcessMessage()) {
								showTips("获取好友信息出错了");
							}
						}
						break;
					case SNSCallback.GET_USER_INFO:
						try {
							JSONObject json;
							json = new JSONObject(response);
							CommonUser user = new CommonUser();
							user.avatar = json.getString("profile_image_url");
							user.largeAvatar = json.getString("avatar_large");
							user.name = json.getString("name");
							user.id = json.getString("id");
							user.gender = json.getString("gender").equals("m") ? Const.MALE : Const.FEMALE;
							user.token = App.getInstance().getWeiboToken();
							// JSONArray education =
							// json.getJSONArray("education");
							// SchoolInfo[] schoolInfos = new SchoolInfo[0];
							// for (int i=0; i< education.length(); i++) {
							// JSONObject schoolJSONInfo =
							// education.getJSONObject(i);
							// SchoolInfo schoolInfo = new SchoolInfo();
							// schoolInfo.school =
							// schoolJSONInfo.getString("name");
							// schoolInfo.department =
							// schoolJSONInfo.getString("department");
							// schoolInfo.year = schoolJSONInfo.getInt("year");
							// schoolInfos[i] = schoolInfo;
							// }
							// user.schoolInfos = schoolInfos;
							mCallback.onComplete(WeiboAuthHelper.this, user);
						} catch (Exception e) {
							AppLogger.printStackTrace(e);
							mCallback.onError(WeiboAuthHelper.this);
						}
						break;
					}
				}
			});

		}

		@Override
		public void onIOException(IOException e) {
			AppLogger.printStackTrace(e);
			if (mCallback != null) {
				mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mCallback.onError(WeiboAuthHelper.this);
					}
				});
			}
		}

		@Override
		public void onError(WeiboException e) {
			if (mCallback != null) {
				mContext.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mCallback.onError(WeiboAuthHelper.this);
					}
				});
			}
		}
	}

	private class MyCallback implements SNSCallback {

		private int mScope = 0;

		public MyCallback(int scope) {
            mScope = scope;
        }

		@Override
		public void onComplete(AuthHelper authHelper, Object data) {
			switch (mScope) {
			case SNSCallback.GET_USER_INFO:
				CommonUser user = (CommonUser)data;
				App app = App.getInstance();
				Date expire = new Date(System.currentTimeMillis() + Long.parseLong(app.getWeiboExpires())*1000);
				mDataAdapter.updateOauth(getType(), app.getWeiboId(), app.getWeiboToken(), user.name, expire);
				break;
			}
		}

		@Override
		public void onError(AuthHelper authHelper) {
			switch (mScope) {
			case SNSCallback.GET_USER_INFO:
				App app = App.getInstance();
				Date expire = new Date(System.currentTimeMillis() + Long.parseLong(app.getWeiboExpires())*1000);
				mDataAdapter.updateOauth(getType(), app.getWeiboId(), app.getWeiboToken(), "", expire);
				break;
			}
		}

		@Override
		public boolean getNeedAuthHelperProcessMessage() {
			return true;
		}
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void update(String title, String message, String url,
			String imageUrl, String subtitle, String targetUrl,
			SNSCallback callback) {
		// TODO 暂时没用到

	}
}

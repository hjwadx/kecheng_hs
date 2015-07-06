package fm.jihua.kecheng.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.GetLoginUserParam;
import com.renn.rennsdk.param.GetUserParam;
import com.renn.rennsdk.param.ListUserFriendParam;
import com.renn.rennsdk.param.PutStatusParam;
import com.renn.rennsdk.param.UploadPhotoParam;

import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.kecheng.interfaces.AuthHelper;
import fm.jihua.kecheng.interfaces.SNSCallback;
import fm.jihua.kecheng.interfaces.SimpleUser;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.helper.Hint;

public class RenrenAuthHelper implements AuthHelper {


	private RennClient rennClient;


	List<SimpleUser> friends = new ArrayList<SimpleUser>();


    int mPage = 1;
	long mBeginAuthTime;
	long mLastCompleteTime;

	DataAdapter mDataAdapter;

	Activity mContext;
	SNSCallback mSNSCallback;

	public RenrenAuthHelper(Activity context) {
		this.mContext = context;
		rennClient = RennClient.getInstance(mContext);
		rennClient.init(Const.APP_ID, Const.API_KEY, Const.SECRET_KEY);
		rennClient.setScope(Const.RENREN_SCOPE);
		rennClient.setTokenType("bearer");
		mDataAdapter = new DataAdapter(mContext, new MyDataCallback());
	}

	public String getThirdPartId(){
		if (rennClient != null && isAuthed()) {
			return String.valueOf(rennClient.getUid());
		}
		return null;
	}
	public String getThirdPartToken(){
		if (rennClient != null && isAuthed()) {
			return rennClient.getAccessToken().accessToken;
		}
		return null;
	}

//	public void setSNSCallback(SNSCallback callback){
//		mSNSCallback = callback;
//	}

	/**
	 * 内部会对当前Activity进行block和unblock
	 * 成功或取消之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	public void auth(final SNSCallback callback){
		if (!isAuthed()) {
			mBeginAuthTime = System.currentTimeMillis();
			rennClient.setLoginListener(new LoginListener() {
				@Override
				public void onLoginSuccess() {
					if (mLastCompleteTime < mBeginAuthTime) {
						mLastCompleteTime = System.currentTimeMillis();
						if(callback.getNeedAuthHelperProcessMessage()){
			                showTips("人人授权成功");
		                }
						callback.onComplete(RenrenAuthHelper.this, null);
					}
					UIUtil.unblock(mContext);
				}

				@Override
				public void onLoginCanceled() {
					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							UIUtil.unblock(mContext);
							if(callback.getNeedAuthHelperProcessMessage()){
				                showTips("取消授权成功");
			                }
							callback.onError(RenrenAuthHelper.this);
						}
					});
				}
			});
			UIUtil.block(mContext);
			rennClient.login(mContext);
		}else {
			callback.onComplete(RenrenAuthHelper.this, null);
		}
	}



	/**
	 * 内部会对当前Activity进行block和unblock
	 * 失败之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	@Override
	public void bind(SNSCallback callback) {
		UIUtil.unblock(mContext);
		mSNSCallback = callback;
		GetLoginUserParam param5 = new GetLoginUserParam();
		try {
            rennClient.getRennService().sendAsynRequest(param5, new CallBack() {
                @Override
                public void onSuccess(RennResponse response) {
                	String name = "";
                	try {
                		name = response.getResponseObject().getString("name");
					} catch (JSONException e) {
						AppLogger.printStackTrace(e);
					}
					mDataAdapter.updateOauth(getType(), String.valueOf(rennClient.getUid()), rennClient.getAccessToken().accessToken, name, null);
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
					mDataAdapter.updateOauth(getType(), String.valueOf(rennClient.getUid()), rennClient.getAccessToken().accessToken, "", null);
                }
            });
        } catch (RennException e1) {
        	callback.onError(RenrenAuthHelper.this);
        	UIUtil.unblock(mContext);
            e1.printStackTrace();
        }
	}

	public void unBind(){
		rennClient.logout();
	}

	public int getType(){
		return Const.RENREN;
	}

	public String getTypeName(){
		return "人人";
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		rennClient.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean isAuthed() {
		return rennClient.getAccessToken() != null && rennClient.isLogin();
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void update(String status, final SNSCallback callback) {
		PutStatusParam putStatusParam = new PutStatusParam();
        putStatusParam.setContent(status);
        try {
            rennClient.getRennService().sendAsynRequest(putStatusParam, new CallBack() {

                @Override
                public void onSuccess(RennResponse response) {
                    callback.onComplete(RenrenAuthHelper.this, null);
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                    callback.onError(RenrenAuthHelper.this);
                }
            });
        } catch (RennException e1) {
        	callback.onError(RenrenAuthHelper.this);
            e1.printStackTrace();
        }
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void upload(String file, String status, final SNSCallback callback) {
		UploadPhotoParam param = new UploadPhotoParam();
		 try{
        	 param.setFile(new File(file));
             param.setDescription(status);
        }catch(Exception e){
        }
		 try {
             rennClient.getRennService().sendAsynRequest(param, new CallBack() {

                 @Override
				public void onSuccess(RennResponse response) {
					callback.onComplete(RenrenAuthHelper.this, null);
				}

                 @Override
                 public void onFailed(String errorCode, String errorMessage) {
					callback.onError(RenrenAuthHelper.this);
				}
             });
         } catch (RennException e1) {
        	 callback.onError(RenrenAuthHelper.this);
             e1.printStackTrace();
         }
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 * 失败之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	public void getFriends(int page, final int limit, final SNSCallback callback){
		ListUserFriendParam param4 = new ListUserFriendParam();
        param4.setUserId(rennClient.getUid());
        param4.setPageSize(limit);
        param4.setPageNumber(page);
        try {
            rennClient.getRennService().sendAsynRequest(param4, new CallBack() {

                @Override
                public void onSuccess(RennResponse response) {
     				try {
						if (response != null && response.getResponseArray().length() > 0) {
							for (int i = 0; i < response.getResponseArray().length(); i++){
								SimpleUser user = new SimpleUser();
								user.avatar = response.getResponseArray().getJSONObject(i).getJSONArray("avatar").getJSONObject(1).getString("url");
								user.name = response.getResponseArray().getJSONObject(i).getString("name");
								user.id = response.getResponseArray().getJSONObject(i).getString("id");
								friends.add(user);
							}
							getFriends(++mPage, limit, callback);
						}else if(callback != null){
							callback.onComplete(RenrenAuthHelper.this, friends);
						}
					} catch (JSONException e) {
						callback.onError(RenrenAuthHelper.this);
						AppLogger.printStackTrace(e);
					}
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                	if (callback.getNeedAuthHelperProcessMessage()) {
						showTips("获取失败");
					}
					if (callback != null) {
						callback.onError(RenrenAuthHelper.this);
					}
                }
            });
        } catch (RennException e1) {
        	if (callback.getNeedAuthHelperProcessMessage()) {
				showTips("获取失败");
			}
        	callback.onError(RenrenAuthHelper.this);
            e1.printStackTrace();
        }
	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 * 失败之后有基本的Toast提示，可以根据SNSCallback的getNeedAuthHelperProcessMessage()来控制是否显示
	 */
	public void getAllFriends(final SNSCallback callback) {
		ListUserFriendParam param4 = new ListUserFriendParam();
        param4.setUserId(rennClient.getUid());
        param4.setPageSize(Const.RENREN_COUNT_PER_REQUEST);
        param4.setPageNumber(mPage);
        try {
            rennClient.getRennService().sendAsynRequest(param4, new CallBack() {

                @Override
                public void onSuccess(RennResponse response) {
     				try {
						if (response != null && response.getResponseArray().length() > 0) {
							for (int i = 0; i < response.getResponseArray().length(); i++){
								SimpleUser user = new SimpleUser();
								user.avatar = response.getResponseArray().getJSONObject(i).getJSONArray("avatar").getJSONObject(0).getString("url");
								user.name = response.getResponseArray().getJSONObject(i).getString("name");
								user.id = response.getResponseArray().getJSONObject(i).getString("id");
								friends.add(user);
							}
							mPage++;
							getAllFriends(callback);
						}else if(callback != null){
							callback.onComplete(RenrenAuthHelper.this, friends);
						}
					} catch (JSONException e) {
						callback.onError(RenrenAuthHelper.this);
						AppLogger.printStackTrace(e);
					}
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                	if (callback.getNeedAuthHelperProcessMessage()) {
						showTips("获取失败");
					}
					if (callback != null) {
						callback.onError(RenrenAuthHelper.this);
					}
                }
            });
        } catch (RennException e1) {
        	if (callback.getNeedAuthHelperProcessMessage()) {
				showTips("获取失败");
			}
        	callback.onError(RenrenAuthHelper.this);
            e1.printStackTrace();
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
						mSNSCallback.onComplete(RenrenAuthHelper.this, null);
					}else {
						mSNSCallback.onError(RenrenAuthHelper.this);
						if (mSNSCallback.getNeedAuthHelperProcessMessage()) {
							showTips(result.notice);
						}
						rennClient.logout();
					}
				}else {
					mSNSCallback.onError(RenrenAuthHelper.this);
					if (mSNSCallback.getNeedAuthHelperProcessMessage()) {
						showTips("绑定人人出错了");
					}
					rennClient.logout();
				}
			}
		}

	}

	/**
	 * 内部不会对当前Activity进行block和unblock，需要自行处理
	 */
	@Override
	public void getUserInfo(final SNSCallback callback) {
		GetUserParam param = new GetUserParam();
		param.setUserId(rennClient.getUid());
		try {
			rennClient.getRennService().sendAsynRequest(param, new CallBack() {

				@Override
				public void onSuccess(final RennResponse response) {
					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (response != null) {
								try {
									JSONObject renrenJson = response
											.getResponseObject();
									CommonUser user = new CommonUser();
									user.avatar = renrenJson.getJSONArray("avatar").getJSONObject(1).getString("url");
									user.largeAvatar = renrenJson.getJSONArray("avatar").getJSONObject(2).getString("url");
									user.name = renrenJson.getString("name");
									user.id = renrenJson.getString("id");
									user.gender = renrenJson.getJSONObject("basicInformation").getString("sex").equals("MALE") ? Const.MALE : Const.FEMALE;
									user.token = getThirdPartToken();
									JSONArray education = renrenJson.getJSONArray("education");
									SchoolInfo[] schoolInfos = new SchoolInfo[education.length()];
									for (int i=0; i< education.length(); i++) {
										JSONObject schoolJSONInfo = education.getJSONObject(i);
										SchoolInfo schoolInfo = new SchoolInfo();
										schoolInfo.school = schoolJSONInfo.getString("name");
										schoolInfo.department = schoolJSONInfo.getString("department");
										schoolInfo.year = schoolJSONInfo.getInt("year");
										schoolInfos[i] = schoolInfo;
									}
									user.schoolInfos = schoolInfos;
									callback.onComplete(RenrenAuthHelper.this, user);
								} catch (JSONException e) {
									AppLogger.printStackTrace(e);
									callback.onError(RenrenAuthHelper.this);
								}
							}
						}
					});
				}

				@Override
				public void onFailed(String errorCode, String errorMessage) {
					mContext.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							callback.onError(RenrenAuthHelper.this);
						}
					});
				}
			});
		} catch (RennException e) {
			callback.onError(RenrenAuthHelper.this);
			AppLogger.printStackTrace(e);
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

	void showTips(final String tips){
		mContext.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Hint.showTipsShort(mContext, tips);
			}
		});
	}
}

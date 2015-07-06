package com.tencent.tauth;

import android.os.Bundle;

import com.tencent.tauth.bean.Album;
import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.bean.Pic;
import com.tencent.tauth.bean.TopicRichInfo;
import com.tencent.tauth.bean.UserInfo;
import com.tencent.tauth.bean.UserProfile;
import com.tencent.tauth.http.AsyncHttpPostRunner;
import com.tencent.tauth.http.AsyncHttpRequestRunner;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.IRequestListener;
import com.tencent.tauth.http.TDebug;
import com.tencent.tauth.http.RequestListenerImpl.AddAlbumListener;
import com.tencent.tauth.http.RequestListenerImpl.AddShareListener;
import com.tencent.tauth.http.RequestListenerImpl.AddTopicListener;
import com.tencent.tauth.http.RequestListenerImpl.AddWeiBoListener;
import com.tencent.tauth.http.RequestListenerImpl.ListAlbumListener;
import com.tencent.tauth.http.RequestListenerImpl.OpenIDListener;
import com.tencent.tauth.http.RequestListenerImpl.UploadPicListener;
import com.tencent.tauth.http.RequestListenerImpl.UserInfoListener;
import com.tencent.tauth.http.RequestListenerImpl.UserProfileListener;

public class TencentOpenAPI {
	private static final String TAG = "TencentOpenAPI";
	
	/**
	 * 获取 open id</br>
	 * 获取数据成功：在回调函数中返回{@link OpenId}实例</br>
	 * 获取数据失败：在回调函数中返回ret(返回码)和msg(错误信息)</br>
	 * 
	 * @author John.Meng<arzen1013@gmail> QQ:3440895
	 * @date 2011-9-5
	 */
	public static void openid(String access_token, Callback callback) {
		String url = String.format(TencentOpenHost.GRAPH_OPENID_URL, access_token);
		asyncRequest(url, new OpenIDListener(callback));
	}
	
	/**
	 * 获取用户信息</br>
	 * 获取数据成功：在回调函数中返回{@link UserInfo}实例</br>
	 * 获取数据失败：在回调函数中返回ret(返回码)和msg(错误信息)</br>
	 * @author John.Meng<arzen1013@gmail> QQ:3440895
	 * @date 2011-9-5
	 */
	public static void userInfo(String access_token, String appid, String openid, Callback callback) {
		String url = String.format(TencentOpenHost.GRAPH_USERINFO_URL, access_token, appid, openid);
		asyncRequest(url, new UserInfoListener(callback));
	}
	
	/**
	* 获取用户真实信息</br>
	* 获取数据成功：在回调函数中返回{@link UserProfile}</code></br>
	* 获取数据失败：在回调函数中返回ret(返回码)和msg(错误信息)</br>
	* @param access_token
	* @param appid
	* @param openid
	* @param callback
	*/
	public static void userProfile(String access_token, String appid, String openid, Callback callback) {
		String url = String.format(TencentOpenHost.GRAPH_USERPROFILE_URL, access_token, appid, openid);
		asyncRequest(url, new UserProfileListener(callback));
	}
	
	/**
	* 同步发表动态到QQ空间</br>
	* 同步成功：在回调函数中返回分享的Id</br>
	* 同步成功：在回调函数中返回ret(返回码)和msg(错误信息)</br>
	* @param access_token
	* @param appid
	* @param openid
	* @param parameters
	* @param callback
	*/
	public static void addShare(String access_token, String appid, String openid, Bundle parameters, Callback callback) {
		parameters.putString("format", "json");//定义API返回的数据格式.取值说明：为xml时表示返回的格式是xml；为json时表示返回的格式是json。
		parameters.putString("access_token", access_token);
		parameters.putString("oauth_consumer_key", appid);
		parameters.putString("openid", openid);

		asyncPost(TencentOpenHost.GRAPH_ADD_SHARE, parameters, new AddShareListener(callback));
	}
	
	/**
	 * 分享到腾讯微博(上传一张图片，并发布一条消息到腾讯微博平台上)
	 * @param access_token
	 * @param appid
	 * @param openid
	 * @param parameters
	 * @param callback
	 */
	public static void addWeiBoPic(String access_token, String appid, String openid, Bundle parameters, Callback callback) {
		parameters.putString("format", "json");//定义API返回的数据格式.取值说明：为xml时表示返回的格式是xml；为json时表示返回的格式是json。
		parameters.putString("access_token", access_token);
		parameters.putString("oauth_consumer_key", appid);
		parameters.putString("openid", openid);
		
		asyncPost(TencentOpenHost.GRAPH_ADD_PIC_T, parameters, new AddWeiBoListener(callback));
	}
	
	/**
	 * 分享到腾讯微博(没有图片)
	 * @param access_token
	 * @param appid
	 * @param openid
	 * @param parameters
	 * @param callback
	 */
	public static void addWeiBoNormal(String access_token, String appid, String openid, Bundle parameters, Callback callback) {
		parameters.putString("format", "json");//定义API返回的数据格式.取值说明：为xml时表示返回的格式是xml；为json时表示返回的格式是json。
		parameters.putString("access_token", access_token);
		parameters.putString("oauth_consumer_key", appid);
		parameters.putString("openid", openid);
		
		asyncPost(TencentOpenHost.GRAPH_ADD_T, parameters, new AddWeiBoListener(callback));
	}
	
	/**
	* 发表说说</br>
	* 发表说说成功：在回调函数中返回{@link TopicRichInfo}实例</br>
	* 发表说说失败：在回调函数中返回ret(返回码)和msg(错误信息)</br>
	* @author email:csshuai2009@gmail.com qq:65112183
	* @version 创建时间：2011-9-20 上午11:11:19
	* @param access_token
	* @param appid
	* @param openid
	* @param parameters
	* @param callback
	*/
	public static void addTopic(String access_token, String appid, String openid, Bundle parameters, Callback callback) {
		parameters.putString("format", "json");//定义API返回的数据格式.取值说明：为xml时表示返回的格式是xml；为json时表示返回的格式是json。
//		parameters.putString("third_source", "0");//第三方应用的平台类型。1表示QQ空间； 2表示腾讯朋友； 3表示腾讯微博平台； 4表示腾讯Q+平台。
		parameters.putString("access_token", access_token);
		parameters.putString("oauth_consumer_key", appid);
		parameters.putString("openid", openid);
		
		asyncPost(TencentOpenHost.GRAPH_ADD_TOPIC, parameters, new AddTopicListener(callback));
	}
	
	/**
	* 获取相册列表</br>
	* 获取列表成功：在回调函数中返回{@link Album}集合</br>
	* 获取列表失败：在回调函数中返回ret(返回码)和msg(错误信息)</br>
	* @author email:csshuai2009@gmail.com qq:65112183
	* @version 创建时间：2011-9-20 上午11:16:13
	* @param access_token
	* @param appid
	* @param openid
	* @param callback
	*/
	public static void listAlbum(String access_token, String appid, String openid, Callback callback) {
		String url = String.format(TencentOpenHost.GRAPH_LIST_ALBUM, access_token, appid, openid);
		asyncRequest(url, new ListAlbumListener(callback));
	}
	
	/**
	* 创建相册</br>
	* 创建相册成功：在回调函数中返回{@link Album}实例</br>
	* 创建相册失败：在回调函数中返回ret(返回码)和msg(错误信息)</br>
	* @author email:csshuai2009@gmail.com qq:65112183
	* @version 创建时间：2011-9-20 上午11:18:54
	* @param access_token
	* @param appid
	* @param openid
	* @param parameters
	* @param callback
	*/
	public static void addAlbum(String access_token, String appid, String openid, Bundle parameters, Callback callback) {
		parameters.putString("format", "json");//定义API返回的数据格式.取值说明：为xml时表示返回的格式是xml；为json时表示返回的格式是json。
		parameters.putString("access_token", access_token);
		parameters.putString("oauth_consumer_key", appid);
		parameters.putString("openid", openid);
		
		asyncPost(TencentOpenHost.GRAPH_ADD_ALBUM, parameters, new AddAlbumListener(callback));
	}
	
	/**
	* 上传照片</br>
	* 上传照片成功：在回调函数中返回{@link Pic}实例</br>
	* 上传照片失败：在回调函数中返回ret(返回码)和msg(错误信息)</br>
	* @author email:csshuai2009@gmail.com qq:65112183
	* @version 创建时间：2011-9-20 上午11:20:02
	* @param access_token
	* @param appid
	* @param openid
	* @param parameters
	* @param callback
	*/
	public static void uploadPic(String access_token, String appid, String openid, Bundle parameters, Callback callback) {
		parameters.putString("format", "json");//定义API返回的数据格式.取值说明：为xml时表示返回的格式是xml；为json时表示返回的格式是json。
		parameters.putString("access_token", access_token);
		parameters.putString("oauth_consumer_key", appid);
		parameters.putString("openid", openid);
		
		asyncPost(TencentOpenHost.GRAPH_UPLOAD_PIC, parameters, new UploadPicListener(callback));
	}
	
	private static void asyncRequest(String url, IRequestListener listener) {
		TDebug.i(TAG, url);
		(new AsyncHttpRequestRunner()).request(url, null, listener);
	}
	
	private static void asyncPost(String url, Bundle parameters, IRequestListener listener) {
		TDebug.i(TAG, url);
		(new AsyncHttpPostRunner()).request(url, parameters, listener);
	}

}

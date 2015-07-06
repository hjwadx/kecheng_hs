package com.tencent.tauth;

public class TencentOpenHost {
	// 获取access token 的地址 GET
	public static final String GRAPH_ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/authorize?response_type=token&display=mobile&client_id=%s&scope=%s&redirect_uri=%s&status_userip=%s&status_os=%s&status_machine=%s&status_version=%s";
	// 获取open id 的地址 GET
	public static final String GRAPH_OPENID_URL = "https://graph.qq.com/oauth2.0/me?access_token=%s";
	// 获取 用户信息 的地址 GET
//	public static final String GRAPH_USERINFO_URL = "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s";
	public static final String GRAPH_USERINFO_URL = "https://graph.qq.com/user/get_simple_userinfo?access_token=%s&oauth_consumer_key=%s&openid=%s";
	// 获取 用户真实信息 的地址 GET
	public static final String GRAPH_USERPROFILE_URL = "https://graph.qq.com/user/get_user_profile?access_token=%s&oauth_consumer_key=%s&openid=%s";
	// 同步发表动态到QQ空间 POST
	public static final String GRAPH_ADD_SHARE = "https://graph.qq.com/share/add_share";
	// 同步发表动态到腾讯微博 POST
	public static final String GRAPH_ADD_WEIBO = "https://graph.qq.com/wb/add_weibo";
	// 同步发表动态到腾讯微博 POST 这个接口逐渐替代ADD_WEIBO
	public static final String GRAPH_ADD_WEIBO_T = "https://graph.qq.com/t/add_t";
	// 发表一条普通的微博，不带图片
	public static final String GRAPH_ADD_T = "https://graph.qq.com/t/add_t";
	// 发表一条带图片的微博
	public static final String GRAPH_ADD_PIC_T = "https://graph.qq.com/t/add_pic_t";
	// 发表说说 POST
	public static final String GRAPH_ADD_TOPIC = "https://graph.qq.com/shuoshuo/add_topic";
	// 获取相册列表 GET
	public static final String GRAPH_LIST_ALBUM = "https://graph.qq.com/photo/list_album?access_token=%s&oauth_consumer_key=%s&openid=%s";
	// 上传照片 POST
	public static final String GRAPH_UPLOAD_PIC = "https://graph.qq.com/photo/upload_pic";
	// 创建相册 POST
	public static final String GRAPH_ADD_ALBUM = "https://graph.qq.com/photo/add_album";

}

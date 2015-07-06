package com.tencent.tauth.http;
/**
 * @author email:csshuai2009@gmail.com qq:65112183
 * @version 创建时间：2011-9-16 上午9:43:55
 * 类说明
 */
public interface Callback {
	void onSuccess(Object obj);
	void onFail(int ret, String msg);
}

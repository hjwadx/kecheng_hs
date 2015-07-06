package com.tencent.tauth.bean;
/**
 * Open ID
 * @author email:csshuai2009@gmail.com qq:65112183
 * @version 创建时间：2011-9-16 上午9:56:04
 */
public class OpenId {
	private String mOpenId;
	private String mClientId;
	public OpenId(String openId, String clientId) {
		this.mOpenId = openId;
		this.mClientId = clientId;
	}
	/**
	* @return open id
	*/
	public String getOpenId() {
		return mOpenId;
	}
	public void setOpenId(String openId) {
		this.mOpenId = openId;
	}
	/**
	* @return client id
	*/
	public String getClientId() {
		return mClientId;
	}
	public void setClientId(String clientId) {
		this.mClientId = clientId;
	}
	
	@Override
	public String toString() {
		return mOpenId;
	}
}

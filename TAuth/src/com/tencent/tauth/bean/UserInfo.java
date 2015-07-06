package com.tencent.tauth.bean;
/**
 * 用户信息
 * @author email:csshuai2009@gmail.com qq:65112183
 * @version 创建时间：2011-9-15 下午4:50:17
 */
public class UserInfo {
	private String mNickName;
	private String mIcon_30;
	private String mIcon_50;
	private String mIcon_100;
	private String mGender;
	public UserInfo(String nickName, String icon_30,
			String icon_50, String icon_100, String gender) {
		this.mNickName = nickName;
		this.mIcon_30 = icon_30;
		this.mIcon_50 = icon_50;
		this.mIcon_100 = icon_100;
		this.mGender = gender;
	}
	/**
	* @return 用户昵称
	*/
	public String getNickName() {
		return mNickName;
	}
	public void setNickName(String nickName) {
		this.mNickName = nickName;
	}
	/**
	* @return 用户头像(30)
	*/
	public String getIcon_30() {
		return mIcon_30;
	}
	public void setIcon_30(String icon_30) {
		this.mIcon_30 = icon_30;
	}
	/**
	* @return 用户头像(50)
	*/
	public String getIcon_50() {
		return mIcon_50;
	}
	public void setIcon_50(String icon_50) {
		this.mIcon_50 = icon_50;
	}
	/**
	* @return 用户头像(100)
	*/
	public String getIcon_100() {
		return mIcon_100;
	}
	public void setIcon_100(String icon_100) {
		this.mIcon_100 = icon_100;
	}
	
	public String getGender() {
		return mGender;
	}
	public void setGender(String gender) {
		this.mGender = gender;
	}
	
	@Override
	public String toString() {
		return "nickname: " + mNickName + "\nicon_30: " + mIcon_30 + "\nicon_50: " + mIcon_50 + "\nicon_100: " + mIcon_100 + "\ngender: " + mGender +"\n";
	}
}

package com.tencent.tauth.bean;
/**
 * 用户真实信息
 * @author email:csshuai2009@gmail.com qq:65112183
 * @version 创建时间：2011-9-15 下午4:50:17
 */
public class UserProfile {
	private String mRealName;
	private int mGender;
	private String mIcon_30;
	private String mIcon_50;
	private String mIcon_100;
	public UserProfile(String realName, int gender, String icon_30,
			String icon_50, String icon_100) {
		this.mRealName = realName;
		this.mGender = gender;
		this.mIcon_30 = icon_30;
		this.mIcon_50 = icon_50;
		this.mIcon_100 = icon_100;
	}
	/**
	* @return 用户真实姓名
	*/
	public String getRealName() {
		return mRealName;
	}
	public void setNickName(String realName) {
		this.mRealName = realName;
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
	/**
	* @return 1为男，0为女
	*/
	public int getGender() {
		return mGender;
	}
	public void setGender(int gender) {
		this.mGender = gender;
	}
	
	@Override
	public String toString() {
		return "realName: " + mRealName + "\ngender: " + (mGender == 0 ? "女" : "男") + "\nicon_30: " + mIcon_30 + "\nicon_50: " + mIcon_50 + "\nicon_100: " + mIcon_100 + "\n";
	}
}

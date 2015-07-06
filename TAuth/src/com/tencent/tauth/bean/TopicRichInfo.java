package com.tencent.tauth.bean;
/**
 * 发表说说返回的信息
 * @author email:csshuai2009@gmail.com qq:65112183
 * @version 创建时间：2011-9-19 下午3:02:35
 */
public class TopicRichInfo {
	private int mRtype;
	private String mUrl2;
	private String mUrl3;
	private int mWho;
	public TopicRichInfo(int rtype, String url2, String url3, int who) {
		this.mRtype = rtype;
		this.mUrl2 = url2;
		this.mUrl3 = url3;
		this.mWho = who;
	}
	public int getRtype() {
		return mRtype;
	}
	public void setRtype(int rtype) {
		this.mRtype = rtype;
	}
	public String getUrl2() {
		return mUrl2;
	}
	public void setUrl2(String url2) {
		this.mUrl2 = url2;
	}
	public String getUrl3() {
		return mUrl3;
	}
	public void setUrl3(String url3) {
		this.mUrl3 = url3;
	}
	public int getWho() {
		return mWho;
	}
	public void setWho(int who) {
		this.mWho = who;
	}
	
	@Override
	public String toString() {
		return "rtype: " + mRtype + "\nurl2: " + mUrl2 + "\nurl3: " + mUrl3 + "\nwho: " + mWho + "\n";
	}
}

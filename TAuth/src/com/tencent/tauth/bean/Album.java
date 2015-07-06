package com.tencent.tauth.bean;
/**
 * 相册信息
 * @author email:csshuai2009@gmail.com qq:65112183
 * @version 创建时间：2011-9-19 下午3:26:10
 */
public class Album {
	private String mAlbumid;
	private int mClassid;
	private long mCreatetime;
	private String mDesc;
	private String mName;
	private long mPicnum;
	private int mPriv;
	public Album(String albumid, int classid, long createtime, String desc,
			String name, long picnum, int priv) {
		this.mAlbumid = albumid;
		this.mClassid = classid;
		this.mCreatetime = createtime;
		this.mDesc = desc;
		this.mName = name;
		this.mPicnum = picnum;
		this.mPriv = priv;
	}
	/**
	* @return 相册的Id
	*/
	public String getAlbumid() {
		return mAlbumid;
	}
	public void setAlbumid(String albumid) {
		this.mAlbumid = albumid;
	}
	public int getClassid() {
		return mClassid;
	}
	public void setClassid(int classid) {
		this.mClassid = classid;
	}
	/**
	* @return 相册创建时间
	*/
	public long getCreatetime() {
		return mCreatetime;
	}
	public void setCreatetime(long createtime) {
		this.mCreatetime = createtime;
	}
	/**
	* @return  相册描述
	*/
	public String getDesc() {
		return mDesc;
	}
	public void setDesc(String desc) {
		this.mDesc = desc;
	}
	/**
	* @return 相册名称
	*/
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	/**
	* @return 相册内图片数量
	*/
	public long getPicnum() {
		return mPicnum;
	}
	public void setPicnum(long picnum) {
		this.mPicnum = picnum;
	}
	/**
	* @return 相册权限
	*/
	public int getPriv() {
		return mPriv;
	}
	public void setPriv(int priv) {
		this.mPriv = priv;
	}
	
	@Override
	public String toString() {
		return "albumid: " + mAlbumid + 
				"\nclassid: " + mClassid + 
				"\ncreatetime: " + mCreatetime + 
				"\ndesc: " + mDesc + 
				"\nname: " + mName + 
				"\npicnum: " + mPicnum + 
				"\npriv: " + mPriv + 
				"\n"
				;
	}
}

package com.tencent.tauth.bean;
/**
 * 图片信息
 * @author email:csshuai2009@gmail.com qq:65112183
 * @version 创建时间：2011-9-19 下午5:58:11
 */
public class Pic {
	private String mAlbumId;
	private String mLloc;
	private String mSloc;
	private int mWidth;
	private int mHeight;
	public Pic(String albumId, String lloc, String sloc, int width,
			int height) {
		this.mAlbumId = albumId;
		this.mLloc = lloc;
		this.mSloc = sloc;
		this.mWidth = width;
		this.mHeight = height;
	}
	/**
	* @return 相册Id
	*/
	public String getAlbumId() {
		return mAlbumId;
	}
	public void setAlbumId(String albumId) {
		this.mAlbumId = albumId;
	}
	/**
	* @return 大图Id
	*/
	public String getLloc() {
		return mLloc;
	}
	public void setLloc(String lloc) {
		this.mLloc = lloc;
	}
	/**
	* @return 小图Id
	*/
	public String getSloc() {
		return mSloc;
	}
	public void setSloc(String sloc) {
		this.mSloc = sloc;
	}
	/**
	* @return 图片宽度
	*/
	public int getWidth() {
		return mWidth;
	}
	public void setWidth(int width) {
		this.mWidth = width;
	}
	/**
	* @return 图片高度
	*/
	public int getHeight() {
		return mHeight;
	}
	public void setHeight(int height) {
		this.mHeight = height;
	}
	
	@Override
	public String toString() {
		return 	"albumid :" + mAlbumId + 
				"\nlloc: " + mLloc + 
				"\nsloc: " + mSloc + 
				"\nheight: " + mHeight + 
				"\nwidth: " + mWidth + 
				"\n";
	}
}

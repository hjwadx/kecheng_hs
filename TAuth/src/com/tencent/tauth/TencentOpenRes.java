package com.tencent.tauth;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

public class TencentOpenRes extends View{
	
	private TencentOpenRes(Context context) {
		super(context);
	}

	static Drawable getCloseBtnSrc(AssetManager am) {
		StateListDrawable drawable = new StateListDrawable();
		try {
			drawable.addState(PRESSED_ENABLED_STATE_SET, Drawable.createFromStream(am.open("close_btn_src_pressed.png"), "close_btn_src_pressed"));
			drawable.addState(EMPTY_STATE_SET, Drawable.createFromStream(am.open("close_btn_src_normal.png"), "close_btn_src_normal"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}

	static Drawable getCloseBtnSrcBg(AssetManager am) {
		InputStream is = null;
		Drawable drawable = null;
		try {
			is = am.open("close_btn_bg.png");
			drawable = Drawable.createFromStream(is, "close_btn_bg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	static Drawable getTitleBg(AssetManager am) {
		InputStream is = null;
		Drawable drawable = null;
		try {
			is = am.open("title_bg.png");
			drawable = Drawable.createFromStream(is, "title_bg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	public static Drawable getBigLoginBtn(AssetManager am) {
		StateListDrawable drawable = new StateListDrawable();
		try {
			drawable.addState(PRESSED_ENABLED_STATE_SET, Drawable.createFromStream(am.open("login_btn_src_big_pressed.png"), "login_btn_src_big_pressed"));
			drawable.addState(EMPTY_STATE_SET, Drawable.createFromStream(am.open("login_btn_src_big_normal.png"), "login_btn_src_big_normal"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	public static Drawable getLoginBtn(AssetManager am) {
		StateListDrawable drawable = new StateListDrawable();
		try {
			drawable.addState(PRESSED_ENABLED_STATE_SET, Drawable.createFromStream(am.open("login_btn_src_pressed.png"), "login_btn_src_pressed"));
			drawable.addState(EMPTY_STATE_SET, Drawable.createFromStream(am.open("login_btn_src_normal.png"), "login_btn_src_normal"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	public static Drawable getSmallLoginBtn(AssetManager am) {
		StateListDrawable drawable = new StateListDrawable();
		try {
			drawable.addState(PRESSED_ENABLED_STATE_SET, Drawable.createFromStream(am.open("login_btn_src_small_pressed.png"), "login_btn_src_small_pressed"));
			drawable.addState(EMPTY_STATE_SET, Drawable.createFromStream(am.open("login_btn_src_small_normal.png"), "login_btn_src_small_normal"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	public static Drawable getLogoutBtn(AssetManager am) {
		StateListDrawable drawable = new StateListDrawable();
		try {
			drawable.addState(PRESSED_ENABLED_STATE_SET, Drawable.createFromStream(am.open("logout_btn_src_pressed.png"), "logout_btn_src_pressed"));
			drawable.addState(EMPTY_STATE_SET, Drawable.createFromStream(am.open("logout_btn_src_normal.png"), "logout_btn_src_normal"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	public static Drawable getSmallLogoutBtn(AssetManager am) {
		StateListDrawable drawable = new StateListDrawable();
		try {
			drawable.addState(PRESSED_ENABLED_STATE_SET, Drawable.createFromStream(am.open("logout_btn_src_small_pressed.png"), "logout_btn_src_small_pressed"));
			drawable.addState(EMPTY_STATE_SET, Drawable.createFromStream(am.open("logout_btn_src_small_normal.png"), "logout_btn_src_small_normal"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
}

package com.tencent.tauth.http;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class TDebug {
	
	private static boolean bDebugMode = false;
	
	public static void setMode(boolean mode) {
		TDebug.bDebugMode = mode;
	}
	
	public static void i(String tag, String msg) {
		if (bDebugMode) {
			Log.i(tag, msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if (bDebugMode) {
			Log.d(tag, msg);
		}
	}
	
	public static void msg(String message, Context con) {
		Toast.makeText(con, message, Toast.LENGTH_SHORT).show();
	}
}

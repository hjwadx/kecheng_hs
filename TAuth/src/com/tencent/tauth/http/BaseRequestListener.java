package com.tencent.tauth.http;


import java.io.FileNotFoundException;
import java.io.IOException;

public class BaseRequestListener implements IRequestListener {
	private static final String TAG = "BaseRequestListener";
	
	@Override
	public void onComplete(String response, Object state) {
	}

	@Override
	public void onFileNotFoundException(FileNotFoundException e, Object state) {
		TDebug.i(TAG, "Resource not found:" + e.getMessage());
	}

	@Override
	public void onIOException(IOException e, Object state) {
		TDebug.i(TAG, "Network Error:" + e.getMessage());
		
	}

}

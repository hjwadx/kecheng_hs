package fm.jihua.kecheng.rest.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import fm.jihua.common.ui.helper.Hint;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.utils.FileUtils;

/**
 * @date 2013-7-20
 * @introduce
 */
public class HandDrawnMapDownloadService extends Service {

	public static final int UPDATE_PROGRESS = 8344;
	public static final String INTENT_URL_STRING = "intent_url";

	MyIBinder iBinder = new MyIBinder();
	DownloadFile downloadFile;

	boolean isDownLoading = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!isDownLoading && downloadFile == null && intent != null) {
			String url = intent.getStringExtra(INTENT_URL_STRING);
			downloadFile = new DownloadFile();
			downloadFile.execute(url);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return iBinder;
	}

	public class MyIBinder extends Binder {
		public HandDrawnMapDownloadService getService() {
			return HandDrawnMapDownloadService.this;
		}
	}

	public boolean checkDownload() {
		return isDownLoading;
	}

	public void cancleAsync() {
		if (downloadFile != null)
			downloadFile.cancel(true);
		downloadFile = null;
		isDownLoading = false;
	}

	// DownloadFile AsyncTask
	private class DownloadFile extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			isDownLoading = true;
		}

		@Override
		protected Boolean doInBackground(String... sUrl) {
			try {
				URL url = new URL(sUrl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();

				// Detect the file lenght
				int fileLength = connection.getContentLength();

				// Declare the internal storage location

				// Download the file
				InputStream input = new BufferedInputStream(url.openStream());

				// Save the downloaded file
				OutputStream output = new FileOutputStream(FileUtils.getInstance().getHandDrawnMapDownloadPath(HandDrawnMapDownloadService.this, FileUtils.HANDDORAWNMAP_NAME_DOWNLOAD));

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while (((count = input.read(data)) != -1) && isDownLoading) {
					total += count;
					// Publish the progress
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				// Close connection
				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			if (onProgressChangedListener != null) {
				onProgressChangedListener.onProgressChanged(values[0]);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			isDownLoading = false;
			if (onProgressChangedListener != null) {
				onProgressChangedListener.onCancaled();
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			isDownLoading = false;
			Bitmap bitmap = null;
			if (!result) {
				Hint.showTipsShort(HandDrawnMapDownloadService.this, R.string.prompt_download_fail);
			} else {
				File file = new File(FileUtils.getInstance().getHandDrawnMapDownloadPath(HandDrawnMapDownloadService.this, FileUtils.HANDDORAWNMAP_NAME_DOWNLOAD));
				String userHandMapString = FileUtils.getInstance().getUserHandMapString((App)getApplication());
				boolean renameTo = file.renameTo(new File(FileUtils.getInstance().getHandDrawnMapDownloadPath(HandDrawnMapDownloadService.this, userHandMapString)));
				if (renameTo) {
					bitmap = FileUtils.getInstance().getCacheBitmap(HandDrawnMapDownloadService.this, userHandMapString);
				} else {
					Hint.showTipsShort(HandDrawnMapDownloadService.this, "下载失败，稍后再试");
				}
			}
			if (onProgressChangedListener != null) {
				onProgressChangedListener.onDownloadCompleted(bitmap);
			}
		}
	}

	private OnProgressChangedListener onProgressChangedListener;

	public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
		this.onProgressChangedListener = onProgressChangedListener;
	}

	public interface OnProgressChangedListener {
		public void onProgressChanged(int currentSize);

		public void onDownloadCompleted(Bitmap bitmap);
		
		public void onCancaled();
	}
}

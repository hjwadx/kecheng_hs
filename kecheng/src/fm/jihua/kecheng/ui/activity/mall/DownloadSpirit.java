package fm.jihua.kecheng.ui.activity.mall;

import java.io.File;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.mozillaonline.providers.DownloadManager;
import com.mozillaonline.providers.DownloadManager.Request;
import com.mozillaonline.providers.downloads.DownloadService;
import com.mozillaonline.providers.downloads.Downloads;

import fm.jihua.kecheng.App;
import fm.jihua.kecheng.rest.entities.mall.Product;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.FileUtils;
import fm.jihua.kecheng.utils.ZipUtils;

/**
 * @date 2013-9-5
 * @introduce
 */
public class DownloadSpirit {

	private Context context;
	private String url;
	private String downloadParentPath;
	private String downloadName;
	private String unZipPath;
	
	private Product product;
	private int productId;

	private long mColumnId = -1;

	public static final int STATUS_COMPLETED = 1010;
	public static final int STATUS_SDCARD_MISSING = 1011;
	public static final int STATUS_UNZIP_FAILED = 1012;
	
	//状态：
	//STATUS_COMPLETED 下载成功并解压成功
	//STATUS_SDCARD_MISSING sd卡不存在
	//STATUS_UNZIP_FAILED  解压失败
	//DownloadManager.STATUS_FAILED 下载失败

	public DownloadSpirit(String url, String unZipPath, Product product) {
		super();
		this.context = App.getInstance().getApplicationContext();
		this.url = url;
		this.downloadParentPath = Const.DOWNLOAD_FOLDER_PATH;
		this.unZipPath = unZipPath;
		this.product = product;
	}
	
	public DownloadSpirit(String url, String unZipPath, int productId) {
		super();
		this.context = App.getInstance().getApplicationContext();
		this.url = url;
		this.downloadParentPath = Const.DOWNLOAD_FOLDER_PATH;
		this.unZipPath = unZipPath;
		this.productId = productId;
	}

	public DownloadSpirit(Context context, String url, String downloadParentPath, String downloadName, String unZipPath, Product product) {
		super();
		this.context = App.getInstance().getApplicationContext();
		this.url = url;
		this.downloadParentPath = downloadParentPath;
		this.downloadName = downloadName;
		this.unZipPath = unZipPath;
	}

	public void start(OnDownloadListener downloadListener) {
		if (checkDownloadFile()) {
			startDownloadService();
			startDownload();

			this.downloadListener = downloadListener;
			myContentObserver = new MyContentObserver();
			App.getInstance().getContentResolver().registerContentObserver(Downloads.CONTENT_URI, true, myContentObserver);
		}
	}

	private boolean checkDownloadFile() {

		File file = null;
		if (FileUtils.getInstance().isSDAvailable()) {
			file = new File(Environment.getExternalStorageDirectory() + downloadParentPath);
			if (!file.exists()) {
				return file.mkdirs();
			} else {
				return true;
			}
		} else {
			if (downloadListener != null) {
				downloadListener.statusChanged(STATUS_SDCARD_MISSING, 0, 0, 0);
			}

			Hint.showTipsLong(context, "SD卡不存在，请检查重新下载");
			return false;
		}
	}

	private void startDownload() {
		DownloadManager.Request request = new Request(Uri.parse(url));
		Random random = new Random();
		if (TextUtils.isEmpty(downloadName)) {
			downloadName = "download" + random.nextInt(10000) + ".zip";
		}
		request.setDestinationInExternalPublicDir(downloadParentPath, downloadName);
		request.setDescription("");
		request.setShowRunningNotification(false);
		mColumnId = App.getInstance().getDownloadManager().enqueue(request);
	}

	// public long getColumnId() {
	// return mColumnId;
	// }

	public void cancle() {
		if (mColumnId != -1) {
			App.getInstance().getDownloadManager().remove(mColumnId);
			App.getInstance().getContentResolver().unregisterContentObserver(myContentObserver);
		}
	}

	private class MyContentObserver extends ContentObserver {
		public MyContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			long[] bytesAndStatus = getBytesAndStatus(mColumnId);
			long current = bytesAndStatus[0];
			long total = bytesAndStatus[1];
			int precent = (int) (bytesAndStatus[0] * 100 / bytesAndStatus[1]);
			int statusType = (int) bytesAndStatus[2];
			if (downloadListener != null) {
				downloadListener.statusChanged(statusType, precent, (int) current, (int) total);
			}
			if (statusType == DownloadManager.STATUS_SUCCESSFUL || statusType == DownloadManager.STATUS_FAILED || statusType == DownloadManager.STATUS_PAUSED) {
				App.getInstance().getContentResolver().unregisterContentObserver(myContentObserver);
				new UnZipAsync().execute();
			}
			
			if(statusType == DownloadManager.STATUS_FAILED){
//				Hint.showTipsShort(context, "下载失败，请稍后重试");
			}
		}
	}

	public long[] getBytesAndStatus(long downloadId) {
		long[] bytesAndStatus = new long[] { -1, -1, 0 };
		DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
		Cursor c = null;
		try {
			c = App.getInstance().getDownloadManager().query(query);
			if (c != null && c.moveToFirst()) {
				bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
				bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
				bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return bytesAndStatus;
	}

	public interface OnDownloadListener {
		public void statusChanged(int statusType, int precent, int current, int total);
	}

	private OnDownloadListener downloadListener;
	private MyContentObserver myContentObserver;

	private void startDownloadService() {
		Intent intent = new Intent();
		intent.setClass(App.getInstance(), DownloadService.class);
		App.getInstance().startService(intent);
	}

	class UnZipAsync extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// UIUtil.block(context, "文件配置中..");
			// Hint.showTipsLong(context, "文件配置中..");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			unZip2File();
			//解压完成，检查文件夹是否存在
			if (product != null) {
				return product.exists();
			} else if (productId != 0) {
				String pathString = Product.getLocalStoreDir(String.valueOf(productId));
				File file = new File(pathString);
				return file.exists();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (downloadListener != null) {
				if(result){
					downloadListener.statusChanged(STATUS_COMPLETED, 0, 0, 0);
				}else{
					downloadListener.statusChanged(STATUS_UNZIP_FAILED, 0, 0, 0);
				}
			}
		}

	}

	private void unZip2File() {
		if (!TextUtils.isEmpty(unZipPath)) {
			File pathUnZip = new File(Environment.getExternalStorageDirectory() + unZipPath);
			File pathDownload = new File(Environment.getExternalStorageDirectory() + downloadParentPath);
			if (!pathUnZip.exists()) {
				pathUnZip.mkdir();
			}
			String mDestPath = pathUnZip.getPath();
			String string = pathDownload.getPath() + File.separator + downloadName;
			ZipUtils.unZip(string, mDestPath);
			clearChildFile();
		}
	}
	
	private void clearChildFile(){
		File fileParent = new File(Environment.getExternalStorageDirectory() + downloadParentPath);
		if(fileParent.exists()){
			File[] listFiles = fileParent.listFiles();
			for (File file : listFiles) {
				file.delete();
			}
		}
	}

}

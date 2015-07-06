package fm.jihua.common.ui.helper;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.SimpleAdapter;

import fm.jihua.common.utils.Compatibility;
import fm.jihua.ui.R;

public abstract class PhotoPicker implements Parcelable {
	
	private final static String TAG = "PhotoPicker";
	
	public File mPhotoFile;
	private Activity mParent;
	public static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	
	public File PHOTO_PATH = getPhotoPath();
	public int WIDTH = getWidth();
	public int HEIGHT = getHeight();
	
	public static final int PHOTO_PICKED_WITH_DATA = 13021;
	public static final int CAMERA_WITH_DATA = 13023;
	
	public PhotoPicker(Activity parent){
		mParent = parent;
	}
	
	@Override
	public int describeContents() {
		return 1;
	}

	protected abstract File getPhotoPath();
	protected abstract int getWidth();
	protected abstract int getHeight();

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeSerializable(mPhotoFile);
	}
	
	public void editPhoto() {
		try {
			//LayoutInflater inflater = LayoutInflater.from(mParent);
			//final View editPhoto = inflater.inflate(R.layout.editphoto, null);
			final AlertDialog.Builder builder = new AlertDialog.Builder(mParent);
			
			 //生成动态数组，加入数据  
	        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
	        
	            HashMap<String, Object> map = new HashMap<String, Object>();  
	            map.put("ItemImage", R.drawable.camera);//图像资源的ID  
	            map.put("ItemTitle", "手机拍照");  
	            listItem.add(map); 
	            
	            HashMap<String, Object> map1 = new HashMap<String, Object>();  
	            map1.put("ItemImage", R.drawable.gallery);//图像资源的ID  
	            map1.put("ItemTitle", "本地图库选取");  
	            listItem.add(map1); 
	        
	        //生成适配器的Item和动态数组对应的元素  
	        SimpleAdapter listItemAdapter = new SimpleAdapter(mParent,listItem,R.layout.editphoto,new String[] {"ItemImage","ItemTitle"},   
	            new int[] {R.id.itemimage,R.id.itemtitle});  
	        builder.setAdapter(listItemAdapter, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						doTakePhoto();
						break;
					case 1:
						doPickPhotoFromGallery();
						break;
					default:
						break;
					}
				}
			});
			// 添加并且显示
//			editPhoto.setAdapter(listItemAdapter);

			// 添加点击
	        final AlertDialog alertDialogs = builder.create();
			alertDialogs.setCanceledOnTouchOutside(true);
			alertDialogs.setTitle("上传头像");
			alertDialogs.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doTakePhoto() {
		try {
			mPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			if (!mPhotoFile.getParentFile().exists()) {
				if (!mPhotoFile.getParentFile().mkdirs()) {
					Log.e("PhotoPicker","创建目标文件所在目录失败！");
				} else {
					mPhotoFile = new File(mPhotoFile.getAbsolutePath());
				}
			}			
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
			mParent.startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
        	Hint.showTipsShort(mParent, "启动摄像头失败");
		}
	}
	
	private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }


	public void doPickPhotoFromGallery() {
		try {
			final Intent intent = getPhotoPickIntent(true);
			mParent.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 封装请求Gallery的intent
	public Intent getPhotoPickIntent(boolean crop) {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			if(crop){
//				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", WIDTH);
				intent.putExtra("outputY", HEIGHT);
				intent.putExtra("outputFormat", "JPEG"); //输入文件格式  
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PHOTO_PATH));
				if (Compatibility.scaleUpIfNeeded4Black()) {
					intent.putExtra("scaleUpIfNeeded", true);
				}
				if (Compatibility.shouldReturnCropData()) {
					intent.putExtra("return-data", true);
				}
			}
			return intent;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Intent getCropImageIntent(Uri photoUri) {
		try {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(photoUri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", WIDTH);
			intent.putExtra("outputY", HEIGHT);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PHOTO_PATH));
			if (Compatibility.shouldReturnCropData()) {
				intent.putExtra("return-data", true);
			}
			return intent;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}

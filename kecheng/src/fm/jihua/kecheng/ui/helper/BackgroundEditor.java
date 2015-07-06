package fm.jihua.kecheng.ui.helper;

import java.io.File;
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
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import fm.jihua.common.ui.helper.PhotoPicker;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.ui.view.MainWeekView;
import fm.jihua.kecheng.utils.Const;

public class BackgroundEditor {
	public File mPhotoFile;
	private Fragment mParent;
	public static final File CUSTOM_BACKGROUND = new File(Environment.getExternalStorageDirectory() + Const.IMAGE_DIR + "background.jpg");
	public static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	static int maxWidth;
	static int maxHeight;

	public BackgroundEditor(Fragment parent, int width, int height){
		mParent = parent;
		maxWidth = width;
		maxHeight = height;
	}
	
	int getStatusBarHeight(Activity activity) {
		Rect rectgle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		return rectgle.top;
	}

	public void editPhoto() {
		try {
			//LayoutInflater inflater = LayoutInflater.from(mParent);
			//final View editPhoto = inflater.inflate(R.layout.editphoto, null);
			ListView editPhoto = new ListView(mParent.getActivity());
			editPhoto.setBackgroundColor(Color.WHITE);
			final AlertDialog.Builder builder = new AlertDialog.Builder(mParent.getActivity());

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
	        SimpleAdapter listItemAdapter = new SimpleAdapter(mParent.getActivity(),listItem,R.layout.editphoto,new String[] {"ItemImage","ItemTitle"},
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
			alertDialogs.setTitle("自定义背景");
			alertDialogs.show();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(Const.TAG, "FaceEditor editPhoto Exception:" + e.getMessage());
		}
	}
	
	public void editPhotoForCustom(final MainWeekView mainWeekView) {
	try {
		//LayoutInflater inflater = LayoutInflater.from(mParent);
		//final View editPhoto = inflater.inflate(R.layout.editphoto, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(mainWeekView.getContext());
		
		 //生成动态数组，加入数据  
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>(); 
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (BackgroundEditor.CUSTOM_BACKGROUND.exists()) {
        	map.put("ItemImage", R.drawable.menu_icon_selected);//图像资源的ID  
            map.put("ItemTitle", "使用当前背景");  
            listItem.add(map);
		}  
        
        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.camera);//图像资源的ID  
        map.put("ItemTitle", "手机拍照");  
        listItem.add(map); 
        
        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.gallery);//图像资源的ID  
        map.put("ItemTitle", "本地图库选取");  
        listItem.add(map); 
        
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(mParent.getActivity(),listItem,R.layout.editphoto,new String[] {"ItemImage","ItemTitle"},   
            new int[] {R.id.itemimage,R.id.itemtitle});  
        builder.setAdapter(listItemAdapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listItem.size() == 2) {
					which += 1;
				} 
				switch (which) {
				case 0:
					mainWeekView.weekView.spiritStyle.setWeekStyleBean(mainWeekView.skinView.getSkinStyleList().get(0));
					break;
				case 1:
					doTakePhoto();
					break;
				case 2:
				    if (BackgroundEditor.CUSTOM_BACKGROUND.exists()) {
				    	mainWeekView.lastModified = BackgroundEditor.CUSTOM_BACKGROUND.lastModified();
					}
					doPickPhotoFromGallery();
					break;
				default:
					break;
				}
			}
		});
		// 添加并且显示
//		editPhoto.setAdapter(listItemAdapter);

		// 添加点击
        final AlertDialog alertDialogs = builder.create();
		alertDialogs.setCanceledOnTouchOutside(true);
		alertDialogs.setTitle("自定义背景");
		alertDialogs.show();
	} catch (Exception e) {
		e.printStackTrace();
		Log.e(Const.TAG, "FaceEditor editPhoto Exception:" + e.getMessage());
	}
}

	public void doTakePhoto() {
		try {
			mPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			if (!mPhotoFile.getParentFile().exists()) {
				if (!mPhotoFile.getParentFile().mkdirs()) {
					Log.e("BackgroundEditor","创建目标文件所在目录失败！");
				} else {
					mPhotoFile = new File(mPhotoFile.getAbsolutePath());
				}
			}
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
			mParent.startActivityForResult(intent, PhotoPicker.CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Log.e(Const.TAG, "FaceEditor doTakePhoto Exception:" + e.getMessage());
        	Hint.debugHint(mParent.getActivity(), "找不到摄像头设备");
        	Hint.showTipsShort(mParent.getActivity(), "启动摄像头失败");
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
			mParent.startActivityForResult(intent, PhotoPicker.PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Log.e(Const.TAG, "FaceEditor doPickPhotoFromGallery Exception:" + e.getMessage());
        	Hint.debugHint(mParent.getActivity(), "找不到相册");
		}
	}

	// 封装请求Gallery的intent
	public static Intent getPhotoPickIntent(boolean crop) {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			if(crop){
//				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", maxWidth);
				intent.putExtra("aspectY", maxHeight);
				intent.putExtra("outputX", maxWidth);
				intent.putExtra("outputY", maxHeight);
				intent.putExtra("outputFormat", "JPEG"); //输入文件格式
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(CUSTOM_BACKGROUND));
				if (Compatibility.shouldReturnCropData()) {
					intent.putExtra("return-data", true);
				}
			}
			return intent;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Log.e(Const.TAG, "FaceEditor doPickPhotoFromGallery Exception:" + e.getMessage());
		}
		return null;
	}

	public static Intent getCropImageIntent(Uri photoUri) {
		try {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(photoUri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", maxWidth);
			intent.putExtra("aspectY", maxHeight);
			intent.putExtra("outputX", maxWidth);
			intent.putExtra("outputY", maxHeight);
			intent.putExtra("scale", true);
			if (Compatibility.scaleUpIfNeeded4Black())
				intent.putExtra("scaleUpIfNeeded", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(CUSTOM_BACKGROUND));
			if (Compatibility.shouldReturnCropData()) {
				intent.putExtra("return-data", true);
			}
			return intent;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Log.e(Const.TAG, "FaceEditor doPickPhotoFromGallery Exception:" + e.getMessage());
		}
		return null;
	}
}

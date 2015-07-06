package fm.jihua.kecheng.ui.activity.profile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.PhotoPicker;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.CameraUtil;
import fm.jihua.common.utils.Compatibility;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Avatar;
import fm.jihua.kecheng.rest.entities.AvatarOrFile;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.rest.entities.UploadAvatatsResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UserDetailsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.helper.FaceEditor;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.ui.widget.MyProfileInfoView;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;

public class MyProfile extends BaseActivity {

	TextView tv_nameEdit;
	TextView tv_genderView;
	MyProfileInfoView myProfileInfoView;
	CachedImageView avatarView;

	FaceEditor mFaceEditor;
	DataAdapter mDataAdapter;
	User mMyself;
	public File mPhotoFile;
	
	boolean hasChooseAvatar;
	boolean isUploadSuceess;
	boolean profileChanged;
	int gender;
	List<CachedImageView> avatarsList = new ArrayList<CachedImageView>() ;
	Avatar[] avatars;
	List<String> avatars_name = new ArrayList<String>();
	List<AvatarOrFile> avatarOrFiles = new ArrayList<AvatarOrFile>();
	
	String name;
	int position;
	
	//Const
	public static final int PHOTO_PICKED_WITH_DATA_NEW = 13030;
	public static final int PHOTO_CROPED_WITH_DATA_NEW = 13031;

	public static final int CAMERA_WITH_DATA_NEW = 13032;
	
	public static final int BIG_AVATAR = 1;
	public static final int SMALL_AVATAR = 2;
	public static final int ADD_AVATAR = 3;
	String onSaveString ="face_editor";
	
	public final static String BRODCAST_SCHOOL_CHANGED = "school_changed";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_profile);
		
		findViews();
		initVariable();
		initViews(mMyself);
		initAvatars();
		initTitlebar();
	}
	
	void initVariable(){
		App app = (App) getApplication();
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		mMyself = app.getMyself();
		avatars = app.getDBHelper().getAvatars(app.getUserDB(), app.getMyUserId());
		mDataAdapter.getUser(app.getMyUserId());
	}
	
	void findViews() {
		myProfileInfoView = (MyProfileInfoView) findViewById(R.id.school_information);
		
		avatarsList.add((CachedImageView)findViewById(R.id.myface));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar0));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar1));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar2));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar3));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar4));	
	}
	
	void initTitlebar(){
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_cancel);
		getKechengActionBar().getMenuButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isEdited() || hasChooseAvatar) {
					showQuitDialog();
				} else {
					finish();
				}
			}
		});
		getKechengActionBar().getRightButton().setVisibility(View.VISIBLE);
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		getKechengActionBar().getRightButton().setOnClickListener(clickListener);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isEdited() || hasChooseAvatar) {
				showQuitDialog();
				return true;
			}		
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	@Override
	public void finish() {
		deleteProvisionalFiles();
		if (profileChanged) {
			Intent intent = new Intent();
			intent.putExtra("result", true);
			if(isUploadSuceess){
				intent.putExtra("AVATAR_CHANGED", true);
			}
			setResult(RESULT_OK, intent);
		}
		super.finish();
	}
	
	void deleteProvisionalFiles() {
		if (avatars_name != null && avatars_name.size() > 0) {
			for (String name : avatars_name) {
				File file = getPhotoPath(name);
        		file.delete();
			}
			avatars_name.clear(); 
		}
	}

	void initViews(User user) {
		
		setTitle("编辑资料");
		tv_nameEdit = (TextView) findViewById(R.id.name); 
		tv_genderView = (TextView) findViewById(R.id.gender);
		avatarView = (CachedImageView) findViewById(R.id.myface);
		findViewById(R.id.gender_parent).setOnClickListener(clickListener);
		findViewById(R.id.name_parent).setOnClickListener(clickListener);
		
		gender = user.sex;
		
		tv_nameEdit.setText(user.name);
		tv_genderView.setText(gender == Const.FEMALE ? R.string.female : R.string.male);
		myProfileInfoView.init(user);
		avatarView.setCorner(true);
		avatarView.setImageURI(Uri.parse(mMyself.origin_avatar_url));
	}
	

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action:		
			case R.id.action_textview:		
				updateUser();
				break;
			case R.id.name_parent:
				intent2EditName();
				break;
			case R.id.gender_parent:
				showSelectSexDialog();
				break;
			default:
				break;
			}
		}
	};
	
	
	void updateUser() {
		User user = myProfileInfoView.getValues(mMyself);
		if (isEdited()) {
			UIUtil.block(MyProfile.this);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("sex", gender);
			map.put("name", tv_nameEdit.getText().toString().trim());
			map.put("grade", user.grade);
			//new
			map.put("birthday", user.birthday);
			map.put("relationship_status", user.relationship_status);
			map.put("signature", user.signature);
			map.put("pursuing_degree", user.pursuing_degree);
			map.put("career_goal", user.career_goal);
			map.put("dormitory", user.dormitory);
			map.put("birthday_visibility", user.birthday_visibility);
			mMyself = user;
			String schoolName = null;
			if(myProfileInfoView.schoolChanged()){
				schoolName = user.school;
			}
			mDataAdapter.updateUser(map, schoolName, user.department, user.life_situations, myProfileInfoView.getCountyId());
		} else if (hasChooseAvatar){
			uploadPhoto();
		} else {
			Hint.showTipsLong(MyProfile.this, R.string.no_data_to_save);
		}	
	}
	
	void intent2EditName(){
		Intent intent = new Intent(MyProfile.this, EditTextActivity.class);
		intent.putExtra(ConstProfile.INTENT_TO_EDITTEXT_TYPE_KEY, ConstProfile.EDITTEXT_TYPE_PROFILE_NAME);
		intent.putExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY, mMyself.name);
		startActivityForResult(intent, ConstProfile.REQUESTCODE_PROFILE_NAME);
	}
	
	boolean isEdited() {
		boolean isEdited = mMyself.sex != gender || !tv_nameEdit.getText().toString().trim().equals(mMyself.name);
		isEdited |= myProfileInfoView.haveChanged();
		return isEdited;
	}
	
	/*			dialog			*/
	void showQuitDialog(){
		new AlertDialog.Builder(MyProfile.this).setTitle(R.string.hint_string).setMessage(R.string.data_save_certain).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				updateUser();
			}
		}).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				dialoginterface.dismiss();
				finish();
			}
		}).show();
	}
	
	void showSelectSexDialog(){
		final String[] choices = getResources().getStringArray(R.array.select_sex);
		DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				tv_genderView.setText(choices[which]);
				gender = which;
				dialog.dismiss();
			}
		};
		int index = gender;
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.choose_sex).setSingleChoiceItems(choices, index, onselect).create();
		dialog.show();
	}

	void uploadPhoto(){
		if (hasChooseAvatar) {
			UIUtil.block(MyProfile.this);
			mDataAdapter.uploadPhotos(avatarOrFiles, mMyself.id);
		}
	}
	
	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(MyProfile.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_UPLOAD_PHOTO:{
				UploadAvatatsResult result = (UploadAvatatsResult) msg.obj;
				if (result != null && result.success) {
					MobclickAgent.onEvent(MyProfile.this, "event_edit_profile_succeed");
					Hint.showTipsLong(MyProfile.this, profileChanged?R.string.my_data_save_success:R.string.avatar_save_success);
					hasChooseAvatar = false;
					avatars = result.avatars;
					avatarOrFiles.clear();
					for (Avatar avatar:avatars) {
						avatarOrFiles.add(new AvatarOrFile(avatar));
					}
					initAvatars();
					//需要删除刚才创建的文件
					deleteProvisionalFiles();	
					profileChanged = true;
					isUploadSuceess = true;
					finish();
				} else {
					Hint.showTipsLong(MyProfile.this, R.string.avatar_save_failed);
				}
			}
				break;
			case DataAdapter.MESSAGE_UPDATE_USER:{
				RegistResult result = (RegistResult) msg.obj;
				if (result != null && result.success) {
					if (result.success) {
						if(myProfileInfoView.schoolChanged()){
							sendThemeChangedBroadcast();
						}
						myProfileInfoView.setSchoolChanged(false);
						myProfileInfoView.setHaveChanged(false);
						mMyself.sex = gender;
						mMyself.name = tv_nameEdit.getText().toString().trim();
						profileChanged = true;
						if (hasChooseAvatar) {
							uploadPhoto();
						}else {
							MobclickAgent.onEvent(MyProfile.this, "event_edit_profile_succeed");
							Hint.showTipsLong(MyProfile.this, R.string.my_data_save_success);
							finish();
						}
					}
				}else {
					Hint.showTipsLong(MyProfile.this, R.string.my_data_save_failed);
				}
			}
				break;
			case DataAdapter.MESSAGE_GET_USER:
				UserDetailsResult userDeatil = (UserDetailsResult) msg.obj;
				if (userDeatil != null) {
					User user = userDeatil.user;
					if (user != null) {
						mMyself = userDeatil.user;
						initViews(mMyself);
					}
					Avatar[] avatarsNet = userDeatil.avatars;
					if (avatarsNet != null) {
						avatars = userDeatil.avatars;
						avatarOrFiles.clear();
						for (Avatar avatar : avatars) {
							avatarOrFiles.add(new AvatarOrFile(avatar));
						}
						initAvatars();
					}
				}
			default:
				break;
			}
		}
	}
	
	void sendThemeChangedBroadcast(){
		Intent intent=new Intent(BRODCAST_SCHOOL_CHANGED);
	    sendBroadcast(intent); 
	}
	
	void initAvatars() {
		avatarView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加头像
				position = 0;
				if (avatarOrFiles.size() > 1) {
					editSmallPhoto(BIG_AVATAR);
				} else {
					editSmallPhoto(ADD_AVATAR);
				}
			}
		});
		if (avatarOrFiles.size() > 0) {
			if (avatarOrFiles.get(0).isAvatar()) {
				avatarView.setImageURI(Uri.parse(avatarOrFiles.get(0).avatar.origin_avatar_url));
			} else {
				Bitmap bmp = ImageHlp.decodeFile(getPhotoPath(avatarOrFiles.get(0).fileName).getAbsolutePath());
				if (bmp != null) {
					avatarView.setImageDrawable(new BitmapDrawable(getResources(), ImageHlp.getRoundedCornerBitmapAuto(bmp)));
					hasChooseAvatar = true;
				}
			}
		} else {
			avatarView.setImageURI(Uri.parse(mMyself.origin_avatar_url));
			avatarsList.get(1).setImageResource(R.drawable.myprofile_cover_icon_addphoto);
			int padding = ImageHlp.changeToSystemUnitFromDP(this, 16);
			avatarsList.get(1).setPadding(padding, padding, padding, padding);
			avatarsList.get(1).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 添加头像
					position = 1;
					editSmallPhoto(ADD_AVATAR);
				}
			});
			return;
		}
		for (int i = 1; i < avatarsList.size();i++) {
			if (i < avatarOrFiles.size()) {
				avatarsList.get(i).setFadeIn(false);
				final int j = i;
				avatarsList.get(i).setImageResource(0);
				if (avatarOrFiles.get(i).isAvatar()) {
					avatarsList.get(i).setImageURI(Uri.parse(avatarOrFiles.get(i).avatar.large_avatar_url));
				} else {
					Bitmap bmp = ImageHlp.decodeFile(getPhotoPath(avatarOrFiles.get(i).fileName).getAbsolutePath());
					if (bmp != null) {
						avatarsList.get(i).setImageDrawable(new BitmapDrawable(getResources(), bmp));
					}
				}
				avatarsList.get(i).setPadding(0, 0, 0, 0);
				avatarsList.get(i).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						position = j;
						editSmallPhoto(SMALL_AVATAR);
					}
				});
			} else if (i == avatarOrFiles.size()) {
				avatarsList.get(i).setFadeIn(false);
				final int j = i;
				int padding = ImageHlp.changeToSystemUnitFromDP(this, 16);
				avatarsList.get(i).setImageResource(R.drawable.myprofile_cover_icon_addphoto);
				avatarsList.get(i).setPadding(padding, padding, padding, padding);
				avatarsList.get(i).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 添加头像
						position = j;
						editSmallPhoto(ADD_AVATAR);
					}
				});
			} else {
				avatarsList.get(i).setImageResource(0);
				avatarsList.get(i).setClickable(false);
			}
		}
		
	}
	
	void deleteSmallAvatar () {
		hasChooseAvatar = true;
		try {
			avatarOrFiles.remove(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initAvatars();
	}
	
	void exChangeCurrentAvatar() {
		hasChooseAvatar = true;		
		AvatarOrFile avatarOrFile = avatarOrFiles.get(0);
		avatarOrFiles.set(0, avatarOrFiles.get(position));
		avatarOrFiles.set(position, avatarOrFile);
		initAvatars();
	}
	
	public void editSmallPhoto(int type) {
		try {
			//LayoutInflater inflater = LayoutInflater.from(mParent);
			//final View editPhoto = inflater.inflate(R.layout.editphoto, null);
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			 //生成动态数组，加入数据  
	        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
	        
	            HashMap<String, Object> map = new HashMap<String, Object>();  
	            map.put("ItemImage", R.drawable.camera);//图像资源的ID  
	            map.put("ItemTitle", getString(R.string.photo_take_picture));  
	            listItem.add(map); 
	            
	            HashMap<String, Object> map1 = new HashMap<String, Object>();  
	            map1.put("ItemImage", R.drawable.gallery);//图像资源的ID  
	            map1.put("ItemTitle", getString(R.string.photo_take_location));  
	            listItem.add(map1); 
	            
	            if (type == SMALL_AVATAR) {          
		            HashMap<String, Object> map2 = new HashMap<String, Object>();  
		            map2.put("ItemImage", R.drawable.menu_icon_delete);//图像资源的ID  
		            map2.put("ItemTitle", getString(R.string.photo_delete));  
		            listItem.add(map2); 	            

	            	HashMap<String, Object> map3 = new HashMap<String, Object>();  
	            	map3.put("ItemImage", R.drawable.menu_icon_selected);//图像资源的ID  
	            	map3.put("ItemTitle", getString(R.string.photo_set_default_picture));  
		            listItem.add(map3);
	            } else if (type == BIG_AVATAR) {
	            	HashMap<String, Object> map2 = new HashMap<String, Object>();  
		            map2.put("ItemImage", R.drawable.menu_icon_delete);//图像资源的ID  
		            map2.put("ItemTitle", getString(R.string.photo_delete));  
		            listItem.add(map2); 
	            } 
	            
	        
	        //生成适配器的Item和动态数组对应的元素  
	        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,R.layout.editphoto,new String[] {"ItemImage","ItemTitle"},   
	            new int[] {R.id.itemimage,R.id.itemtitle});  
	        builder.setAdapter(listItemAdapter,  new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						doTakePhoto();
						break;
					case 1:
						doPickPhotoFromGallery();
						break;
					case 2:
						deleteSmallAvatar();
						break;
					case 3:
						exChangeCurrentAvatar();
						break;
					default:
						break;
					}
				}} );
			// 添加并且显示
//			editPhoto.setAdapter(listItemAdapter);

			// 添加点击
	        final AlertDialog alertDialogs = builder.create();
			alertDialogs.setCanceledOnTouchOutside(true);
			alertDialogs.setTitle(position == 0 ? getString(R.string.dialog_title_modify_head) : getString(R.string.dialog_title_modify_picture));
			alertDialogs.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doTakePhoto() {
		try {
			name = getPhotoFileName();
			avatars_name.add(name);
			mPhotoFile = getPhotoPath(name);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
			startActivityForResult(intent, CAMERA_WITH_DATA_NEW);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
        	Hint.showTipsShort(this, R.string.open_camera_failed);
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private String getPhotoFileName() {
        Date date = new Date(java.lang.System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }
	
	public void doPickPhotoFromGallery() {
		name = getPhotoFileName();
		avatars_name.add(name);
		try {
			final Intent intent = getPhotoPickIntent(true);
			this.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA_NEW);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// 封装请求Gallery的intent
	public Intent getPhotoPickIntent(boolean crop) {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			if (crop) {
				// intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", Const.REGISTER_IMAGE_WIDTH);
				intent.putExtra("outputY", Const.REGISTER_IMAGE_HEIGHT);
				intent.putExtra("outputFormat", "JPEG"); // 输入文件格式
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getPhotoPath(name)));
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Ignore failed requests
		if (resultCode != RESULT_OK)
			return;
		try {
			myProfileInfoView.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
			case PhotoPicker.PHOTO_PICKED_WITH_DATA:{
				// We tell the activity to put the result in MY_AVATAR_URI
				if (mFaceEditor.PHOTO_PATH.exists()) {
					setAvatar();
				}else if (data != null && data.getData() != null) {
					doCropPhoto(data.getData(), Const.PHOTO_CROPED_WITH_DATA);
				}
			}
				break;
			case PHOTO_PICKED_WITH_DATA_NEW:{
				// We tell the activity to put the result in MY_AVATAR_URI
				if (getPhotoPath(name).exists()) {
					setAvatar(position, name);
					doCropPhotoSmall(Uri.fromFile(getPhotoPath(name)), PHOTO_CROPED_WITH_DATA_NEW,position, name);
				}else if (data != null && data.getData() != null) {
					doCropPhotoSmall(data.getData(), PHOTO_CROPED_WITH_DATA_NEW,position, name);
				}
			}
				break;
			case Const.PHOTO_CROPED_WITH_DATA:
				boolean exist;
				if (mFaceEditor.PHOTO_PATH.exists()) {
					exist = true;
				}else {
					String path = CameraUtil.resolvePhotoFromIntent(this, data, mFaceEditor.PHOTO_PATH.getAbsolutePath());
					exist = path != null;
				}
				if (exist) {
					setAvatar();
				}
				break;
			case PHOTO_CROPED_WITH_DATA_NEW:
				boolean exist1;
				if (getPhotoPath(name).exists()) {
					exist1 = true;
				}else {
					String path = CameraUtil.resolvePhotoFromIntent(this, data, getPhotoPath(name).getAbsolutePath());
					exist1 = path != null;
				}
				if (exist1) {
					hasChooseAvatar = true;
					if (position >= avatarOrFiles.size()) {
						avatarOrFiles.add(new AvatarOrFile(name,getPhotoPath(name).getAbsolutePath()));
					} else {
						avatarOrFiles.set(position, new AvatarOrFile(name,getPhotoPath(name).getAbsolutePath()));
					}
					initAvatars();
				}
				break;

			case PhotoPicker.CAMERA_WITH_DATA:
				doCropPhoto(Uri.fromFile(mFaceEditor.mPhotoFile), Const.PHOTO_CROPED_WITH_DATA);
				break;
			case CAMERA_WITH_DATA_NEW:
				doCropPhotoSmall(Uri.fromFile(mPhotoFile), PHOTO_CROPED_WITH_DATA_NEW, position, name);
				break;
			case ConstProfile.REQUESTCODE_PROFILE_NAME:
				tv_nameEdit.setText(data.getStringExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY));
				break;
			default:
				Log.w(Const.TAG, "onActivityResult : invalid request code");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected File getPhotoPath(String name) {
		File file = Const.getDiskCacheDir(this, Const.IMAGE_DIR + name);
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				Log.e("MyProfile", getString(R.string.mkdir_failed));
			}
		}
		return new File(file.getAbsolutePath());
	}
	

	void setAvatar(){
		Bitmap bmp = ImageHlp.decodeFile(mFaceEditor.PHOTO_PATH.getAbsolutePath());
		if (bmp != null) {
			avatarView.setImageDrawable(new BitmapDrawable(getResources(), bmp));
			hasChooseAvatar = true;
		}
	}
	
	void setAvatar(int position, String name){
		Bitmap bmp = ImageHlp.decodeFile(getPhotoPath(name).getAbsolutePath());
		if (bmp != null) {
			avatarsList.get(position).setImageDrawable(new BitmapDrawable(getResources(), bmp));
			hasChooseAvatar = true;
		}
	}

	/**
	 * Sends a newly acquired photo to Gallery for cropping.
	 * 
	 * @param f
	 *            the image file to crop
	 */
	protected void doCropPhoto(Uri uri, int responseId) {
		try {
			final Intent intent = mFaceEditor.getCropImageIntent(uri);
			startActivityForResult(intent, responseId);
		} catch (Exception e) {
			e.printStackTrace();
			Hint.showTipsShort(MyProfile.this, R.string.error_to_crop_picture);
		}
	}
	
	protected void doCropPhotoSmall(Uri uri, int responseId, int position, String name) {
		try {
			// Launch gallery to crop the photo
			final Intent intent = getCropImageIntent(uri, position, name);
			startActivityForResult(intent, responseId);
		} catch (Exception e) {
			e.printStackTrace();
			Hint.showTipsShort(MyProfile.this, R.string.error_to_crop_picture);
		}
	}
	
	public Intent getCropImageIntent(Uri photoUri, int position, String name) {
		try {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(photoUri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", Const.REGISTER_IMAGE_WIDTH);
			intent.putExtra("outputY", Const.REGISTER_IMAGE_HEIGHT);
			if (Compatibility.scaleUpIfNeeded4Black())
				intent.putExtra("scaleUpIfNeeded", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getPhotoPath(name)));
			if (Compatibility.shouldReturnCropData()) {
				intent.putExtra("return-data", true);
			}
			return intent;
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(onSaveString, mFaceEditor);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mFaceEditor = (FaceEditor) savedInstanceState.getParcelable(onSaveString);
	}
}
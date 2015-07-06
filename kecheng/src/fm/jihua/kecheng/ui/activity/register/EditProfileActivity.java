package fm.jihua.kecheng.ui.activity.register;


import java.io.File;
import java.util.Arrays;
import java.util.Calendar;

import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import fm.jihua.common.ui.helper.PhotoPicker;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.CameraUtil;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.profile.EditTextActivity;
import fm.jihua.kecheng.ui.helper.FaceEditor;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.CommonUtils;
import fm.jihua.kecheng.utils.Const;
import fm.jihua.kecheng.utils.ImageHlp;
import fm.jihua.kecheng.utils.UserStatusUtils;
import fm.jihua.kecheng.utils.UserStatusUtils.UserStatus;

public class EditProfileActivity extends BaseActivity {

	DataAdapter mDataAdapter;
	FaceEditor mFaceEditor;

	EditText nameEditText;
//	EditText emailEditText;
//	EditText passwordEditText;
	CachedImageView avatarView;
	TextView genderTextView;
	ImageView gendeImageView;

	String school;
	String department;
	int year;
	String renrenToken;
	String tinyAvatarUrl;
	String originAvatarUrl;
	String name;
//	String email;
//	String password;
	int gender;
	String renrenId;
	boolean hasChooseAvatar;
	boolean isClientAvatar;
	boolean isRegisterSuccess;
//	boolean isSecondTime;
	long lastModified;
	
	//new
	String city;
	int cityId;
	int schoolId; 
	String grade;
	int county_id;
	
	
	String tencentId;
	String tencentToken;
	String weiboId;
	String weiboToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onEvent(this, "enter_regist_profile_view");
		setContentView(R.layout.profile_edit);
		initTitleBar();
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		findViews();
		setListeners();
		getValueFromIntent();
	}
	
	void initTitleBar(){
		setTitle("个人信息");
		getKechengActionBar().getRightButton().setOnClickListener(clickListener);
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_next);
		getKechengActionBar().getLeftButton().setOnClickListener(clickListener);
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_back);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("face_editor", mFaceEditor);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mFaceEditor = (FaceEditor) savedInstanceState.getParcelable("face_editor");
	}
	
	protected int getImageSize() {
		return getResources().getDimensionPixelSize(R.dimen.my_profile_size);
	}
	
	void getValueFromIntent(){
		gender = 0;
		switch (getIntent().getIntExtra("TYPE", 0)) {
		case Const.WEIBO:
			weiboToken = getIntent().getStringExtra("THIRD_TOKEN");
			weiboId = getIntent().getStringExtra("THIRD_ID");
			break;
		case Const.RENREN:
			renrenToken = getIntent().getStringExtra("THIRD_TOKEN");
			renrenId = getIntent().getStringExtra("THIRD_ID");
			break;
		case Const.QQ:
			tencentToken = getIntent().getStringExtra("THIRD_TOKEN");
			tencentId = getIntent().getStringExtra("THIRD_ID");
			break;
		default:
			break;
		}
		if (renrenToken != null || tencentToken != null || weiboToken != null) {
			gender = getIntent().getIntExtra("GENDER", 1);
			tinyAvatarUrl = CommonUtils.replaceNullString(getIntent().getStringExtra("TINY_AVATAR_URL"));
			originAvatarUrl = CommonUtils.replaceNullString(getIntent().getStringExtra("ORIGIN_AVATAR_URL"));
			name = getIntent().getStringExtra("NAME");
			nameEditText.setText(name);
			genderTextView.setText(gender == Const.FEMALE  ? R.string.female : R.string.male);
			gendeImageView.setImageResource(gender == Const.FEMALE ? R.drawable.friendlist_gender_female : R.drawable.friendlist_gender_male);
			avatarView.setImageURI(Uri.parse(tinyAvatarUrl));
			hasChooseAvatar = true;
//			emailEditText.setHint(emailEditText.getHint().toString() + "(可选)");
//			passwordEditText.setHint(passwordEditText.getHint().toString() + "(可选)");
		}
	}

	void findViews() {
		genderTextView = (TextView) findViewById(R.id.gender);
		gendeImageView = (ImageView) findViewById(R.id.gender_image);
		nameEditText = (EditText) findViewById(R.id.name);
//		emailEditText = (EditText) findViewById(R.id.email);
//		passwordEditText = (EditText) findViewById(R.id.password);
		avatarView = (CachedImageView) findViewById(R.id.myface);
	}

	void setListeners() {
		findViewById(R.id.gender_parent).setOnClickListener(clickListener);
		findViewById(R.id.edit_myface).setOnClickListener(clickListener);
	}

	void getValues() {
		school = getIntent().getStringExtra("SCHOOL");
		department = getIntent().getStringExtra("DEPARTMENT");
//		year = getIntent().getIntExtra("YEAR", 0);
		name = nameEditText.getText().toString().trim();
		
		//new
		city = getIntent().getStringExtra("CITY");
		cityId = getIntent().getIntExtra("CITY_ID", 0);
		schoolId = getIntent().getIntExtra("SCHOOL_ID", 0);
		county_id = getIntent().getIntExtra("COUNTY_ID", 0);
		grade = getIntent().getStringExtra("GRADE");
		year = User.getYearFromGrade(grade);
//		email = emailEditText.getText().toString();
//		password = passwordEditText.getText().toString();
	}

	public void editPhoto() {
		if (mFaceEditor == null) {
			mFaceEditor = new FaceEditor(EditProfileActivity.this);
		}
		if (mFaceEditor.PHOTO_PATH.exists()) {
			lastModified = mFaceEditor.PHOTO_PATH.lastModified();
		}
		mFaceEditor.editPhoto();
	}
	
	private void register(){
		App app = (App)getApplication();
		User user = new User();
		user.renren_token = renrenToken;
		user.name = name;
//		user.email = email;
//		user.password = password;
		user.tencent_id = tencentId;
		user.tencent_token = tencentToken;
		user.weibo_id = weiboId;
		user.weibo_token = weiboToken;
		user.sex = gender;
		user.grade = year;
		user.tiny_avatar_url = tinyAvatarUrl;
		user.origin_avatar_url = originAvatarUrl;
		user.android_imei = String.valueOf(app.getPhoneID());
		
		//需要加上这些信息
//		city = getIntent().getStringExtra("CITY");
//		cityId = getIntent().getIntExtra("CITY_ID", 0);
//		schoolId = getIntent().getIntExtra("SCHOOL_ID", 0);
//		grade = getIntent().getStringExtra("GRADE");
		
		if (renrenId != null) {
			user.renren_id = Long.parseLong(renrenId);
		}
		UIUtil.block(this);
		if (isRegisterSuccess) {
//			isSecondTime = true;
			mDataAdapter.uploadPhoto(mFaceEditor.PHOTO_PATH);
		}else {
			mDataAdapter.register(user, school, department, county_id);//这里应该传城市和学校和班级
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.action: {
				getValues();
				if (name == null || name.length() == 0) {
					Hint.showTipsShort(EditProfileActivity.this, R.string.prompt_short_of_name);
					return;
				} else if(name.length() > 15){
					Hint.showTipsShort(EditProfileActivity.this, "名字不能超过15个字符");
					return;
				}
//				EmailValidator validator = new EmailValidator();
//				if (renrenToken == null || renrenToken.length() == 0) {
//					if (email == null || email.length() == 0
//							|| !validator.validate(email)) {
//						MobclickAgent.onEvent(EditProfileActivity.this, "event_edit_profile__next", "wrong_email");
//						Hint.showTipsShort(EditProfileActivity.this, "是不是邮箱填错了？");
//						return;
//					} else if (password == null || password.length() == 0) {
//						MobclickAgent.onEvent(EditProfileActivity.this, "event_edit_profile__next", "wrong_password");
//						Hint.showTipsShort(EditProfileActivity.this, "是不是忘记填密码了？");
//						return;
//					} else if (password.length() < 4) {
//						MobclickAgent.onEvent(EditProfileActivity.this, "event_edit_profile__next", "wrong_password");
//						Hint.showTipsShort(EditProfileActivity.this, "密码必须大于4位");
//						return;
//					}
//				} else {
//					if (email != null && email.length() != 0 && !validator.validate(email)) {
//						MobclickAgent.onEvent(EditProfileActivity.this, "event_edit_profile__next", "wrong_email");
//						Hint.showTipsShort(EditProfileActivity.this, "是不是邮箱填错了？");
//						return;
//					}
//					if (password != null && password.length() != 0 && password.length() < 4) {
//						MobclickAgent.onEvent(EditProfileActivity.this, "event_edit_profile__next", "wrong_password");
//						Hint.showTipsShort(EditProfileActivity.this, "密码必须大于4位");
//						return;
//					}
//				}
				if (!hasChooseAvatar) {
					showConfirmDialog();
//					MobclickAgent.onEvent(EditProfileActivity.this, "event_edit_profile__next", "wrong_avatar");
//					Hint.showTipsShort(EditProfileActivity.this, "是不是忘记选照片了？");
					return;
				}
				register();
			}
				break;
			case R.id.edit_myface:
				editPhoto();
				break;
			case R.id.menu:
				finish();
				break;
			case R.id.gender_parent:
				showSelectSexDialog();
				break;
			default:
				break;
			}
		}
	};
	
	void showSelectSexDialog(){
		final String[] choices = getResources().getStringArray(R.array.select_sex);
		DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				genderTextView.setText(choices[which]);
				gender = which;
				gendeImageView.setImageResource(gender == Const.FEMALE ? R.drawable.friendlist_gender_female : R.drawable.friendlist_gender_male);
				if(!hasChooseAvatar){
					avatarView.setImageResource(gender == Const.FEMALE ? R.drawable.avatar_default_female : R.drawable.avatar_default_male);
				}
				dialog.dismiss();
			}
		};
		int index = gender;
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.choose_sex).setSingleChoiceItems(choices, index, onselect).create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Ignore failed requests
		if (resultCode != RESULT_OK)
			return;
		try {
			switch (requestCode) {
			case PhotoPicker.PHOTO_PICKED_WITH_DATA:{
				// We tell the activity to put the result in MY_AVATAR_URI
				String path;
				if (mFaceEditor.PHOTO_PATH.exists() && lastModified != mFaceEditor.PHOTO_PATH.lastModified()) {
					path = mFaceEditor.PHOTO_PATH.getAbsolutePath();
				}else{
					path = CameraUtil.resolvePhotoFromIntent(this, data, mFaceEditor.PHOTO_PATH.getAbsolutePath());
				}
				if (path != null) {
					doCropPhoto(Uri.fromFile(new File(path)), Const.PHOTO_CROPED_WITH_DATA);
				}else {
					Hint.showTipsLong(this, "图片选取失败");
				}
			}
				break;
			case Const.PHOTO_CROPED_WITH_DATA:
				boolean exist;
				if (mFaceEditor.PHOTO_PATH.exists() && lastModified != mFaceEditor.PHOTO_PATH.lastModified()) {
					exist = true;
				}else {
					String path = CameraUtil.resolvePhotoFromIntent(this, data, mFaceEditor.PHOTO_PATH.getAbsolutePath());
					exist = path != null;
				}
				if (exist) {
					setAvatar();
				}
				break;
			case PhotoPicker.CAMERA_WITH_DATA:
				doCropPhoto(Uri.fromFile(mFaceEditor.mPhotoFile), Const.PHOTO_CROPED_WITH_DATA);
				break;
			default:
				Log.w(Const.TAG, "onActivityResult : invalid request code");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void setAvatar(){
		Bitmap bmp = ImageHlp.decodeFile(mFaceEditor.PHOTO_PATH.getAbsolutePath());
		if (bmp != null) {
			avatarView.setImageDrawable(new BitmapDrawable(getResources(), bmp));
			hasChooseAvatar = true;
			isClientAvatar = true;
		}
	}

	/**
	 * Sends a newly acquired photo to Gallery for cropping.
	 * 
	 * @param f
	 *            the image file to crop
	 */
	protected boolean doCropPhoto(Uri uri, int responseId) {
		try {

			// Add the image to the media store
			// level 8
			/*
			 * MediaScannerConnection.scanFile( this, new String[] {
			 * f.getAbsolutePath() }, new String[] { null }, null);
			 */

			// Launch gallery to crop the photo
			final Intent intent = mFaceEditor.getCropImageIntent(uri);
			startActivityForResult(intent, responseId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "出错了，没找到照片裁剪程序", Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	private class MyDataCallback implements DataCallback {
		@Override
		public void callback(Message msg) {
			UIUtil.unblock(EditProfileActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_REGISTER:{
				if (msg.obj != null) {
					RegistResult result = ((RegistResult)msg.obj);
					if (result.success) {
						MobclickAgent.onEvent(EditProfileActivity.this, "event_regist_succeed");
						isRegisterSuccess = true;
						UserStatusUtils.get().setNewUser(UserStatus.NEW_USER);
						if (isClientAvatar) {
							UIUtil.block(EditProfileActivity.this);
							mDataAdapter.uploadPhoto(mFaceEditor.PHOTO_PATH);
						}else {
							setResult(RESULT_OK);
							finish();
						}
					}else {
						if (msg.obj instanceof RegistResult) {
							Hint.showTipsLong(EditProfileActivity.this, ((RegistResult)msg.obj).error);
						}
					}
				}else {
					Hint.showTipsLong(EditProfileActivity.this, "注册失败了，网络是否正常?");
				}
			}
				break;
			case DataAdapter.MESSAGE_UPLOAD_PHOTO:
				RegistResult result = (RegistResult) msg.obj;
				setResult(RESULT_OK);
				finish();
				break;
			default:
				break;
			}
		}
	}
	
	private void showConfirmDialog(){
		new AlertDialog.Builder(EditProfileActivity.this)
		.setTitle("你还没有选择头像")
		.setMessage("是否设置头像")
		.setPositiveButton("现在设置",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
						editPhoto();
					}
				})
		.setNegativeButton("以后设置",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface,
							int i) {
//						setResult(RESULT_OK);
//						finish();
						hasChooseAvatar = true;
						getKechengActionBar().getRightButton().performClick();
//						clickListener.onClick(findViewById(R.id.action_textview));
					}
				}).show();
	}
}

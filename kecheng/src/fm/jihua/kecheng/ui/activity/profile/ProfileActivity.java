package fm.jihua.kecheng.ui.activity.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Avatar;
import fm.jihua.kecheng.rest.entities.BaseResult;
import fm.jihua.kecheng.rest.entities.CoursesResult;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UserDetailsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.ui.activity.common.ImageViewerActivity;
import fm.jihua.kecheng.ui.activity.home.WeekActivity;
import fm.jihua.kecheng.ui.activity.message.ChatActivity;
import fm.jihua.kecheng.ui.activity.register.RegisterActivity;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class ProfileActivity extends BaseActivity {
	
	DataAdapter mDataAdapter;
	User mUser;
	
	boolean courseUpdated;
	boolean isUploadSuceess;
	List<CachedImageView> avatarsList = new ArrayList<CachedImageView>() ;
	Avatar[] avatars;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		User user = (User) getIntent().getSerializableExtra("USER");
		if(user == null){
			App app = App.getInstance();
			user = app.getMyself();
		}
		mUser = user;
		findView();
		initTitlebar();
		initView();
		mDataAdapter = new DataAdapter(this, new MyDataCallback());
		mDataAdapter.getCourses(mUser.id);
		mDataAdapter.getUser(mUser.id);
		mDataAdapter.visit(user.id);
	}

	void initTitlebar() {
		App app = App.getInstance();
		if (mUser.id == app.getMyUserId()){
			getKechengActionBar().getActionButton().setVisibility(View.VISIBLE);
			getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_edit);
			getKechengActionBar().getActionButton().setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivityForResult(new Intent(ProfileActivity.this,
									MyProfile.class), Const.WEB_VIEWER_WITH_RESULT);
						}
					});
		}
		String stringExtra = getIntent().getStringExtra("FROM");
		if ("WEEKACTIVITY".equals(stringExtra)) {
			getKechengActionBar().getLeftButton().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ProfileActivity.this,
							RegisterActivity.class);
					startActivity(intent);
					finish();
				}
			});
		}
	}
	
	@Override
	public void onBackPressed() {
		String stringExtra = getIntent().getStringExtra("FROM");
		if ("WEEKACTIVITY".equals(stringExtra)) {
			Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
			startActivity(intent);
			finish();
		} else
			super.onBackPressed();
	}
	
	private void findView() {
		avatarsList.add((CachedImageView)findViewById(R.id.avatar));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar0));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar1));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar2));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar3));
		avatarsList.add((CachedImageView)findViewById(R.id.small_avatar4));	
	}

	private void initView() {
		setTitle(mUser.name);
		if (mUser.sex == Const.FEMALE) {
			((TextView) findViewById(R.id.name)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.friendprofile_gender_female, 0);
		}
		if(mUser.name.length() < 8){
			((TextView)findViewById(R.id.name)).setTextSize(23);
		} else if(mUser.name.length() < 16) {
			((TextView)findViewById(R.id.name)).setTextSize(20);
		} else {
			((TextView)findViewById(R.id.name)).setTextSize(18);
		}
		((TextView)findViewById(R.id.name)).setText(mUser.name);
		((TextView)findViewById(R.id.gezi_id)).setText("格子号:"+mUser.gezi_id);
		((TextView)findViewById(R.id.school_and_department)).setText(mUser.school + " " + User.getGradeFromYear(mUser.grade) + mUser.department + "班");
		((CachedImageView)findViewById(R.id.avatar)).setFadeIn(false);
		((CachedImageView)findViewById(R.id.avatar)).setCorner(true);
		((CachedImageView)findViewById(R.id.avatar)).setImageURI(Uri.parse(mUser.origin_avatar_url));
		App app = App.getInstance();
		if (mUser.id == app.getMyUserId()) {
			// findViewById(R.id.chat).setVisibility(View.GONE);
			findViewById(R.id.profile_toolbar).setVisibility(View.GONE);
			initTitlebar();
		}else {
			if (app.getDBHelper().isFriend(app.getUserDB(), mUser.id)) {
				findViewById(R.id.add_friend).setVisibility(View.GONE);
				findViewById(R.id.remove_friend).setVisibility(View.VISIBLE);
			}
		}
		
//		if (mUser.pursuing_degree != null && !"".equals(mUser.pursuing_degree)) {
//			((TextView)findViewById(R.id.year_and_educational_status)).setText(String.valueOf(mUser.grade) + "年入学, " + mUser.pursuing_degree);
//		} else {
//			((TextView)findViewById(R.id.year_and_educational_status)).setText(String.valueOf(mUser.grade) + "年入学, " + "大学生");
//		}
		
//		if (mUser.career_goal != null && !"".equals(mUser.career_goal)) {
//			((TextView)findViewById(R.id.field)).setText("目标行业：" +  mUser.career_goal);
//		}
		
//		if (mUser.life_situations != null && mUser.life_situations.length > 0) {
//			String string = "";
//			int i = 1;
//			for(String str:mUser.life_situations) {
//				string += i == mUser.life_situations.length? str : str +", ";
//				i++;
//			}
//			((TextView)findViewById(R.id.status)).setText("目前状态：" +  string);
//		}	
		
		if (mUser.signature != null && !"".equals(mUser.signature)) {
			((TextView)findViewById(R.id.autograph)).setText("签名：" + mUser.signature);
		} else {
			((TextView)findViewById(R.id.autograph)).setText("签名：" + this.getString(Math.random() > 0.5? R.string.signature_default1 : R.string.signature_default2));
		}
		
		if (mUser.dormitory != null && !"".equals(mUser.dormitory)) {
			((TextView)findViewById(R.id.dormitory)).setText("宿舍：" + mUser.dormitory);
		}
		
		String personal_info = "";
		if (mUser.astrology_sign != null && !"".equals(mUser.astrology_sign)) {
			personal_info += mUser.birthday_visibility == 1 ? mUser.birthday + ", " + mUser.astrology_sign : mUser.astrology_sign;
		} else {
			personal_info = "生日：年龄是个谜";
		}

		if (mUser.relationship_status != null && !"".equals(mUser.relationship_status)) {
			if(!"".equals(personal_info)){
				personal_info += ", " + mUser.relationship_status;
			}else {
				personal_info += mUser.relationship_status;
			}
		}
		if(!"".equals(personal_info)){
			((TextView)findViewById(R.id.personal_info)).setText(personal_info);
		}
	}
    
	CoursesResult result = null;
	
    private class MyDataCallback implements DataCallback {
		@Override
		public void callback(Message msg) {
			Log.i(Const.TAG, "DataCallback Message:" + msg.what);
			UIUtil.unblock(ProfileActivity.this);
			switch (msg.what) {
			case DataAdapter.MESSAGE_GET_COURSES:{
				result = (CoursesResult)msg.obj;
				if (result != null ) {
					if (result.finished && result.success) {
						courseUpdated = true;
					}
				}
			}
				break;
			case DataAdapter.MESSAGE_ADD_FRIEND:{
				BaseResult result = (BaseResult)msg.obj;
				if ( result != null && result.success ) {
					findViewById(R.id.add_friend).setVisibility(View.GONE);
					findViewById(R.id.remove_friend).setVisibility(View.VISIBLE);
					Hint.showTipsShort(ProfileActivity.this, R.string.add_friend_success);
				}else {
					Hint.showTipsShort(ProfileActivity.this, R.string.add_friend_failed);
				}
			}
				break;
			case DataAdapter.MESSAGE_REMOVE_FRIEND:{
				BaseResult result = (BaseResult)msg.obj;
				if ( result != null && result.success ) {
					findViewById(R.id.remove_friend).setVisibility(View.GONE);
					findViewById(R.id.add_friend).setVisibility(View.VISIBLE);
					Hint.showTipsShort(ProfileActivity.this, R.string.delete_friend_success);
				}else {
					Hint.showTipsShort(ProfileActivity.this, R.string.delete_friend_failed);
				}
			}
				break;
			case DataAdapter.MESSAGE_GET_USER:
				UserDetailsResult userDeatil = (UserDetailsResult) msg.obj;
				if (userDeatil != null) {
					if(userDeatil.user != null){
						mUser = userDeatil.user;
						initView();
					}
					if (ObjectUtils.nullSafeEquals(avatars, userDeatil.avatars)){
						break;
					}
					if(userDeatil.avatars != null){
						avatars = userDeatil.avatars;
						if (avatars.length > 0) {
							initAvatars();
						}
					}
				}else {
					
				}
				break;
			default:
				break;
			}
		}
    }
    
    void initAvatars() {
    	for (int i = 0; i < avatarsList.size();i++) {
    		avatarsList.get(i).setFadeIn(false);
			if (i < avatars.length) {
				avatarsList.get(i).setImageURI(Uri.parse( i == 0 ? avatars[0].origin_avatar_url: avatars[i].large_avatar_url));
			} else {
				avatarsList.get(i).setImageBitmap(null);
			}
		}
	}
       
    private void startImageViewerActivity(String origin_avatar_url, int position) {
    	Intent intent = new Intent(ProfileActivity.this, ImageViewerActivity.class);
		intent.putExtra("URL", origin_avatar_url);
		intent.putExtra("position", position);
		ArrayList<Avatar> avatars1 = new ArrayList<Avatar>(); 
		avatars1.addAll(Arrays.asList(avatars));
		intent.putExtra("AVATARS", avatars1);
		startActivity(intent);
	}
    
    public void onClick(View view) {
    	App app = App.getInstance();
		switch (view.getId()) {
		case R.id.chat:{
			Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
			intent.setData(mUser.getJabberUri());
			intent.putExtra(ChatActivity.INTENT_FROM_PROFILE, true);
			// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}
			break;
		case R.id.add_friend:{
			if (!app.getDBHelper().isFriend(app.getUserDB(), mUser.id)) {
				UIUtil.block(ProfileActivity.this);
				mDataAdapter.addFriend(mUser);
			}
			break;
		}
		case R.id.remove_friend:{
			if (app.getDBHelper().isFriend(app.getUserDB(), mUser.id)) {
				UIUtil.block(ProfileActivity.this);
				mDataAdapter.removeFriend(mUser.id);
			}
			break;
		}
		case R.id.view_coruses:{
			Intent intent = new Intent(ProfileActivity.this, WeekActivity.class);
			if (courseUpdated) {
				intent.putExtra(Const.STR_COURSESRESULT, result);
			}
			intent.putExtra("USER", mUser);
			startActivity(intent);
			break;
		}
		default:
			if(avatars != null && avatarsList.indexOf(view) < avatars.length ){
				startImageViewerActivity(avatars[avatarsList.indexOf(view)].origin_avatar_url, avatarsList.indexOf(view));
			}
			break;
		}
    }
    
    @Override
	public void finish() {
		if (isUploadSuceess) {
			Intent intent = new Intent();
			intent.putExtra("result", true);
			setResult(RESULT_OK, intent);
		}
		super.finish();
	}
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK){
			return;
		}
		switch (requestCode) {
		case Const.WEB_VIEWER_WITH_RESULT:
			if (data.getBooleanExtra("AVATAR_CHANGED", false)){
				isUploadSuceess = true;
			}
			App app = App.getInstance();
			mUser = app.getMyself();
			avatars = app.getDBHelper().getAvatars(app.getUserDB(), app.getMyUserId());
			initView();
			initAvatars();
			break;
		default:
			break;
		}
	}
}

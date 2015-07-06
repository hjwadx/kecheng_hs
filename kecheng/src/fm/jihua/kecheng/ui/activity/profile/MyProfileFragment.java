package fm.jihua.kecheng.ui.activity.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import fm.jihua.common.ui.helper.UIUtil;
import fm.jihua.common.utils.ObjectUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.contract.DataCallback;
import fm.jihua.kecheng.rest.entities.Avatar;
import fm.jihua.kecheng.rest.entities.Course;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.rest.entities.UserDetailsResult;
import fm.jihua.kecheng.rest.service.DataAdapter;
import fm.jihua.kecheng.ui.activity.common.ImageViewerActivity;
import fm.jihua.kecheng.ui.activity.course.CourseActivity;
import fm.jihua.kecheng.ui.activity.home.MenuActivity;
import fm.jihua.kecheng.ui.fragment.BaseFragment;
import fm.jihua.kecheng.ui.widget.CachedImageView;
import fm.jihua.kecheng.utils.Const;

public class MyProfileFragment extends BaseFragment {

	DataAdapter mDataAdapter;
	User mUser;
	
	ArrayList<Course> courses = new ArrayList<Course>();
	boolean courseUpdated;
	boolean isUploadSuceess;
	List<CachedImageView> avatarsList = new ArrayList<CachedImageView>() ;
	Avatar[] avatars;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.profile, container, false);
		App app = App.getInstance();
		mUser = app.getMyself();
		findViews(v);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
		App app = App.getInstance();
		mDataAdapter = new DataAdapter(parent, new MyDataCallback());
		mDataAdapter.getUser(app.getMyUserId());
	}
	
	public void initTitlebar() {
		parent.setTitle(mUser.name);
		((MenuActivity)parent).getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_edit);
		((MenuActivity)parent).getKechengActionBar().getRightButton().setVisibility(View.VISIBLE);
		((MenuActivity)parent).getKechengActionBar().getRightButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(parent, "action_edit_profile");
				startActivityForResult(new Intent(parent, MyProfile.class), Const.WEB_VIEWER_WITH_RESULT);
			}
		});
	}
	
	private void initView() {
//		initTitlebar();
		parent.setTitle(mUser.name);
		if (mUser.sex == Const.FEMALE) {
			((TextView) getView().findViewById(R.id.name)).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.friendprofile_gender_female, 0);
		}
		if(mUser.name.length() < 8){
			((TextView)getView().findViewById(R.id.name)).setTextSize(23);
		} else if(mUser.name.length() < 16) {
			((TextView)getView().findViewById(R.id.name)).setTextSize(20);
		} else {
			((TextView)getView().findViewById(R.id.name)).setTextSize(18);
		}
		((TextView)getView().findViewById(R.id.name)).setText(mUser.name);
		((TextView)getView().findViewById(R.id.gezi_id)).setText("格子号:"+mUser.gezi_id);
		((TextView)getView().findViewById(R.id.school_and_department)).setText(mUser.school + " " + User.getGradeFromYear(mUser.grade) + mUser.department + "班");
		((CachedImageView)getView().findViewById(R.id.avatar)).setFadeIn(false);
		((CachedImageView)getView().findViewById(R.id.avatar)).setCorner(true);
		((CachedImageView)getView().findViewById(R.id.avatar)).setImageURI(Uri.parse(mUser.origin_avatar_url));
		getView().findViewById(R.id.profile_toolbar).setVisibility(View.GONE);
		
//		if (mUser.pursuing_degree != null && !"".equals(mUser.pursuing_degree)) {
//			((TextView)getView().findViewById(R.id.year_and_educational_status)).setText(String.valueOf(mUser.grade) + "年入学, " + mUser.pursuing_degree);
//		} else {
//			((TextView)getView().findViewById(R.id.year_and_educational_status)).setText(String.valueOf(mUser.grade) + "年入学, " + "大学生");
//		}
		
//		if (mUser.career_goal != null && !"".equals(mUser.career_goal)) {
//			((TextView)getView().findViewById(R.id.field)).setText("目标行业：" +  mUser.career_goal);
//		}
//		
//		if (mUser.life_situations != null && mUser.life_situations.length > 0) {
//			String string = "";
//			int i = 1;
//			for(String str:mUser.life_situations) {
//				string += i == mUser.life_situations.length? str : str +", ";
//				i++;
//			}
//			((TextView)getView().findViewById(R.id.status)).setText("目前状态：" +  string);
//		}	
		
		if (mUser.signature != null && !"".equals(mUser.signature)) {
			((TextView)getView().findViewById(R.id.autograph)).setText("签名：" + mUser.signature);
		} else {
			((TextView)findViewById(R.id.autograph)).setText("签名：" + this.getString(Math.random() > 0.5? R.string.signature_default1 : R.string.signature_default2));
		}
		
		if (mUser.dormitory != null && !"".equals(mUser.dormitory)) {
			((TextView)getView().findViewById(R.id.dormitory)).setText("宿舍：" + mUser.dormitory);
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
			((TextView)getView().findViewById(R.id.personal_info)).setText(personal_info);
		}
	}
	
	
	void findViews(View v) {
		avatarsList.add((CachedImageView)v.findViewById(R.id.avatar));
		avatarsList.add((CachedImageView)v.findViewById(R.id.small_avatar0));
		avatarsList.add((CachedImageView)v.findViewById(R.id.small_avatar1));
		avatarsList.add((CachedImageView)v.findViewById(R.id.small_avatar2));
		avatarsList.add((CachedImageView)v.findViewById(R.id.small_avatar3));
		avatarsList.add((CachedImageView)v.findViewById(R.id.small_avatar4));	
	}
		
	class MyDataCallback implements DataCallback {

		@Override
		public void callback(Message msg) {
			UIUtil.unblock(parent);
			switch (msg.what) {
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
				}
				break;
			default:
				break;
			}
		}
	}
	
	boolean compareAvatars (Avatar[] avatars_new) {
    	if (avatars.length != avatars_new.length) {
    		return false;
    	} else {
    		int i = 0;
    		for (Avatar avatar : avatars) {
    			if (!avatar.equal(avatars_new[i])) {
    				return false;
    			};
    			i++;
    		}
    	}
		return true;	
    }
	
	void initAvatars() {
    	for (int i = 1; i < avatarsList.size();i++) {
    		avatarsList.get(i).setFadeIn(false);
			if (i < avatars.length) {
				avatarsList.get(i).setImageURI(Uri.parse( i == 0 ? avatars[0].origin_avatar_url: avatars[i].large_avatar_url));
			} else {
				avatarsList.get(i).setImageBitmap(null);
			}
		}
	}
	
	private void startImageViewerActivity(String origin_avatar_url, int position) {
    	Intent intent = new Intent(parent, ImageViewerActivity.class);
		intent.putExtra("URL", origin_avatar_url);
		intent.putExtra("position", position);
		ArrayList<Avatar> avatars1 = new ArrayList<Avatar>(); 
		avatars1.addAll(Arrays.asList(avatars));
		intent.putExtra("AVATARS", avatars1);
		startActivity(intent);
	}
	
	public void onClick(View view) {
		if (avatars != null && avatarsList.indexOf(view) < avatars.length) {
			startImageViewerActivity(avatars[avatarsList.indexOf(view)].origin_avatar_url, avatarsList.indexOf(view));
		}
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
	
	@Override
	public void onShow(){
		super.onShow();
		MobclickAgent.onEvent(parent, "enter_profile_view");
	}
}
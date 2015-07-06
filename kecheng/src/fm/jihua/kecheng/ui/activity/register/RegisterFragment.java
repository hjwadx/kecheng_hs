package fm.jihua.kecheng.ui.activity.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import fm.jihua.kecheng.interfaces.AuthHelper.CommonUser;
import fm.jihua.kecheng.rest.entities.RegistResult;
import fm.jihua.kecheng.ui.activity.BaseMenuActivity;
import fm.jihua.kecheng.ui.fragment.BaseFragment;
import fm.jihua.kecheng.ui.helper.Hint;
import fm.jihua.kecheng_hs.R;

public class RegisterFragment extends BaseFragment implements Listener<RegistResult>, OnClickListener {
	public final static int CITY = 0x101;
	public final static int SCHOOL = 0x102;
	public final static int CLASSES = 0x103;
	public final static int REGISTER = 0x104;
	
	String school;
	String classes;
	String city;
	int cityId;
	int schoolId; 
	String grade;
	String class_number;
	int county_id;
	
	public final static String INTENT_KEY_CITY = "CITY";
	public final static String INTENT_KEY_CITY_ID = "CITY_ID";
	public final static String INTENT_KEY_SCHOOL = "SCHOOL";
	public final static String INTENT_KEY_SCHOOL_ID = "SCHOOL_ID";
	public final static String INTENT_KEY_CLASSES = "CLASSES";
	public final static String INTENT_KEY_GRADE = "GRADE";
	public final static String INTENT_KEY_FROM_REGISTER_VIEW = "FROM_REGISTER_VIEW";
	public final static String INTENT_KEY_COUNTY = "COUNTY";
	
	CommonUser mUser;
	int mType;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.renrenauth, container, false);
		addListeners(v);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((RegisterActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		initTitleBar();
		initview();
	}
	
	void initview() {
		if(isInfoFilled()){
			findViewById(R.id.logo).setVisibility(View.GONE);
			findViewById(R.id.bottom_hint).setVisibility(View.GONE);
		} else {
			findViewById(R.id.logo).setVisibility(View.VISIBLE);
			findViewById(R.id.bottom_hint).setVisibility(View.VISIBLE);
		}
		findViewById(R.id.school_info).setPadding(0, 0, 0, 0);
	}

	void addListeners(View v){
		v.findViewById(R.id.city).setOnClickListener(this);
		v.findViewById(R.id.school).setOnClickListener(this);
		v.findViewById(R.id.classes).setOnClickListener(this);
//		v.findViewById(R.id.department).setOnClickListener(this);
//		v.findViewById(R.id.year).setOnClickListener(this);
	}
	
	public void setUser(CommonUser user){
		mUser = user;
	}
	
	public void setType(int type){
		mType = type;
	}
	
	void initTitleBar(){
		getActivity().setTitle("教育信息");
		((BaseMenuActivity)parent).getKechengActionBar().setLefttButtonGone();
		((BaseMenuActivity)parent).getKechengActionBar().getRightButton().setOnClickListener(this);
		((BaseMenuActivity)parent).getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_next);
	}
	
	private void startEditProfileActivity(){
		if (school == null) {
			Hint.showTipsShort(parent, "是不是忘了选学校了?");
			return;
		}
		Intent intent = new Intent(parent, EditProfileActivity.class);
		if(mUser != null) {
			intent.putExtra("TINY_AVATAR_URL", mUser.avatar);
		    intent.putExtra("ORIGIN_AVATAR_URL", mUser.largeAvatar);
		    intent.putExtra("NAME", mUser.name);
			intent.putExtra("THIRD_ID", mUser.id);		    
		    intent.putExtra("THIRD_TOKEN", mUser.token);
		    intent.putExtra("GENDER", mUser.gender);
		}
		
		intent.putExtra("TYPE", mType);
		
		intent.putExtra("CITY", city);
		intent.putExtra("CITY_ID", cityId);
		intent.putExtra("SCHOOL", school);
		intent.putExtra("SCHOOL_ID", schoolId);
		intent.putExtra("GRADE", grade);
		intent.putExtra("COUNTY_ID", county_id);
		intent.putExtra("DEPARTMENT", class_number);
		startActivityForResult(intent, REGISTER);
	}
	
	public void changeToConfirmView() {
		// findViewById(R.id.confirm).setVisibility(View.VISIBLE);
		// String hint;
		// if (school == null || department == null || year == 0){
		// hint = "请完善您的院系信息";
		// } else {
		// hint = "请确认您的信息无误";
		// }
		// ((TextView)findViewById(R.id.register_hint)).setText(hint);
		// ((View)findViewById(R.id.renren_button).getParent().getParent()).setVisibility(View.GONE);
	}
	
	void updateSchoolInfo(){
		((TextView) findViewById(R.id.city)).setText(city);
		((TextView) findViewById(R.id.city)).setTextColor(getResources().getColor(R.color.textcolor_80));
		((TextView) findViewById(R.id.school)).setText(school);
		((TextView) findViewById(R.id.school)).setTextColor(getResources().getColor(R.color.textcolor_80));
		((TextView) findViewById(R.id.classes)).setText(String.valueOf(classes));
		((TextView) findViewById(R.id.classes)).setTextColor(getResources().getColor(R.color.textcolor_80));
	}
	
	/**
	 * @param schoolInfo can not be null
	 */
//	public void updateSchoolInfo(SchoolInfo schoolInfo){
//		school = schoolInfo.school;
//		department = schoolInfo.department;
//		year = schoolInfo.year;
//		updateSchoolInfo();
//	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.city:
		case R.id.school:{
			Intent intent = new Intent(parent,
					ChooseCityActivity.class);
			intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, class_number);
			intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, grade);
			intent.putExtra(INTENT_KEY_FROM_REGISTER_VIEW, true);
			startActivityForResult(intent, CITY);
		}
			break;
		case R.id.classes: {
			if (school == null) {
				Intent intent = new Intent(parent, ChooseCityActivity.class);
				intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, class_number);
				intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, grade);
				intent.putExtra(INTENT_KEY_FROM_REGISTER_VIEW, true);
				startActivityForResult(intent, CITY);
			}else {
				Intent intent = new Intent(parent, ChooseClassesActivity.class);
				intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, class_number);
				intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, grade);
				startActivityForResult(intent, CLASSES);
			}
		}
			break;
		case R.id.action:{
			startEditProfileActivity();
		}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onResponse(RegistResult result) {
		if (result.success) {
			((RegisterActivity)getActivity()).login();
		}else {
			Hint.showTipsLong(getActivity(), "用户名或密码错误，请再检查一下");
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		try {
			switch (requestCode) {
			case CITY:
				cityId = data.getIntExtra(INTENT_KEY_CITY_ID, 0);
				city = data.getStringExtra(INTENT_KEY_CITY);
				school = data.getStringExtra(INTENT_KEY_SCHOOL);
				schoolId = data.getIntExtra(INTENT_KEY_SCHOOL_ID, 0);
				grade = data.getStringExtra(INTENT_KEY_GRADE);
				class_number = data.getStringExtra(INTENT_KEY_CLASSES);
				county_id = data.getIntExtra(INTENT_KEY_COUNTY, 0);
				classes = grade + class_number + "班";
//				if (classes == null) {
//					changeToConfirmView();
//				}
				updateSchoolInfo();
				break;
			case SCHOOL:
				school = data.getStringExtra(INTENT_KEY_SCHOOL);
				county_id = data.getIntExtra(INTENT_KEY_COUNTY, 0);
				updateSchoolInfo();
				break;
			case CLASSES:
				grade = data.getStringExtra(INTENT_KEY_GRADE);
				class_number = data.getStringExtra(INTENT_KEY_CLASSES);
				classes = grade + class_number + "班";
				updateSchoolInfo();
				break;
			case REGISTER:
				updateSchoolInfo();
				((RegisterActivity)getActivity()).login();
				break;
			default:
				break;
			}
			if(isInfoFilled()){
				((BaseMenuActivity)parent).showTitleBar();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isInfoFilled(){
		return (city != null && school != null && classes != null) || mUser != null;
	}
}
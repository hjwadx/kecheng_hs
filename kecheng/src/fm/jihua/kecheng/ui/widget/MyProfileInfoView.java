package fm.jihua.kecheng.ui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import fm.jihua.common.utils.CommonUtils;
import fm.jihua.kecheng.App;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.School;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.profile.ChooseBirthdayActivity;
import fm.jihua.kecheng.ui.activity.profile.ChooseDepartmentActivity;
import fm.jihua.kecheng.ui.activity.profile.ChooseListActivity;
import fm.jihua.kecheng.ui.activity.profile.ChooseSchoolActivity;
import fm.jihua.kecheng.ui.activity.profile.ChooseStatusActivity;
import fm.jihua.kecheng.ui.activity.profile.ConstProfile;
import fm.jihua.kecheng.ui.activity.profile.EditTextActivity;
import fm.jihua.kecheng.ui.activity.register.ChooseCityActivity;
import fm.jihua.kecheng.ui.activity.register.ChooseClassesActivity;
import fm.jihua.kecheng.ui.activity.register.RegisterFragment;

public class MyProfileInfoView extends LinearLayout{
	TextView schoolView;
//	TextView departmentView;
	TextView cityView;
	TextView classView;
	
	//new 
	TextView birthdayView;
	TextView autographView;
	TextView educational_statusView;
	TextView fieldView;
	TextView statusView;
	TextView loveView;
	TextView dormitoryView;
	TextView birthdayTitleView;
	Activity parent;
	
	String school;
	String department;
	String birthday;
	String constellation;
	String autograph;
//	String educational_status;
//	String field;
//	String[] status;
	String love_status;
	String dormitory;
	boolean show_birthday;
	int age;
	int year;
	int schoolId;
	

	int county_id;
	
	//
//	String classes;
	String city;
	int cityId;
	String grade;
	
	User myUser;
	
	boolean haveChanged;
	boolean schoolChanged;
	
	public MyProfileInfoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		initViews();
	}

	public MyProfileInfoView(Context context) {
		super(context);
		initViews();
	}

	public MyProfileInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}
	
	void initViews(){
		inflate(getContext(), R.layout.myprofile_info, this);
		parent = (Activity) getContext();
		findViews();
		setListeners();
	}
	
	void findViews(){
		schoolView = (TextView) findViewById(R.id.school);
		//new
		birthdayTitleView = (TextView) findViewById(R.id.birthday_title);
		birthdayView = (TextView) findViewById(R.id.birthday);
		autographView = (TextView) findViewById(R.id.autograph);
		educational_statusView = (TextView) findViewById(R.id.educational_status);
		fieldView = (TextView) findViewById(R.id.field);
		statusView = (TextView) findViewById(R.id.status);
		loveView = (TextView) findViewById(R.id.love_status);
		dormitoryView = (TextView) findViewById(R.id.dormitory);

		classView = (TextView) findViewById(R.id.classes);
		cityView = (TextView) findViewById(R.id.city);
	}
	
	void setListeners(){
		findViewById(R.id.school_parent).setOnClickListener(clickListener);
		findViewById(R.id.city_parent).setOnClickListener(clickListener);
		findViewById(R.id.classes_parent).setOnClickListener(clickListener);

		//new
		findViewById(R.id.birthday_parent).setOnClickListener(clickListener);
		findViewById(R.id.autograph_parent).setOnClickListener(clickListener);
		findViewById(R.id.educational_parent).setOnClickListener(clickListener);
		findViewById(R.id.field_parent).setOnClickListener(clickListener);
		findViewById(R.id.status_parent).setOnClickListener(clickListener);
		findViewById(R.id.love_parent).setOnClickListener(clickListener);
		findViewById(R.id.dormitory_parent).setOnClickListener(clickListener);
	}
	
	void updateSchoolInfo(){
		schoolView.setText(school);
		classView.setText(User.getGradeFromYear(year) + department + "班");
		cityView.setText(city);
		
		//new
		birthdayTitleView.setText(show_birthday ? R.string.birthday : R.string.constellation);
		birthdayView.setText(show_birthday ? birthday
				+ " " + constellation : constellation);
		autographView.setText(autograph);
		dormitoryView.setText(dormitory);
//		educational_statusView.setText(educational_status);
//		fieldView.setText(field);
//		if (status != null) {
//			String string = "";
//			int i = 1;
//			for(String str:status) {
//				string += i == status.length? str : str +", ";
//				i++;
//			}
//			statusView.setText(string);
//		}
		loveView.setText(love_status);
	}
	
	public void init(User user){
		myUser = user;
		school = user.school;
		App app = (App)parent.getApplication();
		School schoolObj = app.getSchoolDBHelper().getSchool(school);
		if (schoolObj != null) {
			schoolId = schoolObj.id; 
		}
		department = user.department;
		year = user.grade;
		grade = User.getGradeFromYear(year);
		city = user.city;
		
		//new
		birthday = user.birthday;
		constellation = user.astrology_sign;
		autograph = user.signature;
//		educational_status = user.pursuing_degree;
//		field = user.career_goal;
//		status = user.life_situations;
		show_birthday = (user.birthday_visibility == 1);
		dormitory = user.dormitory;
//		age = user.age;
		love_status = user.relationship_status;
		if (love_status == null || "".equals(love_status)) {
			love_status = getContext().getString(R.string.status_close);
		}
		updateSchoolInfo();
	}
	
	public boolean haveChanged() {
		return haveChanged;
	}
	
	public boolean schoolChanged() {
		return schoolChanged;
	}
	
	public void setSchoolChanged(boolean schoolChanged) {
		this.schoolChanged = schoolChanged;
	}
	
	public void setHaveChanged(boolean haveChanged) {
		this.haveChanged = haveChanged;
	}
	
	public int getCountyId(){
		return county_id;
	}
	
	public User getValues(User user){
		user.school = school;
		user.department = department;
		user.grade = year;
		
		//new
		user.birthday = birthday;
		user.astrology_sign = constellation;
		autograph = autographView.getText().toString();
		dormitory = dormitoryView.getText().toString();
		user.signature = autograph;
		user.dormitory = dormitory;
//		user.pursuing_degree = getContext().getString(R.string.null_string).equals(educational_status) ? "" : educational_status;
//		user.career_goal = getContext().getString(R.string.null_string).equals(field) ? "" : field;
//		if (status.length < 1 || getContext().getString(R.string.null_string).equals(status[0])) {
//			user.life_situations =  new String[0];
//		} else {
//			user.life_situations = status;
//		}
		user.birthday_visibility = show_birthday ? 1 : 3;
		user.relationship_status = love_status.equals(getContext().getString(R.string.status_close)) ? "" : love_status;
		return user;
	}
	
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.city_parent:
			case R.id.school_parent:{
				Intent intent = new Intent(parent,
						ChooseCityActivity.class);
				intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, department);
				intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, grade);
				intent.putExtra(RegisterFragment.INTENT_KEY_FROM_REGISTER_VIEW, true);
				startActivityForResult(intent, RegisterFragment.CITY);
			}
				break;
			case R.id.classes_parent: {
				if (school == null) {
					Intent intent = new Intent(parent, ChooseCityActivity.class);
					intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, department);
					intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, grade);
					intent.putExtra(RegisterFragment.INTENT_KEY_FROM_REGISTER_VIEW, true);
					startActivityForResult(intent, RegisterFragment.CITY);
				}else {
					Intent intent = new Intent(parent, ChooseClassesActivity.class);
					intent.putExtra(RegisterFragment.INTENT_KEY_CLASSES, department);
					intent.putExtra(RegisterFragment.INTENT_KEY_GRADE, grade);
					startActivityForResult(intent, RegisterFragment.CLASSES);
				}
			}
			break;
//			case R.id.status_parent:{
//				Intent intent = new Intent(parent, ChooseStatusActivity.class);
//				intent.putExtra("STATUS", new ArrayList<String>(Arrays.asList(status)));
//				startActivityForResult(intent, ConstProfile.REQUESTCODE_PROFILE_STATUS);
//			}
//				break;
//			case R.id.educational_parent:{
//				Intent intent = new Intent(parent, ChooseListActivity.class);
//				intent.putExtra(ConstProfile.INTENT_TO_CHOOSELIST_TYPE_KEY, ConstProfile.CHOOSELIST_TYPE_EDUCATION);
//				intent.putExtra(ConstProfile.INTENT_TO_CHOOSELIST_TITLE_KEY, getContext().getString(R.string.name_reading));
//				startActivityForResult(intent, ConstProfile.REQUESTCODE_PROFILE_EDUCATIONAL_STATUS);		
//			}
//				break;
//			case R.id.field_parent:{
//				Intent intent = new Intent(parent, ChooseListActivity.class);
//				intent.putExtra(ConstProfile.INTENT_TO_CHOOSELIST_TYPE_KEY, ConstProfile.CHOOSELIST_TYPE_FIELD);
//				intent.putExtra(ConstProfile.INTENT_TO_CHOOSELIST_TITLE_KEY, getContext().getString(R.string.name_target_industries));
//				startActivityForResult(intent, ConstProfile.REQUESTCODE_PROFILE_FIELD);		
//			}
//				break;
			case R.id.birthday_parent:{
				Intent intent = new Intent(parent, ChooseBirthdayActivity.class);
				intent.putExtra("USER", myUser);
				startActivityForResult(intent, ConstProfile.REQUESTCODE_PROFILE_BIRTHDAY);		
			}
				break;
			case R.id.autograph_parent:{
				Intent intent = new Intent(parent, EditTextActivity.class);
				intent.putExtra(ConstProfile.INTENT_TO_EDITTEXT_TYPE_KEY, ConstProfile.EDITTEXT_TYPE_PROFILE_SIGNATURE);
				intent.putExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY, autograph);
				startActivityForResult(intent, ConstProfile.REQUESTCODE_PROFILE_AUTOGRAPH);
			}
				break;
			case R.id.love_parent:{
				showelectLoveStatusDialog();
			}
				break;
			case R.id.dormitory_parent:{
				Intent intent = new Intent(parent, EditTextActivity.class);
				intent.putExtra(ConstProfile.INTENT_TO_EDITTEXT_TYPE_KEY, ConstProfile.EDITTEXT_TYPE_PROFILE_DORMITORY);
				intent.putExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY, dormitory);
				startActivityForResult(intent, ConstProfile.REQUESTCODE_PROFILE_DORMITORY);
			}
				break;
			default:
				break;
			}
		}

	};
	
	void showelectLoveStatusDialog(){
		final String[] choices = getResources().getStringArray(R.array.select_love_status);
		DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loveView.setText(choices[which]);
				if (!choices[which].equals(love_status)) {
					haveChanged = true;
				}
				love_status = choices[which];
				dialog.dismiss();
			}
		};
		int index = CommonUtils.find(choices, loveView.getText().toString());
		AlertDialog dialog = new AlertDialog.Builder(parent).setTitle(R.string.love_status).setSingleChoiceItems(choices, index, onselect).create();
		dialog.show();  
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		try {
			switch (requestCode) {
			case RegisterFragment.CITY:
				if (!data.getStringExtra(RegisterFragment.INTENT_KEY_SCHOOL).equals(school) || !data.getStringExtra(RegisterFragment.INTENT_KEY_GRADE).equals(grade) || !data.getStringExtra(RegisterFragment.INTENT_KEY_CLASSES).equals(department)) {
					haveChanged = true;
				}
				if (!data.getStringExtra(RegisterFragment.INTENT_KEY_SCHOOL).equals(school)) {
					schoolChanged = true;
				}
				cityId = data.getIntExtra(RegisterFragment.INTENT_KEY_CITY_ID, 0);
				city = data.getStringExtra(RegisterFragment.INTENT_KEY_CITY);
				school = data.getStringExtra(RegisterFragment.INTENT_KEY_SCHOOL);
				schoolId = data.getIntExtra(RegisterFragment.INTENT_KEY_SCHOOL_ID, 0);
				grade = data.getStringExtra(RegisterFragment.INTENT_KEY_GRADE);
				department = data.getStringExtra(RegisterFragment.INTENT_KEY_CLASSES);
				county_id = data.getIntExtra(RegisterFragment.INTENT_KEY_COUNTY, 0);
				year = User.getYearFromGrade(grade);
				break;
			case RegisterFragment.SCHOOL:
				if (!data.getStringExtra(RegisterFragment.INTENT_KEY_SCHOOL).equals(school)) {
					haveChanged = true;
					schoolChanged = true;
				}
				county_id = data.getIntExtra(RegisterFragment.INTENT_KEY_COUNTY, 0);
				school = data.getStringExtra(RegisterFragment.INTENT_KEY_SCHOOL);
//				updateSchoolInfo();
				break;
			case RegisterFragment.CLASSES:
				if (!data.getStringExtra(RegisterFragment.INTENT_KEY_GRADE).equals(grade) || !data.getStringExtra(RegisterFragment.INTENT_KEY_CLASSES).equals(department)) {
					haveChanged = true;
				}
				grade = data.getStringExtra(RegisterFragment.INTENT_KEY_GRADE);
				department = data.getStringExtra(RegisterFragment.INTENT_KEY_CLASSES);
				year = User.getYearFromGrade(grade);
//				classes = grade + department + "班";
//				updateSchoolInfo();
				break;
//			case ConstProfile.REQUESTCODE_PROFILE_SCHOOL:
//				schoolId = data.getIntExtra("SCHOOL_ID", 0);
//				if (!data.getStringExtra("SCHOOL").equals(school) || !data.getStringExtra("DEPARTMENT").equals(department)) {
//					haveChanged = true;
//				}
//				if(!data.getStringExtra("SCHOOL").equals(school)){
//					schoolChanged = true;
//				}
//				school = data.getStringExtra("SCHOOL");
//				department = data.getStringExtra("DEPARTMENT");
//				if (year == 0) {
//					year = data.getIntExtra("YEAR", 0);
//					haveChanged = true;
//				}
//				break;
//			case ConstProfile.REQUESTCODE_PROFILE_DEPARTMENT:
//				if (!data.getStringExtra("DEPARTMENT").equals(department)) {
//					haveChanged = true;
//				}
//				department = data.getStringExtra("DEPARTMENT");
//				break;
//			case ConstProfile.REQUESTCODE_PROFILE_YEAR:
//				if (data.getIntExtra("YEAR", 0) != year) {
//					haveChanged = true;
//				}
//				year = data.getIntExtra("YEAR", 0);
//				break;
				
//			case ConstProfile.REQUESTCODE_PROFILE_STATUS:
//				@SuppressWarnings("unchecked")
//				List<String> list = (List<String>) data.getSerializableExtra("STATUS");			
//				if (!Arrays.equals(status, (String[]) list.toArray(new String[list.size()]))) {
//					haveChanged = true;
//				}
//				status = (String[]) list.toArray(new String[list.size()]);
//				break;
//			case ConstProfile.REQUESTCODE_PROFILE_EDUCATIONAL_STATUS:
//				if (!data.getStringExtra("EDUCATION").equals(educational_status)) {
//					haveChanged = true;
//				}
//				educational_status = data.getStringExtra("EDUCATION");
//				break;
//			case ConstProfile.REQUESTCODE_PROFILE_FIELD:
//				if (!data.getStringExtra("FIELD").equals(field)) {
//					haveChanged = true;
//				}
//				field = data.getStringExtra("FIELD");
//				break;
			case ConstProfile.REQUESTCODE_PROFILE_BIRTHDAY:
				myUser = (User) data.getSerializableExtra("USER");
				if (myUser.birthday != null) {
					if (!myUser.birthday.equals(birthday) || show_birthday != (myUser.birthday_visibility == 1)) {
						haveChanged = true;
					}
				}
				birthday = myUser.birthday;
				constellation = myUser.astrology_sign;
				show_birthday = (myUser.birthday_visibility == 1);
//				age = myUser.age;
				break;
			case ConstProfile.REQUESTCODE_PROFILE_AUTOGRAPH:
				if (!data.getStringExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY).equals(autograph)) {
					haveChanged = true;
				}
				autograph = data.getStringExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY);
				break;
			case ConstProfile.REQUESTCODE_PROFILE_DORMITORY:
				if (!data.getStringExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY).equals(dormitory)) {
					haveChanged = true;
				}
				dormitory = data.getStringExtra(ConstProfile.INTENT_TO_EDITTEXT_KEY);
				break;
			case R.id.love_status:
				if (!data.getStringExtra("LOVE").equals(love_status)) {
					haveChanged = true;
				}
				love_status = data.getStringExtra("LOVE");
				break;
			default:
				break;
			}
			updateSchoolInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void startActivityForResult(Intent intent, int requestCode){
		parent.startActivityForResult(intent, requestCode);
	}

}

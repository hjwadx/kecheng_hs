package fm.jihua.kecheng.ui.activity.profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import fm.jihua.kecheng_hs.R;
import fm.jihua.kecheng.rest.entities.User;
import fm.jihua.kecheng.ui.activity.BaseActivity;
import fm.jihua.kecheng.utils.AppLogger;

public class ChooseBirthdayActivity extends BaseActivity {
	
	TextView birthdayView;
//	TextView ageView;
	TextView constellationView;
	WheelView monthView;
	WheelView yearView;
	WheelView dayView;
	SimpleDateFormat dateFormat;

	ToggleButton toggleShowAgeOnly;
	
	User myUser;
	
	public static final int[] constellationEdgeDay = { 20, 19, 21, 20, 21, 22, 
		23, 23, 23, 24, 23, 22 }; 
	
	public static final String[] constellationArr = { "水瓶座", "双鱼座", "白羊座", "金牛座",
			"双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosebirthday);
		myUser = (User) getIntent().getSerializableExtra("USER");
		initTitlebar();
		findViews();
		initView();
		addListener();
	}
	
	private void addListener() {
		MyOnWheelChangedListener listener = new MyOnWheelChangedListener();
		monthView.addChangingListener(listener);
		yearView.addChangingListener(listener);
		dayView.addChangingListener(listener);
	}

	private void initView() {
		birthdayView.setText(myUser.birthday);
		constellationView.setText(myUser.astrology_sign);
//		ageView.setText(myUser.age);
		toggleShowAgeOnly.setChecked(myUser.birthday_visibility == 3);
		initTimePicker();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	private void initTimePicker() {
		Date date = null;
		if(myUser != null && myUser.birthday != null && !"".equals(myUser.birthday)){
			String pattern="yyyy-MM-dd";
			dateFormat=new SimpleDateFormat(pattern);
			try {
				date = dateFormat.parse(myUser.birthday);
			} catch (ParseException e) {
				AppLogger.printStackTrace(e);
			}
		}
        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = new String[] {"一月", "二月", "三月", "四月", "五月", "六月",
        		"七月", "八月", "九月", "十月", "十一月", "十二月"};
        monthView.setViewAdapter(new DateArrayAdapter(this, months, curMonth));
        monthView.setCurrentItem(curMonth);
    
        // year
        int curYear = calendar.get(Calendar.YEAR);
        yearView.setViewAdapter(new DateNumericAdapter(this, curYear - 50, curYear, 1));
        yearView.setCurrentItem(29);
        
        //day
        updateDays(yearView, monthView, dayView);
        dayView.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        if(date != null){
        	 monthView.setCurrentItem(date.getMonth());
        	 yearView.setCurrentItem(date.getYear() - curYear + 1950);
        	 dayView.setCurrentItem(date.getDate() - 1);
        	
        }
	}
	
	void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 50 + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());
        
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

	private void findViews() {
		birthdayView = (TextView) findViewById(R.id.birthday);
		// ageView = (TextView) findViewById(R.id.age);
		constellationView = (TextView) findViewById(R.id.constellation);
		yearView = (WheelView) findViewById(R.id.year_picker);
		monthView = (WheelView) findViewById(R.id.month_picker);
		dayView = (WheelView) findViewById(R.id.day_picker);
		toggleShowAgeOnly = (ToggleButton) findViewById(R.id.toggle_showageonly);
	}

	private void initTitlebar() {
		setTitle(R.string.birthday_and_constellation);
		getKechengActionBar().setLeftImage(R.drawable.menubar_btn_icon_back);
		getKechengActionBar().getMenuButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		getKechengActionBar().setRightImage(R.drawable.menubar_btn_icon_save);
		getKechengActionBar().getActionButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				myUser.birthday = birthdayView.getText().toString();
				myUser.birthday_visibility = toggleShowAgeOnly.isChecked() ? 3 :1;
				myUser.astrology_sign = constellationView.getText().toString();
				// myUser.age = Integer.valueOf(ageView.getText().toString());
				intent.putExtra("USER", myUser);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	/**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
//            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
//                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
    
    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
//            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
//                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
    
	@SuppressLint("SimpleDateFormat")
	String getTimeString() {
		String pattern="yyyy-MM-dd";  
		dateFormat=new SimpleDateFormat(pattern);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,
				calendar.get(Calendar.YEAR) - 50 + yearView.getCurrentItem());
		calendar.set(Calendar.MONTH, monthView.getCurrentItem());
		calendar.set(Calendar.DATE, dayView.getCurrentItem() + 1);
		return dateFormat.format(calendar.getTime());
	}
	
	// Date getTimeDate() {
	// String pattern="yyyy-MM-dd";
	// Date date = null;
	// dateFormat=new SimpleDateFormat(pattern);
	// Calendar calendar = Calendar.getInstance();
	// calendar.set(Calendar.YEAR,
	// calendar.get(Calendar.YEAR) - 50 + yearView.getCurrentItem());
	// calendar.set(Calendar.MONTH, monthView.getCurrentItem());
	// calendar.set(Calendar.DATE, dayView.getCurrentItem() + 1);
	// String timeString = dateFormat.format(calendar.getTime());
	// try {
	// date = dateFormat.parse(timeString);
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	
	
	class MyOnWheelChangedListener implements OnWheelChangedListener {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			updateDays(yearView, monthView, dayView);
			birthdayView.setText(getTimeString());
//			ageView.setText(String.valueOf(getAge()) + "岁");
			constellationView.setText(getConstellation());
		}
		
	}
	
	// int getAge () {
	// Calendar calendar = Calendar.getInstance();
	// int age = calendar.get(Calendar.YEAR) - (calendar.get(Calendar.YEAR) - 50
	// + yearView.getCurrentItem());
	// if (monthView.getCurrentItem() > calendar.get(Calendar.MONTH)) {
	// return Math.max(--age,0);
	// } else if (monthView.getCurrentItem() == calendar.get(Calendar.MONTH) &&
	// dayView.getCurrentItem() + 1 > calendar.get(Calendar.DAY_OF_MONTH)) {
	// return Math.max(--age,0);
	// }
	// return age;
	// }
	
	String getConstellation () {
		int month = monthView.getCurrentItem() + 1;
		int day = dayView.getCurrentItem() + 1;
		month = day < constellationEdgeDay[month - 1] ? month - 1:month; 
		return month > 0 ? constellationArr[month - 1]: constellationArr[11];
	}

}
